package credits_api.service;

import credits_api.entity.*;
import credits_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreditService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CreditTransactionRepository transactionRepository;

    public CreditService(UserRepository userRepository,
                         CourseRepository courseRepository,
                         EnrollmentRepository enrollmentRepository,
                         CreditTransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.transactionRepository = transactionRepository;
    }

    // 1. Asignar créditos a un alumno (Solo Admin) - Transacción atómica
    @Transactional(rollbackFor = Exception.class)
    public User assignCredits(Long studentId, Integer amount, String description) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("El monto de créditos debe ser mayor a cero.");
        }

        // Bloqueamos la fila del usuario para evitar colisiones simultáneas
        User student = userRepository.findByIdWithLock(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado."));

        student.setCreditsBalance(student.getCreditsBalance() + amount);
        userRepository.save(student);

        // Registramos el movimiento en la auditoría inmutable
        CreditTransaction tx = new CreditTransaction();
        tx.setUser(student);
        tx.setAmount(amount);
        tx.setTransactionType("ASSIGNMENT");
        tx.setDescription(description != null ? description : "Asignación de créditos por administrador");
        transactionRepository.save(tx);

        return student;
    }

    // 2. Inscribir alumno a un curso - Transacción crítica con doble bloqueo
    @Transactional(rollbackFor = Exception.class)
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        // Evitamos doble inscripción rápida
        if (enrollmentRepository.existsByUserIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("El estudiante ya está inscrito en este curso.");
        }

        // Bloqueo pesimista (FOR UPDATE) en ambas tablas para garantizar integridad ACID
        User student = userRepository.findByIdWithLock(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado."));

        Course course = courseRepository.findByIdWithLock(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado."));

        // Validación de cupo
        if (course.getEnrolledCount() >= course.getMaxCapacity()) {
            throw new IllegalStateException("El curso ya alcanzó su cupo máximo.");
        }

        // Validación de saldo suficiente
        if (student.getCreditsBalance() < course.getCreditCost()) {
            throw new IllegalStateException("Créditos insuficientes. Saldo actual: "
                    + student.getCreditsBalance() + ", costo del curso: " + course.getCreditCost());
        }

        // Ejecutamos los 3 cambios juntos (si uno falla, Oracle revierte los 3)
        student.setCreditsBalance(student.getCreditsBalance() - course.getCreditCost());
        userRepository.save(student);

        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseRepository.save(course);

        CreditTransaction tx = new CreditTransaction();
        tx.setUser(student);
        tx.setAmount(-course.getCreditCost());
        tx.setTransactionType("ENROLLMENT");
        tx.setDescription("Inscripción al curso: " + course.getTitle());
        transactionRepository.save(tx);

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(student);
        enrollment.setCourse(course);
        return enrollmentRepository.save(enrollment);
    }

    // 3. Consultar historial de movimientos de un alumno
    @Transactional(readOnly = true)
    public List<CreditTransaction> getStudentTransactions(Long studentId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(studentId);
    }

    // 4. Consultar cursos inscritos de un alumno
    @Transactional(readOnly = true)
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByUserId(studentId);
    }
}

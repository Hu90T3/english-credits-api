package credits_api.controller;

import credits_api.dto.UserBalanceResponse;
import credits_api.entity.Course;
import credits_api.entity.CreditTransaction;
import credits_api.entity.Enrollment;
import credits_api.entity.User;
import credits_api.repository.CourseRepository;
import credits_api.repository.UserRepository;
import credits_api.service.CreditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CreditService creditService;

    public StudentController(UserRepository userRepository,
                             CourseRepository courseRepository,
                             CreditService creditService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.creditService = creditService;
    }

    private User getAuthenticatedStudent(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado."));
    }

    // 1. Ver SOLO mi propio perfil y saldo
    @GetMapping("/me")
    public ResponseEntity<UserBalanceResponse> getMyBalance(Authentication auth) {
        User me = getAuthenticatedStudent(auth);
        return ResponseEntity.ok(
                new UserBalanceResponse(me.getId(), me.getFullName(), me.getEmail(), me.getRole(), me.getCreditsBalance())
        );
    }

    // 2. Ver cursos disponibles
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAvailableCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    // 3. Inscribirme a un curso (descuenta créditos atómicamente)
    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<Map<String, Object>> enrollInCourse(@PathVariable Long courseId, Authentication auth) {
        User me = getAuthenticatedStudent(auth);
        Enrollment enrollment = creditService.enrollStudent(me.getId(), courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Inscripción exitosa",
                "enrollmentId", enrollment.getId(),
                "courseTitle", enrollment.getCourse().getTitle(),
                "remainingCredits", enrollment.getUser().getCreditsBalance()
        ));
    }

    // 4. Ver mi historial de movimientos de créditos
    @GetMapping("/transactions")
    public ResponseEntity<List<CreditTransaction>> getMyTransactions(Authentication auth) {
        User me = getAuthenticatedStudent(auth);
        return ResponseEntity.ok(creditService.getStudentTransactions(me.getId()));
    }
}

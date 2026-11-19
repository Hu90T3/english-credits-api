package credits_api.controller;

import credits_api.dto.AssignCreditsRequest;
import credits_api.dto.CreateCourseRequest;
import credits_api.dto.UserBalanceResponse;
import credits_api.entity.Course;
import credits_api.entity.User;
import credits_api.repository.CourseRepository;
import credits_api.service.CreditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CreditService creditService;
    private final CourseRepository courseRepository;

    public AdminController(CreditService creditService, CourseRepository courseRepository) {
        this.creditService = creditService;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        Course course = new Course();
        course.setTitle(request.title());
        course.setLevelCode(request.levelCode());
        course.setCreditCost(request.creditCost());
        course.setMaxCapacity(request.maxCapacity());
        course.setEnrolledCount(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseRepository.save(course));
    }

    @PostMapping("/credits/assign")
    public ResponseEntity<UserBalanceResponse> assignCredits(@Valid @RequestBody AssignCreditsRequest request) {
        User updated = creditService.assignCredits(request.studentId(), request.amount(), request.description());
        return ResponseEntity.ok(
                new UserBalanceResponse(updated.getId(), updated.getFullName(), updated.getEmail(), updated.getRole(), updated.getCreditsBalance())
        );
    }
}

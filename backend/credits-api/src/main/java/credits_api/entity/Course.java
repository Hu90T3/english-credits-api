package credits_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "COURSES")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Column(name = "LEVEL_CODE", nullable = false, length = 10)
    private String levelCode;

    @Column(name = "CREDIT_COST", nullable = false)
    private Integer creditCost;

    @Column(name = "MAX_CAPACITY", nullable = false)
    private Integer maxCapacity;

    @Column(name = "ENROLLED_COUNT", nullable = false)
    private Integer enrolledCount = 0;

    // Getters y Setters (genéralos con tu IDE)

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public Integer getCreditCost() {
        return creditCost;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Integer getEnrolledCount() {
        return enrolledCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public void setCreditCost(Integer creditCost) {
        this.creditCost = creditCost;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void setEnrolledCount(Integer enrolledCount) {
        this.enrolledCount = enrolledCount;
    }
}

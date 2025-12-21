package ru.nsu.vorona;

import java.util.Objects;

/**
 * Одна строка зачетной книжки: дисциплина в определённом семестре.
 */
public final class CourseRecord {

    private final String courseName;
    private final int semester;
    private final ControlType controlType;
    private Grade grade;

    public CourseRecord(String courseName, int semester, ControlType controlType) {
        this.courseName = Objects.requireNonNull(courseName);
        this.semester = semester;
        this.controlType = Objects.requireNonNull(controlType);
    }

    public String getCourseName() {
        return courseName;
    }

    public int getSemester() {
        return semester;
    }

    public ControlType getControlType() {
        return controlType;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public boolean hasGrade() {
        return grade != null;
    }
}

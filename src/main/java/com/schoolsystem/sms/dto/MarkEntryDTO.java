package com.schoolsystem.sms.dto;

/**
 * Used to receive a single student's mark from the results entry form.
 */
public class MarkEntryDTO {
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private Double marks; // null means not yet entered

    public MarkEntryDTO() {}

    public MarkEntryDTO(Long studentId, String studentName, String registrationNumber, Double marks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.registrationNumber = registrationNumber;
        this.marks = marks;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Double getMarks() { return marks; }
    public void setMarks(Double marks) { this.marks = marks; }
}

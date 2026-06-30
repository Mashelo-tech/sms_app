package com.schoolsystem.sms.model;

public enum ResultStatus {
    DRAFT,      // Teacher is still entering marks
    SUBMITTED,  // Teacher has submitted for review
    APPROVED,   // DOS has approved
    RETURNED,   // DOS sent back for correction
    LOCKED      // Finalized, cannot be edited
}

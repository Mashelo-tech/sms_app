package com.schoolsystem.sms.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for a batch of mark entries — submitted as one form for all students in a class.
 */
public class MarkEntryBatchDTO {
    private Long classLevelId;
    private Long subjectId;
    private Long termId;
    private List<MarkEntryDTO> entries = new ArrayList<>();

    public MarkEntryBatchDTO() {}

    public Long getClassLevelId() { return classLevelId; }
    public void setClassLevelId(Long classLevelId) { this.classLevelId = classLevelId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }

    public List<MarkEntryDTO> getEntries() { return entries; }
    public void setEntries(List<MarkEntryDTO> entries) { this.entries = entries; }
}

package com.example.journalsystem.entities;

import java.sql.Date;

public class MeasureDTO {
    private Long id;
    private Long performedBy;
    private String description;
    private Date performedDate;


    public MeasureDTO(Long id, Long performedBy, String description, Date performedDate) {
        this.id = id;
        this.performedBy = performedBy;
        this.description = description;
        this.performedDate = performedDate;
    }

    public Long getId() {
        return id;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public String getDescription() {
        return description;
    }

    public Date getPerformedDate() {
        return performedDate;
    }
}

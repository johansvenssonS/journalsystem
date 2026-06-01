package com.example.journalsystem.entities;


import jakarta.persistence.Id;

import java.sql.Date;

public class DiagnosisDTO {


    @Id
    private Long id;

    private Long setBy;

    private String name;

    private String description;
    private Date diagnosedDate;


    public DiagnosisDTO(Long id, Long setBy, String name, String description, Date diagnosedDate) {
        this.id = id;
        this.setBy = setBy;
        this.name = name;
        this.description = description;
        this.diagnosedDate = diagnosedDate;
    }

    public Date getDiagnosedDate() {
        return diagnosedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Long getSetBy() {
        return setBy;
    }

    public Long getId() {
        return id;
    }
}

package com.example.journalsystem.entities;

import java.sql.Date;
import java.sql.Timestamp;

public class CareContactDTO {
    private Long id;
    private Long departmentId;
    private Long responsibleId;
    private String reason;
    private Timestamp admitDate;
    private String status;


    public CareContactDTO(Long id, Long departmentId, Long responsibleId, String reason, Timestamp admitDate, String status) {
        this.id = id;
        this.departmentId = departmentId;
        this.responsibleId = responsibleId;
        this.reason = reason;
        this.admitDate = admitDate;
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Long getResponsibleId() {
        return responsibleId;
    }

    public String getReason() {
        return reason;
    }

    public Timestamp getAdmitDate() {
        return admitDate;
    }

    public String getStatus() {
        return status;
    }
}

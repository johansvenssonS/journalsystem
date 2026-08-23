package com.example.journalsystem.dto;

import java.time.LocalDateTime;

/// US-17 — body vid utskrivning. Datumet är frivilligt;
/// utelämnas det används tidpunkten för anropet.
public class DischargeCareContactRequest {

    private LocalDateTime dischargeDate;

    public DischargeCareContactRequest() {
    }

    public DischargeCareContactRequest(LocalDateTime dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDateTime dischargeDate) {
        this.dischargeDate = dischargeDate;
    }
}

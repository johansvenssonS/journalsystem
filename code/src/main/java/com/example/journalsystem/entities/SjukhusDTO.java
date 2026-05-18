package com.example.journalsystem.entities;

import java.util.List;

public class SjukhusDTO {
    private Long id;
    private String namn;
    private String adress;
    private String telefon;
    private String regionNamn;
    //private List<String> avdelningar;


    public SjukhusDTO() {
    }

    public SjukhusDTO(Long id, String namn, String adress, String telefon, String regionNamn, List<String> avdelningar) {
        this.id = id;
        this.namn = namn;
        this.adress = adress;
        this.telefon = telefon;
        this.regionNamn = regionNamn;
        this.avdelningar = avdelningar;
    }


    public Long getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }

    public String getAdress() {
        return adress;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getRegionNamn() {
        return regionNamn;
    }

    public List getAvdelningar() {
        return avdelningar;
    }
}

package com.hubspot.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SecPartner {
    @JsonProperty("partners")
    private List<Partner> pertners;

    public List<Partner> getPertners() {
        return pertners;
    }

    public void setPertners(List<Partner> pertners) {
        this.pertners = pertners;
    }
}

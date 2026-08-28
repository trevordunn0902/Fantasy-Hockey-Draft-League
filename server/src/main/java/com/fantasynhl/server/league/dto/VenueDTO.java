package com.fantasynhl.server.league.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VenueDTO {

    @JsonProperty("default")
    private String defaultName;

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
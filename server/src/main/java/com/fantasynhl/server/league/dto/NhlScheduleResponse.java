package com.fantasynhl.server.league.dto;

import java.util.List;

public class NhlScheduleResponse {

    private List<NhlScheduleGameDTO> games;

    public List<NhlScheduleGameDTO> getGames() {
        return games;
    }

    public void setGames(List<NhlScheduleGameDTO> games) {
        this.games = games;
    }
}
package com.fantasynhl.server.league;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "leagues")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "invite_code", unique = true, nullable = false)
    private String inviteCode;

    @Column(name = "invite_only")
    private boolean inviteOnly = true;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Team> teams = new ArrayList<>();

    @Column(name = "max_teams")
    private int maxTeams = 10;

    @Column(name = "draft_started")
    private boolean draftStarted = false;

    @Transient
    private List<Team> draftOrder = new ArrayList<>();

    @Column(name = "draft_order_ids")
    private String draftOrderIds; // comma-separated team IDs

    @Column(name = "current_turn_index")
    private int currentTurnIndex = 0;

    public League() {}

    // Standard getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public boolean isInviteOnly() { return inviteOnly; }
    public void setInviteOnly(boolean inviteOnly) { this.inviteOnly = inviteOnly; }

    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }

    public int getMaxTeams() { return maxTeams; }
    public void setMaxTeams(int maxTeams) { this.maxTeams = maxTeams; }

    public boolean isDraftStarted() { return draftStarted; }
    public void setDraftStarted(boolean draftStarted) { this.draftStarted = draftStarted; }

    public List<Team> getDraftOrder() { return draftOrder; }
    public void setDraftOrder(List<Team> draftOrder) { this.draftOrder = draftOrder; }

    public String getDraftOrderIds() { return draftOrderIds; }
    public void setDraftOrderIds(String draftOrderIds) { this.draftOrderIds = draftOrderIds; }

    public int getCurrentTurnIndex() { return currentTurnIndex; }
    public void setCurrentTurnIndex(int currentTurnIndex) { this.currentTurnIndex = currentTurnIndex; }

    // Helper to rebuild draftOrder from draftOrderIds
    public void reconstructDraftOrder() {
        if (draftOrderIds != null && !draftOrderIds.isEmpty()) {
            List<Long> ids = Arrays.stream(draftOrderIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            draftOrder = ids.stream()
                    .map(id -> teams.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null))
                    .filter(t -> t != null)
                    .collect(Collectors.toList());
        }
    }
}
package com.fantasynhl.server.league.dto;

import com.fantasynhl.server.user.Role;
import com.fantasynhl.server.user.User;

public class UserDTO {
    private Long id;
    private String email;
    private Role role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}
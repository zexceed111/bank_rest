package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}

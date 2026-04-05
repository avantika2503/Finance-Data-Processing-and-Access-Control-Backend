package com.finance.model.dto;

import com.finance.model.enums.Role;
import com.finance.model.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
    private Instant createdAt;
}

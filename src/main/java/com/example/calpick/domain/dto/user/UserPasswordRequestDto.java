package com.example.calpick.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordRequestDto {
    @NotBlank
    public String currentPassword;
    @NotBlank
    public String newPassword;
}

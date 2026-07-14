package com.bakery.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representación pública del usuario. Nunca expone el hash de contraseña.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String email;
    private String displayName;
    private boolean hasPhoto;
}

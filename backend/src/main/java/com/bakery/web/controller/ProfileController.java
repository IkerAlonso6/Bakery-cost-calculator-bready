package com.bakery.web.controller;

import com.bakery.application.dto.UpdateProfileRequest;
import com.bakery.application.dto.UserDTO;
import com.bakery.application.mapper.UserMapper;
import com.bakery.application.port.IUserRepository;
import com.bakery.application.service.ProfileService;
import com.bakery.domain.model.User;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Perfil del usuario autenticado: ver, cambiar nombre y foto.
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserMapper userMapper;

    public ProfileController(ProfileService profileService, UserMapper userMapper) {
        this.profileService = profileService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public UserDTO getProfile() {
        User user = profileService.getCurrent();
        return userMapper.toDto(user, profileService.currentHasPhoto());
    }

    @PutMapping
    public UserDTO updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User updated = profileService.updateDisplayName(request.getDisplayName());
        return userMapper.toDto(updated, profileService.currentHasPhoto());
    }

    @PostMapping("/photo")
    public ResponseEntity<Void> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Photo file must not be empty");
        }
        String contentType = file.getContentType() != null ? file.getContentType() : MediaType.IMAGE_JPEG_VALUE;
        profileService.updatePhoto(file.getBytes(), contentType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> getPhoto() {
        return profileService.getPhoto()
                .map(photo -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(
                                photo.getContentType() != null ? photo.getContentType() : MediaType.IMAGE_JPEG_VALUE))
                        .body(photo.getContent()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

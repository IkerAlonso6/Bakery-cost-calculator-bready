package com.bakery.application.service;

import com.bakery.application.exception.UserNotFoundException;
import com.bakery.application.port.CurrentUserProvider;
import com.bakery.application.port.IUserRepository;
import com.bakery.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Casos de uso del perfil del usuario autenticado: ver, cambiar nombre,
 * subir/consultar foto.
 */
@Service
public class ProfileService {

    private final IUserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public ProfileService(IUserRepository userRepository, CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public User getCurrent() {
        return loadCurrent();
    }

    public boolean currentHasPhoto() {
        return userRepository.hasPhoto(currentUserProvider.getCurrentUserId());
    }

    public User updateDisplayName(String newDisplayName) {
        User user = loadCurrent();
        user.changeDisplayName(newDisplayName);
        return userRepository.save(user);
    }

    public void updatePhoto(byte[] content, String contentType) {
        Integer userId = currentUserProvider.getCurrentUserId();
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Photo must not be empty");
        }
        userRepository.updatePhoto(userId, content, contentType);
    }

    public Optional<IUserRepository.StoredPhoto> getPhoto() {
        return userRepository.findPhoto(currentUserProvider.getCurrentUserId());
    }

    public void deletePhoto() {
        userRepository.deletePhoto(currentUserProvider.getCurrentUserId());
    }

    private User loadCurrent() {
        Integer userId = currentUserProvider.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}

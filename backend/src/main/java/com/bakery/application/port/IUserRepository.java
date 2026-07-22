package com.bakery.application.port;

import com.bakery.domain.model.User;

import java.util.Optional;

/**
 * Port de persistencia de usuarios.
 */
public interface IUserRepository {

    User save(User user);

    Optional<User> findById(Integer id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /** true si el usuario tiene una foto de perfil guardada. */
    boolean hasPhoto(Integer userId);

    void updatePhoto(Integer userId, byte[] photo, String contentType);

    void deletePhoto(Integer userId);

    /** Devuelve la foto (bytes + content-type) si existe. */
    Optional<StoredPhoto> findPhoto(Integer userId);

    /** Foto de perfil almacenada. */
    class StoredPhoto {
        private final byte[] content;
        private final String contentType;

        public StoredPhoto(byte[] content, String contentType) {
            this.content = content;
            this.contentType = contentType;
        }

        public byte[] getContent() {
            return content;
        }

        public String getContentType() {
            return contentType;
        }
    }
}

package ru.alex.professionalslanguageapi.mapper.user;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.dto.user.UserAuthDto;
import ru.alex.professionalslanguageapi.mapper.ReadMapper;


@Component
public class UserAuthMapper extends ReadMapper<User, UserAuthDto> {

    @Override
    public UserAuthDto toDto(User entity) {
        return new UserAuthDto(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAvatar(),
                null
        );
    }

    public UserAuthDto toDto(User entity, String accessToken) {
        return new UserAuthDto(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAvatar(),
                accessToken
        );
    }
}

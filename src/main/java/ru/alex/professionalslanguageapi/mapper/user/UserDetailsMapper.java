package ru.alex.professionalslanguageapi.mapper.user;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.dto.user.UserDetailsDto;
import ru.alex.professionalslanguageapi.mapper.ReadMapper;

@Component
public class UserDetailsMapper extends ReadMapper<User, UserDetailsDto> {
    @Override
    public UserDetailsDto toDto(User entity) {
        return new UserDetailsDto(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPassword(),
                entity.getAvatar()
        );
    }
}

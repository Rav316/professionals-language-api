package ru.alex.professionalslanguageapi.mapper.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.dto.user.UserRegisterDto;
import ru.alex.professionalslanguageapi.mapper.CreateMapper;

@Component
@RequiredArgsConstructor
public class UserRegisterMapper extends CreateMapper<User, UserRegisterDto> {
    private final PasswordEncoder passwordEncoder;

    @Override
    public User toEntity(UserRegisterDto dto) {
        User user = new User();
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPassword(passwordEncoder.encode(dto.password()));

        return user;
    }
}

package ru.alex.professionalslanguageapi.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.professionalslanguageapi.database.entity.LeaderboardItem;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.database.repository.LeaderboardItemRepository;
import ru.alex.professionalslanguageapi.database.repository.UserRepository;
import ru.alex.professionalslanguageapi.dto.user.UserAuthDto;
import ru.alex.professionalslanguageapi.dto.user.UserLoginDto;
import ru.alex.professionalslanguageapi.dto.user.UserRegisterDto;
import ru.alex.professionalslanguageapi.mapper.user.UserAuthMapper;
import ru.alex.professionalslanguageapi.mapper.user.UserRegisterMapper;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final LeaderboardItemRepository leaderboardItemRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRegisterMapper userRegisterMapper;
    private final UserAuthMapper userAuthMapper;

    @Transactional
    public UserAuthDto register(UserRegisterDto userDto) {
        User user = userRegisterMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        userRepository.flush();
        LeaderboardItem leaderboardItem = new LeaderboardItem();
        leaderboardItem.setUser(savedUser);
        leaderboardItem.setScore(0);
        leaderboardItemRepository.save(leaderboardItem);
        String authToken = jwtService.generateAuthToken(userDto.email());
        return userAuthMapper.toDto(savedUser, authToken);
    }

    @Transactional
    public UserAuthDto login(UserLoginDto userDto) {
        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                userDto.email(),
                userDto.password()
        );
        authenticationManager.authenticate(authInputToken);

        User user = userRepository.findByEmail(userDto.email())
                .orElseThrow(() -> new UsernameNotFoundException("user with username " + userDto.email() + " not found"));
        String token = jwtService.generateAuthToken(userDto.email());
        return userAuthMapper.toDto(user, token);
    }

    @Transactional
    public UserAuthDto refreshAuthToken(HttpServletRequest request) {
        return jwtService.refreshAuthToken(request);
    }
}

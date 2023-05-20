package ru.nsu.shelbogashev.tdgserver.api.v0.delegate;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.shelbogashev.tdgserver.generated.api.AuthApiDelegate;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.AuthRequest;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.AuthResponse;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.Message;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.UserRequest;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.exception.IllegalOperationException;
import ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage;
import ru.nsu.shelbogashev.tdgserver.server.rest.User;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtTokenProvider;
import ru.nsu.shelbogashev.tdgserver.service.UserService;

import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.SYSTEM_ALREADY_HAS_USERNAME_ERROR;
import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.USER_NOT_FOUND;

@RestController
@Log4j2
public class AuthApiDelegateImpl implements AuthApiDelegate {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AuthApiDelegateImpl(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @NotNull
    @Override
    public ResponseEntity<AuthResponse> authorizeUser(@NotNull AuthRequest authenticationRequestDto) {
        log.info("authorizeUser() : authenticationRequestDto.getIdentifier()=" + authenticationRequestDto.getIdentifier());
        String username = authenticationRequestDto.getIdentifier();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, authenticationRequestDto.getPassword())
        );
        // TODO: implement
//        if (!authentication.isAuthenticated()) {
//            throw new BadCredentialsException(ResponseMessage.BAD_CREDENTIALS_ERROR);
//        }

        User user = Optional.of(username)
                .flatMap(userService::findByUsername)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        String token = jwtTokenProvider.createToken(user);
        return ResponseEntity.ok(ResponseFactory.getAuthResponse(token));
    }

    @NotNull
    @Override
    public ResponseEntity<Message> registerUser(@NotNull UserRequest userRequest) {
        if (Optional.of(userRequest.getUsername()).flatMap(userService::findByUsername).isPresent()) {
            throw new IllegalOperationException(SYSTEM_ALREADY_HAS_USERNAME_ERROR);
        }

        User user = Mapper.toUser(userRequest);
        return ResponseEntity.ok(ResponseFactory.getMessage(userService.register(user)));
    }
}

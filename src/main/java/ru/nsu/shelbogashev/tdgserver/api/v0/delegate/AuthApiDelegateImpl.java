package ru.nsu.shelbogashev.tdgserver.api.v0.delegate;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.shelbogashev.tdgserver.generated.api.AuthApiDelegate;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.AuthDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.MessageDto;
import ru.nsu.shelbogashev.tdgserver.generated.api.dto.TokenDto;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.dto.ResponseFactory;
import ru.nsu.shelbogashev.tdgserver.server.exception.AuthException;
import ru.nsu.shelbogashev.tdgserver.server.rest.User;
import ru.nsu.shelbogashev.tdgserver.service.AuthService;

import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.INCORRECT_PRESENTATION_OF_CREDENTIALS;
import static ru.nsu.shelbogashev.tdgserver.service.CredentialsHelper.isValidPassword;
import static ru.nsu.shelbogashev.tdgserver.service.CredentialsHelper.isValidUsername;

@RestController
@Log4j2
public class AuthApiDelegateImpl implements AuthApiDelegate {
    private final AuthService userService;

    @Autowired
    public AuthApiDelegateImpl(AuthService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<MessageDto> registerUser(AuthDto authDto) {
        if (!isValidUsername(authDto.getUsername()) || isValidPassword(authDto.getPassword())) {
            throw new AuthException(INCORRECT_PRESENTATION_OF_CREDENTIALS);
        }

        log.info("registerUser() : authDto.getUsername()=" + authDto.getUsername());

        User user = Mapper.toUser(authDto);
        return ResponseEntity.ok(ResponseFactory.toMessage(userService.register(user)));
    }

    @Override
    public ResponseEntity<TokenDto> loginUser(AuthDto authDto) {
        if (!isValidUsername(authDto.getUsername())) {
            throw new AuthException(INCORRECT_PRESENTATION_OF_CREDENTIALS);
        }

        log.info("loginUser() : authDto.getUsername()=" + authDto.getUsername());

        User user = Mapper.toUser(authDto);
        return ResponseEntity.ok(ResponseFactory.toTokenDto(userService.login(user)));
    }
}

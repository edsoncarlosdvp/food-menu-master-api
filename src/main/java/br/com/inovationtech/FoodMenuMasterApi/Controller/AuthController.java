package br.com.inovationtech.FoodMenuMasterApi.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.inovationtech.FoodMenuMasterApi.DTO.LoginRequestDTO;
import br.com.inovationtech.FoodMenuMasterApi.DTO.LoginResponseDTO;
import br.com.inovationtech.FoodMenuMasterApi.Security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.security.jwt.expiration-ms}")
    private long expirationMs;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Login request received for user: {}", loginRequest.username());

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        String token = jwtService.generateToken(loginRequest.username());

        return ResponseEntity.ok(new LoginResponseDTO(token, "Bearer", expirationMs));
    }
}

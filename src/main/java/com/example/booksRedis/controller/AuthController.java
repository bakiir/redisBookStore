package com.example.booksRedis.controller;


import com.example.booksRedis.model.User;
import com.example.booksRedis.repo.UserRepo;
import com.example.booksRedis.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final PasswordEncoder encoder;
    private final UserRepo userRepo;
    private final SessionService sessionService;




    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User request, HttpSession session) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // сохраняем авторизацию в SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);

        // сохраняем SecurityContext в сессию вручную
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok("Logged in. Session ID: " + session.getId());
    }


    @GetMapping("/debug")
    public String whoAmI() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return "User: " + auth.getName() + " | Roles: " + auth.getAuthorities();
    }




    @PostMapping("/register")
    public String register(@RequestBody User request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        userRepo.save(user);
        return "User registered";
    }

    @GetMapping("/me")
    public String currentSession(HttpSession session){
        return "Your session: "+session.getId();
    }

    @PostMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "Logged out";
    }
}

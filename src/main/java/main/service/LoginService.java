package main.service;

import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.body.UserBody;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;//
    private final UserRepository userRepository;//

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();
        return getLoginResponse(user.getUsername());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

    public LoginResponse check(Principal principal) {
        if (principal == null) {
            return new LoginResponse();
        }

        return getLoginResponse(principal.getName());
    }

    private LoginResponse getLoginResponse(String email) {

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);

        main.model.User currentUser =
                userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        UserBody userBody = new UserBody();
        userBody.setEmail(currentUser.getEmail());
        userBody.setName(currentUser.getName());
        userBody.setModeration(currentUser.getIsModerator() == 1);
        userBody.setId(currentUser.getId());
        userBody.setPhoto(currentUser.getPhoto());
        userBody.setModerationCount(currentUser.getModeratorPosts().size());
        userBody.setSettings(currentUser.getIsModerator() == 1);

        loginResponse.setUser(userBody);

        return loginResponse;

    }
}

package com.campusgear.service;



import com.campusgear.dao.UserDAO;
import com.campusgear.model.User;
import com.campusgear.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired 
    private UserDAO userDAO;
    @Autowired 
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(String name, String email, String password, String role) {
        if (role == null || (!role.equalsIgnoreCase("STUDENT")
                && !role.equalsIgnoreCase("STAFF")
                && !role.equalsIgnoreCase("ADMIN"))) {
            throw new RuntimeException("Invalid role. Must be STUDENT, STAFF, or ADMIN");
        }

        if (userDAO.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setRole(role.toUpperCase());
        return userDAO.save(user);
    }

    public String login(String email, String password) {
        User u = userDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, u.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(u.getId(), u.getEmail(), u.getRole());
    }
}

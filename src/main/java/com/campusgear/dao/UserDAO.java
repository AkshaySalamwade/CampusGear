package com.campusgear.dao;


import com.campusgear.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserDAO {

    @Autowired
    private NamedParameterJdbcTemplate jdbc;
    
    @Autowired
    private NamedParameterJdbcTemplate npjt;
    

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("role")
        );
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        Map<String, Object> params = Map.of("email", email);
        List<User> users = jdbc.query(sql, params, this::mapRow);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public User save(User u) {
        // Step 1: Generate ID manually (if table not empty)
        String idSql = "SELECT COALESCE(MAX(id), 0) + 1 FROM users";
        Long newId = jdbc.getJdbcTemplate().queryForObject(idSql, Long.class);

        // Step 2: Insert user with that ID
        String insertSql = """
            INSERT INTO users (id, name, email, password_hash, role)
            VALUES (:id, :name, :email, :password, :role)
        """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", newId);
        params.put("name", u.getName());
        params.put("email", u.getEmail());
        params.put("password", u.getPassword());
        params.put("role", u.getRole());

        int rows = npjt.update(insertSql, params);
        if (rows > 0) {
            u.setId(newId);
        }
        return u;
    }
}

package com.campusgear.model;

import com.campusgear.util.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
   // private Role role;
    private String role;
}

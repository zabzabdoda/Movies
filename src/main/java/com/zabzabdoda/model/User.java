package com.zabzabdoda.model;

import com.zabzabdoda.annotations.FieldValuesMatch;
import com.zabzabdoda.annotations.HasCharacter;
import com.zabzabdoda.annotations.HasDigit;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
@FieldValuesMatch.List({
        @FieldValuesMatch(
                field = "password",
                fieldMatch = "confirmPassword",
                message = "Passwords do not match"
        ),
        @FieldValuesMatch(
                field = "email",
                fieldMatch = "confirmEmail",
                message = "Emails do not match"
        )

})
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "id", nullable = false)
    private int id;

    @Size(min = 3,message = "Username must be at least 3 characters long")
    private String username;

    @Size(min = 5, message = "Password must be at least 5 characters long")
    @HasCharacter(message = "Please include at least 1 character in your password")
    @HasDigit(message = "Please include at least 1 digit in your password")
    private String password;

    @Email(message = "Please provide a valid email address, i.e., name@domain.com")
    private String email;

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "role_id",referencedColumnName = "role_id",nullable = false)
    private Roles role;

    @Transient
    private String confirmEmail;

    @Transient
    private String confirmPassword;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<Review> reviews = new LinkedHashSet<>();

}
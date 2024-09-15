package com.testspring.testspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    // Autorise les urls a être accéder par tout le monde
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/Inscription").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/logout").permitAll()
                .requestMatchers("/public/assets/css/**").permitAll()
                .requestMatchers("/public/assets/images/**").permitAll()
                .requestMatchers("/public/assets/js/**").permitAll()
                .requestMatchers("/public/assets/fonts/**").permitAll()

                .requestMatchers("private/assets/images/**").permitAll()
                .requestMatchers("private/assets/css/**").permitAll()
                .requestMatchers("private/assets/js/**").permitAll()
                .requestMatchers("private/assets/libs/simplebar/**").permitAll()
                .requestMatchers("private/assets/libs/bootstrap/js/**").permitAll()
                .requestMatchers("private/assets/libs/node-waves/**").permitAll()
                .requestMatchers("private/assets/libs/feather-icons/**").permitAll()
                .requestMatchers("private/assets/libs/particles.js/**").permitAll()
                .requestMatchers("private/assets/libs/feather-icons/**").permitAll()

                .requestMatchers("/admin/**").hasAuthority("ROLE_administrateur")
                .requestMatchers("/user/**").hasAuthority("ROLE_utilisateur")
                .anyRequest().authenticated())
                // Configure le handler de succès de connexion personnalisé
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(successHandler))

                // Rédirige l'utilisateur vers l'url / après que ce dernier se soit déconnecter
                .logout(config -> config.logoutSuccessUrl("/"))

                .build();
    }

    // Permet de coder le password d'un utilisateur
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

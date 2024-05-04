package com.taskmaster.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.taskmaster.service.AppUserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private static final String[] WHITELIST_ENDPOINTS = { "/login", "/logout", "/register/**", "/assets/**", "/auth/**" };

	private static final String[] AUTHENTICATE_ENDPOINTS = { "/", "/api/**" };

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.csrfTokenRepository
            (CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.authorizeHttpRequests((auth) -> auth.requestMatchers(WHITELIST_ENDPOINTS).permitAll()
						.requestMatchers(AUTHENTICATE_ENDPOINTS).authenticated())
				.formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/").permitAll())
				.logout((logout) -> logout.logoutSuccessUrl("/login").permitAll())
				.sessionManagement((sessionManagement) -> sessionManagement.maximumSessions(1));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationProvider(AppUserService appUserService) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(appUserService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		List<AuthenticationProvider> providers = List.of(authenticationProvider);
		return new ProviderManager(providers);
	}

}
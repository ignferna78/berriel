package com.project.casaberriel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthenticationSuccessHandler successHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

		//UserBuilder usuarios = User.withDefaultPasswordEncoder();

		//auth.inMemoryAuthentication().withUser(usuarios.username("admin").password("123").roles("ADMIN"));
	//	auth.inMemoryAuthentication().withUser(usuarios.username("pepe").password("123").roles("USER"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/reservas/admin/**").hasRole("ADMIN")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/user/**").hasRole("USER")
				.antMatchers("/css/**", "/js/**", "/images/**", "/lib/**", "/locales/**").permitAll()
				.antMatchers("/reservas/**").permitAll()
				.antMatchers("/registro/**").permitAll()
				.antMatchers("/", "/index", "/login").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/reservas/index")
				.loginProcessingUrl("/autenticacionUsusario")
				.failureUrl("/reservas/index?error=true") // Redirige a la página con parámetro de error
				.successHandler(successHandler) // Usa el manejador de éxito personalizado
				.permitAll()
				.and()
				.logout().logoutSuccessUrl("/reservas/index") // Redirigir a la página de inicio tras
																				// hacer logout
				.invalidateHttpSession(true) // Invalidar la sesión al hacer logout
				.clearAuthentication(true).permitAll();
	}
}
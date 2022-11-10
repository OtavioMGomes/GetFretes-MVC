package br.com.global.mobility.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        http.httpBasic()
            .and()
                .authorizeHttpRequests() 

                // Usuários
                .antMatchers(HttpMethod.GET, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/request/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/request/**").authenticated()
                .antMatchers(HttpMethod.POST, "/request/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/request/**").authenticated()

                .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                // WEB
                .antMatchers(HttpMethod.POST, "/task").authenticated()
                .antMatchers("/css/**").permitAll()

                // Others
                .anyRequest().denyAll()
                // .anyRequest().permitAll()

            .and()
                .csrf().disable()
                // .headers().frameOptions().disable()
            // .and()
                .formLogin()
        ;        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
}

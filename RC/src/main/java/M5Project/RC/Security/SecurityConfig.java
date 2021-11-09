package M5Project.RC.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf().disable()
            .antMatcher("/**").authorizeRequests()    //endpoints that require login
            .antMatchers("/", "/error", "/css/**","/images/**","js/**").permitAll() //endpoints that don't require login
            .antMatchers("/oauth2/authorization/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .loginPage("/")
                .defaultSuccessUrl("/logged", true);

        httpSecurity.sessionManagement()
            .maximumSessions(1)
            .expiredUrl("/?expired=true");
    }

}

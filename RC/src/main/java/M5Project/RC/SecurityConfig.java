package M5Project.RC;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .antMatcher("/**").authorizeRequests()              //endpoints that require login
                .antMatchers("/").permitAll()           //endpoints that don't need login
                .anyRequest().authenticated()
                .and()
                .oauth2Login();
    }

}

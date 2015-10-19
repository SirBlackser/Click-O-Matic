package be.uantwerpen.iw.ei.se;

import be.uantwerpen.iw.ei.se.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@SpringBootApplication
public class ClickOMaticApplication extends WebMvcConfigurerAdapter
{
    public static void main(String[] args)
    {
        SpringApplication.run(ClickOMaticApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.UK);

        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor()
    {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");

        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Configuration
    protected static class AuthenticationSecurity extends GlobalAuthenticationConfigurerAdapter
    {
        @Autowired
        private UserService userService;

        @Override
        public void init(AuthenticationManagerBuilder authBuilder) throws Exception
        {
            authBuilder.userDetailsService(userService);
        }
    }

    @Bean
    public ApplicationSecurity applicationSecurity()
    {
        return new ApplicationSecurity();
    }

    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter
    {
        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            http.authorizeRequests().antMatchers("/homepage").permitAll().anyRequest().fullyAuthenticated()
                                    .and().formLogin().loginPage("/homepage").failureUrl("/homepage?error")
                                    .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/homepage?logOut"))
                                    .and().exceptionHandling().accessDeniedPage("/homepage?accessDenied");
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
       //registry.addViewController("/homepage").setViewName("homepage");
       // registry.addViewController("/homepage?error").setViewName("error");
    }
}

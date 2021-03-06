package com.bolsa.factura.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bolsa.factura.app.auth.handler.LoginSuccessHandler;
import com.bolsa.factura.app.models.service.JpaUserDatailsService;

@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginSuccessHandler successHandler;

    @Autowired
    private DataSource datasource;
    
    @Autowired
    private JpaUserDatailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	// rutas publicas
	http.authorizeRequests().antMatchers("/", "/css/**", "/js/**", "/images/**", "/listar", "/locale").permitAll()
		/*
		 * .antMatchers("/ver/**").hasAnyRole(ROL_USER)
		 * .antMatchers("/uploads/**").hasAnyRole(ROL_USER)
		 * .antMatchers("/form/**").hasAnyRole(ROL_ADMIN)
		 * .antMatchers("/editar/**").hasAnyRole(ROL_ADMIN)
		 * .antMatchers("/delete/**").hasAnyRole(ROL_ADMIN)
		 * .antMatchers("/factura/**").hasAnyRole(ROL_ADMIN)
		 * 
		 * //Esto se reemplaza por anotaciones
		 */
		.anyRequest().authenticated().and().formLogin().successHandler(successHandler).loginPage("/login")
		.permitAll().and().logout().permitAll().and().exceptionHandling().accessDeniedPage("/error_403");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder build) throws Exception {
	PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	// esto es para el uso de INMEMORYAUTHENTICATION
	/*
	 * UserBuilder userBuilder =
	 * User.builder().passwordEncoder(passwordEncoder::encode);
	 * 
	 * build.inMemoryAuthentication()
	 * .withUser(userBuilder.username("admin").password("admin").roles(Utils.ADMIN,
	 * Utils.USER))
	 * .withUser(userBuilder.username("andres").password("andres").roles(Utils.USER)
	 * );
	 */

	// esto es para el uso de DATASOURCE
	/*
	build.jdbcAuthentication().dataSource(datasource).passwordEncoder(passwordEncoder)
		.usersByUsernameQuery("select username, password, enabled from user where username = ?")
		.authoritiesByUsernameQuery(
			"select u.username, a.authority from authorities a inner join user u on a.user_id = u.id where u.username= ?");
			*/
	
	//Esto es para el uso de JPA
	build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

}

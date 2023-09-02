package comercio.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import comercio.api.rest.service.ImplementacaoUserDetaisService;


//mapeia, autoriza ou bloqueia acessos, contra as permissoes dos usuarios
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetaisService   implementacaoUserDetaisService;


	@Override
	protected void configure(HttpSecurity http) throws Exception {
        
		//proteção contra usuarios que nao estao validados por token
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		.disable().authorizeRequests()
		.antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		.antMatchers(HttpMethod.OPTIONS,"/**").permitAll() //liberacao de requisicoes
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index") //redireciona apos usuario deslogar
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  //faz logout e invalida usuario
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
				    UsernamePasswordAuthenticationFilter.class)//filtra as requisições de login para autenticação
		// filtra demais requisições para veirifcar a presença do token no header
		.addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);

	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(implementacaoUserDetaisService) // ira consultar o usuario no banco de dados
		.passwordEncoder(new BCryptPasswordEncoder()); //padrao de codificação de senha

	}

}

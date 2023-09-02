package comercio.api.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import comercio.api.rest.model.Usuario;

//estabelece o gerenciador de token
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	//configura o gerenciador de autenticação
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		
		//obriga a autenticar a url
        super(new AntPathRequestMatcher(url));

        //gerenciador de autenticação
        setAuthenticationManager(authenticationManager);

	}

	//retorna o usuario ao processar a autenticação
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		    //pega o token pra validar
		    Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

		//retorna o login do usuario, senha e acessos/permissões
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
	}

	//retorna o token
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());

	}

}

package comercio.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import comercio.api.rest.ApplicationContextLoad;
import comercio.api.rest.model.Usuario;
import comercio.api.rest.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


//responsavel por autenticar e gerar o token e validar o token
@Service
@Component
public class JWTTokenAutenticacaoService {

	private static final long EXPIRATION_TIME = 172800000; //token expira em dois dias

	private static final String SECRET ="SenhaExtremamenteSecreta"; //senha unica para compor autentição e ajudar na segurança

	private static final String TOKEN_PREFIX = "Bearer"; //prefiço de token

	private static final String HEADER_STRING ="Authorization"; //

	//gera o token e adiciona ao header da resposta http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		//montagem do token
		String JWT = Jwts.builder() // chama o gerador de token
				.setSubject(username) //adiciona o usuario
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); // compactação e algoritmo de geração de senha

		String token = TOKEN_PREFIX + " " + JWT; // junta token com prefixo

		response.addHeader(HEADER_STRING, token); // adiciona ao header http
		
		//atualizar o token no banco
		ApplicationContextLoad.getApplicationContext()
		.getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
       
		
		liberacaoCors(response);//libera resposta para portas diferentes que usam API ou caso clientes web

		response.getWriter().write("{\"Authorization\": \""+token+"\"}"); // escreve token no body do http

	}

	//retorna o usuario validado com token
	public Authentication getAutentication(HttpServletRequest request, HttpServletResponse response) {

		//pega o token do header http
		String token = request.getHeader(HEADER_STRING);

		try {
		if(token != null) {
                                                                //trim tira o espaço vazio do token salvo
			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

			//faz a validação do token do usuario na requisição
			String user = Jwts.parser().setSigningKey(SECRET)
					.parseClaimsJws(tokenLimpo)
					.getBody().getSubject();

			if( user != null ) {

				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserByLogin(user);

			 if(usuario != null) {

				 //valida o token do banco com o token do header
				 if(tokenLimpo.equalsIgnoreCase(usuario.getToken())) {

				 return new UsernamePasswordAuthenticationToken(
						 usuario.getLogin(),
						 usuario.getSenha(),
						 usuario.getAuthorities());
				 }

			 }

			}

		}
		//tratamento de token expirado
		}catch (io.jsonwebtoken.ExpiredJwtException e) {
			try {
				response.getOutputStream().println("Seu TOKEN expirou, faça login novamente.");
			} catch (IOException e1) {
			}
		}

		    liberacaoCors(response);
			return null; //nao autorizado
	}
	
    
	//medida de segurança que impede solicitações de domínios diferentes por padrão nos navegadores da web
	// Ele permite que um servidor especifique quais domínios externos têm permissão para acessar seus recursos por meio de solicitações HTTP.
	private void liberacaoCors(HttpServletResponse response) {

		//Isso permite que qualquer origem (qualquer domínio) acesse os recursos deste servidor
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		// Isso permite que qualquer cabeçalho HTTP seja usado nas solicitações para este servidor
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}

		// Isso especifica os cabeçalhos que podem ser usados na solicitação
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}

		// Isso especifica os métodos HTTP que podem ser usados ao acessar os recursos deste servidor
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}

}

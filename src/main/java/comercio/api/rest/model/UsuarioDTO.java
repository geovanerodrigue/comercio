package comercio.api.rest.model;

import java.io.Serializable;


//padrao dto serve para filtrar por dados especificos que devem retornar de acordo com regra de negocio
//trafegar dados e precisa esconder alguns atributos da classe entidade
public class UsuarioDTO implements Serializable {

	private static final long serialVersionUID =1L;

	private String userLogin;
	private String userEmail;

	public UsuarioDTO(Usuario usuario) {
		this.userLogin = usuario.getLogin();
		this.userEmail = usuario.getEmail();
	}

	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}




}

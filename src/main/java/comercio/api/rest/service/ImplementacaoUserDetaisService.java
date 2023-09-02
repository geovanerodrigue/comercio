package comercio.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import comercio.api.rest.model.Usuario;
import comercio.api.rest.repository.UsuarioRepository;

//responsavel pela validação do usuario
@Service                                               //pacote userdetails usado para metodos que envolvem usuario
public class ImplementacaoUserDetaisService implements UserDetailsService {

   @Autowired
   private UsuarioRepository  usuarioRepository;

     @Override
     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Usuario usuario = usuarioRepository.findUserByLogin(username);

    if(usuario == null) {
    	throw new UsernameNotFoundException("Usuário não foi encontrado.");
    }

    //carrega os itens do usuario, login, senha, permissões
	 return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
 }



}

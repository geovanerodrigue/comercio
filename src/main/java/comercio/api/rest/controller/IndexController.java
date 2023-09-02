package comercio.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import comercio.api.rest.model.Telefone;
import comercio.api.rest.model.Usuario;
import comercio.api.rest.model.UsuarioDTO;
import comercio.api.rest.repository.UsuarioRepository;

@CrossOrigin //permite requisições de aplicações de fora, para um especifico seria @CrossOrigin(origins="https://www.exemplo.com.br/") ou
             //@CrossOrigin(origins={"https://www.exemplo.com.br/", "https://www.outroexemplo.com.br/"})
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository  usuarioRepository;

	@CrossOrigin(origins = "www.exemplo.com.br") //libera o end-point apenas para a aplicação especifica
	@GetMapping(name = "/{id}/relatoriopdf/{venda}", produces = "application/json")  //parametro uri que reflete na url de requisição
	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id,@PathVariable(value = "venda") Long venda) {

     Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}
	
	
	//retorna usuario
	@ResponseBody
	@GetMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value="cacheuser", allEntries = true) //retira e atualiza o cache
	@CachePut("cacheuser")   //carrega via cache
	public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id) { //utiliza DTO para filtrar os item retornados

     Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	//lista usuario
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuario() throws InterruptedException{

		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();



		return new ResponseEntity<>(list, HttpStatus.OK);

	}

	@DeleteMapping(value ="/{id}", produces = "application/text")
	public String delete(@PathVariable("id")Long id) {

		usuarioRepository.deleteById(id);

		return "ok";

	}


	@PostMapping(value="/cadastrar", produces="application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception{

		for (Telefone element : usuario.getTelefones()) {
			element.setUsuario(usuario);
		}
		
		//usando api externa publica
		URL url = new URL("HTTPS://viacep.com.br/ws/"+usuario.getCep()+"/json/ ");
		
		URLConnection connection = url.openConnection();
		
		InputStream is = connection.getInputStream();
		
		BufferedReader  br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(userAux.getCep());
		
		usuario.setLogradouro(userAux.getLogradouro());
		
		usuario.setComplemento(userAux.getComplemento());
		
		usuario.setBairro(userAux.getBairro());
		
		usuario.setLocalidade(userAux.getLocalidade());
		
		usuario.setUf(userAux.getUf());
		
		//criptografa a senha ao salvar usuario
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);

	}

	@PutMapping(name = "/editarusuario")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){

		//outras rotinas antes de atualizar
		for (Telefone element : usuario.getTelefones()) {
			element.setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());

		if(!userTemporario.getSenha().equals(usuario.getSenha())) {//senhas diferentes
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);

	}

}

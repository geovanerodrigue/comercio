package comercio.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@EntityScan(basePackages = {"comercio.api.rest.model"})  //escanear componentes do modelo
@ComponentScan(basePackages = {"comercio.*"})
@EnableJpaRepositories(basePackages = {"comercio.api.rest.repository"}) //ativar o jpa
@EnableTransactionManagement
@EnableWebMvc     //ativar mvc
@RestController
@EnableAutoConfiguration   //autoconfigurar o projeto
@EnableCaching //habilita o carregamento por cache
public class ComercioApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ComercioApplication.class, args);
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

	
	// continuacao do cors que libera o acesso a aplicacao para aplicacoes externas
	@Override
	public void addCorsMappings(CorsRegistry registry) {
    
		registry.addMapping("/usuario/**") //especifica onde esta sendo liberado, parte de controllers		
		.allowedMethods("*") //especifica onde esta sendo liberado, parte de get, post...
		.allowedOrigins("*"); //especifica para quem esta sendo liberado
	}

}

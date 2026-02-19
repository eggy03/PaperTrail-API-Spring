package io.github.eggy03.papertrail.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
        info = @Info(
				title = "PaperTrail API Spring",
				version = "v1.0.2",
                description = "Persistence Storage Operations API for the PaperTrail Bot",
                license = @License(name = "AGPLv3"),
                contact = @Contact(name = "Egg-03", email = "eggzerothree@proton.me")
        )
)
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}

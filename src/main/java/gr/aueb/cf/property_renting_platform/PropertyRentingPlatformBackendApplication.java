package gr.aueb.cf.property_renting_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class PropertyRentingPlatformBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PropertyRentingPlatformBackendApplication.class, args);
	}

}

package cloud.fogbow.fhs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;

@SpringBootApplication(exclude = RepositoryRestMvcAutoConfiguration.class)
public class FogbowFhsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FogbowFhsApplication.class, args);
    }
}

package lastunion.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(Application.class)
public class ApplicationTests {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApplicationTests.class, args);
    }
}
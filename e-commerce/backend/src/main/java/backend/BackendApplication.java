package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
						.directory("C:/koreansandwich/2024-capstone/e-commerce/backend/src/main/resources")
								.load();

		String apiKey = dotenv.get("OPENAI_API_KEY");
		System.out.println("Loaded API Key: " + apiKey);

		SpringApplication.run(BackendApplication.class, args);

	}

}

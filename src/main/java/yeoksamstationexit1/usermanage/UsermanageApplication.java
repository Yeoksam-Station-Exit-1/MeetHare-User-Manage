package yeoksamstationexit1.usermanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class UsermanageApplication {
	@PostConstruct
	public void started() { TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul")); }

	public static void main(String[] args) {
		SpringApplication.run(UsermanageApplication.class, args);
	}

}


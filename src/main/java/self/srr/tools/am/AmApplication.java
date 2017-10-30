package self.srr.tools.am;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import self.srr.tools.am.common.AMConfig;

@SpringBootApplication
@EnableConfigurationProperties({AMConfig.class})
@EnableScheduling
public class AmApplication {


	public static void main(String[] args) {
		SpringApplication.run(AmApplication.class, args);
	}
}

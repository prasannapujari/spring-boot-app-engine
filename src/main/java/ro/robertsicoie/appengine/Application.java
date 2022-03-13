package ro.robertsicoie.appengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class Application {

	@Autowired()
	@Qualifier("datastoreUserDao")

	private static final Logger logger = Logger.getLogger("My App");
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@GetMapping("/")
	public String root() {

	/*	User user = new User();
		user.setIp(request.getRemoteAddr());
		user.setDate(new Date(request.getSession().getCreationTime()));
		user.setAgent(request.getHeader("User-Agent"));
		userDao.add(user);

		StringBuilder response = new StringBuilder();
		userDao.getUsers().forEach(u -> response.append(u).append("<br/>"));*/
		logger.info("Testing");
		return "Testing";
	}

	@GetMapping(value="/app")
	public String  testApp(){
		logger.info("NNNNNNNNNNNNNNNNNNNNNN");
		ReplyToEmail replyToEmail=new ReplyToEmail();
		replyToEmail.callEmail();
		logger.info("NNNNNNNNNNNNNNNNNNNNNN");
		return "test";
	}

	@Scheduled(fixedRate = 100)
	public String firstApp(){
		logger.info("NNNNNNNNNNNNNNNNNNNNNN");
		ReplyToEmail replyToEmail=new ReplyToEmail();
		replyToEmail.callEmail();

		logger.info("NNNNNNNNNNNNNNNNNNNNNN");
		return  "Yes";
	}
}

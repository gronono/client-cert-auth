package clientcertauth;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static java.lang.String.format;

@RestController
public class HelloController {
	@RequestMapping(value = "/hello", method = GET)
	public String hello() {
		return hello("anonyme");
	}
	
	@RequestMapping(value = "/hello/secured", method = GET)
	public String securedHello(Principal principal) {
		return hello(principal.getName());
	}
	
	private String hello(String name) {
		return format("Bonjour %s !", name);
	}
}
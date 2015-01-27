package clientcertauth;

import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class SecurityAnonymousTest extends AbstractSecurityTest {

	
	@Override
	public void testSecured() {
		ResponseEntity<String> response = this.restTemplate.getForEntity(this.baseUrl + "/hello/secured", String.class);
		assertEquals(FORBIDDEN, response.getStatusCode());
	}
}

package clientcertauth;

import java.io.FileNotFoundException;

import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.springframework.util.ResourceUtils.getFile;

import static org.springframework.http.HttpStatus.OK;

import static java.lang.System.setProperty;


public class SecurityLoggedTest extends AbstractSecurityTest {

	@Override
	public void before() throws FileNotFoundException {
		super.before();
		
		setProperty("javax.net.ssl.keyStoreType", "pkcs12");
	    setProperty("javax.net.ssl.keyStore", getFile("classpath:client.p12").getAbsolutePath());
	    setProperty("javax.net.ssl.keyStorePassword", "secret-client");
	}
	
	@Override
	public void testSecured() {
		ResponseEntity<String> response = this.restTemplate.getForEntity(this.baseUrl + "/hello/secured", String.class);
		assertEquals(OK, response.getStatusCode());
	    assertEquals("Bonjour Gerard Bouchard !", response.getBody());
	}
}

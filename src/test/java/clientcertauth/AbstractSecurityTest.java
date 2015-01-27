package clientcertauth;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.springframework.util.ResourceUtils.getFile;

import static org.springframework.http.HttpStatus.OK;

import static java.lang.System.setProperty;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ClientCertAuth.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")   
public abstract class AbstractSecurityTest {

	@Value("${local.server.port}")
    private int port;
	
	@Value("${server.ssl.trust-store}")
	private String trustStore;
	
	@Value("${server.ssl.trust-store-password}")
	private String trustStorePassword;
	
	protected RestTemplate restTemplate = new TestRestTemplate();

	protected String baseUrl;
	
	@Before
	public void before() throws FileNotFoundException {
		this.baseUrl = "https://localhost:" + this.port;
		setProperty("javax.net.ssl.trustStore", getFile(this.trustStore).getAbsolutePath());
		setProperty("javax.net.ssl.trustStorePassword", this.trustStorePassword);

	}
	
	@Test
	public void testAnonymous() {
		ResponseEntity<String> response = this.restTemplate.getForEntity(this.baseUrl + "/hello", String.class);
		assertEquals(OK, response.getStatusCode());
		assertEquals("Bonjour anonyme !", response.getBody());
	}
	
	@Test
	public abstract void testSecured();
	
}

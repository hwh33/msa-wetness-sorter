package unittesting;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import utilities.FileServer;

public class CachingTest {
	
	private final String FILE_SERVER_ADDR = "localhost:8000";
	
	private final String FILE_SERVER_ROOT_DIR = "src/test/data";
	
	@Before
	public void initialize() {
		// Start up the file server.
		FileServer fs = new FileServer(FILE_SERVER_ADDR, FILE_SERVER_ROOT_DIR);
		try {
			fs.start();
		} catch (IOException e) {
			fail("IOException caught while starting file server. msg: " + e.getMessage());
		}
	}
	
	@Test
	public void createNewCacheFromURL() {
		// TODO: implement me
	}
	
	@Test
	public void createNewCacheFromAPIRequests() {
		// TODO: implement me
	}
	
	@Test
	public void loadExistingCache() {
		// TODO: implement me
	}

	@Test
	public void loadCacheFileAndRead() {
		// TODO: implement me
	}
	
	@Test
	public void refreshCache() {
		// TODO: implement me
	}

}

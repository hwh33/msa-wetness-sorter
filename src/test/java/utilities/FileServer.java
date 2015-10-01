package utilities;

import java.io.IOException;

public class FileServer {
	
	private final String SERVER_SCRIPT_PATH = "/src/test/go/server.go";
	
	private String address;
	
	private String fileServerRoot;
	
	private Process serverProc;
	
	public FileServer(String address, String fileServerRoot) {
		this.address = address;
		this.fileServerRoot = fileServerRoot;
	}
	
	public void start() throws IOException {
		if (serverProc != null) {
			// TODO: something
		}
		
		String serverStartCommand = 
				"go run " + SERVER_SCRIPT_PATH + " " + address + " " + fileServerRoot;
		serverProc = Runtime.getRuntime().exec(serverStartCommand);
		// TODO: check that the server has started up correctly
	}
	
	public void kill() {
		// If the server process is null then there is nothing to do.
		if (serverProc != null) {
			serverProc.destroy();
			serverProc = null;
		}
	}

}

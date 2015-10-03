package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileServer {
	
	private final String SERVER_SCRIPT_PATH = "src/test/go/server.go";
	
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
		
		// temporary code below this point
//		InputStream serverStdOut = serverProc.getInputStream();
//		InputStream serverStdErr = serverProc.getErrorStream();
//		BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(serverStdOut));
//		BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(serverStdErr));
//		
//		String stdOutLine = stdOutReader.readLine();
//		String stdErrLine = stdErrReader.readLine();
//		while (true) {
//			while (stdOutLine != null && !stdOutLine.trim().equals("--EOF--")) {
//				System.out.println("Stdout:" + stdOutLine);
//				stdOutLine = stdOutReader.readLine();
//			}
//			while (stdErrLine != null && !stdErrLine.trim().equals("--EOF--")) {
//				System.out.println("Stderr:" + stdErrLine);
//				stdErrLine = stdErrReader.readLine();
//			}
//		}
	}
	
	public void kill() {
		// If the server process is null then there is nothing to do.
		if (serverProc != null) {
			serverProc.destroy();
			serverProc = null;
		}
	}

}

package caching;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Function;

public class Cache {
	
	private enum DownloadProtocol {
		FROM_URL, FROM_API_REQUESTS
	}
	
	private static int BUFFER_SIZE = 4096;
	
	private String cacheFilepath;
	
	private String fileURLString;
	
	private String[] apiRequests;
	
	private Function<String, String> processResponse;
	
	private DownloadProtocol protocol;
	
	public Cache(String cacheFilepath, String fileURLString) throws IOException {
		this.cacheFilepath = cacheFilepath;
		this.fileURLString = fileURLString;
		this.protocol = DownloadProtocol.FROM_URL;
		
		// If the cache file does not already exist, download it from the provided URL.
		File cacheFile = new File(cacheFilepath);
		if (!cacheFile.exists()) {
			writeFileFromURL();
		}
	}
	
	public Cache(String cacheFilepath, String[] apiRequests, 
			Function<String, String> processResponse) throws IOException {
		this.cacheFilepath = cacheFilepath;
		this.protocol = DownloadProtocol.FROM_API_REQUESTS;
		if (processResponse != null) {
			this.processResponse = processResponse;
		} else {
			// If the input is null, we just use the identity function.
			this.processResponse = (s) -> s;
		}
		
		// If the cache file does not already exist, create it by gathering responses from the
		// provided API requests.
		File cacheFile = new File(cacheFilepath);
		if (!cacheFile.exists()) {
			writeFileFromAPIRequests();
		}
	}
	
	public InputStream getInputStream() throws Exception {
		FileInputStream fileStream;
		try {
			fileStream = new FileInputStream(cacheFilepath);
		} catch (FileNotFoundException e) {
			// TODO: something
			throw new Exception(); // temporary
		}
		
		return fileStream;
	}
	
	public void refresh() throws IOException {
		switch (protocol) {
		case FROM_URL:
			writeFileFromURL();
			break;
		case FROM_API_REQUESTS:
			writeFileFromAPIRequests();
			break;
		default:
			// TODO: something
			break;
		}
	}
	
	private void writeFileFromURL() throws IOException {
		if (fileURLString.equals("")) {
			// TODO: something
		}
		
		URL fileURL = new URL(fileURLString);
		HttpURLConnection httpConn = (HttpURLConnection) fileURL.openConnection();
		
		int responseCode = httpConn.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException(
					"No file at provided URL. Server response code: " + responseCode);
		}
		
		InputStream httpStream = httpConn.getInputStream();
		FileOutputStream fileStream = new FileOutputStream(cacheFilepath);
		
		int bytesRead = -1;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((bytesRead = httpStream.read(buffer)) != -1) {
			fileStream.write(buffer, 0, bytesRead);
		}
		
		httpStream.close();
		fileStream.close();
		httpConn.disconnect();
	}
	
	private void writeFileFromAPIRequests() throws IOException {
		if (apiRequests == null || processResponse == null) {
			// TODO: something
		}
		
		FileWriter cacheFileWriter = new FileWriter(cacheFilepath);
		
		for (String apiRequest : apiRequests) {
			URL apiRequestURL = new URL(apiRequest);
			HttpURLConnection httpConn = (HttpURLConnection) apiRequestURL.openConnection();
			httpConn.setRequestMethod("GET");
			
			InputStream httpStream = httpConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpStream));
			StringBuilder response = new StringBuilder();
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				response.append(currentLine + '\n');
				currentLine = reader.readLine();
			}
			reader.close();
			
			String processedResponse = processResponse.apply(response.toString());
			cacheFileWriter.write(processedResponse + '\n');
			
			httpStream.close();
			httpConn.disconnect();
		}
		
		cacheFileWriter.close();
	}

}

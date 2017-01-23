package com.eanurag;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GitHubGist {

	public GitHubGist(String endpoint, String username, String access_token) {
		super();
		this.endpoint = endpoint;
		this.username = username;
		this.access_token = access_token;
	}

	String endpoint;
	String username;
	String access_token;

	public String create(String description, String content, String filename) {
		// String endpoint = "https://api.github.com/gists";
		URL url;
		String html_url = null;
		try {
			url = new URL(endpoint);
			String encode = Base64.getEncoder().encodeToString((username + ":" + access_token).getBytes());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Authorization", "Basic " + encode);

			JSONObject parent = new JSONObject();
			parent.put("description", description);
			parent.put("public", "true");

			JSONObject gist = new JSONObject();
			gist.put("content", content);

			JSONObject child = new JSONObject();
			child.put(filename, gist);

			parent.put("files", child);

			connection.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			System.out.println(parent.toJSONString());
			wr.write(parent.toString());
			wr.flush();
			wr.close();

			int rescode = connection.getResponseCode();
			System.out.println("Response from API: " + rescode);
			if (rescode == HttpURLConnection.HTTP_CREATED) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
					html_url = convertToJSONObject(response.toString());
				}
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return html_url;

	}

	private String convertToJSONObject(String response) {
		String html_url = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(response);
			JSONObject jsonObject = (JSONObject) obj;

			html_url = (String) jsonObject.get("html_url");
			System.out.println("Here is your Direct link to the GIST:" + html_url);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return html_url;

	}
}

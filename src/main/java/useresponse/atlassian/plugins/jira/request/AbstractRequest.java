package useresponse.atlassian.plugins.jira.request;


import com.google.gson.Gson;
import useresponse.atlassian.plugins.jira.exception.InvalidResponseException;
import useresponse.atlassian.plugins.jira.exception.UndefinedUrlException;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequest implements Request {

    private static final int REQUEST_TIMEOUT = 15000;
    private static boolean sslConfigured = false;

    protected Map<Object, Object> parameters = new HashMap<Object, Object>();
    protected String requestType;
    protected String url;
    protected String username = null;
    protected String password = null;

    @Override
    public Map<Object, Object> getParameters() {
        return parameters;
    }

    @Override
    public void addParameter(Object name, Object value) {
        parameters.put(name, value);
    }

    @Override
    public void addParameter(Map map) {
        parameters.putAll(map);
    }

    @Override
    public String sendRequest() throws UndefinedUrlException, NoSuchAlgorithmException, KeyManagementException, InvalidResponseException, IOException {
        if (url == null) {
            throw new UndefinedUrlException("URL for sending is undefined!");
        }
        return sendRequest(url);
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String sendRequest(String url) throws IOException, InvalidResponseException, NoSuchAlgorithmException, KeyManagementException {
        byte[] postData = getJsonFromParameters().getBytes(StandardCharsets.UTF_8);
        URL urlObj = new URL(url);
        URLConnection conn = urlObj.openConnection();
        conn.setReadTimeout(REQUEST_TIMEOUT);
        conn.setConnectTimeout(REQUEST_TIMEOUT);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
        conn.setUseCaches(false);

        conn = processLoginData(conn);

        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).setRequestMethod(requestType);
        }

        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setRequestMethod(requestType);
            configureSsl();
        }

        conn.setDoOutput(true);
        conn.setDoInput(true);

        if (!requestType.equals("GET")) {
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
        }

        int responseCode = 0;
        String responseMessage = "Can not perform request";

        if (conn instanceof HttpURLConnection) {
            responseCode = ((HttpURLConnection) conn).getResponseCode();
            responseMessage = ((HttpURLConnection) conn).getResponseMessage();
        }

        if (conn instanceof HttpsURLConnection) {
            responseCode = ((HttpsURLConnection) conn).getResponseCode();
            responseMessage = ((HttpsURLConnection) conn).getResponseMessage();
        }

        if (responseCode == 404) {
            return "";
        }

        if (responseCode == 0 || responseCode >= 300) {
            throw new InvalidResponseException(responseMessage);
        }

        InputStreamReader input = new InputStreamReader(conn.getInputStream());

        BufferedReader reader = new BufferedReader(input);
        StringBuilder stringBuilder = new StringBuilder();
        String inputLine;

        while ((inputLine = reader.readLine()) != null) {
            stringBuilder.append(inputLine);
        }

        reader.close();
        input.close();

        return stringBuilder.toString();
    }

    private void configureSsl() throws NoSuchAlgorithmException, KeyManagementException {
        if (sslConfigured) {
            return;
        }

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        sslConfigured = true;
    }

    @Override
    public void addLoginData(String username, String password) {
        this.password = password;
        this.username = username;
    }

    private URLConnection processLoginData(URLConnection connection) {
        if (!(username == null && password == null)) {
            try {
                String encodedCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
                connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    private String getJsonFromParameters() {
        return new Gson().toJson(parameters);
    }
}

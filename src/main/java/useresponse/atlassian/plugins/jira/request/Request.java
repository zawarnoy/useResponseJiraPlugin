package useresponse.atlassian.plugins.jira.request;

import useresponse.atlassian.plugins.jira.exception.InvalidResponseException;
import useresponse.atlassian.plugins.jira.exception.UndefinedUrlException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface Request {
    /**
     * @return Map
     */
    Map getParameters();

    void addParameter(Object name, Object value);

    void addParameter(Map map);

    void setUrl(String url);

    void addLoginData(String username, String password);

    String sendRequest() throws UndefinedUrlException, NoSuchAlgorithmException, KeyManagementException, InvalidResponseException, IOException;

    String sendRequest(String url) throws IOException, InvalidResponseException, NoSuchAlgorithmException, KeyManagementException;
}

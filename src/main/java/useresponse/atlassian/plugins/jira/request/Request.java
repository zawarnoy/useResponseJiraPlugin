package useresponse.atlassian.plugins.jira.request;

import useresponse.atlassian.plugins.jira.exception.InvalidResponseException;
import useresponse.atlassian.plugins.jira.exception.UndefinedUrl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface Request {
    /**
     * @return Map
     *
     */
    Map getParameters();

    void addParameter(Object name, Object value);

    void addParameter(Map map);

    String sendRequest() throws UndefinedUrl, NoSuchAlgorithmException, KeyManagementException, InvalidResponseException, IOException;

    String sendRequest(String url) throws IOException, InvalidResponseException, NoSuchAlgorithmException, KeyManagementException;
}

package useresponse.atlassian.plugins.jira.request;

public interface Request {
    void addParameter(String name, String value);
    String sendRequest(String url) throws Exception;
}

package useresponse.atlassian.plugins.jira.request;

public interface Request {
    void addParameter(Object name, Object value);
    String sendRequest(String url) throws Exception;
}

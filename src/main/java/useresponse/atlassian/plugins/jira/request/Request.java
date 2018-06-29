package useresponse.atlassian.plugins.jira.request;

public interface Request {

    void addParameter(String name, String value);
    void sendRequest() throws Exception;

}

package useresponse.atlassian.plugins.jira.request;

import java.util.List;
import java.util.Map;

public interface Request {
    void addParameter(Object name, Object value);
    void addParameter(Map map);
    String sendRequest(String url) throws Exception;
}

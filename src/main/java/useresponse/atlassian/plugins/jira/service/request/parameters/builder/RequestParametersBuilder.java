package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.Issue;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;

import java.util.Map;

public abstract class RequestParametersBuilder {

    protected Map<Object, Object> requestMap;
    protected UseResponseObjectManager useResponseObjectManager;

    public void setRequestMap(Map<Object, Object> map) {
        this.requestMap = map;
    }

    public Map<Object, Object> getRequestMap() {
        return requestMap;
    }

    public RequestParametersBuilder addJiraObjectIdToMap(WithId entity) {
        requestMap.put("jira_id", entity.getId().intValue());
        return this;
    }

    public Map<Object, Object> addHtmlTreat(Map<Object, Object> map) {
        map.put("treat_as_html", 1);
        return map;
    }

    public abstract  <T extends WithId> RequestParametersBuilder  addAuthorToRequest(T entity);

    public RequestParametersBuilder addAddAction() {
        requestMap.put("action", "add");
        return this;
    }

    public RequestParametersBuilder addEditAction() {
        requestMap.put("action", "edit");
        return this;
    }
}

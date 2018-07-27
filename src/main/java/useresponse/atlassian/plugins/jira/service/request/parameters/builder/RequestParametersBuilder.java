package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.entity.WithId;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;

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

    public RequestParametersBuilder addObjectIdToMap(WithId entity) {
        requestMap.put("jira_id", entity.getId().intValue());
        return this;
    }

    public RequestParametersBuilder addHtmlTreat() {
        requestMap.put("treat_as_html", 1);
        return this;
    }



}

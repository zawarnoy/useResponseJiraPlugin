package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public abstract class RequestParametersBuilder {

    protected Map<Object, Object> requestMap;

    @Inject
    @Named("useResponseObjectManager")
    protected UseResponseObjectManager useResponseObjectManager;

    public void setRequestMap(Map<Object, Object> map) {
        this.requestMap = map;
    }

    public Map<Object, Object> getRequestMap() {
        return requestMap;
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

    public RequestParametersBuilder addDeleteAction() {
        requestMap.put("action", "delete");
        return this;
    }

    public RequestParametersBuilder addEditAction() {
        requestMap.put("action", "edit");
        return this;
    }

    public RequestParametersBuilder addUpdateLinkAction() {
        requestMap.put("action", "update_link");
        return this;
    }

    public RequestParametersBuilder addNotifyFlag(boolean notify) {
        requestMap.put("notify", notify);
        return this;
    }
}

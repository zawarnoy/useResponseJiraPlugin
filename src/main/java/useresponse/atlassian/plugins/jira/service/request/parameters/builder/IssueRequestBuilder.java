package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.issue.Issue;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IssueRequestBuilder {

    private IssueRequestParametersBuilder builder;
    private UseResponseObjectManager objectManager;

    public IssueRequestBuilder(IssueRequestParametersBuilder builder, UseResponseObjectManager objectManager) {
        this.builder = builder;
        this.objectManager = objectManager;
    }

    public Map<Object, Object> build(Issue issue) throws IOException {
        if (objectManager.findByJiraId(issue.getId().intValue()) == null) {
            return buildNewIssueMap(issue);
        } else {
            return buildUpdateIssueMap(issue);
        }
    }

    private Map<Object, Object> buildNewIssueMap(Issue issue) throws IOException {
        builder.setRequestMap(new HashMap<Object, Object>());
        builder.addStandardParametersToMap(issue).addOwnershipToMap().addObjectTypeToMap().addObjectIdToMap(issue);
        return builder.getRequestMap();
    }

    private Map<Object, Object> buildUpdateIssueMap(Issue issue) throws IOException {
        builder.setRequestMap(new HashMap<Object, Object>());
        builder.addStandardParametersToMap(issue).addOwnershipToMap().addObjectTypeToMap().addObjectIdToMap(issue);
        return builder.getRequestMap();
    }
}

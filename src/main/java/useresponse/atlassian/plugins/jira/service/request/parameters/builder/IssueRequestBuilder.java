package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.issue.Issue;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IssueRequestBuilder {

    private IssueRequestParametersBuilder builder;
    private UseResponseObjectManager objectManager;

    /**
     * @param builder
     * @param objectManager Builds request map for issue
     */
    public IssueRequestBuilder(IssueRequestParametersBuilder builder, UseResponseObjectManager objectManager) {
        this.builder = builder;
        this.objectManager = objectManager;
    }

    public Map<Object, Object> build(Issue issue) throws IOException {
        if (objectManager.findByJiraId(issue.getId().intValue()) == null) {
            return buildNewIssueRequestMap(issue);
        } else {
            return buildUpdateIssueRequestMap(issue);
        }
    }

    private Map<Object, Object> buildNewIssueRequestMap(Issue issue) throws IOException {
        builder.setRequestMap(new HashMap<Object, Object>());
        builder.
                addStandardParametersToMap(issue).
                addOwnershipToMap().
                addObjectTypeToMap().
                addStatusToMap(issue).
                addCreatedAt(issue).
                addAuthorToRequest(issue).
                addAddAction();
        return builder.getRequestMap();
    }

    private Map<Object, Object> buildUpdateIssueRequestMap(Issue issue) throws IOException {
        builder.setRequestMap(new HashMap<Object, Object>());
        builder.
                addStandardParametersToMap(issue).
                addUseResponseObjectId(issue).
                addStatusToMap(issue).
                addEditAction();
        return builder.getRequestMap();
    }
}

package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IssueRequestBuilder {

    @Autowired
    private IssueRequestParametersBuilder builder;

    @Inject
    @Named("useResponseObjectManager")
    private UseResponseObjectManager objectManager;

    public Map<Object, Object> build(Issue issue) throws IOException {
        return build(issue, true);
    }

    public Map<Object, Object> build(Issue issue, boolean notify) throws IOException {
        if (objectManager.findByJiraId(issue.getId().intValue()) == null) {
            return buildNewIssueRequestMap(issue, notify);
        } else {
            return buildUpdateIssueRequestMap(issue, notify);
        }
    }

    /**
     * Builds request map for update link event
     *
     * @param issue
     * @param issueKeys
     * @return
     * @throws IOException
     */
    public Map<Object, Object> build(Issue issue, Set<String> issueKeys) throws IOException {
        builder.setRequestMap(new HashMap<Object, Object>());
        builder.addNewOldIssueKeysToMap(issueKeys).
                addUpdateLinkAction();

        return builder.getRequestMap();
    }

    private Map<Object, Object> buildNewIssueRequestMap(Issue issue, boolean notify) throws IOException {
        builder.setRequestMap(new HashMap<Object, Object>());
        builder.addStandardParametersToMap(issue).
                addOwnershipToMap().
                addObjectTypeToMap().
                addStatusToMap(issue).
                addCreatedAt(issue).
                addAuthorToRequest(issue).
                addAddAction().
                addNotifyFlag(notify);
        return builder.getRequestMap();
    }

    private Map<Object, Object> buildUpdateIssueRequestMap(Issue issue, boolean notify) throws IOException {
        builder.setRequestMap(new HashMap<>());
        builder.addStandardParametersToMap(issue).
                addUseResponseObjectId(issue).
                addStatusToMap(issue).
                addEditAction().
                addNotifyFlag(notify);
        return builder.getRequestMap();
    }
}

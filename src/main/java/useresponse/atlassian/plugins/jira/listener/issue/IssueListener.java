package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.listener.issue.action.*;



@Component
public class IssueListener implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(IssueListener.class);

    @JiraImport
    private final EventPublisher publisher;

    @Autowired
    public IssueListener(EventPublisher publisher) {
        this.publisher = publisher;
    }


    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();
        Action action = null;

        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            log.info("Issue {} has been created at {}.", issue.getKey(), issue.getCreated());
            action = new CreateIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
            action = new UpdateIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            action = new CommentCreateAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            action = new UpdateCommentAction();
        } else if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
            action = new DeleteIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            action = new DeleteCommentAction();
        }

        if (action != null) {
            try {
                action.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

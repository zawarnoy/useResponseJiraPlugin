package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import useresponse.atlassian.plugins.jira.action.issue.*;


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
        publisher.register(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        publisher.unregister(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        Long eventTypeId = issueEvent.getEventTypeId();

        Action action = selectAction(eventTypeId);

        if (action != null) {
            try {
                action.execute(issueEvent.getIssue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Action selectAction(Long eventTypeId) {
        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            return new CreateIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
            return new UpdateIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            return new CommentCreateAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            return new UpdateCommentAction();
        } else if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
            return new DeleteIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            return new DeleteCommentAction();
        } else {
            return null;
        }
    }
}

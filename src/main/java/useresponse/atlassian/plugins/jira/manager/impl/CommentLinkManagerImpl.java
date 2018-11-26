package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import net.java.ao.Query;
import com.atlassian.activeobjects.external.ActiveObjects;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Scanned
@Named
public class CommentLinkManagerImpl implements CommentLinkManager {

    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public CommentLinkManagerImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public CommentLink add(int useResponseCommentId, int jiraCommentId, int issueId) {
        final CommentLink link = ao.create(CommentLink.class);
        link.setJiraCommentId(jiraCommentId);
        link.setUseResponseCommentId(useResponseCommentId);
        link.setIssueId(issueId);
        link.save();
        return link;
    }

    @Override
    public CommentLink findByUseResponseId(int useResponseCommentId) {
        CommentLink[] objects = ao.find(CommentLink.class, Query.select().where("use_response_comment_id = ?", String.valueOf(useResponseCommentId)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public CommentLink findByJiraId(int jiraCommentId) {
        CommentLink[] objects = ao.find(CommentLink.class, Query.select().where("jira_comment_id = ?", String.valueOf(jiraCommentId)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public CommentLink findOrAdd(int useResponseCommentId, int jiraCommentId, int issueId) {
        CommentLink object = findByJiraId(jiraCommentId);
        if (object != null) {
            return object;
        } else {
            return add(useResponseCommentId, jiraCommentId, issueId);
        }
    }

    @Override
    public void delete(CommentLink commentLink) {
        ao.delete(commentLink);
    }

    @Override
    public List<CommentLink> all() {
        return Arrays.asList(ao.find(CommentLink.class));
    }

    @Override
    public List<CommentLink> findByIssueId(int issueId) {
        return Arrays.asList(ao.find(CommentLink.class, Query.select().where("issue_id = ?", String.valueOf(issueId))));
    }

    @Override
    public void deleteByUseResponseId(int useResponseId) {
        CommentLink item = findByUseResponseId(useResponseId);
        if(item != null) {
            delete(item);
        }
    }
}

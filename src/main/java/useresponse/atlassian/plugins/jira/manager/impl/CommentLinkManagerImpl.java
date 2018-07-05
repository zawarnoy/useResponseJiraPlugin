package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import net.java.ao.Query;
import com.atlassian.activeobjects.external.ActiveObjects;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import javax.inject.Inject;
import javax.inject.Named;
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
    public CommentLink add(int useResponseCommentId, int jiraCommentId) {
        final CommentLink link = ao.create(CommentLink.class);
        link.setJiraCommentId(jiraCommentId);
        link.setUseResponseCommentId(useResponseCommentId);
        link.save();
        return link;
    }

    @Override
    public CommentLink findByUseResponseId(int useResponseCommentId) {
        return null;
    }

    @Override
    public CommentLink findByJiraId(int jiraCommentId) {
        CommentLink[] objects = ao.find(CommentLink.class, Query.select().where("jira_comment_Id = ?", String.valueOf(jiraCommentId)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public CommentLink findOrAdd(int useResponseCommentId, int jiraCommentId) {
        CommentLink object = findByJiraId(jiraCommentId);
        if (object != null) {
            return object;
        } else {
            return add(useResponseCommentId, jiraCommentId);
        }
    }

    @Override
    public List<CommentLink> all() {
            return newArrayList(ao.find(CommentLink.class));
    }
}

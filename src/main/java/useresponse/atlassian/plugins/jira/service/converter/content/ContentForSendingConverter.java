package useresponse.atlassian.plugins.jira.service.converter.content;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;

public class ContentForSendingConverter {

    public static String convert(Issue issue) {
        IssueRenderContext renderContext = new IssueRenderContext(issue);
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType("atlassian-wiki-renderer");
        return renderer.render(issue.getDescription(), renderContext);
    }

    public static String convert(Comment comment) {
        IssueRenderContext renderContext = new IssueRenderContext(comment.getIssue());
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType("atlassian-wiki-renderer");
        return renderer.render(comment.getBody(), renderContext);
    }
}

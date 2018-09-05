package useresponse.atlassian.plugins.jira.service.converter.content;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.service.IconsService;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentConverter {

    static Logger logger = LoggerFactory.getLogger(ContentConverter.class);

    public static String convert(Issue issue) {
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType("atlassian-wiki-renderer");
        String content = renderer.render(issue.getDescription(), issue.getIssueRenderContext());
        return Storage.isFromBinder ? handleContent(content) : content;
    }

    public static String convert(Comment comment) {
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType("atlassian-wiki-renderer");
        String content = renderer.render(comment.getBody(), comment.getIssue().getIssueRenderContext());
        logger.error(content);
        return Storage.isFromBinder ? handleContent(content) : content;
    }

    private static String handleContent(String content) {
        Pattern pattern = Pattern.compile("src=\"/.*?\\.(png|jpg|gif|jpeg|img)");
        Matcher matcher = pattern.matcher(content);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group();
            matcher.appendReplacement(buffer, handleLink(match));
        }
        buffer = matcher.appendTail(buffer);

        return buffer.toString();
    }

    private static String handleLink(String link) {
        String domain = getDomain(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
        return domain == null ? link : "src=\"" + domain + link.substring(5);
    }

    private static String getDomain(String url) {
        Pattern pattern = Pattern.compile("^(?:https?://)?(?:[^@/ ]+@)?(?:www\\.)?([^:/? ]+)(:\\d+)?");
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? matcher.group() : null;
    }

    public static String convertForJira(String content, Issue issue) {
        content = convertIcons(content);
        content = convertFilesInContent(content, issue);
        return content;
    }

    private static String convertFilesInContent(String content, Issue issue) {
        AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
        List<Attachment> attachments = attachmentManager.getAttachments(issue);
        String result = content;

        for (Attachment attachment : attachments) {
            int id = attachment.getId().intValue();

            Pattern pattern = Pattern.compile("\\[[^\\[\\]]*?" + id +"[^\\[\\]]*?\\]");
            Matcher matcher = pattern.matcher(result);
            StringBuffer buffer = new StringBuffer();

            while(matcher.find()) {
                matcher.appendReplacement(buffer, "!" + attachment.getFilename() + "!");
            }

            buffer = matcher.appendTail(buffer);
            result = buffer.toString();
        }
        return result;
    }

    public static String convertIcons(String content) {
        Map<String, String> iconsMap = IconsService.getIconsMap();
        String result = content;

        for (Map.Entry<String, String> entry : iconsMap.entrySet()) {

            Pattern pattern = Pattern.compile("\\[" + entry.getKey() + "\\]");
            Matcher matcher = pattern.matcher(result);
            StringBuffer buffer = new StringBuffer();
            while(matcher.find()) {
                matcher.appendReplacement(buffer, entry.getValue());
            }

            buffer = matcher.appendTail(buffer);
            result = buffer.toString();
        }
        return result;
    }
}

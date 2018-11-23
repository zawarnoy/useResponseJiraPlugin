package useresponse.atlassian.plugins.jira.service.converter.content;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import com.atlassian.jira.issue.fields.renderer.wiki.AtlassianWikiRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.service.IconsService;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentConverter {

    static Logger logger = LoggerFactory.getLogger(ContentConverter.class);

    public static String convert(Issue issue) {
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType(AtlassianWikiRenderer.RENDERER_TYPE);
        String content = renderer.render(issue.getDescription(), issue.getIssueRenderContext());
        return Storage.isFromBinder ? handleContent(content) : content;
    }

    public static String convert(Comment comment) {
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType(AtlassianWikiRenderer.RENDERER_TYPE);
        String content = renderer.render(comment.getBody(), comment.getIssue().getIssueRenderContext());
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

    public static String convertForJira(String content, MutableIssue issue) {
        content = convertIcons(content);
        issue.setDescription(content);
        content = convertImages(issue);
        logger.error(content);
        return content;
    }

    private static String convertFilesInContent(String content, Issue issue) {
        AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
        List<Attachment> attachments = attachmentManager.getAttachments(issue);
        String result = content;

        for (Attachment attachment : attachments) {
            int id = attachment.getId().intValue();

            Pattern pattern = Pattern.compile("\\[[^\\[\\]]*?" + id + "[^\\[\\]]*?\\]");
            Matcher matcher = pattern.matcher(result);
            StringBuffer buffer = new StringBuffer();

            while (matcher.find()) {
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
            while (matcher.find()) {
                matcher.appendReplacement(buffer, entry.getValue());
            }

            buffer = matcher.appendTail(buffer);
            result = buffer.toString();
        }
        return result;
    }

    /**
     * For issues
     *
     * @param issue
     * @return
     */
    public static String convertImages(Issue issue) {
        String content = convertImages(issue, issue.getDescription());
        return content;
    }

    /**
     * For comments
     *
     * @param issue
     * @param content
     * @return
     */
    public static String convertImages(Issue issue, String content) {
        Collection<Attachment> attachments = issue.getAttachments();
        String regEx;
        for (Attachment attachment : attachments) {
            regEx = "\\[(\\d{1,10}_)?" + removeExtension(attachment.getFilename()) + "]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(content);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buffer, "!" + attachment.getFilename() + "!");
            }
            buffer = matcher.appendTail(buffer);
            content = buffer.toString();
        }
        return content;
    }

    public static String convertImages(String content, Collection<String> attachmentNames, long id) {
        String regEx;
        for (String filename : attachmentNames) {
            regEx = "\\[(\\d{1,10}_)?" + removeExtension(filename) + "]";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                content = matcher.replaceAll("!" + filename + "!");
            }
        }
        return content;
    }

    public static String removeExtension(String name) {
        if (name.startsWith(".")) {
            if (name.lastIndexOf('.') == name.indexOf('.')) return name;
        }
        if (!name.contains("."))
            return name;
        return name.substring(0, name.lastIndexOf('.'));
    }

    public static String removeExtension(Attachment attachment) {
        return removeExtension(attachment.getFilename());
    }
}

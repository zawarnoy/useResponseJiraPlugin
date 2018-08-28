package useresponse.atlassian.plugins.jira.service.converter.content;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentForSendingConverter {

    static Logger log = LoggerFactory.getLogger(ContentForSendingConverter.class);

    public static String convert(Issue issue) {
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType("atlassian-wiki-renderer");
        String content = renderer.render(issue.getDescription(), issue.getIssueRenderContext());
        return Storage.isFromBinder ? handleContent(content) : content;
    }

    public static String convert(Comment comment) {
        JiraRendererPlugin renderer = ComponentAccessor.getRendererManager().getRendererForType("atlassian-wiki-renderer");
        return renderer.render(comment.getBody(), comment.getIssue().getIssueRenderContext());
    }


    private static String handleContent(String content) {
        Pattern pattern = Pattern.compile("(/.*?)?/images/icons/emoticons/.*?\\.png");
        Matcher matcher = pattern.matcher(content);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String match = matcher.group();
            matcher.appendReplacement(buffer, handleOneLink(match));
            log.error(matcher.toString());
        }
        buffer = matcher.appendTail(buffer);

        return buffer.toString();
    }

    private static String handleOneLink(String link) {
        Pattern pattern = Pattern.compile("[a-z_-]+\\.png");
        Matcher matcher = pattern.matcher(link);

        matcher.find();
        String pngName = matcher.group(0);
        String baseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
        return  baseUrl + "/images/icons/emoticons/" + pngName;
    }

    private static String makeImgTag(String link) {
        String img = "<img class=\"emoticon\" src=\"" + link + "\" height=\"16\" width=\"16\" align=\"absmiddle\" alt=\"\" border=\"0\"/>";
        return img;
    }

}

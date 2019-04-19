package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultPriorityManager;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.*;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.IssueFileLink;
import useresponse.atlassian.plugins.jira.model.URPriority;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.PrioritiesService;
import useresponse.atlassian.plugins.jira.service.StatusesService;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;


public class UseResponseJiraStatusesLinkServlet extends HttpServlet {

    private final UserManager userManager;
    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;

    @Autowired
    private StatusesService statusesService;

    @Autowired
    private URPriorityManagerImpl urPriorityManager;

    @Autowired
    private IssueFileLinkManagerImpl fileLinkManager;

    @Autowired
    PrioritiesService prioritiesService;

    @Autowired
    PluginSettingsImpl pluginSettings;

    @Inject
    public UseResponseJiraStatusesLinkServlet(@ComponentImport UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();

        try {
            writer.write("<h1>Comments</h1>");
            for (CommentLink object : commentLinkManager.all()) {
                writer.print("ur id: " + object.getUseResponseCommentId() + " jira id:" + object.getJiraCommentId() + " issue id:" + object.getIssueId() + "<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            writer.write("<h1>Items</h1>");
            for (UseResponseObject object : useResponseObjectManager.all()) {
                writer.print(
                        "ur id: " + object.getUseResponseId() +
                                " jira id:" + object.getJiraId() +
                                " object type: " + object.getObjectType() +
                                " sync: " + String.valueOf(object.getNeedOfSync()) + "<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        writer.write("<h1>Statuses</h1>");
        DefaultStatusManager statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);
        Collection<Status> statuses = statusManager.getStatuses();

        Iterator<Status> iterator = statuses.iterator();
        while (iterator.hasNext()) {
            Status status = iterator.next();
            writer.write(status.getName() + "<br>");
        }

        Map<String, String> statusSlug = statusesService.getStatusSlugLinks();

        writer.write("<h1>Statuses Links</h1>");

        for (Map.Entry<String, String> link : statusSlug.entrySet()) {
            writer.print("JIRA: " + link.getKey() + "  UR: " + link.getValue() + "<br>");
        }

        DefaultPriorityManager priorityManager = ComponentAccessor.getComponent(DefaultPriorityManager.class);


        writer.write("<h1>Priority links </h1>");

        for (Map.Entry<String, String> priorityLink : prioritiesService.getPrioritySlugLinks().entrySet()) {
            writer.write(
                    "JIRA: " + priorityLink.getKey() +
                            " UR: " + priorityLink.getValue() + "<br>");
        }


        writer.write("<h1>JIra priorities </h1>");


        for (String priority : prioritiesService.getPrioritiesNames()) {
            writer.write(priority + "<br>");
        }


        writer.write("<h1>UR priorities </h1>");

        for (URPriority urPriority : urPriorityManager.all())
            writer.write(urPriority.getUseResponsePrioritySlug() + "|" + urPriority.getUseResponsePriorityValue() + "<br>");


        writer.write("<h1>Files links </h1>");

        for (IssueFileLink urPriority : fileLinkManager.all())
            writer.write(urPriority.getJiraIssueId() + "|" + urPriority.getSentFilename() + "<br>");

        writer.write("<br>");

        writer.write("SYNC");

        writer.write("Sync basic fields: " + pluginSettings.getSyncBasicFields() + "<br>");

        writer.write("Sync comments: " + pluginSettings.getSyncComments() + "<br>");

        writer.write("Sync statuses: " + pluginSettings.getSyncStatuses() + "<br>");

        writer.write("Sync tickets data: " + pluginSettings.getSyncTicketsData() + "<br>");

        for (Double value : pluginSettings.getAvailableProjectsIds()) {
            writer.write("Project Id: " + value + "<br>");
        }

        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (userManager.getRemoteUser() == null) {
            return;
        }

        Map map = request.getParameterMap();

        if (map.get("use_response_id") == null) {
            return;
        }

        Request sentRequest = new PostRequest();

        sentRequest.addParameter(map);

//        sentRequest.sendRequest();

        map.remove("use_response_id");

        response.getWriter().write("checked");
    }


}
package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultPriorityManager;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.plugin.JiraPluginManager;
import com.atlassian.jira.project.template.module.Icon;
import com.atlassian.jira.util.PathUtils;
import com.atlassian.jira.web.action.issue.util.BackwardCompatibleTemporaryAttachmentUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import com.atlassian.activeobjects.external.ActiveObjects;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.IssueFileLink;
import useresponse.atlassian.plugins.jira.model.URPriority;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.IconsService;
import useresponse.atlassian.plugins.jira.service.PrioritiesService;
import useresponse.atlassian.plugins.jira.service.StatusesService;


public class UseResponseJiraStatusesLinkServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(IssueBinderServlet.class);

    private final Gson gson = new Gson();


    private final ActiveObjects ao;
    private final UserManager userManager;
    private final IssueManager issueManager;

    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;

    @Autowired
    private StatusesLinkManagerImpl linkManager;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManger;

    @Autowired
    private URPriorityManagerImpl urPriorityManager;

    @Autowired
    private IssueFileLinkManagerImpl fileLinkManager;

    @Inject
    public UseResponseJiraStatusesLinkServlet(@ComponentImport ActiveObjects ao,
                                              @ComponentImport UserManager userManager,
                                              @ComponentImport IssueManager issueManager) {
        this.ao = ao;
        this.userManager = userManager;
        this.issueManager = issueManager;
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
            writer.write(status.getSimpleStatus().getName() + "<br>");
        }


        StatusesService statusesService = new StatusesService(ComponentAccessor.getComponent(DefaultStatusManager.class), linkManager);

        Map<String, String> statusSlug = statusesService.getStatusSlugLinks();

        writer.write("<h1>Statuses Links</h1>");

        for (Map.Entry<String, String> link : statusSlug.entrySet()) {
            writer.print("JIRA: " + link.getKey() + "  UR: " + link.getValue() + "<br>");
        }

        DefaultPriorityManager priorityManager = ComponentAccessor.getComponent(DefaultPriorityManager.class);
        PrioritiesService prioritiesService = new PrioritiesService(priorityManager, priorityLinkManger, urPriorityManager);


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

//        writer.write("<h1>Path</h1>: ");

//
//        writer.write(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_PATH_ATTACHMENTS) + "<br><br>");
//        writer.write(ComponentAccessor.getComponentOfType(JiraHome.class).getHomePath() + "<br><br>");
//        writer.write(ComponentAccessor.getAttachmentPathManager().getAttachmentPath() + "<br><br>");
//
//        MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-12");

//        writer.write(issue.getProjectObject().getKey() + "<br>");
//        writer.write(issue.getId().toString());

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
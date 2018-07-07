package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.issue.status.Status;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import useresponse.atlassian.plugins.jira.model.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import com.atlassian.activeobjects.external.ActiveObjects;

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

import com.atlassian.jira.issue.priority.Priority;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.config.DefaultPriorityManager;
import useresponse.atlassian.plugins.jira.service.PrioritiesService;
import useresponse.atlassian.plugins.jira.service.StatusesService;


public class UseResponseJiraStatusesLinkServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(IssueBinderServlet.class);

    private final Gson gson = new Gson();

    @ComponentImport
    private final ActiveObjects ao;

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

    @Inject
    public UseResponseJiraStatusesLinkServlet(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();

        try {
            writer.write("<h1>Comments</h1>");
            for (CommentLink object : commentLinkManager.all()) {
                writer.print("ur id: " + object.getUseResponseCommentId() + " jira id:" + object.getJiraCommentId() + "<br>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            writer.write("<h1>Items</h1>");
            for (UseResponseObject object : useResponseObjectManager.all()) {
                writer.print("ur id: " + object.getUseResponseId() + " jira id:" + object.getJiraId() + "<br>");
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

        for(URPriority urPriority : urPriorityManager.all())
            writer.write(urPriority.getUseResponsePrioritySlug() + "|" +urPriority.getUseResponsePriorityValue() + "<br>");

        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        UseResponseObject object = useResponseObjectManager.findByUseResponseId(11);
        object.setJiraId(111);
        object.save();

        try {
            useResponseObjectManager.add(Integer.valueOf(request.getParameter("useResponseId")), Integer.valueOf(request.getParameter("jiraId")));
            writer.write("{ \"status\" : \"success\" }");
        } catch (Exception ignored) {
            writer.write("{ \"status\" : \"error\" }");
        }
    }






}
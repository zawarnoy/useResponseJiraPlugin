package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.sal.api.user.UserManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.service.request.ServletService;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IssueUnlinkServlet extends HttpServlet {

    @Autowired
    private UseResponseObjectManagerImpl objectManager;

    private final UserManager userManager;
    private final CommentLinkManager commentLinkManager;

    @Autowired
    private SettingsService settingsService;

    @Inject
    public IssueUnlinkServlet(UserManager userManager, CommentLinkManager commentLinkManager) {
        this.userManager = userManager;
        this.commentLinkManager = commentLinkManager;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!settingsService.checkIsAdmin(userManager.getRemoteUserKey())) {
            settingsService.redirectToLogin(req, resp);
            return;
        }

        String json = ServletService.getJsonFromRequest(req);

        Map<Object, Object> requestParams = (new Gson()).fromJson(json, Map.class);
        Map<String, String> responseMap = new HashMap<>();

        try {
            int useresponse_object_id = Integer.valueOf(String.valueOf(requestParams.get("useresponse_object_id")));

            UseResponseObject object = objectManager.findByUseResponseId(useresponse_object_id);

            if (object.getNeedOfSync()) {
                deleteCommentsRelations(object);
            }

            objectManager.delete(objectManager.findByUseResponseId(useresponse_object_id));

            responseMap.put("status", "success");
        } catch (Exception e) {
            responseMap.put("status", "error");
            responseMap.put("message", e.getMessage());
        }

        resp.getWriter().write((new Gson()).toJson(responseMap));
    }

    private void deleteCommentsRelations(UseResponseObject object) throws Exception {
            Issue issue = ComponentAccessor.getIssueManager().getIssueObject(Long.valueOf(object.getJiraId()));
            for (CommentLink link : commentLinkManager.findByIssueId(object.getJiraId())) {
                commentLinkManager.delete(link);
            }
    }

}
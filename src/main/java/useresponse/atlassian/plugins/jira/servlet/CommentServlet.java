package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import useresponse.atlassian.plugins.jira.service.handler.servlet.comment.CommentServletRequestHandler;
import useresponse.atlassian.plugins.jira.service.request.ServletService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommentServlet extends HttpServlet {

    @Inject
    @Named("commentServletRequestHandler")
    private CommentServletRequestHandler handler;

    private final UserManager userManager;

    public CommentServlet(@ComponentImport UserManager userManager) {
        this.userManager = userManager;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ApplicationUser loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        if (loggedUser == null && userManager.isSystemAdmin(userManager.getRemoteUserKey())) {
            return;
        }

        String jsonData = ServletService.getJsonFromRequest(req);

        String response;

        try {
            response = handler.handle(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> respMap = new HashMap<>();
            respMap.put("status", "error");
            respMap.put("message", e.getMessage());
            response = (new Gson()).toJson(respMap);
        }
        resp.getWriter().write(response);
    }
}
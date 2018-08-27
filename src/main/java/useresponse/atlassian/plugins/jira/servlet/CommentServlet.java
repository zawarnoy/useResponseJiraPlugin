package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.CommentLinkManagerImpl;
import useresponse.atlassian.plugins.jira.service.handler.Handler;
import useresponse.atlassian.plugins.jira.service.handler.servlet.comment.RequestHandler;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CommentServlet extends HttpServlet {

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;

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

        String jsonData = this.getJsonFromRequest(req);

        Handler<String, String> handler = new RequestHandler(loggedUser, commentLinkManager);

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

    private String getJsonFromRequest(HttpServletRequest request) throws IOException {
        InputStreamReader reader = new InputStreamReader(request.getInputStream());

        BufferedReader br = new BufferedReader(reader);

        String bufer;
        StringBuilder data = new StringBuilder();

        while ((bufer = br.readLine()) != null) {
            data.append(bufer);
        }

        return data.toString();
    }


}
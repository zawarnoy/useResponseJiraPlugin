package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.exception.NotLoggedException;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.service.handler.Handler;
import useresponse.atlassian.plugins.jira.service.handler.servlet.attachments.AttachmentsRequestHandler;
import useresponse.atlassian.plugins.jira.service.request.ServletService;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to receiving attachments
 */
public class AttachmentsServlet extends HttpServlet {

    @Autowired
    AttachmentsRequestHandler handler;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String answer;

        try {

            ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

            if (user == null) {
                throw new NotLoggedException("");
            }

            String json = ServletService.getJsonFromRequest(req);
            answer = handler.handle(json);

        } catch (Exception e) {
            answer = e.getMessage();
        }
        resp.getWriter().write(answer);
    }

}
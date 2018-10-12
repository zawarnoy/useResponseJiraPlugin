package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.util.AttachmentException;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.handler.Handler;
import useresponse.atlassian.plugins.jira.service.handler.servlet.attachments.RequestHandler;
import useresponse.atlassian.plugins.jira.service.request.ServletService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AttachmentsServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(AttachmentsServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("here!");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

        if(user == null) {
            return;
        }

        String json = ServletService.getJsonFromRequest(req);

        Handler<String, String> handler = new RequestHandler();

        String answer = null;
        try {
            answer = handler.handle(json);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        resp.getWriter().write(answer);
    }

}
package useresponse.atlassian.plugins.jira.servlet;



import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.attachment.CreateAttachmentParamsBean;
import com.atlassian.jira.web.util.AttachmentException;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IssueServlet extends HttpServlet {

    public IssueServlet() {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager();
        CreateAttachmentParamsBean ab = new CreateAttachmentParamsBean.Builder().build();
        try {
            attachmentManager.createAttachment(ab);
        } catch (AttachmentException e) {
            e.printStackTrace();
        }
    }
}
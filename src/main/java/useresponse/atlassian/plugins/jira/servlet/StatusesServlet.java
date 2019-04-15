package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.bc.config.ConstantsService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.DefaultStatusManager;
import com.atlassian.jira.issue.fields.rest.json.beans.StatusJsonBean;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.service.StatusesService;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class StatusesServlet extends HttpServlet {

    @Autowired
    StatusesService statusesService;

    JiraAuthenticationContext authContext;

    private DefaultStatusManager statusManager;

    ConstantsService constantsService;

    Gson gson = new Gson();

    public StatusesServlet() {
        this.authContext = ComponentAccessor.getJiraAuthenticationContext();
        this.constantsService = ComponentAccessor.getComponent(ConstantsService.class);
        this.statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServiceOutcome<Collection<Status>> outcome = constantsService.getAllStatuses(authContext.getLoggedInUser());
        if (!outcome.isValid()) {

        }

        final List<StatusJsonBean> beans = new ArrayList<StatusJsonBean>();
        for (Status status : outcome.getReturnedValue()) {
            resp.getWriter().write(status.getNameTranslation());
        }
    }

}
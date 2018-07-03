package useresponse.atlassian.plugins.jira.servlet;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.Gson;
import javafx.util.Pair;
import org.omg.CORBA.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;

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

@Scanned
public class IssueBinderServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(IssueBinderServlet.class);

    private final Gson gson = new Gson();

    @ComponentImport
    private final ActiveObjects ao;

    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;

    @Inject
    public IssueBinderServlet(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        for (UseResponseObject object : useResponseObjectManager.all()) {
            writer.print(object.getUseResponseId() + " "  + object.getJiraId() + "<br>");
        }
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        UseResponseObject object = useResponseObjectManager.findByUseResponseId(11);
        object.setJiraId(111);
        object.save();

        try {
            useResponseObjectManager.add(Integer.valueOf(request.getParameter("useResponseId")),Integer.valueOf(request.getParameter("jiraId")));
            writer.write("{ \"status\" : \"success\" }");
        } catch (Exception ignored){
            writer.write("{ \"status\" : \"error\" }");
        }
    }

}
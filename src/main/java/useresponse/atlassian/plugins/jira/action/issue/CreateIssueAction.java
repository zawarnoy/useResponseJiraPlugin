package useresponse.atlassian.plugins.jira.action.issue;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.PostRequest;

import javax.inject.Inject;

public class CreateIssueAction extends AbstractAction {
    @Override
    public void execute(Issue issue) throws Exception {
    }
}

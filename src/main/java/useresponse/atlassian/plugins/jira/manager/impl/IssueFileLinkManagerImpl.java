package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.model.IssueFileLink;
import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Query;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Scanned
@Named
public class IssueFileLinkManagerImpl implements IssueFileLinkManager {

    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public IssueFileLinkManagerImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }



    @Override
    public List<IssueFileLink> findByJiraIssueId(int issueId) {
        IssueFileLink[] links = ao.find(IssueFileLink.class, Query.select().where("jira_issue_id = ?", String.valueOf(issueId)));
        return Arrays.asList(links);
    }

    @Override
    public IssueFileLink add(int issueId, String filename) {
        IssueFileLink link = ao.create(IssueFileLink.class);
        link.setJiraIssueId(issueId);
        link.setSentFilename(filename);
        link.save();
        return link;
    }

    @Override
    public List<IssueFileLink> all() {
        return Arrays.asList(ao.find(IssueFileLink.class));
    }

    @Override
    public IssueFileLink find(int issueId, String filename) {
        IssueFileLink[] links = ao.find(IssueFileLink.class, Query.select().where("jira_issue_id = ? AND sent_filename = ?", String.valueOf(issueId), filename));
        return links.length > 0 ? links[0] : null;
    }
}

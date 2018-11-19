package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.model.StatusesLink;
import com.atlassian.activeobjects.external.ActiveObjects;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

@Scanned
@Named
public class StatusesLinkManagerImpl implements StatusesLinkManager {
    Logger logger = LoggerFactory.getLogger(StatusesLinkManagerImpl.class);
    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public StatusesLinkManagerImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public StatusesLink findByJiraStatusName(String jiraStatusName) {
        StatusesLink[] objects = ao.find(StatusesLink.class, Query.select().where("jira_status_name = ?", jiraStatusName));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public StatusesLink findOrAdd(String jiraStatusName, String useResponseStatusSlug) {
        StatusesLink link = findByJiraStatusName(jiraStatusName);
        if(link == null) {
            return add(jiraStatusName, useResponseStatusSlug == null ? "" : useResponseStatusSlug);
        } else {
            return link;
        }
    }

    @Override
    public StatusesLink editUseResponseSlug(String jiraStatusName, String useResponseStatusSlug) {
        StatusesLink link = findByJiraStatusName(jiraStatusName);
        if(link == null) {
            return null;
        } else {
            link.setUseResponseStatusSlug(useResponseStatusSlug);
            link.save();
        }
        return link;
    }

    public StatusesLink editOrAdd(String jiraStatusName, String useResponseStatusSlug) {
        StatusesLink link = editUseResponseSlug(jiraStatusName, useResponseStatusSlug);
        if( link == null) {
            link = add(jiraStatusName, useResponseStatusSlug);
        }
        return link;
    }

    @Override
    public List<StatusesLink> all() {
        return Arrays.asList(ao.find(StatusesLink.class));
    }

    private StatusesLink add(String jiraStatusName, String useResponseStatusSlug) {
        final StatusesLink statusLink = ao.create(StatusesLink.class);
        statusLink.setJiraStatusName(jiraStatusName);
        statusLink.setUseResponseStatusSlug(useResponseStatusSlug);
        statusLink.save();
        return statusLink;
    }
}

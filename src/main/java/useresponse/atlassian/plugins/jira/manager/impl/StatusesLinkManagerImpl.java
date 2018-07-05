package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.model.StatusesLink;
import com.atlassian.activeobjects.external.ActiveObjects;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

@Scanned
@Named
public class StatusesLinkManagerImpl implements StatusesLinkManager {

    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public StatusesLinkManagerImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public StatusesLink findByJiraStatusName(String jiraStatusName) {
        StatusesLink[] objects = ao.find(StatusesLink.class, Query.select().where("jira_status_name = ?", jiraStatusName ));
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
    public StatusesLink edit(String jiraStatusName, String useResponseStatusSlug) {
        return null;
    }

    @Override
    public List<StatusesLink> all() {
        return newArrayList(ao.find(StatusesLink.class));
    }

    private StatusesLink add(String jiraStatusName, String useResponseStatusSlug) {
        final StatusesLink statusLink = ao.create(StatusesLink.class);
        statusLink.setJiraStatusName(jiraStatusName);
        statusLink.setUseResponseStatusSlug(useResponseStatusSlug);
        statusLink.save();
        return statusLink;
    }
}

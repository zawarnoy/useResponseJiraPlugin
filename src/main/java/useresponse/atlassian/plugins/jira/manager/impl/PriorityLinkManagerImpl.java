package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.model.PriorityLink;
import com.atlassian.activeobjects.external.ActiveObjects;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import net.java.ao.Query;
import useresponse.atlassian.plugins.jira.model.URPriority;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

@Scanned
@Named("priorityLinkManager")
public class PriorityLinkManagerImpl implements PriorityLinkManager {

    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public PriorityLinkManagerImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public PriorityLink findByJiraPriorityName(String jiraPriorityName) {
        PriorityLink[] objects = ao.find(PriorityLink.class, Query.select().where("jira_priority_name = ?", String.valueOf(jiraPriorityName)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public PriorityLink findOrAdd(String jiraPriorityName, URPriority useResponsePriority) {
        PriorityLink link = findByJiraPriorityName(jiraPriorityName);
        if (link == null) {
            return add(jiraPriorityName, useResponsePriority);
        }
        return link;
    }

    private PriorityLink add(String jiraPriorityName, URPriority useResponsePriority) {
        PriorityLink link = ao.create(PriorityLink.class);
        link.setJiraPriorityName(jiraPriorityName);
        link.setUseResponsePriority(useResponsePriority);
        link.save();
        return link;
    }

    @Override
    public PriorityLink editUseResponsePriority(String jiraPriorityName, URPriority useResponsePriority) {
        PriorityLink link = findOrAdd(jiraPriorityName, null);
        if (link != null) {
            link.setUseResponsePriority(useResponsePriority);
            link.save();
        }
        return link;
    }

    @Override
    public List<PriorityLink> all() {
        return Arrays.asList(ao.find(PriorityLink.class));
    }
}

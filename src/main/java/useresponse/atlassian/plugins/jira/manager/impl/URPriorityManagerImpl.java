package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.manager.URPriorityManager;
import useresponse.atlassian.plugins.jira.model.URPriority;
import com.atlassian.activeobjects.external.ActiveObjects;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import net.java.ao.Query;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Scanned
@Named
public class URPriorityManagerImpl implements URPriorityManager {

    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public URPriorityManagerImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);
    }

    @Override
    public URPriority add(String useResponsePrioritySlug, String useResponsePriorityValue) {
        final URPriority urPriority = ao.create(URPriority.class);
        urPriority.setUseResponsePrioritySlug(useResponsePrioritySlug);
        urPriority.setUseResponsePriorityValue(useResponsePriorityValue);
        urPriority.save();
        return urPriority;
    }

    @Override
    public URPriority findBySlug(String useResponsePrioritySlug) {
        URPriority[] objects = ao.find(URPriority.class, Query.select().where("use_response_priority_slug = ?", String.valueOf(useResponsePrioritySlug)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public List<URPriority> all() {
        return newArrayList(ao.find(URPriority.class));
    }
}

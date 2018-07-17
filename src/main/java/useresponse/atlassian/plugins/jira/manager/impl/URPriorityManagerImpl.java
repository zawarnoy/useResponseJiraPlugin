package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import useresponse.atlassian.plugins.jira.manager.URPriorityManager;
import useresponse.atlassian.plugins.jira.model.URPriority;
import com.atlassian.activeobjects.external.ActiveObjects;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

import net.java.ao.Query;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collection;
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
    public URPriority findOrAdd(String useResponsePrioritySlug, String useResponsePriorityValue) {
        URPriority urPriority = findBySlug(useResponsePrioritySlug);
        if(urPriority == null) {
            return add(useResponsePrioritySlug, useResponsePriorityValue);
        }
        return urPriority;
    }

    @Override
    public URPriority findBySlug(String useResponsePrioritySlug) {
        URPriority[] objects = ao.find(URPriority.class, Query.select().where("use_response_priority_slug = ?", String.valueOf(useResponsePrioritySlug)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public List<URPriority> all() {
        return Arrays.asList(ao.find(URPriority.class));
    }
}

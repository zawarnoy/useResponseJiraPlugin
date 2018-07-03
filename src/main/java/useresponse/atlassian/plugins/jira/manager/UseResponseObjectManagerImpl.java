package useresponse.atlassian.plugins.jira.manager;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import com.atlassian.activeobjects.external.ActiveObjects;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

@Scanned
@Named
public class UseResponseObjectManagerImpl implements UseResponseObjectManager {

    @ComponentImport
    private final ActiveObjects ao;

    @Inject
    public UseResponseObjectManagerImpl(ActiveObjects ao) {
        this.ao = checkNotNull(ao);

    }

    @Override
    public UseResponseObject add(int useResponseId, int jiraId) {
        final UseResponseObject useResponseObject = ao.create(UseResponseObject.class);
        useResponseObject.setUseResponseId(useResponseId);
        useResponseObject.setJiraId(jiraId);
        useResponseObject.save();
        return useResponseObject;
    }

    @Override
    public UseResponseObject findOrAdd(int useResponseId, int jiraId) {
        UseResponseObject object = findByJiraId(jiraId);
        if (object != null) {
            return object;
        } else
        {
            return add(useResponseId, jiraId);
        }
    }

    @Override
    public UseResponseObject findByUseResponseId(int useResponseId) {
        return null;// ao.findOne(UseResponseObject.class, );
    }

    @Override
    public UseResponseObject findByJiraId(int jiraId) {
        UseResponseObject[] objects = ao.find(UseResponseObject.class, Query.select().where("jira_Id = ?", String.valueOf(jiraId) ));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public List<UseResponseObject> all() {
        return newArrayList(ao.find(UseResponseObject.class));
    }
}

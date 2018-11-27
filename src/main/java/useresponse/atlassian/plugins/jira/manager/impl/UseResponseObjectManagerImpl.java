package useresponse.atlassian.plugins.jira.manager.impl;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import net.java.ao.Query;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import com.atlassian.activeobjects.external.ActiveObjects;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

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
    public UseResponseObject add(int useResponseId, int jiraId, String type, boolean sync) {
        final UseResponseObject useResponseObject = ao.create(UseResponseObject.class);
        useResponseObject.setUseResponseId(useResponseId);
        useResponseObject.setJiraId(jiraId);
        useResponseObject.setNeedOfSync(true);
        useResponseObject.setObjectType(type);
        useResponseObject.setNeedOfSync(sync);
        useResponseObject.save();
        return useResponseObject;
    }

    @Override
    public UseResponseObject findOrAdd(int useResponseId, int jiraId, String type, boolean sync) {
        UseResponseObject object = findByJiraId(jiraId);
        if (object == null) {
            return add(useResponseId, jiraId, type, sync);
        } else {
            return object;
        }
    }

    @Override
    public UseResponseObject findByUseResponseId(int useResponseId) {
        UseResponseObject[] objects = ao.find(UseResponseObject.class, Query.select().where("use_response_id = ?", String.valueOf(useResponseId)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public UseResponseObject findByJiraId(int jiraId) {
        UseResponseObject[] objects = ao.find(UseResponseObject.class, Query.select().where("jira_Id = ?", String.valueOf(jiraId)));
        return objects.length > 0 ? objects[0] : null;
    }

    @Override
    public List<UseResponseObject> all() {
        return Arrays.asList(ao.find(UseResponseObject.class));
    }

    @Override
    public UseResponseObject changeAutosendingFlag(int jiraId, boolean autosendingFlag) {
        UseResponseObject object = findByJiraId(jiraId);
        if (object != null) {
            object.setNeedOfSync(autosendingFlag);
            return object;
        }
        return null;
    }

    @Override
    public void delete(UseResponseObject object) {
        ao.delete(object);
    }

    @Override
    public void delete(int jiraId, int useResponseId) {
        ao.delete(findByJiraId(jiraId));
    }
}

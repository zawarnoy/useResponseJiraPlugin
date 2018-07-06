package useresponse.atlassian.plugins.jira.manager.impl;

import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.model.PriorityLink;

import java.util.List;

public class PriorityLinkMangerImpl implements PriorityLinkManager {
    @Override
    public PriorityLink findByJiraPriorityName(String jiraPriorityName) {
        return null;
    }

    @Override
    public PriorityLink findOrAdd(String jiraPriorityName, String useResponsePriorityName) {
        return null;
    }

    @Override
    public PriorityLink editUseResponsePriority(String jiraPriorityName, String useResponsePriorityName) {
        return null;
    }

    @Override
    public List<PriorityLink> all() {
        return null;
    }
}

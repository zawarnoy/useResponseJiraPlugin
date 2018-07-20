package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.manager.*;

public abstract class AbsctractListenerActionFactory implements ListenerActionFactory {

    protected   UseResponseObjectManager useResponseObjectManager;
    protected  RendererManager rendererManager;
    protected  PluginSettingsFactory pluginSettingsFactory;
    protected  CommentLinkManager commentLinkManager;
    protected WithId entity;
}

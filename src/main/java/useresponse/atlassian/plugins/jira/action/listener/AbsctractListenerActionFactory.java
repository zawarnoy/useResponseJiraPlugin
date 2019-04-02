package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.*;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class AbsctractListenerActionFactory implements ListenerActionFactory {

    protected WithId entity;


    protected RendererManager rendererManager;

    @Inject
    public void setRendererManager(RendererManager rendererManager) {
        this.rendererManager = rendererManager;
    }


    protected PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }


    protected UseResponseObjectManager useResponseObjectManager;

    @Inject
    @Named("useResponseObjectManager")
    public void setUseResponseObjectManager(UseResponseObjectManager useResponseObjectManager) {
        this.useResponseObjectManager = useResponseObjectManager;
    }


    protected CommentLinkManager commentLinkManager;

    @Inject
    @Named("commentLinkManager")
    public void setCommentLinkManager(CommentLinkManager commentLinkManager) {
        this.commentLinkManager = commentLinkManager;
    }


    protected CommentRequestBuilder commentRequestBuilder;

    @Autowired
    public void setCommentRequestBuilder(CommentRequestBuilder commentRequestBuilder) {
        this.commentRequestBuilder = commentRequestBuilder;
    }

    @Override
    public void setEntity(WithId entity) {
        this.entity = entity;
    }

}

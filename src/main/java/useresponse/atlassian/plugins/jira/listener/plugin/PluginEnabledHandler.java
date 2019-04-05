package useresponse.atlassian.plugins.jira.listener.plugin;

import com.atlassian.plugin.event.events.PluginEnabledEvent;
import org.springframework.context.event.EventListener;

public interface PluginEnabledHandler {

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) throws Exception;

}

package useresponse.atlassian.plugins.jira.listener.plugin;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.service.SettingsService;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService({PluginEnabledHandler.class})
@Named("pluginEnabledHandler")
@Scanned
public class PluginEnabledHandlerImpl implements InitializingBean, DisposableBean, PluginEnabledHandler {

    Logger log = LoggerFactory.getLogger(PluginEnabledHandlerImpl.class);

    @Autowired
    SettingsService settingsService;

    @ComponentImport
    private final EventPublisher eventPublisher;

    @Inject
    public PluginEnabledHandlerImpl(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }

    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) throws Exception {
        Plugin plugin = event.getPlugin();

        if (plugin.getKey().equals("useresponse.jira-plugin")) {
            settingsService.prepareDB();
        }
    }
}
package net.blay09.mods.defaultoptions;

import net.blay09.mods.balm.api.event.client.ClientStartedEvent;
import net.blay09.mods.defaultoptions.api.*;

import java.util.ServiceLoader;

public class DefaultOptionsInitializer {

    static {
        DefaultOptionsAPI.__internalMethods = new InternalMethodsImpl();

        ServiceLoader<DefaultOptionsPlugin> loader = ServiceLoader.load(DefaultOptionsPlugin.class);
        loader.forEach(DefaultOptionsPlugin::initialize);
    }

    public static void preLoad() {
        loadDefaults(DefaultOptionsLoadStage.PRE_LOAD);
    }

    public static void postLoad(ClientStartedEvent event) {
        // Once Minecraft has finished loading and there are no default settings yet, populate the default options with the current settings
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            if (!handler.hasDefaults()) {
                try {
                    handler.saveCurrentOptionsAsDefault();
                } catch (DefaultOptionsHandlerException e) {
                    DefaultOptions.logger.warn("Failed to create initial default options from current options for {}", e.getHandlerId(), e);
                }
            }
        }

        loadDefaults(DefaultOptionsLoadStage.POST_LOAD);
    }

    private static void loadDefaults(DefaultOptionsLoadStage stage) {
        for (DefaultOptionsHandler handler : DefaultOptions.getDefaultOptionsHandlers()) {
            if (handler.shouldLoadDefaults() && handler.getLoadStage() == stage) {
                try {
                    DefaultOptions.logger.info("Loaded default options for {}", handler.getId());
                    handler.loadDefaults();
                } catch (DefaultOptionsHandlerException e) {
                    DefaultOptions.logger.error("Failed to load default options for {}", e.getHandlerId(), e);
                }
            }
        }
    }
}

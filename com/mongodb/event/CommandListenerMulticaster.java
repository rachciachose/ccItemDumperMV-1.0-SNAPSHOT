// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.event;

import com.mongodb.diagnostics.logging.Loggers;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.mongodb.assertions.Assertions;
import java.util.List;
import com.mongodb.diagnostics.logging.Logger;
import com.mongodb.annotations.Immutable;

@Immutable
public class CommandListenerMulticaster implements CommandListener
{
    private static final Logger LOGGER;
    private final List<CommandListener> commandListeners;
    
    public CommandListenerMulticaster(final List<CommandListener> commandListeners) {
        Assertions.notNull("commandListeners", commandListeners);
        for (final CommandListener cur : commandListeners) {
            Assertions.notNull("commandListener", cur);
        }
        this.commandListeners = new ArrayList<CommandListener>(commandListeners);
    }
    
    public List<CommandListener> getCommandListeners() {
        return Collections.unmodifiableList((List<? extends CommandListener>)this.commandListeners);
    }
    
    @Override
    public void commandStarted(final CommandStartedEvent event) {
        for (final CommandListener cur : this.commandListeners) {
            try {
                cur.commandStarted(event);
            }
            catch (Exception e) {
                if (!CommandListenerMulticaster.LOGGER.isWarnEnabled()) {
                    continue;
                }
                CommandListenerMulticaster.LOGGER.warn(String.format("Exception thrown raising command started event to listener %s", cur), e);
            }
        }
    }
    
    @Override
    public void commandSucceeded(final CommandSucceededEvent event) {
        for (final CommandListener cur : this.commandListeners) {
            try {
                cur.commandSucceeded(event);
            }
            catch (Exception e) {
                if (!CommandListenerMulticaster.LOGGER.isWarnEnabled()) {
                    continue;
                }
                CommandListenerMulticaster.LOGGER.warn(String.format("Exception thrown raising command succeeded event to listener %s", cur), e);
            }
        }
    }
    
    @Override
    public void commandFailed(final CommandFailedEvent event) {
        for (final CommandListener cur : this.commandListeners) {
            try {
                cur.commandFailed(event);
            }
            catch (Exception e) {
                if (!CommandListenerMulticaster.LOGGER.isWarnEnabled()) {
                    continue;
                }
                CommandListenerMulticaster.LOGGER.warn(String.format("Exception thrown raising command failed event to listener %s", cur), e);
            }
        }
    }
    
    static {
        LOGGER = Loggers.getLogger("protocol.event");
    }
}

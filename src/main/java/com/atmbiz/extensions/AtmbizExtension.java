package com.atmbiz.extensions;

import com.atmbiz.extensions.mq.RPCServer;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Atmbiz extension
 */
public class AtmbizExtension extends AbstractExtension{
    protected static final Logger log = LoggerFactory.getLogger(AtmbizExtension.class);
    private static IExtensionContext ctx;

    @Override
    public String getName() {
        return "Atmbiz";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.ctx = ctx;
        // init MQ server
        RPCServer server = new RPCServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public static IExtensionContext getExtensionContext() {
        return ctx;
    }




}

package com.atmbiz.extensions;

import com.atmbiz.extensions.controller.AtmbizRestServices;
import com.atmbiz.extensions.controller.ServletFilter;
import com.atmbiz.extensions.mq.RPCServer;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public Set<IRestService> getRestServices() {
        HashSet<IRestService> services = new HashSet<>();
        services.add(new IRestService() {
            @Override
            public String getPrefixPath() {
                return "atmbiz";
            }

            @Override
            public Class getImplementation() {
                return AtmbizRestServices.class;
            }

            @Override
            public List<Class> getFilters() {
                return Arrays.asList(ServletFilter.class);
            }
        });

        return services;
    }

    public static IExtensionContext getExtensionContext() {
        return ctx;
    }




}

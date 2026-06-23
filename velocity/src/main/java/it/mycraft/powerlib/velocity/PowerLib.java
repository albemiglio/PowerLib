package it.mycraft.powerlib.velocity;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

public class PowerLib {

    @Getter
    private static ProxyServer proxy;

    @Getter
    private static Object plugin;

    public static void inject(ProxyServer proxyServer, Object plugin) {
        proxy = proxyServer;
        PowerLib.plugin = plugin;
    }

    /**
     * @deprecated use {@link #inject(ProxyServer, Object)} so the scheduler can build tasks bound to the plugin.
     */
    @Deprecated
    public static void inject(ProxyServer proxyServer) {
        proxy = proxyServer;
    }
}

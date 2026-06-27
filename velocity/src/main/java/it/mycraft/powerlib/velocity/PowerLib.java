package it.mycraft.powerlib.velocity;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

/**
 * Static holder for the Velocity proxy and owning plugin instance, used across the Velocity module.
 */
public class PowerLib {

    @Getter
    private static ProxyServer proxy;

    @Getter
    private static Object plugin;

    /**
     * Binds the proxy and the owning plugin so the library can schedule tasks and register listeners.
     *
     * @param proxyServer the Velocity proxy
     * @param plugin      the owning plugin instance
     */
    public static void inject(ProxyServer proxyServer, Object plugin) {
        proxy = proxyServer;
        PowerLib.plugin = plugin;
    }

    /**
     * Binds only the proxy, leaving the plugin instance unset.
     *
     * @param proxyServer the Velocity proxy
     * @deprecated use {@link #inject(ProxyServer, Object)} so the scheduler can build tasks bound to the plugin.
     */
    @Deprecated
    public static void inject(ProxyServer proxyServer) {
        proxy = proxyServer;
    }
}

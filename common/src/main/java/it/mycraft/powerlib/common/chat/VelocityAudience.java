package it.mycraft.powerlib.common.chat;

public class VelocityAudience extends PlatformAudience {

    protected VelocityAudience() {
        try {
            String packageName = VelocityAudience.class.getPackage().getName();
            String adapterPackage = packageName.replace(".common.chat", ".velocity.adapters");
            audienceAdapterClass = Class.forName(adapterPackage + ".AudienceAdapter");
            commandSenderClass = Class.forName("com.velocitypowered.api.proxy.ProxyServer");
        } catch (ClassNotFoundException e) {
            sendError();
        }
    }
}

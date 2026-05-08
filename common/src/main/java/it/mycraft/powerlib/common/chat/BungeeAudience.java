package it.mycraft.powerlib.common.chat;

public class BungeeAudience extends PlatformAudience {

    protected BungeeAudience() {
        try {
            String adapterPackage = BungeeAudience.class.getPackage().getName()
                    .replace(".common.chat", ".bungee.adapters");
            audienceAdapterClass = Class.forName(adapterPackage + ".AudienceAdapter");
            commandSenderClass = Class.forName("net.md_5.bungee.api.CommandSender");
        } catch (ClassNotFoundException e) {
            sendError();
        }
    }
}

package it.mycraft.powerlib.common.chat;

public class BukkitAudience extends PlatformAudience {

    protected BukkitAudience() {
        try {
            String adapterPackage = BukkitAudience.class.getPackage().getName()
                    .replace(".common.chat", ".bukkit.adapters");
            audienceAdapterClass = Class.forName(adapterPackage + ".AudienceAdapter");
            commandSenderClass = Class.forName("org.bukkit.command.CommandSender");
        } catch (ClassNotFoundException e) {
            sendError();
        }
    }
}

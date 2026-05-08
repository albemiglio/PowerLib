package it.mycraft.powerlib.common.chat;

public class VelocityAudience extends PlatformAudience {

    protected VelocityAudience() {
        try {
            String adapterPackage = VelocityAudience.class.getPackage().getName()
                    .replace(".common.chat", ".velocity.adapters");
            audienceAdapterClass = Class.forName(adapterPackage + ".AudienceAdapter");
            commandSenderClass = Class.forName("com.velocitypowered.api.command.CommandSource");
        } catch (ClassNotFoundException e) {
            sendError();
        }
    }
}

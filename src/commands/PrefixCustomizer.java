package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Prefixes;

import java.awt.*;

public class PrefixCustomizer implements Command {

    public boolean called(String[] args, MessageReceivedEvent event) {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            return false;
        } else {
            return true;
        }
    }

    private void writeError (String error, MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Error")
                        .setDescription(error)
                        .build()
        ).queue();
    }

    private void writeMessage (String msg, MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setDescription(msg)
                        .build()
        ).queue();
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            writeMessage(help(), event);
            return;
        }
        if (args.length > 1) {
            writeError("Don't use any Spaces for your Custom Prefix!", event);
            return;
        }
        Prefixes.prefixMap.put(event.getGuild(), args[0]);
        writeMessage("Successfully changed Prefix to" + args[0], event);
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        if (success) {
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setTitle("Not enough Permissions")
                            .setColor(Color.RED)
                            .setDescription("You need to be an Administrator on this Server to use this Command!")
                            .build()
            ).queue();
        }
    }

    public String help() {
        return "Usage of the Command, to change the Prefix for the Bot:\n" +
                "   Yo!changePrefix [new Prefix]\n" +
                "\n" +
                "IMPORTANT:\n" +
                "   When you change the Default Prefix (Yo!)\n" +
                "   you need to use your custom one instead.";
    }
}

package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;

import java.awt.*;

import static util.DefaultMessageWriter.writeError;
import static util.DefaultMessageWriter.writeMessage;

public class PrefixCustomizer implements Command {

    private long guildID = 0;

    public boolean called(String[] args, MessageReceivedEvent event) {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            return false;
        } else {
            return true;
        }
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            guildID = event.getGuild().getIdLong();
            writeMessage(help(), event);
            return;
        }
        if (args.length > 1) {
            writeError("Don't use any Spaces for your Custom Prefix!", event);
            return;
        }
        Prefixes.prefixMap.put(event.getGuild().getIdLong(), args[0]);
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
        return """
                Usage of the Command, to change the Prefix for the Bot:
                $prefix changePrefix [new Prefix]
                
                IMPORTANT:
                   When you change the Default Prefix (Yo!)
                   you need to use your custom one instead.""".replace("$prefix", Prefixes.getPrefix(guildID));
    }
}

package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import util.EventContainer;
import util.Prefixes;

import java.awt.*;

import static util.DefaultMessageWriter.writeError;
import static util.DefaultMessageWriter.writeMessage;

public class PrefixCustomizer implements Command {

    private static final long AUTHOR = 279_184_766_140_678_145L;
    private long guildID = 0;

    public PrefixCustomizer(final String invoke, final String description, final JDA jda) {
        jda.upsertCommand(invoke, description).queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return !event
                .getMember()
                .getPermissions()
                .contains(Permission.ADMINISTRATOR)
                && event.getMember().getUser().getIdLong() != AUTHOR;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        if (args.length == 0) {
            guildID = event.getGuild().getIdLong();
            writeMessage(help(), event);
            return;
        }
        if (args.length > 1) {
            writeError("Don't use any Spaces for your Custom Prefix!", event);
            return;
        }
        Prefixes.addPrefix(event.getGuild().getIdLong(), args[0]);
        writeMessage("Successfully changed Prefix to" + args[0], event);
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {
        if (success) {
            event.getReply().reply(
                    new EmbedBuilder()
                            .setTitle("Not enough Permissions")
                            .setColor(Color.RED)
                            .setDescription("You need to be an Administrator on this Server to use this Command!")
                            .build()
            );
        }
    }

    @Override
    public String help() {
        return ("Usage of the Command, to change the Prefix for the Bot:\n"
                + "$prefix changePrefix [new Prefix]\n"
                + "\n"
                + "IMPORTANT:\n"
                + "  When you change the Default Prefix (Yo!) \n"
                + "  you need to use your custom one instead.").replace("$prefix", Prefixes.getPrefix(guildID));
    }
}

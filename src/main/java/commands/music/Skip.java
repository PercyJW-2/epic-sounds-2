package commands.music;

import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;

import static util.DefaultMessageWriter.*;

public class Skip implements Command {

    private long guildID;

    private final AudioInstanceManager audioInstanceManager;

    public Skip (final AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(final String[] args, final MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(final String[] args, final MessageReceivedEvent event) {
        final Guild guild = event.getGuild();
        guildID = guild.getIdLong();
        if (args != null && args.length > 0
                && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))) {
            writePersistentMessage(help(), event);
            return;
        }

        audioInstanceManager.skip(guild);

        writeMessage("Skipped song.", event);
    }

    @Override
    public void executed(final boolean success, final MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to skip a song and continue to the next if it exists.\n"
                + "To view this message write '" + Prefixes.getPrefix(guildID) + "skip --help'.";
    }
}

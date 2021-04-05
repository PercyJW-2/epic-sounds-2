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

    public Skip (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        guildID = g.getIdLong();
        if (args != null && args.length > 0) {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
                return;
            }
        }

        audioInstanceManager.skip(g);

        writeMessage("Skipped song.",event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to skip a song and continue to the next if it exists.\n" +
                "To view this message write '"+ Prefixes.getPrefix(guildID) + "skip --help'.";
    }
}

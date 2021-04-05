package commands.music;

import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Locale;

import static util.DefaultMessageWriter.*;

public class Volume implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Volume (final AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }
    @Override
    public boolean called(final String[] args, final MessageReceivedEvent event) {
        return false;
    }

    @SuppressWarnings({"PMD.MissingBreakInSwitch", "PMD.CyclomaticComplexity"})
    @Override
    public void action(final String[] args, final MessageReceivedEvent event) {
        boolean show = true;
        int volume = 50;
        if (args.length > 0) {
            for (final String s: args) {
                switch (s.toLowerCase(Locale.getDefault())) {
                    case "--help":
                    case "-h":
                        writePersistentMessage(help(), event);
                        break;
                    case "--show":
                    case "-s":
                        show = true;
                        break;
                    case "--changeVolume":
                    case "-cv":
                        show = false;
                        break;
                    default:
                        show = false;
                        volume = Integer.parseInt(s);
                }
            }
        } else {
            writeMessage("The current Volume is: " + audioInstanceManager.getVolume(event.getGuild()), event);
            return;
        }
        if (show) {
            writeMessage("The current Volume is: " + audioInstanceManager.getVolume(event.getGuild()), event);
        } else {
            if (volume > 200 || volume < 0) {
                writeError("The Number to change the Volume mut be inbetween 0 and 200", event);
            } else {
                audioInstanceManager.setVolume(event.getGuild(), volume);
                writeMessage("The volume was changed to: " + audioInstanceManager.getVolume(event.getGuild()), event);
            }
        }
    }

    @Override
    public void executed(final boolean success, final MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to change the master volume of the bot.\n" +
                "Use '--help' or '-h' to view this message.\n" +
                "Use '--show', '-s' or write nothing to view the current volume.\n" +
                "Write a number to change the Volume to that number.";
    }
}

package commands.music;

import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static util.DefaultMessageWriter.*;

public class Volume implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Volume (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        boolean show = true;
        int volume = 50;
        if (args.length > 0) {
            for (String s: args) {
                switch (s.toLowerCase()) {
                    case "--help", "-h" -> {
                        writePersistentMessage(help(), event);
                        return;
                    }
                    case "--show", "-s" -> show = true;
                    case "--changeVolume", "-cv" -> show = false;
                    default -> {
                        show = false;
                        volume = Integer.parseInt(s);
                    }
                }
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
        } else {
            writeMessage("The current Volume is: " + audioInstanceManager.getVolume(event.getGuild()), event);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to change the master volume of the bot.\n" +
                "Use '--help' or '-h' to view this message.\n" +
                "Use '--show', '-s' or write nothing to view the current volume.\n" +
                "Write a number to change the Volume to that number.";
    }
}

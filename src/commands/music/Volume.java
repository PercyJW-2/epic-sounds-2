package commands.music;

import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static util.defaultMessageWriter.*;


public class Volume implements Command {

    private AudioInstanceManager audioInstanceManager;

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
        if (args != null || args.length > 0) {
            for (String s: args) {
                switch (s.toLowerCase()) {
                    case "--help":
                    case "-h":
                        writePersistentMessage(help(),event);
                        return;
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
                        volume = Integer.valueOf(s);
                }
            }
            if (show) {
                writeMessage("The current Volume is: " + audioInstanceManager.getVolume(event.getGuild()), event);
            } else {
                audioInstanceManager.setVolume(event.getGuild(), volume);
                writeMessage("The volume was changed to: " + audioInstanceManager.getVolume(event.getGuild()), event);
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
        return "Use this Command to change the mastervolume of the bot.\n" +
                "Use '--help' or '-h' to view this message.\n" +
                "Use '--show', '-s' or write nothing to view the current volume.\n" +
                "Write a number to change the Volume to that number.";
    }
}

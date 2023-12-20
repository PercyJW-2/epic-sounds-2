package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInstanceManager;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import epicsounds2.util.EventContainer;

import java.util.Locale;

import static epicsounds2.util.DefaultMessageWriter.*;

public class Volume implements Command {

    private static final int MAX_VOLUME = 200;
    private static final int DEFAULT_VOLUME = 100;
    private final AudioInstanceManager audioInstanceManager;

    public Volume(final AudioInstanceManager audioInstanceManager,
                   final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
        jda.upsertCommand(invoke, description)
                .addOption(
                        OptionType.INTEGER,
                        "volume",
                        "Percentage of volume from 0 to 200 percent",
                        true
                        )
                .queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @SuppressWarnings({
            "PMD.MissingBreakInSwitch",
            "PMD.CyclomaticComplexity",
            "checkstyle:MultipleStringLiterals",
            "checkstyle:InnerAssignment"})
    @Override
    public void action(final String[] args, final EventContainer event) {
        boolean show = true;
        int volume = DEFAULT_VOLUME;
        if (args.length > 0) {
            for (final String str : args) {
                switch (str.toLowerCase(Locale.getDefault())) {
                    case "--help", "-h" -> writeMessage(help(), event);
                    case "--show", "-s" -> show = true;
                    case "--changevolume", "-cv" -> show = false;
                    default -> {
                        show = false;
                        volume = Integer.parseInt(str);
                    }
                }
            }
        } else {
            writeMessage("The current Volume is: " + audioInstanceManager.getVolume(event.getGuild()), event);
            return;
        }
        if (show) {
            writeMessage("The current Volume is: " + audioInstanceManager.getVolume(event.getGuild()), event);
        } else {
            if (volume > MAX_VOLUME || volume < 0) {
                writeError("The Number to change the Volume mut be inbetween 0 and 200", event);
            } else {
                audioInstanceManager.setVolume(event.getGuild(), volume);
                writeMessage("The volume was changed to: " + audioInstanceManager.getVolume(event.getGuild()), event);
            }
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return """
                Use this Command to change the master volume of the bot.
                Use '--help' or '-h' to view this message.
                Use '--show', '-s' or write nothing to view the current volume.
                Write a number to change the Volume to that number.""";
    }
}

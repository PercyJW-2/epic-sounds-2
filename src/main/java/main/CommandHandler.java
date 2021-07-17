package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import commands.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EventConverterUtil;

@SuppressWarnings("PMD.ClassNamingConventions")
public class CommandHandler {
    private static final Map<String, Command> COMMANDS = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);

    protected CommandHandler() {
        throw new UnsupportedOperationException();
    }

    protected static Map<String, Command> getCommands() {
        return COMMANDS;
    }

    public static void handlerCommand(final CommandParser.CommandContainer cmd) {

        if (COMMANDS.containsKey(cmd.invoke)) {

            final boolean safe =
                    COMMANDS.get(cmd.invoke).called(cmd.args, EventConverterUtil.convertMessageEvent(cmd.event));

            if (!safe) {
                COMMANDS.get(cmd.invoke).action(cmd.args, EventConverterUtil.convertMessageEvent(cmd.event));
            }
            COMMANDS.get(cmd.invoke).executed(safe, EventConverterUtil.convertMessageEvent(cmd.event));

        } else {

            LOG.info("That command is not registered");

        }

    }

    public static void handleSlashCommand(final SlashCommandEvent evt) {
        if (COMMANDS.containsKey(evt.getName())) {
            final String options =
                    evt.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.joining(" "));
            final boolean safe = COMMANDS.get(evt.getName()).called(
                    options.split(" "), EventConverterUtil.convertSlashCommandEvent(evt));

            if (!safe) {
                COMMANDS.get(evt.getName()).action(
                        options.split(" "), EventConverterUtil.convertSlashCommandEvent(evt));
            }
            COMMANDS.get(evt.getName()).executed(safe, EventConverterUtil.convertSlashCommandEvent(evt));
        } else {
            LOG.info("That command is not registered");
        }
    }

}

package epicsounds2.main;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import epicsounds2.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import epicsounds2.util.EventConverterUtil;

@SuppressWarnings("PMD.ClassNamingConventions")
public class CommandHandler {
    private static final Map<String, Command> COMMANDS = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);
    private static final String THAT_COMMAND_IS_NOT_REGISTERED = "That command is not registered";
    private static final String DELIMITER = " ";

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

            LOG.info(THAT_COMMAND_IS_NOT_REGISTERED);

        }

    }

    public static void handleSlashCommand(final SlashCommandInteractionEvent evt) {
        if (COMMANDS.containsKey(evt.getName())) {
            final String options =
                    evt.getOptions().stream().map(OptionMapping::getAsString).collect(Collectors.joining(DELIMITER));
            final boolean safe = COMMANDS.get(evt.getName()).called(
                    options.split(DELIMITER), EventConverterUtil.convertSlashCommandEvent(evt));

            if (!safe) {
                COMMANDS.get(evt.getName()).action(
                        options.split(DELIMITER), EventConverterUtil.convertSlashCommandEvent(evt));
            }
            COMMANDS.get(evt.getName()).executed(safe, EventConverterUtil.convertSlashCommandEvent(evt));
        } else {
            LOG.info(THAT_COMMAND_IS_NOT_REGISTERED);
        }
    }

}

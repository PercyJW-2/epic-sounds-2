package main;

import java.util.HashMap;
import java.util.Map;

import commands.Command;

@SuppressWarnings("PMD.ClassNamingConventions")
public class CommandHandler {
    private static final Map<String, Command> COMMANDS = new HashMap<>();

    protected CommandHandler() {
        throw new UnsupportedOperationException();
    }

    protected static Map<String, Command> getCommands() {
        return COMMANDS;
    }

    public static void handlerCommand(final CommandParser.CommandContainer cmd) {

        if (COMMANDS.containsKey(cmd.invoke)) {

            final boolean safe = COMMANDS.get(cmd.invoke).called(cmd.args, cmd.event);

            if (!safe) {
                COMMANDS.get(cmd.invoke).action(cmd.args, cmd.event);
            }
            COMMANDS.get(cmd.invoke).executed(safe, cmd.event);

        } else {

            System.out.println("That command is not registrated");

        }

    }

}

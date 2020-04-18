package main;

import java.util.HashMap;

import commands.Command;

public class CommandHandler {
    public static HashMap<String, Command> commands = new HashMap<>();

    public static void handlerCommand (CommandParser.commandContainer cmd) {

        if (commands.containsKey(cmd.invoke)) {

            boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);

            if (!safe) {
                commands.get(cmd.invoke).action(cmd.args, cmd.event);
            }
            commands.get(cmd.invoke).executed(safe, cmd.event);

        } else {

            System.out.println("That command is not registrated");

        }

    }

}

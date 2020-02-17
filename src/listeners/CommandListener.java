package listeners;

import main.CommandHandler;
import main.CommandParser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.Prefixes;
import static util.defaultMessageWriter.writeMessage;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String prefix = Prefixes.getPrefix(event.getGuild().getIdLong());
        if (event.getMessage().getContentDisplay().startsWith(prefix) && event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId()) {

            CommandHandler.handlerCommand(CommandParser.parser(event.getMessage().getContentDisplay(), event, prefix));

        } else if (event.getMessage().getContentDisplay().startsWith(Prefixes.defaultPrefix + "help") && event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId()) {

            writeMessage("The Default Prefix was Changed. Now you have to use the Command '" + prefix + "help' to get the List of Commands.", event);

        }

    }
}

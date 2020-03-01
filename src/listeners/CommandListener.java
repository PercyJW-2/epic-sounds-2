package listeners;

import main.CommandHandler;
import main.CommandParser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.Prefixes;

import java.util.regex.Pattern;

import static util.defaultMessageWriter.writeMessage;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String prefix = Prefixes.getPrefix(event.getGuild().getIdLong());
        int prefixLen = prefix.length();
        try {
            if (Pattern.quote(event.getMessage().getContentDisplay().substring(0, prefixLen)).equals(Pattern.quote(prefix)) && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {

                CommandHandler.handlerCommand(CommandParser.parser(event.getMessage().getContentDisplay(), event, prefix));

            } else if (event.getMessage().getContentDisplay().startsWith(Prefixes.defaultPrefix) && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {

                writeMessage("The Default Prefix was Changed. Now you have to use the Command '" + prefix + "help' to get the List of Commands.", event);

            }
        } catch (StringIndexOutOfBoundsException ignored) {

        }
    }
}

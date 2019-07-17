package listeners;

import main.CommandHandler;
import main.CommandParser;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.Prefixes;

import java.util.HashMap;

public class CommandListener extends ListenerAdapter {

    private static HashMap<User, Boolean> muted = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String prefix = Prefixes.prefixMap.get(event.getGuild());
        if (prefix == null) {
            prefix = Prefixes.getDefaultPrefix();
        }
        if (event.getMessage().getContentDisplay().startsWith(prefix) && event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId()) {

            CommandHandler.handlerCommand(CommandParser.parser(event.getMessage().getContentDisplay(), event, prefix));

        }

    }
}

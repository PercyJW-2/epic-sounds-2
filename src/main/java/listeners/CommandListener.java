package listeners;

import commands.Help;
import main.CommandHandler;
import main.CommandParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.Prefixes;

import java.util.List;

import static util.DefaultMessageWriter.writeMessage;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {
        final String prefix = Prefixes.getPrefix(event.getGuild().getIdLong());
        final Message msg = event.getMessage();
        final String msgContent = msg.getContentDisplay();
        try {
            if (msgContent.startsWith(prefix)
                    && !msg.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {

                CommandHandler.handlerCommand(
                        CommandParser.parser(msgContent, event, prefix));

            } else if (msgContent.startsWith(Prefixes.defaultPrefix)
                    && !msg.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                new Help().action(new String[]{}, event);
            } else {
                final List<User> mentions = msg.getMentionedUsers();
                if (mentions.contains(event.getJDA().getSelfUser())) {
                    new Help().action(new String[]{}, event);
                }
            }
        } catch (StringIndexOutOfBoundsException ignored) {

        }
    }
}

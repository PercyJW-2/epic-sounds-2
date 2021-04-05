package listeners;

import main.CommandHandler;
import main.CommandParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.Prefixes;

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

                writeMessage("The Default Prefix was Changed. Now you have to use the Command '"
                        + prefix
                        + "help' to get the List of Commands.", event);

            }
        } catch (StringIndexOutOfBoundsException ignored) {

        }
    }
}

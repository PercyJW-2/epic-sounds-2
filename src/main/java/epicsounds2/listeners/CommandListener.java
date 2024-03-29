package epicsounds2.listeners;

import epicsounds2.commands.Help;
import epicsounds2.main.CommandHandler;
import epicsounds2.main.CommandParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import epicsounds2.util.EventConverterUtil;
import epicsounds2.util.Prefixes;

import java.util.List;

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

            } else if (msgContent.startsWith(Prefixes.DEFAULT_PREFIX)
                    && !msg.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
                new Help().action(new String[]{}, EventConverterUtil.convertMessageEvent(event));
            } else {
                final List<User> mentions = msg.getMentions().getUsers();
                if (mentions.contains(event.getJDA().getSelfUser())) {
                    new Help().action(new String[]{}, EventConverterUtil.convertMessageEvent(event));
                }
            }
        } catch (StringIndexOutOfBoundsException ignored) {

        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        CommandHandler.handleSlashCommand(event);
    }
}

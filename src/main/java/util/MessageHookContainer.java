package util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class MessageHookContainer {
    private final Message msg;
    private final InteractionHook hook;

    public MessageHookContainer(final Message msg) {
        this.msg = msg;
        this.hook = null;
    }

    public MessageHookContainer(final InteractionHook hook) {
        this.hook = hook;
        this.msg = null;
    }

    public Message getMsg() {
        return msg;
    }

    public InteractionHook getHook() {
        return hook;
    }
}

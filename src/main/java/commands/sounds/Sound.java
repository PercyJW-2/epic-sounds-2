package commands.sounds;

import commands.Command;
import util.EventContainer;

public class Sound implements Command {
    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {

    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return null;
    }
}

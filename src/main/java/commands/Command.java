package commands;

import util.EventContainer;

public interface Command {

    boolean called(String[] args, EventContainer event);
    void action(String[] args, EventContainer event);
    void executed(boolean success, EventContainer event);
    String help();


}

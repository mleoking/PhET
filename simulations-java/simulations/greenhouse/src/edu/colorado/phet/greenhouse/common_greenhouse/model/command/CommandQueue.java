/**
 * Class: CommandQueue
 * Package: edu.colorado.phet.common.model.command
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.greenhouse.common_greenhouse.model.command;

import java.util.Vector;

public class CommandQueue implements Command {
    Vector al = new Vector();

    public int size() {
        return al.size();
    }

    public void doIt() {
        for (int i = 0; i < al.size(); i++) {
            commandAt(i).doIt();
        }
        al.clear();
    }

    private Command commandAt(int i) {
        return (Command) al.get(i);
    }

    public void addCommand(Command c) {
        al.add(c);
    }

}

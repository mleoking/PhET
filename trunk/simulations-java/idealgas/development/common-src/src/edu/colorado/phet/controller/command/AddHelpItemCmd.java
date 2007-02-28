package edu.colorado.phet.controller.command;

import edu.colorado.phet.graphics.util.HelpItem;
import edu.colorado.phet.controller.PhetApplication;

/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 7, 2003
 * Time: 4:37:08 PM
 * To change this template use Options | File Templates.
 */
public class AddHelpItemCmd implements Command {

    private HelpItem helpItem;

    public AddHelpItemCmd( HelpItem helpItem ) {
        this.helpItem = helpItem;
    }

    public Object doIt() {
        PhetApplication.instance().getPhetFrame().addHelpItem( helpItem );
        return helpItem;
    }
}

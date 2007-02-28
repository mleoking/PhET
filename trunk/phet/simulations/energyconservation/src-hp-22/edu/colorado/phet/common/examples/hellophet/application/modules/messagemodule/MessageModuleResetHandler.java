/**
 * Class: MessageModuleResetHandler
 * Package: edu.colorado.phet.common.examples.hellophet.application.modules.messagemodule
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.common.examples.hellophet.application.modules.messagemodule;

import edu.colorado.phet.common.view.components.media.Resettable;

public class MessageModuleResetHandler implements Resettable {
    MessageModule mm;

    public MessageModuleResetHandler( MessageModule mm ) {
        this.mm = mm;
    }

    public void reset() {
        mm.removeLastMessage();
    }
}

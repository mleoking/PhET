/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 21, 2003
 * Time: 2:59:42 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.controller.PhetAboutDialog;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;

import javax.swing.*;

public class IdealGasAboutDialog extends PhetAboutDialog {

    public IdealGasAboutDialog( JFrame parent ) {
        super( parent );
    }

    protected String getAppTitle() {
        return IdealGasConfig.TITLE;
    }

    protected String getAppVersion() {
        return IdealGasConfig.VERSION;
    }
}

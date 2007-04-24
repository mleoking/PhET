package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.QWIOptionsMenu;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 12:59:16 PM
 *
 */

public class DGOptionsMenu extends QWIOptionsMenu {
    public DGOptionsMenu( QWIModule qwiModule ) {
        super( qwiModule );
        removeExpectationValueItems();
    }
}

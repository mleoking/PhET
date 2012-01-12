// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIOptionsMenu;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 12:59:16 PM
 */

public class DGOptionsMenu extends QWIOptionsMenu {
    public DGOptionsMenu( QWIModule qwiModule ) {
        super( qwiModule );
        removeExpectationValueItems();
    }
}

// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.modules.mandel;

import edu.colorado.phet.quantumwaveinterference.controls.ClearButton;
import edu.colorado.phet.quantumwaveinterference.controls.QWIControlPanel;
import edu.colorado.phet.quantumwaveinterference.controls.ResetButton;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 1:29:46 PM
 */

public class MandelControlPanel extends QWIControlPanel {
    public MandelControlPanel( MandelModule mandelModule ) {
        super( mandelModule );
        addSeparator();
        addSpacer();
        getContentPanel().setAnchor( GridBagConstraints.CENTER );
        addControl( new ResetButton( mandelModule ) );
        addControl( new ClearButton( mandelModule.getSchrodingerPanel() ) );
        getContentPanel().setAnchor( GridBagConstraints.WEST );
        addSpacer();
        addSeparator();
    }
}

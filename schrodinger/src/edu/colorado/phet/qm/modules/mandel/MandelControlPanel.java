/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.controls.ClearButton;
import edu.colorado.phet.qm.controls.ResetButton;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 1:29:46 PM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelControlPanel extends SchrodingerControlPanel {
    public MandelControlPanel( MandelModule mandelModule ) {
        super( mandelModule );
        addControl( new ResetButton( mandelModule ) );
        addControl( new ClearButton( mandelModule.getSchrodingerPanel() ) );
    }
}

package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.piccolo.PhetPCanvas;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 11:15:22 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CCKSimulationPanel extends PhetPCanvas {
    private CCKModel model;

    public CCKSimulationPanel( CCKModel model ) {
        this.model = model;
        setBackground( ICCKModule.BACKGROUND_COLOR );
    }
}

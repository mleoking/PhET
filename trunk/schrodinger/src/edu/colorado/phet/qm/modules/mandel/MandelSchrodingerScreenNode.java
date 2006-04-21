/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 11:13:19 PM
 * Copyright (c) Mar 2, 2006 by Sam Reid
 */

public class MandelSchrodingerScreenNode extends SchrodingerScreenNode {
    public MandelSchrodingerScreenNode( SchrodingerModule module, SchrodingerPanel schrodingerPanel ) {
        super( module, schrodingerPanel );
        getDetectorSheetPNode().setTitle( "Black & White Screen" );
    }

    protected double getWavefunctionGraphicX( double availableWidth ) {
        return 140;
    }
}

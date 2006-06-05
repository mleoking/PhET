/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.piccolo.QWIScreenNode;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 11:13:19 PM
 * Copyright (c) Mar 2, 2006 by Sam Reid
 */

public class MandelSchrodingerScreenNode extends QWIScreenNode {
    public MandelSchrodingerScreenNode( QWIModule module, QWIPanel QWIPanel ) {
        super( module, QWIPanel );
        getDetectorSheetPNode().setTitle( "Black & White Screen" );
    }

    protected double getWavefunctionGraphicX( double availableWidth ) {
        return 140;
    }
}

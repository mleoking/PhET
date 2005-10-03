/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.view.swing.DetectorSheetControlPanel;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 12:54:31 PM
 * Copyright (c) Jul 27, 2005 by Sam Reid
 */

public class DetectorSheetControlPanelPNode extends PNode {
    private DetectorSheetControlPanel detectorSheetControlPanel;

    public DetectorSheetControlPanelPNode( final DetectorSheet detectorSheet ) {
        this.detectorSheetControlPanel = new DetectorSheetControlPanel( detectorSheet );
        PSwing pSwing = new PSwing( detectorSheet.getSchrodingerPanel(), detectorSheetControlPanel );
        addChild( pSwing );
    }

    public void setBrightness() {
        detectorSheetControlPanel.setBrightness();
    }

    public void setClearButtonVisible( boolean b ) {
        detectorSheetControlPanel.setClearButtonVisible( b );
    }

    public void setSaveButtonVisible( boolean b ) {
        detectorSheetControlPanel.setSaveButtonVisible( b );
    }

    public void setBrightnessSliderVisible( boolean b ) {
        detectorSheetControlPanel.setBrightnessSliderVisible( b );
    }

    public void setFadeCheckBoxVisible( boolean b ) {
        detectorSheetControlPanel.setFadeCheckBoxVisible( b );
    }

    public void setTypeControlVisible( boolean b ) {
        detectorSheetControlPanel.setTypeControlVisible( b );
    }
}

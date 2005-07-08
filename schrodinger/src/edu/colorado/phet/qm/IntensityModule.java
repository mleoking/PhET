/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.SplitModel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:06:16 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityModule extends SchrodingerModule {
    private SplitModel splitModel;
    private IntensityPanel intensityPanel;
    private IntensityControlPanel schrodingerControlPanel;

    public IntensityModule( AbstractClock clock ) {
        super( "High Intensity", clock );
        splitModel = new SplitModel( 100, 100 );
        setDiscreteModel( splitModel );
        intensityPanel = new IntensityPanel( this );
        setSchrodingerPanel( intensityPanel );
        schrodingerControlPanel = new IntensityControlPanel( this );
        setSchrodingerControlPanel( schrodingerControlPanel );
    }

    public SplitModel getSplitModel() {
        return splitModel;
    }

    public IntensityPanel getIntensityPanel() {
        return intensityPanel;
    }

    public void setRightDetectorEnabled( boolean selected ) {
        setDetectorEnabled( splitModel.getRightDetector(), selected );
    }

    public void setLeftDetectorEnabled( boolean selected ) {
        setDetectorEnabled( splitModel.getLeftDetector(), selected );
    }

    private void setDetectorEnabled( Detector detector, boolean selected ) {
        if( selected ) {
            splitModel.addDetector( detector );
            intensityPanel.addDetectorGraphic( detector );
        }
        else {
            splitModel.removeDetector( detector );
            intensityPanel.removeDetectorGraphic( detector );
        }
    }
}

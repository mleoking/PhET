/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.SplitModel;
import edu.colorado.phet.qm.view.DetectorGraphic;

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
        synchronizeModel();
    }

    public SplitModel getSplitModel() {
        return splitModel;
    }

    public IntensityPanel getIntensityPanel() {
        return intensityPanel;
    }

    public boolean isRightDetectorEnabled() {
        return splitModel.containsDetector( splitModel.getRightDetector() );
    }

    public boolean isLeftDetectorEnabled() {
        return splitModel.containsDetector( splitModel.getLeftDetector() );
    }

    public void setRightDetectorEnabled( boolean selected ) {
        setDetectorEnabled( splitModel.getRightDetector(), selected );
    }

    public void setLeftDetectorEnabled( boolean selected ) {
        setDetectorEnabled( splitModel.getLeftDetector(), selected );
    }

    private void setDetectorEnabled( Detector detector, boolean selected ) {
        boolean splitMode = shouldBeSplitMode();
        if( selected ) {
            splitModel.addDetector( detector );
            DetectorGraphic detectorGraphic = new RestrictedDetectorGraphic( intensityPanel, detector );
            intensityPanel.addDetectorGraphic( detectorGraphic );
        }
        else {
            splitModel.removeDetector( detector );
            intensityPanel.removeDetectorGraphic( detector );
        }
        boolean newSplitMode = shouldBeSplitMode();
        if( newSplitMode != splitMode ) {
            synchronizeModel();
        }
    }

    private boolean shouldBeSplitMode() {
        return isLeftDetectorEnabled() || isRightDetectorEnabled();
    }

    private void synchronizeModel() {
        boolean splitMode = shouldBeSplitMode();
        splitModel.setSplitModel( splitMode );
        intensityPanel.setSplitMode( splitMode );
    }
}

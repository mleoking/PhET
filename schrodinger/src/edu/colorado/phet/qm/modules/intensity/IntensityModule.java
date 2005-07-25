/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.SplitModel;
import edu.colorado.phet.qm.view.DetectorGraphic;
import edu.colorado.phet.qm.view.RestrictedDetectorGraphic;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;

import java.util.ArrayList;

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
    private ArrayList listeners = new ArrayList();

    public IntensityModule( SchrodingerApplication app ) {
        this( "High Intensity", app );
    }

    protected IntensityModule( String name, SchrodingerApplication app ) {
        super( name, app );
        splitModel = new SplitModel( 100, 100 );
        setDiscreteModel( splitModel );
        intensityPanel = createIntensityPanel();
        setSchrodingerPanel( intensityPanel );
        schrodingerControlPanel = new IntensityControlPanel( this );
        setSchrodingerControlPanel( schrodingerControlPanel );
        synchronizeModel();

        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                if( !getDiscreteModel().isDoubleSlitEnabled() ) {
                    setRightDetectorEnabled( false );
                    setLeftDetectorEnabled( false );
                }
                else {
                    getIntensityPanel().getSlitControlPanel().synchronizeModelState();
                }
            }
        } );


    }

    protected IntensityPanel createIntensityPanel() {
        return new IntensityPanel( this );
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
        if( !getDiscreteModel().isDoubleSlitEnabled() && selected ) {
            return;
        }
        if( isRightDetectorEnabled() != selected ) {
            setDetectorEnabled( splitModel.getRightDetector(), selected );
            notifyDetectorsChanged();
        }
    }

    public void setLeftDetectorEnabled( boolean selected ) {
        if( !getDiscreteModel().isDoubleSlitEnabled() && selected ) {
            return;
        }
        if( isLeftDetectorEnabled() != selected ) {
            setDetectorEnabled( splitModel.getLeftDetector(), selected );
            notifyDetectorsChanged();
        }
    }

    public PhotonColorMap.ColorData getRootColor() {
        return intensityPanel.getRootColor();
    }

    public static interface Listener {

        void detectorsChanged();
    }

    public static class Adapter implements Listener {

        public void detectorsChanged() {
        }
    }


    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifyDetectorsChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.detectorsChanged();
        }
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

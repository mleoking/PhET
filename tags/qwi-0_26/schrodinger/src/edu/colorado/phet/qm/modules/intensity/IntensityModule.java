/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.SplitModel;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.piccolo.DetectorGraphic;
import edu.colorado.phet.qm.view.piccolo.RestrictedDetectorGraphic;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:06:16 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityModule extends SchrodingerModule {
    private SplitModel splitModel;
    private HighIntensitySchrodingerPanel highIntensitySchrodingerPanel;
    private IntensityControlPanel intensityControlPanel;
    private ArrayList listeners = new ArrayList();

    public IntensityModule( SchrodingerApplication app, IClock clock ) {
        this( "High Intensity", app, clock );
    }

    protected IntensityModule( String name, SchrodingerApplication app, IClock clock ) {
        super( name, app, clock );
        splitModel = new SplitModel();
        setDiscreteModel( splitModel );
        highIntensitySchrodingerPanel = createIntensityPanel();
        setSchrodingerPanel( highIntensitySchrodingerPanel );
        intensityControlPanel = new IntensityControlPanel( this );
        setSchrodingerControlPanel( intensityControlPanel );
        synchronizeModel();

        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                if( !getDiscreteModel().isDoubleSlitEnabled() ) {
                    setRightDetectorEnabled( false );
                    setLeftDetectorEnabled( false );
                }
//                else {
//                    getIntensityPanel().getSlitDetectorPanel().synchronizeModelState();
//                }
            }
        } );
        finishInit();

//        getDiscreteModel().addListener( new DebugIntensityReader() );
    }

    protected HighIntensitySchrodingerPanel createIntensityPanel() {
        return new HighIntensitySchrodingerPanel( this );
    }

    public SplitModel getSplitModel() {
        return splitModel;
    }

    public HighIntensitySchrodingerPanel getIntensityPanel() {
        return highIntensitySchrodingerPanel;
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

    public ColorData getRootColor() {
        return highIntensitySchrodingerPanel.getRootColor();
    }

//    public SlitDetectorPanel getSlitDetectorPanel() {
//        return intensityControlPanel.getSlitDetectorPanel();
//    }

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
            DetectorGraphic detectorGraphic = new RestrictedDetectorGraphic( highIntensitySchrodingerPanel, detector );
            highIntensitySchrodingerPanel.addDetectorGraphic( detectorGraphic );
        }
        else {
            splitModel.removeDetector( detector );
            highIntensitySchrodingerPanel.removeDetectorGraphic( detector );
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
        splitModel.setSplitMode( splitMode );
        highIntensitySchrodingerPanel.setSplitMode( splitMode );
    }


}

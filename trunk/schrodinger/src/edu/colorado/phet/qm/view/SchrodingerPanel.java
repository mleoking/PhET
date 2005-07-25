/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.controls.DoubleSlitCheckBox;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.colorado.phet.qm.view.gun.AbstractGun;
import edu.colorado.phet.qm.view.gun.Photon;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerPanel extends ApparatusPanel2 {
    private DiscreteModel discreteModel;
    private SchrodingerModule module;
    private WavefunctionGraphic wavefunctionGraphic;
    private ArrayList rectanglePotentialGraphics = new ArrayList();
    private AbstractGun abstractGun;
    private IntensityDisplay intensityDisplay;
    private RulerGraphic rulerGraphic;
    private ArrayList detectorGraphics = new ArrayList();
    private PhetGraphic doubleSlitCheckBoxGraphic;

    public SchrodingerPanel( SchrodingerModule module ) {
        super( module.getClock() );
        setLayout( null );
        this.module = module;
        this.discreteModel = module.getDiscreteModel();

        wavefunctionGraphic = new WavefunctionGraphic( this );
        addGraphic( wavefunctionGraphic );
        wavefunctionGraphic.setLocation( 100, 50 );

        rulerGraphic = new RulerGraphic( this );
        addGraphic( rulerGraphic, Double.POSITIVE_INFINITY );
        rulerGraphic.setLocation( 20, 20 );
        rulerGraphic.setVisible( false );

        intensityDisplay = new IntensityDisplay( getSchrodingerModule(), this, 60 );
        addGraphic( intensityDisplay, -1 );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setReferenceSize( 600, 600 );
                abstractGun.componentResized( e );
            }
        } );

        getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updateScreen();
            }
        } );

        DoubleSlitCheckBox doubleSlitCheckBox = new DoubleSlitCheckBox( getDiscreteModel() );
        doubleSlitCheckBoxGraphic = PhetJComponent.newInstance( this, doubleSlitCheckBox );
        addGraphic( doubleSlitCheckBoxGraphic );
        doubleSlitCheckBoxGraphic.setLocation( getWavefunctionGraphic().getX() + getWavefunctionGraphic().getWidth(), getWavefunctionGraphic().getY() + getWavefunctionGraphic().getHeight() / 2 );
    }

    protected PhetGraphic getDoubleSlitCheckBoxGraphic() {
        return doubleSlitCheckBoxGraphic;
    }

    protected void updateScreen() {
        intensityDisplay.tryDetecting();
    }

    protected void setGunGraphic( AbstractGun abstractGun ) {
        if( abstractGun != null ) {
            removeGraphic( abstractGun );
        }
        this.abstractGun = abstractGun;
        addGraphic( abstractGun );
        abstractGun.setLocation( wavefunctionGraphic.getX() + wavefunctionGraphic.getWidth() / 2 - abstractGun.getGunWidth() / 2,
                                 wavefunctionGraphic.getY() + wavefunctionGraphic.getHeight() - getGunGraphicOffsetY() );
    }

    private int getGunGraphicOffsetY() {
        return 50;
    }

    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );
        paintChildren( graphics );//to enuse swing components are shown.
    }

    public void setRulerVisible( boolean rulerVisible ) {
        rulerGraphic.setVisible( rulerVisible );
    }

    public void reset() {
        intensityDisplay.reset();
    }

    public void updateGraphics() {
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public void addDetectorGraphic( DetectorGraphic detectorGraphic ) {
        detectorGraphics.add( detectorGraphic );
        addGraphic( detectorGraphic );
    }

    public void addRectangularPotentialGraphic( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        rectanglePotentialGraphics.add( rectangularPotentialGraphic );
        addGraphic( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        for( int i = 0; i < rectanglePotentialGraphics.size(); i++ ) {
            RectangularPotentialGraphic rectangularPotentialGraphic = (RectangularPotentialGraphic)rectanglePotentialGraphics.get( i );
            removeGraphic( rectangularPotentialGraphic );
        }
        rectanglePotentialGraphics.clear();
    }

    public WavefunctionGraphic getWavefunctionGraphic() {
        return wavefunctionGraphic;
    }

    public SchrodingerModule getSchrodingerModule() {
        return module;
    }

    public IntensityDisplay getIntensityDisplay() {
        return intensityDisplay;
    }

    public RulerGraphic getRulerGraphic() {
        return rulerGraphic;
    }

    public AbstractGun getGunGraphic() {
        return abstractGun;
    }

    public void removeDetectorGraphic( DetectorGraphic detectorGraphic ) {
        removeGraphic( detectorGraphic );
        getDiscreteModel().removeDetector( detectorGraphic.getDetector() );
        detectorGraphics.remove( detectorGraphic );
    }

    public void addDetectorGraphic( Detector detector ) {
        DetectorGraphic detectorGraphic = new DetectorGraphic( this, detector );
        addDetectorGraphic( detectorGraphic );
    }

    public DetectorGraphic getDetectorGraphic( Detector detector ) {
        for( int i = 0; i < detectorGraphics.size(); i++ ) {
            DetectorGraphic detectorGraphic = (DetectorGraphic)detectorGraphics.get( i );
            if( detectorGraphic.getDetector() == detector ) {
                return detectorGraphic;
            }
        }
        return null;
    }

    public void removeDetectorGraphic( Detector detector ) {
        DetectorGraphic detectorGraphic = getDetectorGraphic( detector );
        removeDetectorGraphic( detectorGraphic );
    }

    public void setDisplayPhotonColor( Photon photon ) {
        getWavefunctionGraphic().getMagnitudeMap().setPhoton( photon );
        getIntensityDisplay().setDisplayPhotonColor( photon );
    }
}

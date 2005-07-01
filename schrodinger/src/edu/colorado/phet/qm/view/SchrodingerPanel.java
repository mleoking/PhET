/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.qm.IntensityDisplay;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.phetcommon.RulerGraphic;

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
    private GunGraphic gunGraphic;
    private IntensityDisplay intensityDisplay;
    private RulerGraphic rulerGraphic;

    public SchrodingerPanel( SchrodingerModule module ) {
        super( module.getClock() );
        setLayout( null );
        this.module = module;
        this.discreteModel = module.getDiscreteModel();

        wavefunctionGraphic = new WavefunctionGraphic( this );
        addGraphic( wavefunctionGraphic );
        wavefunctionGraphic.setLocation( 100, 50 );

        gunGraphic = new GunGraphic( this );
        addGraphic( gunGraphic );
        gunGraphic.setLocation( wavefunctionGraphic.getX() + wavefunctionGraphic.getWidth() / 2 - gunGraphic.getGunWidth() / 2,
                                wavefunctionGraphic.getY() + wavefunctionGraphic.getHeight() - getGunGraphicOffsetY() );

        rulerGraphic = new RulerGraphic( this );
        addGraphic( rulerGraphic, Double.POSITIVE_INFINITY );
        rulerGraphic.setLocation( 20, 20 );
        rulerGraphic.setVisible( false );

        intensityDisplay = new IntensityDisplay( getSchrodingerModule(), this, 60 );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setReferenceSize( 600, 600 );
                gunGraphic.componentResized( e );
            }
        } );
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

    public GunGraphic getGunGraphic() {
        return gunGraphic;
    }

    public void removeDetector( DetectorGraphic detectorGraphic ) {
        removeGraphic( detectorGraphic );
        getDiscreteModel().removeDetector( detectorGraphic.getDetector() );
    }
}

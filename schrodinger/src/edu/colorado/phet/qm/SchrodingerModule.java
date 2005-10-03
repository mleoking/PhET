/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.piccolo.IntensityGraphic;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerModule extends PiccoloModule {
    private SchrodingerPanel schrodingerPanel;
    private DiscreteModel discreteModel;
    private SchrodingerControlPanel schrodingerControlPanel;
    private SchrodingerApplication schrodingerApplication;
    private SchrodingerMenu menu;

    /**
     * @param schrodingerApplication
     */
    public SchrodingerModule( String name, SchrodingerApplication schrodingerApplication ) {
        super( name, schrodingerApplication.getClock() );
        this.schrodingerApplication = schrodingerApplication;
        setModel( new BaseModel() );


    }

    protected void finishInit() {
        getSchrodingerPanel().addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_I ) {
                    System.out.println( "SingleParticleModule.keyPressed, I" );
                    resetViewTransform();
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        menu = new SchrodingerMenu( this );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        app.getPhetFrame().addMenu( menu );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        app.getPhetFrame().removeMenu( menu );
    }

    protected void setDiscreteModel( DiscreteModel model ) {
        if( discreteModel != null ) {
            getModel().removeModelElement( discreteModel );
        }
        discreteModel = model;
        addModelElement( discreteModel );
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public void reset() {
        clearPotential();
        discreteModel.reset();
        schrodingerPanel.reset();
        resetViewTransform();
    }

    protected void resetViewTransform() {
        getSchrodingerPanel().getCamera().setViewTransform( new AffineTransform() );
        getSchrodingerPanel().getSchrodingerScreenNode().animateViewToCenter();
    }

    public void fireParticle( WaveSetup waveSetup ) {
        discreteModel.fireParticle( waveSetup );
        schrodingerPanel.updateGraphics();
    }

    public void setGridSpacing( final int nx, final int ny ) {
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                discreteModel.setGridSpacing( nx, ny );
                getModel().removeModelElement( this );
            }
        } );
    }

    public void addDetector() {
        Detector detector = new Detector( getDiscreteModel(), 5, 5, 10, 10 );
        addDetector( detector );
    }

    public void addDetector( Detector detector ) {
        discreteModel.addDetector( detector );
        schrodingerPanel.addDetectorGraphic( detector );
    }

    public void addPotential() {
        RectangularPotential rectangularPotential = new RectangularPotential( 5, 20, 10, 10 );
        rectangularPotential.setPotential( Double.MAX_VALUE / 100.0 );
        discreteModel.addPotential( rectangularPotential );//todo should be a composite.
        RectangularPotentialGraphic rectangularPotentialGraphic = new RectangularPotentialGraphic( getSchrodingerPanel(), rectangularPotential );
        getSchrodingerPanel().addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    public SchrodingerControlPanel getSchrodingerControlPanel() {
        return schrodingerControlPanel;
    }

    public IntensityGraphic getIntensityDisplay() {
        return getSchrodingerPanel().getIntensityDisplay();
    }

    protected void setSchrodingerPanel( SchrodingerPanel schrodingerPanel ) {
        setPhetPCanvas( schrodingerPanel );
        this.schrodingerPanel = schrodingerPanel;
    }

    protected void setSchrodingerControlPanel( SchrodingerControlPanel schrodingerControlPanel ) {
        setControlPanel( schrodingerControlPanel );
        this.schrodingerControlPanel = schrodingerControlPanel;
    }

    public PhetFrame getPhetFrame() {
        return schrodingerApplication.getPhetFrame();
    }

    public void removePotential( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        getDiscreteModel().removePotential( rectangularPotentialGraphic.getPotential() );
        getSchrodingerPanel().removePotentialGraphic( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        getDiscreteModel().clearPotential();
        getSchrodingerPanel().clearPotential();
    }

    public void setWaveSize( int size ) {
        getDiscreteModel().setWaveSize( size, size );
        getSchrodingerPanel().setWaveSize( size, size );
    }
}

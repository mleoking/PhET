/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityGraphic;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

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
    private SchrodingerOptionsMenu optionsMenu;

    /**
     * @param schrodingerApplication
     */
    public SchrodingerModule( String name, SchrodingerApplication schrodingerApplication, IClock clock ) {
        super( name, clock );
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
        optionsMenu = new SchrodingerOptionsMenu( this );
    }

    public void activate() {
        super.activate();
        schrodingerApplication.getPhetFrame().addMenu( optionsMenu );
    }

    public void deactivate() {
        super.deactivate();
        schrodingerApplication.getPhetFrame().removeMenu( optionsMenu );
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
        int x = random.nextInt( getDiscreteModel().getWavefunction().getWidth() - 10 );
        int y = random.nextInt( getDiscreteModel().getWavefunction().getHeight() - 10 );

        Detector detector = new Detector( getDiscreteModel(), x, y, 10, 10 );
        addDetector( detector );
    }

    public void addDetector( Detector detector ) {
        discreteModel.addDetector( detector );
        schrodingerPanel.addDetectorGraphic( detector );
    }

    static final Random random = new Random( 0 );

    public void addPotential() {
        int x = random.nextInt( getDiscreteModel().getWavefunction().getWidth() - 10 );
        int y = random.nextInt( getDiscreteModel().getWavefunction().getHeight() - 10 );
        RectangularPotential rectangularPotential = new RectangularPotential( x, y, 10, 10 );
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

    public void setWaveSize( final int size ) {
        System.out.println( "Request to set size: " + size );
        Command cmd = new Command() {
            public void doIt() {
                System.out.println( "Setting size = " + size );
                getDiscreteModel().setWaveSize( size, size );
                getSchrodingerPanel().setWaveSize( size, size );
            }
        };
        if( !getClock().isPaused() ) {
            getModel().execute( cmd );
            System.out.println( "resizing in model thread" );
        }
        else {
            System.out.println( "Resizing immedialely." );
            cmd.doIt();
        }
    }

    public Map getModelParameters() {
        Hashtable modelParameters = new Hashtable();
        modelParameters.putAll( getDiscreteModel().getModelParameters() );

        AbstractGunGraphic gun = getSchrodingerPanel().getGunGraphic();
        Map parameters = gun.getModelParameters();
        modelParameters.putAll( parameters );

        return modelParameters;
    }
}

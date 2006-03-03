/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
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
    private PhetApplication schrodingerApplication;
    private SchrodingerOptionsMenu optionsMenu;
    private ParticleUnits particleUnits;
    private ArrayList listeners = new ArrayList();

    /**
     * @param schrodingerApplication
     */
    public SchrodingerModule( String name, PhetApplication schrodingerApplication, final IClock clock ) {
        super( name, clock );
        this.schrodingerApplication = schrodingerApplication;
        setModel( new BaseModel() );
        setLogoPanelVisible( false );
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
        getDiscreteModel().getDoubleSlitPotential().addListener( new HorizontalDoubleSlit.Listener() {
            public void slitChanged() {
                getSchrodingerPanel().updateWaveGraphic();
            }
        } );
    }

    public void activate() {
        super.activate();
        schrodingerApplication.getPhetFrame().addMenu( optionsMenu );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.activated();
        }
    }

    public void deactivate() {
        super.deactivate();
        schrodingerApplication.getPhetFrame().removeMenu( optionsMenu );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.deactivated();
        }
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

    public void setGridSize( final int nx, final int ny ) {
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                discreteModel.setGridSize( nx, ny );
                getModel().removeModelElement( this );
            }
        } );
    }

    boolean firstDetector = true;

    public void addDetector() {
        int detectorWidth = 10;
        int detectorHeight = detectorWidth;

        int x = random.nextInt( getDiscreteModel().getWavefunction().getWidth() - detectorWidth );
        int y = random.nextInt( getDiscreteModel().getWavefunction().getHeight() - detectorHeight );
        if( firstDetector ) {
            x = getDiscreteModel().getWavefunction().getWidth() / 2 - detectorWidth / 2;
            y = getDiscreteModel().getWavefunction().getHeight() / 2;
            firstDetector = false;
        }

        Detector detector = new Detector( getDiscreteModel(), x, y, detectorWidth, detectorHeight );
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
        RectangularPotential rectangularPotential = new RectangularPotential( getDiscreteModel(), x, y, 10, 10 );
        rectangularPotential.setPotential( Double.MAX_VALUE / 100.0 );
        discreteModel.addPotential( rectangularPotential );//todo should be a composite.
        RectangularPotentialGraphic rectangularPotentialGraphic = new RectangularPotentialGraphic( getSchrodingerPanel(), rectangularPotential );
        getSchrodingerPanel().addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    public IntensityManager getIntensityDisplay() {
        return getSchrodingerPanel().getIntensityDisplay();
    }

    protected void setSchrodingerPanel( SchrodingerPanel schrodingerPanel ) {
        setPhetPCanvas( schrodingerPanel );
        this.schrodingerPanel = schrodingerPanel;
        this.schrodingerPanel.setUnits( particleUnits );
    }

    protected void setSchrodingerControlPanel( SchrodingerControlPanel schrodingerControlPanel ) {
        setControlPanel( schrodingerControlPanel );
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
        Command cmd = new Command() {
            public void doIt() {
                getDiscreteModel().setWaveSize( size, size );
                getSchrodingerPanel().setWaveSize( size, size );
            }
        };
        if( !getClock().isPaused() ) {
            getModel().execute( cmd );
        }
        else {
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

    public void removeAllDetectors() {
        while( discreteModel.getDetectorSet().numDetectors() > 0 ) {
            getSchrodingerPanel().removeDetectorGraphic( discreteModel.getDetectorSet().detectorAt( 0 ) );
        }
    }

    public void removeAllPotentialBarriers() {
        while( discreteModel.getCompositePotential().numPotentials() > 0 ) {
            getDiscreteModel().removePotential( discreteModel.getCompositePotential().potentialAt( 0 ) );
        }
    }

    public void setUnits( ParticleUnits particleUnits ) {
        System.out.println( "particleUnits = " + particleUnits );
        this.particleUnits = particleUnits;
        if( schrodingerPanel != null ) {
            schrodingerPanel.setUnits( particleUnits );
        }
    }

    public boolean confirmReset() {
        int answer = JOptionPane.showConfirmDialog( getPhetFrame(), "Are you sure you want to reset everything?" );
        return answer == JOptionPane.YES_OPTION;
    }


    public void setCellSize( int size ) {
        schrodingerPanel.setCellSize( size );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static interface Listener {
        void deactivated();

        void activated();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}

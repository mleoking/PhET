/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.qm.controls.ResolutionControl;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;

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
    private QWIModel qwiModel;
    private PhetApplication schrodingerApplication;
    private SchrodingerOptionsMenu optionsMenu;
    private ParticleUnits particleUnits;
    private ArrayList listeners = new ArrayList();
    private ResolutionControl.ResolutionSetup resolution;

    /**
     * @param schrodingerApplication
     */
    public SchrodingerModule( String name, PhetApplication schrodingerApplication, final IClock clock ) {
        super( name, clock );
        this.schrodingerApplication = schrodingerApplication;
        this.resolution = getResolutionSetups()[0];
        setModel( new BaseModel() );
        setLogoPanelVisible( false );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                SchrodingerModule.this.stepInTime( dt );
            }
        } );
    }

    private void stepInTime( double dt ) {
        if( qwiModel != null ) {
            qwiModel.stepInTime( dt );
        }
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

    protected void setDiscreteModel( QWIModel model ) {
        this.qwiModel = model;
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    public QWIModel getDiscreteModel() {
        return qwiModel;
    }

    public void reset() {
        clearPotential();
        qwiModel.reset();
        schrodingerPanel.reset();
        resetViewTransform();
    }

    protected void resetViewTransform() {
        getSchrodingerPanel().getCamera().setViewTransform( new AffineTransform() );
    }

    public void fireParticle( WaveSetup waveSetup ) {
        qwiModel.fireParticle( waveSetup );
        schrodingerPanel.updateGraphics();
    }

    public void setGridSize( final int nx, final int ny ) {
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                qwiModel.setGridSize( nx, ny );
                getModel().removeModelElement( this );
            }
        } );
    }

    boolean firstDetector = true;

    public void addDetector() {
        int detectorWidth = (int)( getDiscreteModel().getGridWidth() / 4.5 );
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
        qwiModel.addDetector( detector );
        schrodingerPanel.addDetectorGraphic( detector );
    }

    static final Random random = new Random( 0 );

    public void addPotential() {
        int w = (int)( getDiscreteModel().getGridWidth() / 4.5 );
        int x = random.nextInt( getDiscreteModel().getWavefunction().getWidth() - w );
        int y = random.nextInt( getDiscreteModel().getWavefunction().getHeight() - w );

        RectangularPotential rectangularPotential = new RectangularPotential( getDiscreteModel(), x, y, w, w );
        rectangularPotential.setPotential( Double.MAX_VALUE / 100.0 );
        qwiModel.addPotential( rectangularPotential );//todo should be a composite.
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
        getDiscreteModel().clearPotentialIgnoreSlits();
        getSchrodingerPanel().clearPotential();
    }

    public void setWaveSize( final int size ) {
//        System.out.println( "Set wave size= " + size );
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
        while( qwiModel.getDetectorSet().numDetectors() > 0 ) {
            getSchrodingerPanel().removeDetectorGraphic( qwiModel.getDetectorSet().detectorAt( 0 ) );
        }
    }

    public void removeAllPotentialBarriers() {
        while( qwiModel.getCompositePotential().numPotentials() > 0 ) {
            getDiscreteModel().removePotential( qwiModel.getCompositePotential().potentialAt( 0 ) );
        }
    }

    public void setUnits( ParticleUnits particleUnits ) {
//        System.out.println( "particleUnits = " + particleUnits );
        this.particleUnits = particleUnits;
        if( schrodingerPanel != null ) {
            schrodingerPanel.setUnits( particleUnits );
        }
    }

    public boolean confirmReset() {
        return true;
//        int answer = JOptionPane.showConfirmDialog( getPhetFrame(), "Are you sure you want to reset everything?" );
//        return answer == JOptionPane.YES_OPTION;
    }


    public void setCellSize( int size ) {
        schrodingerPanel.setCellSize( size );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void beamTypeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.beamTypeChanged();
        }
    }

    public static ResolutionControl.ResolutionSetup[]getResolutionSetups() {
        int[]configFor1024x768 = new int[]{8, 4, 2};
        String[]names = new String[]{"low", "medium", "high"};
        double[]lightFudge = new double[]{1, 0.5, 0.25};
        double[]particleFudge = new double[]{1, 1.0 / 4.0, 1.0 / 16.0};
//        Integer[]v = new Integer[configFor1024x768.length];
        ResolutionControl.ResolutionSetup[] resolutionSetup = new ResolutionControl.ResolutionSetup[configFor1024x768.length];
        for( int i = 0; i < resolutionSetup.length; i++ ) {
//            resolutionSetup[i] = new Integer( configFor1024x768[i] );
            resolutionSetup[i] = new ResolutionControl.ResolutionSetup( configFor1024x768[i], names[i], lightFudge[i], particleFudge[i] );
        }
        return resolutionSetup;
    }

    public ResolutionControl.ResolutionSetup getResolution() {
        return resolution;
    }

    public void setResolution( ResolutionControl.ResolutionSetup value ) {
        this.resolution = value;
    }

    public static interface Listener {
        void deactivated();

        void activated();

        void beamTypeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public boolean hasHelp() {
        return false;
    }
}

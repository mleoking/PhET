/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.qm.controls.QWIControlPanel;
import edu.colorado.phet.qm.controls.ResolutionControl;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.gun.AbstractGunNode;
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

public class QWIModule extends PiccoloModule {
    private QWIPanel QWIPanel;
    private QWIModel qwiModel;
    private PhetApplication schrodingerApplication;
    private QWIOptionsMenu optionsMenu;
    private ParticleUnits particleUnits;
    private ArrayList listeners = new ArrayList();
    private ResolutionControl.ResolutionSetup resolution;
    static final Random random = new Random( 0 );

    /**
     * @param schrodingerApplication
     */
    public QWIModule( String name, PhetApplication schrodingerApplication, final IClock clock ) {
        super( name, clock );
        this.schrodingerApplication = schrodingerApplication;
        this.resolution = getResolutionSetups()[0];
        setModel( new BaseModel() );
        setLogoPanelVisible( false );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                QWIModule.this.stepInTime( dt );
            }
        } );
    }

    protected void stepInTime( double dt ) {
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
        optionsMenu = createOptionsMenu();
        getQWIModel().getDoubleSlitPotential().addListener( new HorizontalDoubleSlit.Listener() {
            public void slitChanged() {
                getSchrodingerPanel().updateWaveGraphic();
            }
        } );
    }

    protected QWIOptionsMenu createOptionsMenu() {
        return new QWIOptionsMenu( this );
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

    protected void setQWIModel( QWIModel model ) {
        this.qwiModel = model;
        qwiModel.getDetectorSet().addListener( new DetectorSet.Listener() {
            public void detectionAttempted() {
                getSchrodingerPanel().updateWaveGraphic();
            }
        } );
//        System.out.println( "model = " + model );
    }

    public QWIPanel getSchrodingerPanel() {
        return QWIPanel;
    }

    public QWIModel getQWIModel() {
        return qwiModel;
    }

    public void reset() {
        clearPotential();
        qwiModel.reset();
        QWIPanel.reset();
        resetViewTransform();
    }

    protected void resetViewTransform() {
        getSchrodingerPanel().getCamera().setViewTransform( new AffineTransform() );
    }

    public void fireParticle( WaveSetup waveSetup ) {
        qwiModel.fireParticle( waveSetup );
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
        int detectorWidth = (int)( getQWIModel().getGridWidth() / 4.5 );
        int detectorHeight = detectorWidth;

        int x = random.nextInt( getQWIModel().getWavefunction().getWidth() - detectorWidth );
        int y = random.nextInt( getQWIModel().getWavefunction().getHeight() - detectorHeight );
        if( firstDetector ) {
            x = getQWIModel().getWavefunction().getWidth() / 2 - detectorWidth / 2;
            y = getQWIModel().getWavefunction().getHeight() / 2;
            firstDetector = false;
        }

        Detector detector = new Detector( getQWIModel(), x, y, detectorWidth, detectorHeight );
        addDetector( detector );
    }

    public void addDetector( Detector detector ) {
        qwiModel.addDetector( detector );
        QWIPanel.addDetectorGraphic( detector );
    }

    public void addPotential() {
        int w = (int)( getQWIModel().getGridWidth() / 4.5 );
        int x = random.nextInt( getQWIModel().getWavefunction().getWidth() - w );
        int y = random.nextInt( getQWIModel().getWavefunction().getHeight() - w );

        RectangularPotential rectangularPotential = new RectangularPotential( getQWIModel(), x, y, w, w );
        rectangularPotential.setPotential( Double.MAX_VALUE / 100.0 );
        qwiModel.addPotential( rectangularPotential );//todo should be a composite.
        RectangularPotentialGraphic rectangularPotentialGraphic = createPotentialGraphic( rectangularPotential );
        getSchrodingerPanel().addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    protected RectangularPotentialGraphic createPotentialGraphic( RectangularPotential rectangularPotential ) {
        return new RectangularPotentialGraphic( getSchrodingerPanel(), rectangularPotential );
    }

    public IntensityManager getIntensityDisplay() {
        return getSchrodingerPanel().getIntensityDisplay();
    }

    protected void setSchrodingerPanel( QWIPanel QWIPanel ) {
        setSimulationPanel( QWIPanel );
        this.QWIPanel = QWIPanel;
        this.QWIPanel.setUnits( particleUnits );
    }

    protected void setSchrodingerControlPanel( QWIControlPanel qwiControlPanel ) {
        setControlPanel( qwiControlPanel );
    }

    public PhetFrame getPhetFrame() {
        return schrodingerApplication.getPhetFrame();
    }

    public void removePotential( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        getQWIModel().removePotential( rectangularPotentialGraphic.getPotential() );
        getSchrodingerPanel().removePotentialGraphic( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        getQWIModel().clearPotentialIgnoreSlits();
        getSchrodingerPanel().clearPotential();
    }

    public void setWaveSize( final int size ) {
//        System.out.println( "Set wave size= " + size );
        Command cmd = new Command() {
            public void doIt() {
                getQWIModel().setWaveSize( size, size );
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
        modelParameters.putAll( getQWIModel().getModelParameters() );

        AbstractGunNode gun = getSchrodingerPanel().getGunGraphic();
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
            getQWIModel().removePotential( qwiModel.getCompositePotential().potentialAt( 0 ) );
        }
    }

    public void setUnits( ParticleUnits particleUnits ) {
        this.particleUnits = particleUnits;
        if( QWIPanel != null ) {
            QWIPanel.setUnits( particleUnits );
        }
    }

    public boolean confirmReset() {
        return true;
//        int answer = JOptionPane.showConfirmDialog( getPhetFrame(), "Are you sure you want to reset everything?" );
//        return answer == JOptionPane.YES_OPTION;
    }


    public void setCellSize( int size ) {
        QWIPanel.setCellSize( size );
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

    public static ResolutionControl.ResolutionSetup[] getResolutionSetups() {
        int[] configFor1024x768 = new int[]{8, 4, 2};
        String[] names = new String[]{"low", "medium", "high"};
        double[] lightFudge = new double[]{1, 0.5, 0.25};
        double[] particleFudge = new double[]{1, 1.0 / 4.0, 1.0 / 16.0};
        ResolutionControl.ResolutionSetup[] resolutionSetup = new ResolutionControl.ResolutionSetup[configFor1024x768.length];
        for( int i = 0; i < resolutionSetup.length; i++ ) {
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

    public void setMinimumProbabilityForDetection( double minimumProbabilityForDetection ) {
        getSchrodingerPanel().getIntensityDisplay().setMinimumProbabilityForDetection( minimumProbabilityForDetection );
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

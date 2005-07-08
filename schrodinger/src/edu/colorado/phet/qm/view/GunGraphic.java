/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.propagators.FiniteDifferencePropagator2ndOrder;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class GunGraphic extends GraphicLayerSet {
    private SchrodingerPanel schrodingerPanel;
    private PhetImageGraphic gunImageGraphic;
    private JComboBox comboBox;
    private GunItem currentObject;
    private GunItem[] items;

    public GunGraphic( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        this.schrodingerPanel = schrodingerPanel;
        gunImageGraphic = new PhetImageGraphic( getComponent(), "images/laser.gif" );
        addGraphic( gunImageGraphic );
        items = new GunItem[]{
            new Photon( this, "Photons", "images/photon-thumb.jpg" ),
            new Electron( this, "Electrons", "images/electron-thumb.jpg" ),
            new Atom( this, "Atoms", "images/atom-thumb.jpg" )};

        comboBox = createParticleTypeSelectorComboBox();
        schrodingerPanel.add( comboBox );

        setVisible( true );
        setupObject( items[0] );
    }

    public void fireParticle() {
        //add the specified wavefunction everywhere, then renormalize..?
        //clear the old wavefunction.

        currentObject.fireParticle();
//        lastWave = currentObject.getInitialWavefunction( getDiscreteModel().getWavefunction() );
    }

    DiscreteModel getDiscreteModel() {
        return schrodingerPanel.getDiscreteModel();
    }

    public void setLocation( int x, int y ) {
        super.setLocation( x, y );
        double scaleX = schrodingerPanel.getGraphicTx().getScaleX();
        double scaleY = schrodingerPanel.getGraphicTx().getScaleY();
        comboBox.setBounds( (int)( ( x - comboBox.getPreferredSize().width - 2 ) * scaleX ), (int)( y * scaleY ),
                            comboBox.getPreferredSize().width, comboBox.getPreferredSize().height );
        System.out.println( "comboBox.getLocation() = " + comboBox.getLocation() );
        setupObject( currentObject );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        comboBox.setVisible( visible );
    }

    public void componentResized( ComponentEvent e ) {
        this.setLocation( getLocation() );//to fix combobox
    }

    public PhetImageGraphic getGunImageGraphic() {
        return gunImageGraphic;
    }

    public static interface MomentumChangeListener {
        void momentumChanged( double val );
    }

    public void addMomentumChangeListener( MomentumChangeListener momentumChangeListener ) {
        for( int i = 0; i < items.length; i++ ) {
            items[i].addMomentumChangeListerner( momentumChangeListener );
        }
    }

    static abstract class GunItem extends ImageComboBox.Item {
        protected GunGraphic gunGraphic;
        private ArrayList momentumChangeListeners = new ArrayList();

        public GunItem( GunGraphic gunGraphic, String label, String imageLocation ) {
            super( label, imageLocation );
            this.gunGraphic = gunGraphic;
        }

        public abstract void setup( GunGraphic gunGraphic );

        public abstract void teardown( GunGraphic gunGraphic );

        public GunGraphic getGunGraphic() {
            return gunGraphic;
        }

        public abstract double getStartPy();

        public WaveSetup getInitialWavefunction( Wavefunction currentWave ) {
            double x = getDiscreteModel().getGridWidth() * 0.5;
            double y = getStartY();
            double px = 0;
            double py = getStartPy();

            Point phaseLockPoint = new Point( (int)x, (int)( y + 5 ) );

            double dxLattice = getStartDxLattice();
            GaussianWave waveSetup = new GaussianWave( new Point( (int)x, (int)y ),
                                                       new Vector2D.Double( px, py ), dxLattice );

            double desiredPhase = currentWave.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();

            Wavefunction copy = currentWave.createEmptyWavefunction();
            waveSetup.initialize( copy );

            double uneditedPhase = copy.valueAt( phaseLockPoint.x, phaseLockPoint.y ).getComplexPhase();
            double deltaPhase = desiredPhase - uneditedPhase;

            waveSetup.setPhase( deltaPhase );

            return waveSetup;
        }

        protected double getStartY() {
            double y = getDiscreteModel().getGridHeight() * 0.8;
            return y;
        }

        private WaveSetup getInitialWavefunctionVerifyCorrect( Wavefunction currentWave ) {
            double x = getDiscreteModel().getGridWidth() * 0.5;
            double y = getStartY();
            double px = 0;
            double py = getStartPy();
            System.out.println( "py = " + py );

            Point phaseLockPoint = new Point( (int)x, (int)( y + 5 ) );

            double dxLattice = getStartDxLattice();
            System.out.println( "dxLattice = " + dxLattice );
            GaussianWave waveSetup = new GaussianWave( new Point( (int)x, (int)y ),
                                                       new Vector2D.Double( px, py ), dxLattice );

            Complex centerValue = currentWave.valueAt( phaseLockPoint.x, phaseLockPoint.y );
            double desiredPhase = centerValue.getComplexPhase();

            System.out.println( "original Center= " + centerValue + ", desired phase=" + desiredPhase );

            Wavefunction copy = currentWave.createEmptyWavefunction();
            waveSetup.initialize( copy );

            Complex centerValueCopy = copy.valueAt( phaseLockPoint.x, phaseLockPoint.y );
            System.out.println( "unedited: =" + centerValueCopy + ", unedited phase=" + centerValueCopy.getComplexPhase() );

            double uneditedPhase = centerValueCopy.getComplexPhase();
            double deltaPhase = desiredPhase - uneditedPhase;

            System.out.println( "deltaPhase = " + deltaPhase );
            waveSetup.setPhase( deltaPhase );

            Wavefunction test = currentWave.createEmptyWavefunction();
            waveSetup.initialize( test );
            Complex testValue = test.valueAt( phaseLockPoint.x, phaseLockPoint.y );
            System.out.println( "created testValue = " + testValue + ", created phase=" + testValue.getComplexPhase() );

            return waveSetup;
        }

        protected void clearWavefunction() {
            getDiscreteModel().clearWavefunction();
        }

        public void fireParticle() {
            WaveSetup initialWavefunction = getInitialWavefunction( getDiscreteModel().getWavefunction() );
            getSchrodingerModule().fireParticle( initialWavefunction );
        }

        private SchrodingerModule getSchrodingerModule() {
            return gunGraphic.getSchrodingerModule();
        }

        protected DiscreteModel getDiscreteModel() {
            return getSchrodingerModule().getDiscreteModel();
        }

        protected double getStartDxLattice() {
//            double dxLattice = 0.04 * getDiscreteModel().getGridWidth();
            double dxLattice = 0.06 * getDiscreteModel().getGridWidth();
            return dxLattice;
        }

        public void addMomentumChangeListerner( MomentumChangeListener momentumChangeListener ) {
            momentumChangeListeners.add( momentumChangeListener );
            hookupListener( momentumChangeListener );
        }

        protected abstract void hookupListener( MomentumChangeListener momentumChangeListener );

        protected void notifyMomentumChanged() {
            double momentum = getStartPy();
            for( int i = 0; i < momentumChangeListeners.size(); i++ ) {
                MomentumChangeListener momentumChangeListener = (MomentumChangeListener)momentumChangeListeners.get( i );
                momentumChangeListener.momentumChanged( momentum );
            }
        }

    }

    private SchrodingerModule getSchrodingerModule() {
        return schrodingerPanel.getSchrodingerModule();
    }

    static class Electron extends GunItem {
        public PhetGraphic graphic;
        public JSlider velocity;
        private double electronMass = 1.0;

        public Electron( GunGraphic gunGraphic, String label, String imageLocation ) {
            super( gunGraphic, label, imageLocation );
            velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
            velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );
            graphic = PhetJComponent.newInstance( gunGraphic.getComponent(), velocity );
        }

        public void setup( GunGraphic gunGraphic ) {
            gunGraphic.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();

            gunGraphic.addGraphic( graphic );
            graphic.setLocation( -graphic.getWidth() - 2, gunGraphic.getComboBox().getHeight() + 2 );
        }


        public void teardown( GunGraphic gunGraphic ) {
            gunGraphic.removeGraphic( graphic );
        }

        public double getStartPy() {
            double velocityValue = new Function.LinearFunction( 0, 1000, 0, 1.5 ).evaluate( velocity.getValue() );
            return -velocityValue * electronMass;
        }

        protected void hookupListener( MomentumChangeListener momentumChangeListener ) {
            velocity.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    notifyMomentumChanged();
                }
            } );
        }
    }

    private JComboBox getComboBox() {
        return comboBox;
    }

    static Potential getPotential( GunGraphic gunGraphic ) {
        return gunGraphic.schrodingerPanel.getDiscreteModel().getPotential();
    }

    static class Photon extends GunItem {
        private JSlider wavelength;
        private PhetGraphic wavelengthSliderGraphic;
        private double hbar = 1.0;

        public Photon( GunGraphic gunGraphic, String label, String imageLocation ) {
            super( gunGraphic, label, imageLocation );
            wavelength = new JSlider( JSlider.HORIZONTAL, 8, 500, 500 / 2 );
            wavelength.setBorder( BorderFactory.createTitledBorder( "Wavelength" ) );
            wavelengthSliderGraphic = PhetJComponent.newInstance( gunGraphic.getComponent(), wavelength );
        }

        public void setup( GunGraphic gunGraphic ) {
            gunGraphic.getSchrodingerModule().getDiscreteModel().setPropagatorClassical();
            gunGraphic.addGraphic( wavelengthSliderGraphic );
            wavelengthSliderGraphic.setLocation( -wavelengthSliderGraphic.getWidth() - 2, gunGraphic.getComboBox().getPreferredSize().height + 2 );
        }

        public void teardown( GunGraphic gunGraphic ) {
            gunGraphic.removeGraphic( wavelengthSliderGraphic );
        }

        public void fireParticle() {

            Propagator propagator = gunGraphic.getDiscreteModel().getPropagator();
            if( propagator instanceof FiniteDifferencePropagator2ndOrder ) {
                FiniteDifferencePropagator2ndOrder prop = (FiniteDifferencePropagator2ndOrder)propagator;
                WaveSetup setup = getInitialWavefunction( gunGraphic.getDiscreteModel().getWavefunction() );
                Wavefunction init = gunGraphic.getDiscreteModel().getWavefunction().createEmptyWavefunction();
                setup.initialize( init );
//                new RandomizePhase().randomizePhase( init );
//                int numAvg = 5;
//                for( int i = 0; i < numAvg; i++ ) {
//                    new AveragePropagator().propagate( init );
//                }
//
//                init.setMagnitude( 1.0 );
                prop.addInitialization( init, init );
            }
            super.fireParticle();

        }

        protected double getStartY() {
            return getDiscreteModel().getGridHeight() * 0.9;
        }

        public double getStartPy() {
            double wavelengthValue = new Function.LinearFunction( 0, wavelength.getMaximum(), 5, 20 ).evaluate( wavelength.getValue() );
            double momentum = -hbar * 2 * Math.PI / wavelengthValue;
//            System.out.println( "wavelengthValue = " + wavelengthValue + ", momentum=" + momentum );
            return momentum;
        }

        protected void hookupListener( MomentumChangeListener momentumChangeListener ) {
            wavelength.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    notifyMomentumChanged();
                }
            } );
        }
    }

    static class Atom extends GunItem {
        private JSlider mass;
        private PhetGraphic massGraphic;
        private JSlider velocity;
        private PhetGraphic velocityGraphic;

        public Atom( GunGraphic gunGraphic, String label, String imageLocation ) {
            super( gunGraphic, label, imageLocation );
            mass = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
            mass.setBorder( BorderFactory.createTitledBorder( "Mass" ) );
            massGraphic = PhetJComponent.newInstance( gunGraphic.getComponent(), mass );

            velocity = new JSlider( JSlider.HORIZONTAL, 0, 1000, 1000 / 2 );
            velocity.setBorder( BorderFactory.createTitledBorder( "Velocity" ) );
            velocityGraphic = PhetJComponent.newInstance( gunGraphic.getComponent(), velocity );
        }

        public void setup( GunGraphic gunGraphic ) {
            gunGraphic.getSchrodingerModule().getDiscreteModel().setPropagatorModifiedRichardson();

            gunGraphic.addGraphic( massGraphic );
            massGraphic.setLocation( -massGraphic.getWidth() - 2, gunGraphic.getComboBox().getHeight() + 2 );

            gunGraphic.addGraphic( velocityGraphic );
            velocityGraphic.setLocation( -velocityGraphic.getWidth() - 2, massGraphic.getY() + massGraphic.getHeight() + 2 );
        }

        public void teardown( GunGraphic gunGraphic ) {
            gunGraphic.removeGraphic( massGraphic );
            gunGraphic.removeGraphic( velocityGraphic );
        }

        public double getStartPy() {
            double velocityValue = new Function.LinearFunction( 0, 1000, 0, 1.5 ).evaluate( velocity.getValue() );
            double massValue = new Function.LinearFunction( 0, 1000, 0, 1 ).evaluate( mass.getValue() );
            return -velocityValue * massValue;
        }

        protected void hookupListener( MomentumChangeListener momentumChangeListener ) {
            mass.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    notifyMomentumChanged();
                }
            } );
            velocity.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    notifyMomentumChanged();
                }
            } );
        }
    }

    private JComboBox createParticleTypeSelectorComboBox() {

        final ImageComboBox imageComboBox = new ImageComboBox( items );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( items[index] );
            }
        } );

        return imageComboBox;
    }

    private void setupObject( GunItem item ) {
        if( item != currentObject ) {
            getDiscreteModel().clearWavefunction();
            if( currentObject != null ) {
                currentObject.teardown( this );
            }
            item.setup( this );
            currentObject = item;
        }
    }

    public int getGunWidth() {
        return gunImageGraphic.getWidth();
    }
}

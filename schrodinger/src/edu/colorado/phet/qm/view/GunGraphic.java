/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class GunGraphic extends GraphicLayerSet {
    private SchrodingerPanel schrodingerPanel;

    public JButton fireOne;
    public JCheckBox alwaysOnCheckBox;
    public JSlider intensitySlider;
    private int time = 0;
    private int lastFireTime = 0;

    private PhetImageGraphic phetImageGraphic;
    private JComboBox comboBox;
    private GunItem currentObject;
    private GunItem[] items;
    private boolean alwaysOn;
//    private WaveSetup lastWave;

    public GunGraphic( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        this.schrodingerPanel = schrodingerPanel;
        phetImageGraphic = new PhetImageGraphic( getComponent(), "images/laser.gif" );
        addGraphic( phetImageGraphic );

        fireOne = new JButton( "Fire Once" );
        fireOne.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
//                schrodingerPanel.getSchrodingerModule().getSchrodingerControlPanel().fireParticle();
            }
        } );
        fireOne.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                phetImageGraphic.clearTransform();
                phetImageGraphic.translate( 0, 10 );
            }

            public void mouseReleased( MouseEvent e ) {
                phetImageGraphic.clearTransform();
            }
        } );
        alwaysOnCheckBox = new JCheckBox( "High Intensity" );
        alwaysOnCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setAlwaysOn( alwaysOnCheckBox.isSelected() );
            }
        } );

        intensitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 0 );
        intensitySlider.setBorder( BorderFactory.createTitledBorder( "Intensity" ) );
        PhetGraphic intensityGraphic = PhetJComponent.newInstance( schrodingerPanel, intensitySlider );

        PhetGraphic fireJC = PhetJComponent.newInstance( schrodingerPanel, fireOne );
        PhetGraphic onJC = PhetJComponent.newInstance( schrodingerPanel, alwaysOnCheckBox );
        addGraphic( fireJC );
        addGraphic( onJC );
        addGraphic( intensityGraphic );
        onJC.setLocation( phetImageGraphic.getWidth() + 2, fireJC.getHeight() + 2 );
        fireJC.setLocation( phetImageGraphic.getWidth() + 2, 0 );
        intensityGraphic.setLocation( phetImageGraphic.getWidth() + 2, onJC.getY() + onJC.getHeight() + 4 );
        intensitySlider.setEnabled( false );

        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                time++;
                if( isTimeToFire() ) {
                    autofire();
                }
            }
        } );

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

    private DiscreteModel getDiscreteModel() {
        return schrodingerPanel.getDiscreteModel();
    }

    public void setLocation( int x, int y ) {
        super.setLocation( x, y );
        comboBox.setBounds( x - comboBox.getPreferredSize().width - 2, y,
                            comboBox.getPreferredSize().width, comboBox.getPreferredSize().height );
        System.out.println( "comboBox.getLocation() = " + comboBox.getLocation() );
        setupObject( currentObject );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        comboBox.setVisible( visible );
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
        private GunGraphic gunGraphic;
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
            double y = getDiscreteModel().getGridHeight() * 0.8;
            double px = 0;
            double py = getStartPy();
            System.out.println( "py = " + py );

            double dxLattice = getStartDxLattice();
            System.out.println( "dxLattice = " + dxLattice );
            GaussianWave waveSetup = new GaussianWave( new Point( (int)x, (int)y ),
                                                       new Vector2D.Double( px, py ), dxLattice );

            Complex centerValue = currentWave.valueAt( (int)x, (int)y );
            double desiredPhase = centerValue.getComplexPhase();

            System.out.println( "original Center= " + centerValue + ", desired phase=" + desiredPhase );

            Wavefunction copy = currentWave.createEmptyWavefunction();
            waveSetup.initialize( copy );

            Complex centerValueCopy = copy.valueAt( (int)x, (int)y );
            System.out.println( "unedited: =" + centerValueCopy + ", unedited phase=" + centerValueCopy.getComplexPhase() );

            double uneditedPhase = centerValueCopy.getComplexPhase();
            double deltaPhase = desiredPhase - uneditedPhase;

            System.out.println( "deltaPhase = " + deltaPhase );
            waveSetup.setPhase( deltaPhase );

            Wavefunction test = currentWave.createEmptyWavefunction();
            waveSetup.initialize( test );
            Complex testValue = test.valueAt( (int)x, (int)y );
            System.out.println( "created testValue = " + testValue + ", created phase=" + testValue.getComplexPhase() );

            return waveSetup;
        }

        public void fireParticle() {
            WaveSetup initialWavefunction = getInitialWavefunction( getDiscreteModel().getWavefunction() );
            getSchrodingerModule().fireParticle( initialWavefunction );
        }

        private SchrodingerModule getSchrodingerModule() {
            return gunGraphic.getSchrodingerModule();
        }

        private DiscreteModel getDiscreteModel() {
            return getSchrodingerModule().getDiscreteModel();
        }

        protected double getStartDxLattice() {
            double dxLattice = 0.04 * getDiscreteModel().getGridWidth();
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

    static class Photon extends GunItem {
        private JSlider wavelength;
        private PhetGraphic graphic;
        private double hbar = 1.0;

        public Photon( GunGraphic gunGraphic, String label, String imageLocation ) {
            super( gunGraphic, label, imageLocation );
            wavelength = new JSlider( JSlider.HORIZONTAL, 8, 500, 500 / 2 );
            wavelength.setBorder( BorderFactory.createTitledBorder( "Wavelength" ) );
            graphic = PhetJComponent.newInstance( gunGraphic.getComponent(), wavelength );
        }

        public void setup( GunGraphic gunGraphic ) {
            gunGraphic.addGraphic( graphic );
            graphic.setLocation( -graphic.getWidth() - 2, gunGraphic.getComboBox().getHeight() + 2 );
        }

        public void teardown( GunGraphic gunGraphic ) {
            gunGraphic.removeGraphic( graphic );
        }

        public double getStartPy() {
            double wavelengthValue = new Function.LinearFunction( 0, 1000, 0, 100 ).evaluate( wavelength.getValue() );
            double momentum = -hbar * 2 * Math.PI / wavelengthValue;
            System.out.println( "wavelengthValue = " + wavelengthValue + ", momentum=" + momentum );
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
        if( currentObject != null ) {
            currentObject.teardown( this );
        }
        item.setup( this );
        currentObject = item;
    }

    private void autofire() {
        lastFireTime = time;
        System.out.println( "System.currentTimeMillis() = " + System.currentTimeMillis() );
        fireParticle();
//        if( intensitySlider.getValue() == intensitySlider.getMaximum() ) {
//            for( int i = 0; i < 1000; i++ ) {
//                schrodingerPanel.getSchrodingerModule().getIntensityDisplay().detectOne();
//            }
//        }
//        else {
//            schrodingerPanel.getSchrodingerModule().getIntensityDisplay().detectOne();
//        }
    }

    private boolean isTimeToFire() {
        if( alwaysOnCheckBox.isSelected() && intensitySlider.getValue() != 0 ) {
            int numStepsBetweenFire = getNumStepsBetweenFire();
            return time >= numStepsBetweenFire + lastFireTime;
        }
        return false;
    }

    private int getNumStepsBetweenFire() {

        double frac = intensitySlider.getValue() / ( (double)intensitySlider.getMaximum() );
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 1, 20, 1 );
        return (int)linearFunction.evaluate( frac );
    }

//    static class Firer extends DiscreteModel.Adapter {
//
//        public void finishedTimeStep( DiscreteModel model ) {
//            System.out.println( "adding particle." );
//        }
//
//    }

//    Firer firer = new Firer();

    private void setAlwaysOn( boolean on ) {
        fireOne.setEnabled( !on );
        intensitySlider.setEnabled( on );
        this.alwaysOn = on;
//        if( !on ) {
//            getDiscreteModel().removeListener( firer );
//        }
//        if( on ) {
//            getDiscreteModel().addListener( firer );
//        }
    }

    public int getGunWidth() {
        return phetImageGraphic.getWidth();
    }
}

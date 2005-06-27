/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class GunGraphic extends GraphicLayerSet {
    private SchrodingerPanel schrodingerPanel;

    public JButton fireOne;
    public JCheckBox alwaysOn;
    public JSlider intensitySlider;
    private int time = 0;
    private int lastFireTime = 0;

    private PhetImageGraphic phetImageGraphic;
    private JComboBox comboBox;
    private GunItem currentObject;
    private GunItem[] items;

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
        alwaysOn = new JCheckBox( "High Intensity" );
        alwaysOn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setAlwaysOn( alwaysOn.isSelected() );
            }
        } );

        intensitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 0 );
        intensitySlider.setBorder( BorderFactory.createTitledBorder( "Intensity" ) );
        PhetGraphic intensityGraphic = PhetJComponent.newInstance( schrodingerPanel, intensitySlider );

        PhetGraphic fireJC = PhetJComponent.newInstance( schrodingerPanel, fireOne );
        PhetGraphic onJC = PhetJComponent.newInstance( schrodingerPanel, alwaysOn );
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
                    fire();
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

    static abstract class GunItem extends ImageComboBox.Item {
        private GunGraphic gunGraphic;

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

        public WaveSetup getInitialWavefunction() {
            double x = getDiscreteModel().getGridWidth() * 0.5;
            double y = getDiscreteModel().getGridHeight() * 0.8;
            double px = 0;
            double py = getStartPy();
            double dxLattice = getStartDxLattice();
            WaveSetup waveSetup = new GaussianWaveSetup( new Point( (int)x, (int)y ),
                                                         new Vector2D.Double( px, py ), dxLattice );
            edit( waveSetup );
            return waveSetup;
        }

        public void fireParticle() {
            getSchrodingerModule().fireParticle( getInitialWavefunction() );
        }

        protected void edit( WaveSetup waveSetup ) {
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

    private void fire() {
        lastFireTime = time;
        if( intensitySlider.getValue() == intensitySlider.getMaximum() ) {
            for( int i = 0; i < 1000; i++ ) {
                schrodingerPanel.getSchrodingerModule().getIntensityDisplay().detectOne();
            }
        }
        else {
            schrodingerPanel.getSchrodingerModule().getIntensityDisplay().detectOne();
        }
    }

    private boolean isTimeToFire() {
        if( alwaysOn.isSelected() && intensitySlider.getValue() != 0 ) {
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

    private void setAlwaysOn( boolean on ) {
        fireOne.setEnabled( !on );
        intensitySlider.setEnabled( on );
        if( !on ) {
            getDiscreteModel().setPaused( false );
        }
        if( on ) {
            getDiscreteModel().getWavefunction().clear();
            getDiscreteModel().setPaused( true );
            getSchrodingerModule().getSchrodingerPanel().repaintAll();
            //simulate wavefunction, including handling detectors/slits.
            final Wavefunction copy = getDiscreteModel().getWavefunction().copy();
            currentObject.getInitialWavefunction().initialize( copy );

            double etaTime = new VerticalETA().getETA( copy );
            //todo error check on etaTime
            if( etaTime < 0 || etaTime > 500 ) {
                System.out.println( "BAD etaTime = " + etaTime );
                etaTime = 500;
            }

            //show a message: 'warming up for high intensity beam.'
            System.out.println( "Warming up for rapid fire." );

            final PhetShadowTextGraphic msg = new PhetShadowTextGraphic( getComponent(),
                                                                         new Font( "Lucida Sans", Font.BOLD, 20 ),
                                                                         "Warming up for rapid fire.", Color.green, 1, 1, Color.white );
            getSchrodingerModule().getSchrodingerPanel().addGraphic( msg, Double.POSITIVE_INFINITY );
            msg.setLocation( 50, 100 );

            final PhetShadowTextGraphic readout = new PhetShadowTextGraphic( getComponent(), new Font( "Lucida Sans", Font.BOLD, 16 ),
                                                                             "", Color.red, 1, 1, Color.blue );
            getSchrodingerModule().getSchrodingerPanel().addGraphic( readout );
            readout.setLocation( msg.getX(), msg.getY() + msg.getHeight() + 2 );

            JButton cancel = new JButton( "Cancel" );
            final PhetGraphic cancelGraphic = PhetJComponent.newInstance( getComponent(), cancel );
            getSchrodingerModule().getSchrodingerPanel().addGraphic( cancelGraphic, Double.POSITIVE_INFINITY );

            cancelGraphic.setLocation( msg.getX(), readout.getY() + readout.getHeight() + 2 + cancelGraphic.getHeight() );

            final double etaTime1 = etaTime;
            Thread t = new Thread( new Runnable() {
                public void run() {

                    for( int i = 0; i < etaTime1; i++ ) {
                        getDiscreteModel().getPropagator().propagate( copy );
                        if( i % 3 == 0 ) {
                            System.out.println( i + "/" + etaTime1 );
                            double percent = i / etaTime1 * 100;
                            String pct = new DecimalFormat( "0.00" ).format( percent );
                            readout.setText( pct + " %" );
                            try {
                                Thread.sleep( 60 );
                            }
                            catch( InterruptedException e ) {
                                e.printStackTrace();
                            }
                        }
                    }
                    cancelGraphic.setVisible( false );
                    readout.setVisible( false );
                    msg.setVisible( false );
                    getSchrodingerModule().getSchrodingerPanel().getIntensityDisplay().setRegionOfInterestSub( copy );
                }
            } );
            t.start();
        }
    }

    public int getGunWidth() {
        return phetImageGraphic.getWidth();
    }
}

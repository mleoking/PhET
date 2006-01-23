/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.phetcommon.GlossyPanel;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.phetcommon.TestBorder2;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class HighIntensityGunGraphic extends AbstractGunGraphic {
    private JCheckBox alwaysOnCheckBox;
    private ModelSlider intensitySlider;
    private boolean on = false;
    private HighIntensityBeam[] beams;
    private HighIntensityBeam currentBeam;
    private Photon photon;
    private static final double MAX_INTENSITY_READOUT = 40;
    protected final PSwing gunControlPSwing;
    private VerticalLayoutPanel gunControlPanel;

    public HighIntensityGunGraphic( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        alwaysOnCheckBox = new JCheckBox( "On", true );
        alwaysOnCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setOn( alwaysOnCheckBox.isSelected() );
            }
        } );
        intensitySlider = new ModelSlider( "Intensity ( particles/second )", "", 0, MAX_INTENSITY_READOUT, 0, new DecimalFormat( "0.000" ) );
        intensitySlider.setModelTicks( new double[]{0, 10, 20, 30, 40} );
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateIntensity();
            }
        } );
        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                stepBeam();
            }
        } );

        JPanel gunControlPanel = createGunControlPanel();
        gunControlPSwing = new PSwing( schrodingerPanel, gunControlPanel );
        addChild( gunControlPSwing );

        setupObject( beams[0] );
        setOn( true );
    }

    private JPanel createGunControlPanel() {
        GlossyPanel glossyPanel = new GlossyPanel();
        VerticalLayoutPanel gunControlPanel = new VerticalLayoutPanel() {
            protected void paintComponent( Graphics g ) {
                Color lightGray = new Color( 192, 192, 192 );
                Color shadedGray = new Color( 228, 228, 228 );
                GradientPaint gradientPaint = new GradientPaint( 0, 0, lightGray, gunControlPanel.getWidth() / 2, gunControlPanel.getHeight() / 2, shadedGray, true );
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint( gradientPaint );
                g2.fillRect( 0, 0, getWidth(), getHeight() );
                super.paintComponent( g );
            }
        };
        gunControlPanel.setOpaque( false );
//        gunControlPanel.setBorder( BorderFactory.createTitledBorder( "Gun" ) );
//        gunControlPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
//        gunControlPanel.setBorder( new OuterBorder3D());

        gunControlPanel.setBorder( new TestBorder2().getCompoundBorder() );
//        gunControlPanel.setBorder( new BevelBorder( BevelBorder.RAISED,Color.red, Color.green,Color.blue, Color.yellow));

//        gunControlPanel.setBorder( BorderFactory.createTitledBorder( "Gun" ) );
        gunControlPanel.add( intensitySlider );
        gunControlPanel.add( alwaysOnCheckBox );
//        alwaysOnCheckBox.setOpaque( false );
//        intensitySlider.setOpaque( false );

        setOpaque( gunControlPanel, false );

        return gunControlPanel;
    }

    private Border createBorder() {
        return new MetalBorders.Flush3DBorder();
    }

    private Border getSoft() {
        return new SoftBevelBorder( SoftBevelBorder.RAISED, Color.gray, Color.darkGray );
    }

    private Border createBorder1() {
        return BorderFactory.createRaisedBevelBorder();
//        return new SoftBevelBorder( SoftBevelBorder.RAISED, Color.blue, Color.darkGray, Color.red, Color.green );
    }

    private void setOpaque( JComponent container, boolean b ) {
        container.setOpaque( b );
        for( int i = 0; i < container.getComponentCount(); i++ ) {
            Component child = container.getComponent( i );
            if( child instanceof JComponent && !( child instanceof JTextComponent ) ) {
                setOpaque( (JComponent)child, b );
            }
        }
    }

    protected void layoutChildren() {
        super.layoutChildren();
        gunControlPSwing.setOffset( getControlOffsetX(), getControlOffsetY() );
//        getComboBoxGraphic().setOffset( gunControlPSwing.getFullBounds().getMaxX(), gunControlPSwing.getFullBounds().getY() );
        if( getGunControls() != null ) {
            getGunControls().setOffset( gunControlPSwing.getFullBounds().getMaxX(), gunControlPSwing.getFullBounds().getY() );
        }
    }

    public PSwing getGunControlPSwing() {
        return gunControlPSwing;
    }

    protected double getControlOffsetX() {
        return getGunImageGraphic().getFullBounds().getWidth() - 40;
    }

    protected Point getGunLocation() {
        if( currentBeam != null ) {
            return currentBeam.getGunLocation();
        }
        else {
            return new Point();
        }
    }

    private void stepBeam() {
        currentBeam.stepBeam();
    }

    protected ImagePComboBox initComboBox() {
        photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );
        HighIntensityBeam[] mybeams = new HighIntensityBeam[]{
                new PhotonBeam( this, photon ),
                new ParticleBeam( DefaultGunParticle.createElectron( this ) ),
                new ParticleBeam( DefaultGunParticle.createNeutron( this ) ),
                new ParticleBeam( DefaultGunParticle.createHelium( this ) ),
        };
        setBeams( mybeams );
        final ImagePComboBox imageComboBox = new ImagePComboBox( beams );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( beams[index] );
            }
        } );
        return imageComboBox;
    }

    protected void setBeams( HighIntensityBeam[] mybeams ) {
        this.beams = mybeams;
    }

    public void setupObject( HighIntensityBeam beam ) {
        if( beam != currentBeam ) {
            getDiscreteModel().clearWavefunction();
            if( currentBeam != null ) {
                currentBeam.deactivate( this );
            }
            beam.activate( this );
            currentBeam = beam;
            currentBeam.setHighIntensityModeOn( alwaysOnCheckBox.isSelected() );
        }
        updateGunLocation();
    }

    private void updateIntensity() {
        double intensity = new Function.LinearFunction( 0, MAX_INTENSITY_READOUT, 0, 1 ).evaluate( intensitySlider.getValue() );

        System.out.println( "slidervalue=" + intensitySlider.getValue() + ", intensity = " + intensity );
        for( int i = 0; i < beams.length; i++ ) {
            beams[i].setIntensity( intensity );
        }
    }

    public void setOn( boolean on ) {
        this.on = on;
        currentBeam.setHighIntensityModeOn( on );
    }

    public boolean isOn() {
        return on;
    }

    public ColorData getRootColor() {
        return photon == null ? null : photon.getRootColor();
    }
}

/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.phetcommon.LucidaSansFont;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    protected JCheckBox alwaysOnCheckBox;
    protected ModelSlider intensitySlider;
    private boolean on = false;
    private HighIntensityBeam[] beams;
    private HighIntensityBeam currentBeam;
    private Photon photon;
    private static final double MAX_INTENSITY_READOUT = 40;
    private GunControlPanel gunControlPanel;

    protected ModelSlider getIntensitySlider() {
        return intensitySlider;
    }

    protected JCheckBox getAlwaysOnCheckBox() {
        return alwaysOnCheckBox;
    }

    public HighIntensityGunGraphic( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        alwaysOnCheckBox = new JCheckBox( "On", on ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        alwaysOnCheckBox.setVerticalTextPosition( AbstractButton.BOTTOM );
        alwaysOnCheckBox.setHorizontalTextPosition( AbstractButton.CENTER );
        alwaysOnCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setOn( alwaysOnCheckBox.isSelected() );
            }
        } );
        intensitySlider = new ModelSlider( "Intensity ( particles/second )", "", 0, MAX_INTENSITY_READOUT, MAX_INTENSITY_READOUT, new DecimalFormat( "0.000" ) );
        intensitySlider.setModelTicks( new double[]{0, 10, 20, 30, 40} );
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateIntensity();
            }
        } );
        updateIntensity();
        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                stepBeam();
            }
        } );

        gunControlPanel = createGunControlPanel();
//        addChild( gunControlPanel.getPSwing() );

        setupObject( beams[0] );
//        setOn( true );

//        HelpBalloon helpBalloon = new HelpBalloon( schrodingerPanel.getSchrodingerModule().getDefaultHelpPane(), "Increase the Gun Intensity" ,HelpBalloon.BOTTOM_CENTER, 100);
//        helpBalloon.pointAt( intensitySlider,(PSwing)gunControlPanel.getPSwing(),schrodingerPanel  );
//        schrodingerPanel.getSchrodingerModule().getDefaultHelpPane().add( helpBalloon );
        alwaysOnCheckBox.setFont( new LucidaSansFont( 13, true ) );
        alwaysOnCheckBox.setBackground( gunBackgroundColor );
        setOnGunControl( new PSwing( schrodingerPanel, alwaysOnCheckBox ) );
    }

    protected GunControlPanel createGunControlPanel() {
        GunControlPanel gunControlPanel = new GunControlPanel( getSchrodingerPanel() );
        gunControlPanel.add( intensitySlider );
        return gunControlPanel;
    }

    protected void layoutChildren() {
        super.layoutChildren();
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

    protected void setGunControls( JComponent gunControl ) {
        gunControlPanel.setGunControls( gunControl );
    }

    public GunControlPanel getGunControlPanel() {
        return gunControlPanel;
    }

    public boolean isPhotonMode() {
        return currentBeam instanceof PhotonBeam;
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
            currentBeam.setHighIntensityModeOn( on );
            System.out.println( "alwaysOnCheckBox.isSelected() = " + alwaysOnCheckBox.isSelected() );
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
        if( currentBeam != null ) {
            currentBeam.setHighIntensityModeOn( on );
        }
    }

    public boolean isOn() {
        return on;
    }

    public ColorData getRootColor() {
        return photon == null ? null : photon.getRootColor();
    }
}

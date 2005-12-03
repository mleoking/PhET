/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

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
    private JCheckBox alwaysOnCheckBox;
    private ModelSlider intensitySlider;
    private boolean on = false;
    private HighIntensityBeam[] beams;
    private HighIntensityBeam currentBeam;
    private Photon photon;
    private static final double MAX_INTENSITY_READOUT = 40;
    protected final PSwing gunControlPSwing;
//    private PSwing intensityGraphic;
//    private PSwing onCheckboxGraphic;

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
//        intensityGraphic = new PSwing( schrodingerPanel, intensitySlider );
//        onCheckboxGraphic = new PSwing( schrodingerPanel, alwaysOnCheckBox );

//        addChild( onCheckboxGraphic );
//        addChild( intensityGraphic );
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
        JPanel gunControlPanel = new VerticalLayoutPanel();
        gunControlPanel.setBorder( BorderFactory.createTitledBorder( "Gun" ) );
        gunControlPanel.add( intensitySlider );
        gunControlPanel.add( alwaysOnCheckBox );
        return gunControlPanel;
    }

    protected void layoutChildren() {
        super.layoutChildren();
        double layoutX = getRelLayoutX();
        gunControlPSwing.setOffset( layoutX, getControlOffsetY() );
        getComboBoxGraphic().setOffset( gunControlPSwing.getFullBounds().getMaxX(), gunControlPSwing.getFullBounds().getY() );
        if( getGunControls() != null ) {
            getGunControls().setOffset( getComboBoxGraphic().getFullBounds().getX(), getComboBoxGraphic().getFullBounds().getMaxY() );
        }
    }

    protected double getRelLayoutX() {
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
        DefaultGunParticle electron = new DefaultGunParticle( this, "Electrons", "images/electron-thumb.jpg", 1.0 );
        DefaultGunParticle atom = new DefaultGunParticle( this, "Atoms", "images/atom-thumb.jpg" );

        HighIntensityBeam[] mybeams = new HighIntensityBeam[]{
                new PhotonBeam( this, photon ),
                new ParticleBeam( electron ),
                new ParticleBeam( atom )};
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

    public PhotonColorMap.ColorData getRootColor() {
        return photon == null ? null : photon.getRootColor();
    }
}

/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.components.ModelSlider;
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
    private PSwing intensityGraphic;
    private PSwing onCheckboxGraphic;

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
        intensityGraphic = new PSwing( schrodingerPanel, intensitySlider );
        onCheckboxGraphic = new PSwing( schrodingerPanel, alwaysOnCheckBox );

        addChild( onCheckboxGraphic );
        addChild( intensityGraphic );

        //todo piccolo
//        final WiggleMe wiggleMe = new WiggleMe( getSchrodingerPanel(), getSchrodingerPanel().getSchrodingerModule().getModel(), "Increase the Intensity", intensityGraphic );
//        schrodingerPanel.addWorldChild( wiggleMe, Double.POSITIVE_INFINITY );
//        getSchrodingerPanel().addMouseListener( new MouseAdapter() {
//            public void mousePressed( MouseEvent e ) {
//                wiggleMe.setVisible( false );
//            }
//        } );


        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                stepBeam();
            }
        } );
        setupObject( beams[0] );
        setOn( true );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        intensityGraphic.setOffset( getGunImageGraphic().getFullBounds().getWidth() + 2 + getFireButtonInsetDX(), 0 + getControlOffsetY() );
        onCheckboxGraphic.setOffset( intensityGraphic.getFullBounds().getX() + intensityGraphic.getFullBounds().getWidth() / 2 - onCheckboxGraphic.getFullBounds().getWidth() / 2, intensityGraphic.getFullBounds().getMaxY() + 4 );
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
        Electron e = new Electron( this, "Electrons", "images/electron-thumb.jpg" );
        Atom atom = new Atom( this, "Atoms", "images/atom-thumb.jpg" );

        HighIntensityBeam[] mybeams = new HighIntensityBeam[]{
                new PhotonBeam( this, photon ),
                new ParticleBeam( e ),
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

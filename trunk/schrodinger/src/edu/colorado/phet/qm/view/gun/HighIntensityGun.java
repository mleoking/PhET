/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;
import edu.colorado.phet.qm.phetcommon.WiggleMe;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class HighIntensityGun extends AbstractGun {
    private JCheckBox alwaysOnCheckBox;
    private ModelSlider intensitySlider;
    private boolean on = false;
    private HighIntensityBeam[] beams;
    private HighIntensityBeam currentBeam;
    private Photon photon;

    public HighIntensityGun( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        alwaysOnCheckBox = new JCheckBox( "On", true );
        alwaysOnCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setOn( alwaysOnCheckBox.isSelected() );
            }
        } );
        intensitySlider = new ModelSlider( "Intensity", "", 0, 1, 0, new DecimalFormat( "0.000" ) );
        intensitySlider.setModelTicks( new double[]{0, 0.5, 1.0} );
//        intensitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 0 );
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateIntensity();
            }
        } );
//        intensitySlider.setBorder( BorderFactory.createTitledBorder( "Intensity" ) );
        PhetGraphic intensityGraphic = PhetJComponent.newInstance( schrodingerPanel, intensitySlider );
        PhetGraphic onCheckboxGraphic = PhetJComponent.newInstance( schrodingerPanel, alwaysOnCheckBox );

        addGraphic( onCheckboxGraphic );
        addGraphic( intensityGraphic );
        intensityGraphic.setLocation( getGunImageGraphic().getWidth() + 2 + getFireButtonInsetDX(), 0 + getControlOffsetY() );

        final WiggleMe wiggleMe = new WiggleMe( getSchrodingerPanel(), getSchrodingerPanel().getSchrodingerModule().getModel(), "Increase the Intensity", intensityGraphic );
        schrodingerPanel.addGraphic( wiggleMe, Double.POSITIVE_INFINITY );
        getSchrodingerPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                wiggleMe.setVisible( false );
            }
        } );

        onCheckboxGraphic.setLocation( intensityGraphic.getX() + intensityGraphic.getWidth() / 2 - onCheckboxGraphic.getWidth() / 2, intensityGraphic.getY() + intensityGraphic.getHeight() + 4 );
        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                stepBeam();
            }
        } );
        setupObject( beams[0] );
    }

    private void stepBeam() {
        currentBeam.stepBeam();
    }

    protected JComboBox initComboBox() {
        photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );
        Electron e = new Electron( this, "Electrons", "images/electron-thumb.jpg" );
        Atom atom = new Atom( this, "Atoms", "images/atom-thumb.jpg" );

        HighIntensityBeam[] mybeams = new HighIntensityBeam[]{
            new PhotonBeam( this, photon ),
            new ParticleBeam( e ),
            new ParticleBeam( atom )};
        setBeams( mybeams );
        final ImageComboBox imageComboBox = new ImageComboBox( beams );
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
    }

    private void updateIntensity() {
        double intensity = new Function.LinearFunction( 0, 1, 0, 1 ).evaluate( intensitySlider.getValue() );
        for( int i = 0; i < beams.length; i++ ) {
            beams[i].setIntensity( intensity );
        }
    }

    private void setOn( boolean on ) {
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

/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.qm.phetcommon.ImageComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class HighIntensityGun extends AbstractGun {
    private JCheckBox alwaysOnCheckBox;
    private JSlider intensitySlider;
    private int lastFireTime = 0;
    private int time = 0;
    private boolean on = false;
    private int numStepsBetweenFire = 4;
    private HighIntensityBeam highIntensityBeam;
    private HighIntensityBeam[] highIntensityBeams;

    public HighIntensityGun( final SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
        alwaysOnCheckBox = new JCheckBox( "On" );
        alwaysOnCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setOn( alwaysOnCheckBox.isSelected() );
            }
        } );

        intensitySlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 0 );
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateIntensity();
            }
        } );
        intensitySlider.setBorder( BorderFactory.createTitledBorder( "Intensity" ) );
        PhetGraphic intensityGraphic = PhetJComponent.newInstance( schrodingerPanel, intensitySlider );

        PhetGraphic onJC = PhetJComponent.newInstance( schrodingerPanel, alwaysOnCheckBox );

        addGraphic( onJC );
        addGraphic( intensityGraphic );
        onJC.setLocation( getGunImageGraphic().getWidth() + 2, 0 );

        intensityGraphic.setLocation( getGunImageGraphic().getWidth() + 2, onJC.getY() + onJC.getHeight() + 4 );
        intensitySlider.setEnabled( false );

        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                time++;
                if( isTimeToFire() ) {
                    autofire();
                }
            }
        } );
        highIntensityBeams = new HighIntensityBeam[]{
            new PhotonBeam( this )
        };
    }

    protected JComboBox initComboBox() {
        final ImageComboBox.Item[] gunItems = new GunParticle[]{
            new Photon( this, "Photons", "images/photon-thumb.jpg" ),
            new Electron( this, "Electrons", "images/electron-thumb.jpg" ),
            new Atom( this, "Atoms", "images/atom-thumb.jpg" )};
        final ImageComboBox imageComboBox = new ImageComboBox( gunItems );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
//        imageComboBox.addItemListener( new ItemListener() {
//            public void itemStateChanged( ItemEvent e ) {
//                int index = imageComboBox.getSelectedIndex();
//                setupObject( gunItems[index] );
//            }
//        } );

        return imageComboBox;
    }

    private void updateIntensity() {
        double intensity = new Function.LinearFunction( 0, 1000, 0, 1 ).evaluate( intensitySlider.getValue() );
        for( int i = 0; i < highIntensityBeams.length; i++ ) {
            highIntensityBeams[i].setIntensity( intensity );
        }
    }

    private boolean isTimeToFire() {
//        if( alwaysOnCheckBox.isSelected() && intensitySlider.getValue() != 0 ) {
        if( alwaysOnCheckBox.isSelected() ) {
            int numStepsBetweenFire = getNumStepsBetweenFire();
            return time >= numStepsBetweenFire + lastFireTime;
        }
        return false;
    }

    private int getNumStepsBetweenFire() {
        return numStepsBetweenFire;
//        double frac = intensitySlider.getValue() / ( (double)intensitySlider.getMaximum() );
//        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, 1, 20, 1 );
//        return (int)linearFunction.evaluate( frac );
    }

    private void setOn( boolean on ) {
        intensitySlider.setEnabled( on );
        this.on = on;
//        super.getCurrentObject().setHighIntensityModeOn( on );
    }

    public boolean isOn() {
        return on;
    }

    private void autofire() {
        lastFireTime = time;
//        getCurrentObject().autofire();
    }

//    protected void setupObject( GunItem item ) {
//        super.setupObject( item );
//        if( item != null && alwaysOnCheckBox != null ) {
//            item.setHighIntensityModeOn( alwaysOnCheckBox.isSelected() );
//        }
//    }
}

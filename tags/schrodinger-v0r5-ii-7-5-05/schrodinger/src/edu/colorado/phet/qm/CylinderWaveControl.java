/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.model.CylinderSource;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.PlaneWave;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.GunGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 5:56:06 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class CylinderWaveControl extends JCheckBox {
    private SchrodingerModule module;
    private DiscreteModel discreteModel;
    private CylinderSource waveSource;
    private double momentum = 2.0 / 10.0 * Math.PI;

    public CylinderWaveControl( SchrodingerModule module, DiscreteModel discreteModel ) {
        super( "Cylinder wave" );
        this.module = module;
        this.discreteModel = discreteModel;

        waveSource = new CylinderSource( createRectRegionForCylinder(), createPlaneWave() );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( isSelected() ) {
                    waveSource.setRegion( createRectRegionForCylinder() );
                    getDiscreteModel().addListener( waveSource );
                }
                else {
                    getDiscreteModel().removeListener( waveSource );
                }
            }
        } );

        module.getSchrodingerPanel().getGunGraphic().addMomentumChangeListener( new GunGraphic.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                momentum = val;
                waveSource.setWave( createPlaneWave() );
            }
        } );
    }

    private PlaneWave createPlaneWave() {
//        double k = 2.0 / 10.0 * Math.PI;
        System.out.println( "k = " + 2.0 / 10.0 * Math.PI );
        System.out.println( "momentum = " + momentum );

        final PlaneWave planeWave = new PlaneWave( momentum, getDiscreteModel().getGridWidth() );
        planeWave.setMagnitude( 0.055 );
        return planeWave;
    }

    private Rectangle createRectRegionForCylinder() {
        int damping = getDiscreteModel().getDamping().getDepth();
        int cylinderRadius = 10;
        Point center = new Point( getWavefunction().getHeight() / 2, getWavefunction().getHeight() - damping - cylinderRadius );
        Point corner = new Point( center.x + cylinderRadius, center.y + cylinderRadius );
        Rectangle rectangle = new Rectangle();
        rectangle.setFrameFromCenter( center, corner );
        return rectangle;
    }

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }
}

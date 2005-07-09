/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.model.*;
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
    private double dPhase = Math.PI / 6;
    private double waveMagnitude = 0.075;
    private double phase = 0.0;

    public CylinderWaveControl( SchrodingerModule module, DiscreteModel discreteModel ) {
        super( "Cylinder wave" );
        this.module = module;
        this.discreteModel = discreteModel;

        waveSource = new CylinderSource( createRectRegionForCylinder(), createPlaneWave( phase ) );

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
                dPhase = Math.abs( momentum );
                System.out.println( "momentum = " + momentum + ", dPhase=" + dPhase );

                waveSource.setWave( createPlaneWave( phase ) );
            }
        } );
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updatePhase();
            }
        } );
    }


    private void updatePhase() {
        phase += dPhase;
        waveSource.setWave( createPlaneWave( phase ) );
    }

    private Wave createPlaneWave( double phase ) {
//        double k = 2.0 / 10.0 * Math.PI;
//        System.out.println( "k = " + 2.0 / 10.0 * Math.PI );
//        System.out.println( "momentum = " + momentum );

        final PlaneWave planeWave = new PlaneWave( momentum, getDiscreteModel().getGridWidth() );
        planeWave.setPhase( phase );

        planeWave.setMagnitude( waveMagnitude );
        return new DampedWave( planeWave );
    }

    private Rectangle createRectRegionForCylinder() {
        int damping = getDiscreteModel().getDamping().getDepth();
//        int cylinderRadius = 10;
//        int cylinderRadius = 50;
//        int cylinderRadius = 100;
//        int cylinderRadius = 30;
        int cylinderRadius = 50;
//        Point center = new Point( getWavefunction().getHeight() / 2, getWavefunction().getHeight() - damping - cylinderRadius );
        int depthToShow = damping + 5;
        Point center = new Point( getWavefunction().getWidth() / 2, getWavefunction().getHeight() + cylinderRadius - depthToShow );
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

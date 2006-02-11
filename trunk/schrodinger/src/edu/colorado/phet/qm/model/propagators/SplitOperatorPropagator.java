/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.WaveDebugger;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;
import edu.colorado.phet.qm.view.complexcolormaps.VisualColorMap3;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 28, 2005
 * Time: 3:35:37 PM
 * Copyright (c) Jun 28, 2005 by Sam Reid
 */

public class SplitOperatorPropagator extends Propagator {
    private PhysicalSystem physicalSystem;


    public SplitOperatorPropagator( PhysicalSystem physicalSystem, Potential potential ) {
        super( potential );
        this.physicalSystem = physicalSystem;
    }

    public void propagate( Wavefunction w ) {
//        Wavefunction expV = getExpV( w.getWidth(), w.getHeight() );
        Wavefunction expT = getExpT( w.getWidth(), w.getHeight() );
//        new WaveDebugger( "expt",expT.getNormalizedInstance(),10,10).setVisible( true );
//        Wavefunction psi = multiplyPointwise( expV, w.copy() );
        Wavefunction psi = w.copy();
        Wavefunction phi = QWIFFT2D.forwardFFT( psi );
        waveDebugger.setWavefunction( phi.copy(), new VisualColorMap3() );
        phi = multiplyPointwise( expT, phi );
        psi = QWIFFT2D.inverseFFT( phi );
//        psi = multiplyPointwise( expV, psi );
        w.setWavefunction( psi );
    }

    public static void main( String[] args ) {
        Wavefunction expt = new SplitOperatorPropagator( null, null ).getExpT( 100, 100 );
        expt.printWaveToScreen( new DecimalFormat( "0.000000" ) );
    }

    private Wavefunction getExpV( int width, int height ) {
        return ones( width, height );
    }

    private Wavefunction getExpT( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, getExpTValue( i, k, wavefunction ) );
            }
        }
        return wavefunction;
    }

    private Complex getExpTValueWraparound( int i, int j, Wavefunction wavefunction ) {
        double px = ( i + wavefunction.getWidth() / 2 ) % wavefunction.getWidth();//wavefunction.getWidth()-i-1;
        double py = ( j + wavefunction.getHeight() / 2 ) % wavefunction.getHeight();
        double psquared = px * px + py * py;
        double ke = psquared * scale;
        if( i >= wavefunction.getWidth() - 5 || j >= wavefunction.getHeight() - 5 ) {
            return new Complex();
        }
//        if (i>wavefunction.getWidth()*0.9||j>wavefunction.getHeight()*0.9){
//            return new Complex( );
//        }
        //scale is directly or inversely proportional to each of the following:
        // the physical area of the box L*W (in meters)
        // the time step dt (in seconds)
        //the mass of the particle (kg)
        return Complex.exponentiateImaginary( -ke );//todo why the sign error?
    }

    private Complex getExpTValue( int i, int j, Wavefunction wavefunction ) {
        double px = i;//wavefunction.getWidth()-i-1;
        double py = j;
        double psquared = px * px + py * py;
        double ke = psquared * scale;
        if( i >= wavefunction.getWidth() - 5 || j >= wavefunction.getHeight() - 5 ) {
            return new Complex();
        }
//        if (i>wavefunction.getWidth()*0.9||j>wavefunction.getHeight()*0.9){
//            return new Complex( );
//        }
        //scale is directly or inversely proportional to each of the following:
        // the physical area of the box L*W (in meters)
        // the time step dt (in seconds)
        //the mass of the particle (kg)
        return Complex.exponentiateImaginary( -ke );//todo why the sign error?
    }

    private Complex getExpTValueOK( int i, int j, Wavefunction wavefunction ) {
        double px = i;//wavefunction.getWidth()-i-1;
        double py = j;
        double psquared = px * px + py * py;
        double ke = psquared * scale;
        if( i >= wavefunction.getWidth() - 5 || j >= wavefunction.getHeight() - 5 ) {
            return new Complex();
        }
//        if (i>wavefunction.getWidth()*0.9||j>wavefunction.getHeight()*0.9){
//            return new Complex( );
//        }
        //scale is directly or inversely proportional to each of the following:
        // the physical area of the box L*W (in meters)
        // the time step dt (in seconds)
        //the mass of the particle (kg)
        return Complex.exponentiateImaginary( -ke );//todo why the sign error?
    }

    static double scale;

    private static WaveDebugger waveDebugger;

    static {
        JFrame controls = new JFrame( "SOM controls" );
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        DecimalFormat textFieldFormat = new DecimalFormat( "0.0000000" );
        final ModelSlider modelSlider = new ModelSlider( "scale", "1/p^2", 0, 0.001, 0.0001, textFieldFormat, textFieldFormat );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                scale = modelSlider.getValue();
            }
        } );
        scale = modelSlider.getValue();
        verticalLayoutPanel.add( modelSlider );
        controls.setContentPane( verticalLayoutPanel );
        controls.pack();
        controls.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        controls.setVisible( true );

        waveDebugger = new WaveDebugger( "wave", new Wavefunction( 50, 50 ), 2, 2 );
        waveDebugger.setComplexColorMap( new VisualColorMap3() );
        waveDebugger.setVisible( true );
    }

    private Complex getExpTValuePhysical( int i, int j ) {
        double h = 1;
        double boxLength = 1;
        double boxHeight = boxLength;

        double dt = 0.000001;
        double mass = 1.0;

        double dpx = h / boxLength;
        double dpy = h / boxHeight;
        double px = dpx * i;
        double py = dpy * j;
        double pSquared = px * px + py * py;
        double numerator = pSquared * dt / 2 / mass;

        return Complex.exponentiateImaginary( -numerator );
    }

    private Wavefunction ones( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, 1, 0 );
            }
        }
        return wavefunction;
    }


    private Wavefunction multiplyPointwise( Wavefunction a, Wavefunction b ) {
        Wavefunction result = new Wavefunction( a.getWidth(), a.getHeight() );
        for( int i = 0; i < result.getWidth(); i++ ) {
            for( int k = 0; k < result.getHeight(); k++ ) {
                result.setValue( i, k, a.valueAt( i, k ).times( b.valueAt( i, k ) ) );
            }
        }
        return result;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new SplitOperatorPropagator( physicalSystem, getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
    }

}

package edu.colorado.phet.quantumwaveinterference.model.propagators;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.Potential;
import edu.colorado.phet.quantumwaveinterference.model.Propagator;
import edu.colorado.phet.quantumwaveinterference.model.Wavefunction;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class RichardsonPropagator extends Propagator {
    private double simulationTime;
    private int timeStep;//the iteration number
//    private Wave wave;

    private double hbar;
    private double mass;
    private double epsilon;
    private Complex alpha;
    private Complex beta;
    private Complex[][] betaeven;
    private Complex[][] betaodd;

    protected Wavefunction copy;

    public RichardsonPropagator( double TAU, Potential potential, double hbar, double mass ) {
        super( potential );
        setDeltaTime( TAU );
//        this.wave = wave;
        setPotential( potential );
        simulationTime = 0.0;
        timeStep = 0;
        this.mass = mass;
        this.hbar = hbar;
//        hbar = 1;
//        mass = 1;

//        setDeltaTime( 0.8* mass / hbar );
//        setDeltaTime( 0.95 * mass / hbar );
//        setDeltaTime( 0.5);
//        setDeltaTime( 1.0 * mass / hbar );
        betaeven = new Complex[0][0];
        betaodd = new Complex[0][0];
        update();

//        showControlDialog();
    }


    public void setHBar( double hBar ) {
        this.hbar = hBar;
        update();
    }

    public void setMass( double mass ) {
        this.mass = mass;
        update();
    }

    public void showControlDialog() {
        JFrame frame = new JFrame();
        final ModelSlider modelSlider = new ModelSlider( "dt", QWIResources.getString( "unitless" ), 0, 2, getDeltaTime(), new DecimalFormat( "0.000" ) );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setDeltaTime( modelSlider.getValue() );
                System.out.println( "getDeltaTime() = " + getDeltaTime() );
            }
        } );
        frame.getContentPane().add( modelSlider );
        frame.pack();
        frame.setVisible( true );
    }

    public Map getModelParameters() {
        Map map = new HashMap();
        map.put( "deltaTime", "" + getDeltaTime() );
        map.put( "propagator_mass", "" + mass );
        map.put( "hbar", "" + hbar );
        return map;
    }

    public void setValue( int i, int j, double real, double imag ) {
    }

    public void update() {
        epsilon = hbar * getDeltaTime() / mass;
//        epsilon = toEpsilon( getDeltaTime() );
        alpha = createAlpha();
        beta = createBeta();
        if( betaeven == null ) {
            return;
        }
        for( int i = 0; i < betaeven.length; i++ ) {
            for( int j = 0; j < betaeven[i].length; j++ ) {
                betaeven[i][j] = new Complex();
                betaodd[i][j] = new Complex();
                if( ( i + j ) % 2 == 0 ) {
                    betaeven[i][j] = beta;
                }
                else {
                    betaodd[i][j] = beta;
                }
            }
        }
    }

    protected Complex createAlpha() {
        return new Complex( ( 1 + Math.cos( epsilon ) ) / 2.0, -Math.sin( epsilon ) / 2.0 );//from the paper
    }

    protected Complex createBeta() {
        return new Complex( ( 1 - Math.cos( epsilon ) ) / 2.0, Math.sin( epsilon ) / 2.0 );
    }

    public void propagate( Wavefunction w ) {
//        int numSteps = ;
//        setDeltaTime( 0.0822/numSteps);
//        for( int i = 0; i < numSteps; i++ ) {
        propagateOnce( w );
//        }
    }

    private void propagateOnce( Wavefunction w ) {
        int nx = w.getWidth();
        if( betaeven.length != w.getWidth() ) {
            betaeven = new Complex[nx][nx];
            betaodd = new Complex[nx][nx];
            update();
        }
        prop2D( w );
        simulationTime += getDeltaTime();
        timeStep++;
    }

    protected void prop2D( Wavefunction w ) {
        copy = new Wavefunction( w.getWidth(), w.getHeight() );
        applyPotential( w );
        stepIt( w, 0, -1 );
        stepIt( w, 0, 1 );
        stepIt( w, 1, 0 );
        stepIt( w, -1, 0 );
    }

    Complex aTemp = new Complex();
    Complex bTemp = new Complex();
    Complex cTemp = new Complex();

    protected void stepIt( Wavefunction w, int dx, int dy ) {
        w.copyTo( copy );
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                stepIt( w, i, j, dx, dy );
            }
        }
        for( int i = 0; i < w.getWidth(); i++ ) {
            stepItConstrained( w, i, 0, dx, dy );
            stepItConstrained( w, i, w.getHeight() - 1, dx, dy );
        }
        for( int j = 1; j < w.getHeight(); j++ ) {//todo should this start at 0?
            stepItConstrained( w, 0, j, dx, dy );
            stepItConstrained( w, w.getWidth() - 1, j, dx, dy );
        }
    }

    private void stepItConstrained( Wavefunction w, int i, int j, int dx, int dy ) {
        int nxPlus = ( i + dx + w.getWidth() ) % w.getWidth();
        int nyPlus = ( j + dy + w.getHeight() ) % w.getHeight();

        int nxMinus = ( i - dx + w.getWidth() ) % w.getWidth();
        int nyMinus = ( j - dy + w.getHeight() ) % w.getHeight();

        aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
        bTemp.setToProduct( betaeven[i][j], copy.valueAt( nxPlus, nyPlus ) );
        cTemp.setToProduct( betaodd[i][j], copy.valueAt( nxMinus, nyMinus ) );
        w.valueAt( i, j ).setToSum( aTemp, bTemp, cTemp );
    }

    private void stepIt( Wavefunction w, int i, int j, int dx, int dy ) {
        boolean even = ( i + j ) % 2 == 0;
        if( even ) {
            aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
            bTemp.setToProduct( betaeven[i][j], copy.valueAt( i + dx, j + dy ) );
            w.valueAt( i, j ).setToSum( aTemp, bTemp );
        }
        else {
            aTemp.setToProduct( alpha, copy.valueAt( i, j ) );
            cTemp.setToProduct( betaodd[i][j], copy.valueAt( i - dx, j - dy ) );
            w.valueAt( i, j ).setToSum( aTemp, cTemp );
        }
    }

    Complex potTemp = new Complex();
    Complex waveTemp = new Complex();

    protected void applyPotential( Wavefunction w ) {//todo ignore damping region
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                double pot = getPotential().getPotential( i, j, timeStep );
                //todo: do we need this original potential code?  It failed for neutrons (maybe bad hbar?)
//                potTemp.setValue( Math.cos( pot * getDeltaTime() / hbar ), -Math.sin( pot * getDeltaTime() / hbar ) );
//                waveTemp.setValue( w.valueAt( i, j ) );
//                w.valueAt( i, j ).setToProduct( waveTemp, potTemp );
                if( pot > 10 ) {
                    w.valueAt( i, j ).setValue( 0, 0 );
                }
            }
        }
    }

    public void setDeltaTime( double deltaTime ) {
        super.setDeltaTime( deltaTime );
        update();
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new RichardsonPropagator( getDeltaTime(), getPotential(), getHBar(), getMass() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public double getEpsilon() {
        return epsilon;
    }

//    public Wave getWave() {
//        return wave;
//    }


    public double getHBar() {
        return hbar;
    }

    public double getMass() {
        return mass;
    }
}
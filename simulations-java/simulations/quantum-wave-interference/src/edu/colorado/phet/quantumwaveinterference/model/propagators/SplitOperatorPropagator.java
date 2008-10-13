/*  */
package edu.colorado.phet.quantumwaveinterference.model.propagators;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.*;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;
import edu.colorado.phet.quantumwaveinterference.view.complexcolormaps.VisualColorMap3;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 28, 2005
 * Time: 3:35:37 PM
 */

public class SplitOperatorPropagator extends Propagator {

    private double scale = 0.004;
    private WaveDebugger momentumDisplay;

    private Wavefunction expT = null;
    private Wavefunction expV = null;
    private Wavefunction temp = null;
    private QWIModel QWIModel;

    static boolean displayMomentumWavefunction = false;

    public SplitOperatorPropagator( QWIModel QWIModel, Potential potential ) {
        super( potential );
        this.QWIModel = QWIModel;
        if( QWIModel != null ) {
            QWIModel.addListener( new QWIModel.Adapter() {
                public void potentialChanged() {
                    updateExpV();
                }
            } );
        }
    }

    public void activate() {
        super.activate();
        addDebugControls();
    }

    public void deactivate() {
        super.deactivate();
    }

    private void addDebugControls() {
        JFrame controls = new JFrame( QWIResources.getString( "som.controls" ) );
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        DecimalFormat textFieldFormat = new DecimalFormat( "0.0000000" );
        final ModelSlider modelSlider = new ModelSlider( QWIResources.getString( "scale" ), QWIResources.getString( "1.p.2" ), 0, 0.1, scale, textFieldFormat );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setScale( modelSlider.getValue() );
            }

        } );
//        scale = modelSlider.getValue();
        verticalLayoutPanel.add( modelSlider );
        final JCheckBox comp = new JCheckBox( QWIResources.getString( "show.momenta" ) );
        comp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                displayMomentumWavefunction = comp.isSelected();
            }
        } );
        verticalLayoutPanel.add( comp );
        controls.setContentPane( verticalLayoutPanel );
        controls.pack();
        controls.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        controls.setVisible( true );

        initMomentumDisplay();
    }

    private void initMomentumDisplay() {
        momentumDisplay = new WaveDebugger( QWIResources.getString( "wave" ), new Wavefunction( 50, 50 ), 3, 3 );
        momentumDisplay.setComplexColorMap( new VisualColorMap3() );
        momentumDisplay.setVisible( true );
    }

    private void setScale( double value ) {
        this.scale = value;
        expT = null;
    }

    public void propagate( Wavefunction w ) {
        throw new RuntimeException( "QWIFFT2D not defined (see opensourcephysics dependence)" );
//        Wavefunction expV = getExpV( w.getWidth(), w.getHeight() );
//        Wavefunction expT = getExpT( w.getWidth(), w.getHeight() );
//        Wavefunction temp = getTempWavefunction( w.getWidth(), w.getHeight() );
//        multiplyPointwise( expV, w, temp );
//        Wavefunction phi = QWIFFT2D.forwardFFT( temp );
//        if( displayMomentumWavefunction ) {
//            if( momentumDisplay == null ) {
//                initMomentumDisplay();
//            }
//            momentumDisplay.setWavefunction( phi.copy(), new VisualColorMap3() );
//        }
//        multiplyPointwise( expT, phi, temp );
//        Wavefunction psi2 = QWIFFT2D.inverseFFT( temp );
//        multiplyPointwise( expV, psi2, temp );
//        w.setWavefunction( temp );
    }

    private Wavefunction getTempWavefunction( int width, int height ) {
        if( temp == null || temp.getWidth() != width || temp.getHeight() != height ) {
            temp = new Wavefunction( width, height );
        }
        return temp;
    }

    private void updateExpV() {
        expV = null;
    }

    private Wavefunction getExpV( int width, int height ) {
        if( expV == null || expV.getWidth() != width || expV.getHeight() != height ) {
            expV = createExpV( width, height );
        }
        return expV;
    }

    private Wavefunction createExpV( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, getExpVValue( i, k ) );
            }
        }
        return wavefunction;
    }

    private Complex getExpVValue( int i, int k ) {
        Potential p = getPotential();
        double dt = 1.0;
        double potential = p.getPotential( i, k, 0 );
        if( potential != 0.0 ) {
            potential = Double.MAX_VALUE / 2;
        }
        return Complex.exponentiateImaginary( -potential * dt / 2.0 );
    }

    private Wavefunction getExpT( int width, int height ) {
        if( expT == null || expT.getWidth() != width || expT.getHeight() != height ) {
            expT = createExpT( width, height );
        }
        return expT;
    }

    private Wavefunction createExpT( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, getExpTValueWraparound( i, k, wavefunction ) );
            }
        }
        return wavefunction;
    }

    private Complex getExpTValueWraparound( int i, int j, Wavefunction wavefunction ) {
        double px = i;
        double py = j;
        if( i >= wavefunction.getWidth() / 2 ) {
            px = wavefunction.getWidth() - i;
        }
        if( j >= wavefunction.getHeight() / 2 ) {
            py = wavefunction.getHeight() - j;
        }

        double psquared = px * px + py * py;
        double ke = psquared * scale;
        //scale is directly or inversely proportional to each of the following:
        // the physical area of the box L*W (in meters)
        // the time step dt (in seconds)
        //the mass of the particle (kg)
        return Complex.exponentiateImaginary( -ke );
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

    private Wavefunction multiplyPointwise( Wavefunction a, Wavefunction b, Wavefunction result ) {
        for( int i = 0; i < result.getWidth(); i++ ) {
            for( int k = 0; k < result.getHeight(); k++ ) {
                Complex x = a.wavefunction[i][k];
                Complex y = b.wavefunction[i][k];
                double real = x.real * y.real - x.imag * y.imag;
                double imag = x.real * y.imag + x.imag * y.real;
                result.wavefunction[i][k].setValue( real, imag );
            }
        }
        return result;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new SplitOperatorPropagator( QWIModel, getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
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
}

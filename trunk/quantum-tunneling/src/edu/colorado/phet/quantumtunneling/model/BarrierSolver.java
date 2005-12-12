/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.util.Complex;
import edu.colorado.phet.quantumtunneling.util.MutableComplex;


/**
 * BarrierSolver is a closed-form solution to the wave function equation
 * for barrier potentials. Single and double barriers are supported.
 * <p>
 * For single barriers:
 * <code>
 * Region1: psi(x,t) = ( e^(i*k1*x1) + B * e^(-i*k1*x1) ) * e^(-i*E*t/h)
 * Region2: psi(x,t) = ( C * e^(i*k2*x2) + D * e^(-i*k2*x2) ) * e^(-i*E*t/h)
 * Region3: psi(x,t) = ( F * e^(i*k3*x3) ) * e^(-i*E*t/h)
 * </code>
 * <p>
 * For double barriers:
 * <code>
 * Region1: psi(x,t) = ( e^(i*k1*x1) + B * e^(-i*k1*x1) ) * e^(-i*E*t/h)
 * Region2: psi(x,t) = ( C * e^(i*k2*x2) + D * e^(-i*k2*x2) ) * e^(-i*E*t/h)
 * Region3: psi(x,t) = ( F * e^(i*k3*x3) + G * e^(-i*k3*x3) ) * e^(-i*E*t/h)
 * Region4: psi(x,t) = ( H * e^(i*k4*x4) + I * e^(-i*k4*x4) ) * e^(-i*E*t/h)
 * Region5: psi(x,t) = ( J * e^(i*k5*x5) ) * e^(-i*E*t/h)
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarrierSolver extends AbstractSolver {
    
    /**
     * Constructor.
     * 
     * @param te
     * @param pe
     */
    public BarrierSolver( TotalEnergy te, BarrierPotential pe ) {
        super( te, pe );
        assert( pe.getNumberOfBarriers() == 1 );
    }

    /**
     * Solves the wave function.
     * 
     * @param x position, in nm
     * @param t time, in fs
     */
    public Complex solve( final double x, final double t ) {
        int numberOfBarriers = ((BarrierPotential)getPotentialEnergy()).getNumberOfBarriers();
        if ( numberOfBarriers == 1 ) {
            return solveSingle( x, t );
        }
        else if ( numberOfBarriers == 2 ) {
            return solveDouble( x, t );
        }
        else {
            throw new IllegalStateException( "no solution for " + numberOfBarriers + " barriers" );
        }
    }
    
    /*
     * Solves the wave function for a single barrier.
     */
    private Complex solveSingle( final double x, final double t ) {
        Complex result = null;

        final double E = getTotalEnergy().getEnergy();
        
        if ( isLeftToRight() && E < getPotentialEnergy().getEnergy( 0 ) ) {
            result = new Complex( 0, 0 );
        }
        else if ( isRightToLeft() && E < getPotentialEnergy().getEnergy( 1 ) ) {
            result = new Complex( 0, 0 );
        }
        else {
            // boundary between regions
            final double x1 = getPotentialEnergy().getEnd( 0 );
            final double x2 = getPotentialEnergy().getEnd( 1 );
            
            Complex k1 = getK( 0 );
            Complex k2 = getK( 1 );
            Complex k3 = getK( 2 );
            
            int regionIndex = getPotentialEnergy().getRegionIndexAt( x );
            
            if ( regionIndex == 0 ) {
                   
                /*
                 * B =
                 *  ( e^(2*i*k1*x1) * ( e^(2*i*k2*x2) * (k1 + k2) * (k2 - k3) - e^(2*i*k2*x1) * (-k1 + k2) * (k2 + k3) ) ) /
                 *  ( -( e^(2*i*k2*x2) * (-k1 + k2) * (k2 - k3) ) + e^(2*i*k2*x1) * (k1 + k2) * (k2 + k3) )
                 */
                MutableComplex B = new MutableComplex();
                {
                    // Common terms
                    MutableComplex t1 = new MutableComplex( Complex.I ); // e^(2*i*k1*x1)
                    t1.multiply( 2 * x1 );
                    t1.multiply( k1 );
                    t1.exp();
                    
                    MutableComplex t2 = new MutableComplex( Complex.I ); // e^(2*i*k2*x2)
                    t2.multiply( 2 * x2 );
                    t2.multiply( k2 );
                    t2.multiply( t1 );
                    t2.exp();
                    
                    MutableComplex t3 = new MutableComplex( Complex.I ); // e^(2*i*k2*x1)
                    t3.multiply( 2 * x1 );
                    t3.multiply( k2 );
                    t3.exp();
                    
                    // Numerator
                    MutableComplex numerator = new MutableComplex();
                    {
                        MutableComplex t4 = new MutableComplex(); // e^(2*i*k2*x2) * (k1 + k2) * (k2 - k3)
                        t4.setValue( t2 );
                        t4.multiply( k1.getAdd( k2 ) );
                        t4.multiply( k2.getSubtract( k3 ) );
                        
                        MutableComplex t5 = new MutableComplex(); // e^(2*i*k2*x1) * (-k1 + k2) * (k2 + k3)
                        t5.setValue( t3 );
                        t5.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
                        t5.multiply( k2.getAdd( k3 ) );
                        
                        numerator.setValue( t4 );
                        numerator.subtract( t5 );
                        numerator.multiply( t1 );
                    }
                    
                    // Denominator
                    MutableComplex denominator = new MutableComplex();
                    {
                        MutableComplex t6 = new MutableComplex(); // -( e^(2*i*k2*x2) * (-k1 + k2) * (k2 - k3) )
                        t6.setValue( t2 );
                        t6.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
                        t6.multiply( k2.getSubtract( k3 ) );
                        t6.multiply( -1 );
                        
                        MutableComplex t7 = new MutableComplex(); // e^(2*i*k2*x1) * (k1 + k2) * (k2 + k3)
                        t7.setValue( t3 );
                        t7.multiply( k1.getAdd( k2 ) );
                        t7.multiply( k2.getAdd( k3 ) );
                        
                        denominator.setValue( t6 );
                        denominator.add( t7 );
                    }
                    
                    // B
                    B.setValue( numerator );
                    B.divide( denominator );
                }
                
                // psi1(x,t)
                {
                    Complex term1 = commonTerm1( x, regionIndex ); // e^(ikx)
                    Complex term2 = commonTerm2( x, regionIndex ); // e^(-ikx)
                    Complex term3 = commonTerm3( t, E ); // e^(-i*E*t/h)
                    Complex rightMoving = term1.getMultiply( term3 );
                    Complex leftMoving = B.getMultiply( term2 ).getMultiply( term3 );
                    result = rightMoving.getAdd( leftMoving );
                }
            }
            else if ( regionIndex == 1 ) {
                
                /*
                 * C =
                 *  ( 2 * e^(i*(k1 + k2)*x1) * k1 * (k2 + k3) ) /
                 *  ( -( e^(2*i*k2*x2) * (-k1 + k2) * (k2 - k3) ) + e^(2*i*k2*x1) * (k1 + k2) * (k2 + k3) )
                 */
                MutableComplex C = new MutableComplex();
                {
                    
                }
                
                /*
                 * D =  
                 *  ( 2 * e^(I*((k1 + k2)*x1 + 2*k2*x2)) * k1 * (k2 - k3) ) /
                 *  ( -( e^(2*I*k2*x2) * (-k1 + k2) * (k2 - k3) ) + e^(2*I*k2*x1) * (k1 + k2) * (k2 + k3) )
                 */
                MutableComplex D = new MutableComplex();
                {
                    
                }
                
                // psi2(x,t)
                {
                    Complex term1 = commonTerm1( x, regionIndex ); // e^(ikx)
                    Complex term2 = commonTerm2( x, regionIndex ); // e^(-ikx)
                    Complex term3 = commonTerm3( t, E ); // e^(-i*E*t/h)
                    Complex rightMoving = C.getMultiply( term1.getMultiply( term3 ) );
                    Complex leftMoving = D.getMultiply( term2 ).getMultiply( term3 );
                    result = rightMoving.getAdd( leftMoving );
                }
            }
            else if ( regionIndex == 2 ) {

                /* F =
                 *  ( 4 * e^(i*(k1*x1 - k3*x2 + k2*(x1 + x2))) * k1 * k2 ) /
                 *  ( -( e^(2*i*k2*x2) * (-k1 + k2) * (k2 - k3) ) + e^(2*i*k2*x1) * (k1 + k2) * (k2 + k3) ) )
                 */
                MutableComplex F = new MutableComplex();
                {
                    // Numerator
                    MutableComplex numerator = new MutableComplex();
                    {
                        MutableComplex t1 = new MutableComplex(); // e^(i*(k1*x1 - k3*x2 + k2*(x1 + x2)))
                        t1.setValue( Complex.I.getMultiply( k1.getMultiply( x1 ) ) );
                        t1.subtract( Complex.I.getMultiply( k3.getMultiply( x2 ) ) );
                        t1.add( Complex.I.getMultiply( k2.getMultiply( x1 + x2 ) ) );
                        t1.exp();

                        numerator.setValue( 4 );
                        numerator.multiply( t1 );
                        numerator.multiply( k1 );
                        numerator.multiply( k2 );
                    }
                    
                    // Denominator
                    MutableComplex denominator = new MutableComplex();
                    {
                        MutableComplex t2 = new MutableComplex( Complex.I ); // -( e^(2*i*k2*x2) * (-k1 + k2) * (k2 - k3) )
                        t2.multiply( 2 * x2 );
                        t2.multiply( k2 );
                        t2.exp();
                        t2.multiply( k1.getMultiply( -1 ).getAdd( k2 ) );
                        t2.multiply( k2.getSubtract( k3 ) );
                        t2.multiply( -1 );
                        
                        MutableComplex t3 = new MutableComplex( Complex.I ); // e^(2*i*k2*x1) * (k1 + k2) * (k2 + k3)
                        t3.multiply( 2 * x1 );
                        t3.multiply( k2 );
                        t3.exp();
                        t3.multiply( k1.getAdd( k2 ) );
                        t3.multiply( k2.getAdd( k3 ) );

                        denominator.setValue( t2 );
                        denominator.add( t3 );
                    }
                    
                    // F
                    F.setValue( numerator );
                    F.divide( denominator );
                }
                
                // psi3(x,t)
                {
                    Complex term1 = commonTerm1( x, regionIndex ); // e^(ikx)
                    Complex term3 = commonTerm3( t, E ); // e^(-i*E*t/h)
                    Complex rightMoving = F.getMultiply( term1 );
                    result = rightMoving.getMultiply( term3 );
                }
            }
            else {
                // outside of the potential energy space
            }
        }
        
        return result;
    }
    
    /*
     * Solves the wave function for a double barrier.
     */
    private Complex solveDouble( final double x, final double t ) {
        return null;//XXX
    }
}

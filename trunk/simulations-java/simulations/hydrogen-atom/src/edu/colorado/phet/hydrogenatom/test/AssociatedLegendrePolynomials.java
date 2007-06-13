package edu.colorado.phet.hydrogenatom.test;

import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;

import java.util.ArrayList;

/**
 * This feasibility test code for computing the associated Legendre polynomials is here to facilitate discussion.
 * Applications shouldn't depend on this in its current state.
 *
 * My model starts to differ from Wolfram and Oliver at L>=7, is this ever a case for our simulation?
 * Is it possible that they are making an approximation that diverges?  My solution doesn't make approximations, but
 * I'm not sure it's correct. 
 * 
 * Author: Sam Reid
 * Jun 13, 2007, 6:55:28 AM
 *
 * Used the definition at:
 * http://mathworld.wolfram.com/LegendrePolynomial.html
 *
 * Note: to compare with Mathematica online, you can use:
 * x^2*(3)*( LegendreP[7,3,-0.99])
 * as input to The Integrator:
 * http://integrals.wolfram.com/index.jsp 
 */
public class AssociatedLegendrePolynomials {
    public static void main( String[] args ) {
        int successCount = 0;
        int totalCount = 0;
        for( int L = 0; L < 7; L++ ) {//my model starts to differ from Wolfram and Oliver at L>=7, is this ever a case for our simulation?
            for( int m = 0; m <= L; m++ ) {
                for( double x = -1.0; x <= 1.0; x += 0.01 ) {

                    double a = Double.NaN;//todo; make SchrodingerModel.getAssociatedLegendrePolynomial( L, m, x ) public
//                    double a = SchrodingerModel.getAssociatedLegendrePolynomial( L, m, x );
                    if (Double.isNaN( a)){
                        System.out.println( "make SchrodingerModel.getAssociatedLegendrePolynomial( L, m, x ) public to run this comparison" );
                        return;
                    }
                    double b = getALP2( L, m, x );
                    double diff = Math.abs( a - b );
                    totalCount++;
                    if( diff < 1E-7 ) {
                        System.out.println( "success" );
                        successCount++;
                    }
                    else {
                        System.out.println( "fail L=" + L + ", m=" + m + ", x=" + x );
                        System.out.println( "a = " + a + ", b=" + b );
                    }

                }
            }
        }
        System.out.println( "successCount = " + successCount );
        System.out.println( "totalCount = " + totalCount );

    }

    static class Term {
        int power;
        int coeff;

        public Term( int power, int coeff ) {
            this.power = power;
            this.coeff = coeff;
        }

        public int getPower() {
            return power;
        }

        public int getCoeff() {
            return coeff;
        }

        public void derive( int numDer ) {
            for( int i = 0; i < numDer; i++ ) {
                derive();
            }
        }

        private void derive() {
            if( power == 0 ) {
                power = 0;
                coeff = 0;
            }
            else {
                int newCoeff = coeff * power;
                int newPower = power - 1;
                this.coeff = newCoeff;
                this.power = newPower;
            }
        }

        public double eval( double x ) {
            return Math.pow( x, power ) * coeff;
        }
    }

    public static double getALP2( int L, int m, double x ) {
        ArrayList productTerms = new ArrayList();
        productTerms.add( new Term( 0, 1 ) );
        for( int i = 0; i < L; i++ ) {
            //x^2-1 times each term on left side
            ArrayList terms = new ArrayList();
            for( int k = 0; k < productTerms.size(); k++ ) {
                Term term = (Term)productTerms.get( k );
                terms.add( new Term( term.getPower() + 2, term.getCoeff() ) );
                terms.add( new Term( term.getPower(), term.getCoeff() * -1 ) );
            }
            productTerms = terms;
        }
        for( int k = 0; k < productTerms.size(); k++ ) {
            Term t = (Term)productTerms.get( k );
            t.derive( L + m );
        }
        //wolfram says there is a sign convention difference here
//        double legendre=Math.pow(-1,m)*Math.pow(2,-L)/fact( L)*Math.pow(1-x*x,m/2.0)*eval(productTerms,x);
        double legendre =
                Math.pow( -1, m ) *
                Math.pow( 2, -L ) / fact( L ) * Math.pow( 1 - x * x, m / 2.0 ) * eval( productTerms, x );
        return legendre;
    }


    private static double eval( ArrayList productTerms, double x ) {
        double sum = 0;
        for( int i = 0; i < productTerms.size(); i++ ) {
            Term term = (Term)productTerms.get( i );
            sum += term.eval( x );
        }
        return sum;
    }

    public static double fact( int a ) {
        return ( a == 0 || a == 1 ) ? 1 : a * fact( a - 1 );
    }
}

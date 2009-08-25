
package edu.colorado.phet.titration.prototype;

import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.common.phetcommon.math.Complex;
import edu.colorado.phet.titration.test.RootTest;

/*
 * * Titration prototype model, based on Kelly Lancaster's Octave code.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TPModel {

    private static final double Kw = TPConstants.Kw;
    private static final int ROOTS_ITERATIONS = 30;
    private static final double ROOTS_THRESHOLD = 0.00001;

    private TPModel() {}

    /*
    ## strong base/strong acid

    function strongbase (Ca,Cb,Va,Vb)
    Kw = 1e-14;

    ## solve quadratic eqn for H: a*H^2 + b*H + c = 0;
    a = -(Va + Vb);
    b = Ca.*Va - Cb.*Vb;
    c = Kw.*(Va + Vb);
    #H1 = (-b + sqrt(b.^2 - 4.*a.*c))./(2.*a);
    H2 = (-b - sqrt(b.^2 - 4.*a.*c))./(2.*a);

    ## find pH at each Va value
    #pH1 = -log10(H1);
    pH2 = -log10(H2);

    ## plot pH versus Va
    #plot(Va,pH1);
    plot(Va,pH2); axis ([0,75,0,14]); xlabel('Va'); ylabel('pH');

    endfunction
     */
    public static final double strongBase( double Ca, double Cb, double Va, double Vb ) {
        double a = -( Va + Vb );
        double b = Ca * Va - Cb * Vb;
        double c = Kw * ( Va + Vb );
        double H2 = ( -b - Math.sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
        double pH2 = -Math.log10( H2 );
        return pH2;
    }

    /*
     ## weak base/strong acid

    function weakbase (Ca,Cb,Va,Vb,Kb)
    Kw = 1e-14;

    ## solve cubic eqn for H: a*H^3 + b*H^2 + c*H + d = 0
    a = -(Va + Vb);
    b = Ca.*Va - Cb.*Vb - (Kw./Kb).*(Va + Vb);
    c = Kw.*(Va + Vb) + (Ca.*Va.*Kw)./Kb;
    d = ((Kw.^2)./Kb).*(Va + Vb);

    for indx = 1:length(Va)
    x = roots([a(indx), b(indx), c(indx), d(indx)]);
    x = sort(x);
    H1(indx) = x(1);
    H2(indx) = x(2);
    H3(indx) = x(3);
    end

    ## find pH at each Va value
    #pH1 = -log10(H1);
    #pH2 = -log10(H2);
    pH3 = -log10(H3);

    ## plot pH versus Va
    #plot(Va,pH1);
    #plot(Va,pH2);
    plot(Va,pH3); axis ([0,75,0,14]); xlabel('Va'); ylabel('pH');

    endfunction
     */
    public static double weakBase( double Ca, double Cb, double Va, double Vb, double Kb ) {
        double a = -( Va + Vb );
        double b = Ca * Va - Cb * Vb - ( Kw / Kb ) * ( Va + Vb );
        double c = Kw * ( Va + Vb ) + ( Ca * Va * Kw ) / Kb;
        double d = ( ( Kw * Kw ) / Kb ) * ( Va + Vb );
        double[] coefficients = new double[] { a, b, c, d }; // high to low
        return pH( coefficients );
    }

    /*
    ## strong acid/strong base

    function strongacid (Ca,Cb,Va,Vb)
    Kw = 1e-14;

    ## solve quadratic eqn for H: a*H^2 + b*H + c = 0;
    a = Va + Vb;
    b = Cb.*Vb - Ca.*Va;
    c = -Kw.*(Va + Vb);
    H1 = (-b + sqrt(b.^2 - 4.*a.*c))./(2.*a);
    #H2 = (-b - sqrt(b.^2 - 4.*a.*c))./(2.*a);

    ## find pH at each Vb value
    pH1 = -log10(H1);
    #pH2 = -log10(H2);

    ## plot pH versus Vb
    plot(Vb,pH1); axis ([0,75,0,14]); xlabel('Vb'); ylabel('pH');
    #plot(Vb,pH2);

    endfunction
     */
    public static final double strongAcid( double Ca, double Cb, double Va, double Vb ) {
        double a = Va + Vb;
        double b = Cb * Vb - Ca * Va;
        double c = -Kw * ( Va + Vb );
        double H1 = ( -b + Math.sqrt( b * b - 4 * a * c ) ) / ( 2 * a );
        double pH1 = -Math.log10( H1 );
        return pH1;
    }

    /*
    ## weak acid/strong base

    function weakacid (Ca,Cb,Va,Vb,Ka)
    Kw = 1e-14;

    ## solve cubic eqn for H: a*H^3 + b*H^2 + c*H + d = 0
    a = Va + Vb;
    b = Cb.*Vb + Va.*Ka + Vb.*Ka;
    c = Cb.*Vb.*Ka - Ca.*Va.*Ka - Va.*Kw - Vb.*Kw;
    d = -Kw.*Ka.*(Va + Vb);

    for indx = 1:length(Vb)
    x = roots([a(indx), b(indx), c(indx), d(indx)]);
    x = sort(x);
    H1(indx) = x(1);
    H2(indx) = x(2);
    H3(indx) = x(3);
    end

    ## find pH at each Vb value
    #pH1 = -log10(H1);
    #pH2 = -log10(H2);
    pH3 = -log10(H3);

    ## plot pH versus Vb
    #plot(Vb,pH1);
    #plot(Vb,pH2);
    plot(Vb,pH3); axis ([0,75,0,14]); xlabel('Vb'); ylabel('pH');

    endfunction
    */
    public static final double weakAcid( double Ca, double Cb, double Va, double Vb, double Ka ) {
        double a = Va + Vb;
        double b = Cb * Vb + Va * Ka + Vb * Ka;
        double c = Cb * Vb * Ka - Ca * Va * Ka - Va * Kw - Vb * Kw;
        double d = -Kw * Ka * ( Va + Vb );
        double[] coefficients = new double[] { a, b, c, d }; // high to low
        return pH( coefficients );
    }

    /*
     ## diprotic acid/strong base

    function diproticacid (Ca,Cb,Va,Vb,K1,K2)
    Kw = 1e-14;

    ## solve quartic eqn for H: a*H^4 + b*H^3 + c*H^2 + d*H + e = 0
    a = Va + Vb;
    b = Cb.*Vb + Va.*K1 + Vb.*K1;
    c = Cb.*Vb.*K1 - Ca.*Va.*K1 + Va.*K1.*K2 + Vb.*K1.*K2 - Va.*Kw - Vb.*Kw;
    d = Cb.*Vb.*K1.*K2 - 2*Ca.*Va.*K1.*K2 - Va.*K1.*Kw - Vb.*K1.*Kw;
    e = -(K1.*K2.*Kw).*(Va + Vb);

    for indx = 1:length(Vb)
    x = roots([a(indx), b(indx), c(indx), d(indx), e(indx)]);
    x = sort(x);
    H1(indx) = x(1);
    H2(indx) = x(2);
    H3(indx) = x(3);
    H4(indx) = x(4);
    end

    ## find pH at each Vb value
    #pH1 = -log10(H1);
    #pH2 = -log10(H2);
    #pH3 = -log10(H3);
    pH4 = -log10(H4);

    ## plot pH versus Vb
    #plot(Vb,pH1);
    #plot(Vb,pH2);
    #plot(Vb,pH3);
    plot(Vb,pH4); axis ([0,75,0,14]); xlabel('Vb'); ylabel('pH');

    endfunction
     */
    public static final double diproticAcid( double Ca, double Cb, double Va, double Vb, double Ka1, double Ka2 ) {
        double a = Va + Vb;
        double b = Cb * Vb + Va * Ka1 + Vb * Ka1;
        double c = Cb * Vb * Ka1 - Ca * Va * Ka1 + Va * Ka1 * Ka2 + Vb * Ka1 * Ka2 - Va * Kw - Vb * Kw;
        double d = Cb * Vb * Ka1 * Ka2 - 2 * Ca * Va * Ka1 * Ka2 - Va * Ka1 * Kw - Vb * Ka1 * Kw;
        double e = -( Ka1 * Ka2 * Kw ) * ( Va + Vb );
        double[] coefficients = new double[] { a, b, c, d, e }; // high to low
        return pH( coefficients );
    }

    /*
     ## triprotic acid/strong base

    function triproticacid (Ca,Cb,Va,Vb,K1,K2,K3)
    Kw = 1e-14;

    ## solve 5th-order eqn for H: a*H^5 + b*H^4 + c*H^3 + d*H^2 + e*H + f = 0
    a = Va + Vb;
    b = Cb.*Vb + Va.*K1 + Vb.*K1;
    c = -Ca.*Va.*K1 + Cb.*Vb.*K1 + Va.*K1.*K2 + Vb.*K1.*K2 - Va.*Kw - Vb.*Kw;
    d = -2*Ca.*Va.*K1.*K2 + Cb.*Vb.*K1.*K2 + Va.*K1.*K2.*K3 + Vb.*K1.*K2.*K3 - Va.*K1.*Kw - Vb.*K1.*Kw;
    e = -3*Ca.*Va.*K1.*K2.*K3 + Cb.*Vb.*K1.*K2.*K3 - Va.*K1.*K2.*Kw - Vb.*K1.*K2.*Kw;
    f = -Va.*K1.*K2.*K3.*Kw - Vb.*K1.*K2.*K3.*Kw;

    for indx = 1:length(Vb)
    x = roots([a(indx), b(indx), c(indx), d(indx), e(indx), f(indx)]);
    x = sort(x);
    H1(indx) = x(1);
    H2(indx) = x(2);
    H3(indx) = x(3);
    H4(indx) = x(4);
    H5(indx) = x(5);
    end

    ## find pH at each Vb value
    #pH1 = -log10(H1);
    #pH2 = -log10(H2);
    #pH3 = -log10(H3);
    #pH4 = -log10(H4);
    pH5 = -log10(H5);

    ## plot pH versus Vb
    #plot(Vb,pH1);
    #plot(Vb,pH2);
    #plot(Vb,pH3);
    #plot(Vb,pH4);
    plot(Vb,pH5); axis ([0,100,0,14]); xlabel('Vb'); ylabel('pH');

    endfunction
     */
    public static final double triproticAcid( double Ca, double Cb, double Va, double Vb, double Ka1, double Ka2, double Ka3 ) {
        double a = Va + Vb;
        double b = Cb * Vb + Va * Ka1 + Vb * Ka1;
        double c = -Ca * Va * Ka1 + Cb * Vb * Ka1 + Va * Ka1 * Ka2 + Vb * Ka1 * Ka2 - Va * Kw - Vb * Kw;
        double d = -2 * Ca * Va * Ka1 * Ka2 + Cb * Vb * Ka1 * Ka2 + Va * Ka1 * Ka2 * Ka3 + Vb * Ka1 * Ka2 * Ka3 - Va * Ka1 * Kw - Vb * Ka1 * Kw;
        double e = -3 * Ca * Va * Ka1 * Ka2 * Ka3 + Cb * Vb * Ka1 * Ka2 * Ka3 - Va * Ka1 * Ka2 * Kw - Vb * Ka1 * Ka2 * Kw;
        double f = -Va * Ka1 * Ka2 * Ka3 * Kw - Vb * Ka1 * Ka2 * Ka3 * Kw;
        double[] coefficients = new double[] { a, b, c, d, e, f }; // high to low
        return pH( coefficients );
    }

    /*
     * Given an array of complex roots, return a sorted array of the real values.
     */
    private static Double[] sortReal( Complex[] roots ) {
        ArrayList<Double> list = new ArrayList<Double>();
        for ( int i = 0; i < roots.length; i++ ) {
            list.add( new Double( roots[i].getReal() ) );
        }
        Collections.sort( list );
        return (Double[]) list.toArray( new Double[list.size()] );
    }

    /*
     * Given an array of coefficients (high to low) for an Nth order polynomial,
     * find the roots, sort them, and then calculate the pH based on the Nth root. 
     */
    private static final double pH( double[] coefficients ) {
        Complex[] polynomial = new Complex[coefficients.length];
        for ( int i = 0; i < coefficients.length; i++ ) {
            polynomial[i] = new Complex( coefficients[i], 0 );
        }
        Complex[] roots = RootTest.findRootsOptimizedDurandKerner( polynomial, ROOTS_ITERATIONS, ROOTS_THRESHOLD );
        Double[] rootsSorted = sortReal( roots );
        double H = rootsSorted[coefficients.length - 2];
        return -Math.log10( H );
    }
}

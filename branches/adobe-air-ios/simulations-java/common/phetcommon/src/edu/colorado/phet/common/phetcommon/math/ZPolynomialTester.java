// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import junit.framework.TestCase;

public class ZPolynomialTester extends TestCase {
    // 5x^2 + 1x^3
    private static final PolynomialTerm[] TEST_TERMS1 = new PolynomialTerm[] {
            PolynomialTerm.parsePolynomialTerm( "2x^2" ),
            PolynomialTerm.parsePolynomialTerm( "3x^2" ),
            PolynomialTerm.parsePolynomialTerm( "1x^3" )
    };

    // 4x^1 + 1x^2 + 2x^3
    private static final PolynomialTerm[] TEST_TERMS2 = new PolynomialTerm[] {
            PolynomialTerm.parsePolynomialTerm( "4x^1" ),
            PolynomialTerm.parsePolynomialTerm( "1x^2" ),
            PolynomialTerm.parsePolynomialTerm( "2x^3" )
    };

    private static final Polynomial TEST_POLY1 = new Polynomial( TEST_TERMS1 );
    private static final Polynomial TEST_POLY2 = new Polynomial( TEST_TERMS2 );

    public void testAllTermsPresent() {
        assertEquals( 4, TEST_POLY1.getTerms().length );
    }

    public void testLikeTermsAddedCorrectly() {
        assertEquals( 5, TEST_POLY1.getTerms()[2].getCoeff() );
    }

    public void testEval() {
        assertEquals( 28.0, TEST_POLY1.eval( 2 ), 0.0 );
    }

    public void testEquality() {
        assertEquals( new Polynomial( TEST_TERMS1 ), TEST_POLY1 );
        assertEquals( TEST_POLY1, new Polynomial( TEST_TERMS1 ) );
        assertFalse( TEST_POLY1.equals( TEST_POLY2 ) );
        assertFalse( new Polynomial( TEST_TERMS1 ).equals( TEST_POLY2 ) );
    }

    public void testStringParse() {
        assertEquals( TEST_POLY1, P( "5x^2 + 1x^3" ) );
    }

    public void testPlus() {
        assertEquals( P( "4x^1 + 6x^2 + 3x^3" ), TEST_POLY1.plus( TEST_POLY2 ) );
    }

    public void testDerive() {
        assertEquals( P( "10x^1 + 3x^2" ), TEST_POLY1.derive() );
    }

    public void testTimesTerm() {
        assertEquals( P( "10x^3 + 2x^4" ), TEST_POLY1.times( PolynomialTerm.parsePolynomialTerm( "2x^1" ) ) );
    }

    public void testPowWithZero() {
        assertEquals( new Polynomial( 1 ), TEST_POLY1.pow( 0 ) );
    }

    public void testPowWithNegative() {
        try {
            TEST_POLY1.pow( -1 );
        }
        catch ( IllegalArgumentException e ) {
        }
    }

    public void testTimesPolynomial() {
        assertEquals( P( "20x^3 + 9x^4 + 11x^5 + 2x^6" ), TEST_POLY1.times( TEST_POLY2 ) );
    }

    public void testSquare() {
        // (2x^2 + 1)^2 = 4x^4 + 4x^2 + 1
        assertEquals( P( "4x^4 + 4x^2 + 1x^0" ), P( "1x^0 + 2x^2" ).pow( 2 ) );
    }

    private static Polynomial P( String expr ) {
        return Polynomial.parsePolynomial( expr );
    }
}

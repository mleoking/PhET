// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import junit.framework.TestCase;

public class ZPolynomialTermTester extends TestCase {
    public void testGetters() {
        PolynomialTerm term = new PolynomialTerm( 2, 3 );

        assertEquals( 2, term.getPower() );
        assertEquals( 3, term.getCoeff() );
    }

    public void testStringParser() {
        PolynomialTerm term = PolynomialTerm.parsePolynomialTerm( "3x^2" );

        assertEquals( 2, term.getPower() );
        assertEquals( 3, term.getCoeff() );
    }

    public void testStringParserWithPlusSign() {
        PolynomialTerm term = PolynomialTerm.parsePolynomialTerm( "+ 3x^2" );

        assertEquals( 2, term.getPower() );
        assertEquals( 3, term.getCoeff() );
    }

    public void testStringParserWithMinusSign() {
        PolynomialTerm term = PolynomialTerm.parsePolynomialTerm( "- 3x^2" );

        assertEquals( 2, term.getPower() );
        assertEquals( -3, term.getCoeff() );
    }

    public void testToString() {
        assertEquals( "3x^2", PolynomialTerm.parsePolynomialTerm( "3x^2" ).toString() );
    }

    public void testEquality() {
        PolynomialTerm term = PolynomialTerm.parsePolynomialTerm( "3x^2" );
        PolynomialTerm identicalTerm = PolynomialTerm.parsePolynomialTerm( "3x^2" );
        PolynomialTerm differentTerm = PolynomialTerm.parsePolynomialTerm( "1x^2" );

        assertEquals( term, identicalTerm );
        assertEquals( identicalTerm, term );
        assertFalse( term.equals( differentTerm ) );
        assertFalse( identicalTerm.equals( differentTerm ) );
    }

    public void testDerivation() {
        PolynomialTerm term = PolynomialTerm.parsePolynomialTerm( "3x^2" );
        PolynomialTerm derivation = PolynomialTerm.parsePolynomialTerm( "6x^1" );

        assertEquals( derivation, term.derive() );
    }

    public void testDerivationOfConstant() {
        PolynomialTerm term = PolynomialTerm.parsePolynomialTerm( "3x^0" );

        assertEquals( PolynomialTerm.ZERO, term.derive() );
    }

    public void testDerivationOfZeroIsZero() {
        assertEquals( PolynomialTerm.ZERO, PolynomialTerm.ZERO.derive() );
    }

    public void testEval() {
        PolynomialTerm term = PolynomialTerm.parsePolynomialTerm( "3x^2" );

        assertEquals( 48.0, term.eval( 4 ), 0.0 );
    }

    public void testAdd() {
        PolynomialTerm term1 = PolynomialTerm.parsePolynomialTerm( "3x^2" );
        PolynomialTerm term2 = PolynomialTerm.parsePolynomialTerm( "4x^2" );
        PolynomialTerm sum = PolynomialTerm.parsePolynomialTerm( "7x^2" );

        assertEquals( sum, term1.plus( term2 ) );
    }

    public void testTimes() {
        PolynomialTerm term1 = PolynomialTerm.parsePolynomialTerm( "3x^2" );
        PolynomialTerm term2 = PolynomialTerm.parsePolynomialTerm( "4x^4" );

        PolynomialTerm prod = PolynomialTerm.parsePolynomialTerm( "12x^6" );

        assertEquals( prod, term1.times( term2 ) );
    }
}

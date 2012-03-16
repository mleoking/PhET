// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.test;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.reactionsandrates.model.MoleculeA;
import edu.colorado.phet.reactionsandrates.model.MoleculeB;
import edu.colorado.phet.reactionsandrates.model.SimpleMolecule;
import edu.colorado.phet.reactionsandrates.model.collision.ReactionSpring;
import edu.colorado.phet.reactionsandrates.model.collision.Spring;

import java.awt.geom.Point2D;

/**
 * TestSpring_A
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestSpring_A {

    public static void main( String[] args ) {
        MoleculeA mA = new MoleculeA();

        Point2D.Double fixedEnd = new Point2D.Double( 100, 50 );
        Spring spring = new Spring( 1, 30, fixedEnd, 0 );

        mA.setPosition( fixedEnd.getX() - mA.getRadius(), fixedEnd.getY() );
        spring.attachBodyAtSpringLength( mA, 0 );

        double pe = spring.getPotentialEnergy();
        System.out.println( "pe = " + pe );

        MoleculeB mB = new MoleculeB();
        mB.setPosition( fixedEnd.getX() + mB.getRadius(), fixedEnd.getY() );

        ReactionSpring rSpring = new ReactionSpring( 500, 50, 100, new SimpleMolecule[]{mA, mB},
                                                     true );
        System.out.println( "rSpring.getPotentialEnergy() = " + rSpring.getPotentialEnergy() );

    }
}

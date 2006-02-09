/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.collision;

import edu.colorado.phet.collision.SphereSphereExpert;

public class AtomAtomCollision extends SphereSphereExpert {
//public class AtomAtomCollision extends HardsphereCollision {

//    private Atom atom1;
//    private Atom atom2;
//
//    /**
//     * Provided so class can register a prototype with the CollisionFactory
//     */
//    private AtomAtomCollision() {
//        //NOP
//    }
//
//    public AtomAtomCollision( Atom atom1, Atom atom2 ) {
//        this.atom1 = atom1;
//        this.atom2 = atom2;
//    }
//
//    protected Vector2D getLoa( Particle particleA, Particle particleB ) {
//        Point2D posA = particleA.getPosition();
//        Point2D posB = particleB.getPosition();
//        return new Vector2D.Double( posA.getX() - posB.getX(),
//                             posA.getY() - posB.getY() );
//    }
//
//    //
//    // Abstract methods
//    //
////    public void collide() {
////        super.collide( atom1, atom2, getLoa( atom1, atom2 ) );
////    }
//
//    /**
//     *
//     * @param particleA
//     * @param particleB
//     * @return
//     */
//    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
//        Collision result = null;
//        if( particleA instanceof Atom && particleB instanceof Atom ) {
//            result = new AtomAtomCollision( (Atom)particleA, (Atom)particleB );
//        }
//        return result;
//    }
//
//    //
//    // Static fields and methods
//    //
//    static public void register() {
//        CollisionFactory.addPrototype( new AtomAtomCollision() );
//    }
}

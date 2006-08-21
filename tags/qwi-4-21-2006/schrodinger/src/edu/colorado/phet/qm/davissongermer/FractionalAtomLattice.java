/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:27:54 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public abstract class FractionalAtomLattice {
    private double spacingBetweenAtoms;
    private double potential;
    private double atomRadius;
    private double y0;

    public FractionalAtomLattice( double atomRadius, double spacingBetweenAtoms, double y0, double potential ) {
        this.atomRadius = atomRadius;
        this.spacingBetweenAtoms = spacingBetweenAtoms;
        this.y0 = y0;
        this.potential = potential;
    }

    public double getSpacingBetweenAtoms() {
        return spacingBetweenAtoms;
    }

    public void setSpacing( double spacingBetweenAtoms ) {
        this.spacingBetweenAtoms = spacingBetweenAtoms;
    }

    public double getAtomRadius() {
        return atomRadius;
    }

    public void setAtomRadius( double atomRadius ) {
        this.atomRadius = atomRadius;
    }

    public ConcreteAtomLattice toConcreteAtomLattice( int latticeWidth, int latticeHeight ) {
//        System.out.println( "FractionalAtomLattice.toConcreteAtomLattice, latticeWidth="+latticeWidth );
        int concreteDiameter = getConcreteAtomDiameter( latticeWidth, latticeHeight );
        int concreteSpacing = getConcreteSpacing( latticeWidth, latticeHeight );
        ConcreteAtomLattice concreteAtomLattice = new ConcreteAtomLattice( latticeWidth, latticeHeight );
        int concreteY0 = getConcreteY0( latticeHeight );
        for( int xCenter = latticeWidth / 2; xCenter <= latticeWidth; xCenter += concreteSpacing ) {
            for( int yCenter = concreteY0; yCenter >= 0; yCenter -= concreteSpacing ) {
//            int yCenter=concreteY0;
                addAtom( xCenter, yCenter, concreteDiameter, concreteAtomLattice );
            }
        }
//        System.out.println( "FractionalAtomLattice.toConcreteAtomLattice" );
        for( int xCenter = latticeWidth / 2 - concreteSpacing; xCenter >= 0; xCenter -= concreteSpacing ) {
            for( int yCenter = concreteY0; yCenter >= 0; yCenter -= concreteSpacing ) {
//                System.out.println( "xCenter = " + xCenter );
//                System.out.println( "yCenter = " + yCenter );
                addAtom( xCenter, yCenter, concreteDiameter, concreteAtomLattice );
            }
        }
        concreteAtomLattice.setHeadAtom( latticeWidth / 2, concreteY0 );
        concreteAtomLattice.updateAll();
        return concreteAtomLattice;
    }

    private int getConcreteY0( int latticeHeight ) {
        return (int)( y0 * latticeHeight );
    }

    public Point getCenterAtomConcretePoint( int latticeWidth, int latticeHeight ) {
        return new Point( latticeWidth / 2, getConcreteY0( latticeHeight ) );
    }

    private void addAtom( int xCenter, int yCenter, int diameter, ConcreteAtomLattice concreteAtomLattice ) {
        Point center = new Point( xCenter, yCenter );
        AtomPotential atomPotential = createPotential( center, diameter, potential );
        concreteAtomLattice.addAtomPotentialNoUpdate( atomPotential );
    }

    protected abstract AtomPotential createPotential( Point center, int diameter, double potential );

    private int getConcreteSpacing( int latticeWidth, int latticeHeight ) {
        return round( spacingBetweenAtoms * latticeWidth );//could overlap
//        return Math.max( getConcreteAtomDiameter( latticeWidth, latticeHeight ), spacing );
    }

    private int getConcreteAtomDiameter( int latticeWidth, int latticeHeight ) {
//        return (int)( atomRadius * latticeWidth * 2.0 );
//        return round( atomRadius * latticeWidth * 2.0 );
        return round2( atomRadius * latticeWidth * 2.0 );
    }

    private int round2( double v ) {
        return (int)Math.round( v );
    }

    private int round( double v ) {
        return (int)v;
    }

    public double getY0() {
        return y0;
    }

    public void setY0( double y0 ) {
        this.y0 = y0;
    }

}

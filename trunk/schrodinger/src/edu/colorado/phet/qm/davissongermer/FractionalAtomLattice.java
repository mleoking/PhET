/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:27:54 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class FractionalAtomLattice {
    private double spacingBetweenAtoms;
    private double potential;
    private double atomRadius;

    public FractionalAtomLattice( double atomRadius, double spacingBetweenAtoms, double potential ) {
        this.atomRadius = atomRadius;
        this.spacingBetweenAtoms = spacingBetweenAtoms;
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
        int concreteAtomRadius = getConcreteAtomRadius( latticeWidth, latticeHeight );
        int concreteSpacing = getConcreteSpacing( latticeWidth, latticeHeight );
        ConcreteAtomLattice concreteAtomLattice = new ConcreteAtomLattice( latticeWidth, latticeHeight );

        for( int xCenter = latticeWidth / 2; xCenter <= latticeWidth; xCenter += concreteSpacing ) {
            for( int yCenter = 0; yCenter < latticeHeight / 2; yCenter += concreteSpacing ) {
                addAtom( xCenter, yCenter, concreteAtomRadius, concreteAtomLattice );
            }
        }
        for( int xCenter = latticeWidth / 2 - concreteSpacing; xCenter >= 0; xCenter -= concreteSpacing ) {
            for( int yCenter = 0; yCenter < latticeHeight / 2; yCenter += concreteSpacing ) {
                addAtom( xCenter, yCenter, concreteAtomRadius, concreteAtomLattice );
            }
        }
        return concreteAtomLattice;
    }

    private void addAtom( int xCenter, int yCenter, int concreteAtomRadius, ConcreteAtomLattice concreteAtomLattice ) {
        Point center = new Point( xCenter, yCenter );
        CircularPotential circularPotential = new CircularPotential( center, concreteAtomRadius, potential );
        concreteAtomLattice.addCircularPotential( circularPotential );
    }

    private int getConcreteSpacing( int latticeWidth, int latticeHeight ) {
        int spacing = (int)( spacingBetweenAtoms * latticeWidth );
        return Math.max( 2 * getConcreteAtomRadius( latticeWidth, latticeHeight ), spacing );
    }

    private int getConcreteAtomRadius( int latticeWidth, int latticeHeight ) {
        return (int)( atomRadius * latticeWidth );
    }
}

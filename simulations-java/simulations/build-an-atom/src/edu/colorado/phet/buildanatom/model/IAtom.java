// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.model;


/**
 * Interface implemented by all atoms that allows users to obtain information
 * about the composition and other attributes of the atom.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public interface IAtom {
    int getNumProtons();
    int getNumNeutrons();
    int getNumElectrons();
    int getCharge();
    int getMassNumber();
    double getAtomicMass();
    String getSymbol();
    String getName();
    String getFormattedCharge();
    boolean isStable();

}

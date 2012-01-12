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

    /**
     * Get the natural abundance for this particular isotope.
     *
     * @return - value from 0 to 1, inclusive, representing the proportion of
     *         this isotope that occurs in nature versus other isotopes with the same
     *         atomic number.
     */
    double getNaturalAbundance();

    /**
     * @return - True if the half life of this atom is greater than the age of the
     *         universe, false if not.
     */
    boolean isStable();
}

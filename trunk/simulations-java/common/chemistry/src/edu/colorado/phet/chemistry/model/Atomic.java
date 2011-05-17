//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.chemistry.model;

import java.awt.*;

/**
 * Interface that represents something like an atom
 * TODO: consider rename to IAtom
 */
public interface Atomic {
    /**
     * @return Standard 1 or 2 letter symbol
     */
    public String getSymbol();

    /**
     * @return Radius, in picometers
     */
    public double getRadius();

    /**
     * @return dimensionless, see https://secure.wikimedia.org/wikipedia/en/wiki/Electronegativity
     */
    public double getElectronegativity();

    /**
     * @return in atomic mass units (u). from http://www.webelements.com/periodicity/atomic_weight/
     */
    public double getAtomicWeight();

    /**
     * @return color used in visual representations
     */
    public Color getColor();

    /*---------------------------------------------------------------------------*
    * comparison utilities
    *----------------------------------------------------------------------------*/

    /**
     * @param atom Another atom
     * @return Whether these atoms are the same type (have the same symbol)
     */
    public boolean isSameTypeOfAtom( Atomic atom );

    public boolean isHydrogen();

    public boolean isCarbon();

    public boolean isOxygen();
}

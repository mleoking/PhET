/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model portion of Isotopes Mixture module.  This model contains a mixture
 * of isotopes and allows a user to move various different isotopes in and
 * out of the "Isotope Test Chamber", and simply keeps track of the average
 * mass within the chamber.
 *
 * @author John Blanco
 */
public class IsotopeMixturesModel {

    // -----------------------------------------------------------------------
    // Class Data
    // -----------------------------------------------------------------------

    private static final Dimension2D ISOTOPE_TEST_CHAMBER_SIZE = new PDimension(500, 300); // In picometers.
    private static final Rectangle2D ISOTOPE_TEST_CHAMBER_RECT =
        new Rectangle2D.Double(0, 0, ISOTOPE_TEST_CHAMBER_SIZE.getWidth(), ISOTOPE_TEST_CHAMBER_SIZE.getHeight());

    // -----------------------------------------------------------------------
    // Instance Data
    // -----------------------------------------------------------------------

    // -----------------------------------------------------------------------
    // Constructor(s)
    // -----------------------------------------------------------------------

    // -----------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------

    public Rectangle2D getIsotopeTestChamberRect(){
        return ISOTOPE_TEST_CHAMBER_RECT;
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}

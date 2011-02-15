/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.buildanatom.model.IConfigurableAtomModel;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model portion of Isotopes Mixture module.  This model contains a mixture
 * of isotopes and allows a user to move various different isotopes in and
 * out of the "Isotope Test Chamber", and simply keeps track of the average
 * mass within the chamber.
 *
 * @author John Blanco
 */
public class IsotopeMixturesModel implements Resettable, IConfigurableAtomModel {

    // -----------------------------------------------------------------------
    // Class Data
    // -----------------------------------------------------------------------

    private static final Dimension2D ISOTOPE_TEST_CHAMBER_SIZE = new PDimension( 350, 300 ); // In picometers.

    private static final Rectangle2D ISOTOPE_TEST_CHAMBER_RECT =
            new Rectangle2D.Double(
                -ISOTOPE_TEST_CHAMBER_SIZE.getWidth() / 2,
                -ISOTOPE_TEST_CHAMBER_SIZE.getHeight() / 2,
                ISOTOPE_TEST_CHAMBER_SIZE.getWidth(),
                ISOTOPE_TEST_CHAMBER_SIZE.getHeight() );

    // -----------------------------------------------------------------------
    // Instance Data
    // -----------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    // This atom is the "prototype isotope", meaning that it is set in order
    // to set the atomic weight of the family of isotopes that are currently
    // in use.
    private final SimpleAtom prototypeIsotope = new SimpleAtom();

    // This property contains the list of isotopes that exist in nature as
    // variations of the current "prototype isotope".  In other words, this
    // contains a list of all stable isotopes that match the atomic weight
    // of the currently configured isotope.  There should be only one of each
    // possible isotope.
    private final Property< ArrayList<ImmutableAtom> > possibleIsotopesProperty =
        new Property< ArrayList<ImmutableAtom> >(new ArrayList<ImmutableAtom>());

    // -----------------------------------------------------------------------
    // Constructor(s)
    // -----------------------------------------------------------------------
    public IsotopeMixturesModel( BuildAnAtomClock clock ) {
        this.clock = clock;
    }

    // -----------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public IDynamicAtom getAtom(){
        return prototypeIsotope;
    }

    public void setAtomConfiguration( IAtom atom ) {
        prototypeIsotope.setNumProtons( atom.getNumProtons() );
        prototypeIsotope.setNumElectrons( atom.getNumElectrons() );
        prototypeIsotope.setNumNeutrons( atom.getNumNeutrons() );
        possibleIsotopesProperty.setValue( AtomIdentifier.getAllIsotopes( atom.getNumProtons() ) );
    }

    public Rectangle2D getIsotopeTestChamberRect(){
        return ISOTOPE_TEST_CHAMBER_RECT;
    }

    public void reset() {
        // TODO Auto-generated method stub
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}

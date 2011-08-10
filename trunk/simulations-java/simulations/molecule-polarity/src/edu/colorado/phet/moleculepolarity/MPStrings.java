// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Strings for "Molecule Polarity", statically loaded to detect missing strings at startup.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPStrings {

    private MPStrings() {
    }

    private static final PhetResources RESOURCES = new PhetResources( MPConstants.PROJECT_NAME );

    public static final String A = RESOURCES.getLocalizedString( "A" );
    public static final String ATOM_LABELS = RESOURCES.getLocalizedString( "atomLabels" );
    public static final String B = RESOURCES.getLocalizedString( "B" );
    public static final String C = RESOURCES.getLocalizedString( "C" );
    public static final String COVALENT = RESOURCES.getLocalizedString( "covalent" );
    public static final String VIEW = RESOURCES.getLocalizedString( "View" );
    public static final String BOND_DIPOLES = RESOURCES.getLocalizedString( "bondDipoles" );
    public static final String BOND_TYPE = RESOURCES.getLocalizedString( "bondType" );
    public static final String ELECTRIC_FIELD = RESOURCES.getLocalizedString( "electricField" );
    public static final String ELECTRON_DENSITY = RESOURCES.getLocalizedString( "electronDensity" );
    public static final String ELECTRONEGATIVITY = RESOURCES.getLocalizedString( "electronegativity" );
    public static final String ELECTROSTATIC_POTENTIAL = RESOURCES.getLocalizedString( "electrostaticPotential" );
    public static final String IONIC = RESOURCES.getLocalizedString( "ionic" );
    public static final String ISOSURFACE = RESOURCES.getLocalizedString( "isosurface" );
    public static final String LESS = RESOURCES.getLocalizedString( "less" );
    public static final String MOLECULE = RESOURCES.getLocalizedString( "molecule" );
    public static final String MOLECULAR_DIPOLE = RESOURCES.getLocalizedString( "molecularDipole" );
    public static final String MORE = RESOURCES.getLocalizedString( "more" );
    public static final String NONE = RESOURCES.getLocalizedString( "none" );
    public static final String OFF = RESOURCES.getLocalizedString( "off" );
    public static final String ON = RESOURCES.getLocalizedString( "on" );
    public static final String PARTIAL_CHARGES = RESOURCES.getLocalizedString( "partialCharges" );
    public static final String PATTERN_0ATOM_NAME = RESOURCES.getLocalizedString( "pattern.0atomName" );
    public static final String PATTERN_0LABEL = RESOURCES.getLocalizedString( "pattern.0label" );
    public static final String REAL_MOLECULES = RESOURCES.getLocalizedString( "realMolecules" );
    public static final String TEST = RESOURCES.getLocalizedString( "test" );
    public static final String THREE_ATOMS = RESOURCES.getLocalizedString( "threeAtoms" );
    public static final String TWO_ATOMS = RESOURCES.getLocalizedString( "twoAtoms" );

    // Symbols, i18n not required
    public static final char DELTA = '\u03B4';
}
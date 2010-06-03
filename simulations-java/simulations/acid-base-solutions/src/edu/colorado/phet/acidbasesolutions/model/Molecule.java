/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.advancedacidbasesolutions.AABSSymbols;

/**
 * Base class for molecules, inner classes for specific molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {

    private final String name;
    private final String symbol; // chemical symbol
    private final Image icon; // icon used to visually represent the molecule
    private final Color color; // color used in label, bar charts, etc.
    
    public Molecule( String name, String symbol, Image icon, Color color ) {
        this.name = name;
        this.symbol = symbol;
        this.icon = icon;
        this.color = color;
    }
    
    /**
     * Uses the same string for both the name and symbol.
     */
    public Molecule( String symbol, Image icon, Color color ) {
        this( symbol, symbol, icon, color );
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public Image getIcon() {
        return icon;
    }
    
    public Color getColor() {
        return color;
    }
    
    // HA, generic representation of an acid
    public static class GenericAcidMolecule extends Molecule {
        public GenericAcidMolecule() {
            super( ABSSymbols.HA, ABSImages.HA_MOLECULE, ABSColors.HA );
        }
    }
    
    // A-, generic representation of an acid's product
    public static class GenericAcidProductMolecule extends Molecule {
        public GenericAcidProductMolecule() {
            super( ABSSymbols.A_MINUS, ABSImages.A_MINUS_MOLECULE, ABSColors.A_MINUS );
        }
    }
    
    // B, generic representation of a weak base
    public static class GenericWeakBaseMolecule extends Molecule {
        public GenericWeakBaseMolecule() {
            super( ABSSymbols.B, ABSImages.B_MOLECULE, ABSColors.B );
        }
    }
    
    // BH+, generic representation of a weak base's product
    public static class GenericWeakBaseProductMolecule extends Molecule {
        public GenericWeakBaseProductMolecule() {
            super( ABSSymbols.BH_PLUS, ABSImages.BH_PLUS_MOLECULE, ABSColors.BH_PLUS );
        }
    }
    
    // MOH, generic representation of a strong base
    public static class GenericStrongBaseMolecule extends Molecule {
        public GenericStrongBaseMolecule() {
            super( ABSSymbols.MOH, ABSImages.MOH_MOLECULE, ABSColors.MOH );
        }
    }
    
    // M+, generic representation of a strong base's product
    public static class GenericStrongBaseProductMolecule extends Molecule {
        public GenericStrongBaseProductMolecule() {
            super( ABSSymbols.M_PLUS, ABSImages.M_PLUS_MOLECULE, ABSColors.M_PLUS );
        }
    }
    
    // H3O+ (hydronium)
    public static class H3OMolecule extends Molecule {
        public H3OMolecule() {
            super( ABSSymbols.H3O_PLUS, ABSImages.H3O_PLUS_MOLECULE, ABSColors.H3O_PLUS );
        }
    }
    
    // OH- (hydroxide)
    public static class OHMolecule extends Molecule {
        public OHMolecule() {
            super( ABSSymbols.OH_MINUS, ABSImages.OH_MINUS_MOLECULE, ABSColors.OH_MINUS );
        }
    }
    
    // H2O (water)
    public static class Water extends Molecule {
        public Water() {
            super( ABSStrings.WATER, AABSSymbols.H2O, ABSImages.H2O_MOLECULE, ABSColors.H2O );
        }
    }
}

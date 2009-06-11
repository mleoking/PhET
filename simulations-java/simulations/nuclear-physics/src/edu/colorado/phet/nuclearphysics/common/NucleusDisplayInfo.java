/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common;

import java.awt.Color;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class encapsulates the information that is used to display each of the
 * nuclei used in this simulation.  The information includes textual strings
 * like the chemical symbol and the textual name (e.g. carbon-14) as well as
 * other display information such as the color to use for the text when
 * placing a label on a nucleus.
 * 
 * @author John Blanco
 */
public class NucleusDisplayInfo {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	public static final NucleusDisplayInfo CARBON_14_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.CARBON_14_LEGEND_LABEL,
			NuclearPhysicsStrings.CARBON_14_CHEMICAL_SYMBOL,
			NuclearPhysicsStrings.CARBON_14_ISOTOPE_NUMBER,
			NuclearPhysicsConstants.CARBON_14_LABEL_COLOR,
			NuclearPhysicsConstants.CARBON_COLOR );

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final String longName;            // Mostly used in control panels.
	private final String chemicalSymbol;      // Used all over, but especially when labeling a nucleus.
	private final String isotopeNumberString; // Mostly used when labeling a nucleus.
	private final Color labelColor;           // Mostly used when labeling a nucleus.
	private final Color displayColor;         // Color used when portraying nucleus as a sphere.

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public NucleusDisplayInfo(
			String name, 
			String chemicalSymbol, 
			String isotopeNumberString, 
			Color labelColor,
			Color displayColor) {
		this.longName = name;
		this.chemicalSymbol = chemicalSymbol;
		this.isotopeNumberString = isotopeNumberString;
		this.labelColor = labelColor;
		this.displayColor = displayColor;
	}

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	public String getName() {
		return longName;
	}

	public String getChemicalSymbol() {
		return chemicalSymbol;
	}

	public String getIsotopeNumberString() {
		return isotopeNumberString;
	}

	public Color getLabelColor() {
		return labelColor;
	}

	public Color getDisplayColor() {
		return displayColor;
	}
	
	public static NucleusDisplayInfo getDisplayInfoForNucleusType(NucleusType nucleusType){
		
		NucleusDisplayInfo displayInfo = null;
		
		switch (nucleusType){
		case CARBON_14:
			displayInfo = CARBON_14_DISPLAY_INFO;
			break;
		}
		
		if (displayInfo == null){
			System.err.println("Warning: No display information available for selected nucleus, nucleus = " + nucleusType);
			assert false;
		}
		
		return displayInfo;
	}
}

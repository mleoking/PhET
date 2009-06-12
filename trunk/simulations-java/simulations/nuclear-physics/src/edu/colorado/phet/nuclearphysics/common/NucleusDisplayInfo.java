/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common;

import java.awt.Color;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;

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

	public static final NucleusDisplayInfo NITROGEN_14_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.NITROGEN_14_LEGEND_LABEL,
			NuclearPhysicsStrings.NITROGEN_14_CHEMICAL_SYMBOL,
			NuclearPhysicsStrings.NITROGEN_14_ISOTOPE_NUMBER,
			NuclearPhysicsConstants.NITROGEN_14_LABEL_COLOR,
			NuclearPhysicsConstants.NITROGEN_COLOR );

	public static final NucleusDisplayInfo URANIUM_238_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.URANIUM_238_LEGEND_LABEL,
			NuclearPhysicsStrings.URANIUM_238_CHEMICAL_SYMBOL,
			NuclearPhysicsStrings.URANIUM_238_ISOTOPE_NUMBER,
			NuclearPhysicsConstants.URANIUM_238_LABEL_COLOR,
			NuclearPhysicsConstants.URANIUM_238_COLOR );
	
	public static final NucleusDisplayInfo POLONIUM_211_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.POLONIUM_211_LEGEND_LABEL,
			NuclearPhysicsStrings.POLONIUM_211_CHEMICAL_SYMBOL,
			NuclearPhysicsStrings.POLONIUM_211_ISOTOPE_NUMBER,
			NuclearPhysicsConstants.POLONIUM_LABEL_COLOR,
			NuclearPhysicsConstants.POLONIUM_COLOR );

	public static final NucleusDisplayInfo LEAD_206_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.LEAD_206_LEGEND_LABEL,
			NuclearPhysicsStrings.LEAD_206_CHEMICAL_SYMBOL,
			NuclearPhysicsStrings.LEAD_206_ISOTOPE_NUMBER,
			NuclearPhysicsConstants.LEAD_LABEL_COLOR,
			NuclearPhysicsConstants.LEAD_COLOR );

	public static final NucleusDisplayInfo LEAD_207_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.LEAD_207_LEGEND_LABEL,
			NuclearPhysicsStrings.LEAD_207_CHEMICAL_SYMBOL,
			NuclearPhysicsStrings.LEAD_207_ISOTOPE_NUMBER,
			NuclearPhysicsConstants.LEAD_LABEL_COLOR,
			NuclearPhysicsConstants.LEAD_COLOR );

	public static final NucleusDisplayInfo CUSTOM_NUCLEUS_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.CUSTOM_NUCLEUS_LEGEND_LABEL,
			NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL,
			"",  // No isotope number for the custom nucleus.
			NuclearPhysicsConstants.CUSTOM_NUCLEUS_LABEL_COLOR,
			NuclearPhysicsConstants.CUSTOM_NUCLEUS_PRE_DECAY_COLOR );

	public static final NucleusDisplayInfo DECAYED_CUSTOM_NUCLEUS_DISPLAY_INFO = new NucleusDisplayInfo(
			NuclearPhysicsStrings.CUSTOM_NUCLEUS_LEGEND_LABEL,
			NuclearPhysicsStrings.CUSTOM_NUCLEUS_CHEMICAL_SYMBOL,
			"",  // No isotope number for the custom nucleus.
			NuclearPhysicsConstants.DECAYED_CUSTOM_NUCLEUS_LABEL_COLOR,
			NuclearPhysicsConstants.CUSTOM_NUCLEUS_POST_DECAY_COLOR );

	public static final NucleusDisplayInfo DEFAULT_DISPLAY_INFO = new NucleusDisplayInfo(
			"XX",
			"XX",
			"XX",
			Color.WHITE,
			Color.GRAY );
	
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
	
	/**
	 * Constructor.  Not intended to be instantiated outside of this class,
	 * but this could change if for some reason more dynamic display info is
	 * needed in the future.
	 */
	private NucleusDisplayInfo(
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

		case NITROGEN_14:
			displayInfo = NITROGEN_14_DISPLAY_INFO;
			break;
			
		case LEAD_206:
			displayInfo = LEAD_206_DISPLAY_INFO;
			break;
			
		case LEAD_207:
			displayInfo = LEAD_207_DISPLAY_INFO;
			break;
			
		case POLONIUM_211:
			displayInfo = POLONIUM_211_DISPLAY_INFO;
			break;
			
		case URANIUM_238:
			displayInfo = URANIUM_238_DISPLAY_INFO;
			break;
			
		case CUSTOM:
			displayInfo = CUSTOM_NUCLEUS_DISPLAY_INFO;
			break;
			
		case CUSTOM_POST_DECAY:
			displayInfo = DECAYED_CUSTOM_NUCLEUS_DISPLAY_INFO;
			break;
			
		default:
			System.err.println("Warning: No display information available for selected nucleus " + nucleusType + ", using default");
			assert false;  // Add the needed information if you hit this while debugging.
		    displayInfo = DEFAULT_DISPLAY_INFO;
		    break;
		}
		
		return displayInfo;
	}
}

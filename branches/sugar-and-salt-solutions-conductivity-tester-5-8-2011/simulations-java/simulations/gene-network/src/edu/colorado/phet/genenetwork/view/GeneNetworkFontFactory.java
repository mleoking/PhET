// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * This class is used to obtain fonts for use in the Gene Network simulations.
 * This came into existence because of problems using the PhetFont class,
 * since it provides non-serif fonts, and with those fonts it is very
 * difficult to tell the difference between labels like "lacI" and "lacl".
 * 
 * The basic idea is that this class will provide a font with a serif if no
 * preferred font is specified for the current locale, but will use the
 * preferred font if one is specified.
 * 
 * @author John Blanco
 */
public class GeneNetworkFontFactory {
	
	public static final PhetFont TEST_FONT = new PhetFont();

	/**
	 * Provide a font based on the specified size and style.  Note that this
	 * doesn't handle all styles, just the ones needed for this sim.  Add more
	 * if you need them.
	 * 
	 * @param size
	 * @param style
	 * @return
	 */
	public static Font getFont(int size, int style){
		Font returnFont = null;
		if (TEST_FONT.isPreferred()){
			if (style == Font.BOLD){
				returnFont = new PhetFont(size, true);
			}
			else{
				returnFont = new PhetFont(size);
			}
		}
		else {
			returnFont = new Font("Serif", style, size);
		}
		return returnFont;
	}
}

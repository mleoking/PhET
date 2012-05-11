// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSymbols;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

//REVIEW - There only appears to be one solvent used in the sim: Water.  Why
// not just have a class that represents water? My guess is that other solvents
// were envisioned or are planned for the future.  Should probable either not
// this, or just make this be water.

/**
 * A solvent (in this case a liquid) that dissolves another liquid (the solute) to create a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solvent implements IFluid {

    //REVIEW: Style comment - seems a bit odd to have Water here at the top of the class.
    /*
     * Pure water is a type of solvent.
     * Since Solvents are immutable, we could use one static instance of Water.
     * But that seems a bit precarious; if it's ever changed to mutable, it could make for some odd/difficult bugs.
     */
    public static class Water extends Solvent {

        public static final Color COLOR = new Color( 224, 255, 255 );

        public Water() {
            super( Strings.WATER, BLLSymbols.WATER, COLOR );
        }
    }

    public final String name;
    public final String formula;
    public final Property<Color> color; // mutable only so that we can experiment with colors via developer controls

    public Solvent( String name, String formula, Color color ) {
        this.name = name;
        this.formula = formula;
        this.color = new Property<Color>( color );
    }

    public Color getFluidColor() {
        return color.get();
    }

    //REVIEW: The color property is public, so why have this method?  This isn't done elsewhere, such as for the location property in Movable.
    public void addFluidColorObserver( SimpleObserver observer ) {
        color.addObserver( observer );
    }
}

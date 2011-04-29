// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;

/**
 * This function returns a different color value based on the property with
 * which it is constructed.  This is generally used to set to color for a
 * displayed value where the color varies based on the value.
 *
 * @author John Blanco
 */
public class ChargeColorFunction implements Function0<Color> {

    private final Property<Integer> chargeProperty;

    public ChargeColorFunction( Property<Integer> chargeProperty ){
        this.chargeProperty = chargeProperty;
    }

    public Color apply() {
        Color color;
        if ( chargeProperty.getValue() > 0 ){
            color = PhetColorScheme.RED_COLORBLIND;
        }
        else if ( chargeProperty.getValue() < 0 ){
            color = Color.BLUE;
        }
        else {
            color = Color.BLACK;
        }
        return color;
    }
}

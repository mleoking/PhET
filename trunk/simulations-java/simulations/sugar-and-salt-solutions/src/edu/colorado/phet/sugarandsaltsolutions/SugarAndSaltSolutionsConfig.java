// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Shared configuration for changing colors in all tabs with a control in the developer menu
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsConfig {
    public final Property<Color> backgroundColor = new Property<Color>( Color.black );
    public final Property<Color> saltColor = new Property<Color>( Color.white );
}

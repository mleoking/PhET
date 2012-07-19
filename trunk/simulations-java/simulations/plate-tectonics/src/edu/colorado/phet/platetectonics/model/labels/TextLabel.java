package edu.colorado.phet.platetectonics.model.labels;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

/**
 * A simple text label model, with position
 */
public class TextLabel {
    public final Property<ImmutableVector3F> centerPosition;
    public final String label;

    public TextLabel( Property<ImmutableVector3F> centerPosition, String label ) {
        this.centerPosition = centerPosition;
        this.label = label;
    }
}

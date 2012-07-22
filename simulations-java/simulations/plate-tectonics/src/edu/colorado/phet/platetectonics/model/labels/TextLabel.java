package edu.colorado.phet.platetectonics.model.labels;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A simple text label model, with position
 */
public class TextLabel {
    public final Property<Vector3F> centerPosition;
    public final String label;

    public TextLabel( Property<Vector3F> centerPosition, String label ) {
        this.centerPosition = centerPosition;
        this.label = label;
    }
}

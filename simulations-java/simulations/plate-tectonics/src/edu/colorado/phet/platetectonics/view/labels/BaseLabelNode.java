package edu.colorado.phet.platetectonics.view.labels;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.view.ColorMode;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.DARK_LABEL;
import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.LIGHT_LABEL;

/**
 * Base class for the label nodes. Mainly used for the color switching between display modes
 */
public class BaseLabelNode extends GLNode {
    private final Property<ColorMode> colorMode;
    private final boolean dark;

    public static final float PIXEL_SCALE = 3; // pixel up-scaling (for smoothness of text)
    public static final float TEXT_DISPLAY_SCALE = 0.45f; // factor for scaling the text

    // how large the text labels should be
    public static final float LABEL_SCALE = TEXT_DISPLAY_SCALE / PIXEL_SCALE;

    public BaseLabelNode( Property<ColorMode> colorMode, boolean dark ) {
        this.colorMode = colorMode;
        this.dark = dark;
    }

    private boolean hasDarkColor() {
        return dark == ( colorMode.get() == ColorMode.DENSITY || colorMode.get() == ColorMode.COMBINED );
    }

    public Color getColor() {
        return hasDarkColor() ? DARK_LABEL : LIGHT_LABEL;
    }
}

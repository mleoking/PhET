package edu.colorado.phet.platetectonics.view.labels;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.view.ColorMode;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.DARK_LABEL;
import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.LIGHT_LABEL;

public class BaseLabelNode extends GLNode {
    private final Property<ColorMode> colorMode;
    private final boolean dark;

    public BaseLabelNode( Property<ColorMode> colorMode, boolean dark ) {
        this.colorMode = colorMode;
        this.dark = dark;
    }

    private boolean hasDarkColor() {
        return dark == ( colorMode.get() == ColorMode.DENSITY );
    }

    public Color getColor() {
        return hasDarkColor() ? DARK_LABEL : LIGHT_LABEL;
    }
}

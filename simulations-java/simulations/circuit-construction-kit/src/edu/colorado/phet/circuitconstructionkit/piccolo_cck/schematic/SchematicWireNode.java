package edu.colorado.phet.circuitconstructionkit.piccolo_cck.schematic;

import java.awt.*;

import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Wire;
import edu.colorado.phet.circuitconstructionkit.piccolo_cck.lifelike.WireNode;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 11:39:46 PM
 */

public class SchematicWireNode extends WireNode {
    public SchematicWireNode( CCKModel cckModel, Wire wire, Component component ) {
        super( cckModel, wire, component );
        setWirePaint( Color.black );
        setHighlightStrokeWidth( DEFAULT_HIGHLIGHT_STROKE_WIDTH * 0.6 );
    }
}

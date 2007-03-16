package edu.colorado.phet.cck.piccolo_cck.schematic;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.cck.piccolo_cck.lifelike.WireNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 11:39:46 PM
 * Copyright (c) Oct 5, 2006 by Sam Reid
 */

public class SchematicWireNode extends WireNode {
    public SchematicWireNode( CCKModel cckModel, Wire wire, Component component ) {
        super( cckModel, wire, component );
        setWirePaint( Color.black );
        setHighlightStrokeWidth( DEFAULT_HIGHLIGHT_STROKE_WIDTH * 0.6 );
    }
}

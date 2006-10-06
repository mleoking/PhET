package edu.colorado.phet.cck.piccolo_cck.schematic;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Battery;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicBatteryNode extends SchematicPlatedNode {
    private static double SCALE = 1.2;

    public SchematicBatteryNode( CCKModel cckModel, Battery battery, Component component, ICCKModule module ) {
        super( cckModel, battery, component, module, 0.1, 0.43, 3 * SCALE, 1.75 * SCALE );
    }
}

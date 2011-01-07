// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 */
public class SchematicBatteryNode extends SchematicPlatedNode {
    private static double SCALE = 1.2;

    public SchematicBatteryNode(CCKModel cckModel, Battery battery, JComponent component, CCKModule module) {
        super(cckModel, battery, component, module, 0.1, 0.43, 3 * SCALE, 1.75 * SCALE);
    }
}

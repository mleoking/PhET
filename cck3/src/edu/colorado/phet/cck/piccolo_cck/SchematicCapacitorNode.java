package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Capacitor;
import edu.colorado.phet.cck.piccolo_cck.schematic.SchematicPlatedNode;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 12:42:42 AM
 * Copyright (c) Oct 6, 2006 by Sam Reid
 */

public class SchematicCapacitorNode extends SchematicPlatedNode {
    private static double SCALE = 1.2;

    public SchematicCapacitorNode( CCKModel cckModel, Capacitor capacitor, JComponent component, ICCKModule module ) {
        super( cckModel, capacitor, component, module, 0.1, 0.4, 3 * SCALE, 3 * SCALE );
    }
}

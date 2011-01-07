// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Capacitor;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicPlatedNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 12:42:42 AM
 */

public class SchematicCapacitorNode extends SchematicPlatedNode {
    private static double SCALE = 1.2;

    public SchematicCapacitorNode(CCKModel cckModel, Capacitor capacitor, JComponent component, CCKModule module) {
        super(cckModel, capacitor, component, module, 0.1, 0.4, 3 * SCALE, 3 * SCALE);
    }

    public Shape getClipShape(PNode parent) {
        Shape clip = getClipShape();
        PAffineTransform a = super.getLocalToGlobalTransform(null);
        PAffineTransform b = parent.getGlobalToLocalTransform(null);
        clip = a.createTransformedShape(clip);
        clip = b.createTransformedShape(clip);
        return clip;
    }
}

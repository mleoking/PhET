package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Capacitor;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic.SchematicPlatedNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 12:42:42 AM
 */

public class SchematicCapacitorNode extends SchematicPlatedNode {
    private static double SCALE = 1.2;

    public SchematicCapacitorNode( CCKModel cckModel, Capacitor capacitor, JComponent component, ICCKModule module ) {
        super( cckModel, capacitor, component, module, 0.1, 0.4, 3 * SCALE, 3 * SCALE );
    }

    public Shape getClipShape( PNode parent ) {
        Shape clip = getClipShape();
//        Shape rightClip = getPlateClipShape( rightPlate.getPath(), parent );
//        Area ax = new Area( leftClip );
//        ax.add( new Area( rightClip ) );
//        Shape leftWireShape = leftWire.getPathBoundExpanded();
        PAffineTransform a = super.getLocalToGlobalTransform( null );
        PAffineTransform b = parent.getGlobalToLocalTransform( null );
        clip = a.createTransformedShape( clip );
        clip = b.createTransformedShape( clip );
//        ax.subtract( new Area( leftWireShape ) );
        return clip;
    }
}

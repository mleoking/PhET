package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Capacitor;
import edu.colorado.phet.cck.piccolo_cck.schematic.SchematicPlatedNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;

import javax.swing.*;
import java.awt.*;

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

    public Shape getClipShape( PNode parent ) {
        Shape clip = getClipShape( );
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

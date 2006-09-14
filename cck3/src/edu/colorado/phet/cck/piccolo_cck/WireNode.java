package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.components.Wire;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:24:18 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class WireNode extends PhetPNode {
    private Wire wire;
    private PPath ppath;

    public WireNode( final Wire wire ) {
        this.wire = wire;

        ppath = new PPath();
        ppath.setPaint( Color.blue );
        ppath.setStroke( new BasicStroke( 0.01f ) );
        addChild( ppath );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                super.mouseDragged( event );
                PDimension delta = event.getDeltaRelativeTo( WireNode.this );
                wire.getStartJunction().translate( delta.width, delta.height );
                wire.getEndJunction().translate( delta.width, delta.height );
            }
        } );
        wire.getStartJunction().addObserver( new SimpleObserver() {
            public void update() {
                WireNode.this.update();
            }
        } );
        wire.getEndJunction().addObserver( new SimpleObserver() {
            public void update() {
                WireNode.this.update();
            }
        } );
        update();
    }

    private void update() {
        Line2D.Double line = new Line2D.Double( wire.getStartPoint(), wire.getEndPoint() );
        Shape wireShape = new BasicStroke( 0.2f ).createStrokedShape( line );
        ppath.setPathTo( wireShape );
    }
}

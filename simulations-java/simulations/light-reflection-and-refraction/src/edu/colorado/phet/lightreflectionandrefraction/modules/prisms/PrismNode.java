// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class PrismNode extends PNode {
    public PrismNode( final ModelViewTransform transform, final Prism prism ) {
        addChild( new PhetPPath( new Color( 60, 214, 214 ), new BasicStroke(), Color.darkGray ) {{
            prism.shape.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( prism.shape.getValue().toShape() ) );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                final Dimension2D delta = transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
                prism.translate( delta.getWidth(), delta.getHeight() );
            }
        } );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import java.awt.*;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.Medium;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class PrismNode extends PNode {
    private final ModelViewTransform transform;
    private final Prism prism;

    public PrismNode( final ModelViewTransform transform, final Prism prism, final Property<Medium> prismMedium ) {
        this.transform = transform;
        this.prism = prism;
        addChild( new PhetPPath( new Color( 60, 214, 214 ), new BasicStroke(), Color.darkGray ) {{
            prism.shape.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( prism.shape.getValue().toShape() ) );
                }
            } );
            prismMedium.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( prismMedium.getValue().getColor() );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                doDrag( event );
            }
        } );
    }

    public void doDrag( PInputEvent event ) {
        final Dimension2D delta = transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
        prism.translate( delta.getWidth(), delta.getHeight() );
    }
}

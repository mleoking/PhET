// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.CanvasBoundedDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.fractions.intro.matchinggame.model.Representation;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class RepresentationNode extends PNode {
    private Representation representation;

    public RepresentationNode( final ModelViewTransform transform, final Representation representation ) {
        this.representation = representation;
        representation.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D immutableVector2D ) {
                setOffset( transform.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new CanvasBoundedDragHandler( this ) {
            @Override protected void dragNode( DragEvent event ) {
                representation.position.set( representation.position.get().plus( event.delta ) );
            }
        } );
    }
}
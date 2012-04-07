// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.imperative;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class CircleNode extends PNode {
    public CircleNode( final Circle circle ) {
        addChild( new PhetPPath() {{
            circle.dragging.addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean dragging ) {
                    setPaint( dragging ? Color.red : Color.blue );
                }
            } );
            circle.position.addObserver( new VoidFunction1<Vector2D>() {
                @Override public void apply( final Vector2D vector2D ) {
                    setPathTo( new Ellipse2D.Double( circle.position.get().x - circle.radius, circle.position.get().y - circle.radius, circle.radius, circle.radius ) );
                }
            } );
        }} );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                circle.dragging.set( true );
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                circle.position.set( circle.position.get().plus( event.getDeltaRelativeTo( getParent().getParent() ) ) );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                circle.dragging.set( false );
            }
        } );
    }
}
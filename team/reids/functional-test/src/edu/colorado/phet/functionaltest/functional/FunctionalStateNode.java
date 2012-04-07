// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional;

import fj.F;
import fj.data.Option;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.functionaltest.functional.model.Circle;
import edu.colorado.phet.functionaltest.functional.model.FunctionalState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class FunctionalStateNode extends PNode {
    public FunctionalStateNode( Property<FunctionalState> model ) {
        for ( Circle circle : model.get().getCircles() ) {
            addChild( new CircleNode( model, circle ) );
        }
        Option<Circle> circle = model.get().circles.find( new F<Circle, Boolean>() {
            @Override public Boolean f( final Circle circle ) {
                return circle.dragging;
            }
        } );

        addChild( new PhetPText( circle.toString() ) {{
            setOffset( 200, 0 );
        }} );
    }

    private static class CircleNode extends PNode {
        public CircleNode( final Property<FunctionalState> model, final Circle circle ) {
            addChild( new PhetPPath( circle.toShape(), circle.dragging ? Color.red : Color.blue ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    final FunctionalState originalState = model.get();
                    FunctionalState newState = new FunctionalState( originalState.circles.map( new F<Circle, Circle>() {
                        @Override public Circle f( final Circle c ) {
                            return circle == c ? c.withDragging( true ) : c;
                        }
                    } ) ).sort();
                    model.set( newState );
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    final FunctionalState originalState = model.get();
                    FunctionalState newState = new FunctionalState( originalState.circles.map( new F<Circle, Circle>() {
                        @Override public Circle f( final Circle circle ) {
                            return circle.dragging ? circle.translate( event.getDeltaRelativeTo( getParent() ) ) : circle;
                        }
                    } ) );
                    model.set( newState );
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    final FunctionalState originalState = model.get();
                    FunctionalState newState = new FunctionalState( originalState.circles.map( new F<Circle, Circle>() {
                        @Override public Circle f( final Circle circle ) {
                            return circle.withDragging( false );
                        }
                    } ) );
                    model.set( newState );
                }
            } );
        }
    }
}
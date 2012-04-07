// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionaltest.functional2;

import fj.F;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.util.FJUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class StateNode extends PNode {
    public StateNode( final Property<State2> model, final State2 state ) {
        for ( final Circle2 circle : state.circles ) {
            addChild( new PhetPPath( new Ellipse2D.Double( circle.position.x - circle.radius, circle.position.y - circle.radius, circle.radius * 2, circle.radius * 2 ),
                                     circle.dragging ? Color.red : Color.blue ) {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( final PInputEvent event ) {
                        model.set( new State2( model.get().circles.map( new F<Circle2, Circle2>() {
                            @Override public Circle2 f( final Circle2 circle2 ) {
                                return circle2 == circle ? circle2.withDragging( true ) : circle2;
                            }
                        } ).
                                sort( FJUtils.ord( new F<Circle2, Double>() {
                                    @Override public Double f( final Circle2 circle2 ) {
                                        return circle2.dragging ? 1.0 : 0.0;
                                    }
                                } ) ) ) );
                    }

                    @Override public void mouseDragged( final PInputEvent event ) {
                        model.set( new State2( model.get().circles.map( new F<Circle2, Circle2>() {
                            @Override public Circle2 f( final Circle2 circle2 ) {
                                return circle2.dragging ? circle2.withPosition( circle2.position.plus( event.getDeltaRelativeTo( getParent() ) ) ) : circle2;
                            }
                        } ) ) );
                    }

                    @Override public void mouseReleased( final PInputEvent event ) {
                        model.set( new State2( model.get().circles.map( new F<Circle2, Circle2>() {
                            @Override public Circle2 f( final Circle2 circle2 ) {
                                return circle2.withDragging( false );
                            }
                        } ) ) );
                    }
                } );
            }} );
        }

        addChild( new PhetPText( state.circles.find( new F<Circle2, Boolean>() {
            @Override public Boolean f( final Circle2 circle2 ) {
                return circle2.dragging;
            }
        } ).toString() ) );
    }
}
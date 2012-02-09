// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.tests;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.intro.intro.view.FractionsIntroCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Renders the pie set node from the given model.  Unconventional way of using piccolo, where the scene graph is recreated any time the model changes.
 * Done to support immutable model and still get some piccolo benefits.
 *
 * @author Sam Reid
 */
public class PieSetNode extends PNode {
    public PieSetNode( final Property<PieSetState> model ) {
        model.addObserver( new VoidFunction1<PieSetState>() {
            public void apply( PieSetState state ) {
                removeAllChildren();

                for ( Slice cell : state.cells ) {
                    addChild( new PhetPPath( cell.toShape(), new BasicStroke( 1 ), Color.darkGray ) );
//                    addChild( new PhetPPath( new Rectangle2D.Double( cell.getCenter().getX(), cell.getCenter().getY(), 2, 2 ) ) );
                }
                for ( final Slice slice : state.slices ) {
                    addChild( new PhetPPath( slice.toShape(), FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 1 ), Color.darkGray ) {{
                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {

                            //Flag one slice as dragging
                            @Override public void mousePressed( PInputEvent event ) {
                                PieSetState state = model.get();
                                model.set( new PieSetState( state.numerator, state.denominator, state.cells, state.slices.remove( slice ).append( slice.dragging( true ) ) ) );
                            }

                            //Set all drag flags to false
                            @Override public void mouseReleased( PInputEvent event ) {
                                PieSetState state = model.get();
                                model.set( new PieSetState( state.numerator, state.denominator, state.cells, state.slices.map( new Function1<Slice, Slice>() {
                                    public Slice apply( Slice slice ) {
                                        return slice.dragging( false );
                                    }
                                } ) ) );
                            }

                            //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
                            @Override public void mouseDragged( PInputEvent event ) {
                                PieSetState state = model.get();
                                final PDimension delta = event.getCanvasDelta();
                                PieSetState newState = new PieSetState( state.numerator, state.denominator, state.cells, state.slices.map( new Function1<Slice, Slice>() {
                                    public Slice apply( Slice slice ) {
                                        return slice.dragging ? slice.translate( delta.getWidth(), delta.getHeight() ) : slice;
                                    }
                                } ) );
                                model.set( newState );
                            }
                        } );
                    }} );
//                    addChild( new PhetPPath( new Rectangle2D.Double( slice.getCenter().getX(), slice.getCenter().getY(), 2, 2 ) ) );
                }
            }
        } );
    }
}
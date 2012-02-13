// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.AnimationTarget;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.Slice;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Node used in PieSetNode for showing user-draggable pieces.  This matches with the immutable model and so has some quirky implementation because there is
 * an impedance mismatch with Piccolo.
 *
 * @author Sam Reid
 */
public class MovableSliceNode extends PNode {
    public MovableSliceNode( final PNode rootNode, final SettableProperty<PieSet> model, final Slice slice ) {

        final Shape shape = slice.shape();
        if ( Double.isNaN( shape.getBounds2D().getX() ) || Double.isNaN( shape.getBounds2D().getY() ) ) {
            //TODO: Find and prevent the NaNs in the first place
            return;
        }
        addChild( new PhetPPath( shape, FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 1 ), Color.darkGray ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                //Flag one slice as dragging
                @Override public void mousePressed( PInputEvent event ) {
                    PieSet state = model.get();
                    final PieSet newState = new PieSet( state.denominator, state.pies, state.slices.delete( slice, Equal.<Slice>anyEqual() ).snoc( slice.dragging( true ) ) );
                    model.set( newState );
                }

                //Set all drag flags to false
                @Override public void mouseReleased( PInputEvent event ) {
                    final PieSet state = model.get();

                    //Any dropped pieces should snap to their destination.
                    final List<Slice> newSlices = state.slices.map( new F<Slice, Slice>() {
                        public Slice f( Slice s ) {
                            Slice target = state.getDropTarget( s );
                            if ( s.dragging && target != null ) { return s.moveTo( target ); }
                            else if ( s.dragging ) {
                                final Slice destination = PieSet.createBucketSlice( model.get().denominator );
                                return s.dragging( false ).animationTarget( new AnimationTarget( destination.tip, destination.angle ) );
                            }
                            else { return s; }
                        }
                    } );
                    final PieSet newState = state.slices( newSlices );
                    model.set( newState );
                }

                //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
                @Override public void mouseDragged( PInputEvent event ) {
                    PieSet state = model.get();
                    final Dimension2D delta = event.getDeltaRelativeTo( rootNode );
                    PieSet newState = new PieSet( state.denominator, state.pies, state.slices.map( new F<Slice, Slice>() {
                        public Slice f( Slice slice ) {
                            return slice.dragging ? slice.translate( delta.getWidth(), delta.getHeight() ) : slice;
                        }
                    } ) );
                    model.set( newState );
                }
            } );
        }} );
    }
}
// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.Equal;
import fj.F;
import fj.data.List;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Node used in PieSetNode for showing user-draggable pieces.  This matches with the immutable model and so has
 * some quirky implementation (see comments below) because there is an impedance mismatch with Piccolo.
 *
 * @author Sam Reid
 */
public class MovableSliceNode extends PNode {

    public MovableSliceNode( PNode child, final PNode rootNode, final SettableProperty<PieSet> model, final Slice slice ) {

        addChild( child );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {

            //Flag one slice as dragging
            @Override public void mousePressed( PInputEvent event ) {
                PieSet state = model.get();

                //Do not allow the user to grab a piece that is animating to a target, it causes the representations to get out of sync
                if ( slice.animationTarget == null ) {

                    //If dragging from the bucket, do not delete the old piece (since bucket should always look like it has an infinite supply)
                    if ( state.isInBucket( slice ) ) {
                        model.set( state.slices( state.slices.snoc( slice.dragging( true ) ) ) );
                    }
                    else {
                        model.set( state.slices( state.slices.delete( slice, Equal.<Slice>anyEqual() ).snoc( slice.dragging( true ) ) ) );
                    }
                }
            }

            //Set all drag flags to false
            @Override public void mouseReleased( PInputEvent event ) {
                final PieSet state = model.get();

                //Any dropped pieces should snap to their destination.
                final List<Slice> newSlices = state.slices.map( new F<Slice, Slice>() {
                    public Slice f( Slice s ) {
                        Slice target = state.getDropTarget( s );
                        return s.dragging && target != null ? s.moveTo( target ) :
                               s.dragging ? s.dragging( false ).animationTarget( model.get().sliceFactory.createBucketSlice( model.get().denominator ) ) :
                               s;
                    }
                } );
                final PieSet newState = state.slices( newSlices );
                model.set( newState );
            }

            //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
            @Override public void mouseDragged( PInputEvent event ) {
                PieSet state = model.get();
                final Dimension2D delta = event.getDeltaRelativeTo( rootNode );
                PieSet newState = state.slices( state.slices.map( new F<Slice, Slice>() {
                    public Slice f( Slice s ) {
                        return s.dragging ? s.translate( delta.getWidth(), delta.getHeight() ) : s;
                    }
                } ) );
                model.set( newState );
            }
        } );
    }
}
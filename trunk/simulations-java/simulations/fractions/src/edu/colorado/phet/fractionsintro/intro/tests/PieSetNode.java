// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.tests;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;

import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.MovableSlice;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.PieSetState;
import edu.colorado.phet.fractionsintro.intro.model.slicemodel.Slice;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static fj.Equal.equal;
import static fj.Function.curry;
import static fj.data.List.single;

/**
 * Renders the pie set node from the given model.  Unconventional way of using piccolo, where the scene graph is recreated any time the model changes.
 * Done to support immutable model and still get some piccolo benefits.
 *
 * @author Sam Reid
 */
public class PieSetNode extends PNode {

    private final BucketView bucketView;

    public static <T> Equal<T> refEqual() {
        return equal( curry( new F2<T, T, Boolean>() {
            public Boolean f( final T a1, final T a2 ) {
                return a1 == a2;
            }
        } ) );
    }

    public PieSetNode( final Property<PieSetState> model ) {

        final ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point(), new Point(), 1 );
        Dimension2DDouble STAGE_SIZE = new Dimension2DDouble( 1024, 768 );
        bucketView = new BucketView( new Bucket( STAGE_SIZE.width / 2, -STAGE_SIZE.height + 100, new Dimension2DDouble( 250, 100 ), Color.green, "pieces" ), mvt );

        model.addObserver( new SimpleObserver() {
            public void update() {
                rebuildScene( model );
            }
        } );
    }

    private void rebuildScene( final Property<PieSetState> model ) {
        removeAllChildren();

        addChild( bucketView.getHoleNode() );

        PieSetState state = model.get();
        for ( Slice cell : state.cells ) {
            addChild( new PhetPPath( cell.shape, new BasicStroke( state.cellFilled( cell ) ? 2 : 1 ), Color.darkGray ) );
//                    addChild( new PhetPPath( new Rectangle2D.Double( cell.getCenter().getX(), cell.getCenter().getY(), 2, 2 ) ) );
        }
        for ( final MovableSlice slice : state.slices ) {
            addChild( new PhetPPath( slice.shape, FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 1 ), Color.darkGray ) {{
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {

                    //Flag one slice as dragging
                    @Override public void mousePressed( PInputEvent event ) {
                        PieSetState state = model.get();
                        final List<MovableSlice> newSlice = single( slice.dragging( true ).container( null ) );
                        final PieSetState newState = new PieSetState( state.numerator, state.denominator, state.pies, state.slices.delete( slice, PieSetNode.<MovableSlice>refEqual() ).append( newSlice ) );
                        model.set( newState );
                    }

                    //Set all drag flags to false
                    @Override public void mouseReleased( PInputEvent event ) {
                        final PieSetState state = model.get();

                        //Any dropped pieces should snap to their destination.
                        model.set( state.slices( state.slices.map( new F<MovableSlice, MovableSlice>() {
                            public MovableSlice f( MovableSlice s ) {
                                Slice target = state.getDropTarget( s );
                                if ( s.dragging && target != null ) { return s.moveTo( state.getDropTarget( s ) ); }
                                else if ( s.dragging ) { return s.dragging( false ); }
                                else { return s; }
                            }
                        } ) ) );
                    }

                    //Drag the dragged slice as identified by the model (since nodes will be destroyed as this happens)
                    @Override public void mouseDragged( PInputEvent event ) {
                        PieSetState state = model.get();
                        final PDimension delta = event.getCanvasDelta();
                        PieSetState newState = new PieSetState( state.numerator, state.denominator, state.pies, state.slices.map( new F<MovableSlice, MovableSlice>() {
                            public MovableSlice f( MovableSlice slice ) {
                                return slice.dragging ? slice.translate( delta.getWidth(), delta.getHeight() ) : slice;
                            }
                        } ) );
                        model.set( newState );
                    }
                } );
            }} );
//                    addChild( new PhetPPath( new Rectangle2D.Double( slice.getCenter().getX(), slice.getCenter().getY(), 2, 2 ) ) );

            addChild( bucketView.getFrontNode() );
        }
    }
}
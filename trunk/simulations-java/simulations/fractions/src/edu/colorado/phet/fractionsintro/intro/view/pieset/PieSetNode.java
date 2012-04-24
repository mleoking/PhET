// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Pie;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;

/**
 * Renders the pie set node from the given model.  Unconventional way of using piccolo, where the scene graph is recreated any time the model changes.
 * Done to support immutable model and still get efficient reuse of piccolo.
 *
 * @author Sam Reid
 */
public class PieSetNode extends FNode {

    public final BucketView bucketView;

    //Flag for showing the center of a shape.  This can help debug pie piece rotation.
    private static final boolean debugCenter = false;
    public static F<SliceNodeArgs, PNode> NodeToShape = new F<SliceNodeArgs, PNode>() {
        @Override public PNode f( final SliceNodeArgs s ) {
            return new ShapeNode( s.slice );
        }
    };

    public PieSetNode( final SettableProperty<PieSet> model, PNode rootNode, boolean iconTextOnTheRight ) {
        this( model, rootNode, NodeToShape, CreateEmptyCellsNode, new F<PieSet, PNode>() {
            @Override public PNode f( final PieSet pieSet ) {
                return createBucketIcon( pieSet, NodeToShape );
            }
        }, iconTextOnTheRight );
    }

    //Create a PieSetNode, have to pass in the root node since the scene graph tree is reconstructed each time and you cannot use getDeltaRelativeTo(getParent) since the node may no longer be in the tree
    public PieSetNode( final SettableProperty<PieSet> model, final PNode rootNode, final F<SliceNodeArgs, PNode> createSliceNode, final F<PieSet, PNode> createEmptyCellsNode, final F<PieSet, PNode> createBucketIcon, final boolean iconTextOnTheRight ) {
        bucketView = new BucketView( model.get().sliceFactory.bucket, createSinglePointScaleInvertedYMapping( new Point(), new Point(), 1 ) );

        final SimpleObserver observer = new SimpleObserver() {
            public void update() {

                //Only update when visible, thus saving time since all models update at the same time
                removeAllChildren();
                if ( getVisible() ) {
                    addChild( new PieSetContentNode( bucketView, model, createSliceNode, rootNode, createEmptyCellsNode, createBucketIcon, iconTextOnTheRight ) );
                }
            }
        };
        model.addObserver( observer );
        addPropertyChangeListener( PROPERTY_VISIBLE, new PropertyChangeListener() {
            @Override public void propertyChange( final PropertyChangeEvent evt ) {
                observer.update();
            }
        } );
    }

    //Construct the exterior to show the pie cells border with a bigger stroke than the individual cells
    //Cache to save on performance, otherwise drops frame rate to < 5 fps
    //Still reduced performance a bit due to runtime rendering, could be rewritten if necessary to add explicit getBorderShape function for each pie
    public static final Cache<List<Slice>, Area> cache = new Cache<List<Slice>, Area>( new F<List<Slice>, Area>() {
        @Override public Area f( final List<Slice> cells ) {
            return new Area() {{
                for ( Slice cell : cells ) {
                    //Enlarge a bit to cover up "seams" in the horizontal bars
                    add( new Area( cell.getShape() ) );
                    add( new Area( new BasicStroke( 2 ).createStrokedShape( cell.getShape() ) ) );
                }
            }};
        }
    } );

    //Creates a shape for showing the empty cells
    public static final F<PieSet, PNode> CreateEmptyCellsNode = new F<PieSet, PNode>() {
        @Override public PNode f( final PieSet state ) {
            final FNode node = new FNode();
            for ( Slice cell : state.cells ) {
                boolean anythingInPie = state.pieContainsSliceForCell( cell );

                node.addChild( new PhetPPath( cell.getShape(), new BasicStroke( 1 ), anythingInPie ? Color.black : Color.lightGray ) );

                if ( debugCenter ) {
                    node.addChild( new PhetPPath( new Rectangle2D.Double( cell.getCenter().getX(), cell.getCenter().getY(), 2, 2 ) ) );
                }
            }

            //Show the outline in a thicker (2.0f) stroke
            state.pies.map( new F<Pie, PNode>() {
                @Override public PNode f( final Pie pie ) {
                    boolean filled = pie.cells.exists( new F<Slice, Boolean>() {
                        @Override public Boolean f( final Slice cell ) {
                            return state.cellFilledNowOrSoon( cell );
                        }
                    } );
                    return new PhetPPath( cache.f( pie.cells ), new BasicStroke( 2.0f ), filled ? Color.black : Color.lightGray );
                }
            } ).foreach( node.addChild );

            return node;
        }
    };

    //Create the icon for the bucket
    public static PNode createBucketIcon( final PieSet state, final F<SliceNodeArgs, PNode> createSliceNode ) {
        return new PNode() {{
            final int denominator = state.denominator;
            for ( int i = 0; i < denominator; i++ ) {
                Slice cell = state.sliceFactory.createPieCell( state.pies.length(), 0, i, denominator );
                addChild( new PhetPPath( cell.getShape(), Color.white, new BasicStroke( 3 ), Color.black ) );
            }

            Slice slice = state.sliceFactory.createPieCell( state.pies.length(), 0, 0, denominator );

            //Create the slice.  Wrap the state in a dummy Property to facilitate reuse of MovableSliceNode code.
            //Could be improved by generalizing MovableSliceNode to not require
            final PNode node = createSliceNode.f( new SliceNodeArgs( slice, state.denominator, false ) );
            node.setPickable( false );
            node.setChildPaintInvalid( false );
            addChild( node );

            //Make as large as possible, but small enough that tall representations (like vertical bars) fit
            scale( 0.28 );
        }};
    }
}
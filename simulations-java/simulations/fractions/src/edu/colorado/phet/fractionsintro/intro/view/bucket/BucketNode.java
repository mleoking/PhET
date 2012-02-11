// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.bucket;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.fractionsintro.intro.model.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.view.RepresentationArea;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PNode layer that shows bucket foreground, layer with draggable bucket contents and bucket hole.
 *
 * @author Sam Reid
 */
public class BucketNode extends PNode {
    public static final Random random = new Random();
    private PNode bucketContentsLayer = new PNode();
    private final FractionsIntroModel model;
    private final RepresentationArea representationArea;
    private final BucketView bucketView;

    public BucketNode( PDimension STAGE_SIZE, final FractionsIntroModel model, final RepresentationArea representationArea ) {
        this.model = model;
        this.representationArea = representationArea;
        final ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point(), new Point(), 1 );
        bucketView = new BucketView( new Bucket( STAGE_SIZE.width / 2, -STAGE_SIZE.height + 100, new Dimension2DDouble( 250, 100 ), Color.green, "pieces" ), mvt );
        addChild( bucketView.getHoleNode() );
        addChild( bucketContentsLayer );
        addChild( bucketView.getFrontNode() );

        //Have to call update when the container state changes, but we need to make sure this listener is called after the container state itself changes
        //So we can do that by observing the container state for changes and updating when its denominator changes
        model.containerState.addObserver( new ChangeObserver<ContainerSet>() {
            public void update( ContainerSet newValue, ContainerSet oldValue ) {
                if ( newValue.denominator != oldValue.denominator ) {
                    refillBucket();
                }
            }
        } );
        refillBucket();
    }

    //Fill the bucket with the right number of slices which can be dragged to the container cells
    private void refillBucket() {
        bucketContentsLayer.removeAllChildren();
        int numSlicesForBucket = model.containerState.get().getAllCellPointers().length() - model.containerState.get().getFilledCells().length();
        for ( int i = 0; i < numSlicesForBucket; i++ ) {
            PieSliceNode pieSliceNode = new PieSliceNode( model.denominator.get() ) {{
                final PieSliceNode node = this;
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mouseDragged( PInputEvent event ) {
                        final PDimension delta = event.getDeltaRelativeTo( node.getParent() );
                        node.translate( delta.getWidth(), delta.getHeight() );
                    }

                    @Override public void mouseReleased( PInputEvent event ) {
                        //find out where the pie should go

                        final Shape globalShape = node.getLocalToGlobalTransform( null ).createTransformedShape( node.shape );
                        CellPointer cp = representationArea.getClosestOpenCell( globalShape, getGlobalFullBounds().getCenter2D() );
                        if ( cp != null ) {
                            FractionsIntroModel.setUserToggled( true );
                            model.containerState.set( model.containerState.get().toggle( cp ) );
                            FractionsIntroModel.setUserToggled( false );
                            bucketContentsLayer.removeChild( node );
                        }
                    }
                } );
            }};
            double availableWidth = bucketView.getHoleNode().getFullBounds().getWidth() - pieSliceNode.getFullBounds().getWidth() - 10;//Last term accounts for bucket side slope
            pieSliceNode.centerFullBoundsOnPoint( bucketView.getHoleNode().getFullBounds().getCenterX() + ( random.nextDouble() - 0.5 ) * availableWidth, bucketView.getHoleNode().getFullBounds().getCenterY() );
            bucketContentsLayer.addChild( pieSliceNode );
        }
    }
}
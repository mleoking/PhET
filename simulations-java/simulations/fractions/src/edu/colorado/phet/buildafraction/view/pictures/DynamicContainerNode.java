package edu.colorado.phet.buildafraction.view.pictures;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.view.SpinnerButtonNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * Container that can be subdivided into different divisions
 *
 * @author Sam Reid
 */
public class DynamicContainerNode extends PNode {
    public DynamicContainerNode( final ContainerContext context ) {
        final IntegerProperty selectedPieceSize = new IntegerProperty( 1 );
        final VoidFunction1<Boolean> increment = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                selectedPieceSize.increment();
            }
        };
        final VoidFunction1<Boolean> decrement = new VoidFunction1<Boolean>() {
            public void apply( final Boolean autoSpinning ) {
                selectedPieceSize.decrement();
            }
        };
        addChild( new VBox(
                new SpinnerButtonNode( spinnerImage( ROUND_BUTTON_UP ), spinnerImage( ROUND_BUTTON_UP_PRESSED ), spinnerImage( ROUND_BUTTON_UP_GRAY ), increment, selectedPieceSize.lessThan( 6 ) ),
                new PNode() {{
                    selectedPieceSize.addObserver( new VoidFunction1<Integer>() {
                        public void apply( final Integer numPieces ) {
                            removeAllChildren();
                            addChild( new SimpleContainerNode( numPieces ) );
                        }
                    } );
                }},
                new SpinnerButtonNode( spinnerImage( ROUND_BUTTON_DOWN ), spinnerImage( ROUND_BUTTON_DOWN_PRESSED ), spinnerImage( ROUND_BUTTON_DOWN_GRAY ), decrement, selectedPieceSize.greaterThan( 1 ) ) ) );
    }

    public static BufferedImage spinnerImage( final BufferedImage image ) {
        return BufferedImageUtils.multiScaleToWidth( image, 50 );
    }
}
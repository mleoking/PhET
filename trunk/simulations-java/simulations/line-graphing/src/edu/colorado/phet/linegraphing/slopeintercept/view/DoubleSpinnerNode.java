// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.view.SpinnerButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Spinner for a double value, with up and down arrows.
 * Unlike a JSpinner, the value is not directly editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class DoubleSpinnerNode extends PNode {

    // Spinner that is color coded for intercept
    public static class InterceptSpinnerNode extends DoubleSpinnerNode {
        public InterceptSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent,
                   Images.SPINNER_UP_UNPRESSED_YELLOW, Images.SPINNER_UP_PRESSED_YELLOW, Images.SPINNER_UP_GRAY,
                   Images.SPINNER_DOWN_UNPRESSED_YELLOW, Images.SPINNER_DOWN_PRESSED_YELLOW, Images.SPINNER_DOWN_GRAY,
                   value, range, font, format, true /* abs */ );
        }
    }

    // Spinner that is color coded for slope
    public static class SlopeSpinnerNode extends DoubleSpinnerNode {
        public SlopeSpinnerNode( IUserComponent userComponent, Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
            super( userComponent,
                   Images.SPINNER_UP_UNPRESSED_GREEN, Images.SPINNER_UP_PRESSED_GREEN, Images.SPINNER_UP_GRAY,
                   Images.SPINNER_DOWN_UNPRESSED_GREEN, Images.SPINNER_DOWN_PRESSED_GREEN, Images.SPINNER_DOWN_GRAY,
                   value, range, font, format );
        }
    }

    public DoubleSpinnerNode( IUserComponent userComponent,
                              Image upUnpressedImage, Image upPressedImage, Image upDisabledImage,
                              Image downUnpressedImage, Image downPressedImage, Image downDisabledImage,
                              Property<Double> value, Property<DoubleRange> range, PhetFont font, NumberFormat format ) {
        this( userComponent, upUnpressedImage, upPressedImage, upDisabledImage, downUnpressedImage, downPressedImage, downDisabledImage,
              value, range, font, format, false /* abs */ );
    }

    public DoubleSpinnerNode( IUserComponent userComponent,
                              final Image upUnpressedImage, final Image upPressedImage, final Image upDisabledImage,
                              final Image downUnpressedImage, final Image downPressedImage, final Image downDisabledImage,
                              final Property<Double> value, final Property<DoubleRange> range, PhetFont font, final NumberFormat format, final boolean abs ) {

        SpinnerButtonNode incrementButton = new SpinnerButtonNode<Double>( UserComponentChain.chain( userComponent, "up" ),
                                                                   upUnpressedImage, upPressedImage, upDisabledImage,
                                                                   value,
                                                                   // increment the value
                                                                   new Function0<Double>() {
                                                                       public Double apply() {
                                                                           return value.get() + 1;
                                                                       }
                                                                   },
                                                                   // enabled the button if value < range.max
                                                                   new CompositeProperty<Boolean>( new Function0<Boolean>() {
                                                                       public Boolean apply() {
                                                                           return value.get() < range.get().getMax();
                                                                       }
                                                                   }, value, range ) );

        SpinnerButtonNode decrementButton = new SpinnerButtonNode<Double>( UserComponentChain.chain( userComponent, "down" ),
                                                                   downUnpressedImage, downPressedImage, downDisabledImage,
                                                                   value,
                                                                   // decrement the value
                                                                   new Function0<Double>() {
                                                                       public Double apply() {
                                                                           return value.get() - 1;
                                                                       }
                                                                   },
                                                                   // enabled the button if value > range.min
                                                                   new CompositeProperty<Boolean>( new Function0<Boolean>() {
                                                                       public Boolean apply() {
                                                                           return value.get() > range.get().getMin();
                                                                       }
                                                                   }, value, range ) );
        final PText textNode = new PhetPText( font );
        textNode.setPickable( false );

        // compute max text width
        textNode.setText( format.format( abs ? Math.abs( range.get().getMin() ) : range.get().getMin() ) );
        double minValueWidth = textNode.getFullBoundsReference().getWidth();
        textNode.setText( format.format( abs ? Math.abs( range.get().getMax() ) : range.get().getMax() ) );
        double maxValueWidth = textNode.getFullBoundsReference().getWidth();
        final double maxWidth = Math.max( minValueWidth, maxValueWidth );
        PPath horizontalStrutNode = new PPath( new Rectangle2D.Double( 0, 0, maxWidth, 1 ) ) {{
            setStroke( null );
            setPickable( false );
        }};

        // rendering order
        addChild( horizontalStrutNode );
        addChild( incrementButton );
        addChild( decrementButton );
        addChild( textNode );

        // layout
        horizontalStrutNode.setOffset( 0, 0 );
        textNode.setOffset( maxWidth - textNode.getFullBoundsReference().getWidth(), 0 );
        incrementButton.setOffset( textNode.getFullBoundsReference().getMaxX() + 3,
                                   textNode.getFullBoundsReference().getCenterY() - incrementButton.getFullBoundsReference().getHeight() - 1 );
        decrementButton.setOffset( incrementButton.getXOffset(),
                                   textNode.getFullBoundsReference().getCenterY() + 1 );

        // when the value changes, update the display
        final double minusWidth = new PhetPText( "-", font ).getFullBoundsReference().getWidth();
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                // displayed value
                final double adjustedValue = abs ? Math.abs( value ) : value;
                textNode.setText( format.format( adjustedValue ) );
                 // centered, adjusting for minus sign in negative values
                final double minusXOffset = adjustedValue < 0 ? ( minusWidth / 2 ) : 0;
                textNode.setOffset( ( ( maxWidth - textNode.getFullBoundsReference().getWidth() ) / 2 ) - minusXOffset, textNode.getYOffset() );
            }
        } );
    }
}

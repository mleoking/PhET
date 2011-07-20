// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;

/**
 * This class represents the bars in the bar chart.  They grow upwards in the Y direction from a baseline offset of y=0.
 *
 * @author Sam Reid
 */
public class Bar extends PNode {
    public static final float WIDTH = 40;

    public Bar( final ObservableProperty<Color> color, String caption, final Option<PNode> icon, final ObservableProperty<Double> value, final ObservableProperty<Boolean> showValue, final double verticalAxisScale ) {
        // Create and add the bar itself.
        final PPath bar = new PhetPPath( new BasicStroke( 1f ), Color.BLACK ) {{
            color.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setPaint( color );
                }
            } );
        }};
        addChild( bar );
        // Wire up the bar to change size based on the observable entity.
        value.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                float height = (float) ( value * verticalAxisScale );

                //Graphics problems occur if you let the bar go too high, so clamp it
                float maxBarHeight = 10000f;
                if ( height > maxBarHeight || Float.isNaN( height ) || Float.isInfinite( height ) ) {
                    height = maxBarHeight;
                }
                bar.setPathToRectangle( 0, -height, WIDTH, height );
            }
        } );
        // Create and add the caption.
        PText captionNode = new PText( caption ) {{
            setFont( CONTROL_FONT );
            // Position so that it is centered under the bar.
            setOffset( WIDTH / 2 - getFullBoundsReference().width / 2, 5 );
        }};
        addChild( captionNode );

        //If specified, show an icon next to the caption
        if ( icon.isSome() ) {
            PNode iconNode = new StandardizedNode( icon.get() );
            iconNode.setOffset( captionNode.getFullBounds().getMaxX() + 2,
                                captionNode.getFullBounds().getCenterY() - iconNode.getFullBounds().getHeight() / 2 );
            addChild( iconNode );
        }

        //Optionally show the readout of the exact value above the bar itself
        PText valueReadout = new PText() {{
            setFont( CONTROL_FONT );
            value.addObserver( new VoidFunction1<Double>() {
                public void apply( Double molesPerMeterCubed ) {
                    //Convert to Moles per Liter from SI
                    double litersPerCubicMeter = 1000;//See: http://wiki.answers.com/Q/How_many_metres_cubed_are_in_a_litre
                    double molesPerLiter = molesPerMeterCubed / litersPerCubicMeter;

                    //Update the text
                    setText( new DecimalFormat( "0.00" ).format( molesPerLiter ) + " mol/L" );

                    //Show the label centered above the bar, even if bar is zero height
                    setOffset( bar.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2,
                               bar.getFullBounds().getMinY() - getFullBounds().getHeight() );
                }
            } );

            //Only show the readout if the user has opted to do so
            showValue.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean showValue ) {
                    setVisible( showValue );
                }
            } );
        }};
        addChild( valueReadout );
    }
}
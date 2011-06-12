/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.setBackgroundDeep;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;
import static java.awt.Color.white;

/**
 * Optional bar chart that shows concentrations for both salt and sugar (if any)
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ConcentrationBarChart extends PNode {

    private final double CHART_HEIGHT = 234;
    protected final int INSET = 5;
    //Convert from model units (Mols) to stage units
    private final double verticalAxisScale;

    public ConcentrationBarChart( ObservableProperty<Double> saltConcentration,
                                  ObservableProperty<Double> sugarConcentration,
                                  final SettableProperty<Boolean> showValues,
                                  final SettableProperty<Boolean> visible,
                                  double scaleFactor ) {

        verticalAxisScale = 160 * 1E-4 * scaleFactor;
        final double totalWidth = 220;
        final PNode background = new PhetPPath( new Rectangle2D.Double( 0, 0, totalWidth, CHART_HEIGHT ),
                                                WATER_COLOR, new BasicStroke( 1f ), Color.BLACK );
        addChild( background );

        final double abscissaY = CHART_HEIGHT - 60;
        addChild( new PhetPPath( new Line2D.Double( INSET, abscissaY, totalWidth - INSET, abscissaY ), new BasicStroke( 2 ), Color.black ) );

        //Add a Salt concentration bar
        addChild( new Bar( white, "Salt", saltConcentration, showValues, verticalAxisScale ) {{
            setOffset( totalWidth / 2 - getFullBoundsReference().width / 2 - Bar.WIDTH, abscissaY );
        }} );

        //Add a Sugar concentration bar
        addChild( new Bar( white, "Sugar", sugarConcentration, showValues, verticalAxisScale ) {{
            setOffset( totalWidth / 2 - getFullBoundsReference().width / 2 + Bar.WIDTH + 25, abscissaY );
        }} );

        //Show the title
        addChild( new PText( "Concentration" ) {{
            setFont( SugarAndSaltSolutionsCanvas.TITLE_FONT );
            setOffset( ConcentrationBarChart.this.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, INSET );
        }} );

        //Commented out because we are considering removing it as of 5/15/2011
        //If this gets re-enabled, the totalWidth should be increased (around 250)
        //Add a vertical axis with labeled tick marks
//        addChild( new VerticalAxis( verticalAxisScale ) {{
//            setOffset( background.getFullBounds().getMaxX() - 50, abscissaY );
//        }} );

        //Add a minimize button that hides the bar chart (replaced with a "+" button which can be used to restore it
        addChild( new PImage( PhetCommonResources.getMinimizeButtonImage() ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    visible.set( false );
                }
            } );
            setOffset( background.getFullBounds().getWidth() - getFullBounds().getWidth() - INSET, INSET );
        }}
        );

        //Only show this bar chart if the user has opted to do so
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        //Add a checkbox that lets the user toggle on and off whether actual values are shown
        //This is in a VBox in case we need to add other controls later
        addChild( new VBox() {{
            addChild( new PSwing( new PropertyCheckBox( "Show values", showValues ) {{
                setFont( CONTROL_FONT );
                setBackgroundDeep( this, WATER_COLOR );
            }} ) );
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2, background.getHeight() - getFullBounds().getHeight() - INSET );
        }} );
    }

    // This class represents the bars on the bar chart.  They grow upwards in
    // the Y direction from a baseline offset of y=0.
    public static class Bar extends PNode {
        public static final float WIDTH = 40;

        public Bar( Color color, String caption, final ObservableProperty<Double> value, final ObservableProperty<Boolean> showValue, final double verticalAxisScale ) {
            // Create and add the bar itself.
            final PPath bar = new PhetPPath( color, new BasicStroke( 1f ), Color.BLACK );
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

    //Vertical axis and tick marks with labels
    private class VerticalAxis extends PNode {
        private VerticalAxis( double verticalAxisScale ) {

            //Show the vertical line, like a y-axis
            addChild( new PhetPPath( new Line2D.Double( 0, 0, 0, -verticalAxisScale * 0.82 ) ) );

            //Show tick marks along the side
            double[] tickMarks = new double[] { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08 };

            //Only label the even tick marks
            boolean even = false;
            for ( double tickMark : tickMarks ) {

                //Put it in the chart coordinate frame
                double y = -tickMark * 10 * verticalAxisScale;

                //Add the tick mark
                PhetPPath tick = new PhetPPath( new Line2D.Double( 0, y, 4, y ) );
                addChild( tick );

                //If an even number, show the tick mark
                if ( even ) {
                    PText label = new PText( new DecimalFormat( "0.00" ).format( tickMark / 1000.0 ) );
                    label.setOffset( tick.getFullBounds().getMaxX(), tick.getFullBounds().getCenterY() - label.getFullBounds().getHeight() / 2 );
                    addChild( label );
                }
                even = !even;
            }
        }
    }
}
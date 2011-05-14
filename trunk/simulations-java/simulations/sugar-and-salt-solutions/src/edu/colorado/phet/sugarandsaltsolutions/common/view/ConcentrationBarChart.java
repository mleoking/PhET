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
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

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

    private final double CHART_HEIGHT = 225;
    protected final int INSET = 5;

    public ConcentrationBarChart( ObservableProperty<Double> saltConcentration,
                                  ObservableProperty<Double> sugarConcentration,
                                  SettableProperty<Boolean> showValues,
                                  final SettableProperty<Boolean> visible ) {
        final double totalWidth = 200;
        final PNode background = new PhetPPath( new Rectangle2D.Double( 0, 0, totalWidth, CHART_HEIGHT ),
                                                WATER_COLOR, new BasicStroke( 1f ), Color.BLACK );
        addChild( background );

        final double abscissaY = CHART_HEIGHT - 65;
        addChild( new PhetPPath( new Line2D.Double( INSET, abscissaY, totalWidth - INSET, abscissaY ), new BasicStroke( 2 ), Color.black ) );

        //Add a Sugar concentration bar
        addChild( new Bar( white, "Salt", saltConcentration, showValues ) {{
            setOffset( totalWidth / 2 - getFullBoundsReference().width / 2 - Bar.WIDTH, abscissaY );
        }} );

        //Add a Salt concentration bar
        addChild( new Bar( white, "Sugar", sugarConcentration, showValues ) {{
            setOffset( totalWidth / 2 - getFullBoundsReference().width / 2 + Bar.WIDTH, abscissaY );
        }} );

        //Show the title
        addChild( new PText( "Concentration" ) {{
            setFont( new PhetFont( 16 ) );
            setOffset( ConcentrationBarChart.this.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, INSET );
        }} );

        //Add a checkbox that lets the user toggle on and off whether actual values are shown
        addChild( new PSwing( new PropertyCheckBox( "Show values", showValues ) {{
            setFont( CONTROL_FONT );
            SwingUtils.setBackgroundDeep( this, WATER_COLOR );
        }} ) {{

            //Center it horizontally in the bottom of the box, move up one pixel so it doesn't overlap the border stroke
            setOffset( background.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2,
                       background.getFullBounds().getMaxY() - getFullBounds().getHeight() - 1 );
        }} );

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
    }

    // This class represents the bars on the bar chart.  They grow upwards in
    // the Y direction from a baseline offset of y=0.
    public static class Bar extends PNode {
        public static final float WIDTH = 40;

        //Convert from model units (Mols) to stage units
        private final int verticalAxisScale = 160;

        public Bar( Color color, String caption, final ObservableProperty<Double> value, final ObservableProperty<Boolean> showValue ) {
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
                setFont( new PhetFont( 16 ) );
                // Position so that it is centered under the bar.
                setOffset( WIDTH / 2 - getFullBoundsReference().width / 2, 5 );
            }};
            addChild( captionNode );

            //Optionally show the readout of the exact value above the bar itself
            PText valueReadout = new PText() {{
                value.addObserver( new VoidFunction1<Double>() {
                    public void apply( Double molesPerMeterCubed ) {
                        //Convert to Moles per Liter from SI
                        double molesPerLiter = molesPerMeterCubed * 1000.0;

                        //Update the text
                        setText( new DecimalFormat( "0.00" ).format( molesPerLiter ) + " Mol/L" );

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
}

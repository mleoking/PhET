/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property5.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Optional bar chart that shows concentrations for both salt and sugar (if any)
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ConcentrationBarChart extends PNode {

    private final double CHART_HEIGHT = 200;
    private final int INSET = 5;

    public ConcentrationBarChart( ObservableProperty<Double> saltConcentration, ObservableProperty<Double> sugarConcentration ) {
        final double totalWidth = 200;
        PNode background = new PhetPPath( new Rectangle2D.Double( 0, 0, totalWidth, CHART_HEIGHT ),
                                          SugarAndSaltSolutionsApplication.WATER_COLOR, new BasicStroke( 1f ), Color.BLACK );
        addChild( background );

        final double abscissaY = CHART_HEIGHT - 50;
        addChild( new PhetPPath( new Line2D.Double( INSET, abscissaY, totalWidth - INSET, abscissaY ), new BasicStroke( 2 ), Color.black ) );

        //Add a Sugar concentration bar
        addChild( new Bar( Color.white, "Salt", saltConcentration ) {{
            setOffset( totalWidth / 2 - getFullBoundsReference().width / 2 - Bar.WIDTH, abscissaY );
        }} );

        //Add a Salt concentration bar
        addChild( new Bar( Color.white, "Sugar", sugarConcentration ) {{
            setOffset( totalWidth / 2 - getFullBoundsReference().width / 2 + Bar.WIDTH, abscissaY );
        }} );
    }

    // This class represents the bars on the bar chart.  They grow upwards in
    // the Y direction from a baseline offset of y=0.
    public static class Bar extends PNode {
        public static final float WIDTH = 40;

        public Bar( Color color, String caption, ObservableProperty<Double> value ) {
            // Create and add the bar itself.
            final PPath bar = new PhetPPath( color, new BasicStroke( 1f ), Color.BLACK );
            addChild( bar );
            // Wire up the bar to change size based on the observable entity.
            value.addObserver( new VoidFunction1<Double>() {
                public void apply( Double value ) {
                    float height = (float) ( value * 1E5 );
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
        }
    }
}

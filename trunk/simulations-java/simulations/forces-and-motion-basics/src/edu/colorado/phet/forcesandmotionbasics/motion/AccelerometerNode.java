package edu.colorado.phet.forcesandmotionbasics.motion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the horizontal bar gauge showing the acceleration.  Decorations (title and tick labels) are left off here since a tiny version without them is used in the control panel.
 *
 * @author Sam Reid
 */
public class AccelerometerNode extends PNode {
    public AccelerometerNode( final Property<Option<Double>> acceleration ) {
        final double height = 15;
        final double barWidth = 170;
        final double barSideInset = 7;
        final PhetPPath background = new PhetPPath( new RoundRectangle2D.Double( 0 - barSideInset, 0, barWidth + barSideInset * 2, height, 10, 10 ), new GradientPaint( 0, 4, Color.white, 0, (float) height, new Color( 207, 208, 210 ), true ) );
        addChild( background );

        //Tweaked to get 10m/s/s to line up with 1st tick
        final double scale = 4.22;

        final boolean showBar = true;
        if ( showBar ) {
            addChild( new PhetPPath( new Rectangle2D.Double( barWidth / 2, 0, 25, height ), new GradientPaint( 0, 5, new Color( 248, 194, 216 ), 0, (float) height, new Color( 154, 105, 127 ), true ) ) {{
                acceleration.addObserver( new VoidFunction1<Option<java.lang.Double>>() {
                    public void apply( final Option<java.lang.Double> doubles ) {
                        double value = doubles.getOrElse( 0.0 );
                        final double scaled = value * scale;
                        if ( value > 0 ) {
                            final double scaledValue = scaled;
                            setPathTo( new Rectangle2D.Double( barWidth / 2, 0, scaledValue, height ) );
                        }
                        else {
                            final double scaledValue = Math.abs( scaled );
                            setPathTo( new Rectangle2D.Double( barWidth / 2 - scaledValue, 0, scaledValue, height ) );
                        }
                    }
                } );
            }} );
        }
        final boolean showKnob = true;
        if ( showKnob ) {
            final double knobThickness = 1;
            addChild( new PhetPPath( new Rectangle2D.Double( barWidth / 2, 0, knobThickness, height ), new GradientPaint( 0, 5, new Color( 248, 194, 216 ), 0, (float) height, new Color( 154, 105, 127 ), true ) ) {{
                acceleration.addObserver( new VoidFunction1<Option<java.lang.Double>>() {
                    public void apply( final Option<java.lang.Double> doubles ) {
                        double value = doubles.getOrElse( 0.0 );
                        final double scaled = value * scale;
                        final double scaledValue = scaled;
                        setPathTo( new Rectangle2D.Double( barWidth / 2 + scaledValue - knobThickness / 2, 0, knobThickness, height ) );
                    }
                } );
            }} );
        }
        addChild( new PhetPPath( background.getPathReference(), new BasicStroke( 1 ), Color.black ) );

        final double majorTickInset = 6;
        final double minorTickInset = 7;
        addChild( new PhetPPath( new Line2D.Double( barWidth / 2, majorTickInset, barWidth / 2, height - majorTickInset ) ) );
        addChild( new PhetPPath( new Line2D.Double( 0, majorTickInset, 0, height - majorTickInset ) ) );
        addChild( new PhetPPath( new Line2D.Double( barWidth, majorTickInset, barWidth, height - majorTickInset ) ) );
        addChild( new PhetPPath( new Line2D.Double( barWidth / 4, minorTickInset, barWidth / 4, height - minorTickInset ) ) );
        addChild( new PhetPPath( new Line2D.Double( 3 * barWidth / 4, minorTickInset, 3 * barWidth / 4, height - minorTickInset ) ) );
    }
}
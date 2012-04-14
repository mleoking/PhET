// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;

/**
 * TODO: Document after design finalized
 *
 * @author Sam Reid
 */
public class SpeedometerNode extends PNode {
    private final double maxSpeed;
    private final double anglePerTick = Math.PI * 2 / 4 / 8;
    private final int numTicks = ( 8 + 2 ) * 2;

    public SpeedometerNode( String title, final double width, final ObservableProperty<Option<Double>> speed, final double maxSpeed ) {
        this.maxSpeed = maxSpeed;
        addChild( new PhetPPath( new Ellipse2D.Double( 0, 0, width, width ), Color.white, new BasicStroke( 2 ), Color.gray ) );
        addChild( new PhetPText( title ) {{
            setFont( new PhetFont( 16 ) );

            //Scale the text to fit within the speedometer if it is too big
            final double MAX_WIDTH = 65.0;
            if ( getFullWidth() > MAX_WIDTH ) {
                scale( MAX_WIDTH / getFullWidth() );
            }
            setOffset( width / 2 - getFullBounds().getWidth() / 2, width * 0.2 );
        }} );
        addChild( new PhetPPath( new BasicStroke( 2 ), Color.red ) {{
            speed.addObserver( new VoidFunction1<Option<Double>>() {
                public void apply( Option<Double> speed ) {
                    if ( speed.isSome() ) {
                        double angle = speedToAngle( speed.get() );
                        ImmutableVector2D center = new ImmutableVector2D( width / 2, width / 2 );
                        ImmutableVector2D delta = ImmutableVector2D.createPolar( 1.0, angle );
                        ImmutableVector2D tail = center.minus( delta.times( width / 10 ) );
                        ImmutableVector2D tip = center.plus( delta.times( width / 2 ) );
                        setPathTo( new Line2D.Double( tail.toPoint2D(), tip.toPoint2D() ) );
                    }
                    else {
                        setPathTo( new Rectangle2D.Double( 0, 0, 0, 0 ) );
                    }
                }
            } );
        }} );
        double dotWidth = 2;
        addChild( new PhetPPath( new Ellipse2D.Double( width / 2 - dotWidth / 2, width / 2 - dotWidth / 2, dotWidth, dotWidth ), Color.blue ) );

        double ds = maxSpeed / numTicks;
        for ( int i = 0; i < numTicks + 1; i++ ) {
            double angle = speedToAngle( i * ds );
            ImmutableVector2D center = new ImmutableVector2D( width / 2, width / 2 );
            ImmutableVector2D delta = ImmutableVector2D.createPolar( 1.0, angle );
            ImmutableVector2D tail = center.plus( delta.times( width / 2 * ( i % 2 == 0 ? 0.9 : 0.93 ) ) );
            ImmutableVector2D tip = center.plus( delta.times( width / 2 ) );
            final PhetPPath tick = new PhetPPath( new Line2D.Double( tail.toPoint2D(), tip.toPoint2D() ), new BasicStroke( i % 2 == 0 ? 1 : 0.5f ), Color.black );
            addChild( tick );
        }
    }

    public double speedToAngle( double speed ) {
        final Function.LinearFunction fun = new Function.LinearFunction( 0, maxSpeed, -Math.PI - anglePerTick * 2, anglePerTick * 2 );
        return fun.evaluate( speed );
    }
}
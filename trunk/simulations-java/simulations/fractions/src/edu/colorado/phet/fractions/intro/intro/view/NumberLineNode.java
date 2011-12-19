// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a number line and a dot on the number line to represent a fraction.
 * The dot is draggable to change the fraction.  The number line is truncated at 6 (the max number for fraction values) so it won't look weird at odd aspect ratios.
 *
 * @author Sam Reid
 */
public class NumberLineNode extends PNode {
    public NumberLineNode( final Property<Integer> numerator, final Property<Integer> denominator, ValueEquals<ChosenRepresentation> showing ) {
        scale( 5 );
        showing.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                setVisible( aBoolean );
            }
        } );

        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();

                //always go the same distance to whole numbers
                final double distanceBetweenTicks = 32;
                int divisionsBetweenTicks = denominator.get();

                double dx = distanceBetweenTicks / divisionsBetweenTicks;

                //The number line itself
                addChild( new PhetPPath( new Line2D.Double( 0, 0, dx * 6 * divisionsBetweenTicks, 0 ) ) );

                for ( int i = 0; i <= divisionsBetweenTicks * 6; i++ ) {

                    //Major ticks at each integer
                    if ( i % divisionsBetweenTicks == 0 ) {
                        int div = i / divisionsBetweenTicks;
                        final int mod = div % 2;
                        double height = mod == 0 ? 8 : 8;
                        final BasicStroke stroke = mod == 0 ? new BasicStroke( 1 ) : new BasicStroke( 0.5f );
                        final PhetPPath path = new PhetPPath( new Line2D.Double( i * dx, -height, i * dx, height ), stroke, Color.black );
                        final PhetPPath highlightPath = new PhetPPath( new Line2D.Double( i * dx, -height, i * dx, height ), new BasicStroke( 4 ), Color.yellow );

                        final int finalI = i;
                        new RichSimpleObserver() {
                            @Override public void update() {
                                highlightPath.setVisible( numerator.get().equals( finalI ) );
                            }
                        }.observe( numerator, denominator );
                        addChild( highlightPath );
                        addChild( path );
                        if ( mod == 0 || true ) {
                            addChild( new PhetPText( div + "", new PhetFont( 8 ) ) {{
                                setOffset( path.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, path.getFullBounds().getMaxY() );
                            }} );
                        }
                    }
                    else {

                        final PhetPPath highlightPath = new PhetPPath( new Line2D.Double( i * dx, -4, i * dx, 4 ), new BasicStroke( 4 ), Color.yellow );

                        final int finalI = i;
                        new RichSimpleObserver() {
                            @Override public void update() {
                                highlightPath.setVisible( numerator.get().equals( finalI ) );
                            }
                        }.observe( numerator, denominator );
                        addChild( highlightPath );

                        //minor ticks between the integers
                        addChild( new PhetPPath( new Line2D.Double( i * dx, -4, i * dx, 4 ), new BasicStroke( 0.25f ), Color.black ) );
                    }
                }

                final double w = 5;
                final double w2 = 0;
                addChild( new PhetPPath( new Area( new Ellipse2D.Double( -w / 2, -w / 2, w, w ) ) {{
                    subtract( new Area( new Ellipse2D.Double( -w2 / 2, -w2 / 2, w2, w2 ) ) );
                }}, FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 0.6f ), Color.black ) {{
                    setOffset( (double) numerator.get() / denominator.get() * distanceBetweenTicks, 0 );
                }} );
            }
        }.observe( numerator, denominator );
    }
}
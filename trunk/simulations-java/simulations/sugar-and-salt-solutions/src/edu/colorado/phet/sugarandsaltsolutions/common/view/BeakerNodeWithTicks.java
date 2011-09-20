// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.PATTERN__BEAKER_TICK_LABEL;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.Units.metersCubedToLiters;
import static java.awt.Color.black;
import static java.awt.Color.white;

/**
 * Graphical display of the beaker, including tick marks and labels along the side
 *
 * @author Sam Reid
 */
public class BeakerNodeWithTicks extends BeakerNode {
    public BeakerNodeWithTicks( ModelViewTransform transform, Beaker beaker, boolean showTickLabels, final Property<Boolean> whiteBackground ) {
        super( transform, beaker );

        //Add Tick marks and labels
        double viewX = transform.modelToViewX( beaker.getX() - beaker.getWallThickness() / 2 );

        //only show labels (and tick marks) for 0 L, 1 L, and 2 L, and tick marks for 0.5 L and 1.5 L

        //Show the minor ticks (behind the major ticks since we just draw over them).  Show one tick every 0.5L
        //Scale should be 1 if beaker can hold 2L
        final double scale = new LinearFunction( 0, 0.002, 0, 1 ).evaluate( beaker.getMaxFluidVolume() );

        double dMinorTick = 0.0005 * scale;//meters cubed
        for ( double minorTick = 0;
              minorTick <= 0.002 * scale;//Go up to the max fluid volume of 2L
              minorTick += dMinorTick ) {
            //Width of the tick line in stage coordinates
            double lineWidth = 5.0;

            //Find where the tick mark should be in the vertical direction
            double viewY = transform.modelToViewY( beaker.getHeightForVolume( minorTick ) + beaker.getY() );

            //Add the tick mark
            final Line2D.Double line = new Line2D.Double( viewX - lineWidth, viewY, viewX, viewY );
            addChild( new PhetPPath( line, new BasicStroke( 2 ), Color.white ) {{

                //Show it in black against white background and white against blue background
                whiteBackground.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean whiteBackground ) {
                        setStrokePaint( whiteBackground ? black : white );
                    }
                } );
            }} );
        }

        //Show the major tick marks, using formatting that suppresses the decimal point for round numbers, like 0.5L, 1L, etc.
        //Units are in SI here
        for ( double tick : new double[] { 0, 0.001 * scale, 0.002 * scale } ) {
            //Width of the tick mark line in stage coordinates
            double lineWidth = 10.0;

            //Location of the tick mark in the vertical direction
            double viewY = transform.modelToViewY( beaker.getHeightForVolume( tick ) + beaker.getY() );

            //Create and add the tick mark
            final PhetPPath tickMark = new PhetPPath( new Line2D.Double( viewX - lineWidth, viewY, viewX, viewY ), new BasicStroke( 4 ), white ) {{

                //Show it in black against white background and white against blue background
                whiteBackground.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean whiteBackground ) {
                        setStrokePaint( whiteBackground ? black : white );
                    }
                } );
            }};
            addChild( tickMark );

            if ( showTickLabels ) {
                //Create and add a tick mark label to the left of the tick mark, like "0.5L"
                final PNode labelNode = createLabelNode( tick, whiteBackground );
                labelNode.setOffset( tickMark.getFullBounds().getX() - labelNode.getFullBounds().getWidth(), tickMark.getFullBounds().getCenterY() - labelNode.getFullBounds().getHeight() / 2 );
                addChild( labelNode );
            }
        }
    }

    //Create a text (PText or HTMLNode) node to show the value.  HTML is used for formatting exponentials
    private PNode createLabelNode( double volume, final Property<Boolean> whiteBackground ) {
        final PhetFont font = new PhetFont( 20 );
        if ( volume == 0 || volume > 1E-20 ) {

            final double liters = metersCubedToLiters( volume );

            //Converts the SI unit to liters and formats it according to the specified format (such as 0.5L, 1L,...)
            String formatLiters = liters < 1E-6 && liters > 0 ? "" : new DecimalFormat( "0" ).format( liters );

            return new PText( MessageFormat.format( PATTERN__BEAKER_TICK_LABEL, formatLiters ) ) {{

                //Show it in black against white background and white against blue background
                whiteBackground.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean whiteBackground ) {
                        setTextPaint( whiteBackground ? black : white );
                    }
                } );
                setFont( font );
            }};
        }
        else {
            final String value = volumeToHTMLString( volume, "0" );
            return new HTMLNode( MessageFormat.format( PATTERN__BEAKER_TICK_LABEL, value ) ) {{

                //Show it in black against white background and white against blue background
                whiteBackground.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean whiteBackground ) {
                        setHTMLColor( whiteBackground ? black : white );
                    }
                } );
                setFont( font );
            }};
        }
    }

    //Get the HTML text that will be used for the specified volume in meters cubed
    public static String volumeToHTMLString( double volumeInMetersCubed, String format ) {

        //Convert to liters
        double volumeInLiters = metersCubedToLiters( volumeInMetersCubed );

        //Use an exponent of 10^-23 so that the prefix will range from 0 to 2 like in the first tab
        int exponent = -23;
        double mantissa = volumeInLiters / Math.pow( 10, exponent );

        //Show no water as 0 L water
        if ( volumeInLiters == 0 ) {
            return "0";
        }

        //Otherwise, show in exponential notation
        else {
            return new DecimalFormat( format ).format( mantissa ) + "x10<sup>" + exponent + "</sup>";
        }
    }
}
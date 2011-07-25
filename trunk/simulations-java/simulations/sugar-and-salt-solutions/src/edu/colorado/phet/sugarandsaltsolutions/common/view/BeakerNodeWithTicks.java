// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.metersCubedToLiters;
import static java.awt.Color.white;

/**
 * Graphical display of the beaker, including tick marks and labels along the side
 *
 * @author Sam Reid
 */
public class BeakerNodeWithTicks extends BeakerNode {
    public BeakerNodeWithTicks( ModelViewTransform transform, Beaker beaker ) {
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
            addChild( new PhetPPath( line, new BasicStroke( 2 ), white ) );
        }

        //Show the major tick marks, using formatting that suppresses the decimal point for round numbers, like 0.5L, 1L, etc.
        //Units are in SI here
        for ( double tick : new double[] { 0, 0.001 * scale, 0.002 * scale } ) {
            //Width of the tick mark line in stage coordinates
            double lineWidth = 10.0;

            //Location of the tick mark in the vertical direction
            double viewY = transform.modelToViewY( beaker.getHeightForVolume( tick ) + beaker.getY() );

            //Create and add the tick mark
            final PhetPPath tickMark = new PhetPPath( new Line2D.Double( viewX - lineWidth, viewY, viewX, viewY ), new BasicStroke( 4 ), white );
            addChild( tickMark );

            //Create and add a tick mark label to the left of the tick mark, like "0.5L"
            final PNode labelNode = createLabelNode( tick );
            labelNode.setOffset( tickMark.getFullBounds().getX() - labelNode.getFullBounds().getWidth(), tickMark.getFullBounds().getCenterY() - labelNode.getFullBounds().getHeight() / 2 );
            addChild( labelNode );
        }
    }

    //Create a text (PText or HTMLNode) node to show the value.  HTML is used for formatting exponentials
    private PNode createLabelNode( double volume ) {
        final PhetFont font = new PhetFont( 20 );
        final Color textPaint = white;
        if ( volume == 0 || volume > 1E-20 ) {

            final double liters = metersCubedToLiters( volume );

            //Converts the SI unit to liters and formats it according to the specified format (such as 0.5L, 1L,...)
            String formatLiters = liters < 1E-6 && liters > 0 ? "" : new DecimalFormat( "0" ).format( liters );

            return new PText( formatLiters + "L" ) {{
                setTextPaint( textPaint );
                setFont( font );
            }};
        }
        else {
            return new HTMLNode( volumeToHTMLString( volume, "0" ) + "L" ) {{
                setFont( font );
                setHTMLColor( textPaint );
            }};
        }
    }

    //Get the HTML text that will be used for the specified volume in meters cubed
    public static String volumeToHTMLString( double volume, String format ) {
        //Convert the number to exponent + mantissa so it can be displayed using HTML like 2 x 10^-24
        // see http://www.thatsjava.com/java-essentials/68687
        double log10 = Math.log10( metersCubedToLiters( volume ) );
        int exponent = (int) log10;
        double mantissa = Math.pow( 10, log10 - exponent );

        //The algorithm above can yield mantissas like 0.123, so move the decimal point over one step
        if ( mantissa < 1 ) {
            mantissa = mantissa * 10;
            exponent = exponent - 1;
        }

        return new DecimalFormat( format ).format( mantissa ) + "x10<sup>" + exponent + "</sup>";
    }
}
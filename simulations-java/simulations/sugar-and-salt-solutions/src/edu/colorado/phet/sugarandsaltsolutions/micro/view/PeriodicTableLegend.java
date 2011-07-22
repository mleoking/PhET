// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;
import static java.awt.Color.*;

/**
 * Legend for the periodic table to indicate coloring scheme for metals vs nonmetals.
 *
 * @author Sam Reid
 */
public class PeriodicTableLegend extends PNode {

    //Create a table legend based on the width of the periodic table
    public PeriodicTableLegend( final double periodicTableWidth ) {
        Rectangle2D.Double box = new Rectangle2D.Double( 0, 0, 100, 50 );

        //Create a box in the bottom left for gray metals
        final HBox metalBox = new HBox( new PhetPPath( box, lightGray, new BasicStroke( 1 ), black ), new PText( "Metal" ) {{
            setFont( CONTROL_FONT );
            setTextPaint( white );
        }} );
        addChild( metalBox );

        //Create a box in the bottom right for pink non-metals
        addChild( new HBox( new PhetPPath( box, pink, new BasicStroke( 1 ), black ), new PText( "Non-metal" ) {{
            setFont( CONTROL_FONT );
            setTextPaint( white );
        }} ) {{
            setOffset( periodicTableWidth - getFullBounds().getWidth(), 0 );
        }} );
    }
}
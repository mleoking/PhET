// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable;

import java.awt.BasicStroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.CELL_DIMENSION;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.METAL;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.NON_METAL;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas.CONTROL_FONT;
import static edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable.PeriodicTableDialog.METAL_COLOR;
import static edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable.PeriodicTableDialog.NON_METAL_COLOR;
import static java.awt.Color.black;
import static java.awt.Color.white;

/**
 * Legend for the periodic table to indicate coloring scheme for metals vs nonmetals.
 *
 * @author Sam Reid
 */
public class PeriodicTableLegend extends PNode {

    //Create a table legend based on the width of the periodic table
    public PeriodicTableLegend( final double periodicTableWidth, double scale, final ObservableProperty<Boolean> whiteBackground ) {

        //use color squares that are the same size as the element squares
        Rectangle2D.Double box = new Rectangle2D.Double( 0, 0, CELL_DIMENSION * scale, CELL_DIMENSION * scale );

        //Create a box in the bottom left for gray metals
        final HBox metalBox = new HBox( new PhetPPath( box, METAL_COLOR, new BasicStroke( 1 ), black ), new PText( METAL ) {{
            setFont( CONTROL_FONT );
            whiteBackground.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean whiteBackground ) {
                    setTextPaint( whiteBackground ? black : white );
                }
            } );
        }} );
        addChild( metalBox );

        //Create a box in the bottom right for pink non-metals
        addChild( new HBox( new PhetPPath( box, NON_METAL_COLOR, new BasicStroke( 1 ), black ), new PText( NON_METAL ) {{
            setFont( CONTROL_FONT );
            whiteBackground.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean whiteBackground ) {
                    setTextPaint( whiteBackground ? black : white );
                }
            } );
        }} ) {{
            setOffset( periodicTableWidth - getFullBounds().getWidth(), 0 );
        }} );
    }
}
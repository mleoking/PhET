// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the different dots, one for each page and the labels above each dot.  Dot is filled in if that is where the user is.  Like on an ipad homescreen or on angry birds.
 *
 * @author Sam Reid
 */
class CarouselDotComponent extends PNode {
    public CarouselDotComponent( final IntegerProperty selectedPage ) {
        final int circleDiameter = 16;

        addChild( new HBox( new VBox( text( "1-5", selectedPage, 0 ), circle( selectedPage, circleDiameter, 0 ) ),
                            new VBox( text( "6-10", selectedPage, 1 ), circle( selectedPage, circleDiameter, 1 ) ) ) );
    }

    private PhetPPath circle( final IntegerProperty selectedPage, final int circleDiameter, final int index ) {
        return new PhetPPath( new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), new BasicStroke( 2 ), Color.gray ) {{
            selectedPage.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setPaint( integer == index ? Color.black : BuildAFractionCanvas.TRANSPARENT );
                }
            } );
        }};
    }

    private PhetPText text( final String s, final IntegerProperty selectedPage, final int index ) {
        return new PhetPText( s, new PhetFont( 15 ) ) {{
            selectedPage.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setTextPaint( integer == index ? Color.black : Color.gray );
                }
            } );
        }};
    }
}
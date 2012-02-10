// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;

/**
 * @author Sam Reid
 */
public class NumberGraphic extends VBox {
    public NumberGraphic( int number ) {
        super( 2 );
        HBox box = new HBox( 2 );
        for ( int i = 0; i < number; i++ ) {
            box.addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 20, 20 ), Color.blue ) );
            if ( i % 4 == 3 ) {
                addChild( box );
                box = new HBox( 2 );
            }
        }
        if ( box.getChildrenCount() > 0 ) {
            addChild( box );
        }
    }

    public static void main( String[] args ) {
        new NumberGraphic( 10 );
    }
}
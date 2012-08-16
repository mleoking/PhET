// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode.Element;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.v;
import static edu.colorado.phet.common.phetcommon.view.util.ShapeUtils.createShapeFromPoints;
import static edu.colorado.phet.functions.intro.view.Representation.*;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class NavigationBar extends PNode {
    @SuppressWarnings("unchecked") public NavigationBar() {
        final PhetFont font = new PhetFont( 26, true );
        final PhetFont smallFont = new PhetFont( 18 );
        RadioButtonStripControlPanelNode<Representation> panel = new RadioButtonStripControlPanelNode<Representation>(
                new Property<Representation>( SHAPES ),
                Arrays.asList( new Element<Representation>( new ShapeIcon(), SHAPES, null ),
                               new Element<Representation>( new PhetPText( "ABC", font ), TEXT, null ),
                               new Element<Representation>( new PhetPText( "123", font ), NUMBERS, null ),
                               new Element<Representation>( new VBox( 3, new ShapeIcon() {{scale( 0.5 );}},
                                                                      new PhetPText( "ABC", smallFont ),
                                                                      new PhetPText( "123", smallFont ) ), MIXED, null ) ), 5 );
        addChild( panel );
    }

    public static class ShapeIcon extends PNode {
        public ShapeIcon() {
            double sideLength = 50;
            final double y = -sideLength * Math.sqrt( 3 ) / 2;
            addChild( new PhetPPath( createShapeFromPoints( list( v( 0, 0 ), v( sideLength, 0 ), v( sideLength / 2, y ) ) ), Color.red, new BasicStroke( 2 ), Color.black ) );
        }
    }
}
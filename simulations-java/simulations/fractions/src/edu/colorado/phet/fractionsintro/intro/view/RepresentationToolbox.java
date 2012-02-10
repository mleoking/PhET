// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Toolbox that lets the user select different representations.
 *
 * @author Sam Reid
 */
public class RepresentationToolbox extends PNode {
    static class Selector extends PNode {
        Selector( String text, final Property<Boolean> selected ) {
            final Font labelFont = new PhetFont( 18 );
            final PhetPText child = new PhetPText( text, labelFont );
            Rectangle2D r = RectangleUtils.expandRectangle2D( new PhetPText( "Reduced fraction", labelFont ).getFullBounds(), 5, 5 );
            addChild( new PhetPPath( new RoundRectangle2D.Double( r.getX(), r.getY(), r.getWidth(), r.getHeight(), 10, 10 ), Color.yellow ) {{
                selected.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean selected ) {
                        setPaint( selected ? Color.yellow : new Color( 0, 0, 0, 0 ) );
                    }
                } );
            }} );
            addChild( child );

            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    selected.set( !selected.get() );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }
    }

}
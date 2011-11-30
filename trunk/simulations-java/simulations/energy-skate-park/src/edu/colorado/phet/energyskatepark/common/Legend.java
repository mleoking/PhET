// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 8:02:03 PM
 */

public class Legend extends PhetPNode {
    private final ArrayList list = new ArrayList();
    private final double padY = 2;
    private final PPath background;

    public Legend() {
        background = new PPath();
        background.setPaint( Color.white );
        addChild( background );
    }

    public void setBackgroundPaint( Paint paint ) {
        background.setPaint( paint );
    }

    private static class Entry extends PNode {
        final String text;
        final PNode node;
        private final ShadowPText textNode;

        public Entry( String text, Color textColor, PNode node ) {
            this.text = text;
            this.node = node;
            textNode = new ShadowPText( text );
            textNode.setShadowOffset( 1, 1 );
            textNode.setFont( new PhetFont( 16, false, false ) );
            textNode.setShadowColor( Color.black );
            textNode.setTextPaint( textColor );
            addChild( textNode );
            addChild( node );
            textNode.setOffset( node.getFullBounds().getMaxX() + 2, node.getFullBounds().getY() );
        }

        public void setFont( Font font ) {
            textNode.setFont( font );
        }
    }

    public void addEntry( String text, Color color ) {
        Entry entry = new Entry( text, color, new SquareColor( color ) );
        list.add( entry );
        addChild( entry );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        if ( list.size() > 0 ) {
            Entry first = (Entry) list.get( 0 );
            first.setOffset( 0, 5 );
            Rectangle2D bounds = new PBounds( first.getFullBounds() );
            for ( int i = 1; i < list.size(); i++ ) {
                Entry prev = (Entry) list.get( i - 1 );
                Entry entry = (Entry) list.get( i );
                entry.setOffset( prev.getFullBounds().getX(), prev.getFullBounds().getMaxY() + padY );
                bounds = bounds.createUnion( entry.getFullBounds() );
            }
            background.setPathTo( RectangleUtils.expand( bounds, 5, 5 ) );
            double dy = background.getFullBounds().getY();
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                getChild( i ).translate( 0, -dy );
            }
        }
    }

    private static class SquareColor extends PPath {
        public SquareColor( Color color ) {
            setPathTo( new Rectangle( 0, 0, 10, 10 ) );
            setPaint( color );
        }
    }

    public void setFont( Font font ) {
        for ( int i = 0; i < list.size(); i++ ) {
            Entry entry = (Entry) list.get( i );
            entry.setFont( font );
        }
    }

}

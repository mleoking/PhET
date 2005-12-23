/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.piccolo.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 22, 2005
 * Time: 8:02:03 PM
 * Copyright (c) Dec 22, 2005 by Sam Reid
 */

public class Legend extends PNode {
    private ArrayList list = new ArrayList();
    private double insetY = 2;
    private PPath background;

    public Legend() {
        background = new PPath();
        background.setPaint( Color.white );
        addChild( background );
    }

    private static class Entry extends PNode {
        String text;
        PNode node;
        private ShadowPText textNode;

        public Entry( String text, Color textColor, PNode node ) {
            this.text = text;
            this.node = node;
            textNode = new ShadowPText( text );
            textNode.setFont( new LucidaSansFont( 16, false, true ) );
            textNode.setShadowColor( Color.black );
            textNode.setTextPaint( textColor );
            addChild( textNode );
            addChild( node );
            textNode.setOffset( node.getFullBounds().getMaxX() + 2, node.getFullBounds().getY() );
        }
    }

    public void addEntry( String text, Color color ) {
        Entry entry = new Entry( text, color, new SquareColor( color ) );
        list.add( entry );
        addChild( entry );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        if( list.size() > 0 ) {
            Rectangle2D bounds = new PBounds( ( (PNode)list.get( 0 ) ).getFullBounds() );
            for( int i = 1; i < list.size(); i++ ) {
                Entry prev = (Entry)list.get( i - 1 );
                Entry entry = (Entry)list.get( i );
                entry.setOffset( prev.getFullBounds().getX(), prev.getFullBounds().getMaxY() + insetY );
                bounds = bounds.createUnion( entry.getFullBounds() );
            }
            background.setPathTo( RectangleUtils.expand( bounds, 5, 5 ) );
        }
    }

    private static class SquareColor extends PPath {
        public SquareColor( Color color ) {
            setPathTo( new Rectangle( 0, 0, 10, 10 ) );
            setPaint( color );
        }
    }
}

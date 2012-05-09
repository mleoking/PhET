// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public @Data class GameItem {
    public final String text;
    public final Vector2D position;

    public GameItem( final String text, Vector2D position ) {
        this.text = text;
        this.position = position;
    }

    public PNode toPNode() {
        return new PText( text ) {{
            setOffset( position.toPoint2D() );
        }};
    }
}
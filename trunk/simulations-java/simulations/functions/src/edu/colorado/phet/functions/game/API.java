// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import fj.data.List;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Functions that can be used in the game maker
 *
 * @author Sam Reid
 */
public class API {
    public static GameItem head( final GameState gameState ) {
        return gameState.getItems().head();
    }

    //How to do typed tuples and let user specify type parameters?
    public static GameItem append( final GameItem translate, final String s ) {
        return new GameItem( translate.text + s, translate.position );
    }

    public static GameState gameState( GameItem items ) {
        return new GameState( items );
    }

    public static GameState fromItems( final List<GameItem> map ) {
        return new GameState( map );
    }

    public static GameItem translate( final GameItem gameItem, final Vector2D vector2D ) {
        return new GameItem( gameItem.text, gameItem.position.plus( vector2D ) );
    }

    public static GameItem setPosition( final GameItem gameItem, final Vector2D position ) {
        return new GameItem( gameItem.text, position );
    }
}
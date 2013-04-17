// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import fj.data.List;
import lombok.Data;

import java.util.Arrays;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public @Data class GameState {
    public final List<GameItem> items;

    public GameState( GameItem... items ) {
        this.items = List.iterableList( Arrays.asList( items ) );
    }

    public GameState( final List<GameItem> map ) {
        this.items = map;
    }

    public PNode toPiccoloNode( final EventRecorder eventRecorder ) {
        return new PNode() {{
            for ( GameItem item : items ) {
                addChild( item.toPNode() );
            }
        }};
    }


}
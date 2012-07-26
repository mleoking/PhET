// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import lombok.Data;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public @Data class LevelInfo {
    public final LevelIdentifier levelIdentifier;
    public final String name;
    public final PNode icon;
    public final LevelProgress levelProgress;

    public static final F<LevelInfo, PNode> _icon = new F<LevelInfo, PNode>() {
        @Override public PNode f( final LevelInfo levelInfo ) {
            return levelInfo.icon;
        }
    };
}
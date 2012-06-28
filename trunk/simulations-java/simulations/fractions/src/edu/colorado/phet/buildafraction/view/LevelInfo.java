package edu.colorado.phet.buildafraction.view;

import fj.F;
import lombok.Data;

import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public @Data class LevelInfo {
    public final String name;
    public final PNode icon;
    public final int filledStars;
    public final int maxStars;
    public final int levelIndex;
    public final LevelType levelType;

    public static final F<LevelInfo, PNode> _icon = new F<LevelInfo, PNode>() {
        @Override public PNode f( final LevelInfo levelInfo ) {
            return levelInfo.icon;
        }
    };
}
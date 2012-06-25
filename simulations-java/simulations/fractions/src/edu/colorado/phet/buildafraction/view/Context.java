package edu.colorado.phet.buildafraction.view;

/**
 * @author Sam Reid
 */
public interface Context {
    void homeButtonPressed();

    void levelButtonPressed( AbstractLevelSelectionNode parent, LevelInfo info );
}
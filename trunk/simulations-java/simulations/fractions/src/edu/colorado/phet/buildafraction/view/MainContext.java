package edu.colorado.phet.buildafraction.view;

/**
 * @author Sam Reid
 */
public interface MainContext {
    void homeButtonPressed();

    void levelButtonPressed( AbstractLevelSelectionNode parent, LevelInfo info );
}
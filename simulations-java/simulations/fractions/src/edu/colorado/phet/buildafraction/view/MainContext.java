package edu.colorado.phet.buildafraction.view;

/**
 * @author Sam Reid
 */
public interface MainContext {
    void goBackToHomeScreen();

    void levelButtonPressed( AbstractLevelSelectionNode parent, LevelInfo info );
}
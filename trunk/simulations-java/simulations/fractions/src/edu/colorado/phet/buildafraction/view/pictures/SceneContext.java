package edu.colorado.phet.buildafraction.view.pictures;

/**
 * @author Sam Reid
 */
public interface SceneContext {
    void goToNextPictureLevel( int newLevelIndex );

    void goToNextNumberLevel( int newLevelIndex );

    void goToLevelSelectionScreen();
}
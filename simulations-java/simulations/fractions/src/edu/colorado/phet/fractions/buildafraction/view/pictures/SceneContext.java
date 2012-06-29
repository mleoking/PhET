// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

/**
 * @author Sam Reid
 */
public interface SceneContext {
    void goToNextPictureLevel( int newLevelIndex );

    void goToNextNumberLevel( int newLevelIndex );

    void goToLevelSelectionScreen();
}
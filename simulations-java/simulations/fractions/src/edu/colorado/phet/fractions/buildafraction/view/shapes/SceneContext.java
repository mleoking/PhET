// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

/**
 * Context for a scene, which provides an interface for navigation and resampling.
 *
 * @author Sam Reid
 */
public interface SceneContext {
    void goToNextShapeLevel( int newLevelIndex );

    void goToNextNumberLevel( int newLevelIndex );

    void goToLevelSelectionScreen();

    void resampleShapeLevel( int levelIndex );

    void resampleNumberLevel( int levelIndex );
}
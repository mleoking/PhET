// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

/**
 * Context for a scene, which provides an interface for navigation and re-sampling.
 *
 * @author Sam Reid
 */
public interface SceneContext {
    void goToShapeLevel( int newLevelIndex );

    void goToNumberLevel( int newLevelIndex );

    void goToLevelSelectionScreen( int fromLevelIndex );

    void resampleShapeLevel( int levelIndex );

    void resampleNumberLevel( int levelIndex );
}
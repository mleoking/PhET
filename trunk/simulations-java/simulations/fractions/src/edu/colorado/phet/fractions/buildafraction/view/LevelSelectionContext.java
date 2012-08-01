// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.Component;

/**
 * Context for level selection for resetting, and navigating to different levels.
 *
 * @author Sam Reid
 */
public interface LevelSelectionContext {
    void levelButtonPressed( AbstractLevelSelectionNode parent, LevelInfo info );

    void reset();

    Component getComponent();
}
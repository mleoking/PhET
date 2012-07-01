// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.Component;

/**
 * @author Sam Reid
 */
public interface MainContext {
    void levelButtonPressed( AbstractLevelSelectionNode parent, LevelInfo info );

    void reset();

    Component getComponent();
}
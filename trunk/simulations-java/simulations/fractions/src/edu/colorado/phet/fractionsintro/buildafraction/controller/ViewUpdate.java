package edu.colorado.phet.fractionsintro.buildafraction.controller;

import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;
import edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas;

/**
 * @author Sam Reid
 */
public interface ViewUpdate {
    void update( BuildAFractionState oldState, ModelUpdate update, BuildAFractionState newState, BuildAFractionCanvas canvas );
}
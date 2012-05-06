package edu.colorado.phet.fractionsintro.buildafraction.controller;

import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionState;

/**
 * @author Sam Reid
 */
public interface ModelUpdate {
    BuildAFractionState update( BuildAFractionState state );
}
package edu.colorado.phet.fractionsintro.buildafraction_functional.controller;

import edu.colorado.phet.fractionsintro.buildafraction_functional.model.BuildAFractionState;

/**
 * @author Sam Reid
 */
public interface ModelUpdate {
    BuildAFractionState update( BuildAFractionState state );
}
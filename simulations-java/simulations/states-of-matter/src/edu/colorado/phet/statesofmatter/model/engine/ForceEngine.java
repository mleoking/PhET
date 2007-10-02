package edu.colorado.phet.statesofmatter.model.engine;

import java.util.Collection;

public interface ForceEngine {
    ForceComputation compute(Collection particles, EngineConfig descriptor);
}

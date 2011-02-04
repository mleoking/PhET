// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.modules.LRRModule;

/**
 * @author Sam Reid
 */
public class IntroModule extends LRRModule<LRRModel> {
    public IntroModule() {
        super( "Intro", new LRRModel() );
        setSimulationPanel( new LightReflectionAndRefractionCanvas( getLRRModel() ) );
    }
}

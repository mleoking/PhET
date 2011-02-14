// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.modules.LRRModule;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.LightReflectionAndRefractionCanvas;

/**
 * @author Sam Reid
 */
public class PrismsModule extends LRRModule<LRRModel> {
    public PrismsModule() {
        super( "Prism Break", new LRRModel() );
        setSimulationPanel( new LightReflectionAndRefractionCanvas( getLRRModel() ) );
    }
}

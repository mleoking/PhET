// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import edu.colorado.phet.lightreflectionandrefraction.modules.LRRModule;

/**
 * @author Sam Reid
 */
public class PrismsModule extends LRRModule<PrismsModel> {
    public PrismsCanvas canvas;

    public PrismsModule() {
        super( "Prism Break", new PrismsModel() );
        canvas = new PrismsCanvas( getLRRModel(), moduleActive, resetAll );
        setSimulationPanel( canvas );
    }

    @Override
    protected void resetAll() {
        super.resetAll();
        canvas.resetAll();
    }
}

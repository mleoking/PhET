package edu.colorado.phet.insidemagnets;

import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * @author Sam Reid
 */
public class InsideMagnetsModule extends Module {
    private InsideMagnetsModel insideMagnetsModel;

    public InsideMagnetsModule() {
        this( new InsideMagnetsModel() );
    }

    public InsideMagnetsModule( InsideMagnetsModel model ) {
        super( "Inside Magnets", model.getClock() );
        this.insideMagnetsModel = model;
        setSimulationPanel( new InsideMagnetsCanvas( this ) );
    }

    public InsideMagnetsModel getInsideMagnetsModel() {
        return insideMagnetsModel;
    }
}

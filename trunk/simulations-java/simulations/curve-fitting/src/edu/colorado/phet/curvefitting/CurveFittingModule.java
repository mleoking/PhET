package edu.colorado.phet.curvefitting;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 4, 2008
 * Time: 10:48:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class CurveFittingModule extends Module {
    public CurveFittingModule() {
        super( "Curve Fitting", new ConstantDtClock( 30, 1.0 ) );
        setSimulationPanel( new CurveFittingSimulationPanel() );
    }
}

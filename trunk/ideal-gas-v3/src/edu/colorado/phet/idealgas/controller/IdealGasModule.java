/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.idealgas.Strings;
import edu.colorado.phet.idealgas.view.BaseIdealGasApparatusPanel;
import edu.colorado.phet.idealgas.model.IdealGasModel;

public class IdealGasModule extends Module {

    public IdealGasModule() {
        super( Strings.idealGasModuleName );

        setModel( new IdealGasModel() );
        setApparatusPanel( new BaseIdealGasApparatusPanel( this ) );
    }
}

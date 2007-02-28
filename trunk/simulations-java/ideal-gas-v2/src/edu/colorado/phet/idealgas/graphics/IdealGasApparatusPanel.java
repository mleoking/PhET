/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 18, 2003
 * Time: 8:46:30 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.graphics;

//import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
//import edu.colorado.phet.graphics.MovableImageGraphic;
//import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.common.application.PhetApplication;

import java.awt.*;

public class IdealGasApparatusPanel extends BaseIdealGasApparatusPanel {

    public IdealGasApparatusPanel( PhetApplication application ) {
        super( application,"Ideal Gas" );
    }

    /**
     * Starts the application running for the specific apparatus panel
     */
    public void activate() {
        super.activate();
        if( getIdealGasApplication().getPhetMainPanel() != null ) {
            getIdealGasApplication().run();
        }
    }
}

/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Oct 3, 2004
 * Time: 8:35:49 PM
 * Copyright (c) Oct 3, 2004 by Sam Reid
 */
public class GrabBagReadoutGraphic extends ReadoutGraphic {
    public GrabBagReadoutGraphic( CCKModule module, Branch branch, ModelViewTransform2D transform, ApparatusPanel apparatusPanel, DecimalFormat decimalFormat ) {
        super( module, branch, transform, apparatusPanel, decimalFormat );
        super.setVisible( false );
    }

    public void setVisible( boolean visible ) {
        //do nothing.
    }
}

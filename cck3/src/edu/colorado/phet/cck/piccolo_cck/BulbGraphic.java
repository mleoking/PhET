package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Bulb;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 1:03:35 AM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class BulbGraphic extends BranchNode {
    private Bulb bulb;
    private CCKModel cckModel;

    public BulbGraphic( CCKModel cckModel, Bulb bulb ) {
        this.cckModel = cckModel;
        this.bulb = bulb;
    }

    public Branch getBranch() {
        return null;
    }
}

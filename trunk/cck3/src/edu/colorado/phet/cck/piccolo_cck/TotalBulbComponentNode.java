package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Bulb;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:03:08 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class TotalBulbComponentNode extends BranchNode {
    private Bulb bulb;
    private FilamentNode filamentNode;
    private BulbComponentNode bulbComponentNode;

    public TotalBulbComponentNode( CCKModel cckModel, Bulb bulb ) {
        filamentNode = new FilamentNode( bulb.getFilament() );
        bulbComponentNode = new BulbComponentNode( cckModel, bulb );
        addChild( filamentNode );
        addChild( bulbComponentNode );
        this.bulb = bulb;
    }

    public Branch getBranch() {
        return bulb;
    }
}

package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Capacitor;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 9:07:00 AM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */

public class CapacitorNode extends BranchNode {
    private CCKModel model;
    private Capacitor capacitor;
    private static final Color tan = new Color( 255, 220, 130 );
    private Color plate1Color = tan;
    private Color plate2Color = tan;

    public CapacitorNode( CCKModel model, Capacitor capacitor ) {
        this.model = model;
        this.capacitor = capacitor;
    }

    public Branch getBranch() {
        return capacitor;
    }
}

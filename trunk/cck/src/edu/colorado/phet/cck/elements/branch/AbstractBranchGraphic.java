package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 10:21:56 PM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public interface AbstractBranchGraphic extends InteractiveGraphic, TransformListener, Boundary {
    Shape getStartWireShape();

    Shape getEndWireShape();

    Branch getBranch();

    void setWireColor(Color color);

    InteractiveGraphic getMainBranchGraphic();
}

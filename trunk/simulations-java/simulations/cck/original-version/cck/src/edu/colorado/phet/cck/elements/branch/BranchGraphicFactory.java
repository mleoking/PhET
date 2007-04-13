package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.components.AmmeterBranch;
import edu.colorado.phet.cck.elements.branch.components.Battery;
import edu.colorado.phet.cck.elements.branch.components.Bulb;
import edu.colorado.phet.cck.elements.branch.components.Switch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Nov 16, 2003
 * Time: 1:48:51 PM
 * Copyright (c) Nov 16, 2003 by Sam Reid
 */
public interface BranchGraphicFactory {
    DefaultCompositeBranchGraphic getSwitchGraphic( Switch branch );

    AbstractBranchGraphic getBulbGraphic( Bulb bulb );

    AbstractBranchGraphic getResistorGraphic( Branch resistor );

    DefaultCompositeBranchGraphic getWireGraphic( Branch wire );

    AbstractBranchGraphic getBatteryGraphic( Battery branch );

    DefaultCompositeBranchGraphic getImageGraphic( Circuit circuit, ModelViewTransform2D transform, Branch branch, CCK2Module module, BufferedImage image );

    void apply( CCK2Module cck2Module );

    AbstractBranchGraphic getAmmeterBranchGraphic( AmmeterBranch ammeterBranch );
}

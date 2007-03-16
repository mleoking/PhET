package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.piccolo_cck.ComponentImageNode;
import edu.colorado.phet.common.view.util.BufferedImageUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 12:02:15 AM
 * Copyright (c) Oct 5, 2006 by Sam Reid
 */
public class BatteryNode extends ComponentImageNode {
    private Battery battery;
    private ICCKModule module;

    public BatteryNode( CCKModel model, Battery battery, JComponent component, ICCKModule module ) {
        super( model, battery, getBatteryImage(), component, module );
        this.battery = battery;
        this.module = module;
        update();
    }

    private static BufferedImage getBatteryImage() {
        BufferedImage image = CCKImageSuite.getInstance().getLifelikeSuite().getBatteryImage();
        return BufferedImageUtils.rescaleFractional( image, 1.0, 1.3 );
    }

}

package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;
import edu.colorado.phet.circuitconstructionkit.view.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentImageNode;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 12:02:15 AM
 */
public class BatteryNode extends ComponentImageNode {
    private Battery battery;
    private CCKModule module;

    public BatteryNode( CCKModel model, Battery battery, JComponent component, CCKModule module ) {
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

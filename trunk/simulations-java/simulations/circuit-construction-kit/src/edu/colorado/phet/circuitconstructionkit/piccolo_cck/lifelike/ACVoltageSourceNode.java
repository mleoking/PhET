package edu.colorado.phet.circuitconstructionkit.piccolo_cck.lifelike;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.ACVoltageSource;
import edu.colorado.phet.circuitconstructionkit.piccolo_cck.ComponentImageNode;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 8:59:30 AM
 */

public class ACVoltageSourceNode extends ComponentImageNode {
    private ICCKModule module;
    private ACVoltageSource acVoltageSource;

    public ACVoltageSourceNode( CCKModel model, ACVoltageSource acVoltageSource, JComponent component, ICCKModule module ) {
        super( model, acVoltageSource, CCKImageSuite.getInstance().getACVoltageSourceImage(), component, module );
        this.module = module;
        this.acVoltageSource = acVoltageSource;
    }

}

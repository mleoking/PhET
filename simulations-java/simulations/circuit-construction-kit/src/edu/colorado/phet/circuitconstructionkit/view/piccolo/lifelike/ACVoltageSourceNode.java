package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.ACVoltageSource;
import edu.colorado.phet.circuitconstructionkit.view.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentImageNode;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 8:59:30 AM
 */

public class ACVoltageSourceNode extends ComponentImageNode {
    private CCKModule module;
    private ACVoltageSource acVoltageSource;

    public ACVoltageSourceNode( CCKModel model, ACVoltageSource acVoltageSource, JComponent component, CCKModule module ) {
        super( model, acVoltageSource, CCKImageSuite.getInstance().getACVoltageSourceImage(), component, module );
        this.module = module;
        this.acVoltageSource = acVoltageSource;
    }

}

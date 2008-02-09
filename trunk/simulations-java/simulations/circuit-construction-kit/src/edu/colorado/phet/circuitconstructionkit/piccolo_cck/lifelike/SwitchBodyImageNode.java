package edu.colorado.phet.circuitconstructionkit.piccolo_cck.lifelike;

import edu.colorado.phet.circuitconstructionkit.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.piccolo_cck.ComponentImageNode;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 */

public class SwitchBodyImageNode extends ComponentImageNode {
    public SwitchBodyImageNode( CCKModel model, Switch s, JComponent component, ICCKModule module ) {
        super( model, s, CCKImageSuite.getInstance().getKnifeBoardImage(), component, module );
    }
}

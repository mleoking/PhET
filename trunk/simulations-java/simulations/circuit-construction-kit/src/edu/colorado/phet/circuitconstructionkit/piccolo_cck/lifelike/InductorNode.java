package edu.colorado.phet.circuitconstructionkit.piccolo_cck.lifelike;

import edu.colorado.phet.circuitconstructionkit.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Inductor;
import edu.colorado.phet.circuitconstructionkit.piccolo_cck.ComponentImageNode;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 10:49:26 AM
 */

public class InductorNode extends ComponentImageNode {
    private ICCKModule module;
    private Inductor inductor;

    public InductorNode( CCKModel model, Inductor inductor, JComponent component, ICCKModule module ) {
        super( model, inductor, CCKImageSuite.getInstance().getInductorImage(), component, module );
        this.module = module;
        this.inductor = inductor;
    }

}

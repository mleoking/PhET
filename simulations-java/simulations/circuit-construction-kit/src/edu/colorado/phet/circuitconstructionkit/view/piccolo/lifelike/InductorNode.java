package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Inductor;
import edu.colorado.phet.circuitconstructionkit.view.CCKImageSuite;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentImageNode;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 10:49:26 AM
 */

public class InductorNode extends ComponentImageNode {
    private CCKModule module;
    private Inductor inductor;

    public InductorNode( CCKModel model, Inductor inductor, JComponent component, CCKModule module ) {
        super( model, inductor, CCKImageSuite.getInstance().getInductorImage(), component, module );
        this.module = module;
        this.inductor = inductor;
    }

}

package edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.grabbag.GrabBagResistor;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.ComponentImageNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 12:05:30 PM
 */
public class GrabBagResistorNode extends ComponentImageNode {
    private GrabBagResistor resistor;
    private CCKModule module;
    private SimpleObserver resistorObserver = new SimpleObserver() {
        public void update() {
            GrabBagResistorNode.this.update();
        }
    };

    public GrabBagResistorNode( CCKModel model, final GrabBagResistor resistor, JComponent component, final CCKModule module ) {
        super( model, resistor, resistor.getItemInfo().getImage(), component, module );
        this.resistor = resistor;
        this.module = module;
        resistor.addObserver( resistorObserver );
    }

    public void delete() {
        super.delete();
        resistor.removeObserver( resistorObserver );
    }

//    protected JPopupMenu createPopupMenu() {
//        return new ComponentMenu.ResistorMenu( resistor, module );//todo this should have a separate class, not reuse ResistorMenu
//    }

}

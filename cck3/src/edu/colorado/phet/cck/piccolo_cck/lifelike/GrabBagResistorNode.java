package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.grabbag.GrabBagResistor;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.piccolo_cck.ComponentImageNode;
import edu.colorado.phet.common_cck.util.SimpleObserver;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 12:05:30 PM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */
public class GrabBagResistorNode extends ComponentImageNode {
    private GrabBagResistor resistor;
    private ICCKModule module;
    private SimpleObserver resistorObserver = new SimpleObserver() {
        public void update() {
            GrabBagResistorNode.this.update();
        }
    };

    public GrabBagResistorNode( CCKModel model, final GrabBagResistor resistor, JComponent component, final ICCKModule module ) {
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

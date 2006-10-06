package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.grabbag.GrabBagResistor;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.piccolo_cck.ComponentImageNode;
import edu.colorado.phet.cck.piccolo_cck.ComponentMenu;
import edu.colorado.phet.common_cck.util.SimpleObserver;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 12:05:30 PM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */
public class GrabBagResistorNode extends ComponentImageNode {
    private GrabBagResistor resistor;
    private ICCKModule module;

    public GrabBagResistorNode( CCKModel model, final GrabBagResistor resistor, Component component, final ICCKModule module ) {
        super( model, resistor, resistor.getItemInfo().getImage(), component );
        this.resistor = resistor;
        this.module = module;
        resistor.addObserver( new SimpleObserver() {
            public void update() {
                GrabBagResistorNode.this.update();
            }
        } );
    }

    protected JPopupMenu createPopupMenu() {
        return new ComponentMenu.ResistorMenu( resistor, module ).getMenuComponent();//todo this should have a separate class, not reuse ResistorMenu
    }

}

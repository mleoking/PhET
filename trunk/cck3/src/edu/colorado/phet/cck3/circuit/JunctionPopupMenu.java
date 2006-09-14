/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.common.RepaintyMenu;
import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.components.Branch;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 9:48:27 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class JunctionPopupMenu extends RepaintyMenu {
    private Junction junction;
    private CircuitGraphic circuitGraphic;
    private JunctionSplitter splitter;
    private JMenuItem splitItem;

    public JunctionPopupMenu( Junction junction, CircuitGraphic circuitGraphic, CCKModule module ) {
        super( module.getApparatusPanel() );
        this.junction = junction;
        this.circuitGraphic = circuitGraphic;
        splitter = new JunctionSplitter( junction, circuitGraphic, module );
        splitItem = splitter.toJMenuItem();
        add( splitItem );
    }

    public void show( Component invoker, int x, int y ) {
        Circuit circuit = circuitGraphic.getCircuit();
        Branch[] b = circuit.getAdjacentBranches( junction );
        if( b.length > 1 ) {
            splitItem.setEnabled( true );
        }
        else {
            splitItem.setEnabled( false );
        }
        super.show( invoker, x, y );

    }
}

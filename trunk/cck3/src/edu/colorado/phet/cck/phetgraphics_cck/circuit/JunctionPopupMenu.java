/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.common.RepaintyMenu;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetgraphicsModule;

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

    public JunctionPopupMenu( Junction junction, CircuitGraphic circuit, CCKPhetgraphicsModule module ) {
        super( module.getApparatusPanel() );
        this.junction = junction;
        this.circuitGraphic = circuit;
        splitter = new JunctionSplitter( junction, circuit, module );
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

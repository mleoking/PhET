/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit;

import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.phetgraphics_cck.CCKModule;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.model.ModelElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 3:25:46 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class JunctionSplitter {
    Junction junction;
    //deletes the unified junction, then splits the others, and creates new junctions.
    CircuitGraphic circuitGraphic;
    private CCKModule module;

    public JunctionSplitter( Junction junction, CircuitGraphic circuitGraphic, CCKModule module ) {
        this.junction = junction;
        this.circuitGraphic = circuitGraphic;
        this.module = module;
    }

    public JMenuItem toJMenuItem() {
        JMenuItem item = new JMenuItem( SimStrings.get( "JunctionSplitter.SplitMenuItem" ) );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getModel().addModelElement( new ModelElement() {
                    public void stepInTime( double dt ) {
                        split();
                        module.getModel().removeModelElement( this );
                    }
                } );
            }
        } );
        return item;
    }

    private void split() {
        System.out.println( "Calling split." );
        circuitGraphic.split( junction );//getCircuit().split(junction);
    }
}

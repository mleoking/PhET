package edu.colorado.phet.cck.phetgraphics_cck.circuit;

import edu.colorado.phet.cck.CCKResources;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetgraphicsModule;
import edu.colorado.phet.common_cck.model.ModelElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 3:25:46 PM
 */
public class JunctionSplitter {
    Junction junction;
    //deletes the unified junction, then splits the others, and creates new junctions.
    CircuitGraphic circuitGraphic;
    private CCKPhetgraphicsModule module;

    public JunctionSplitter( Junction junction, CircuitGraphic circuitGraphic, CCKPhetgraphicsModule module ) {
        this.junction = junction;
        this.circuitGraphic = circuitGraphic;
        this.module = module;
    }

    public JMenuItem toJMenuItem() {
        JMenuItem item = new JMenuItem( CCKResources.getString( "JunctionSplitter.SplitMenuItem" ) );
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

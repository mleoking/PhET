/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.davissongermer.QWIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 1, 2006
 * Time: 7:22:27 AM
 */

public class PotentialPanel extends VerticalLayoutPanel {
    public PotentialPanel( final QWIModule module ) {

        setFillNone();
        setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "controls.barriers.title" ) ) );

        JButton clear = new JButton( QWIStrings.getString( "controls.barriers.remove-all" ) );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearPotential();
            }
        } );

        JButton newBarrier = new JButton( QWIStrings.getString( "controls.barriers.add-barrier" ) );
        newBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addPotential();
            }
        } );
        add( newBarrier );
        add( clear );

    }
}

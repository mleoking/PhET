/**
 * Class: ProfileShapingControlPanel
 * Class: edu.colorado.phet.nuclearphysics.controller
 * User: Ron LeMaster
 * Date: Mar 1, 2004
 * Time: 10:50:03 AM
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SingleNucleusFissionControlPanel extends JPanel {
    private SingleNucleusFissionModule module;

    public SingleNucleusFissionControlPanel( final SingleNucleusFissionModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );
        int rowIdx = 0;

        JButton fireNeutronBtn = new JButton( "Fire Neutron" );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
            }
        } );
        try {
            GraphicsUtil.addGridBagComponent( this, fireNeutronBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }
}

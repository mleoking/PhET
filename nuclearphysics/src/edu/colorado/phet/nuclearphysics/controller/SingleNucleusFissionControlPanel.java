/**
 * Class: ProfileShapingControlPanel
 * Class: edu.colorado.phet.nuclearphysics.controller
 * User: Ron LeMaster
 * Date: Mar 1, 2004
 * Time: 10:50:03 AM
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SingleNucleusFissionControlPanel extends JPanel {
    private SingleNucleusFissionModule module;

    public SingleNucleusFissionControlPanel( final SingleNucleusFissionModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );
        int rowIdx = 0;

        JButton fireNeutronBtn = new JButton( SimStrings.get( "SingleNucleusFissionControlPanel.FireButton" ) );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
            }
        } );

        JButton resetBtn = new JButton( SimStrings.get( "SingleNucleusFissionControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
            }
        } );

        try {
            GraphicsUtil.addGridBagComponent( this, new JLabel( "  " ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, fireNeutronBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( "  " ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, resetBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }

        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "SingleNucleusFissionControlPanel.ControlBorder" ) );
        this.setBorder( titledBorder );
    }
}

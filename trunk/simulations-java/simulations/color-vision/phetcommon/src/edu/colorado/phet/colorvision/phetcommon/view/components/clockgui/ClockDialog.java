/* Copyright 2002-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */
package edu.colorado.phet.colorvision.phetcommon.view.components.clockgui;

import edu.colorado.phet.colorvision.phetcommon.model.clock.AbstractClock;
import edu.colorado.phet.colorvision.phetcommon.model.clock.ThreadedClock;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ClockDialog
 *
 * @author Ron LeMaster
 * @version $Revision:14443 $
 */
public class ClockDialog extends JDialog {

    private AbstractClock clock;
    private ClockControlPanel clockControlPanel;

    public ClockDialog( JFrame parentFrame, ThreadedClock clock ) {
        super( parentFrame, SimStrings.get( "Common.ClockDialog.Title" ), false );
        init( clock );
        this.pack();
        clock.addClockStateListener( clockControlPanel );
    }

    private void init( ThreadedClock clock ) {
        this.clock = clock;
        Container contentPane = this.getContentPane();
        contentPane.setLayout( new BorderLayout() );
        clockControlPanel = new ClockControlPanel( clock );
        contentPane.add( clockControlPanel, BorderLayout.CENTER );
        contentPane.add( buildButtonPanel(), BorderLayout.SOUTH );
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPnl = new JPanel( new FlowLayout() );
        JButton okBtn = new JButton( SimStrings.get( "Common.ClockDialog.OK" ) );
        okBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                updateClock();
                ClockDialog.this.hide();
            }
        } );
        buttonPnl.add( okBtn );

        JButton cancelBtn = new JButton( SimStrings.get( "Common.ClockDialog.Cancel" ) );
        cancelBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                ClockDialog.this.hide();
            }
        } );
        buttonPnl.add( cancelBtn );
        return buttonPnl;
    }

    private void updateClock() {
        clock.setDt( clockControlPanel.getDt() );
        clock.setDelay( clockControlPanel.getSleepInterval() );
        this.hide();
    }

    public void setClock( AbstractClock c ) {
        this.clock = c;
        clockControlPanel.setClock( c );
    }

}

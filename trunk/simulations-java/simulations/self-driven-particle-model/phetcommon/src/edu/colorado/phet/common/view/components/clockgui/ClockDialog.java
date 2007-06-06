/* Copyright 2002-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/components/clockgui/ClockDialog.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view.components.clockgui;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ClockDialog
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public class ClockDialog extends JDialog {

    private AbstractClock clock;
    private ClockParamSetterPanel clockParamSetterPanel;

    public ClockDialog( JFrame parentFrame, ThreadedClock clock ) {
        super( parentFrame, SimStrings.get( "Common.ClockDialog.Title" ), false );
        init( clock );
        this.pack();
        clock.addClockStateListener( clockParamSetterPanel );
    }

    private void init( ThreadedClock clock ) {
        this.clock = clock;
        Container contentPane = this.getContentPane();
        contentPane.setLayout( new BorderLayout() );
        clockParamSetterPanel = new ClockParamSetterPanel( clock );
        contentPane.add( clockParamSetterPanel, BorderLayout.CENTER );
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
        clock.setDt( clockParamSetterPanel.getDt() );
        clock.setDelay( clockParamSetterPanel.getSleepInterval() );
        this.hide();
    }

    public void setClock( AbstractClock c ) {
        this.clock = c;
        clockParamSetterPanel.setClock( c );
    }

}

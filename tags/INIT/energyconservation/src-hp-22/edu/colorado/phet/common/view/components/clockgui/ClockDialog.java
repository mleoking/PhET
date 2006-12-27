/*
 * Class: FixedClock
 * Package: edu.colorado.phet.graphicaldomain
 *
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:46:11 AM
 */
package edu.colorado.phet.common.view.components.clockgui;

import edu.colorado.phet.common.model.FixedClock;
import edu.colorado.phet.common.model.IClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockDialog extends JDialog {

    private IClock clock;
    private ClockControlPanel clockControlPanel;

    public ClockDialog( JFrame parentFrame, IClock clock ) {
        super( parentFrame, "FixedClock Settings", false );
        init( clock );
        this.pack();
        clock.addClockStateListener( clockControlPanel );
    }

    private void init( IClock clock ) {
        this.clock = clock;
        Container contentPane = this.getContentPane();
        contentPane.setLayout( new BorderLayout() );
        clockControlPanel = new ClockControlPanel( clock );
        contentPane.add( clockControlPanel, BorderLayout.CENTER );
        contentPane.add( buildButtonPanel(), BorderLayout.SOUTH );
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPnl = new JPanel( new FlowLayout() );
        JButton okBtn = new JButton( "OK" );
        okBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                updateClock();
                ClockDialog.this.hide();
            }
        } );
        buttonPnl.add( okBtn );

        JButton cancelBtn = new JButton( "Cancel" );
        cancelBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                ClockDialog.this.hide();
            }
        } );
        buttonPnl.add( cancelBtn );
        return buttonPnl;
    }

    private void updateClock() {
        clock.setRequestedDT( clockControlPanel.getDt() );
        clock.setRequestedWaitTime( clockControlPanel.getSleepInterval() );
        this.hide();
    }

    public void setClock( FixedClock c ) {
        this.clock = c;
        clockControlPanel.setClock( c );
    }

//    public void waitTimeChanged(int waitTime) {
//        clockControlPanel.
//    }
//
//    public void dtChanged(double dt) {
//    }
//
//    public void threadPriorityChanged(ThreadPriority tp) {
//    }
}

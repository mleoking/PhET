/*
 * Class: FixedClock
 * Package: edu.colorado.phet.graphicaldomain
 *
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:46:11 AM
 */
package edu.colorado.phet.common.view.components.clockgui;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ThreadedClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockDialog extends JDialog {

    private AbstractClock clock;
    private ClockControlPanel clockControlPanel;

    public ClockDialog( JFrame parentFrame, ThreadedClock clock ) {
        super( parentFrame, "FixedClock Settings", false );
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
        clock.setDt( clockControlPanel.getDt() );
        clock.setDelay( clockControlPanel.getSleepInterval() );
//        clock.setRequestedDT(clockControlPanel.getDt());
//        clock.setRequestedWaitTime(clockControlPanel.getSleepInterval());
        this.hide();
    }

    public void setClock( AbstractClock c ) {
        this.clock = c;
        clockControlPanel.setClock( c );
    }

//    public void delayChanged(int waitTime) {
//        clockControlPanel.
//    }
//
//    public void dtChanged(double dt) {
//    }
//
//    public void threadPriorityChanged(ThreadPriority tp) {
//    }
}

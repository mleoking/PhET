/*
 * Class: FixedClock
 * Package: edu.colorado.phet.graphicaldomain
 *
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:46:11 AM
 */
package edu.colorado.phet.common_cck.view.components.clockgui;

import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ThreadedClock;
import edu.colorado.phet.common_cck.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

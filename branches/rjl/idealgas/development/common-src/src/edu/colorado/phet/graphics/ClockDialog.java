/*
 * Class: Clock
 * Package: edu.colorado.phet.graphicaldomain
 *
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:46:11 AM
 */
package edu.colorado.phet.graphics;

import edu.colorado.phet.physics.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockDialog extends JDialog {

    private Clock clock;
    private ClockControlPanel clockControlPanel;

    public ClockDialog( JFrame parentFrame, Clock clock ) {
        super( parentFrame, "Clock Settings", true );
        init( clock );
        this.pack();
    }

    private void init( Clock clock ) {
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
                setClock();
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

    private void setClock() {
        clock.setDt( clockControlPanel.getDt() );
        clock.setWaitTime( clockControlPanel.getSleepInterval() );
        this.hide();
    }
}

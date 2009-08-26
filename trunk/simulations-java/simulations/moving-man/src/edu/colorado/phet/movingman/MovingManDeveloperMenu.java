package edu.colorado.phet.movingman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

public class MovingManDeveloperMenu extends JMenu {
    public MovingManDeveloperMenu() {
        super( "Developer" );
        JMenuItem menuItem = new JMenuItem( "Edit Model Parameters..." );
        menuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final JSpinner spinner = new JSpinner( new SpinnerNumberModel( UpdateStrategy.VelocityDriven.velWindow, 1, 20, 1 ) );
                spinner.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        UpdateStrategy.VelocityDriven.velWindow = (Integer) spinner.getValue();
                        System.out.println( "Changed window to: " + UpdateStrategy.VelocityDriven.velWindow );
                    }
                } );
                final JPanel spinnerControl = new JPanel();
                spinnerControl.add( new JLabel( "Velocity smoothing window" ) );
                spinnerControl.add( spinner );
                VerticalLayoutPanel controls = new VerticalLayoutPanel();
                controls.add( spinnerControl );
                JDialog dialog = new JDialog();
                dialog.setContentPane( controls );
                dialog.pack();
                dialog.setVisible( true );
            }
        } );
        add( menuItem );
    }
}

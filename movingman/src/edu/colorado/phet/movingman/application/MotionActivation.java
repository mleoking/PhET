package edu.colorado.phet.movingman.application;


import edu.colorado.phet.movingman.application.motionandcontrols.MotionAndControls;
import edu.colorado.phet.movingman.application.motionandcontrols.OscillateAndPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:33:04 PM
 * To change this template use Options | File Templates.
 */
public class MotionActivation {
    MovingManModule module;
    JDialog dialog;
    static ArrayList dialogs = new ArrayList();
    private GridBagConstraints gridBagConstraints;

    public MotionActivation( MovingManModule module ) {
        this.module = module;
    }

    public void setupInDialog( MotionAndControls mac, final MovingManControlPanel controls ) {
        if( dialog != null ) {
            dialog.setVisible( false );
            dialog.dispose();
            dialog = null;
        }
        dialog = new JDialog( (Frame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() ), "Controls", false );
        dialogs.add( dialog );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                if( module.isMotionMode() ) {
                    module.setPauseMode();
                }
                controls.resetComboBox();
            }
        } );
        controls.getInitialPositionSpinner().setValue( new Double( module.getMan().getX() ) );

        OscillateAndPanel.VerticalLayoutPanel panel = new OscillateAndPanel.VerticalLayoutPanel();
        gridBagConstraints = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        if( !( mac instanceof OscillateAndPanel ) ) {
            panel.add( controls.getInitialPositionSpinner() );
        }
        panel.add( mac.getControlPanel() );

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
        buttonPanel.add( controls.getStartMotionButton() );
        buttonPanel.add( controls.getAnotherPauseButton() );

//        JButton minimizeButton=new JButton("Minimize");
//        buttonPanel.add(minimizeButton);
//        minimizeButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                MotionActivation.this.dialog.setVisible(false);
//            }
//        });
//        buttonPanel.add(minimizeButton);

        panel.add( buttonPanel );
        dialog.setContentPane( panel );
        dialog.setTitle( mac.getName() );
        dialog.setBackground( Color.yellow );
        dialog.pack();

        JFrame f = (JFrame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
        centerInParent( dialog, f );
        moveRight( dialog );
        dialog.setVisible( true );
        MovingManControlPanel mainPanel = module.getMovingManControlPanel();
        mainPanel.getStartMotionButton().setEnabled( true );
        controls.getAnotherPauseButton().setEnabled( false );
        module.setMotionMode( mac );//.getStepMotion());
        mac.initialize( module.getMan() );
        module.setPauseMode();
        SwingUtilities.updateComponentTreeUI( dialog );
    }

    private void moveRight( JDialog dialog ) {
        int width = dialog.getWidth();
        Window ancestor = SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
        int x = ancestor.getWidth() + ancestor.getX() - width;
        dialog.setLocation( x, dialog.getY() );
    }

    public static void centerInParent( JDialog dialog, JFrame parent ) {
        Rectangle frameBounds = parent.getBounds();
        Rectangle dialogBounds = new Rectangle( (int)( frameBounds.getMinX() + frameBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int)( frameBounds.getMinY() + frameBounds.getHeight() / 2 - dialog.getHeight() / 2 ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    public void clearDialogs() {
        for( int i = 0; i < dialogs.size(); i++ ) {
            JDialog jDialog = (JDialog)dialogs.get( i );
            jDialog.setVisible( false );
        }
    }
}

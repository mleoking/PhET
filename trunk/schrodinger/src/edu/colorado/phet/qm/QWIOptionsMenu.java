/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.qm.controls.PropagatorPanel;
import edu.colorado.phet.qm.controls.ResolutionControl;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.phetcommon.UIController;
import edu.colorado.phet.qm.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 11:33:39 AM
 * Copyright (c) Jul 27, 2005 by Sam Reid
 */

public class QWIOptionsMenu extends JMenu {
    private QWIModule qwiModule;
    private JDialog dialog;
    private JDialog propagatorControlFrame;
    private boolean debug = false;

    public QWIOptionsMenu( final QWIModule qwiModule ) {
        super( "Options" );
        setMnemonic( 'o' );
        this.qwiModule = qwiModule;
//        JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem();

        final JCheckBoxMenuItem x = new JCheckBoxMenuItem( "Show Expectation Value <X>" );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayXExpectation( x.isSelected() );
            }
        } );
        add( x );

        final JCheckBoxMenuItem y = new JCheckBoxMenuItem( "Show Expectation Value <Y>" );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayYExpectation( y.isSelected() );
            }
        } );
        add( y );

        JMenuItem item = new JMenuItem( "Resolution" );
        final ResolutionControl resolutionControl = new ResolutionControl( qwiModule );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( dialog == null ) {
                    dialog = new JDialog( qwiModule.getPhetFrame() );

                    dialog.setContentPane( resolutionControl.getControls() );
                    dialog.pack();
                    qwiModule.addListener( new QWIModule.Listener() {
                        public void deactivated() {
                            qwiModule.removeListener( this );
                            dialog.setVisible( false );
                            dialog.dispose();
                        }

                        public void activated() {
                        }

                        public void beamTypeChanged() {
                        }
                    } );
                }
                dialog.show();
            }
        } );
        add( item );

        if( debug ) {
            JMenuItem printModelParameters = new JMenuItem( "Print Model Parameter" );
            printModelParameters.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    printModelParameters();
                }
            } );
            add( printModelParameters );
        }

        addSeparator();
        JMenuItem propagators = new JMenuItem( "Propagators" );
        propagators.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( propagatorControlFrame == null ) {
                    propagatorControlFrame = new JDialog( qwiModule.getPhetFrame(), "Propagators" );

                    propagatorControlFrame.setContentPane( new PropagatorPanel( getDiscreteModel() ) );
                    propagatorControlFrame.pack();
                    SwingUtils.centerDialogInParent( propagatorControlFrame );
                }
                propagatorControlFrame.setVisible( true );
            }

        } );
//        add( propagators );

        JMenuItem ui = new JMenuItem( "UI" );
        ui.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                UIController.showUIController();
            }
        } );
        add( ui );
    }

    private QWIModel getDiscreteModel() {
        return qwiModule.getQWIModel();
    }

    private void printModelParameters() {
        Map modelParams = qwiModule.getModelParameters();
        String text = toText( modelParams );
        JOptionPane.showMessageDialog( this, text );
    }

    private String toText( Map modelParams ) {
        Iterator iterator = modelParams.keySet().iterator();
        String text = new String();
        while( iterator.hasNext() ) {
            Object key = (Object)iterator.next();
            Object value = modelParams.get( key );
            text += key + " = " + value;
            if( iterator.hasNext() ) {
                text += System.getProperty( "line.separator" );
            }
        }
        return text;
    }

    private QWIPanel getSchrodingerPanel() {
        return qwiModule.getSchrodingerPanel();
    }
}

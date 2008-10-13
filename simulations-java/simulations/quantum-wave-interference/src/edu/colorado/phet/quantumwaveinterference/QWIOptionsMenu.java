/*  */
package edu.colorado.phet.quantumwaveinterference;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.quantumwaveinterference.controls.PropagatorPanel;
import edu.colorado.phet.quantumwaveinterference.controls.ResolutionControl;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.phetcommon.UIController;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 11:33:39 AM
 */

public class QWIOptionsMenu extends JMenu {
    private QWIModule qwiModule;
    private JDialog dialog;
    private JDialog propagatorControlFrame;
    private boolean debug = false;
    private JCheckBoxMenuItem expectationValueXItem;
    private JCheckBoxMenuItem expectationValueYItem;

    public QWIOptionsMenu( final QWIModule qwiModule ) {
        super( QWIResources.getString( "menus.options" ) );
        setMnemonic( 'o' );
        this.qwiModule = qwiModule;
//        JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem();

        expectationValueXItem = new JCheckBoxMenuItem( QWIResources.getString( "menus.show-expectation-x" ) );
        expectationValueXItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayXExpectation( expectationValueXItem.isSelected() );
            }
        } );
        add( expectationValueXItem );

        expectationValueYItem = new JCheckBoxMenuItem( QWIResources.getString( "menus.show-expectation-y" ) );
        expectationValueYItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayYExpectation( expectationValueYItem.isSelected() );
            }
        } );
        add( expectationValueYItem );

        JMenuItem item = new JMenuItem( QWIResources.getString( "menus.resolution" ) );
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
            JMenuItem printModelParameters = new JMenuItem( QWIResources.getString( "print.model.parameter" ) );
            printModelParameters.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    printModelParameters();
                }
            } );
            add( printModelParameters );
        }

        addSeparator();
        JMenuItem propagators = new JMenuItem( QWIResources.getString( "propagators" ) );
        propagators.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( propagatorControlFrame == null ) {
                    propagatorControlFrame = new JDialog( qwiModule.getPhetFrame(), QWIResources.getString( "propagators" ) );

                    propagatorControlFrame.setContentPane( new PropagatorPanel( getDiscreteModel() ) );
                    propagatorControlFrame.pack();
                    SwingUtils.centerDialogInParent( propagatorControlFrame );
                }
                propagatorControlFrame.setVisible( true );
            }

        } );
//        add( propagators );

        JMenuItem ui = new JMenuItem( QWIResources.getString( "ui" ) );
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

    public void removeExpectationValueItems() {
        remove( expectationValueXItem );
        remove( expectationValueYItem );
    }

    private QWIPanel getSchrodingerPanel() {
        return qwiModule.getSchrodingerPanel();
    }
}

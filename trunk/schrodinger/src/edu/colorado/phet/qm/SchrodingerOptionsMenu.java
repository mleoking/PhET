/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.controls.ResolutionControl;
import edu.colorado.phet.qm.view.SchrodingerPanel;

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

public class SchrodingerOptionsMenu extends JMenu {
    private SchrodingerModule schrodingerModule;
    private JDialog dialog;

    public SchrodingerOptionsMenu( final SchrodingerModule schrodingerModule ) {
        super( "Options" );
        setMnemonic( 'o' );
        this.schrodingerModule = schrodingerModule;
//        JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem();

        final JCheckBoxMenuItem x = new JCheckBoxMenuItem( "Show Observable <X>" );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayXExpectation( x.isSelected() );
            }
        } );
        add( x );

        final JCheckBoxMenuItem y = new JCheckBoxMenuItem( "Show Observable <Y>" );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayYExpectation( y.isSelected() );
            }
        } );
        add( y );

        JMenuItem item = new JMenuItem( "Resolution" );
        final ResolutionControl resolutionControl = new ResolutionControl( schrodingerModule.getSchrodingerControlPanel() );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( dialog == null ) {
                    dialog = new JDialog( schrodingerModule.getPhetFrame() );

                    dialog.setContentPane( resolutionControl.getControls() );
                    dialog.pack();
                }
                dialog.show();
            }
        } );
        add( item );

        JMenuItem printModelParameters = new JMenuItem( "Print Model Parameter" );
        printModelParameters.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                printModelParameters();
            }

        } );
        add( printModelParameters );
    }

    private void printModelParameters() {
        Map modelParams = schrodingerModule.getModelParameters();
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

    private SchrodingerPanel getSchrodingerPanel() {
        return schrodingerModule.getSchrodingerPanel();
    }
}

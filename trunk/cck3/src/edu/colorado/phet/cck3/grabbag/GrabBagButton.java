/** Sam Reid*/
package edu.colorado.phet.cck3.grabbag;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.common_cck.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common_cck.view.util.BufferedImageUtils;
import edu.colorado.phet.common_cck.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:17:40 PM
 * Copyright (c) Sep 14, 2004 by Sam Reid
 */
public class GrabBagButton extends JButton {
    private GrabBag bag;
    private CCKModule module;
    private JFrame dialog;

    public GrabBagButton( CCKModule module ) {
        super( SimStrings.get( "GrabBagButton.ButtonTitle" ) );
        this.module = module;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showGrabBag();
            }
        } );
        bag = new GrabBag();
        dialog = new JFrame( SimStrings.get( "GrabBagButton.DialogTitle" ) );
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        contentPane.setAnchor( GridBagConstraints.CENTER );
        contentPane.setFill( GridBagConstraints.NONE );

        JLabel click = new JLabel( SimStrings.get( "GrabBagButton.Help" ) );
        contentPane.add( click );
        JLabel empty = new JLabel( " " );
        contentPane.add( empty );

        dialog.setContentPane( contentPane );
        for( int i = 0; i < bag.numItems(); i++ ) {
            final GrabBagItem it = bag.itemAt( i );
            BufferedImage image = it.getImage();
            BufferedImage fixedSize = BufferedImageUtils.rescaleYMaintainAspectRatio( module.getApparatusPanel(), image, 40 );
//            JButton jb = new JButton( it.getName(), new ImageIcon( fixedSize ) );
            ImageIcon icon = new ImageIcon( fixedSize );
            JButton jb = new JButton( it.getName() );
            JPanel panel = new JPanel();
            panel.add( new JLabel( icon ) );
            panel.add( jb );
            jb.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    showItem( it );
                }
            } );
            SwingUtilities.updateComponentTreeUI( jb );
            contentPane.add( panel );
        }
        dialog.pack();

        SwingUtilities.updateComponentTreeUI( dialog );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.pack();
    }

    private void showItem( GrabBagItem it ) {
        dialog.setVisible( false );
        Resistor b = it.createBranch( module );
        module.getCircuit().addBranch( b );
        module.getCircuitGraphic().addGraphic( b, BufferedImageUtils.flipY( it.getImage() ) );

        module.layoutElectrons( new Branch[]{b} );
        module.getApparatusPanel().repaint();
    }

    private void showGrabBag() {
        dialog.setVisible( true );
    }
}

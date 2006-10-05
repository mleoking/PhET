/** Sam Reid*/
package edu.colorado.phet.cck.grabbag;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Resistor;
import edu.colorado.phet.cck.phetgraphics_cck.HasCircuitGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common_cck.view.util.BufferedImageUtils;
import edu.colorado.phet.common_cck.view.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:17:40 PM
 * Copyright (c) Sep 14, 2004 by Sam Reid
 */
public class GrabBagButton extends JButton {
    private GrabBag bag;
    private ICCKModule module;
    private JFrame dialog;

    public GrabBagButton( ICCKModule module ) {
        super( SimStrings.get( "GrabBagButton.ButtonTitle" ) );
        setOpaque( false );

        try {
            setIcon( new ImageIcon( ImageLoader.loadBufferedImage( "images/bag.gif" ) ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
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
            BufferedImage fixedSize = BufferedImageUtils.rescaleYMaintainAspectRatio( module.getSimulationPanel(), image, 40 );
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
        if( module instanceof HasCircuitGraphic ) {
            ( (HasCircuitGraphic)module ).getCircuitGraphic().addGraphic( b, BufferedImageUtils.flipY( it.getImage() ) );
        }


        module.layoutElectrons( new Branch[]{b} );
        module.getSimulationPanel().repaint();
    }

    private void showGrabBag() {
        dialog.setVisible( true );
    }
}

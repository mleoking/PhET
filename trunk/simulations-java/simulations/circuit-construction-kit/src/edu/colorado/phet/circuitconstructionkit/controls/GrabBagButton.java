package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;
import edu.colorado.phet.circuitconstructionkit.model.grabbag.GrabBag;
import edu.colorado.phet.circuitconstructionkit.model.grabbag.GrabBagItem;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:17:40 PM
 */
public class GrabBagButton extends JButton {
    private GrabBag bag;
    private CCKModule module;
    private JFrame dialog;

    public GrabBagButton( CCKModule module ) {
        super( CCKResources.getString( "GrabBagButton.ButtonTitle" ) );
//        setOpaque( false );

        try {
            setIcon( new ImageIcon( BufferedImageUtils.rescaleYMaintainAspectRatio( ImageLoader.loadBufferedImage( "circuit-construction-kit/images/bag.gif" ), 45 ) ) );
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
        dialog = new JFrame( CCKResources.getString( "GrabBagButton.DialogTitle" ) );
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        contentPane.setAnchor( GridBagConstraints.CENTER );
        contentPane.setFill( GridBagConstraints.NONE );

        JLabel click = new JLabel( CCKResources.getString( "GrabBagButton.Help" ) );
        contentPane.add( click );
        JLabel empty = new JLabel( " " );
        contentPane.add( empty );

        dialog.setContentPane( contentPane );
        for ( int i = 0; i < bag.numItems(); i++ ) {
            final GrabBagItem it = bag.itemAt( i );
            BufferedImage image = it.getImage();
            BufferedImage fixedSize = BufferedImageUtils.rescaleYMaintainAspectRatio( image, 40 );
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
//        if( module instanceof HasCircuitGraphic ) {
//            ( (HasCircuitGraphic)module ).getCircuitGraphic().addGraphic( b, BufferedImageUtils.flipY( it.getImage() ) );
//        }


        module.layoutElectrons( new Branch[]{b} );
        module.getSimulationPanel().repaint();
    }

    private void showGrabBag() {
        dialog.setVisible( true );
    }
}

/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.test;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.*;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;


public class TestPhotonNode extends JFrame {

    public static void main( String[] args ) {
        JFrame frame = new TestPhotonNode( "Photon test");
        frame.show();
    }
    
    public TestPhotonNode( String title ) {
        super( title );

        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.add( createPanel( Color.BLACK ) );
        panel.add( createPanel( Color.WHITE ) );
//        panel.add( createPanel( Color.LIGHT_GRAY ) );
//        panel.add( createPanel( new Color( 255, 102, 0 ) ) ); // pumpkin orange
        
        getContentPane().add( panel );
        pack();
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    private static JPanel createPanel( Color background ) {
        JPanel panel = new JPanel();
        panel.setBackground( background );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 15, 15, 15, 15 ) ); // top,left,bottom,right
        panel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( createLabel( 300 ), row, col++ );
        layout.addComponent( createLabel( 400 ), row, col++ );
        layout.addComponent( createLabel( 440 ), row, col++ );
        layout.addComponent( createLabel( 475 ), row, col++ );
        layout.addComponent( createLabel( 525 ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( createLabel( 575 ), row, col++ );
        layout.addComponent( createLabel( 610 ), row, col++ );
        layout.addComponent( createLabel( 660 ), row, col++ );
        layout.addComponent( createLabel( 750 ), row, col++ );
        layout.addComponent( createLabel( 800 ), row, col++ );
        return panel;
    }
    
    private static JLabel createLabel( double wavelength ) {
        Image image = PhotonNode.createPhotonImage( wavelength );
        Icon icon = new ImageIcon( image );
        JLabel label = new JLabel( icon );
        label.setOpaque( false );
        return label;
    }
}

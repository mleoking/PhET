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

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.hydrogenatom.view.PhotonNode;
import edu.umd.cs.piccolo.PNode;


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
        panel.add( createPanel( Color.LIGHT_GRAY ) );
        panel.add( createPanel( new Color( 255, 102, 0 ) ) ); // pumpkin orange
        
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
        layout.addComponent( createLabel( Color.RED ), row, col++ );
        layout.addComponent( createLabel( Color.GREEN ), row, col++ );
        layout.addComponent( createLabel( Color.BLUE ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( createLabel( Color.MAGENTA ), row, col++ );
        layout.addComponent( createLabel( Color.YELLOW ), row, col++ );
        layout.addComponent( createLabel( Color.CYAN ), row, col++ );
        return panel;
    }
    
    private static JLabel createLabel( Color photonColor ) {
        PNode photonNode = new PhotonNode( photonColor );
        Image image = photonNode.toImage();
        Icon icon = new ImageIcon( image );
        JLabel label = new JLabel( icon );
        label.setOpaque( false );
        return label;
    }
}

package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.fitness.FitnessResources;

/**
 * Created by: Sam
 * Apr 23, 2008 at 10:35:43 AM
 */
public class DietControlPanel extends VerticalLayoutPanel {
    public DietControlPanel() {
        JPanel baseDietPanel = new JPanel();
        baseDietPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        baseDietPanel.add( new JLabel( "Base Diet: " ) );
        baseDietPanel.add( new JComboBox( new Object[]{"Balanced Diet"} ) );
        add( baseDietPanel );

        add( new JLabel( "Additionally:" ) );

        JButton button = new JButton( "Add Food", createIcon( "+", Color.blue, new PhetDefaultFont( 20, true ) ) );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
            }
        } );
        setFillNone();
        add( button );
        setFillHorizontal();

        DietComponent ban = new DietComponent( "banana split", "bananasplit.png", 100 );
        DietComponent burger = new DietComponent( "burger", "burger.png", 100 );
        DietComponent grapefruit = new DietComponent( "grapefruit", "grapefruit.png", 100 );
        DietComponent strawberry = new DietComponent( "strawberry", "strawberry.png", 100 );
        add( ban );
        add( burger );
        add( grapefruit );
        add( strawberry );
    }

    private Icon createIcon( String s, Color color, PhetDefaultFont font ) {
        ShadowPText shadowPText = new ShadowPText( s, color, font );
        return new ImageIcon( shadowPText.toImage() );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new DietControlPanel() );
        frame.pack();
        frame.setVisible( true );
    }

    private class DietComponent extends JPanel {
        public DietComponent( String name, String image, double cal ) {
            add( new JLabel( new ImageIcon( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( image ), 30) ) ) );
            add( new JLabel( "<html>One " + name + " per day<br>(" + cal + " kcal/day)</html>" ) );
            add( new JButton( createIcon( "-", Color.red, new PhetDefaultFont( 20, true ) ) ) );
//            add( new JButton( "Remove" ) );
        }
    }
}

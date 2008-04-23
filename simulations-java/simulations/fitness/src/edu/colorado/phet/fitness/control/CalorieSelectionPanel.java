package edu.colorado.phet.fitness.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;

/**
 * Created by: Sam
 * Apr 23, 2008 at 10:35:43 AM
 */
public class CalorieSelectionPanel extends VerticalLayoutPanel {
    public CalorieSelectionPanel( CalorieSet available, CalorieSet selected ) {
        JPanel baseDietPanel = new JPanel();
        baseDietPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        baseDietPanel.add( new JLabel( "Base Diet: " ) );
        baseDietPanel.add( new JComboBox( new Object[]{"Balanced Diet"} ) );
        add( baseDietPanel );

        add( new JLabel( "Additionally:" ) );

//        JButton button = new JButton( "Add Food", createIcon( "+", Color.blue, new PhetDefaultFont( 20, true ) ) );
//        button.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//            }
//        } );
//        setFillNone();
//        add( button );
//        setFillHorizontal();
        JSplitPane pane = new JSplitPane();
        for ( int i = 0; i < available.getItemCount(); i++ ) {
            DietComponent ban = new DietComponent( available.getItem( i ) );
            add( ban );
        }
    }

    private Icon createIcon( String s, Color color, PhetDefaultFont font ) {
        ShadowPText shadowPText = new ShadowPText( s, color, font );
        return new ImageIcon( shadowPText.toImage() );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new CalorieSelectionPanel( FitnessModel.availableFoods, FitnessModel.availableFoods ) );
        frame.pack();
        frame.setVisible( true );
    }

    private class DietComponent extends JPanel {
        public DietComponent( String name, String image, double cal ) {
            add( new JLabel( new ImageIcon( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( image ), 30 ) ) ) );
            add( new JLabel( "<html>One " + name + " per day<br>(" + cal + " kcal/day)</html>" ) );
            add( new JButton( createIcon( "-", Color.red, new PhetDefaultFont( 20, true ) ) ) );
//            add( new JButton( "Remove" ) );
        }

        public DietComponent( CaloricItem item ) {
            this( item.getName(), item.getImage(), item.getCalories() );
        }
    }
}

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
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;

/**
 * Created by: Sam
 * Apr 23, 2008 at 10:35:43 AM
 */
public class CalorieSelectionPanel extends VerticalLayoutPanel {
    public CalorieSelectionPanel( final CalorieSet available, final CalorieSet selected ) {
        JPanel baseDietPanel = new JPanel();
        baseDietPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
        baseDietPanel.add( new JLabel( "Base Diet: " ) );
        baseDietPanel.add( new JComboBox( new Object[]{"Balanced Diet"} ) );
        add( baseDietPanel );

        add( new JLabel( "In addition to your Balanced Diet, you can select additional items:" ) );

        JPanel leftPanel = new VerticalLayoutPanel();
        for ( int i = 0; i < available.getItemCount(); i++ ) {
            DietComponent ban = new DietComponent( available.getItem( i ) );
            JButton button = new JButton( "Add to Diet" );
            ban.add( button );
            final int i1 = i;
            button.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    System.out.println( "CalorieSelectionPanel.actionPerformed" );
                    selected.addItem( available.getItem( i1 ) );
                }
            } );
            leftPanel.add( ban );
        }

        final JPanel rightPanel = new VerticalLayoutPanel();
        for ( int i = 0; i < selected.getItemCount(); i++ ) {
            rightPanel.add( new SelectedComponent( selected.getItem( i ) ) );
        }

//        JPanel lp = new VerticalLayoutPanel();
//        lp.add( new JLabel( "Available" ) );
//        lp.add(new JScrollPane(leftPanel));

        JScrollPane jScrollPane = new JScrollPane( leftPanel );
        jScrollPane.setBorder( BorderFactory.createTitledBorder( "Available Foods" ) );
        JScrollPane jScrollPane1 = new JScrollPane( rightPanel );
        jScrollPane1.setBorder( BorderFactory.createTitledBorder( "Selected" ) );
        final JSplitPane pane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, jScrollPane, jScrollPane1 );
        pane.setDividerLocation( 0.5 );

        add( pane );
        pane.setPreferredSize( new Dimension( 300, 300 ) );
        selected.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                rightPanel.add( new SelectedComponent( item ) );
                pane.setDividerLocation( 0.5 );
                pane.invalidate();
                pane.revalidate();
            }
        } );
    }

    private Icon createIcon( String s, Color color, PhetDefaultFont font ) {
        ShadowPText shadowPText = new ShadowPText( s, color, font );
        return new ImageIcon( shadowPText.toImage() );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new CalorieSelectionPanel( FitnessModel.availableFoods, new CalorieSet() ) );
        frame.pack();
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

    private class DietComponent extends JPanel {
        public DietComponent( String name, String image, double cal ) {
            add( new JLabel( new ImageIcon( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( image ), 30 ) ) ) );
            add( new JLabel( "<html>One " + name + " per day<br>(" + cal + " kcal/day)</html>" ) );
//            add( new JButton( createIcon( "-", Color.red, new PhetDefaultFont( 20, true ) ) ) );
//            add( new JButton( "Remove" ) );
        }

        public DietComponent( CaloricItem item ) {
            this( item.getName(), item.getImage(), item.getCalories() );
        }
    }

    private class SelectedComponent extends DietComponent {
        public SelectedComponent( CaloricItem item ) {
            super( item );
            add( new JButton( "Remove from Diet" ) );
//            DietComponent ban = new DietComponent( selected.getItem( i ) );
//            rightPanel.add( ban );
        }
    }
}

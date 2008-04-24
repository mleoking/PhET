package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

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

        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.Y_AXIS ) );
        for ( int i = 0; i < selected.getItemCount(); i++ ) {
            rightPanel.add( new SelectedComponent( selected, selected.getItem( i ) ) );
        }

        JScrollPane jScrollPane = new JScrollPane( leftPanel );
        jScrollPane.setBorder( createTitledBorder( "Available Foods" ) );
        JScrollPane jScrollPane1 = new JScrollPane( rightPanel );
        jScrollPane1.setBorder( createTitledBorder( "Selected Foods" ) );
        final JSplitPane pane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, jScrollPane, jScrollPane1 );
        pane.setDividerLocation( 0.5 );

        add( pane );
        pane.setPreferredSize( new Dimension( 300, 300 ) );
        selected.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                rightPanel.add( Box.createRigidArea( new Dimension( 2, 2 ) ) );
                rightPanel.add( new SelectedComponent( selected, item ) );
                pane.setDividerLocation( 0.5 );
                pane.invalidate();
                pane.revalidate();
            }

            public void itemRemoved( CaloricItem item ) {
                for ( int i = 0; i < rightPanel.getComponentCount(); i++ ) {
                    Component c = rightPanel.getComponent( i );
                    if ( c instanceof SelectedComponent ) {
                        SelectedComponent sc = (SelectedComponent) c;
                        if ( sc.item == item ) {
                            rightPanel.remove( sc );
                            i--;
                            rightPanel.invalidate();
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }
                    }

                }
            }
        } );
        JButton button = new JButton( "Done" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyDonePressed();
            }
        } );
        setFillNone();
        add( button );
    }

    public static interface Listener {
        void donePressed();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDonePressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).donePressed();
        }
    }

    private TitledBorder createTitledBorder( String title ) {
        return new TitledBorder( new BevelBorder( BevelBorder.LOWERED ), title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new PhetDefaultFont( 20, true ) ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        };
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
        private CalorieSet set;
        private CaloricItem item;

        public SelectedComponent( final CalorieSet set, final CaloricItem item ) {
            super( item );
            this.set = set;
            this.item = item;
            JButton button = new JButton( "Remove from Diet" );
            button.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    set.removeItem( item );
                }
            } );
            add( button );
//            DietComponent ban = new DietComponent( selected.getItem( i ) );
//            rightPanel.add( ban );
        }
    }
}

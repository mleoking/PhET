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
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.module.fitness.CaloricFoodItem;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.colorado.phet.fitness.view.FitnessColorScheme;

/**
 * Created by: Sam
 * Apr 23, 2008 at 10:35:43 AM
 */
public class CalorieSelectionPanel extends JPanel {

    public CalorieSelectionPanel( final CalorieSet available, final CalorieSet selected ) {
        setLayout( new GridBagLayout() );
        JPanel leftPanel = new VerticalLayoutPanel();
        for ( int i = 0; i < available.getItemCount(); i++ ) {
            DietComponent ban = new DietComponent( available.getItem( i ) );
            JButton button = new JButton( "Add" );
            ban.add( button );
            final int i1 = i;
            button.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selected.addItem( available.getItem( i1 ) );
                }
            } );
            leftPanel.add( ban );
        }
        leftPanel.add( Box.createVerticalStrut( 10 ), new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 10, 10, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

        final JPanel rightPanel = new VerticalLayoutPanel();
        final Component rightStrut = Box.createVerticalBox();
        rightPanel.add( rightStrut, new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1E6, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
        for ( int i = 0; i < selected.getItemCount(); i++ ) {
            rightPanel.remove( rightStrut );//todo: fix this awkward workaround
            rightPanel.add( new SelectedComponent( selected, selected.getItem( i ) ) );
            rightPanel.add( rightStrut, new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1E6, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
        }

        JScrollPane leftScrollPane = new JScrollPane( leftPanel );
        leftScrollPane.setBorder( createTitledBorder( "Grocery Store" ) );

        JScrollPane rightScrollPane = new JScrollPane( rightPanel );
        rightScrollPane.setBorder( createTitledBorder( "Diet" ) );

        final JSplitPane pane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, rightScrollPane );
        pane.setDividerLocation( 0.5 );

        add( pane, new GridBagConstraints( 0, 0, 1, 1, 1E6, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
        pane.setPreferredSize( new Dimension( 300, 300 ) );
        selected.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                rightPanel.remove( rightStrut );
                rightPanel.add( new SelectedComponent( selected, item ) );
                rightPanel.add( rightStrut, new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1E6, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
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
                            rightPanel.invalidate();
                            rightPanel.revalidate();
                            rightPanel.repaint();
                            break;//remove the first matching item
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
        add( button, new GridBagConstraints( 0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
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
        private DietComponent( String name, String image, double cal ) {
            add( new JLabel( new ImageIcon( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( image ), 50 ) ) ) );
            add( new JLabel( "<html>One " + name + " per day<br>(" + cal + " kcal/day)</html>" ) );
//            add( new JButton( createIcon( "-", Color.red, new PhetDefaultFont( 20, true ) ) ) );
//            add( new JButton( "Remove" ) );
        }

        public DietComponent( CaloricItem item ) {
            this( item.getName(), item.getImage(), item.getCalories() );
            showPieChart( item );
        }

        protected void showPieChart( CaloricItem item ) {
            if ( item instanceof CaloricFoodItem ) {
                CaloricFoodItem c = (CaloricFoodItem) item;
                PhetPCanvas canvas = new PhetPCanvas();
                canvas.setPreferredSize( new Dimension( 50, 50 ) );
                canvas.addScreenChild( new PieChartNode( new PieChartNode.PieValue[]{
                        new PieChartNode.PieValue( c.getCarbCalories(), FitnessColorScheme.CARBS ),
                        new PieChartNode.PieValue( c.getProteinCalories(), FitnessColorScheme.PROTEIN ),
                        new PieChartNode.PieValue( c.getLipidCalories(), FitnessColorScheme.FATS ),
                }, new Rectangle( 5, 5, 40, 40 ) ) );
                canvas.setOpaque( false );
                canvas.setBackground( new Color( 0, 0, 0, 0 ) );
                canvas.setBorder( null );
                add( canvas );
            }
        }
    }

    private class SelectedComponent extends DietComponent {
        private CalorieSet set;
        private CaloricItem item;

        public SelectedComponent( final CalorieSet set, final CaloricItem item ) {
            super( item );
            this.set = set;
            this.item = item;
            JButton button = new JButton( "Remove" );
            button.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    set.removeItem( item );
                }
            } );
            add( button );
        }

        protected void showPieChart( CaloricItem item ) {
            //no-op
        }
    }
}

package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.module.fitness.CaloricFoodItem;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.colorado.phet.fitness.view.FitnessColorScheme;

/**
 * Created by: Sam
 * Apr 23, 2008 at 10:35:43 AM
 */
public class CalorieSelectionPanel extends JPanel implements ICalorieSelectionPanel {
    private ArrayList listeners = new ArrayList();
    Random random = new Random();

    public CalorieSelectionPanel( final CalorieSet available, final CalorieSet selected, String availableTitle, String selectedTitle ) {
        setLayout( new GridBagLayout() );
        JComponent leftPanel = Box.createVerticalBox();

        for ( int i = 0; i < available.getItemCount(); i++ ) {
            DietComponent dietComponent = new DietComponent( available.getItem( i ), true );
            JButton addButton = new JButton( "Add" );
            dietComponent.add( addButton );
            final int i1 = i;
            addButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selected.addItem( available.getItem( i1 ) );
                }
            } );
            leftPanel.add( dietComponent );
//            leftPanel.add(new JTextField(random.nextInt( 10 )));
        }

        final JComponent rightPanel = Box.createVerticalBox();
        for ( int i = 0; i < selected.getItemCount(); i++ ) {
            rightPanel.add( new SelectedComponent( selected, selected.getItem( i ) ) );
        }

        JScrollPane leftScrollPane = new JScrollPane( leftPanel );
        leftScrollPane.setBorder( createTitledBorder( availableTitle ) );

        JScrollPane rightScrollPane = new JScrollPane( rightPanel );
        rightScrollPane.setBorder( createTitledBorder( selectedTitle ) );

        final JPanel pane = new JPanel( new GridLayout( 1, 2 ) );
        pane.add( leftScrollPane );
        pane.add( rightScrollPane );

        add( pane, new GridBagConstraints( 0, 1, 1, 1, 1E6, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
        selected.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                rightPanel.add( new SelectedComponent( selected, item ) );
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
        add( button, new GridBagConstraints( 0, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
    }

    public static interface Listener {
        void donePressed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDonePressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).donePressed();
        }
    }

    public static TitledBorder createTitledBorder( String title ) {
        return new TitledBorder( new BevelBorder( BevelBorder.LOWERED ), title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new PhetDefaultFont( 16, true ) ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        };
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new CalorieSelectionPanel( FitnessModel.availableFoods, new CalorieSet(), "Grocery Store", "Diet" ) );
        frame.pack();
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

    private class DietComponent extends JPanel {

        private DietComponent( String name, String image, double cal ) {
            setLayout( new BoxLayout( this,BoxLayout.X_AXIS ) );
            if ( image != null && image.trim().length() > 0 ) {
                add( new JLabel( new ImageIcon( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( image ), 50 ) ) ) );
            }
            JLabel jLabel = new JLabel( "<html>One " + name + " per day<br>(" + cal + " kcal/day)</html>" );
            jLabel.setFont( new PhetDefaultFont( 12 ) );
            add( jLabel );
        }

        public DietComponent( CaloricItem item, boolean showPieChart ) {
            this( item.getName(), item.getImage(), item.getCalories() );
            if ( showPieChart ) {
                showPieChart( item );
            }
        }

        protected void showPieChart( CaloricItem item ) {
            if ( item instanceof CaloricFoodItem ) {
                CaloricFoodItem c = (CaloricFoodItem) item;
                PhetPCanvas pieChartCanvas = new PhetPCanvas();
                pieChartCanvas.setPreferredSize( new Dimension( 50, 50 ) );
                pieChartCanvas.addScreenChild( new PieChartNode( new PieChartNode.PieValue[]{
                        new PieChartNode.PieValue( c.getCarbCalories(), FitnessColorScheme.CARBS ),
                        new PieChartNode.PieValue( c.getProteinCalories(), FitnessColorScheme.PROTEIN ),
                        new PieChartNode.PieValue( c.getLipidCalories(), FitnessColorScheme.FATS ),
                }, new Rectangle( 5, 5, 40, 40 ) ) );
                pieChartCanvas.setOpaque( false );
                pieChartCanvas.setBackground( new Color( 0, 0, 0, 0 ) );
                pieChartCanvas.setBorder( null );
                add( pieChartCanvas );
            }
        }
    }

    private class SelectedComponent extends DietComponent {
        private CalorieSet set;
        private CaloricItem item;

        public SelectedComponent( final CalorieSet set, final CaloricItem item ) {
            super( item, false );
            this.set = set;
            this.item = item;
            boolean removableFood = item instanceof CaloricFoodItem && ( (CaloricFoodItem) item ).isRemovable();
            if ( removableFood || !( item instanceof CaloricFoodItem ) ) {
                JButton button = new JButton( "Remove" );
                button.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        set.removeItem( item );
                    }
                } );
                add( button );
            }
        }
    }
}

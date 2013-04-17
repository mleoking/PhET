// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseClock;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModel;
import edu.colorado.phet.eatingandexercise.view.EatingAndExerciseColorScheme;

/**
 * Created by: Sam
 * Apr 23, 2008 at 10:35:43 AM
 */
public class CalorieSelectionPanel extends JPanel implements ICalorieSelectionPanel {
    private ArrayList listeners = new ArrayList();

    public CalorieSelectionPanel( final CalorieSet available, final CalorieSet selected, String availableTitle, String selectedTitle ) {
        setLayout( new GridBagLayout() );
        JComponent leftPanel = new MyVerticalLayoutPanel();

        for ( int i = 0; i < available.getItemCount(); i++ ) {
            DietComponent dietComponent = new DietComponent( available.getItem( i ), true );
            JButton addButton = new JButton( EatingAndExerciseResources.getString( "edit.add" ) );
            dietComponent.add( addButton );
            final int i1 = i;
            addButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selected.addItem( available.getItem( i1 ) );
                }
            } );
            leftPanel.add( dietComponent );
        }

        final JComponent rightPanel = new MyVerticalLayoutPanel();
        for ( int i = 0; i < selected.getItemCount(); i++ ) {
            rightPanel.add( new SelectedComponent( selected, selected.getItem( i ) ) );
        }

        JScrollPane leftScrollPane = new JScrollPane( leftPanel );
        int unitIncrement = 40;
        leftScrollPane.getVerticalScrollBar().setUnitIncrement( unitIncrement );
        leftScrollPane.setBorder( createTitledBorder( availableTitle ) );

        JScrollPane rightScrollPane = new JScrollPane( rightPanel );
        rightScrollPane.getVerticalScrollBar().setUnitIncrement( unitIncrement );
        rightScrollPane.setBorder( createTitledBorder( selectedTitle ) );

        final JPanel pane = new JPanel( new GridLayout( 1, 2 ) );
        pane.add( leftScrollPane );
        pane.add( rightScrollPane );

        add( pane, new GridBagConstraints( 0, 1, 1, 1, 1E6, 1E6, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 1, 1, 1, 1 ), 0, 0 ) );
        selected.addListener( new CalorieSet.Adapter() {
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
        JButton button = new JButton( EatingAndExerciseResources.getString( "edit.done" ) );
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
        return new TitledBorder( new BevelBorder( BevelBorder.LOWERED ), title, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new PhetFont( 16, true ) ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        };
    }

    private class DietComponent extends JPanel {
        int maxImageW = 60;

        private DietComponent( String image, String jlabelText ) {
            setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
            int imageW = 0;
            if ( image != null && image.trim().length() > 0 ) {
                BufferedImage bufferedImage = BufferedImageUtils.multiScaleToHeight( EatingAndExerciseResources.getImage( image ), 50 );
                imageW = bufferedImage.getWidth();
                add( new JLabel( new ImageIcon( bufferedImage ) ) );
            }
            if ( maxImageW - imageW > 0 ) {
                add( Box.createHorizontalStrut( maxImageW - imageW ) );
            }
            JLabel jLabel = new JLabel( jlabelText );
            jLabel.setFont( new PhetFont( 12 ) );
            add( jLabel );
        }

        public DietComponent( CaloricItem item, boolean showPieChart ) {
            this( item.getImage(), item.getLabelText() );
            if ( showPieChart && item instanceof CaloricFoodItem ) {
                showPieChart( (CaloricFoodItem) item );
            }
        }

        protected void showPieChart( CaloricFoodItem c ) {
            PhetPCanvas pieChartCanvas = new PhetPCanvas();
            pieChartCanvas.setPreferredSize( new Dimension( 50, 50 ) );
            pieChartCanvas.addScreenChild( new PieChartNode( new PieChartNode.PieValue[]{
                    new PieChartNode.PieValue( c.getCarbCalories(), EatingAndExerciseColorScheme.CARBS ),
                    new PieChartNode.PieValue( c.getProteinCalories(), EatingAndExerciseColorScheme.PROTEIN ),
                    new PieChartNode.PieValue( c.getLipidCalories(), EatingAndExerciseColorScheme.FATS ),
            }, new Rectangle( 5, 5, 40, 40 ) ) );
            pieChartCanvas.setOpaque( false );
            pieChartCanvas.setBackground( new Color( 0, 0, 0, 0 ) );
            pieChartCanvas.setBorder( null );
            add( pieChartCanvas );
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
                JButton button = new JButton( EatingAndExerciseResources.getString( "edit.remove" ) );
                button.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        set.removeItem( item );
                    }
                } );
                add( button );
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( new CalorieSelectionPanel( new EatingAndExerciseModel( new EatingAndExerciseClock() ).getAvailableExercise(), new CalorieSet(), "Grocery store", EatingAndExerciseResources.getString( "diet" ) ) );
        frame.pack();
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

}

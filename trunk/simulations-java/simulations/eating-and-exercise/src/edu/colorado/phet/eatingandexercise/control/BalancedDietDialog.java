package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.control.valuenode.LinearValueControlNode;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;

/**
 * Created by: Sam
 * Jun 26, 2008 at 8:55:58 AM
 */
public class BalancedDietDialog extends JDialog {
    public BalancedDietDialog( final CaloricFoodItem item ) {
        JPanel contentPane = new VerticalLayoutPanel();
//        contentPane.setLayout( new GridBagLayout() );
        JLabel label = new JLabel( item.getLabelText(), new ImageIcon( EatingAndExerciseResources.getImage( item.getImage() ) ), SwingConstants.CENTER ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        contentPane.add( label );
        //todo: this slider is limited to 5 because its scaling is done locally (based on previous value), so allowing it to go to zero causes failure
        //correct solution is to update value not based on previous value
        LinearValueControlNode linearValueControlNode = new LinearValueControlNode( EatingAndExerciseResources.getString( "units.calories" ), EatingAndExerciseResources.getString( "units.cal" ), 5, 4000, item.getCalories(), new DefaultDecimalFormat( "0.0" ) );
        linearValueControlNode.setTextFieldColumns( 6 );

        linearValueControlNode.addListener( new LinearValueControlNode.Listener() {
            public void valueChanged( double value ) {
                item.setTotalCalories( value );
//                System.out.println( "set calories: "+value+", item.getCalories() = " + item.getCalories() );
            }
        } );
        contentPane.add( new PNodeComponent( linearValueControlNode ) );

        contentPane.setOpaque( true );
        contentPane.setBackground( Color.white );
        SwingUtils.centerWindowOnScreen( this );
        setContentPane( contentPane );
        pack();
    }

    public static void main( String[] args ) {
        BalancedDietDialog dialog = new BalancedDietDialog( new CaloricFoodItem( "balanced diet", Human.FOOD_PYRAMID, 5, 5, 5 ) );
        dialog.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        dialog.setVisible( true );
    }
}

package edu.colorado.phet.fitness.module.fitness;

import java.text.DecimalFormat;

import edu.colorado.phet.fitness.model.Human;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Jun 6, 2008 at 1:39:30 PM
 */
public class BMIReadout extends PNode {
    private PText pText;
    private Human human;

    public BMIReadout( Human human ) {
        this.pText = new PText();
        this.human = human;
        addChild( pText );
        human.addListener( new Human.Adapter() {
            public void bmiChanged() {
                updateReadout();
            }
        } );
        updateReadout();
    }

    private void updateReadout() {
        pText.setText( "BMI: " + new DecimalFormat( "0.0" ).format( human.getBMI() ) );
    }
}

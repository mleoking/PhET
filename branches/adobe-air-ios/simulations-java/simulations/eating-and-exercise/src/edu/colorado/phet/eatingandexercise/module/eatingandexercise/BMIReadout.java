// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Jun 6, 2008 at 1:39:30 PM
 */
public class BMIReadout extends PNode {
    private PText pText;
    private Human human;
    private PhetPPath background;

    public BMIReadout( Human human ) {
        this.pText = new PText();
        pText.setFont( new PhetFont( 12, true ) );
        this.human = human;

        human.addListener( new Human.Adapter() {
            public void bmiChanged() {
                updateReadout();
            }
        } );

        background = new PhetPPath( new Color( 0.8f, 0.2f, 0.3f ) );
        addChild( background );
        addChild( pText );
        updateReadout();
    }

    private void updateReadout() {
        pText.setText( "BMI: " + new DecimalFormat( "0.0" ).format( human.getBMI() ) + " " + EatingAndExerciseResources.getString( "units.bmi" ) );
        Rectangle2D v = RectangleUtils.expand( pText.getFullBounds(), 2, 2 );
//        background.setPathTo( v );
        background.setPathTo( new RoundRectangle2D.Double( v.getX(), v.getY(), v.getWidth(), v.getHeight(), 12, 12 ) );
    }
}

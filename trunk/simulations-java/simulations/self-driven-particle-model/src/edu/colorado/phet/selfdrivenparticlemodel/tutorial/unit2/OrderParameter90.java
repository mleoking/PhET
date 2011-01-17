// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import java.awt.*;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1.FullFeatureBaseClass;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PText;

public class OrderParameter90 extends FullFeatureBaseClass {
    private PText orderParamText;
    private DecimalFormat decimalFormat;
    private PActivity activity;

    public OrderParameter90( BasicTutorialCanvas basicPage ) {
        super( basicPage );

        setText( "Some Dynamical Systems can be characterized by their degree of orderliness.  " +
                 "This quantity is termed the 'order parameter'.  " +
                 "In this model, the order parameter reflects the similarity of particles' headings.  " +
                 "Try to get the order parameter above 0.9" );
//        artificialAdvance();
        orderParamText = new PText();
        orderParamText.setTextPaint( Color.blue );
        orderParamText.setFont( new PhetFont( 16, true ) );
        decimalFormat = new DecimalFormat( "0.00" );
        activity = new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                updateText();
                if ( isOrderParamaterAwesome() ) {
                    advance();
                }
            }
        };
    }

    private void updateText() {
        orderParamText.setText( "Order Parameter = " + decimalFormat.format( getParticleModel().getOrderParameter() ) );
    }

    public PText getOrderParamText() {
        return orderParamText;
    }

    protected boolean isOrderParamaterAwesome() {
        return getParticleModel().getOrderParameter() > 0.9;
    }

    public void init() {
        super.init();
        getBasePage().createUniverse();
        getBasePage().setNumberParticles( 20 );
        getBasePage().getParticleModel().setRandomness( getInitRandomness() );
        updateText();
        orderParamText.setOffset( getRadiusControlGraphic().getFullBounds().getX(), getRadiusControlGraphic().getFullBounds().getMaxY() + super.getDy() );
        orderParamText.recomputeLayout();
        addChild( orderParamText );
        getBasePage().getRoot().addActivity( activity );
        startModel();
    }

    protected double getInitRandomness() {
        return 1.0;
    }

    public void teardown() {
        super.teardown();
        removeChild( orderParamText );
        getBasePage().getRoot().getActivityScheduler().removeActivity( activity );
    }
}

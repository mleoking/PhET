// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp;


/**
 * User: Sam Reid
 * Date: Aug 9, 2005
 * Time: 12:44:38 AM
 */

public class SimpleRampControlPanel extends RampControlPanel {
//    private JCheckBox frictionlessCheckbox;

    public SimpleRampControlPanel( SimpleRampModule simpleRampModule ) {
        super( simpleRampModule );

//        frictionlessCheckbox = createFrictionlessCheckbox();
        addControlFullWidth( new ObjectSelectionPanel( simpleRampModule, simpleRampModule.getRampObjects() ) );
        addControl( getFrictionlessCheckBox() );
        super.addPositionAngleControls();
        finishInit();
    }
}

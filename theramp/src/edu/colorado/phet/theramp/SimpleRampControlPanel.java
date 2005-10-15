/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;


/**
 * User: Sam Reid
 * Date: Aug 9, 2005
 * Time: 12:44:38 AM
 * Copyright (c) Aug 9, 2005 by Sam Reid
 */

public class SimpleRampControlPanel extends RampControlPanel {
//    private JCheckBox frictionlessCheckbox;

    public SimpleRampControlPanel( SimpleRampModule simpleRampModule ) {
        super( simpleRampModule );

//        frictionlessCheckbox = createFrictionlessCheckbox();
        addControl( getFrictionlessCheckBox() );
        addControlFullWidth( new ObjectSelectionPanel( simpleRampModule, simpleRampModule.getRampObjects() ) );
        super.addPositionAngleControls();
        finishInit();
    }
}

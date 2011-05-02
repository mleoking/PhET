// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import javax.swing.*;

/**
 * A check box that provides style/font for controls in Sugar and Salt Solutions
 *
 * @author Sam Reid
 */
public class CheckBox extends JCheckBox {
    public CheckBox( String text ) {
        super( text );
        setFont( SugarAndSaltSolutionsCanvas.CONTROL_FONT );
    }
}

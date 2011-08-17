// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * @author Sam Reid
 */
public class ButtonModel {
    public Font getFont() {
        return new PhetFont( 16 );
    }

    public String getText() {
        return "hello";
    }
}

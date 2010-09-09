/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.Color;

import javax.swing.JRadioButton;
import javax.swing.UIManager;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * A radio button whose text is HTML.  Swing doesn't properly handle the "graying out"
 * of text for JComponents that use HTML strings. See Unfuddle #1704.  This is a 
 * quick-and-dirty workaround for one type of JComponent. A more general solution 
 * is needed - or better yet, a Java fix.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HTMLRadioButton extends JRadioButton {
    
    private Color foreground;
    
    /**
     * Constructor
     * @param text plain text, HTML fragment, or HTML document
     */
    public HTMLRadioButton( String text ) {
        super( HTMLUtils.toHTMLString( text ) );
        this.foreground = getForeground();
    }
    
    public void setForeground( Color foreground ) {
        this.foreground = foreground;
        update();
    }
    
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        update();
    }

    private void update() {
        super.setForeground( isEnabled() ? foreground : getDisabledColor() );
    }

    private Color getDisabledColor() {
        Color color = UIManager.getColor( "RadioButton.disabledText" );
        if ( color == null ) {
            color = Color.GRAY;
        }
        return color;
    }
}

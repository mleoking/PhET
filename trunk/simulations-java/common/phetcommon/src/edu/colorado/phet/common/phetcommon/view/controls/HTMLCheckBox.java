// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * A check box whose text is HTML.  Swing doesn't properly handle the "graying out"
 * of text for JComponents that use HTML strings. See Unfuddle #1704.  This is a
 * quick-and-dirty workaround for one type of JComponent. A more general solution
 * is needed - or better yet, a Java fix.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HTMLCheckBox extends JCheckBox {

    private Color foreground;

    /**
     * Constructor
     *
     * @param text plain text, HTML fragment, or HTML document
     */
    public HTMLCheckBox( String text ) {
        super( HTMLUtils.toHTMLString( text ) );
        this.foreground = getForeground();
    }

    @Override
    public void setForeground( Color foreground ) {
        this.foreground = foreground;
        update();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        update();
    }

    @Override
    public void setText( String text ) {
        super.setText( HTMLUtils.toHTMLString( text ) );
    }

    private void update() {
        super.setForeground( isEnabled() ? foreground : getDisabledColor() );
    }

    private Color getDisabledColor() {
        Color color = UIManager.getColor( "CheckBox.disabledText" );
        if ( color == null ) {
            color = Color.GRAY;
        }
        return color;
    }
}

/* Copyright 2010, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.Color;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * A label whose text is HTML.  Swing doesn't properly handle the "graying out"
 * of text for JComponents that use HTML strings. See Unfuddle #1704.  This is a 
 * quick-and-dirty workaround for one type of JComponent. A more general solution 
 * is needed - or better yet, a Java fix.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HTMLLabel extends JLabel {
    
    private Color foreground;
    
    /**
     * Constructor
     * @param text plain text, HTML fragment, or HTML document
     */
    public HTMLLabel( String text ) {
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
        Color color = UIManager.getColor( "Label.disabledText" );
        if ( color == null ) {
            color = Color.GRAY;
        }
        return color;
    }
    
    // test
    public static void main( String[] args ) {
       
        HTMLLabel label1 = new HTMLLabel( "<html>label1</html>" );
        HTMLLabel label2 = new HTMLLabel( "<html>label2</html>" );
        label2.setEnabled( false );
        
        JPanel panel = new JPanel();
        panel.add( label1 );
        panel.add( label2 );
        
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

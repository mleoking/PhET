package edu.colorado.phet.common.application;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class JMultilineLabel extends JComponent {
    private String text;
    private Insets margin = new Insets( 5, 5, 5, 5 );
    private int maxWidth = Integer.MAX_VALUE;
    private boolean justify;
    private final FontRenderContext frc = new FontRenderContext( null, false, false );
    private static final String JUSTIFIED = "justified";

    public JMultilineLabel( String text ) {
        setText( text );
    }

    private void morph() {
        revalidate();
        repaint();
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        String old = this.text;
        this.text = text;
        firePropertyChange( "text", old, this.text );
        if( ( old == null ) ? text != null : !old.equals( text ) ) {
            morph();
        }
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth( int maxWidth ) {
        if( maxWidth <= 0 ) {
            throw new IllegalArgumentException();
        }
        int old = this.maxWidth;
        this.maxWidth = maxWidth;
        firePropertyChange( "maxWidth", old, this.maxWidth );
        if( old != this.maxWidth ) {
            morph();
        }
    }

    public boolean isJustified() {
        return justify;
    }

    public void setJustified( boolean justify ) {
        boolean old = this.justify;
        this.justify = justify;
        firePropertyChange( JUSTIFIED, old, this.justify );
        if( old != this.justify ) {
            repaint();
        }
    }

    public Dimension getPreferredSize() {
        return paintOrGetSize( null, getMaxWidth() );
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        paintOrGetSize( (Graphics2D)g, getWidth() );
    }

    private Dimension paintOrGetSize( Graphics2D g, int width ) {
        Insets insets = getInsets();
        width -= insets.left + insets.right + margin.left + margin.right;
        float w = insets.left + insets.right + margin.left + margin.right;
        float x = insets.left + margin.left, y = insets.top + margin.top;
        if( width > 0 && text != null && text.length() > 0 ) {
            AttributedString as = new AttributedString( getText() );
            as.addAttribute( TextAttribute.FONT, getFont() );
            AttributedCharacterIterator aci = as.getIterator();
            LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer( aci, frc );
            float max = 0;

            while( lineBreakMeasurer.getPosition() < aci.getEndIndex() ) {
                TextLayout textLayout = lineBreakMeasurer.nextLayout( width );
                if( g != null && isJustified() && textLayout.getVisibleAdvance() > 0.80 * width ) {
                    textLayout = textLayout.getJustifiedLayout( width );
                }
                if( g != null ) {
                    textLayout.draw( g, x, y + textLayout.getAscent() );
                }
                y += textLayout.getDescent() + textLayout.getLeading() + textLayout.getAscent();
                max = Math.max( max, textLayout.getVisibleAdvance() );
            }
            w += max;
        }
        return new Dimension( (int)Math.ceil( w ), (int)Math.ceil( y ) + insets.bottom + margin.bottom );
    }
}
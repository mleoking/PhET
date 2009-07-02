package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.text.MessageFormat;

import javax.swing.JCheckBox;

import edu.colorado.phet.acidbasesolutions.ABSColors;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Check box for component ratios.
 * Component symbols are displayed in specified colors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RatioCheckBox extends JCheckBox {
    
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final Color DISABLED_COLOR = Color.GRAY;
    
    private String symbol1, symbol2;
    private Color color1, color2;
    
    public RatioCheckBox() {
        super();
    }
    
    public RatioCheckBox( String symbol1, String symbol2 ) { 
        this( symbol1, DEFAULT_COLOR, symbol2, DEFAULT_COLOR );
    }
    
    public RatioCheckBox( String symbol1, Color color1, String symbol2, Color color2 ) {
        super();
        setComponents( symbol1, color1, symbol2, color2 );
    }
    
    public void setComponent( String symbol1, String symbol2 ) {
        setComponents( symbol1, DEFAULT_COLOR, symbol2, DEFAULT_COLOR );
    }
    
    public void setComponents( String symbol1, Color color1, String symbol2, Color color2 ) {
        this.symbol1 = symbol1;
        this.color1 = color1;
        this.symbol2 = symbol2;
        this.color2 = color2;
        updateLabel();
    }
    
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateLabel();
    }
    
    private void updateLabel() {
        boolean isColored = isEnabled() || isSelected();
        String s1 = HTMLUtils.createColoredFragment( symbol1, isColored ? color1 : DISABLED_COLOR );
        String s2 = HTMLUtils.createColoredFragment( symbol2, isColored ? color2 : DISABLED_COLOR );
        Object[] args = { s1, s2 };
        String html = HTMLUtils.toHTMLString( MessageFormat.format( ABSStrings.CHECK_BOX_RATIO, args ) );
        setText( html );
        if ( !isColored ) {
            setForeground( DISABLED_COLOR );
        }
    }
    
    /**
     * Check box for H3O/OH ratio.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     */
    public static class HydroniumHydroxideRatioCheckBox extends RatioCheckBox {
        
        public HydroniumHydroxideRatioCheckBox() {
            super( ABSSymbols.H3O_PLUS, ABSColors.H3O_PLUS, ABSSymbols.OH_MINUS, ABSColors.OH_MINUS );
        }
    }
}

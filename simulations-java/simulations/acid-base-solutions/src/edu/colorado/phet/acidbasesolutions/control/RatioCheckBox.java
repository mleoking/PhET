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
public abstract class RatioCheckBox extends JCheckBox {
    
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final Color DISABLED_COLOR = Color.GRAY;
    
    private String format;
    private String symbol1, symbol2;
    private Color color1, color2;
    
    public RatioCheckBox( String format ) {
        this( format, "?", "?" );
    }
    
    public RatioCheckBox( String format, String symbol1, String symbol2 ) { 
        this( format, symbol1, DEFAULT_COLOR, symbol2, DEFAULT_COLOR );
    }
    
    public RatioCheckBox( String format, String symbol1, Color color1, String symbol2, Color color2 ) {
        super();
        this.format = format;
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
        String html = HTMLUtils.toHTMLString( MessageFormat.format( format, args ) );
        setText( html );
        setForeground( isColored ? DEFAULT_COLOR : DISABLED_COLOR );
    }
    
    /**
     * Check box for specific solute ion ratio, identifies specific ions.
     */
    public static class SpecificSoluteRatioCheckBox extends RatioCheckBox {
        
        public SpecificSoluteRatioCheckBox() {
            super( ABSStrings.CHECK_BOX_SOLUTE_RATIO_SPECIFIC );
        }
    }
    
    /**
     * Check box for general solute ion ratio, does not identify specific ions.
     */
    public static class GeneralSoluteRatioCheckBox extends RatioCheckBox {
        
        public GeneralSoluteRatioCheckBox() {
            super( ABSStrings.CHECK_BOX_SOLUTE_RATIO_GENERAL );
        }
    }
    
    /**
     * Check box for H3O/OH ratio.
     */
    public static class HydroniumHydroxideRatioCheckBox extends RatioCheckBox {
        
        public HydroniumHydroxideRatioCheckBox() {
            super( ABSStrings.CHECK_BOX_H3O_OH_RATIO, ABSSymbols.H3O_PLUS, ABSColors.H3O_PLUS, ABSSymbols.OH_MINUS, ABSColors.OH_MINUS );
        }
    }
}

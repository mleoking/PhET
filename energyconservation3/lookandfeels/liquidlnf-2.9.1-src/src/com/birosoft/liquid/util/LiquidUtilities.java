/*
 * LiquidUtilities.java
 *
 * Created on September 28, 2006, 12:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.birosoft.liquid.util;

import java.awt.FontMetrics;

/**
 *
 * @author evickroy
 */
public class LiquidUtilities {
    
    final public static String CLIP_STRING = "...";
    
    /** Creates a new instance of LiquidUtilities */
    public LiquidUtilities() {
    }
    
    
    /**
     * Returns the width of a String based on the Font.
     *
     * @param fm FontMetrics used to measure the String width
     * @param string String to get the width of
     */
    public static int stringWidth(FontMetrics fm, String string){
        return fm.stringWidth(string);
    }
    
    /**
     * Clips the String to the space provided if it is too long.
     *
     * @param fm FontMetrics used to measure the String width
     * @param string String to possibly clip
     * @param availWidth Amount of space that the string can be drawn in
     * @return Clipped string that can fit in the provided space.
     */
    public static String clipStringIfNecessary(FontMetrics fm, String string, int availWidth) {
        if( string == null || string.length() == 0 ) {
            return "";
        }
        
        int textWidth = LiquidUtilities.stringWidth(fm, string);
        
        if( textWidth > availWidth ) {
            return LiquidUtilities.clipString(fm, string, availWidth);
        }
        return string;
    }
    
    /**
     * Clips the String to the width provided.  
     * 
     * NOTE: This assumes that the string doesn't fit in the available space.
     *
     * @param fm FontMetrics used to measure the String width
     * @param string String to display
     * @param availWidth Amount of space that the string can be drawn in
     * @return Clipped string that can fit in the provided space.
     */
    public static String clipString(FontMetrics fm, String string, int availWidth) {

        int clipWidth = LiquidUtilities.stringWidth(fm, CLIP_STRING);

        int chars = 0;
        for(int maxLength = string.length(); chars < maxLength; chars++) {
            clipWidth += fm.charWidth(string.charAt(chars));
            
            if( clipWidth > availWidth ) {
                break;
            }
        }
        string = string.substring(0, chars) + CLIP_STRING;
        
        return string;
    }
    
}

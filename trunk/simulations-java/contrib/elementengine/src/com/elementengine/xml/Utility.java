/*
 * Utility.java
 *
 * Created on April 27, 2003, 9:09 AM
 */

package com.elementengine.xml;
import java.util.StringTokenizer;

/**
 *
 * @author  Default
 */
public final class Utility {
    
    private Utility() {
    }
    
     
   public static String trim (String trimString){
       int lastSpace =trimString.length();
       int spaceIndex = 0;
       while((trimString.charAt(0) == ' ') ){   // remove leading space
         trimString = trimString.substring(1);
         if (trimString.equals(" ")){                      
            break;  // out of while
         }
      }
      int len = trimString.length();   // remove trailing spaces
      while(trimString.charAt(len-1) == ' '){
        trimString= trimString.substring(0,len-1);
        len--;
      }
      return trimString;
    }
   
    public static    double[] getDoubles(String doubleString) {
         double[] doubleData;
         int i = 0;
         StringTokenizer t = new StringTokenizer(doubleString,",() \t\n");
         
         doubleData = new double[t.countTokens()];
         while (t.hasMoreTokens()){
             doubleData[i] = (double)new Double(t.nextToken()).doubleValue();
             i++;
         }
         return doubleData;
   
    }
    
    public static    float[] getFloats(String floatString) {
         float[] floatData;
         int i = 0;
         StringTokenizer t = new StringTokenizer(floatString,",() \t\n");
         floatData = new float[t.countTokens()];
         while (t.hasMoreTokens()){
             floatData[i] = (float)new Float(t.nextToken()).floatValue();
             i++;
         }
         return floatData;
    }

}

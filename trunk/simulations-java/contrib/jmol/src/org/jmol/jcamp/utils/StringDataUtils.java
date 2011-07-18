/**
 * Copyright (c) 2005, Shravan Sadasivan & Richard Spinney 
 * Department of Chemisty - The Ohio State University
 */

package org.jmol.jcamp.utils;

public class StringDataUtils {
  
  /**
   * Returns the sub-string of the string provided
   * @param str
   * @param i
   * @param j
   * @return String
   */
  public static String jcampSubString(String str, int i, int j){
    if (str.length()<j) 
      return str;      
    return str.substring(i,j);    
  }
  
  /**
   * Truncates blank spaces at the end of the string provided
   * @param str
   * @return String
   */
  public static String truncateEndBlanks(String str) {
    while (str.charAt(str.length()-1)==' ') 
       str=str.substring(0,str.length()-1);
    return str;
  }
  
  /**
   * Compares two strings provided and returns an expected integer value based on the comparison
   * @param str1
   * @param str2
   * @return int
   */
  public static int compareStrings(String str1, String str2){
    if (str1==null) return -1;
    if (str2==null) return -1;

    if (str1.length()!=str2.length()) return -1;

    return str1.compareTo(str2);    
  }
  
  /**
   * Reduces the precision of numerical data strings provided
   * @param data
   * @return String
   */
  public static String reduceDataPrecision(String data) {
    // trop de precision sur Communicator!
    if (data.length()>10) {
       if (data.indexOf('E')!=-1)
          data=data.substring(0,data.indexOf('E')-1)+"e"+data.substring(data.indexOf('E')+1);
       if (data.indexOf('e')==-1) data=data.substring(0,9);
            else data=String.valueOf(
                              Math.pow(10,Double.valueOf(data.substring(data.indexOf('e')+1)).doubleValue())*
                              Double.valueOf(data.substring(0,Math.min(9,data.indexOf('e')-1))).doubleValue()
                                              );
       }    
    return data;    
   }
}

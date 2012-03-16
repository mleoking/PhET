package org.jmol.jcamp.utils;

public class NumericDataUtils {
  
  public static int convToIntelInt(int i) {
    int byte0 = i & 0xff;
    int byte1 = (i>>8) & 0xff;
    int byte2 = (i>>16) & 0xff;
    int byte3 = (i>>24) & 0xff;
                // swap the byte order
    return (byte0<<24) | (byte1<<16) | (byte2<<8) | byte3;
  }
  
  public static float convToIntelFloat(int i) {
    int byte0 = i & 0xff;
    int byte1 = (i>>8) & 0xff;
    int byte2 = (i>>16) & 0xff;
    int byte3 = (i>>24) & 0xff;
                // swap the byte order
    int new_i = (byte0<<24) | (byte1<<16) | (byte2<<8) | byte3;

    return Float.intBitsToFloat(new_i);
  }
  
  public static double convToIntelDouble(long i) {
    long byte0 = i & 0xff;
    long byte1 = (i>>8) & 0xff;
    long byte2 = (i>>16) & 0xff;
    long byte3 = (i>>24) & 0xff;
    long byte4 = (i>>32) & 0xff;
    long byte5 = (i>>40) & 0xff;
    long byte6 = (i>>48) & 0xff;
    long byte7 = (i>>56) & 0xff;

                // swap the byte order
    long l = (byte0<<56) | (byte1<<48) | (byte2<<40) | (byte3<<32) | (byte4<<24) | (byte5<<16) | (byte6<<8) | byte7;

    return Double.longBitsToDouble(l);
  }
}

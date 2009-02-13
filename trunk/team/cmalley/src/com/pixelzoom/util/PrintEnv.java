
package com.pixelzoom.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class PrintEnv {

    public static void main( String[] args ) {
        Map map = System.getenv();
        Set keySet = map.keySet();
        Iterator i = keySet.iterator();
        while ( i.hasNext() ) {
            String key = (String) i.next();
            System.out.println( "key=" + key + " value=" + System.getenv( key ) );
        }
    }
}

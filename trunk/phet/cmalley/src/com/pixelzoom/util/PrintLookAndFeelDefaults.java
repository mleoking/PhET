package com.pixelzoom.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * PrintLookAndFeelDefaults prints the default look-and-feel definitions to System.out.
 * The output is alphabetically sorted by key.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PrintLookAndFeelDefaults
{
  public static void main( String[] args )
  {
      // Get the currently installed look and feel
      UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
      
      // Retrieve the keys. We can't use an iterator since the map
      // may be modified during the iteration. So retrieve all keys at once. 
      String[] keySet = (String[]) uidefs.keySet().toArray( new String[0] );
      
      // Sort the keys.
      List keys = Arrays.asList( keySet );
      Collections.sort( keys );
      
      // Print out each key/value pair.
      for( int i = 0; i < keys.size(); i++ ) {
          Object key = keys.get( i );
          Object value = uidefs.get( key );
          System.out.println( key + ": " + value );
      }
  }
}


/* end of file */
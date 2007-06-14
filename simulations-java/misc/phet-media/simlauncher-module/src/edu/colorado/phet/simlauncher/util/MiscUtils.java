/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/util/MiscUtils.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.3 $
 * Date modified : $Date: 2006/06/07 00:25:55 $
 */
package edu.colorado.phet.simlauncher.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * General
 *
 * @author Ron LeMaster
 * @version $Revision: 1.3 $
 */
public class MiscUtils {

    public static void listProperties( Class componentClass ) {
        try {
            BeanInfo bi = Introspector.getBeanInfo( componentClass );
            PropertyDescriptor[] pds = bi.getPropertyDescriptors();
            for( int i = 0; i < pds.length; i++ ) {
                // Get property name
                String propName = pds[i].getName();
                System.out.println( "propName = " + propName );
            }
            // class, prop1, prop2, PROP3
        }
        catch( java.beans.IntrospectionException e ) {
        }
    }
}

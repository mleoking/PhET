/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jul 25, 2005
 * Time: 10:47:08 AM
 * Copyright (c) Jul 25, 2005 by Sam Reid
 */

public class ModelDebugger implements ModelElement {
    private Class aClass;
    public static boolean debugEnabled = false;

    public ModelDebugger( Class aClass ) {
        this.aClass = aClass;
    }

    public void stepInTime( double dt ) {
        if( debugEnabled ) {
            System.out.println( " Stepping model= " + aClass );
        }
    }
}

/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * MvcEventChannel
 * <p>
 * A specialization of EventChannel that executes calls to specified event listeners in the Swing
 * thread. These listeners are indicated by their type: SwingThreadModelListener.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModelEventChannel extends EventChannel {

    public ModelEventChannel( Class interf ) {
        super( interf );
    }

    protected void invokeMethod( final Method method,
                                 final Object target,
                                 final Object[] args ) throws InvocationTargetException, IllegalAccessException {

        // If the target listener is part of the view, then invoke the callback method in the
        // Swing dispatch thread. Otherwise, call the parent class behavior
        if( target instanceof SwingThreadModelListener ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    try {
                        ModelEventChannel.super.invokeMethod( method, target, args );
                    }
                    catch( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                    catch( IllegalAccessException e ) {
                        e.printStackTrace();
                    }
                }
            } );
        }
        else {
            super.invokeMethod( method, target, args );
        }
    }
}

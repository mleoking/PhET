/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import javax.swing.*;
import java.awt.*;

/**
 * RemoteUnavaliableMessagePane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RemoteUnavaliableMessagePane {

    private static String title = "PhET site inaccessible";
    private static String message = "<html>Your request cannot be completed because<br>" +
                                    "the PhET web site cannot be reached." +
                                    "<br><br>" +
                                    "Please check that you have an active internet connection.";

    public static void show( Component parent ) {
        JOptionPane.showMessageDialog( parent, message, title, JOptionPane.INFORMATION_MESSAGE );
    }

    private RemoteUnavaliableMessagePane() {
    }
}

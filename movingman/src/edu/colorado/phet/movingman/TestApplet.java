/** Sam Reid*/
package edu.colorado.phet.movingman;

import javax.swing.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Aug 18, 2004
 * Time: 9:40:41 AM
 * Copyright (c) Aug 18, 2004 by Sam Reid
 */
public class TestApplet extends JApplet {
    public void init() {
        try {
            MovingManModule.main( new String[0] );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}

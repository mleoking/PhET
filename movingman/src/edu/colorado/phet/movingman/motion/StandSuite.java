/** Sam Reid*/
package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.movingman.Man;
import edu.colorado.phet.movingman.MovingManModule;

import javax.swing.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Aug 13, 2004
 * Time: 10:17:33 AM
 * Copyright (c) Aug 13, 2004 by Sam Reid
 */
public class StandSuite extends MotionSuite {
    public StandSuite( MovingManModule module ) throws IOException {
        super( module, "Stand Still" );
        StepMotion stay = new StepMotion() {
            public double stepInTime( Man man, double dt ) {
                return man.getX();
            }
        };

        JPanel standStillPanel = new JPanel();
        standStillPanel.add( new JLabel( "<html>Click 'Go!'<br>to start standing still.</html>" ) );

        super.setMotion( stay );
        super.setControlPanel( standStillPanel );
    }

    public void initialize( Man man ) {
        MovingManModule module = getModule();
        module.repaintBackground();
    }


    public void collidedWithWall() {
    }
}

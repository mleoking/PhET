package edu.colorado.phet.ec2.elements.car;

//import edu.colorado.phet.coreadditions.graphics.ModelViewTransform2d;

import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jul 27, 2003
 * Time: 12:19:00 AM
 * Copyright (c) Jul 27, 2003 by Sam Reid
 */
public interface CarMode {
    public void stepInTime( Car c, double dt );

    void initialize( Car car, double dt );

    void mousePressed( CarGraphic cg, MouseEvent event );

    void mouseDragged( CarGraphic cg, MouseEvent event );

    void mouseReleased( CarGraphic cg, MouseEvent event );
}

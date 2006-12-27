/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common;

import javax.swing.*;
import java.awt.dnd.DropTarget;

/**
 * User: Sam Reid
 * Date: Aug 11, 2003
 * Time: 11:24:19 AM
 * Copyright (c) Aug 11, 2003 by Sam Reid
 */
public class TestFrame {
    private final JFrame jFrame = new JFrame();

    public String getName() {
        return jFrame.getName();
    }

    public synchronized DropTarget getDropTarget() {
        return jFrame.getDropTarget();
    }
}

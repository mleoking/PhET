/*Copyright, Sam Reid, 2003.*/
package org.reids.anttasks.tests;

import org.apache.tools.ant.Task;
import org.reids.anttasks.CopyClasspath;

/**
 * User: Sam Reid
 * Date: Jun 27, 2003
 * Time: 3:19:41 PM
 * Copyright (c) Jun 27, 2003 by Sam Reid
 */
public class TestCopyClasspath {
    public static void main(String[] args) {
        Task t = new CopyClasspath();
        t.execute();
    }
}

package edu.colorado.phet.movingman.application;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 30, 2004
 * Time: 11:23:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class MMTest extends TestCase {
    public TestResult run() {
        MovingManModule.main( new String[0] );
        return super.run();    //To change body of overridden methods use File | Settings | File Templates.
    }
}

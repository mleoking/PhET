/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.examples.hellophet.application;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.examples.hellophet.application.modules.fastmodule.FastModule;
import edu.colorado.phet.common.examples.hellophet.application.modules.messagemodule.MessageModule;
import edu.colorado.phet.common.examples.hellophet.application.modules.compositetestmodule.CompositeTestControlPanel;
import edu.colorado.phet.common.examples.hellophet.application.modules.compositetestmodule.CompositeTestModule;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.model.DynamicClock;
import edu.colorado.phet.common.model.ThreadPriority;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 8:28:34 PM
 * Copyright (c) May 18, 2003 by Sam Reid
 */
public class HelloPhet {

    public static void main( String[] args ) {
        Module m = new MessageModule();
        Module fast = new FastModule();
        Module test = new CompositeTestModule();
        ApplicationDescriptor desc = new ApplicationDescriptor( "Hello PhET!", "A PhET platform development test.", ".01-beta-*", 600, 600 );
//        ApplicationDescriptor desc=new ApplicationDescriptor("Hello PhET!","A PhET platform development test.",".01-beta-*",100,100,true);
        PhetApplication tpa = new PhetApplication( desc, new Module[]{test, m, fast}, new DynamicClock( 1, 20, ThreadPriority.NORMAL ) );
//        m.setApplication(tpa);
        tpa.startApplication( test );
    }
}

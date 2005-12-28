/** Sam Reid*/
package edu.colorado.phet.cck3.tests.junit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.cck3.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import junit.framework.TestCase;/*Test CVS Commit on mac*/

/**
 * User: Sam Reid
 * Date: Jul 8, 2004
 * Time: 4:37:13 PM
 * Copyright (c) Jul 8, 2004 by Sam Reid
 */
public class TestCurrent extends TestCase {
    private CCK3Module module;
    private SwingTimerClock clock;
    private PhetApplication app;

    public TestCurrent( String s ) {
        super( s );
    }

    protected void setUp() throws Exception {
        super.setUp();
        module = new CCK3Module( new String[0] );
        clock = new SwingTimerClock( 1, 30 );
        app = new PhetApplication( new ApplicationModel( "Title", "x", "x",
                                                         new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) ), module, clock ) );
        app.startApplication();
    }

    public void testOhmsLaw() {
        double r = 5.0;
        double v = 9.0;
        testSimpleCircuit( r, v );
    }

    private void testSimpleCircuit( double r, double v ) {
        double battResistance = CCK3Module.MIN_RESISTANCE;
        Resistor resistor = new Resistor( module.getKirkhoffListener(), new Junction( 5, 5 ),
                                          new Junction( 5, 6 ), 1, 1 );
        resistor.setResistance( r - battResistance );
        Battery battery = new Battery( module.getKirkhoffListener(), resistor.getStartJunction(), resistor.getEndJunction(), 1, 1, battResistance, false );
        battery.setVoltageDrop( v );
        module.getCircuit().addBranch( resistor );
        module.getCircuit().addBranch( battery );
//        module.solve();
        KirkhoffSolver ks = new KirkhoffSolver();
        ks.apply( module.getCircuit() );
        double currentThroughResistor = resistor.getCurrent();

        //This doesn't account for battery internal resistance.
        
        assertEquals( "Wrong current for r=" + r + ", v=" + v, -v / r, currentThroughResistor, .1 );
        assertEquals( "Wrong voltage drop for r=" + r + ", v=" + v, v, module.getCircuit().getVoltage( new VoltmeterGraphic.JunctionConnection( resistor.getStartJunction() ),
                                                                                                       new VoltmeterGraphic.JunctionConnection( resistor.getEndJunction() ) ),
                      .1 );
    }

    public void testOhmsLawMany() {
        double numRes = 5;
        double numVolt = 4;
        double minVolt = -100;
        double maxVolt = 100;
        double minRes = CCK3Module.MIN_RESISTANCE * 2;
        double maxRes = 100;

        double dVolt = ( maxVolt - minVolt ) / numVolt;
        double dRes = ( maxRes - minRes ) / numRes;

        for( double r = minRes; r <= maxRes; r += dRes ) {
            for( double v = minVolt; v <= maxVolt; v += dVolt ) {
                module.clear();
                testSimpleCircuit( r, v );
            }
        }
    }

}

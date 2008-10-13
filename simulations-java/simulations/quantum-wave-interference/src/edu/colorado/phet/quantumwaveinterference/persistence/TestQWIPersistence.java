package edu.colorado.phet.quantumwaveinterference.persistence;

import edu.colorado.phet.common.phetcommon.util.persistence.PersistenceUtil;
import edu.colorado.phet.quantumwaveinterference.QuantumWaveInterferenceApplication;
import edu.colorado.phet.quantumwaveinterference.davissongermer.QWIStrings;
import junit.framework.TestCase;

/**
 * Author: Sam Reid
 * Jul 5, 2007, 11:53:13 PM
 */
public class TestQWIPersistence extends TestCase {
    public void testQWIPersistenceEmptyState() throws PersistenceUtil.CopyFailedException {
        //TODO if you want this test case, port it to use PhetApplicationConfig
//        QWIStrings.init( new String[0] );
//        QuantumWaveInterferenceApplication qwiApplication = new QuantumWaveInterferenceApplication( new String[0] );
//        QWIState state = new QWIState( qwiApplication.getIntensityModule() );
//        QWIState copy = (QWIState)PersistenceUtil.copy( state );
//        System.out.println( "copy = " + copy );
//        assertEquals( "qwi state copy should be the same", copy, state );
    }
}

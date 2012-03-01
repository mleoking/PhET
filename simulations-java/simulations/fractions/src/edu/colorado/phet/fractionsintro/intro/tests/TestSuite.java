// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.tests;

import junit.framework.TestCase;

import edu.colorado.phet.fractionsintro.intro.model.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.FactorySet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;

/**
 * @author Sam Reid
 */
public class TestSuite extends TestCase {

    public void testAnimateBucketSliceToPie() {
        ContainerSet containerSet = new ContainerSet( 1, new Container[] { new Container( 1, new int[0] ), new Container( 1, new int[0] ), new Container( 1, new int[0] ) } );
        PieSet pieSet = FactorySet.IntroTab.circularSliceFactory.fromContainerSetState( containerSet );
        pieSet = pieSet.animateBucketSliceToPie( new CellPointer( 0, 0 ) );
        pieSet = pieSet.animateBucketSliceToPie( new CellPointer( 1, 0 ) );
        assertEquals( pieSet.toContainerSet(), new ContainerSet( 1, new Container[] {
                new Container( 1, new int[] { 0 } ), new Container( 1, new int[] { 0 } ), new Container( 1, new int[0] ),
                new Container( 1, new int[0] ), new Container( 1, new int[0] ), new Container( 1, new int[0] ) } ) );
    }
}

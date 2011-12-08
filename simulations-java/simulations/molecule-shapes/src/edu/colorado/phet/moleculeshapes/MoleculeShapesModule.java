// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.PhetJMEApplication;

import com.jme3.math.ColorRGBA;

/**
 * Abstract class for modules that show a single molecule view
 */
public abstract class MoleculeShapesModule extends JMEModule {

    public MoleculeShapesModule( Frame parentFrame ) {
        super( parentFrame, new Function1<Frame, PhetJMEApplication>() {
            public PhetJMEApplication apply( Frame frame ) {
                final PhetJMEApplication application = new PhetJMEApplication( frame );
                MoleculeShapesColor.BACKGROUND.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
                    public void apply( ColorRGBA colorRGBA ) {
                        application.backgroundColor.set( colorRGBA );
                    }
                } );
                return application;
            }
        } );
    }
}

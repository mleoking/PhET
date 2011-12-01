// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.module;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.jmephet.JMEModule;

/**
 * Abstract class for modules that show a single molecule view
 */
public abstract class MoleculeViewModule extends JMEModule {

    // whether bond angles should be shown
    public final Property<Boolean> showBondAngles = new Property<Boolean>( "Show bond angles", false );

    public MoleculeViewModule( Frame parentFrame, String name, IClock clock ) {
        super( parentFrame, name, clock );
    }

    /**
     * @return Our relative screen display scale compared to the stage scale
     */
    public ImmutableVector2D getScale() {
        return new ImmutableVector2D( getCanvasSize().getWidth() / getStageSize().getWidth(),
                                      getCanvasSize().getHeight() / getStageSize().getHeight() );
    }

    public float getApproximateScale() {
        ImmutableVector2D scale = getScale();
        return (float) ( ( scale.getX() + scale.getY() ) / 2 );
    }
}

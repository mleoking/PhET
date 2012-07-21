// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.tabs;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.jmephet.JMETab;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.model.Molecule;

/**
 * Abstract class for modules that show a single molecule view
 */
public abstract class MoleculeViewTab extends JMETab {

    private Property<Molecule> molecule = new Property<Molecule>( null );

    // whether bond angles should be shown
    public final Property<Boolean> showBondAngles = new Property<Boolean>( false );

    // whether lone pairs should be shown
    public final Property<Boolean> showLonePairs = new Property<Boolean>( true );

    // whether terminal lone pairs should also be shown
    public final Property<Boolean> showAllLonePairs = new Property<Boolean>( false );

    public MoleculeViewTab( String name ) {
        super( JMEUtils.getApplication(), name );
    }

    /**
     * @return Our relative screen display scale compared to the stage scale
     */
    public Vector2D getScale() {
        return new Vector2D( getCanvasSize().getWidth() / getStageSize().getWidth(),
                             getCanvasSize().getHeight() / getStageSize().getHeight() );
    }

    public float getApproximateScale() {
        Vector2D scale = getScale();
        return (float) ( ( scale.getX() + scale.getY() ) / 2 );
    }

    public Molecule getMolecule() {
        return molecule.get();
    }

    public void setMolecule( Molecule molecule ) {
        this.molecule.set( molecule );
    }

    public Property<Molecule> getMoleculeProperty() {
        return molecule;
    }

    /*---------------------------------------------------------------------------*
    * options
    *----------------------------------------------------------------------------*/

    public boolean allowTogglingLonePairs() {
        return true;
    }

    public boolean allowTogglingAllLonePairs() {
        return true;
    }
}

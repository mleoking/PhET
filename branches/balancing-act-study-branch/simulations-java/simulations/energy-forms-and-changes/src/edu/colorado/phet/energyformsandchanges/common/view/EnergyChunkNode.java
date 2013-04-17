// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunkNode extends PNode {

    public static final double Z_DISTANCE_WHERE_FULLY_FADED = 0.1; // In meters.
    private static final double WIDTH = 24; // In screen coords, which is close to pixels.

    private static final Map<EnergyType, Image> mapEnergyTypeToImage = new HashMap<EnergyType, Image>() {{
        put( EnergyType.THERMAL, EnergyFormsAndChangesResources.Images.E_THERM_BLANK_ORANGE );
        put( EnergyType.ELECTRICAL, EnergyFormsAndChangesResources.Images.E_ELECTRIC_BLANK );
        put( EnergyType.MECHANICAL, EnergyFormsAndChangesResources.Images.E_MECH_BLANK );
        put( EnergyType.LIGHT, EnergyFormsAndChangesResources.Images.E_LIGHT_BLANK );
        put( EnergyType.CHEMICAL, EnergyFormsAndChangesResources.Images.E_CHEM_BLANK_LIGHT );
        put( EnergyType.HIDDEN, EnergyFormsAndChangesResources.Images.E_DASHED_BLANK );
    }};

    public EnergyChunkNode( final EnergyChunk energyChunk, final ModelViewTransform mvt ) {

        // Control the overall visibility of this node.
        energyChunk.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        // Set up updating of transparency based on Z position.
        energyChunk.zPosition.addObserver( new VoidFunction1<Double>() {
            public void apply( Double zPosition ) {
                updateTransparency( zPosition );
            }
        } );

        // Monitor the energy type and make the image match it.
        energyChunk.energyType.addObserver( new VoidFunction1<EnergyType>() {
            public void apply( EnergyType energyType ) {
                removeAllChildren();
                addChild( createEnergyChunkNode( energyType ) );
            }
        } );

        // Set this node's position when the corresponding model element moves.
        energyChunk.position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }

    // Update the transparency, which is a function of several factors.
    private void updateTransparency( double zPosition ) {

        double zFadeValue = 1;
        if ( zPosition < 0 ) {
            zFadeValue = Math.max( ( Z_DISTANCE_WHERE_FULLY_FADED + zPosition ) / Z_DISTANCE_WHERE_FULLY_FADED, 0 );
        }
        setTransparency( (float) zFadeValue );
    }

    public static PNode createEnergyChunkNode( EnergyType energyType ) {
        final PImage background = new PImage( mapEnergyTypeToImage.get( energyType ) );
        background.addChild( new PhetPText( EnergyFormsAndChangesResources.Strings.ENERGY_CHUNK_LABEL, new PhetFont( 16, true ) ) {{
            setScale( Math.min( background.getFullBoundsReference().width / getFullBoundsReference().width,
                                background.getFullBoundsReference().height / getFullBoundsReference().height ) * 0.95 );
            setOffset( background.getFullBoundsReference().width / 2 - getFullBoundsReference().width / 2,
                       background.getFullBoundsReference().height / 2 - getFullBoundsReference().height / 2 );
        }} );
        background.setScale( WIDTH / background.getFullBoundsReference().width );
        background.setOffset( -background.getFullBoundsReference().width / 2, -background.getFullBoundsReference().height / 2 );
        return background;
    }
}

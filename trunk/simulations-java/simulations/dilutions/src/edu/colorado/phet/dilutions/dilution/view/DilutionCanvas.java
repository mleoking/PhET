// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution.view;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.control.DilutionsSliderNode.SolutionVolumeSliderNode;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;
import edu.colorado.phet.dilutions.common.view.BeakerNode;
import edu.colorado.phet.dilutions.common.view.ConcentrationDisplayNode;
import edu.colorado.phet.dilutions.common.view.SolutionNode;
import edu.colorado.phet.dilutions.dilution.model.DilutionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionCanvas extends AbstractDilutionsCanvas implements Resettable {

    private final Property<Boolean> valuesVisible = new Property<Boolean>( false );//TODO delete this?

    public DilutionCanvas( DilutionModel model, Frame parentFrame ) {

        // water nodes
        BeakerNode waterBeakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, 0.5, model.water, valuesVisible );
        final PDimension waterCylinderSize = waterBeakerNode.getCylinderSize();
        final double waterCylinderEndHeight = waterBeakerNode.getCylinderEndHeight();
        SolutionNode waterSolutionNode = new SolutionNode( waterCylinderSize, waterCylinderEndHeight, model.water, model.getSolutionVolumeRange() );

        PNode equalsNode = new FancyEqualsNode();

        // dilution nodes
        BeakerNode dilutionBeakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, 0.5, model.dilution, valuesVisible );
        final PDimension dilutionCylinderSize = dilutionBeakerNode.getCylinderSize();
        final double dilutionCylinderEndHeight = dilutionBeakerNode.getCylinderEndHeight();
        SolutionNode dilutionSolutionNode = new SolutionNode( dilutionCylinderSize, dilutionCylinderEndHeight, model.dilution, model.getSolutionVolumeRange() );
        SolutionVolumeSliderNode dilutionVolumeSliderNode = new SolutionVolumeSliderNode( Strings.VOLUME_V2, new PDimension( 5, 0.8 * dilutionCylinderSize.getHeight() ),
                                                                                          model.dilution.volume, model.getSolutionVolumeRange(),
                                                                                          Strings.SMALL, Strings.BIG, valuesVisible );
        PDimension concentrationBarSize = new PDimension( 40, dilutionCylinderSize.getHeight() + 50 );
        ConcentrationDisplayNode concentrationDisplayNode = new ConcentrationDisplayNode( Strings.CONCENTRATION_M2, concentrationBarSize,
                                                                                          model.dilution, model.getConcentrationRange(),
                                                                                          valuesVisible );

        // rendering order
        {
            addChild( waterSolutionNode );
            addChild( waterBeakerNode );
            addChild( dilutionSolutionNode );
            addChild( dilutionBeakerNode );
            addChild( dilutionVolumeSliderNode );
            addChild( concentrationDisplayNode );
            addChild( equalsNode );
        }

        // layout
        {
            waterBeakerNode.setOffset( 200, 200 );
            waterSolutionNode.setOffset( waterBeakerNode.getOffset() );
            equalsNode.setOffset( waterBeakerNode.getFullBoundsReference().getMaxX() + 5,
                                  waterBeakerNode.getYOffset() + ( waterCylinderSize.getHeight() / 2 ) - ( equalsNode.getFullBoundsReference().getHeight() / 2 ) );
            dilutionBeakerNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 25,
                                          waterBeakerNode.getYOffset() );
            dilutionSolutionNode.setOffset( dilutionBeakerNode.getOffset() );
            dilutionVolumeSliderNode.setOffset( dilutionBeakerNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( concentrationDisplayNode ) + 20,
                                                dilutionBeakerNode.getYOffset() );
            concentrationDisplayNode.setOffset( dilutionVolumeSliderNode.getFullBoundsReference().getMaxX() - PNodeLayoutUtils.getOriginXOffset( concentrationDisplayNode ) + 20,
                                                dilutionBeakerNode.getFullBoundsReference().getMaxY() - concentrationDisplayNode.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( concentrationDisplayNode ) );
        }
    }

    public void reset() {
        valuesVisible.reset();
    }
}

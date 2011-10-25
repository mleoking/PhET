// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution.view;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;
import edu.colorado.phet.dilutions.common.view.BeakerNode;
import edu.colorado.phet.dilutions.common.view.SolutionNode;
import edu.colorado.phet.dilutions.dilution.model.DilutionModel;
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

        // dilution nodes
        BeakerNode dilutionBeakerNode = new BeakerNode( model.getSolutionVolumeRange().getMax(), Strings.UNITS_LITERS, 0.5, model.dilution, valuesVisible );
        final PDimension dilutionCylinderSize = dilutionBeakerNode.getCylinderSize();
        final double dilutionCylinderEndHeight = dilutionBeakerNode.getCylinderEndHeight();
        SolutionNode dilutionSolutionNode = new SolutionNode( dilutionCylinderSize, dilutionCylinderEndHeight, model.dilution, model.getSolutionVolumeRange() );

        // rendering order
        {
            addChild( waterSolutionNode );
            addChild( waterBeakerNode );
            addChild( dilutionSolutionNode );
            addChild( dilutionBeakerNode );
        }

        // layout
        {
            waterBeakerNode.setOffset( 400, 200 );
            dilutionBeakerNode.setOffset( waterBeakerNode.getFullBoundsReference().getMaxX() + 50, waterBeakerNode.getYOffset() );
            waterSolutionNode.setOffset( waterBeakerNode.getOffset() );
            dilutionSolutionNode.setOffset( dilutionBeakerNode.getOffset() );
        }
    }

    public void reset() {
        valuesVisible.reset();
    }
}

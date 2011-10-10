// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.dilutions.control.ShowValuesNode;
import edu.colorado.phet.dilutions.control.SoluteControlNode;
import edu.colorado.phet.dilutions.model.MolarityModel;

/**
 * Canvas for the "Molarity" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityCanvas extends AbstractDilutionsCanvas implements Resettable {

    private final Property<Boolean> valuesVisible = new Property<Boolean>( false );

    public MolarityCanvas( MolarityModel model, Frame parentFrame ) {

        // nodes
        SoluteControlNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solution.solute );
        ShowValuesNode showValuesNode = new ShowValuesNode( valuesVisible );
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, 18, Color.BLACK, Color.YELLOW ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( soluteControlNode );
            addChild( showValuesNode );
            addChild( resetAllButtonNode );
        }

        // layout
        {
            soluteControlNode.setOffset( 100, 100 );
            showValuesNode.setOffset( 300, 300 );
            resetAllButtonNode.setOffset( showValuesNode.getXOffset(), showValuesNode.getFullBoundsReference().getMaxY() + 10 );
        }
    }

    public void reset() {
        valuesVisible.reset();
    }
}

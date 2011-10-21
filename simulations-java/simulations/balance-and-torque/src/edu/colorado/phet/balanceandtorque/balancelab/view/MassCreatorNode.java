// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.balancelab.model.BalanceLabModel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author John Blanco
 */
public abstract class MassCreatorNode extends ModelElementCreatorNode {

    private static Format MASS_FORMATTER = new DecimalFormat( "###" );

    /**
     * Constructor that creates a version that is unlabeled.
     *
     * @param model
     * @param mvt
     * @param canvas
     */
    public MassCreatorNode( final BalanceLabModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
    }

    /**
     * Constructor that creates a version that is labeled with the mass.
     *
     * @param model
     * @param mvt
     * @param canvas
     * @param mass
     */
    public MassCreatorNode( final BalanceLabModel model, final ModelViewTransform mvt, final PhetPCanvas canvas, double mass ) {
        super( model, mvt, canvas );
        String valueText = MASS_FORMATTER.format( mass );
        setCaption( MessageFormat.format( BalanceAndTorqueResources.Strings.PATTERN_0_VALUE_1_UNITS, valueText, BalanceAndTorqueResources.Strings.KG ) );
    }
}

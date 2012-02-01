// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.awt.Font;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import edu.colorado.phet.beerslawlab.BLLConstants;
import edu.colorado.phet.beerslawlab.BLLResources.Images;
import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.model.Solute.SoluteForm;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyIcon;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for choosing the form of the solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SoluteFormChoiceNode extends PhetPNode {

    private static final Font FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );

    public SoluteFormChoiceNode( final Property<SoluteForm> soluteForm ) {

        final JPanel solidPanel = new JPanel() {{
            add( new PropertyRadioButton<SoluteForm>( Strings.SOLID, soluteForm, SoluteForm.SOLID ) {{
                setFont( FONT );
                setOpaque( false );
            }} );
            add( new PropertyIcon<SoluteForm>( UserComponents.shakerIcon, new ImageIcon( Images.SHAKER_ICON ), soluteForm, SoluteForm.SOLID ) );
        }};

        final JPanel solutionPanel = new JPanel() {{
            add( new PropertyRadioButton<SoluteForm>( Strings.SOLUTION, soluteForm, SoluteForm.STOCK_SOLUTION ) {{
                setFont( FONT );
                setOpaque( false );
            }} );
            add( new PropertyIcon<SoluteForm>( UserComponents.dropperIcon, new ImageIcon( Images.DROPPER_ICON ), soluteForm, SoluteForm.STOCK_SOLUTION ) );
        }};

        JPanel panel = new JPanel() {{
            setOpaque( false );
            add( solidPanel );
            add( Box.createHorizontalStrut( 15 ) );
            add( solutionPanel );
        }};
        addChild( new PSwing( panel ) );
    }
}

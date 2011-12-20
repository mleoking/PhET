// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.control;

import java.awt.Font;

import javax.swing.JPanel;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.model.SoluteForm;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for choosing the form of the solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteFormChoiceNode extends PhetPNode {

    private static final Font FONT = new PhetFont( 18 );

    public SoluteFormChoiceNode( final Property<SoluteForm> soluteForm ) {
        JPanel panel = new VerticalLayoutPanel() {{
            setOpaque( false );
            add( new PropertyRadioButton<SoluteForm>( Strings.SOLID, soluteForm, SoluteForm.SOLID ) {{
                setFont( FONT );
                setOpaque( false );
            }} );
            add( new PropertyRadioButton<SoluteForm>( Strings.SOLUTION, soluteForm, SoluteForm.SOLUTION ) {{
                setFont( FONT );
                setOpaque( false );
            }} );
        }};
        addChild( new PSwing( panel ) );
    }
}

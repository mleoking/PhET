// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Gives the user a list of options
 */
public class OptionsPanel extends PNode {
    public OptionsPanel( final Property<Boolean> showLabels ) {
        this( showLabels, false, new Property<Boolean>( false ) );
    }

    public OptionsPanel( final Property<Boolean> showLabels,
                         final boolean containsWaterOption,
                         final Property<Boolean> showWater ) {
        final PNode title = new PText( "Options" );
        addChild( title );

        PSwing showLabelCheckBox = new PSwing( new JCheckBox( "Show Labels" ) {{
            setSelected( showLabels.get() );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( ActionEvent actionEvent ) {
                    final boolean showThem = isSelected();
                    LWJGLUtils.invoke( new Runnable() {
                        @Override public void run() {
                            showLabels.set( showThem );
                        }
                    } );
                }
            } );
        }} ) {{
            setOffset( 0, title.getFullBounds().getMaxY() + 5 );
        }};
        addChild( showLabelCheckBox );

        title.setOffset( ( showLabelCheckBox.getFullBounds().getWidth() - title.getFullBounds().getWidth() ) / 2, 0 );

    }
}

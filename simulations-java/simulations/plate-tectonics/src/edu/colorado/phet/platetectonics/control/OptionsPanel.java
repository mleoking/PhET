// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Gives the user a list of options
 */
public class OptionsPanel extends PNode {
    public OptionsPanel( final Property<Boolean> showLabels, Runnable resetAll ) {
        this( showLabels, false, new Property<Boolean>( false ), resetAll );
    }

    public OptionsPanel( final Property<Boolean> showLabels,
                         final boolean containsWaterOption,
                         final Property<Boolean> showWater,
                         final Runnable resetAll ) {
        final PNode title = new PText( "Options" );
        addChild( title );

        final Property<Double> maxWidth = new Property<Double>( title.getFullBounds().getWidth() );
        final Property<Double> y = new Property<Double>( title.getFullBounds().getMaxY() );

        final PSwing showLabelCheckBox = new PSwing( new JCheckBox( "Show Labels" ) {{
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
            setOffset( 0, y.get() + 5 );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
        }};
        addChild( showLabelCheckBox );

        PSwing showWaterCheckBox = null;
        if ( containsWaterOption ) {
            showWaterCheckBox = new PSwing( new JCheckBox( "Show Seawater" ) {{
                setSelected( showWater.get() );
                addActionListener( new ActionListener() {
                    @Override public void actionPerformed( ActionEvent actionEvent ) {
                        final boolean showThem = isSelected();
                        LWJGLUtils.invoke( new Runnable() {
                            @Override public void run() {
                                showWater.set( showThem );
                            }
                        } );
                    }
                } );
            }} ) {{
                setOffset( 0, y.get() + 5 );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            }};
            addChild( showWaterCheckBox );
        }

        PNode resetAllNode = new TextButtonNode( "Reset All", new PhetFont( 14 ), Color.ORANGE ) {{
            setOffset( 0, y.get() + 15 );
            y.set( getFullBounds().getMaxY() );
            maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( ActionEvent actionEvent ) {
                    LWJGLUtils.invoke( resetAll );
                }
            } );
        }};
        addChild( resetAllNode );

        // horizontally center title
        title.setOffset( ( maxWidth.get() - title.getFullBounds().getWidth() ) / 2, title.getYOffset() );

        // horizontally center reset all button
        resetAllNode.setOffset( ( maxWidth.get() - resetAllNode.getFullBounds().getWidth() ) / 2, resetAllNode.getYOffset() );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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
        this( showLabels, false, new Property<Boolean>( false ), new Property<Boolean>( false ), resetAll );
    }

    public OptionsPanel( final Property<Boolean> showLabels,
                         final boolean containsWaterOption,
                         final Property<Boolean> showWater,
                         final Property<Boolean> showWaterEnabled,
                         final Runnable resetAll ) {
        final PNode title = new PText( "Options" );
        addChild( title );

        final Property<Double> maxWidth = new Property<Double>( title.getFullBounds().getWidth() );
        final Property<Double> y = new Property<Double>( title.getFullBounds().getMaxY() );

        final PSwing showLabelCheckBox = new PSwing( new JCheckBox( "Show Labels" ) {{
            setSelected( showLabels.get() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent actionEvent ) {
                    final boolean showThem = isSelected();
                    LWJGLUtils.invoke( new Runnable() {
                        public void run() {
                            showLabels.set( showThem );
                        }
                    } );
                }
            } );
            showLabels.addObserver( new SimpleObserver() {
                public void update() {
                    final boolean showThem = showLabels.get();
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setSelected( showThem );
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
                    public void actionPerformed( ActionEvent actionEvent ) {
                        final boolean showThem = isSelected();
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                showWater.set( showThem );
                            }
                        } );
                    }
                } );
                showWaterEnabled.addObserver( new SimpleObserver() {
                    public void update() {
                        final Boolean enabled = showWaterEnabled.get();
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setEnabled( enabled );
                            }
                        } );
                    }
                } );
                showWater.addObserver( new SimpleObserver() {
                    public void update() {
                        final boolean showThem = showWater.get();
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setSelected( showThem );
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

        // TODO: remove. hiding the reset all button on the 1st tab not ideal
        if ( containsWaterOption ) {
            PNode resetAllNode = new TextButtonNode( "Reset All", new PhetFont( 14 ), Color.ORANGE ) {{
                setOffset( 0, y.get() + 15 );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent actionEvent ) {
                        LWJGLUtils.invoke( resetAll );
                    }
                } );
            }};
            addChild( resetAllNode );

            // horizontally center reset all button
            resetAllNode.setOffset( ( maxWidth.get() - resetAllNode.getFullBounds().getWidth() ) / 2, resetAllNode.getYOffset() );
        }

        // horizontally center title
        title.setOffset( ( maxWidth.get() - title.getFullBounds().getWidth() ) / 2, title.getYOffset() );
    }
}

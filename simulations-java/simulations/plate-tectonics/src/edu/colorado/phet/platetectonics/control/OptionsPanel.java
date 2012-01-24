// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsResources;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.view.ColorMode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.PANEL_TITLE_FONT;

/**
 * Gives the user a list of options
 */
public class OptionsPanel extends PNode {
    public OptionsPanel( final Property<Boolean> showLabels, Runnable resetAll, Property<ColorMode> colorMode ) {
        this( showLabels, false, new Property<Boolean>( false ), new Property<Boolean>( false ), resetAll, colorMode );
    }

    public OptionsPanel( final Property<Boolean> showLabels,
                         final boolean containsWaterOption,
                         final Property<Boolean> showWater,
                         final Property<Boolean> showWaterEnabled,
                         final Runnable resetAll,
                         final Property<ColorMode> colorMode ) {
        final PNode title = new PText( PlateTectonicsResources.Strings.OPTIONS ) {{
            setFont( PANEL_TITLE_FONT );
        }};
        addChild( title );

        final Property<Double> maxWidth = new Property<Double>( title.getFullBounds().getWidth() );
        final Property<Double> y = new Property<Double>( title.getFullBounds().getMaxY() );

        if ( !containsWaterOption ) {
            final PSwing densityMode = new PSwing( new JRadioButton( Strings.DENSITY_VIEW ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setSelected( true );
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                colorMode.set( ColorMode.DENSITY );
                            }
                        } );
                    }
                } );
                colorMode.addObserver( new SimpleObserver() {
                    public void update() {
                        final boolean set = colorMode.get() == ColorMode.DENSITY;
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setSelected( set );
                            }
                        } );
                    }
                } );
            }} ) {{
                setOffset( 0, y.get() + 5 );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            }};
            addChild( densityMode );
            final PSwing temperatureMode = new PSwing( new JRadioButton( Strings.TEMPERATURE_VIEW ) {{
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setSelected( true );
                        LWJGLUtils.invoke( new Runnable() {
                            public void run() {
                                colorMode.set( ColorMode.TEMPERATURE );
                            }
                        } );
                    }
                } );
                colorMode.addObserver( new SimpleObserver() {
                    public void update() {
                        final boolean set = colorMode.get() == ColorMode.TEMPERATURE;
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setSelected( set );
                            }
                        } );
                    }
                } );
            }} ) {{
                setOffset( 0, y.get() );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            }};
            addChild( temperatureMode );
        }

        final PSwing showLabelCheckBox = new PSwing( new JCheckBox( Strings.SHOW_LABELS ) {{
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
            showWaterCheckBox = new PSwing( new JCheckBox( Strings.SHOW_SEAWATER ) {{
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
            PNode resetAllNode = new TextButtonNode( Strings.RESET_ALL, new PhetFont( 14 ), Color.ORANGE ) {{
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

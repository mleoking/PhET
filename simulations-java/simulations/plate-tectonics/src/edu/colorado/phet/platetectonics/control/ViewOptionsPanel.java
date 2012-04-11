// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJCheckBox;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsResources;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.modules.CrustTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.view.ColorMode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.PANEL_TITLE_FONT;

/**
 * Gives the user a list of view options
 */
public class ViewOptionsPanel extends PNode {
    public ViewOptionsPanel( final PlateTectonicsTab tab, final Property<Boolean> showLabels, Property<ColorMode> colorMode ) {
        this( tab, showLabels, false, new Property<Boolean>( false ), new Property<Boolean>( false ), colorMode );
    }

    public ViewOptionsPanel( final PlateTectonicsTab tab, final Property<Boolean> showLabels,
                             final boolean containsWaterOption,
                             final Property<Boolean> showWater,
                             final Property<Boolean> showWaterEnabled,
                             final Property<ColorMode> colorMode ) {
        final PNode title = new PText( PlateTectonicsResources.Strings.OPTIONS ) {{
            setFont( PANEL_TITLE_FONT );
        }};
        addChild( title );

        final Property<Double> maxWidth = new Property<Double>( title.getFullBounds().getWidth() );
        final Property<Double> y = new Property<Double>( title.getFullBounds().getMaxY() );

        final PSwing densityMode = new PSwing( new SimSharingJRadioButton( UserComponents.densityView, Strings.DENSITY_VIEW ) {{
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
        final PSwing temperatureMode = new PSwing( new SimSharingJRadioButton( UserComponents.temperatureView, Strings.TEMPERATURE_VIEW ) {{
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
        final PSwing combinedMode = new PSwing( new SimSharingJRadioButton( UserComponents.bothView, Strings.BOTH_VIEW ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setSelected( true );
                    LWJGLUtils.invoke( new Runnable() {
                        public void run() {
                            colorMode.set( ColorMode.COMBINED );
                        }
                    } );
                }
            } );
            colorMode.addObserver( new SimpleObserver() {
                public void update() {
                    final boolean set = colorMode.get() == ColorMode.COMBINED;
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
        addChild( combinedMode );

        y.set( y.get() + 5 );

        // TODO: implement labels for 2nd tab
        if ( tab instanceof CrustTab ) {
            final PSwing showLabelCheckBox = new PSwing( new SimSharingJCheckBox( UserComponents.showLabels, Strings.SHOW_LABELS ) {{
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
                setOffset( 0, y.get() );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            }};
            addChild( showLabelCheckBox );
        }

        PSwing showWaterCheckBox = null;
        if ( containsWaterOption ) {
            showWaterCheckBox = new PSwing( new SimSharingJCheckBox( UserComponents.showWater, Strings.SHOW_SEAWATER ) {{
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
                setOffset( 0, y.get() );
                y.set( getFullBounds().getMaxY() );
                maxWidth.set( Math.max( maxWidth.get(), getFullBounds().getWidth() ) );
            }};
            addChild( showWaterCheckBox );
        }

        // this prevents panel resizing when the button bounds change (like when they are pressed)
        addChild( new Spacer( 0, y.get(), 1, 1 ) );

        // horizontally center title
        title.setOffset( ( maxWidth.get() - title.getFullBounds().getWidth() ) / 2, title.getYOffset() );
    }
}

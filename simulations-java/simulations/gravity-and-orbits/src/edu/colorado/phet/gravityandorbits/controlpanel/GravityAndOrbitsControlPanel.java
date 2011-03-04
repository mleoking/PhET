// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.*;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * Control panel for a GravityAndOrbitsMode (one control panel per mode).  Multiple control panels
 * are synchronized through Module specific properties.
 */
public class GravityAndOrbitsControlPanel extends VerticalLayoutPanel {

    public static final Color BACKGROUND = new Color( 3, 0, 133 );
    public static final Color FOREGROUND = Color.white;
    public static final Font CONTROL_FONT = new PhetFont( 16, true );

    public GravityAndOrbitsControlPanel( final GravityAndOrbitsModule module, GravityAndOrbitsModel model ) {
        super();

        setFillNone();
        setAnchor( GridBagConstraints.WEST );

        // add mode check-boxes
        for ( GravityAndOrbitsMode m : module.getModes() ) {
            add( m.newComponent( module.getModeProperty() ) );
        }
        setFillHorizontal();

        // "Physics" subpanel
        add( new VerticalLayoutPanel() {{
            setOpaque( false );
            setAnchor( GridBagConstraints.WEST );
            setFill( GridBagConstraints.NONE );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), GAOStrings.PHYSICS ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );

            // Gravity on/off control
            add( new HorizontalLayoutPanel() {{
                add( new JLabel( MessageFormat.format( GAOStrings.PATTERN_LABEL, GAOStrings.GRAVITY ) ) {{
                    setFontsAndColors( this );
                }} );
                add( Box.createRigidArea( new Dimension( 20, 1 ) ) );
                add( new PropertyRadioButton<Boolean>( GAOStrings.ON, module.getGravityEnabledProperty(), true ) {{
                    setFontsAndColors( this );
                }} );
                add( new PropertyRadioButton<Boolean>( GAOStrings.OFF, module.getGravityEnabledProperty(), false ) {{
                    setFontsAndColors( this );
                }} );
            }} );
        }} );

        // "Show" subpanel
        add( new VerticalLayoutPanel() {{
            setOpaque( false );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), GAOStrings.SHOW ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );
            setFillNone();
            setAnchor( GridBagConstraints.WEST );

            //Checkboxes for Gravity force and Velocity vectors
            add( new JPanel( new GridLayout( 2, 2 ) ) {
                {
                    setOpaque( false );

                    add( new GAOCheckBox( GAOStrings.GRAVITY_FORCE, module.getShowGravityForceProperty() ) );
                    addArrow( PhetColorScheme.GRAVITATIONAL_FORCE );
                    add( new GAOCheckBox( GAOStrings.VELOCITY, module.getShowVelocityProperty() ) );
                    addArrow( PhetColorScheme.VELOCITY );
                    setMaximumSize( getPreferredSize() );
                }

                private void addArrow( final Color color ) {
                    add( new JLabel( new ImageIcon( new ArrowNode( new Point2D.Double(), new Point2D.Double( 65, 0 ), 15, 15, 5, 2, true ) {{
                        setPaint( color );
                        setStrokePaint( Color.darkGray );
                    }}.toImage() ) ) );
                }
            } );
            add( new GAOCheckBox( GAOStrings.MASS, module.getShowMassProperty() ) );
            add( new GAOCheckBox( GAOStrings.PATH, module.getShowPathProperty() ) );
            add( new GAOCheckBox( GAOStrings.GRID, module.getShowGridProperty() ) );
            //Panel with measuring tape.
            add( new GAOCheckBox( GAOStrings.MEASURING_TAPE, module.getMeasuringTapeVisibleProperty() ) {
                {
                    final Icon defaultIcon = getIcon();
                    final Icon disabledUnselectedIcon = grayOut( UIManager.getIcon( "CheckBox.icon" ) );
                    final Icon disabledSelectedIcon = disabledUnselectedIcon;//todo: find a way to get this from the UIManager; until this is fixed just render as unselected when disabled

                    module.getScaleProperty().addObserver( new SimpleObserver() {
                        public void update() {
                            setEnabled( module.getScaleProperty().getValue() == Scale.REAL );//only enable the measuring tape in real scale
                            setForeground( module.getScaleProperty().getValue() == Scale.REAL ? Color.white : Color.darkGray );
                            if ( isEnabled() ) {
                                iconFixWorkaround( defaultIcon );
                            }
                            else {
                                if ( isSelected() ) {
                                    iconFixWorkaround( disabledSelectedIcon );
                                }
                                else {
                                    iconFixWorkaround( disabledUnselectedIcon );
                                }
                            }
                            iconFixWorkaround( isEnabled() ? defaultIcon : disabledUnselectedIcon );
                        }

                        //Sets the icon, but only on Windows.  This is because on the Windows L&F, the check box icon itself still looks clickable even when disabled.
                        //Applying this fix to Linux causes the checkbox to not render; not sure what it would do to Mac (which looks good without the workaround), therefore we just apply the workaround to Windows.
                        //Tested that this works properly on Windows with both Java 1.6 and 1.5
                        private void iconFixWorkaround( Icon icon ) {
                            if ( PhetUtilities.isWindows() ) {
                                setIcon( icon );
                            }
                        }
                    } );
                }

                private Icon grayOut( Icon checkBoxIcon ) {
                    final BufferedImage image = toImage( this, checkBoxIcon );

                    float[] scales = { 1f, 1f, 1f, 0.5f };//TODO: what about OS's that don't use an alpha channel?
                    float[] offsets = new float[4];

                    final BufferedImage filtered = new RescaleOp( scales, offsets, null ).filter( image, null );
                    final Icon disabledIcon = new ImageIcon( filtered );
                    return disabledIcon;
                }
            } );
        }} );

        // Mass sliders
        for ( Body body : model.getBodies() ) {
            if ( body.isMassSettable() ) {
                add( new BodyMassControl( body, body.getMassProperty().getInitialValue() / 2, body.getMassProperty().getInitialValue() * 2,
                                          "", "", body.getTickValue(), body.getTickLabel() ) );
            }
        }
    }

    private static void setFontsAndColors( JComponent component ) {
        component.setFont( CONTROL_FONT );
        component.setForeground( FOREGROUND );
    }

    public static BufferedImage toImage( Component component, Icon icon ) {
        BufferedImage image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE );//guessing type based on experience with mac problems
        Graphics2D g2 = image.createGraphics();
        icon.paintIcon( component, g2, 0, 0 );
        g2.dispose();
        return image;
    }
}
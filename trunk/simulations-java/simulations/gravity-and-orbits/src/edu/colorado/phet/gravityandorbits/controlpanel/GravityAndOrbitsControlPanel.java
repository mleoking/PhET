// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.PhetLineBorder;
import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
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

        setBackground( BACKGROUND );
        setFillNone();
        setAnchor( GridBagConstraints.WEST );

        // add mode check-boxes
        for ( GravityAndOrbitsMode m : module.getModes() ) {
            add( m.newComponent( module.getModeProperty() ) );
        }
        setFillHorizontal();

        // "Physics" subpanel
        add( new VerticalLayoutPanel() {{
            setBackground( BACKGROUND );
            setOpaque( false );
            setAnchor( GridBagConstraints.WEST );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), GAOStrings.PHYSICS ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );

            // Gravity on/off control
            add( new PropertyCheckBox( GAOStrings.GRAVITY, module.getGravityEnabledProperty() ) {{
                setFont( CONTROL_FONT );
                setForeground( FOREGROUND );
                setBackground( BACKGROUND );
            }} );
        }} );

        // "Show" subpanel
        add( new VerticalLayoutPanel() {{
            setBackground( BACKGROUND );
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
                    setBackground( BACKGROUND );
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
        }} );

        // "Scale" subpanel
        add( new VerticalLayoutPanel() {{
            setBackground( BACKGROUND );
            setOpaque( false );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), GAOStrings.SCALE ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );
            setFillNone();
            setAnchor( GridBagConstraints.WEST );

            add( new GAORadioButton<Scale>( GAOStrings.CARTOON, module.getScaleProperty(), Scale.CARTOON ) );
            add( new GAORadioButton<Scale>( GAOStrings.REAL, module.getScaleProperty(), Scale.REAL ) );

            //Panel with measuring tape.
            add( new JPanel() {{
                setBackground( BACKGROUND );
                setOpaque( false );
                add( Box.createRigidArea( new Dimension( 25, 1 ) ) );

                add( new GAOCheckBox( GAOStrings.MEASURING_TAPE, module.getMeasuringTapeVisibleProperty() ) {
                    {
                        final Icon defaultIcon = getIcon();
                        final Icon disabledUnselectedIcon = grayOut( UIManager.getIcon( "CheckBox.icon" ) );
                        final Icon disabledSelectedIcon = disabledUnselectedIcon;//todo: find a way to get this from the UIManager; until this is fixed just render as unselected when disabled
//                    final Icon disabledSelectedIcon = grayOut( UIManager.getLookAndFeel().getDisabledSelectedIcon(this, new ImageIcon( toImage( this, UIManager.getIcon( "CheckBox.icon" )) ) ) );//http://stackoverflow.com/questions/1663729/accessing-look-and-feel-default-icons

                        module.getScaleProperty().addObserver( new SimpleObserver() {
                            public void update() {
                                setEnabled( module.getScaleProperty().getValue() == Scale.REAL );//only enable the measuring tape in real scale
                                setForeground( module.getScaleProperty().getValue() == Scale.REAL ? Color.white : Color.darkGray );
                                if ( isEnabled() ) {
                                    setIcon( defaultIcon );
                                }
                                else {
                                    if ( isSelected() ) {
                                        setIcon( disabledSelectedIcon );
                                    }
                                    else {
                                        setIcon( disabledUnselectedIcon );
                                    }
                                }
                                setIcon( isEnabled() ? defaultIcon : disabledUnselectedIcon );
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
        }} );

        // Mass sliders
        for ( Body body : model.getBodies() ) {
            if ( body.isMassSettable() ) {
                add( new BodyMassControl( body, body.getMassProperty().getInitialValue() / 2, body.getMassProperty().getInitialValue() * 2,
                                          "", "", body.getTickValue(), body.getTickLabel() ) );
            }
        }
    }

    //For debugging
    public static void showIcon( String name, final Icon icon ) {
        new JFrame( name ) {{
            setContentPane( new JCheckBox() {{
                setIcon( icon );
            }} );
            pack();
        }}.setVisible( true );
    }

    //For debugging
    public static void showImage( String name, final Image image ) {
        new JFrame( name ) {{
            setContentPane( new JLabel( new ImageIcon( image ) ) );
            pack();
        }}.setVisible( true );
    }

    public static BufferedImage toImage( Component component, Icon icon ) {
        BufferedImage image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE );//guessing type based on experience with mac problems
//        BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage( icon.getIconWidth(), icon.getIconHeight() );
        Graphics2D g2 = image.createGraphics();
        icon.paintIcon( component, g2, 0, 0 );
        g2.dispose();
        return image;
    }
}
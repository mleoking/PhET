// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.*;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

import static java.text.MessageFormat.format;

/**
 * Control panel for a GravityAndOrbitsMode (one control panel per mode).  Multiple control panels
 * are synchronized through Module specific properties.
 */
public class GravityAndOrbitsControlPanel extends VerticalLayoutPanel {

    public static final Color BACKGROUND = new Color( 3, 0, 133 );
    public static final Color FOREGROUND = Color.white;
    public static final Font CONTROL_FONT = new PhetFont( 16, true );

    public GravityAndOrbitsControlPanel( final GravityAndOrbitsModule module, final GravityAndOrbitsModel model ) {
        super();

        setFillNone();
        setAnchor( GridBagConstraints.WEST );

        // add mode check-boxes
        for ( GravityAndOrbitsMode m : module.getModes() ) {
            add( m.newControl( module.modeProperty ) );
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
                add( new JLabel( format( GAOStrings.PATTERN_LABEL, GAOStrings.GRAVITY ) ) {{
                    setFontsAndColors( this );
                }} );
                add( Box.createRigidArea( new Dimension( 20, 1 ) ) );
                add( new GAORadioButton<Boolean>( GAOStrings.ON, module.gravityEnabledProperty, true ) );
                add( new GAORadioButton<Boolean>( GAOStrings.OFF, module.gravityEnabledProperty, false ) );
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
            add( new JPanel( new GridLayout( 2, 2 ) ) {{
                setOpaque( false );

                add( new GAOCheckBox( GAOStrings.GRAVITY_FORCE, module.showGravityForceProperty ) );
                add( newArrow( PhetColorScheme.GRAVITATIONAL_FORCE ) );
                add( new GAOCheckBox( GAOStrings.VELOCITY, module.showVelocityProperty ) );
                add( newArrow( PhetColorScheme.VELOCITY ) );
                setMaximumSize( getPreferredSize() );
            }} );
            if ( module.showMassCheckBox ) {//only show this on real mode
                add( new GAOCheckBox( GAOStrings.MASS, module.showMassProperty ) );
            }
            add( new GAOCheckBox( GAOStrings.PATH, module.showPathProperty ) );
            add( new GAOCheckBox( GAOStrings.GRID, module.showGridProperty ) );
            //Panel with measuring tape.
            if ( module.showMeasuringTape ) {
                add( new GAOCheckBox( GAOStrings.MEASURING_TAPE, module.measuringTapeVisibleProperty ) );
            }
        }} );

        // Mass sliders
        for ( Body body : model.getBodies() ) {
            if ( body.isMassSettable() ) {
                //REVIEW are the parameters associated with the empty string args vestigial? Might be better to add a BodyMassControl constructor that makes them optional.
                add( new BodyMassControl( body, body.getMassProperty().getInitialValue() / 2, body.getMassProperty().getInitialValue() * 2, "", "", body.getTickValue(), body.getTickLabel() ) );
            }
        }
    }

    //REVIEW use GrabbableVectorNode to create the arrows, so that they look the same in play area and control panel.
    private JLabel newArrow( final Color color ) {
        return new JLabel( new ImageIcon( new ArrowNode( new Point2D.Double(), new Point2D.Double( 65, 0 ), 15, 15, 5, 2, true ) {{
            setPaint( color );
            setStrokePaint( Color.darkGray );
        }}.toImage() ) );
    }

    private static void setFontsAndColors( JComponent component ) {
        component.setFont( CONTROL_FONT );
        component.setForeground( FOREGROUND );
    }
}
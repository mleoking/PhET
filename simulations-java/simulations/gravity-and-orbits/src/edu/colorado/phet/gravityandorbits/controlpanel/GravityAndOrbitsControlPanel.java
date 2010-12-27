/* Copyright 2010, University of Colorado */

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.geom.Point2D;

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
 * Control panel template.
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

        // Gravity on/off control
        add( new PropertyCheckBox( GAOStrings.GRAVITY, module.getGravityEnabledProperty() ) {{
            setFont( CONTROL_FONT );
            setForeground( FOREGROUND );
            setBackground( BACKGROUND );
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
            add( new JPanel() {{
                setBackground( BACKGROUND );
                setOpaque( false );
                add( Box.createRigidArea( new Dimension( 25, 1 ) ) );
                add( new GAOCheckBox( GAOStrings.MEASURING_TAPE, module.getMeasuringTapeVisibleProperty() ) {{
                    module.getScaleProperty().addObserver( new SimpleObserver() {
                        public void update() {
                            setEnabled( module.getScaleProperty().getValue() == Scale.REAL );//only enable the measuring tape in real scale
                            setForeground( module.getScaleProperty().getValue() == Scale.REAL ? Color.white : Color.darkGray );
                        }
                    } );
                }} );
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
}
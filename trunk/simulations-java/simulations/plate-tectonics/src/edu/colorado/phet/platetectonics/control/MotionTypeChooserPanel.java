// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.MotionType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Allows you to choose between convergent, divergent and transform boundaries
 */
public class MotionTypeChooserPanel extends PNode {
    private Property<MotionType> motionType;
    private PlateMotionModel plateModel;

    private static final double SPACING = 10;
    private static final double ICON_WIDTH = 100;
    private static final double ICON_HEIGHT = 80;

    public MotionTypeChooserPanel( final PlateMotionModel plateModel ) {
        this.plateModel = plateModel;
        this.motionType = plateModel.motionType;

        boolean showDivergent = plateModel.leftPlateType.get().isContinental() && plateModel.rightPlateType.get().isContinental();

        motionType.set( MotionType.CONVERGENT );

        PSwing convergentButton = new PSwing( new MotionTypeChooserRadioButton( "Convergent", MotionType.CONVERGENT ) );
        PSwing divergentButton = new PSwing( new MotionTypeChooserRadioButton( "Divergent", MotionType.DIVERGENT ) );
        PSwing transformButton = new PSwing( new MotionTypeChooserRadioButton( "Transform", MotionType.TRANSFORM ) );

        Property<Double> x = new Property<Double>( 0.0 );

        // TODO: refactor for simplicity
        convergentButton.setOffset( x.get(), ICON_HEIGHT + 10 );
        addChild( convergentButton );
        addChild( new Spacer( x.get(), 0, ICON_WIDTH, ICON_HEIGHT ) );
        x.set( x.get() + ICON_WIDTH + SPACING );


        if ( showDivergent ) {
            divergentButton.setOffset( x.get(), ICON_HEIGHT + 10 );
            addChild( divergentButton );
            addChild( new Spacer( x.get(), 0, ICON_WIDTH, ICON_HEIGHT ) );
            x.set( x.get() + ICON_WIDTH + SPACING );
        }

        transformButton.setOffset( x.get(), ICON_HEIGHT + 10 );
        addChild( transformButton );
        addChild( new Spacer( x.get(), 0, ICON_WIDTH, ICON_HEIGHT ) );
        x.set( x.get() + ICON_WIDTH + SPACING );
    }

    private class MotionTypeChooserRadioButton extends JRadioButton {
        private MotionTypeChooserRadioButton( String title, final MotionType type ) {
            super( title );
            setSelected( motionType.get() == type );
            addActionListener( new ActionListener() {
                @Override public void actionPerformed( ActionEvent actionEvent ) {
                    LWJGLUtils.invoke( new Runnable() {
                        @Override public void run() {
                            motionType.set( type );
                        }
                    } );
                }
            } );
            motionType.addObserver( new ChangeObserver<MotionType>() {
                @Override public void update( final MotionType newValue, MotionType oldValue ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        @Override public void run() {
                            setSelected( motionType.get() == newValue );
                        }
                    } );
                }
            } );
            plateModel.animationStarted.addObserver( new SimpleObserver() {
                @Override public void update() {
                    final boolean enabled = !plateModel.animationStarted.get();
                    SwingUtilities.invokeLater( new Runnable() {
                        @Override public void run() {
                            setEnabled( enabled );
                        }
                    } );
                }
            } );
        }
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishMetricControlPanel;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFCheckBox;
import edu.colorado.phet.fluidpressureandflow.common.view.FPAFRadioButton;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.isSelected;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.icon;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.*;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.*;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.RulerIcon;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.getConstraints;

/**
 * Control panel for the Water Tower module, has ruler, measuring tape, units and 'hose'
 *
 * @author Sam Reid
 */
public class WaterTowerControlPanel extends VerticalLayoutPanel {
    public WaterTowerControlPanel( final WaterTowerModule module ) {

        //Measuring devices
        add( new JPanel( new GridBagLayout() ) {{

            //Ruler check box
            add( new FPAFCheckBox( rulerCheckBox, RULER, module.rulerVisible ), getConstraints( 0, 0 ) );

            //Measuring tape
            add( new FPAFCheckBox( measuringTapeCheckBox, MEASURING_TAPE, module.measuringTapeVisible ), getConstraints( 0, 1 ) );

            //Icons omitted in this tab to save horizontal space
            //Ruler icon
            add( RulerIcon( module ), getConstraints( 1, 0 ) );

            //Measuring tape icon
            add( MeasuringTapeIcon( module ), getConstraints( 1, 1 ) );
        }} );

        //Units control panel that allows choice between english and metric
        SettableProperty<UnitSet> units = module.model.units;
        add( new EnglishMetricControlPanel( new FPAFRadioButton<UnitSet>( metricRadioButton, METRIC, units, UnitSet.METRIC ),
                                            new FPAFRadioButton<UnitSet>( englishRadioButton, ENGLISH, units, UnitSet.ENGLISH ) ) );

        //Separator
        add( Box.createRigidArea( new Dimension( 5, 5 ) ) );//separate the "hose" control a bit from the other controls so it is easier to parse visually

        add( new JPanel( new GridBagLayout() ) {{
            //Hose on/off
            add( new FPAFCheckBox( hoseCheckBox, HOSE, module.model.hose.enabled ), getConstraints( 0, 0 ) );

            //Icon for hose
            add( HoseIcon( module ), getConstraints( 1, 0 ) );
        }} );
    }

    private JLabel MeasuringTapeIcon( final WaterTowerModule module ) {
        PNode measuringTape = new FPAFMeasuringTape( ModelViewTransform.createIdentity(), new BooleanProperty( true ), new Property<UnitSet>( UnitSet.METRIC ) ).getBodyNode();
        return new JLabel( new ImageIcon( multiScaleToHeight( toBufferedImage( measuringTape.toImage() ), 35 ) ) ) {{
            addMouseListener( new MouseAdapter() {
                @Override public void mousePressed( final MouseEvent e ) {
                    SimSharingManager.sendUserMessage( measuringTapeCheckBoxIcon, icon, pressed, parameterSet( isSelected, !module.measuringTapeVisible.get() ) );
                    module.measuringTapeVisible.toggle();
                }
            } );
        }};
    }

    private JLabel HoseIcon( final WaterTowerModule module ) {
        final int width = 60;
        final int height = 14;
        PNode node = new PNode() {{
            final PhetPPath hosePart = new PhetPPath( new RoundRectangle2D.Double( 0, 0, width, height, 10, 10 ), Color.green, new BasicStroke( 1 ), Color.darkGray ) {{
                //workaround the "edges get cut off in toImage" problem
                setBounds( -1, -1, width + 2, height + 2 );
            }};
            addChild( hosePart );
            addChild( new PImage( BufferedImageUtils.multiScaleToHeight( getRotatedImage( Images.NOZZLE, Math.PI / 2 ), (int) ( hosePart.getFullBounds().getHeight() + 4 ) ) ) {{
                setOffset( hosePart.getFullBounds().getMaxX() - getFullBounds().getWidth() + 15, hosePart.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
            }} );
        }};
        final ImageIcon imageIcon = new ImageIcon( node.toImage() );

        //restore
//        module.model.hose.enabled.set( enabled );
        return new JLabel( imageIcon ) {{
            addMouseListener( new MouseAdapter() {
                @Override public void mousePressed( final MouseEvent e ) {
                    SimSharingManager.sendUserMessage( hoseCheckBoxIcon, icon, pressed, parameterSet( isSelected, !module.model.hose.enabled.get() ) );
                    module.model.hose.enabled.toggle();
                }
            } );
        }};
    }
}
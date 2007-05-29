package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.rotation.controls.SymbolKey;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:48:07 AM
 */
public class TorqueControlPanel extends JPanel {
    public TorqueControlPanel( GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel ) {
        super( new GridBagLayout() );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        SymbolKey symbolKey = new SymbolKey();
        setBorder( BorderFactory.createTitledBorder( "Controls" ) );
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
        JPanel sliderPanel = new JPanel( new GridBagLayout() );

        TorqueSlider[] sliders = new TorqueSlider[]{
                new TorqueSlider( 0, 1, "R=Outer Radius", "0.00", "m", rotationLayoutStrategy ),
                new TorqueSlider( 0, 1, "r=Inner Radius", "0.00", "m", rotationLayoutStrategy ),
                new TorqueSlider( 0, 1, "Mass", "0.00", "kg", rotationLayoutStrategy ),
                new TorqueSlider( 0, 1, "Force of Brake", "0.00", "N", rotationLayoutStrategy )
        };
        ILayoutStrategy rotationLayoutStrategy = new TorqueSliderLayout( sliders );
        for( int i = 0; i < sliders.length; i++ ) {
            sliderPanel.add( sliders[i], gridBagConstraints );
        }
//        ShowVectorsControl showVectorsControl = new ShowVectorsControl( vectorViewModel );
        add( sliderPanel, getConstraints( 0, 0, 2 ) );
        add( graphSelectionControl, getConstraints( 1, 1, 1 ) );
//        add( Box.createRigidArea( new Dimension( 50, 10 ) ), getConstraints( 1, 0 ) );
        add( symbolKey, getConstraints( 0, 1, 1 ) );
//        add( showVectorsControl, getConstraints( 0, 1 ) );
    }

    private GridBagConstraints getConstraints( int gridX, int gridY, int gridWidth ) {
        return new GridBagConstraints( gridX, gridY, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }

    static class TorqueSlider extends LinearValueControl {

        public TorqueSlider( double min, double max, String label, String textFieldPattern, String units, ILayoutStrategy iLayoutStrategy ) {
            super( min, max, label, textFieldPattern, units, iLayoutStrategy );
            getSlider().setPaintLabels( false );
        }
    }

    static class TorqueSliderLayout implements ILayoutStrategy {
        private TorqueSlider[] sliders;

        public TorqueSliderLayout( TorqueSlider[] sliders ) {
            this.sliders = sliders;
        }

        public void doLayout( AbstractValueControl valueControl ) {
            
            // Get the components that will be part of the layout
            JComponent slider = valueControl.getSlider();
            JComponent textField = valueControl.getTextField();
            JComponent valueLabel = valueControl.getValueLabel();
            JComponent unitsLabel = valueControl.getUnitsLabel();

            // Label+textfield+units in a panel.
            JPanel valuePanel = new JPanel();
            EasyGridBagLayout valueLayout = new EasyGridBagLayout( valuePanel );
            valuePanel.setLayout( valueLayout );
            valueLayout.setAnchor( GridBagConstraints.WEST );
            valueLayout.addComponent( valueLabel, 0, 0 );
            valueLayout.addComponent( textField, 0, 1 );
            valueLayout.addComponent( unitsLabel, 0, 2 );

            // Label+textfield+units above slider
            EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
            valueControl.setLayout( layout );
            layout.addComponent( valuePanel, 0, 0 );
            layout.addComponent( slider, 0, 1 );
        }
    }
}

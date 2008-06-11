package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.circuitconstructionkit.model.ResistivityManager;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jun 11, 2008
 * Time: 9:55:32 AM
 */
public class ResistivitySlider extends JPanel {
    private LinearValueControl control;

    public ResistivitySlider() {
//        super(min, max, label, textFieldPattern, units);

        //            resistivitySlider = new PhetSlider( CCKResources.getString( "CCK3ControlPanel.WireResistivitySlider" ),
//                                                CCKResources.getString( "CCK3ControlPanel.WireResistivitySliderMeasure" ),
//                                                ResistivityManager.DEFAULT_RESISTIVITY, 1, module.getResistivityManager().getResistivity(),
//                                                new DecimalFormat( "0.0000000" ) );
//            resistivitySlider.setBorder( null );
//            resistivitySlider.getTitleLabel().setFont( CCKLookAndFeel.getFont() );
//            resistivitySlider.setNumMajorTicks( 5 );
//            resistivitySlider.setNumMinorTicksPerMajorTick( 5 );
        super();
        control = new LinearValueControl(ResistivityManager.DEFAULT_RESISTIVITY, 1, CCKResources.getString("CCK3ControlPanel.WireResistivitySlider"), "0.000000", "");
        add(control);


        Font labelFont = new PhetFont(Font.PLAIN, 10);
        JLabel lowLabel = new JLabel(CCKResources.getString("CCK3ControlPanel.AlmostNoneLabel"));
        lowLabel.setFont(labelFont);
        JLabel highLabel = new JLabel(CCKResources.getString("CCK3ControlPanel.LotsLabel"));
        highLabel.setFont(labelFont);

//        Dictionary labels = new Hashtable();
//        labels.put(new Double(ResistivityManager.DEFAULT_RESISTIVITY), lowLabel);
//        labels.put(new Double(1), highLabel);

        control.addTickLabel(ResistivityManager.DEFAULT_RESISTIVITY, lowLabel);
        control.addTickLabel(1, highLabel);
        control.setTextFieldVisible(false);
//            resistivitySlider.setExtremumLabels( lowLabel, highLabel );
//            resistivitySlider.getTextField().setVisible( false );
//            resistivitySlider.getUnitsReadout().setVisible( false );
    }

    public double getValue() {
        return control.getValue();
    }

    public void addChangeListener(ChangeListener changeListener) {
        control.addChangeListener(changeListener);
    }
}

/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.chainreaction;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.view.LabeledNucleusNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class defines a subpanel that goes on the main control panel for the
 * chain reaction tab and controls various aspects of the chain reaction.
 *
 * @author John Blanco
 */
public class ChainReactionControlsSubPanel extends VerticalLayoutPanel {
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    private LinearValueControl _u235AmountControl;
    private LinearValueControl _u238AmountControl;
    private ChainReactionModel _model;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public ChainReactionControlsSubPanel(ChainReactionModel model) {
        
        _model = model;
        
        // Add the border around the sub panel.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysics2Resources.getString( "MultipleNucleusFissionControlPanel.ControlBorder" ),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetDefaultFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Add the slider that controls the number of U-235 nuclei that appear.
        _u235AmountControl = new LinearValueControl( 0, 100, "U-235", "###", "Nuclei" );
        _u235AmountControl.setValue( 0.0 );
        _u235AmountControl.setUpDownArrowDelta( 1 );
        _u235AmountControl.setTextFieldEditable( true );
        _u235AmountControl.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _u235AmountControl.setTickPattern( "0" );
        _u235AmountControl.setMajorTickSpacing( 25 );
        _u235AmountControl.setMinorTickSpacing( 12.5 );
        _u235AmountControl.setBorder( BorderFactory.createEtchedBorder() );
        _u235AmountControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int num = _model.setNumU235Nuclei( (int)Math.round(_u235AmountControl.getValue()) );
                _u235AmountControl.setValue( (double )num );
            }
        } );        
        add(_u235AmountControl);
        
        // Add the slider that controls the number of U-238 nuclei that appear.
        _u238AmountControl = new LinearValueControl( 0, 100, "U-238", "###", "Nuclei" );
        _u238AmountControl.setValue( 0.0 );
        _u238AmountControl.setUpDownArrowDelta( 1 );
        _u238AmountControl.setTextFieldEditable( true );
        _u238AmountControl.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _u238AmountControl.setTickPattern( "0" );
        _u238AmountControl.setMajorTickSpacing( 25 );
        _u238AmountControl.setMinorTickSpacing( 12.5 );
        _u238AmountControl.setBorder( BorderFactory.createEtchedBorder() );
        _u238AmountControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                // TODO: JPB TBD.
            }
        } );
        
        add(_u238AmountControl);

    }
}

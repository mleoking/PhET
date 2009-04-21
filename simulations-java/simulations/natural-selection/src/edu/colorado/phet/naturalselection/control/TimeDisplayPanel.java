/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

/**
 * Shows the current month and generation within the control panel
 *
 * @author Jonathan Olson
 */
public class TimeDisplayPanel extends JPanel implements NaturalSelectionModel.NaturalSelectionModelListener {

    // labels that will need to change when month or generation is changed
    private JLabel monthLabel;
    private JLabel generationLabel;

    public TimeDisplayPanel( NaturalSelectionModel _model ) {
        // get the starting month
        int startMonth = NaturalSelectionDefaults.START_MONTH;

        // layout and GUI fun

        monthLabel = new JLabel( NaturalSelectionModel.MONTH_NAMES[startMonth] );
        monthLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 10, 0 ) ) );
        monthLabel.setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );
        generationLabel = new JLabel( String.valueOf( startMonth ) );
        generationLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 10, 0 ) ) );
        generationLabel.setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );

        setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        add( new JLabel( "Month:" ), c );
        c.gridy++;
        add( monthLabel, c );
        c.gridy++;
        add( new JLabel( "Generation:" ), c );
        c.gridy++;
        add( generationLabel, c );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        _model.addListener( this );
    }

    public void setMonth( String monthName ) {
        monthLabel.setText( monthName );
    }

    public void setGeneration( int generation ) {
        generationLabel.setText( String.valueOf( generation ) );
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onMonthChange( String monthName ) {
        setMonth( monthName );
    }

    public void onGenerationChange( int generation ) {
        setGeneration( generation );
    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}

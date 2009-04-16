package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class TimeDisplayPanel extends JPanel implements NaturalSelectionModel.NaturalSelectionModelListener {
    private JLabel monthLabel;
    private JLabel generationLabel;

    public TimeDisplayPanel( NaturalSelectionModel _model ) {
        

        monthLabel = new JLabel( "Month: January" );
        add( monthLabel );
        generationLabel = new JLabel( "Generation: 0" );
        add( generationLabel );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        _model.addListener( this );
    }

    public void setMonth( String monthName ) {
        monthLabel.setText( "Month: " + monthName );
    }

    public void setGeneration( int generation ) {
        generationLabel.setText( "Generation: " + String.valueOf( generation ) );
    }

    public void onMonthChange( String monthName ) {
        setMonth( monthName );
    }

    public void onGenerationChange( int generation ) {
        setGeneration( generation );
    }
}

package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;

public class TimeDisplayPanel extends JPanel implements NaturalSelectionModel.NaturalSelectionModelListener {
    private JLabel monthLabel;
    private JLabel generationLabel;

    public TimeDisplayPanel( NaturalSelectionModel _model ) {

        monthLabel = new JLabel( "January" );
        monthLabel.setBorder( new EmptyBorder( new Insets( 0, 0, 10, 0 ) ) );
        monthLabel.setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );
        generationLabel = new JLabel( "0" );
        generationLabel.setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );

        /*
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( monthLabel );
        add( generationLabel );
        */

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

        setMinimumSize( new Dimension( 400, 0 ) );

        //setBorder( new LineBorder( Color.BLACK, 1 ) );

        _model.addListener( this );
    }

    public void setMonth( String monthName ) {
        monthLabel.setText( monthName );
    }

    public void setGeneration( int generation ) {
        generationLabel.setText( String.valueOf( generation ) );
    }

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

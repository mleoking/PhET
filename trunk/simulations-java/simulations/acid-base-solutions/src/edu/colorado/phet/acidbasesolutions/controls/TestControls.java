/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel that provides access to various tests.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestControls extends JPanel {
    
    private final ABSModel model;
    private final JRadioButton pHMeterRadioButton, pHPaperRadioButton, conductivityTesterRadioButton;
    private boolean isSyncingWithModel;
    
    public TestControls( final ABSModel model ) {
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.TESTS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        titledBorder.setBorder( ABSConstants.TITLE_BORDER_BORDER );
        setBorder( titledBorder );
        
        // model
        {
            this.model = model;
            isSyncingWithModel = false;

            model.getPHMeter().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    pHMeterRadioButton.setSelected( model.getPHMeter().isVisible() );
                }
            } );

            model.getPHPaper().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    pHPaperRadioButton.setSelected( model.getPHPaper().isVisible() );
                }
            } );

            model.getConductivityTester().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    conductivityTesterRadioButton.setSelected( model.getConductivityTester().isVisible() );
                }
            } );
        }
        
        // icons - clicking on these selects associated radio buttons
        JLabel pHMeterIcon = new JLabel( new ImageIcon( ABSImages.PH_METER_ICON ) );
        pHMeterIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                pHMeterRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
        JLabel pHPaperIcon = new JLabel( new ImageIcon( ABSImages.PH_PAPER_ICON ) );
        pHPaperIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                pHPaperRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
        JLabel conductivityTesterIcon = new JLabel( new ImageIcon( ABSImages.LIGHT_BULB_ICON ) );
        conductivityTesterIcon.addMouseListener( new MouseAdapter() {
            @Override 
            public void mousePressed( MouseEvent event ) {
                conductivityTesterRadioButton.setSelected( true );
                updateModel();
            }
        } );
        
        // radio buttons
        
        {
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    updateModel();
                }
            };
            
            pHMeterRadioButton = new JRadioButton( ABSStrings.PH_METER );
            pHMeterRadioButton.addActionListener( actionListener );

            pHPaperRadioButton = new JRadioButton( ABSStrings.PH_PAPER );
            pHPaperRadioButton.addActionListener( actionListener );

            conductivityTesterRadioButton = new JRadioButton( ABSStrings.CONDUCTIVITY );
            conductivityTesterRadioButton.addActionListener( actionListener );

            ButtonGroup group = new ButtonGroup();
            group.add( pHMeterRadioButton );
            group.add( pHPaperRadioButton );
            group.add( conductivityTesterRadioButton );
        }
        
        // layout
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( pHMeterRadioButton, row, column++ );
            layout.addComponent( pHMeterIcon, row++, column );
            column = 0;
            layout.addComponent( pHPaperRadioButton, row, column++ );
            layout.addComponent( pHPaperIcon, row++, column );
            column = 0;
            layout.addComponent( conductivityTesterRadioButton, row, column++ );
            layout.addComponent( conductivityTesterIcon, row++, column );
        }

        // default state
        {
            pHMeterRadioButton.setSelected( model.getPHMeter().isVisible() );
            pHPaperRadioButton.setSelected( model.getPHPaper().isVisible() );
            conductivityTesterRadioButton.setSelected( model.getConductivityTester().isVisible() );
        }
    }

    private void updateModel() {
        if ( !isSyncingWithModel ) {
            model.getPHMeter().setVisible( pHMeterRadioButton.isSelected() );
            model.getPHPaper().setVisible( pHPaperRadioButton.isSelected() );
            model.getConductivityTester().setVisible( conductivityTesterRadioButton.isSelected() );
        }
    }
}

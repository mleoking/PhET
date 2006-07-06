/**
 * Class: MultipleNucleusFissionControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 17, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.model.Uranium238;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MultipleNucleusFissionControlPanel extends JPanel {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Random random = new Random();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private MultipleNucleusFissionModule module;
    private MyModelSlider numU235Slider;
    private MyModelSlider numU238Slider;
    private JTextField percentDecayTF;
    private JButton resetBtn;

    /**
     * Constructor
     *
     * @param module
     */
    public MultipleNucleusFissionControlPanel( final MultipleNucleusFissionModule module ) {
        super();
        this.module = module;

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if( !( SwingUtilities.getWindowAncestor( MultipleNucleusFissionControlPanel.this ) == null ) ) {
                    SwingUtilities.getWindowAncestor( MultipleNucleusFissionControlPanel.this ).validate();
                }
            }
        } );

        // Add an element to the model that will update the spinner with the number of
        // nuclei
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                int modelNum = module.getU235Nuclei().size();
                int viewNum = (int)numU235Slider.getValue();
                if( modelNum != viewNum ) {
                    numU235Slider.setValue( module.getU235Nuclei().size() );
                }

                // Compute and display the number of U235 nuclei that have fissioned
                DecimalFormat percentFormat = new DecimalFormat( "#%" );
                double percent = 0;
                int startNumU235 = module.getStartingNumU235();
                if( startNumU235 != 0 ) {
                    percent = ( (double)( startNumU235 - modelNum ) ) / startNumU235;
                }
                percentDecayTF.setText( percentFormat.format( percent ) );

                modelNum = module.getU238Nuclei().size();
                viewNum = (int)numU238Slider.getValue();
                if( modelNum != viewNum ) {
                    numU238Slider.setValue( module.getU238Nuclei().size() );
                }
            }
        } );

        //--------------------------------------------------------------------------------------------------
        // Sliders that add and remove nuclei
        //--------------------------------------------------------------------------------------------------

        makeNewNucleusSliders();

        //--------------------------------------------------------------------------------------------------
        // Buttons
        //--------------------------------------------------------------------------------------------------

        resetBtn = new JButton( SimStrings.get( "MultipleNucleusFissionControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
                percentDecayTF.setText( "0" );
                numU235Slider.setValue( 1 );
                numU238Slider.setValue( 0 );
            }
        } );
        resetBtn.setEnabled( true );

        //--------------------------------------------------------------------------------------------------
        // Other controls
        //--------------------------------------------------------------------------------------------------

        percentDecayTF = new JTextField( 4 );
        percentDecayTF.setHorizontalAlignment( JTextField.RIGHT );
        percentDecayTF.setText( "0" );
        percentDecayTF.setEditable( false );
        percentDecayTF.setBackground( Color.white );

        // Layout the panel
        setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 5, 5, 5 ), 5, 5 );
        gbc.gridwidth = 2;
        add( numU235Slider, gbc );

        gbc.gridy++;
        add( numU238Slider, gbc );
        gbc.gridy++;
        gbc.gridwidth = 1;
        add( new JLabel( SimStrings.get( "MultipleNucleusFissionControlPanel.FissionPercentLabel" ), JLabel.RIGHT ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add( percentDecayTF, gbc );
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add( resetBtn, gbc );
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "MultipleNucleusFissionControlPanel.ControlBorder" ) );
        this.setBorder( titledBorder );
    }

    private void makeNewNucleusSliders() {
        if( numU235Slider != null ) {
            remove( numU235Slider );
        }
        if( numU238Slider != null ) {
            remove( numU238Slider );
        }

        DecimalFormat sliderFormat = new DecimalFormat( "#0" );
        numU235Slider = new MyModelSlider( "U235", "", 0, 100, 0, sliderFormat );
        numU235Slider.setPreferredSliderWidth( 150 );
        numU235Slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                    int numU235 = (int)numU235Slider.getValue();
                    setNumU235Nuclei( numU235 );
                }
        } );
        numU238Slider = new MyModelSlider( "U238", "", 0, 100, 0, sliderFormat );
        numU238Slider.setPreferredSliderWidth( 150 );
        numU238Slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                    int numU238 = (int)numU238Slider.getValue();
                    setNumU238Nuclei( numU238 );
            }
        } );
    }

    private void addNucleusSliders() {
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 5, 5, 5 ), 5, 5 );
        gbc.gridwidth = 2;
        add( numU235Slider, gbc );
        gbc.gridy++;
        add( numU238Slider, gbc );
    }

    private void setNumU235Nuclei( final int num ) {
            int delta = num - module.getU235Nuclei().size();
            for( int i = 0; i < delta; i++ ) {
                module.addU235Nucleus();
            }
            for( int i = 0; i < -delta; i++ ) {
                int numNuclei = module.getU235Nuclei().size();
                Uranium235 nucleus = (Uranium235)module.getU235Nuclei().get( random.nextInt( numNuclei ) );
                module.removeU235Nucleus( nucleus );
            }

            if( num != module.getU235Nuclei().size() ) {
                makeNewNucleusSliders();
                addNucleusSliders();
                numU235Slider.setValue( module.getU235Nuclei().size() );
                JOptionPane.showMessageDialog( MultipleNucleusFissionControlPanel.this,
                                               SimStrings.get( "MultipleNucleusFissionControlPanel.NucleusPlacementFailed" ) );
            }
    }

    private void setNumU238Nuclei( final int num ) {
        int delta = num - module.getU238Nuclei().size();
        for( int i = 0; i < delta; i++ ) {
            module.addU238Nucleus();
        }
        for( int i = 0; i < -delta; i++ ) {
            int numNuclei = module.getU238Nuclei().size();
            Uranium238 nucleus = (Uranium238)module.getU238Nuclei().get( random.nextInt( numNuclei ) );
            module.removeU238Nucleus( nucleus );
        }
        if( num != module.getU238Nuclei().size() ) {
            makeNewNucleusSliders();
            addNucleusSliders();
            numU238Slider.setValue( module.getU238Nuclei().size() );
            JOptionPane.showMessageDialog( MultipleNucleusFissionControlPanel.this,
                                           SimStrings.get( "MultipleNucleusFissionControlPanel.NucleusPlacementFailed" ) );
        }
    }

    /**
     * For debugging
     */
    class MyModelSlider extends ModelSlider {

        public MyModelSlider( String title, String units, final double min, final double max, double initial ) {
            super( title, units, min, max, initial );
        }

        public MyModelSlider( String title, String units, final double min, final double max, double initialValue, NumberFormat textFieldFormat ) {
            super( title, units, min, max, initialValue, textFieldFormat );
        }

        public MyModelSlider( String title, String units, final double min, final double max, double initialValue, NumberFormat textFieldFormat, NumberFormat sliderLabelFormat ) {
            super( title, units, min, max, initialValue, textFieldFormat, sliderLabelFormat );
        }

    }
}
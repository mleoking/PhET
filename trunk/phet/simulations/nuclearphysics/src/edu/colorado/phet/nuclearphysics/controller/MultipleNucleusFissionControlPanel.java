/**
 * Class: MultipleNucleusFissionControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 17, 2004
 */
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
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsModel;
import edu.colorado.phet.nuclearphysics.model.Neutron;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * The only way I could get the sliders to work properly with the code that limits the number of nuclei that can
 * be added was to remove the sliders and replace them with new ones whenever the slider is asking for more than
 * can be added.
 */
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
    private JTextField percentFissionedTF;
    private boolean percentFissionedTFEnabled;
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

                modelNum = module.getU238Nuclei().size();
                viewNum = (int)numU238Slider.getValue();
                if( modelNum != viewNum ) {
                    numU238Slider.setValue( module.getU238Nuclei().size() );
                }
            }
        } );

        // Add a listener to the model that will adjust the percentage fissioned text field when the numbers
        // of nuclei change.
        ( (NuclearPhysicsModel)module.getModel() ).addNucleusListener( new NuclearPhysicsModel.NucleusListener() {
            public void nucleusAdded( NuclearPhysicsModel.ChangeEvent event ) {
                if( percentFissionedTFEnabled ) {
                    setPercentFissionedTF();
                }
            }

            public void nucleusRemoved( NuclearPhysicsModel.ChangeEvent event ) {
                if( percentFissionedTFEnabled ) {
                    setPercentFissionedTF();
                }
            }

            void setPercentFissionedTF() {
                // Compute and display the number of U235 nuclei that have fissioned
                int modelNum = module.getU235Nuclei().size();
                DecimalFormat percentFormat = new DecimalFormat( "#%" );
                double percent = 0;
                int startNumU235 = module.getStartingNumU235();
                if( startNumU235 != 0 ) {
                    percent = ( (double)( startNumU235 - modelNum ) ) / startNumU235;
                }
                percentFissionedTF.setText( percentFormat.format( percent ) );
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
                percentFissionedTF.setText( "0%" );
                numU235Slider.setValue( 1 );
                numU238Slider.setValue( 0 );
            }
        } );
        resetBtn.setEnabled( true );

        //--------------------------------------------------------------------------------------------------
        // Other controls
        //--------------------------------------------------------------------------------------------------

        percentFissionedTF = new JTextField( 4 );
        percentFissionedTF.setHorizontalAlignment( JTextField.RIGHT );
        percentFissionedTF.setText( "0%" );
        percentFissionedTF.setEditable( false );
        percentFissionedTF.setBackground( Color.white );

        module.addNeutronFiredListener( new ChainReactionModule.NeutronFiredListener() {
            public void neutronFired( ChainReactionModule.NeutronFiredEvent event ) {
                percentFissionedTFEnabled = true;
            }
        } );

        //--------------------------------------------------------------------------------------------------
        // Layout the panel
        //--------------------------------------------------------------------------------------------------

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
        add( percentFissionedTF, gbc );
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
        numU235Slider = new MyModelSlider( "U-235", "", 0, 100, 0, sliderFormat );
        numU235Slider.setPreferredSliderWidth( 150 );
        numU235Slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int numU235 = (int)numU235Slider.getValue();
                setNumU235Nuclei( numU235 );

                NuclearPhysicsModel model = (NuclearPhysicsModel)module.getModel();
                java.util.List l = model.getNuclearModelElements();
//                for( int i = 0; i < l.size(); i++ ) {
//                    Object o = l.get( i );
//                    if( o instanceof Neutron ) {
//                        System.out.println( "MultipleNucleusFissionControlPanel.stateChanged" );
//                    }
//                }
            }
        } );
        numU235Slider.addMouseListener( new PercentFissionedTFDisabler() );


        numU238Slider = new MyModelSlider( "U-238", "", 0, 100, 0, sliderFormat );
        numU238Slider.setPreferredSliderWidth( 150 );
        numU238Slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int numU238 = (int)numU238Slider.getValue();
                setNumU238Nuclei( numU238 );
            }
        } );
        numU238Slider.getSlider().addMouseListener( new PercentFissionedTFDisabler() );
        numU238Slider.getTextField().addMouseListener( new PercentFissionedTFDisabler() );
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

        public synchronized void addMouseListener( MouseListener l ) {
            super.getTextField().addMouseListener( l );
            super.getSlider().addMouseListener( l );
        }

    }

    /**
     * Disables the percentFissionedTF and sets it to 0
     */
    private class PercentFissionedTFDisabler extends MouseAdapter {
        public void mousePressed( MouseEvent e ) {
            percentFissionedTF.setText( "0%" );
            percentFissionedTFEnabled = false;
        }
    }
}
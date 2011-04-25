// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.model.*;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPComboBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.view.BendingLightCanvas.labelFont;

/**
 * Controls for changing and viewing the medium type, including its current index of refraction (depends on the laser wavelength through the dispersion function).
 *
 * @author Sam Reid
 */
public class MediumControlPanel extends PNode {
    //Range of the index of refraction slider
    private static final int MIN = 1;
    private static final double MAX = 1.6;

    //Dummy state for putting the combo box in "custom" mode, meaning none of the other named substances are selected
    private final MediumState CUSTOM = new MediumState( BendingLightStrings.CUSTOM,
                                                        BendingLightModel.MYSTERY_B.getIndexOfRefractionForRedLight() + 1.2,//Higher value than could be shown on the slider
                                                        false,
                                                        true );
    private final Property<Medium> medium;//The medium to observe
    private final Property<Double> laserWavelength;

    public MediumControlPanel( final PhetPCanvas phetPCanvas,
                               final Property<Medium> medium,
                               final String name,
                               final boolean textFieldVisible,
                               final Property<Double> laserWavelength,
                               final String format,
                               final int columns ) {
        this.medium = medium;
        this.laserWavelength = laserWavelength;
        final MediumState initialMediumState = medium.getValue().getMediumState();

        //Create the top component which contains the title & combo box
        final PNode topComponent = new PNode() {{
            final PText materialLabel = new PText( name ) {{
                setFont( new PhetFont( labelFont.getSize(), true ) );
            }};

            //States to choose from (and indicate) in the combo box
            final Object[] mediumStates = new Object[] {
                    BendingLightModel.AIR,
                    BendingLightModel.WATER,
                    BendingLightModel.GLASS,
                    BendingLightModel.MYSTERY_A,
                    BendingLightModel.MYSTERY_B,
                    CUSTOM,
            };

            //Create and add the combo box
            final PComboBox comboBox = new PhetPComboBox( mediumStates ) {
                {
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            MediumState selected = (MediumState) getSelectedItem();
                            if ( !selected.custom ) {
                                setMediumState( selected, medium );
                            }
                        }
                    } );
                    updateComboBox();
                    medium.addObserver( new SimpleObserver() {
                        public void update() {
                            updateComboBox();
                        }
                    } );
                    setFont( labelFont );
                    setMediumState( initialMediumState, medium );
                }

                //Updates the combo box to show which item is selected
                private void updateComboBox() {
                    int selected = -1;
                    for ( int i = 0; i < mediumStates.length; i++ ) {
                        MediumState mediumState = (MediumState) mediumStates[i];
                        if ( mediumState.dispersionFunction.getIndexOfRefraction( laserWavelength.getValue() ) == medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ) {
                            selected = i;
                        }
                    }
                    if ( selected != -1 ) {
                        setSelectedIndex( selected );
                    }
                    else {
                        //No match to a named medium, so it must be a custom medium
                        setSelectedItem( CUSTOM );
                    }
                }
            };

            //Wrap the combo box in a PSwing
            final PSwing comboBoxPSwing = new PSwing( comboBox ) {{
                comboBox.setEnvironment( this, phetPCanvas );
                setOffset( materialLabel.getFullBounds().getMaxX() + 10, materialLabel.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 + 1 );
            }};

            //Add the label and combo box
            addChild( materialLabel );
            addChild( comboBoxPSwing );
        }};
        addChild( topComponent );

        //Many efforts were made to make this control work with LinearValueControl, including writing custom layouts.
        //However, for unknown reasons, some text was always clipped off, and we decided to proceed by doing the layout in Piccolo, which resolved the problem.
        final PNode slider = new PNode() {{
            //Show a label above the slider that reads "index of refraction" or a variant (when referring to air)
            final PNode sliderTopComponent = new PNode() {{
                final PText label = new PText( textFieldVisible ? BendingLightStrings.INDEX_OF_REFRACTION_COLON : BendingLightStrings.INDEX_OF_REFRACTION ) {{
                    setFont( BendingLightCanvas.labelFont );
                }};
                addChild( label );

                //If the text field is supposed to be shown, add a JTextField so the user can see and change the index of refraction
                System.out.println("textFieldVisible = " + textFieldVisible);
                if ( textFieldVisible ) {
                    addChild( new PSwing( new JTextField( new DecimalFormat( format ).format( medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ), columns ) {{
                        System.out.println("Entered initializer");
                        setFont( BendingLightCanvas.labelFont );

                        //Listen for when the user presses enter
                        addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                double value = Double.parseDouble( getText() );
                                if ( value > MIN && value < MAX ) {
                                    System.out.println("Wow!!!!");
                                    setCustomIndexOfRefraction( value );
                                }
                            }
                        } );

                        //Update the text readout when the medium or laser wavelength changes (since laser wavelength change could change the index of refraction where dispersion is modeled)
                        new RichSimpleObserver() {
                            public void update() {
                                setText( new DecimalFormat( format ).format( medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ) );
                            }
                        }.observe( medium, laserWavelength );
                    }} ) {{
                        //Put the text pswing to the right of the "index of refraction" label, and above the slider
                        setOffset( label.getFullBounds().getMaxX() + 10, label.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                    }} );
                }
            }};
            addChild( sliderTopComponent );

            /**
             * Label class for showing "low" and "high" to the sides of the slider in the prism break tab
             */
            class LowHighLabel extends JLabel {
                LowHighLabel( String text, boolean visible ) {
                    super( text );
                    setFont( new PhetFont( 14 ) );
                    setVisible( visible );
                }
            }

            //Add the piccolo node with the slider
            addChild( new PSwing( new JPanel() {{
                //Use a custom layout so that we can easily position the low and high labels aligned with the focus rectangle of the slider, so they
                //appear to the left and right of the slider thumb, not between the slider track and the slider labels
                setLayout( null );

                //Create the "low" and "high" labels
                final LowHighLabel lowLabel = new LowHighLabel( BendingLightStrings.LOW, !textFieldVisible );
                final LowHighLabel highLabel = new LowHighLabel( BendingLightStrings.HIGH, !textFieldVisible );

                //Create the slider
                final JSlider slider = new JSlider( 0, 10000 ) {{
                    final Function.LinearFunction mapping = new Function.LinearFunction( getMinimum(), getMaximum(), MIN, MAX );
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            if ( isFocusOwner() ) {//Only send events if caused by user, otherwise selecting "mystery b" causes buggy behavior
                                final double indexOfRefraction = mapping.evaluate( getValue() );
                                setCustomIndexOfRefraction( indexOfRefraction );
                            }
                        }
                    } );
                    new RichSimpleObserver() {
                        public void update() {
                            setValue( (int) mapping.createInverse().evaluate( medium.getValue().getIndexOfRefraction( laserWavelength.getValue() ) ) );
                        }
                    }.observe( medium, laserWavelength );
                    setPaintTicks( true );
                    setPaintLabels( true );
                    setLabelTable( new Hashtable<Object, Object>() {{
                        put( (int) mapping.createInverse().evaluate( BendingLightModel.AIR.getIndexOfRefractionForRedLight() ), new TickLabel( BendingLightStrings.AIR ) );
                        put( (int) mapping.createInverse().evaluate( BendingLightModel.WATER.getIndexOfRefractionForRedLight() ), new TickLabel( BendingLightStrings.WATER ) );
                        put( (int) mapping.createInverse().evaluate( BendingLightModel.GLASS.getIndexOfRefractionForRedLight() ), new TickLabel( BendingLightStrings.GLASS ) );
                    }} );
                    setPreferredSize( new Dimension( Math.max( (int) sliderTopComponent.getFullBounds().getWidth(), 200 ), getPreferredSize().height ) );
                }};
                lowLabel.setBounds( 0, 0, lowLabel.getPreferredSize().width, lowLabel.getPreferredSize().height );
                slider.setBounds( lowLabel.getPreferredSize().width, 0, slider.getPreferredSize().width, slider.getPreferredSize().height );
                highLabel.setBounds( lowLabel.getPreferredSize().width + slider.getPreferredSize().width, 0, highLabel.getPreferredSize().width, highLabel.getPreferredSize().height );

                //Add the slider and labels
                add( slider );
                add( lowLabel );
                add( highLabel );

                //Have to set the size of this component so that things are as close together as possible
                setPreferredSize( new Dimension( lowLabel.getPreferredSize().width + slider.getPreferredSize().width + highLabel.getPreferredSize().width, slider.getPreferredSize().height ) );
            }} ) {{
                setOffset( 0, sliderTopComponent.getFullBounds().getMaxY() );//Set the offset of the slider PSwing
            }} );
            setOffset( 0, sliderTopComponent.getFullBounds().getMaxY() + 10 );
            sliderTopComponent.setOffset( getFullBounds().getWidth() / 2 - sliderTopComponent.getFullBounds().getWidth() / 2, 0 );
        }};

        //Hide the slider for "mystery" substance
        medium.addObserver( new SimpleObserver() {
            public void update() {
                slider.setVisible( !medium.getValue().isMystery() );
            }
        } );

        //For "mystery" substance instead of slider show text "N Unknown"
        final PText unknown = new PText( BendingLightStrings.N_UNKNOWN ) {{
            setFont( labelFont );
            centerFullBoundsOnPoint( slider.getFullBounds().getCenterX(), slider.getFullBounds().getCenterY() );
            medium.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( medium.getValue().isMystery() );
                }
            } );
        }};

        //Rendering order
        addChild( slider );
        addChild( unknown );

        //Layout
        topComponent.setOffset( getFullBounds().getCenterX() - topComponent.getFullBounds().getWidth() / 2, 0 );
        topComponent.setOffset( getFullBounds().getCenterX() - topComponent.getFullBounds().getWidth() / 2, 0 );
    }

    //Called when the user enters a new index of refraction (with text box or slider), updates the model with the specified value
    private void setCustomIndexOfRefraction( double indexOfRefraction ) {
        //Have to pass the value through the dispersion function to account for the current wavelength of the laser (since index of refraction is a function of wavelength)
        final DispersionFunction dispersionFunction = new DispersionFunction( indexOfRefraction, laserWavelength.getValue() );
        medium.setValue( new Medium( medium.getValue().shape, new MediumState( BendingLightStrings.CUSTOM, dispersionFunction, false, false ), MediumColorFactory.getColor( dispersionFunction.getIndexOfRefractionForRed() ) ) );
    }

    //Update the medium state from the combo box
    private void setMediumState( MediumState mediumState, Property<Medium> medium ) {
        medium.setValue( new Medium( medium.getValue().shape, mediumState, MediumColorFactory.getColor( mediumState.getIndexOfRefractionForRedLight() ) ) );
    }
}
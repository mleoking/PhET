/**
 * Class: MultipleNucleusFissionControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 17, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;

public class MultipleNucleusFissionControlPanel extends JPanel {
    private static Random random = new Random();
    private MultipleNucleusFissionModule module;
    private JSpinner numNucleiSpinner;

    public MultipleNucleusFissionControlPanel( final MultipleNucleusFissionModule module ) {
        super();

        // Add an element to the model that will update the spinner with the number of
        // nuclei
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                numNucleiSpinner.setValue( new Integer( module.getNuclei().size() ) );
            }
        } );

        this.module = module;

        // Create the controls
        JButton fireNeutronBtn = new JButton( "Fire Neutron" );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
            }
        } );

//        JButton addNucleusBtn = new JButton( "Add Nucleus" );
//        addNucleusBtn.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                addNucleus();
//            }
//        } );
//
        SpinnerModel spinnerModel = new SpinnerNumberModel( 1, 0, 100, 1 );
        numNucleiSpinner = new JSpinner( spinnerModel );
        numNucleiSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumNuclei( ( (Integer)numNucleiSpinner.getValue() ).intValue() );
            }
        } );
        numNucleiSpinner.setPreferredSize( new Dimension( 80, 50 ) );
        Font spinnerFont = new Font( "SansSerif", Font.BOLD, 40 );
        numNucleiSpinner.setFont( spinnerFont );

        // Layout the panel
        setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, numNucleiSpinner,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, fireNeutronBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    private void setNumNuclei( int num ) {
        for( int i = 0; i < num - module.getNuclei().size(); i++ ) {
            addNucleus();
        }
        for( int i = 0; i < module.getNuclei().size() - num; i++ ) {
            removeNucleus();
        }
    }

    private void addNucleus() {
        double width = module.getApparatusPanel().getWidth();
        double height = module.getApparatusPanel().getHeight();
        boolean overlapping = false;
        Point2D.Double location = null;
        int attempts = 0;
        do {
            // If there is already a nucleus at (0,0), the generate a random location
            boolean centralNucleusExists = false;
            for( int i = 0; i < module.getNuclei().size() && !centralNucleusExists; i++ ) {
                Uranium235 testNucleus = (Uranium235)module.getNuclei().get( i );
                if( testNucleus.getLocation().getX() == 0 && testNucleus.getLocation().getY() == 0 ) {
                    centralNucleusExists = true;
                }
            }

            double x = centralNucleusExists ? random.nextDouble() * width / 2 * ( random.nextBoolean() ? 1 : -1 ) : 0;
            double y = centralNucleusExists ? random.nextDouble() * height / 2 * ( random.nextBoolean() ? 1 : -1 ) : 0;
            location = new Point2D.Double( x, y );

            overlapping = false;
            for( int j = 0; j < module.getNuclei().size() && !overlapping; j++ ) {
                Uranium235 testNucleus = (Uranium235)module.getNuclei().get( j );
                if( testNucleus.getLocation().distance( location ) < testNucleus.getRadius() * 3 ) {
                    overlapping = true;
                }
            }
            attempts++;
        } while( overlapping && attempts < 50 );

        if( attempts >= 50 ) {
            numNucleiSpinner.setEnabled( false );
            JOptionPane.showMessageDialog( SwingUtilities.getRoot( this ),
                                           "Unable to add any more nuclei", "Attention!",
                                           JOptionPane.INFORMATION_MESSAGE );
        }
        else {
            Uranium235 nucleus = new Uranium235( location );
            module.addNeucleus( nucleus );
        }
    }

    private void removeNucleus() {
        int numNuclei = module.getNuclei().size();
        Nucleus nucleus = (Nucleus)module.getNuclei().get( random.nextInt( numNuclei ) );
        module.removeNucleus( nucleus );
    }

    //
    // Inner classes
    //
    private class NucleusNumberPanel extends JPanel {

        public NucleusNumberPanel() {

            Integer value = new Integer( 0 );
            Integer min = new Integer( 0 );
            Integer max = new Integer( 20 );
            Integer step = new Integer( 1 );
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            final JSpinner numNucleiSpinner = new JSpinner( model );
//            numNucleiSpinner.setPreferredSize( new Dimension( 20, 5 ) );
            numNucleiSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setNumNuclei( ( (Integer)numNucleiSpinner.getValue() ).intValue() );
                }
            } );

            setLayout( new GridBagLayout() );
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, numNucleiSpinner,
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }
}

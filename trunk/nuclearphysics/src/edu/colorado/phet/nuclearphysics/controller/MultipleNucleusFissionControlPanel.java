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
import edu.colorado.phet.nuclearphysics.model.Uranium238;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;

public class MultipleNucleusFissionControlPanel extends JPanel {

    //
    // Static fields and methods
    //
    private static Random random = new Random();
    private static final int U235 = 1;
    private static final int U238 = 2;

    //
    // Instance fields and methods
    //
    private MultipleNucleusFissionModule module;
    private JSpinner numU235Spinner;
    private JSpinner numU238Spinner;
    private JTextField percentDecayTF;
    private int startNumU235;

    public MultipleNucleusFissionControlPanel( final MultipleNucleusFissionModule module ) {
        super();
        this.module = module;

        // Add an element to the model that will update the spinner with the number of
        // nuclei
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                int modelNum = module.getU235Nuclei().size();
                int viewNum = ( (Integer)numU235Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
//                    numU235Spinner.setValue( new Integer( module.getU235Nuclei().size() ) );
                }

                // Compute and display the number of U235 nuclei that have fissioned
                if( startNumU235 != 0 ) {
                    percentDecayTF.setText( Integer.toString( ( startNumU235 - modelNum ) * 100 / startNumU235 ) );
                }

                modelNum = module.getU238Nuclei().size();
                viewNum = ( (Integer)numU238Spinner.getValue() ).intValue();
                if( modelNum != viewNum ) {
//                    numU238Spinner.setValue( new Integer( module.getU238Nuclei().size() ) );
                }

            }
        } );


        // Create the controls
        JButton fireNeutronBtn = new JButton( "Fire Neutron" );
        fireNeutronBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.fireNeutron();
                percentDecayTF.setText( "0" );
                percentDecayTF.setEditable( false );
                percentDecayTF.setBackground( Color.white );
                startNumU235 = ( (Integer)numU235Spinner.getValue() ).intValue();
            }
        } );

        Font spinnerFont = new Font( "SansSerif", Font.BOLD, 40 );
        numU235Spinner = new JSpinner( new SpinnerNumberModel( 1, 0, 200, 1 ) );
        numU235Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumU235Nuclei( ( (Integer)numU235Spinner.getValue() ).intValue() );
            }
        } );
        numU235Spinner.setPreferredSize( new Dimension( 80, 30 ) );
        numU235Spinner.setFont( spinnerFont );

        numU238Spinner = new JSpinner( new SpinnerNumberModel( 0, 0, 200, 1 ) );
        numU238Spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumU238Nuclei( ( (Integer)numU238Spinner.getValue() ).intValue() );
            }
        } );
        numU238Spinner.setPreferredSize( new Dimension( 80, 30 ) );
        numU238Spinner.setFont( spinnerFont );

        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
                startNumU235 = 0;
                percentDecayTF.setText( "0" );
                numU235Spinner.setValue( new Integer( 1 ) );
                numU238Spinner.setValue( new Integer( 0 ) );
            }
        } );

        percentDecayTF = new JTextField( 4 );
        percentDecayTF.setHorizontalAlignment( JTextField.RIGHT );
        percentDecayTF.setText( "0" );

        // Layout the panel
        setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new JLabel( "<html><br>Number of<br><sup><font size=-1>235</font></sup>U nulcei</html>" ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, numU235Spinner,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( "<html><br>Number of<br><sup><font size=-1>238</font></sup>U nulcei</html></html>" ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, numU238Spinner,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( "<html><br>Percent <sup><font size=-1>235</font></sup>U<br> nuclei fissioned</html>" ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, percentDecayTF,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( "  " ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, fireNeutronBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( "  " ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, resetBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Controls" );
        this.setBorder( titledBorder );
    }

    private synchronized void setNumU235Nuclei( int num ) {
        int delta = num - module.getU235Nuclei().size();
        for( int i = 0; i < delta; i++ ) {
            Point2D.Double location = findLocationForNewNucleus();
            if( location != null ) {
                module.addU235Nucleus( new Uranium235( location, module.getModel() ) );
            }
        }
        for( int i = 0; i < -delta; i++ ) {
            int numNuclei = module.getU235Nuclei().size();
            Uranium235 nucleus = (Uranium235)module.getU235Nuclei().get( random.nextInt( numNuclei ) );
            module.removeU235Nucleus( nucleus );
        }
    }

    private void setNumU238Nuclei( int num ) {
        int delta = num - module.getU238Nuclei().size();
        for( int i = 0; i < delta; i++ ) {
            Point2D.Double location = findLocationForNewNucleus();
            if( location != null ) {
                module.addU238Nucleus( new Uranium238( location, module.getModel() ) );
            }
        }
        for( int i = 0; i < -delta; i++ ) {
            int numNuclei = module.getU238Nuclei().size();
            Uranium238 nucleus = (Uranium238)module.getU238Nuclei().get( random.nextInt( numNuclei ) );
            module.removeU238Nucleus( nucleus );
        }
    }

    private Point2D.Double findLocationForNewNucleus() {
        double width = module.getApparatusPanel().getWidth() / module.getPhysicalPanel().getScale();
        double height = module.getApparatusPanel().getHeight() / module.getPhysicalPanel().getScale();
        boolean overlapping = false;
        Point2D.Double location = new Point2D.Double();
        int attempts = 0;
        do {
            // If there is already a nucleus at (0,0), then generate a random location
            boolean centralNucleusExists = false;
            for( int i = 0; i < module.getNuclei().size() && !centralNucleusExists; i++ ) {
                Nucleus testNucleus = (Nucleus)module.getNuclei().get( i );
                if( testNucleus.getLocation().getX() == 0 && testNucleus.getLocation().getY() == 0 ) {
                    centralNucleusExists = true;
                }
            }

            double x = centralNucleusExists ? random.nextDouble() * width / 2 * ( random.nextBoolean() ? 1 : -1 ) : 0;
            double y = centralNucleusExists ? random.nextDouble() * height / 2 * ( random.nextBoolean() ? 1 : -1 ) : 0;
            location.setLocation( x, y );

            overlapping = false;
            for( int j = 0; j < module.getNuclei().size() && !overlapping; j++ ) {
                Nucleus testNucleus = (Nucleus)module.getNuclei().get( j );
                if( testNucleus.getLocation().distance( location ) < testNucleus.getRadius() * 3 ) {
                    overlapping = true;
                }
            }

            // todo: the hard-coded 50 here should be replaced with the radius of a Uranium nucleus
            if( location.getX() != 0 && location.getY() != 0 ) {
                overlapping = overlapping || module.getNeutronPath().ptSegDist( location ) < 50;
            }


            attempts++;
        } while( overlapping && attempts < 50 );

        if( overlapping ) {
            location = null;
        }
        return location;
    }
}

// Copyright 2002-2011, University of Colorado
package jass.render;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Controller extends javax.swing.JDialog {
    protected static final double MAX_SLIDERVAL = 10000;
    protected static final int NSLIDERS = 21; // defaults
    protected static final int NBUTTONS = 4; // defaults
    protected double[] val; //initial slider values
    protected String[] names; //slider labels
    protected double[] min, max; //slider ranges
    protected int nsliders = NSLIDERS;
    protected int nbuttons = NBUTTONS;

    /**
     * Set the values of the sliders and call its handlers.
     */
    public void setSliders( double[] val, double[] min, double[] max, String[] names ) {
        setValues( val, min, max, names );
        for( int i = 0; i < val.length; i++ ) {
            onSlider( i );
        }
    }


    /**
     * Set button names
     */
    public void setButtonNames( String[] names ) {
        for( int i = 0; i < names.length; i++ ) {
            setButtonName( names[i], i );
        }
    }


    /**
     * Set button name
     */
    public void setButtonName( String name, int k ) {
        jButton[k].setText( name );
    }

    /**
     * Set the values of the sliders (will not call its handlers)
     */
    protected void setValues( double[] val, double[] min, double[] max, String[] names ) {
        for( int i = 0; i < val.length; i++ ) {
            this.names[i] = names[i];
            this.val[i] = val[i];
            this.min[i] = min[i];
            this.max[i] = max[i];
            int x = (int)( MAX_SLIDERVAL * ( val[i] - min[i] ) / ( max[i] - min[i] ) );
            jSlider[i].setValue( x );
            jTextPane[i].setText( names[i] + new Double( val[i] ).toString() );
        }
    }

    private void initValues() {
        val = new double[nsliders];
        min = new double[nsliders];
        max = new double[nsliders];
        names = new String[nsliders];
        for( int i = 0; i < nsliders; i++ ) {
            names[i] = "-                         ";
            val[i] = .511131313131231231231231212312313;
            min[i] = 0;
            max[i] = 1;
        }
    }

    /**
     * Creates new form Controller with nsl sliders and nbut buttons
     */
    public Controller( java.awt.Frame parent, boolean modal, int nsl, int nbut ) {
        super( parent, parent.getTitle(), modal );
        this.nsliders = nsl;
        this.nbuttons = nbut;

        jSlider = new javax.swing.JSlider[nsliders];
        jTextPane = new javax.swing.JTextPane[nsliders];
        jButton = new javax.swing.JButton[nbuttons];

        initComponents();
        pack();
        // put incenter
        setLocationRelativeTo( null );
    }

    /**
     * Member class to handle slider events
     */
    class LabeledMouseMotionAdapter extends java.awt.event.MouseMotionAdapter {
        private int label;

        public LabeledMouseMotionAdapter( int label ) {
            super();
            this.label = label;
        }

        public void mouseDragged( java.awt.event.MouseEvent evt ) {
            jSliderMouseDragged( label, evt );
        }
    }

    /**
     * Member class to handle button events
     */
    class LabeledMouseAdapter extends java.awt.event.MouseAdapter {
        private int label;

        public LabeledMouseAdapter( int label ) {
            super();
            this.label = label;
        }

        public void mousePressed( java.awt.event.MouseEvent evt ) {
            jButtonMousePressed( label, evt );
        }
    }

    private void initComponents() {
        for( int i = 0; i < nsliders; i++ ) {
            jSlider[i] = new javax.swing.JSlider();
            jTextPane[i] = new javax.swing.JTextPane();
        }
        for( int i = 0; i < nbuttons; i++ ) {
            jButton[i] = new javax.swing.JButton();
        }
        getContentPane().setLayout( new java.awt.GridLayout( nsliders + ( nbuttons + 1 ) / 2, 2 ) );
        /*
        addWindowListener (new java.awt.event.WindowAdapter () {
            public void windowClosing (java.awt.event.WindowEvent evt) {
                closeDialog (evt);
                System.exit(0);
            }
        }
            );
        */

        for( int i = 0; i < nsliders; i++ ) {
            jSlider[i].setMaximum( (int)MAX_SLIDERVAL );
            getContentPane().add( jSlider[i] );
            jTextPane[i].setEditable( false );
            jTextPane[i].setText( "" );
            getContentPane().add( jTextPane[i] );
        }

        for( int i = 0; i < nsliders; i++ ) {
            jSlider[i].addMouseMotionListener( new LabeledMouseMotionAdapter( i ) );
        }

        for( int i = 0; i < nbuttons; i++ ) {
            jButton[i].addMouseListener( new LabeledMouseAdapter( i ) );
            getContentPane().add( jButton[i] );
        }

        initValues();
        setValues( val, min, max, names );
    }

    /**
     * Save slider states to file
     */
    public void saveToFile( String fn ) {
        try {
            BufferedWriter br = new BufferedWriter( new FileWriter( fn ) );
            double x = 0;
            for( int i = 0; i < nsliders; i++ ) {
                br.write( names[i] );
                br.newLine();
                br.write( new Double( val[i] ).toString() );
                br.newLine();
                br.write( new Double( min[i] ).toString() );
                br.newLine();
                br.write( new Double( max[i] ).toString() );
                br.newLine();
            }
            br.close();
        }
        catch( Exception e ) {
            System.out.println( this + " " + e );
        }
    }

    /**
     * Load slider states from file and call handlers
     */
    public void loadFromFile( String fn ) {
        try {
            BufferedReader br = new BufferedReader( new FileReader( fn ) );
            double x = 0;
            for( int i = 0; i < nsliders; i++ ) {
                names[i] = br.readLine();
                val[i] = (float)( new Double( br.readLine() ).doubleValue() );
                min[i] = (float)( new Double( br.readLine() ).doubleValue() );
                max[i] = (float)( new Double( br.readLine() ).doubleValue() );
            }
            br.close();
            setValues( val, min, max, names );
            for( int i = 0; i < nsliders; i++ ) {
                onSlider( i );
            }
        }
        catch( Exception e ) {
            System.out.println( this + " " + e );
        }
    }

    /**
     * User should override this handler
     */
    public void onSlider( int slider ) {
        ;
    }

    /**
     * User should override this handler
     */
    public void onButton( int button ) {
        ;
    }

    private void jButtonMousePressed( int k, java.awt.event.MouseEvent evt ) {
        onButton( k );
    }

    private void jSliderMouseDragged( int k, java.awt.event.MouseEvent evt ) {
        double sval = jSlider[k].getValue() / MAX_SLIDERVAL;
        val[k] = (float)( sval * ( max[k] - min[k] ) + min[k] );
        jTextPane[k].setText( names[k] + new Double( val[k] ).toString() );
        onSlider( k );
    }

    /**
     * Closes the dialog
     */
    private void closeDialog( java.awt.event.WindowEvent evt ) {
        setVisible( false );
        dispose();
    }

    protected javax.swing.JSlider[] jSlider;
    protected javax.swing.JTextPane[] jTextPane;
    protected javax.swing.JButton[] jButton;

}

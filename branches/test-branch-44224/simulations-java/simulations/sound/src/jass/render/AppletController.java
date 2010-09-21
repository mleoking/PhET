package jass.render;

import javax.swing.*;

/**
 * Applet derived class intended to be subclassed to create applet with sliders and buttons.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public abstract class AppletController extends JApplet {


    protected javax.swing.JSlider[] jSlider;
    protected javax.swing.JTextPane[] jTextPane;
    protected javax.swing.JButton[] jButton;

    protected abstract void setNSliders(); // {nsliders = 7; }

    protected abstract void setNButtons(); // {nbuttons = 2; }

    /**
     * Initializes the applet
     */
    public void init() {
        initComponents();
    }

    protected static final double MAX_SLIDERVAL = 10000;
    protected static final int NSLIDERS = 30;
    protected static final int NBUTTONS = 4;
    protected double[] val; //initial slider values
    protected String[] names; //slider labels
    protected double[] min, max; //slider ranges
    protected int nsliders = NSLIDERS;
    protected int nbuttons = NBUTTONS;

    /**
     * Set the slider values (do not call its handlers)
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

    /**
     * Init the slider variables
     */
    protected void initValues() {
        val = new double[nsliders];
        min = new double[nsliders];
        max = new double[nsliders];
        names = new String[nsliders];
        for( int i = 0; i < nsliders; i++ ) {
            names[i] = "-    ";
            val[i] = .5111313131;
            min[i] = 0;
            max[i] = 1;

        }
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


    /**
     * This method is called from within the init() method to
     * initialize the form.
     */
    protected void initComponents() {
        setNSliders();
        setNButtons();
        jSlider = new javax.swing.JSlider[nsliders];
        jTextPane = new javax.swing.JTextPane[nsliders];
        jButton = new javax.swing.JButton[nbuttons];

        for( int i = 0; i < nsliders; i++ ) {
            jSlider[i] = new javax.swing.JSlider();
            jTextPane[i] = new javax.swing.JTextPane();
        }
        for( int i = 0; i < nbuttons; i++ ) {
            jButton[i] = new javax.swing.JButton();
        }

        //getContentPane().setLayout(new java.awt.GridLayout(nsliders + (1 + nbuttons) / 2, 2));
        getContentPane().setLayout( new BoxLayout( getContentPane(), BoxLayout.Y_AXIS ) );

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
     * User should verride slider handler
     */
    protected void onSlider( int k ) {
        ;
    }

    /**
     * User should verride button handler
     */
    protected void onButton( int k ) {
        ;
    }


    protected void jButtonMousePressed( int k, java.awt.event.MouseEvent evt ) {
        onButton( k );
    }

    /**
     * Get slider values, scale, and call onSlider()
     */
    protected void jSliderMouseDragged( int k, java.awt.event.MouseEvent evt ) {
        double sval = jSlider[k].getValue() / MAX_SLIDERVAL;
        val[k] = (float)( sval * ( max[k] - min[k] ) + min[k] );
        jTextPane[k].setText( names[k] + new Double( val[k] ).toString() );
        onSlider( k );
    }
}


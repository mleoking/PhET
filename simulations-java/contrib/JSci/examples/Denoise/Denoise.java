import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import JSci.awt.DefaultGraph2DModel;
import JSci.maths.wavelet.FWTCoef;
import JSci.maths.wavelet.Signal;
import JSci.maths.wavelet.daubechies2.Daubechies2;
import JSci.maths.wavelet.daubechies3.Daubechies3;
import JSci.maths.wavelet.daubechies4.Daubechies4;
import JSci.maths.wavelet.daubechies5.Daubechies5;
import JSci.maths.wavelet.daubechies6.Daubechies6;
import JSci.maths.wavelet.daubechies7.Daubechies7;
import JSci.maths.wavelet.daubechies8.Daubechies8;
import JSci.swing.JGraphLayout;
import JSci.swing.JLineGraph;

/**
 * Example class for denoising a signal.
 * <p>
 * In this case an astronomical spectrum of a faint galaxy stored in
 * the file "galaxy.txt" is used. The denoised spectrum is written to
 * file "galaxy_denoise.txt" and both spectra are plotted in a graph.
 *
 * @authors Peter W. Draper (p.w.draper@durham.ac.uk).
 */
public class Denoise extends Frame
{
    public static void main( String[] args )
    {
        Denoise denoise = new Denoise();
    }

    protected Denoise()
    {
        super( "Denoise graphs" );
        addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent evt ) 
                {
                    dispose();
                    System.exit(0);
                }
            });
        setLayout( new GridLayout( 3, 1 ) );
        setSize( 400, 800 );

        if ( readData() ) {
            doCalcs();
            doDisplay();
            writeData();
        }
    }
    
    // Choose a filter to use.
    //private Daubechies8 filter = new Daubechies8();
    private Daubechies2 filter = new Daubechies2();
    //private Daubechies6 filter = new Daubechies6();
    //private Daubechies7 filter = new Daubechies7();
    //private Daubechies4 filter = new Daubechies4();

    /** Data read from input file, plus padding */
    private double[] noisy = null;

    /** Data after it has been denoised */
    private double[] clean = null;

    /** Offset into data array where real values start */
    private int offset = 0;

    /** Number of lines read from input file */
    private int nlines = 0;

    /** Power of 2 used to create data areas */
    private int maxlevel = 1;

    /** Name of input file */
    public static final String INFILE = "galaxy.txt";

    /** Name of output file */
    public static final String OUTFILE = "galaxy_denoise.txt";

    /** Denoise threshold (0-1). */
    public static double threshold = 0.5;

    /**
     * Read in the "noisy" data.
     */
    private boolean readData()
    {
        int filtertype = filter.getFilterType();

        //  Read the data from the text file. This just uses the first
        //  word from each line.
        try {
            BufferedReader r = new BufferedReader( new FileReader( INFILE ) );
            String raw = null;

            // Count lines.
            while ( ( raw = r.readLine() ) != null ) {
                nlines++;
            }
            r.close();

            //  Need a power of 2 for data size, plus padding for
            //  dyadic multiresolution scaling functions.
            maxlevel = 1;
            while ( nlines > Math.pow( 2.0, (double) maxlevel ) ) {
                maxlevel++;
            }
            int count = (int) Math.pow( 2.0, (double) maxlevel ) + filtertype;

            //  Now really read data. If buffered place near centre.
            r = new BufferedReader( new FileReader( INFILE ) );
            noisy = new double[count];
            offset = count = ( count - nlines ) / 2;
            StringTokenizer st = null;
            while ( ( raw = r.readLine() ) != null ) {
                st = new StringTokenizer( raw );
                noisy[count++] = Float.parseFloat( st.nextToken() );
            }
            r.close();

            // Fill any buffered regions with end values.
            double value = noisy[offset];
            for ( int i = 0; i < offset; i++ ) {
                noisy[i] = value;
            }
            value = noisy[offset + nlines - 1];
            for ( int i = offset + nlines; i < noisy.length; i++ ) {
                noisy[i] = value;
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** 
     * Determine the wavelet coefficient and generate a denoised
     * signal by setting some of the coefficients to zero and
     * re-generating.
     */
    private void doCalcs()
    {
        // Choose a maximum level and use that. Note 20 is max
        // possible, and we need to leave space for filtertype 
        // padding (-4).
        int level = Math.min( maxlevel - 4, 20 );

        // Make the Signal and filter it.
        Signal signal = new Signal( noisy );
        signal.setFilter( filter );
        FWTCoef signalCoeffs = signal.fwt( level );

        //  Zero any coefficients that are less than some fraction of
        //  the total sum.
        signalCoeffs.denoise( threshold );

        //  Rebuild the signal with the new set of coefficients.
        double[] rebuild = 
            signalCoeffs.rebuildSignal( filter ).evaluate( 0 );

        //  Trim padding away from all data.
        double[] trimmed = new double[nlines];
        System.arraycopy( noisy, offset, trimmed, 0, nlines );
        noisy = trimmed;
        clean = new double[nlines];
        System.arraycopy( rebuild, offset, clean, 0, nlines );
    }

    /**
     * Write the denoised data to an output file.
     */
    private void writeData()
    {
        try {
            PrintWriter r = new PrintWriter( new FileWriter( OUTFILE ) );
            for ( int i = 0; i < clean.length; i++ ) {
                r.println( clean[i] );
            }
            r.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Draw a graph showing the original data and the denoised version
     * for comparison.
     */
    private void doDisplay()
    {
        DefaultGraph2DModel model1 = new DefaultGraph2DModel();
        model1.setXAxis( 0.0f, (float) noisy.length, noisy.length );
        model1.addSeries( noisy );
        model1.addSeries( clean );
        model1.setSeriesVisible( 1, true );

        Panel panel1 = new Panel();
        panel1.setLayout( new JGraphLayout() );
        Label title = new Label( "Overlay graph", Label.CENTER );
        panel1.add( title, "Title" );
        JLineGraph graph1 = new JLineGraph( model1 );
        graph1.setColor(0, Color.red);
        graph1.setColor(1, Color.blue);
        panel1.add( graph1, "Graph" );
        add( panel1 );

        DefaultGraph2DModel model2 = new DefaultGraph2DModel();
        model2.setXAxis( 0.0f, (float) noisy.length, noisy.length );
        model2.addSeries( noisy );

        Panel panel2 = new Panel();
        panel2.setLayout( new JGraphLayout() );
        title = new Label( "Raw data", Label.CENTER );
        panel2.add( title, "Title" );
        JLineGraph graph2 = new JLineGraph( model2 );
        graph2.setColor(0, Color.red);
        panel2.add( graph2, "Graph" );
        add( panel2 );

        DefaultGraph2DModel model3 = new DefaultGraph2DModel();
        model3.setXAxis( 0.0f, (float) clean.length, clean.length );
        model3.addSeries( clean );

        Panel panel3 = new Panel();
        panel3.setLayout( new JGraphLayout() );
        title = new Label( "Denoised data", Label.CENTER );
        panel3.add( title, "Title" );
        JLineGraph graph3 = new JLineGraph( model3 );
        graph3.setColor(0, Color.blue);
        panel3.add( graph3, "Graph" );
        add( panel3 );

        setVisible( true );
    }
}

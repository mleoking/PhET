import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;


/**
 * Base class for this set of applets, incorporating miscellaneous helper
 * methods.
 * <p/>
 * <ul>
 * <li>Automatic double buffering (based on applet parameter)
 * <li>Automatic repainting and buffer adjustment on resize
 * <li>Fix for checkboxes in Mozilla
 * <li>Standard AppletInfo and parameterInfo methods
 * <li>Font changes (based on applet parameter)
 * </ul>
 */
public abstract class BaseApplet extends Applet implements ActionListener, ItemListener {
    /**
     * Performs extra intialisation specific to this class
     */
    public void init() {
        changeFont();
        resizeListener = new ResizeListener();
        String s = getParameter( "usebuffer" );
        if( s != null && ( s.equalsIgnoreCase( "no" ) || s.equalsIgnoreCase( "off" ) || s.equalsIgnoreCase( "n" ) ) ) {
            useBuffer = false;
        }
        else {
            useBuffer = true;
        }

        s = getParameter( "safety" );
        if( s != null ) {
            s = s.toLowerCase();
        }
        safetyLimit = !( s != null && ( s.equals( "no" ) || s.equals( "off" ) || s.equals( "n" ) ) );
    }


    /**
     * Applets should use this method to perform any final initialisation
     * necessary to &quot;activate&quot; the applet
     */
    public void start() {
        addComponentListener( resizeListener );
    }


    /**
     * When this method is called, the applet should cease any user
     * interaction.  In particular, any threads explicitly started should
     * be stopped, any external resources should be closed.  This method
     * should negate the action of {@link #start() start()}.
     */
    public void stop() {
        removeComponentListener( resizeListener );
    }


    /**
     * Subclasses should at least provide an empty implementation of this
     * method.
     */
    public abstract void actionPerformed( ActionEvent ae );


    /**
     * Default {@link ItemListener ItemListener} implementation, calls
     * {@link #actionPerformed(ActionEvent) actionPerformed(null)}
     */
    public void itemStateChanged( ItemEvent ie ) {
        actionPerformed( null );
    }


    /**
     * Used internally to read and apply font settings
     */
    protected void changeFont() {
        String fontName = getParameter( "fontname" );
        String fontSize = getParameter( "fontsize" );

        if( fontName == null || fontSize == null ) {
            return;
        }

        int size;
        try {
            size = Integer.parseInt( fontSize );
        }
        catch( NumberFormatException e ) {
            System.err.println( "Invalid font size specified - using default font" );
            return;
        }
        Font f = new Font( fontName, 0, size );
        setFont( f );
    }


    /**
     * Unless overridden, returns (C) 2002 University of Southampton, UK&quot;
     */
    public String getAppletInfo() {
        return "(C) 2002 University of Southampton, UK";
    }


    /**
     * Unless overridden, returns parameter descriptions for
     * <ul>
     * <li>fontname
     * <li>fontsize
     * <li>usebuffer
     * <li>safety
     * </ul>
     */
    public String[][] getParameterInfo() {
        return new String[][]{
                {"fontname", "String", "Name of font type to use"},
                {"fontsize", "int", "Size of font"},
                {"usebuffer", "String", "Set to \"no\" or \"off\" to disable buffering"},
                {"safety", "String", "Set to \"no\" or \"off\" to disable extra range checks"},
        };
    }


    /**
     * Invalidates the buffer contents and repaints the applet
     */
    public void refresh() {
        refresh = true;
        repaint();
        //paint(getGraphics());
    }

    /*
         Better like this

         Calls {@link #paint(Graphics) paint}(getGraphics()) immediately

     public void repaint()
     {
         paint(getGraphics());
     }*/


    /**
     * Method overridden to save clearing the screen.  Since we are pasting
     * the contents of a buffer, the prior state of the display area is
     * immaterial.
     */
    public void update( Graphics g ) {
        paint( g );
        //super.update(g);
    }


    /**
     * Paints the applet onto the specified <code>Graphics</code> object.
     * If buffering is enabled and the buffer has not been invalidated, then
     * the contents of the buffer will be painted.  Otherwise,
     * {@link #forcePaint(Graphics) forcePaint()} will be called to
     * regenerate the buffer.
     *
     * @param g The <code>Graphics</code> object on which to paint
     */
    public void paint( Graphics g ) {
        //	Don't want synchronous calls to doPaint()
        synchronized( this ) {
            if( g == null ) {
                return;
            }
            Image b = buffer;
            if( useBuffer && !refresh && b != null ) {
                g.drawImage( b, 0, 0, null );
            }
            else {
                forcePaint( g );
            }
        }
    }


    /**
     * Forces the buffer to be regenerated, then painted onto the specified
     * <code>Graphics</code> object.
     *
     * @param g The <code>Graphics</code> object on which to paint
     */
    public void forcePaint( Graphics g ) {
        if( useBuffer ) {
            Image b;
            if( buffer == null ) {
                buffer = createImage( getSize().width, getSize().height );
            }
            if( ( b = buffer ) != null ) {
                //	Don't need to sync for buffer, as ref to old one is OK
                Graphics _g = b.getGraphics();
                _g.setColor( getBackground() );
                _g.fillRect( 0, 0, getSize().width, getSize().height );
                doPaint( _g );
                refresh = false;
                g.drawImage( b, 0, 0, null );
                return;
            }
        }

        doPaint( g );
        refresh = false;
    }


    /**
     * Called by {@link #forcePaint(Graphics) forcePaint()} to actually
     * perform the painting.  Subclasses should implement this method as if
     * it were the standard <code>paint()</code> method; buffering will
     * then take place transparently.
     *
     * @param g The <code>Graphics</code> object on which to paint
     */
    protected abstract void doPaint( Graphics g );


    /**
     * The image used for buffering
     */
    protected Image buffer;
    /**
     * <code>true</code> if buffering is enabled, else <code>false</code>
     */
    protected boolean useBuffer;
    /**
     * <code>true</code> if the buffer has been invalidated, else <code>false</code>
     */
    protected boolean refresh = true;
    /**
     * <code>true</code> if extra range checks are enabled, else <code>false</code>
     */
    protected boolean safetyLimit = true;
    /**
     * Instance of {@link BaseApplet.ResizeListener ResizeListener} used to detect size changes
     */
    protected ResizeListener resizeListener;
    protected int GRAPH_MARGIN = 16;


    /**
     * Recreates buffer and repaints when size of applet is changed.  Most
     * browsers don't (can't) do this anyway, so it is only useful for
     * Sun's <code>appletviewer</code> tool.
     */
    protected class ResizeListener extends ComponentAdapter {
        public void componentResized( ComponentEvent ce ) {
            buffer = createImage( getSize().width, getSize().height );
            refresh();
        }
    }


    /**
     Manually adjusts selected checkbox in a <code>CheckboxGroup</code>,
     fixing a problem experienced with Mozilla.
     */
    protected class MozillaWorkaround extends MouseAdapter {
        public MozillaWorkaround( Checkbox cb, CheckboxGroup cbg ) {
            this.cb = cb;
            this.cbg = cbg;
            cb.addMouseListener( this );
        }

        public void mousePressed( MouseEvent me ) {
            cbg.setSelectedCheckbox( cb );
            actionPerformed( null );
        }

        protected Checkbox cb;
		protected CheckboxGroup cbg;
	}
}
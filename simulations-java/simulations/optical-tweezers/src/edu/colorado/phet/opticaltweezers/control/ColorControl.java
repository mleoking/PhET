/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ColorChooserFactory;

/**
 * ColorControl is a control for setting a color.
 * Clicking on the "color chip" opens a color chooser dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorControl extends HorizontalLayoutPanel implements ColorChooserFactory.Listener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension DEFAULT_CHIP_SIZE = new Dimension( 15, 15 );
    private static final Stroke CHIP_STROKE = new BasicStroke( 1f );
    private static final Color CHIP_STROKE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _labelString;
    private Color _color;
    private Dimension _chipSize;
    
    private JLabel _colorChip;
    private JDialog _colorChooserDialog;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ColorControl( String labelString, Color color ) {
        this( labelString, color, DEFAULT_CHIP_SIZE );
    }
    
    public ColorControl( String labelString, Color color, Dimension chipSize ) {
        super();
        
        _labelString = labelString;
        _color = color;
        _chipSize = new Dimension( chipSize );
        
        JLabel label = new JLabel( labelString );
        
        _colorChip = new JLabel();
        setColor( color );
        
        _colorChip.addMouseListener( new MouseInputAdapter() {
            public void mouseClicked( MouseEvent event ) {
                if ( event.getSource() instanceof JLabel ) {
                    openColorChooser();
                }
            }
        } );
        
        add( label );
        add( Box.createHorizontalStrut( 5 ) );
        add( _colorChip );
    }
    
    /**
     * Sets the color chip's color.
     * Subclasses can override this to add additional behavior.
     * 
     * @param color
     */
    protected void setColor( Color color ) {
        Rectangle r = new Rectangle( 0, 0, _chipSize.width, _chipSize.height );
        BufferedImage image = new BufferedImage( r.width, r.height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( color );
        g2.fill( r );
        g2.setStroke( CHIP_STROKE );
        g2.setColor( CHIP_STROKE_COLOR );
        g2.draw( r );
        _colorChip.setIcon( new ImageIcon( image ) );
    }
    
    /*
     * Opens the color chooser dialog.
     */
    private void openColorChooser() {
        closeColorChooser();
        Frame parent = PhetApplication.instance().getPhetFrame();
        _colorChooserDialog = ColorChooserFactory.createDialog( _labelString, parent, _color, this );
        _colorChooserDialog.show();
    }
    
    /*
     * Closes the color chooser dialog.
     */
    private void closeColorChooser() {
        if ( _colorChooserDialog != null ) {
            _colorChooserDialog.dispose();
        }
    }
    
    //----------------------------------------------------------------------------
    // ColorChooserFactory.Listener implementation
    //----------------------------------------------------------------------------
    
    public void colorChanged( Color color ) {
        setColor( color ); 
    }

    public void ok( Color color ) {
        setColor( color );  
    }

    public void cancelled( Color originalColor ) {
        setColor( originalColor );
    }
}

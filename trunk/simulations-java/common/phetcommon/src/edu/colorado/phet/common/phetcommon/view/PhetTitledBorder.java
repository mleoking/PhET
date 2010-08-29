package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * This is the default Border to be used in subsections of the control panel in phet simulations.
 * It provides the proper font and font style, and provides antialiasing and curved edges for the border.
 * See ticket #2476: https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/2476
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class PhetTitledBorder extends TitledBorder {
    public static final PhetFont DEFAULT_FONT = new PhetFont( Font.BOLD, PhetFont.getDefaultFontSize() + 4 );

    public PhetTitledBorder( String title ) {
        super( new PhetLineBorder(), title );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( String title, int titleJustification, int titlePosition ) {
        super( new PhetLineBorder(), title, titleJustification, titlePosition );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( String title, int titleJustification, int titlePosition, Font titleFont ) {
        super( new PhetLineBorder(), title, titleJustification, titlePosition, titleFont );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor ) {
        super( new PhetLineBorder(), title, titleJustification, titlePosition, titleFont, titleColor );
        init( DEFAULT_FONT );
    }

    //The following methods allow the client to specify an underlying border other than the default line border

    public PhetTitledBorder( Border border ) {
        super( border );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( Border border, String title ) {
        super( border, title );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( Border border, String title, int titleJustification, int titlePosition ) {
        super( border, title, titleJustification, titlePosition );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( Border border, String title, int titleJustification, int titlePosition, Font titleFont ) {
        super( border, title, titleJustification, titlePosition, titleFont );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( Border border, String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor ) {
        super( border, title, titleJustification, titlePosition, titleFont, titleColor );
        init( DEFAULT_FONT );
    }

    public PhetTitledBorder( String title, Font font ) {
        super( title );
        init( font );
    }

    private void init( Font font ) {
        setTitleFont( font );
    }

    @Override
    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
        //Overrides to add antialiasing, otherwise looks terrible on Windows
        Graphics2D g2 = (Graphics2D) g;
        Object oldAntialiasHint = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintBorder( c, g, x, y, width, height );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldAntialiasHint );
    }

    /**
     * This sample main demonstrates usage of the PhetTitledBorder
     *
     * @param args not used
     */
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final JPanel contentPane = new VerticalLayoutPanel();
        contentPane.setBorder( new PhetTitledBorder( "Border" ) );
        for ( int i = 0; i < 10; i++ ) {
            contentPane.add( new JLabel( "medium sized label " + i ) );
        }
        contentPane.add( new JButton( "A button" ) );
        frame.setContentPane( contentPane );
        frame.pack();
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
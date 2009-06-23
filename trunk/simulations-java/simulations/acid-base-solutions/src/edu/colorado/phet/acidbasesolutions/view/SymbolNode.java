package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Creates a symbol with optional superscripts and subscripts that can be aligned with other symbols.
 * <p>
 * Equations were originally implemented using HTMLNode.
 * But HTMLNode provides no information about baselines, making vertical alignment impossible.
 * And because baselines weren't aligned in equations, users were experiencing optical illusions
 * that made some symbols appear to be larger than others.
 * <p>
 * In this approach, we align the tops of uppercase letters with y=0.
 * But NOTE! This implementation is not intended to be general, it was chosen by 
 * the design team as a cost-feature tradeoff.  The assumptions made include:
 * <p>
 * <ul>
 * <li>symbols are specified as HTML or HTML fragments
 * <li>each symbol contains at least one uppercase character
 * <li>all uppercase characters have the same height
 * <li>characters have no ascenders
 * <li>all characters can be aligned on the cap height line
 * <li>y=0 corresponds to the cap height line (top of uppercase letters)
 * <li>'>' and '<' are used only for HTML tags
 * <li>HTML tags other than <html>, <sup>, <sub> are silently ignored
 * <li>all HTML tags are in lowercase, ie, <sub>, not <SUB>
 * * <p>
 * For an introduction to typography terms, see http://en.wikipedia.org/wiki/Baseline_(typography)
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SymbolNode extends PComposite {
    
    private static final double SCRIPT_SCALE = 0.8;
    private static final double X_SPACING = 0;
    
    private static final String HTML_BEGIN_TAG = "<html>";
    private static final String HTML_END_TAG = "</html>";
    private static final String SUPERSCRIPT_BEGIN_TAG = "<sup>";
    private static final String SUPERSCRIPT_END_TAG = "</sup>";
    private static final String SUBSCRIPT_BEGIN_TAG = "<sub>";
    private static final String SUBSCRIPT_END_TAG = "</sub>";
    
    private final Font font;
    private final Color color;
    private final double scriptScale;
    private double capHeight;
    
    public SymbolNode( String html, Font font, Color color ) {
        super();
        
        this.font = font;
        this.color = color;
        this.scriptScale = SCRIPT_SCALE;
        this.capHeight = 0;
        
        // convert HTML to nodes
        ArrayList<FragmentNode> nodes = htmlToNodes( html );
        
        // layout the nodes
        Iterator<FragmentNode> i = nodes.iterator();
        double xOffset = 0;
        while ( i.hasNext() ) {
            FragmentNode node = i.next();
            addChild( node );
            if ( node instanceof BodyNode ) {
                node.setOffset( xOffset, 0 );
                capHeight = node.getFullBoundsReference().getHeight();
            }
            else if ( node instanceof SuperscriptNode ) {
                node.setOffset( xOffset, -( node.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                node.setOffset( xOffset, ( node.getFullBoundsReference().getHeight() / 2 ) );
            }
            xOffset += node.getFullBoundsReference().getWidth() + X_SPACING;
        }
    }
    
    /**
     * Gets the height of capital (uppercase) letters.
     * @return
     */
    public double getCapHeight() {
        return capHeight;
    }
    
    /*
     * Converts HTML into an ordered list of nodes.
     */
    private ArrayList<FragmentNode> htmlToNodes( final String html ) {
        
        ArrayList<FragmentNode> nodeList = new ArrayList<FragmentNode>();
        
        String s = new String( html ); // operate on a copy
        
        // strip off html tags
        if ( s.startsWith( HTML_BEGIN_TAG ) ) {
            s = s.substring( HTML_BEGIN_TAG.length() );
        }
        if ( s.endsWith( HTML_END_TAG ) ) {
            s = s.substring( 0, s.length() - HTML_END_TAG.length() );
        }
        
        // convert html to nodes
        boolean done = false;
        String token = null;
        while ( !done ) {
            
            // look for the next tag of interest
            int index;
            final int subIndex = s.indexOf( SUBSCRIPT_BEGIN_TAG );
            final int supIndex = s.indexOf( SUPERSCRIPT_BEGIN_TAG );
            if ( subIndex == -1 ) {
                index = supIndex;
            }
            else if ( supIndex == -1 ) {
                index = subIndex;
            }
            else {
                index = Math.min( supIndex, subIndex );
            }
            
            if ( index == -1 ) {
                // if no tag, any remaining text is part of the symbol body
                if ( s.length() > 0 ) {
                    nodeList.add( new BodyNode( s, font, color ) );
                }
                done = true;
            }
            else {
                
                // anything before the tag of interest is part of the symbol body
                token = s.substring( 0, index );
                if ( token.length() > 0 ) {
                    nodeList.add( new BodyNode( token, font, color ) );
                }
                s = s.substring( index, s.length() );
                
                if ( s.startsWith( SUPERSCRIPT_BEGIN_TAG ) ) {
                    // superscript
                    index = s.indexOf( SUPERSCRIPT_END_TAG );
                    if ( index == -1 ) {
                        throw new UnsupportedOperationException( "malformed HTML, missing " + SUPERSCRIPT_END_TAG + ": " + s );
                    }
                    else {
                        token = s.substring( SUPERSCRIPT_BEGIN_TAG.length(), index );
                        nodeList.add( new SuperscriptNode( token, font, color, scriptScale ) );
                        s = s.substring( index + SUPERSCRIPT_END_TAG.length(), s.length() );
                    }
                }
                else if ( s.startsWith( SUBSCRIPT_BEGIN_TAG ) ) {
                    // subscript
                    index = s.indexOf( SUBSCRIPT_END_TAG );
                    if ( index == -1 ) {
                        throw new UnsupportedOperationException( "malformed HTML, missing " + SUBSCRIPT_END_TAG + ": " + s );
                    }
                    else {
                        token = s.substring( SUBSCRIPT_BEGIN_TAG.length(), index );
                        nodeList.add( new SubscriptNode( token, font, color, scriptScale ) );
                        s = s.substring( index + SUBSCRIPT_END_TAG.length(), s.length() );
                    }
                }
                else {
                    // unrecognized tags (eg, <i>) are skipped and included in the next token
                    s = s.substring( index + 1, s.length() );
                }
            }
        }
        
        return nodeList;
    }
    
    /*
     * Base class for rendering an HTML fragment.
     */
    private static abstract class FragmentNode extends HTMLNode {
        public FragmentNode( String text, Font font, Color color ) {
            super( HTMLUtils.toHTMLString( text ) );
            setFont( font );
            setHTMLColor( color );
        }
    }
    
    /*
     * A fragment that is part of the main body of the symbol.
     */
    private static class BodyNode extends FragmentNode {
        public BodyNode( String text, Font font, Color color ) {
            super( text, font, color );
            // verify that text contains at least one uppercase char
            if ( !text.matches( ".*[A-Z]+.*" ) ) {
                throw new IllegalArgumentException( "text must contain at least one uppercase char: " + text );
            }
        }
    }
    
    /*
     * A fragment that is a superscript.
     */
    private static class SuperscriptNode extends FragmentNode {
        public SuperscriptNode( String text, Font font, Color color, double scale ) {
            super( text, font, color );
            scale( scale );
        }
    }
    
    /*
     * A fragment that is a subscript.
     */
    private static class SubscriptNode extends FragmentNode {
        public SubscriptNode( String text, Font font, Color color, double scale ) {
            super( text, font, color );
            scale( scale );
        }
    }
    
    /* test */
    public static void main( String[] args ) {

        Font font = new PhetFont( Font.BOLD, 18 );
        ArrayList<PNode> nodes = new ArrayList<PNode>();
        nodes.add( new SymbolNode( ABSSymbols.H3O_PLUS, font, Color.RED ) );
        nodes.add( new SymbolNode( ABSSymbols.HA, font, Color.GREEN ) );
        nodes.add( new SymbolNode( ABSSymbols.H2O, font, Color.BLUE ) );
        nodes.add( new SymbolNode( ABSSymbols.C5H5NH_PLUS, font, Color.BLACK ) );
        nodes.add( new SymbolNode( ABSSymbols.HCl, font, Color.BLACK ) );
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 400 ) );
        
        double xOffset = 20;
        double yOffset = 100;
        Iterator<PNode> i = nodes.iterator();
        while ( i.hasNext() ) {
            PNode node = i.next();
            canvas.getLayer().addChild( node );
            node.setOffset( xOffset, yOffset );
            xOffset = node.getFullBoundsReference().getMaxX() + 10;
        }
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

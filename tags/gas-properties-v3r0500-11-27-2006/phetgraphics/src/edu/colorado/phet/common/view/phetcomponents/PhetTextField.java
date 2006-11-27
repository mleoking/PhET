package edu.colorado.phet.common.view.phetcomponents;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 24, 2004
 * Time: 5:34:05 PM
 * To change this template use File | Settings | File Templates.
 *
 * @deprecated Use PhetJComponent with a TextField.
 */
public class PhetTextField extends CompositePhetGraphic {
    private PhetTextGraphic textGraphic;
    private FontRenderContext fontRenderContext;
    private CaretGraphic caretGraphic;
    private PhetShapeGraphic backgroundGraphic;
    private int maxChars = 8;
    private Timer blinkTimer;

    public PhetTextField( Component component, Font font, String text, Color color, int x, int y ) {
        super( component );
        textGraphic = new PhetTextGraphic( component, font, text, color, x, y );
        addKeyListener( new KeyListener() {
            public void keyTyped( KeyEvent e ) {
            }

            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                String text = textGraphic.getText();
                if( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
                    if( text.length() > 0 ) {
                        text = text.substring( 0, text.length() - 1 );
                        textGraphic.setText( text );
                        move( -1 );
                        update();
                    }
                }
                else if( e.getKeyCode() == KeyEvent.VK_LEFT ) {
                    move( -1 );
                    update();
                }
                else if( e.getKeyCode() == KeyEvent.VK_RIGHT ) {
                    move( 1 );
                    update();
                }
                else {
                    text += e.getKeyChar();
                    textGraphic.setText( text );
                    move( 1 );
                    update();
                }

            }
        } );

        backgroundGraphic = new PhetShapeGraphic( component, textGraphic.getLocalBounds(), Color.white, new BasicStroke( 1.0f ), Color.black );
        fontRenderContext = new FontRenderContext( new AffineTransform(), true, false );
        addGraphic( backgroundGraphic );
        addGraphic( textGraphic );
        caretGraphic = new CaretGraphic( component );
        addGraphic( caretGraphic );
        blinkTimer = new Timer( 500, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                blink();
            }
        } );
//        blinkTimer.start();
        move( text.length() );
        update();
        caretGraphic.setVisible( false );
    }

    public void lostKeyFocus() {
        blinkTimer.stop();
        caretGraphic.setVisible( false );
    }

    public void gainedKeyFocus() {
        blinkTimer.start();
        caretGraphic.setVisible( true );
    }

    private void update() {
        Rectangle background = textGraphic.getFont().getStringBounds( "000000", fontRenderContext ).getBounds();
        background.x = 0;
        background.y = 0;
        background = RectangleUtils.expand( background, 5, 5 );
        backgroundGraphic.setShape( background );
    }

    private void move( int dx ) {
        caretGraphic.move( dx );
    }

    private void blink() {
        caretGraphic.setVisible( !caretGraphic.isVisible() );
    }

    public class CaretGraphic extends CompositePhetGraphic {
        private TextLayout textLayout;
        private PhetShapeGraphic graphic;
        private int caretLocation;

        public CaretGraphic( Component component ) {
            super( component );
            BasicStroke strkoe = new BasicStroke( 1.0f );
            graphic = new PhetShapeGraphic( component, null, Color.blue, strkoe, Color.black );
            addGraphic( graphic );
            updateCaret();
        }

        private void updateCaret() {
            if( textGraphic.getText().length() > 0 ) {

                TextLayout textLayout = new TextLayout( textGraphic.getText(), textGraphic.getFont(), fontRenderContext );
                Shape[] shape = textLayout.getCaretShapes( caretLocation );
                Shape sh = shape[0];

                //line up the bottoms
                Rectangle loc = textGraphic.getLocalBounds();
                int textBottom = loc.y + loc.height;
                int cursorBottom = sh.getBounds().y + sh.getBounds().height;
                int dx = textBottom - cursorBottom;
                graphic.setTransform( AffineTransform.getTranslateInstance( 0, dx ) );
                graphic.setShape( sh );
                setBoundsDirty();
                autorepaint();
            }
        }

        public void move( int dx ) {
            int newVal = caretLocation + dx;
            if( newVal < 0 ) {
                newVal = 0;
            }
            if( newVal >= textGraphic.getText().length() ) {
                newVal = textGraphic.getText().length();
            }
            caretLocation = newVal;
            updateCaret();
        }

    }
}

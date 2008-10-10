package phet;

import geom.jgv.controller.JGVController;
import geom.jgv.gui.CameraCanvas;
import geom.jgv.model.Geom;
import geom.jgv.view.BSPTree;

import java.io.ByteArrayInputStream;

import javax.swing.*;

import org.sjg.xml.Document;
import org.sjg.xml.Parser;


public class ReidTest extends JFrame {
    private CameraCanvas cameraCanvas;

    public ReidTest() throws Exception {
        super( "Reid Test" );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( 640, 480 );

        cameraCanvas = new CameraCanvas();
//        JGVPanel jgvPanel = new JGVPanel( this );
        setContentPane( cameraCanvas );

        Document dom = Parser.parse( new ByteArrayInputStream( cubeXML.getBytes() ) );
        world = Geom.parse( dom );
        if ( world != null ) {
            recalcWorld();
        }
        JGVController jgvController = new JGVController( null );
        jgvController.setCamera( cameraCanvas );
    }

    Geom world = new Geom();
    public boolean debug = false;

    public void recalcWorld() {
        BSPTree bsp = new BSPTree();
        bsp.debug = this.debug;
        bsp.build( world );
        cameraCanvas.setTree( bsp );
        cameraCanvas.repaint();
    }

    public static void main( String[] args ) throws Exception {
        new ReidTest().setVisible( true );
    }

    String cubeXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                     "<geom>\n" +
                     "  <geom-inst node=\"4cubes\">\n" +
                     "    <transform>\n" +
                     "       .5   0   0  0\n" +
                     "        0  .5   0  0\n" +
                     "        0   0  .5  0\n" +
                     "       .5  .5   0  1\n" +
                     "    </transform>\n" +
                     "    <transform>\n" +
                     "       .5   0   0  0\n" +
                     "        0  .5   0  0\n" +
                     "        0   0  .5  0\n" +
                     "       .5  -.5   0  1\n" +
                     "    </transform>\n" +
                     "    <transform>\n" +
                     "       .5   0   0  0\n" +
                     "        0  .5   0  0\n" +
                     "        0   0  .5  0\n" +
                     "       -.5  .5   0  1\n" +
                     "    </transform>\n" +
                     "    <transform>\n" +
                     "       .5   0   0  0\n" +
                     "        0  .5   0  0\n" +
                     "        0   0  .5  0\n" +
                     "       -.5  -.5   0  1\n" +
                     "    </transform>\n" +
                     "  <geom-off vertices=\"8\" faces=\"6\" edges=\"12\">\n" +
                     "    <vertices>\n" +
                     "      <vertex index=\"0\" x=\"-0.5\" y=\"-0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"1\" x=\"0.5\" y=\"-0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"2\" x=\"0.5\" y=\"0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"3\" x=\"-0.5\" y=\"0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"4\" x=\"-0.5\" y=\"-0.5\" z=\"0.5\"/>\n" +
                     "      <vertex index=\"5\" x=\"0.5\" y=\"-0.5\" z=\"0.5\"/>\n" +
                     "      <vertex index=\"6\" x=\"0.5\" y=\"0.5\" z=\"0.5\"/>\n" +
                     "      <vertex index=\"7\" x=\"-0.5\" y=\"0.5\" z=\"0.5\"/>\n" +
                     "    </vertices>\n" +
                     "    <faces>\n" +
                     "      <face>\n" +
                     "        <vertices>3 2 1 0</vertices>\n" +
                     "\t<color r=\"0.800\" g=\"0.098\" b=\"0.098\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>4 5 6 7</vertices>\n" +
                     "\t<color r=\"0.098\" g=\"0.647\" b=\"0.400\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>2 3 7 6</vertices>\n" +
                     "\t<color r=\"0.098\" g=\"0.098\" b=\"0.800\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>0 1 5 4</vertices>\n" +
                     "\t<color r=\"0.898\" g=\"0.600\" b=\"0.000\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>0 4 7 3</vertices>\n" +
                     "\t<color r=\"0.000\" g=\"0.600\" b=\"0.800\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>1 2 6 5</vertices>\n" +
                     "\t<color r=\"0.498\" g=\"0.000\" b=\"0.898\"/>\n" +
                     "      </face>\n" +
                     "    </faces>\n" +
                     "  </geom-off>\n" +
                     "  </geom-inst>\n" +
                     "  <geom-inst node=\"1cube\">\n" +
                     "    <pickable>\n" +
                     "      <action type=\"URL\">http://www.msnbc.com/</action>\n" +
                     "    </pickable>\n" +
                     "    <transform>\n" +
                     "       .3   0   0  0\n" +
                     "        0  .3   0  0\n" +
                     "        0   0  .3  0\n" +
                     "        0   0  -1  1\n" +
                     "    </transform>\n" +
                     "  <geom-off vertices=\"8\" faces=\"6\" edges=\"12\">\n" +
                     "    <vertices>\n" +
                     "      <vertex index=\"0\" x=\"-0.5\" y=\"-0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"1\" x=\"0.5\" y=\"-0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"2\" x=\"0.5\" y=\"0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"3\" x=\"-0.5\" y=\"0.5\" z=\"-0.5\"/>\n" +
                     "      <vertex index=\"4\" x=\"-0.5\" y=\"-0.5\" z=\"0.5\"/>\n" +
                     "      <vertex index=\"5\" x=\"0.5\" y=\"-0.5\" z=\"0.5\"/>\n" +
                     "      <vertex index=\"6\" x=\"0.5\" y=\"0.5\" z=\"0.5\"/>\n" +
                     "      <vertex index=\"7\" x=\"-0.5\" y=\"0.5\" z=\"0.5\"/>\n" +
                     "    </vertices>\n" +
                     "    <faces>\n" +
                     "      <face>\n" +
                     "        <vertices>3 2 1 0</vertices>\n" +
                     "\t<color r=\"0.800\" g=\"0.098\" b=\"0.098\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>4 5 6 7</vertices>\n" +
                     "\t<color r=\"0.098\" g=\"0.647\" b=\"0.400\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>2 3 7 6</vertices>\n" +
                     "\t<color r=\"0.098\" g=\"0.098\" b=\"0.800\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>0 1 5 4</vertices>\n" +
                     "\t<color r=\"0.898\" g=\"0.600\" b=\"0.000\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>0 4 7 3</vertices>\n" +
                     "\t<color r=\"0.000\" g=\"0.600\" b=\"0.800\"/>\n" +
                     "      </face>\n" +
                     "      <face>\n" +
                     "        <vertices>1 2 6 5</vertices>\n" +
                     "\t<color r=\"0.498\" g=\"0.000\" b=\"0.898\"/>\n" +
                     "      </face>\n" +
                     "    </faces>\n" +
                     "  </geom-off>\n" +
                     "  </geom-inst>\n" +
                     "  <geom-annotation on=\"cube\" x=\".5\" y=\"0.7\" z=\".3\" r=\"1\" g=\"1\" b=\"1\">\n" +
                     "    <note>All paths lead to GOOGLE</note>\n" +
                     "    <pickable>\n" +
                     "      <action type=\"URL\">http://www.google.com/</action>\n" +
                     "    </pickable>\n" +
                     "  </geom-annotation>\n" +
                     "  <geom-annotation on=\"cube\" x=\"-.5\" y=\"-0.7\" z=\".3\" r=\"1\" g=\"1\" b=\"0\" node=\"annotation1\">\n" +
                     "    <note>Menu of things to do</note>\n" +
                     "    <pickable>\n" +
                     "      <action type=\"menu\">\n" +
                     "        <item index=\"0\" label=\"Goto MSNBC\">\n" +
                     "          <action type=\"URL\">http://www.msnbc.com/</action>\n" +
                     "        </item>\n" +
                     "        <item index=\"1\" label=\"Goto Daeron.com\">\n" +
                     "          <action type=\"URL\">http://www.daeron.com/</action>\n" +
                     "        </item>\n" +
                     "        <item index=\"2\" label=\"Load Car\">\n" +
                     "          <action type=\"load-geom\">car-outline.xml</action>\n" +
                     "        </item>\n" +
                     "        <item index=\"3\" label=\"Hide Cube Faces\">\n" +
                     "          <action type=\"appearance\" on=\"4cubes\">\n" +
                     "            <drawface>false</drawface>\n" +
                     "          </action>\n" +
                     "        </item>\n" +
                     "        <item index=\"4\" label=\"Show Cube Faces\">\n" +
                     "          <action type=\"appearance\" on=\"4cubes\">\n" +
                     "            <drawface>true</drawface>\n" +
                     "          </action>\n" +
                     "        </item>\n" +
                     "        <item index=\"5\" label=\"Hide Cubes Edges\">\n" +
                     "          <action type=\"appearance\" on=\"4cubes\">\n" +
                     "            <drawedge>false</drawedge>\n" +
                     "          </action>\n" +
                     "        </item>\n" +
                     "        <item index=\"6\" label=\"Show Cubes Edges\">\n" +
                     "          <action type=\"appearance\" on=\"4cubes\">\n" +
                     "            <drawedge>true</drawedge>\n" +
                     "          </action>\n" +
                     "        </item>\n" +
                     "        <item index=\"7\" label=\"Get Info\">\n" +
                     "          <action type=\"server\" label=\"Get Info\" p=\"abc\"/>\n" +
                     "        </item>\n" +
                     "      </action>\n" +
                     "    </pickable>\n" +
                     "  </geom-annotation>\n" +
                     "</geom>";
}
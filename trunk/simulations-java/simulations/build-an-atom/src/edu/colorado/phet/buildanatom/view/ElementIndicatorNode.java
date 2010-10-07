package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows periodic table, etc
 *
 * @author Sam Reid
 */
public class ElementIndicatorNode extends PNode {
    public ElementIndicatorNode( final Atom atom ) {
        final PText elementNameTextNode = new PText( atom.getName() ) {{
            setFont( BuildAnAtomConstants.READOUT_FONT );
            setTextPaint( Color.red );
        }};
        //See http://www.ptable.com/
        final PNode table = new PNode();
        for ( int i = 1; i <= 112; i++ ) {
            PNode elementCell = new ElementCell( atom, i );
            final Point gridPoint = getGridPoint( i );
            double x = ( gridPoint.getY() - 1 ) * CELL_DIMENSION;     //expansion cells render as "..." on top of each other
            double y = ( gridPoint.getX() - 1 ) * CELL_DIMENSION;
            elementCell.setOffset( x, y );
            table.addChild( elementCell );
        }
        final SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                elementNameTextNode.setText( atom.getName() );
                elementNameTextNode.setOffset( table.getFullBounds().getWidth() / 2 - elementNameTextNode.getFullBounds().getWidth() / 2, 0 );
            }
        };
        atom.addObserver( updateText );
        updateText.update();
        addChild( table );
        addChild( elementNameTextNode );
    }

    //Reports (row,column) on the grid, with a 1-index

    private Point getGridPoint( int i ) {
        //http://www.ptable.com/ was useful here
        if ( i == 1 ) {
            return new Point( 1, 1 );
        }
        if ( i == 2 ) {
            return new Point( 1, 18 );
        }
        else if ( i == 3 ) {
            return new Point( 2, 1 );
        }
        else if ( i == 4 ) {
            return new Point( 2, 2 );
        }
        else if ( i >= 5 && i <= 10 ) {
            return new Point( 2, i + 8 );
        }
        else if ( i == 11 ) {
            return new Point( 3, 1 );
        }
        else if ( i == 12 ) {
            return new Point( 3, 2 );
        }
        else if ( i >= 13 && i <= 18 ) {
            return new Point( 3, i );
        }
        else if ( i >= 19 && i <= 36 ) {
            return new Point( 4, i - 18 );
        }
        else if ( i >= 37 && i <= 54 ) {
            return new Point( 5, i - 36 );
        }
        else if ( i >= 19 && i <= 36 ) {
            return new Point( 4, i - 36 );
        }
        else if ( i == 55 ) {
            return new Point( 6, 1 );
        }
        else if ( i == 56 ) {
            return new Point( 6, 2 );
        }
        else if ( i >= 57 && i <= 71 ) {
            return new Point( 6, 3 );
        }
        else if ( i >= 72 && i <= 86 ) {
            return new Point( 6, i - 68 );
        }
        else if ( i == 87 ) {
            return new Point( 7, 1 );
        }
        else if ( i == 88 ) {
            return new Point( 7, 2 );
        }
        else if ( i >= 89 && i <= 103 ) {
            return new Point( 7, 3 );
        }
        else if ( i >= 104 && i <= 118 ) {
            return new Point( 7, i - 100 );
        }
        return new Point( 1, 1 );
    }

    public static int CELL_DIMENSION = 20;

    public String getLine( int atomicNumber ) {
        StringTokenizer stringTokenizer = new StringTokenizer( table, "\n" );
        while ( stringTokenizer.hasMoreElements() ) {
            String element = stringTokenizer.nextToken();
            if ( element.startsWith( atomicNumber + "" ) ) {
                return element;
            }
        }
        return null;
    }

    private class ElementCell extends PNode {
        public ElementCell( final Atom atom, final int atomicNumber ) {
            final PhetPPath box = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_DIMENSION, CELL_DIMENSION ), new BasicStroke( 1 ), Color.black );
            addChild( box );

            String line = getLine( atomicNumber );
            StringTokenizer stringTokenizer = new StringTokenizer( line, "\t " );
            stringTokenizer.nextToken();//number
            stringTokenizer.nextToken();//name
            String abbreviation = stringTokenizer.nextToken();//abbreviation

            if ( atomicNumber >= 57 && atomicNumber <= 71 ) {
                abbreviation = "...";
            }
            if ( atomicNumber >= 89 && atomicNumber <= 103 ) {
                abbreviation = "...";
            }
            final PText text = new PText( abbreviation );
            text.setOffset( box.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2, box.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
            addChild( text );

            atom.addObserver( new SimpleObserver() {
                public void update() {
                    boolean match = atom.getNumProtons() == atomicNumber;
                    text.setFont( new PhetFont( PhetFont.getDefaultFontSize(), match ) );
                    Color paint = null;
                    if (match) paint =Color.white;
                    else if (atomicNumber<=10) paint = new Color( 129,143,224);
                    box.setPaint( paint );
                }
            } );
        }
    }
//Copied table from http://www.zyra.org.uk/elements.htm
    String table = "1  \tHYDROGEN  \tH  \t1.008\n" +
                   "2 \tHELIUM \tHe \t4.003\n" +
                   "3 \tLITHIUM \tLi \t6.939\n" +
                   "4 \tBERYLLIUM \tBe \t9.012\n" +
                   "5 \tBORON \tB \t10.811\n" +
                   "6 \tCARBON \tC \t12.011\n" +
                   "7 \tNITROGEN \tN \t14.007\n" +
                   "8 \tOXYGEN \tO \t15.999\n" +
                   "9 \tFLUORINE \tF \t18.998\n" +
                   "10 \tNEON \tNe \t20.183\n" +
                   "11 \tSODIUM \tNa \t22.990\n" +
                   "12 \tMAGNESIUM \tMg \t24.312\n" +
                   "13 \tALUMINIUM \tAl \t26.982\n" +
                   "14 \tSILICON \tSi \t28.086\n" +
                   "15 \tPHOSPHORUS \tP \t30.974\n" +
                   "16 \tSULPHUR \tS \t32.064\n" +
                   "17 \tCHLORINE \tCl \t35.453\n" +
                   "18 \tARGON \tA \t39.948\n" +
                   "19 \tPOTASSIUM \tK \t39.102\n" +
                   "20 \tCALCIUM \tCa \t40.08\n" +
                   "21 \tSCANDIUM \tSc \t44.956\n" +
                   "22 \tTITANIUM \tTi \t47.90\n" +
                   "23 \tVANADIUM \tV \t50.94\n" +
                   "24 \tCHROMIUM \tCr \t52.00\n" +
                   "25 \tMANGANESE \tMn \t54.94\n" +
                   "26 \tIRON \tFe \t55.85\n" +
                   "27 \tCOBALT \tCo \t58.93\n" +
                   "28 \tNICKEL \tNi \t58.71\n" +
                   "29 \tCOPPER \tCu \t63.54\n" +
                   "30 \tZINC \tZn \t65.37\n" +
                   "31 \tGALLIUM \tGa \t69.72\n" +
                   "32 \tGERMANIUM \tGe \t72.59\n" +
                   "33 \tARSENIC \tAs \t74.92\n" +
                   "34 \tSELENIUM \tSe \t78.96\n" +
                   "35 \tBROMINE \tBr \t79.909\n" +
                   "36 \tKRYPTON \tKr \t83.80\n" +
                   "37 \tRUBIDIUM \tRb \t85.47\n" +
                   "38 \tSTRONTIUM \tSr \t87.62\n" +
                   "39 \tYTTRIUM \tY \t88.905\n" +
                   "40 \tZIRCONIUM \tZr \t91.22\n" +
                   "41 \tNIOBIUM \tNb \t92.906\n" +
                   "42 \tMOLYBDENUM \tMo \t95.94\n" +
                   "43 \tTECHNETIUM \tTc \t99.00\n" +
                   "44 \tRUTHENIUM \tRu \t101.07\n" +
                   "45 \tRHODIUM \tRh \t102.92\n" +
                   "46 \tPALLADIUM \tPd \t106.4\n" +
                   "47 \tSILVER \tAg \t107.87\n" +
                   "48 \tCADMIUM \tCd \t112.40\n" +
                   "49 \tINDIUM \tIn \t114.82\n" +
                   "50 \tTIN \tSn \t118.69\n" +
                   "51 \tANTIMONY \tSb \t121.75\n" +
                   "52 \tTELLURIUM \tTe \t127.60\n" +
                   "53 \tIODINE \tI \t126.904\n" +
                   "54 \tXENON \tXe \t131.30\n" +
                   "55 \tCAESIUM \tCs \t132.905\n" +
                   "56 \tBARIUM \tBa \t137.34\n" +
                   "57 \tLANTHANUM \tLa \t138.91\n" +
                   "58 \tCERIUM \tCe \t140.12\n" +
                   "59 \tPRASEODYMIUM \tPr \t140.907\n" +
                   "60 \tNEODYMIUM \tNd \t144.24\n" +
                   "61 \tPROMETHIUM \tPm \t147\n" +
                   "62 \tSAMARIUM \tSm \t150.35\n" +
                   "63 \tEUROPIUM \tEu \t151.96\n" +
                   "64 \tGADOLINIUM \tGd \t157.25\n" +
                   "65 \tTERBIUM \tTb \t158.92\n" +
                   "66 \tDYSPROSIUM \tDy \t162.50\n" +
                   "67 \tHOLMIUM \tHo \t164.93\n" +
                   "68 \tERBIUM \tEr \t167.26\n" +
                   "69 \tTHULIUM \tTm \t168.93\n" +
                   "70 \tYTTERBIUM \tYb \t173.04\n" +
                   "71 \tLUTETIUM \tLu \t174.97\n" +
                   "72 \tHAFNIUM \tHf \t178.49\n" +
                   "73 \tTANTALUM \tTa \t180.95\n" +
                   "74 \tTUNGSTEN\tW \t183.85\n" +
                   "75 \tRHENIUM \tRe \t186.2\n" +
                   "76 \tOSMIUM \tOs \t190.2\n" +
                   "77 \tIRIDIUM \tIr \tI92.2\n" +
                   "78 \tPLATINUM \tPt \t195.09\n" +
                   "79 \tGOLD \tAu \t196.97\n" +
                   "80 \tMERCURY \tHg \t200.59\n" +
                   "81 \tTHALLIUM \tTl \t204.37\n" +
                   "82 \tLEAD \tPb \t207.19\n" +
                   "83 \tBISMUTH \tBi \t208.98\n" +
                   "84 \tPOLONIUM \tPo \t210\n" +
                   "85 \tASTATINE \tAt \t211\n" +
                   "86 \tRADON \tRn \t222\n" +
                   "87 \tFRANCIUM \tFr \t223\n" +
                   "88 \tRADIUM \tRa \t226.05\n" +
                   "89 \tACTINIUM \tAc \t227.05\n" +
                   "90 \tTHORIUM \tTh \t232.12\n" +
                   "91 \tPROTACTINIUM \tPa \t231.05\n" +
                   "92 \tURANIUM \tU \t238.07\n" +
                   "93 \tNEPTUNIUM \tNp \t237\n" +
                   "94 \tPLUTONIUM \tPu \t239\n" +
                   "95 \tAMERICIUM \tAm \t241\n" +
                   "96 \tCURIUM \tCm \t242\n" +
                   "97 \tBERKELIUM \tBk \t243_250\n" +
                   "98 \tCALIFORNIUM \tCf \t251\n" +
                   "99 \tEINSTEINIUM \tEs \t246,247\n" +
                   "100 \tFERMIUM \tFm \t250,252_256\n" +
                   "101 \tMENDELEVIUM \tMd \t256\n" +
                   "102 \tNOBELIUM \tNo \t254\n" +
                   "103 \tLAWRENCIUM \tLr \t257\n" +
                   "104 \tRUTHERFORDIUM\tRf\t267 ?\n" +
                   "105 \tDUBNIUM\tDb\t268 ?\n" +
                   "106 \tSEABORGIUM \tSg\t271 ?\n" +
                   "107 \tBOHRIUM\tBh\n" +
                   "108 \tHASSIUM\tHs\n" +
                   "109 \tMEITNERIUM\tMt\n" +
                   "110 \tDARMSTADTIUM\tDs\n" +
                   "111 \tROENTGENIUM\tRg\n" +
                   "112 \tUNUNBIUM\tCn\n" +//Modified this line based on http://www.ptable.com/
                   "113 \tUNUNTRIUM\tUut\n" +
                   "114 \tUNUNQUADIUM\tUuq\n" +
                   "115 \tUNUNPENTIUM\tUup\n" +
                   "116 \tUNUNHEXIUM\tUuh\n" +
                   "117 \tUNUNSEPTIUM\tUus\n" +
                   "118 \tUNUNOCTIUM\tUuo\n" +
                   "119 \tUNUNENNIUM\tUue\n" +
                   "120 \tUNBINILIUM\tUbn\n" +
                   "121 \tUNBIUNIUM\tUbu\n" +
                   "122 \tUNBIBIUM\tUbb\n" +
                   "123 \tUNBITRIUM\tUbt\n" +
                   "124 \tUNBIQUADIUM\tUbq\n" +
                   "125 \tUNBIPENTIUM\tUbp\n" +
                   "126 \tUNBIHEXIUM\tUbh\n" +
                   "127 \tUNBISEPTIUM\tUbs\n" +
                   "128 \tUNBIOCTIUM\tUbo\n" +
                   "129 \tUNBIENNIUM\tUbe\n" +
                   "130 \tUNTRINILIUM\tUtn\n" +
                   "131 \tUNTRINIUM\tUtu";

}

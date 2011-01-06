package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a node that represents a periodic table of the elements.
 * It is not interactive by default, but provides overrides that can be used
 * to add interactivity.
 *
 * This makes some assumptions about which portions of the table to display,
 * and may not work for all situations.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class PeriodicTableNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static int CELL_DIMENSION = 20;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    public Color backgroundColor = null;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     * @param backgroundColor
     */
    public PeriodicTableNode( final IAtom atom, Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
        //See http://www.ptable.com/
        final PNode table = new PNode();
        for ( int i = 1; i <= 56; i++ ) {
            addElement( atom, table, i );
        }
        // Add in a single entry to represent the lanthanide series.
        addElement( atom, table, 57 );
        for ( int i = 71; i <= 88; i++ ) {
            addElement( atom, table, i );
        }
        // Add in a single entry to represent the actinide series.
        addElement( atom, table, 89 );
        for ( int i = 103; i <= 112; i++ ) {
            addElement( atom, table, i );
        }

        addChild( table );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private void addElement( final IAtom atom, final PNode table, int atomicNumber ) {
        ElementCell elementCell = new ElementCell( atom, atomicNumber, backgroundColor );
        final Point gridPoint = getGridPoint( atomicNumber );
        double x = ( gridPoint.getY() - 1 ) * CELL_DIMENSION;     //expansion cells render as "..." on top of each other
        double y = ( gridPoint.getX() - 1 ) * CELL_DIMENSION;
        elementCell.setOffset( x, y );
        table.addChild( elementCell );
        elementCellCreated( elementCell );
    }

    /**
     * Listener callback, override when needing notification of the creation
     * of element cells.  This is useful when creating an interactive chart,
     * since it is a good opportunity to hook up event listeners to the cell.
     *
     * @param elementCell
     */
    protected void elementCellCreated( ElementCell elementCell ) {
    }

    /**
     * Reports (row,column) on the grid, with a 1-index
     *
     * @param i
     * @return
     */
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

    /**
     * Get the description line for the element that corresponds to the given
     * atomic number.  See the table that is defined elsewhere in this file in
     * order to see the format of the description lines.
     *
     * @param atomicNumber
     * @return
     */
    private static String getElementDescriptionLine( int atomicNumber ) {
        StringTokenizer stringTokenizer = new StringTokenizer( table, "\n" );
        while ( stringTokenizer.hasMoreElements() ) {
            String element = stringTokenizer.nextToken();
            if ( element.startsWith( atomicNumber + "" ) ) {
                return element;
            }
        }
        return null;
    }

    /**
     *
     * @param atomicNumber
     * @return
     */
    public static String getElementAbbreviation( int atomicNumber ) {
        String line = getElementDescriptionLine( atomicNumber );
        StringTokenizer stringTokenizer = new StringTokenizer( line, "\t " );
        stringTokenizer.nextToken();//number
        stringTokenizer.nextToken();//name
        String abbreviation = stringTokenizer.nextToken();//abbreviation

        if ( atomicNumber >= 57 && atomicNumber <= 71 ) {
            abbreviation = BuildAnAtomStrings.ELEMENT_LANTHANUM_SYMBOL;
        }
        if ( atomicNumber >= 89 && atomicNumber <= 103 ) {
            abbreviation = BuildAnAtomStrings.ELEMENT_ACTINIUM_SYMBOL;
        }
        return abbreviation;
    }

    //Copied table from http://www.zyra.org.uk/elements.htm
    //Used for generating the translatable version below in main()
    public static final String tableOrig = "1 \tHYDROGEN  \tH  \t1.008\n" +
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
                                           "18 \tARGON \tAr \t39.948\n" +
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

    //internationalized version used in the sim, machine generated by main()
    public static final String table = "1 \tHYDROGEN\t" + BuildAnAtomStrings.ELEMENT_HYDROGEN_SYMBOL + "\n" +
                                       "2 \tHELIUM\t" + BuildAnAtomStrings.ELEMENT_HELIUM_SYMBOL + "\n" +
                                       "3 \tLITHIUM\t" + BuildAnAtomStrings.ELEMENT_LITHIUM_SYMBOL + "\n" +
                                       "4 \tBERYLLIUM\t" + BuildAnAtomStrings.ELEMENT_BERYLLIUM_SYMBOL + "\n" +
                                       "5 \tBORON\t" + BuildAnAtomStrings.ELEMENT_BORON_SYMBOL + "\n" +
                                       "6 \tCARBON\t" + BuildAnAtomStrings.ELEMENT_CARBON_SYMBOL + "\n" +
                                       "7 \tNITROGEN\t" + BuildAnAtomStrings.ELEMENT_NITROGEN_SYMBOL + "\n" +
                                       "8 \tOXYGEN\t" + BuildAnAtomStrings.ELEMENT_OXYGEN_SYMBOL + "\n" +
                                       "9 \tFLUORINE\t" + BuildAnAtomStrings.ELEMENT_FLUORINE_SYMBOL + "\n" +
                                       "10 \tNEON\t" + BuildAnAtomStrings.ELEMENT_NEON_SYMBOL + "\n" +
                                       "11 \tSODIUM\t" + BuildAnAtomStrings.ELEMENT_SODIUM_SYMBOL + "\n" +
                                       "12 \tMAGNESIUM\t" + BuildAnAtomStrings.ELEMENT_MAGNESIUM_SYMBOL + "\n" +
                                       "13 \tALUMINIUM\t" + BuildAnAtomStrings.ELEMENT_ALUMINIUM_SYMBOL + "\n" +
                                       "14 \tSILICON\t" + BuildAnAtomStrings.ELEMENT_SILICON_SYMBOL + "\n" +
                                       "15 \tPHOSPHORUS\t" + BuildAnAtomStrings.ELEMENT_PHOSPHORUS_SYMBOL + "\n" +
                                       "16 \tSULPHUR\t" + BuildAnAtomStrings.ELEMENT_SULPHUR_SYMBOL + "\n" +
                                       "17 \tCHLORINE\t" + BuildAnAtomStrings.ELEMENT_CHLORINE_SYMBOL + "\n" +
                                       "18 \tARGON\t" + BuildAnAtomStrings.ELEMENT_ARGON_SYMBOL + "\n" +
                                       "19 \tPOTASSIUM\t" + BuildAnAtomStrings.ELEMENT_POTASSIUM_SYMBOL + "\n" +
                                       "20 \tCALCIUM\t" + BuildAnAtomStrings.ELEMENT_CALCIUM_SYMBOL + "\n" +
                                       "21 \tSCANDIUM\t" + BuildAnAtomStrings.ELEMENT_SCANDIUM_SYMBOL + "\n" +
                                       "22 \tTITANIUM\t" + BuildAnAtomStrings.ELEMENT_TITANIUM_SYMBOL + "\n" +
                                       "23 \tVANADIUM\t" + BuildAnAtomStrings.ELEMENT_VANADIUM_SYMBOL + "\n" +
                                       "24 \tCHROMIUM\t" + BuildAnAtomStrings.ELEMENT_CHROMIUM_SYMBOL + "\n" +
                                       "25 \tMANGANESE\t" + BuildAnAtomStrings.ELEMENT_MANGANESE_SYMBOL + "\n" +
                                       "26 \tIRON\t" + BuildAnAtomStrings.ELEMENT_IRON_SYMBOL + "\n" +
                                       "27 \tCOBALT\t" + BuildAnAtomStrings.ELEMENT_COBALT_SYMBOL + "\n" +
                                       "28 \tNICKEL\t" + BuildAnAtomStrings.ELEMENT_NICKEL_SYMBOL + "\n" +
                                       "29 \tCOPPER\t" + BuildAnAtomStrings.ELEMENT_COPPER_SYMBOL + "\n" +
                                       "30 \tZINC\t" + BuildAnAtomStrings.ELEMENT_ZINC_SYMBOL + "\n" +
                                       "31 \tGALLIUM\t" + BuildAnAtomStrings.ELEMENT_GALLIUM_SYMBOL + "\n" +
                                       "32 \tGERMANIUM\t" + BuildAnAtomStrings.ELEMENT_GERMANIUM_SYMBOL + "\n" +
                                       "33 \tARSENIC\t" + BuildAnAtomStrings.ELEMENT_ARSENIC_SYMBOL + "\n" +
                                       "34 \tSELENIUM\t" + BuildAnAtomStrings.ELEMENT_SELENIUM_SYMBOL + "\n" +
                                       "35 \tBROMINE\t" + BuildAnAtomStrings.ELEMENT_BROMINE_SYMBOL + "\n" +
                                       "36 \tKRYPTON\t" + BuildAnAtomStrings.ELEMENT_KRYPTON_SYMBOL + "\n" +
                                       "37 \tRUBIDIUM\t" + BuildAnAtomStrings.ELEMENT_RUBIDIUM_SYMBOL + "\n" +
                                       "38 \tSTRONTIUM\t" + BuildAnAtomStrings.ELEMENT_STRONTIUM_SYMBOL + "\n" +
                                       "39 \tYTTRIUM\t" + BuildAnAtomStrings.ELEMENT_YTTRIUM_SYMBOL + "\n" +
                                       "40 \tZIRCONIUM\t" + BuildAnAtomStrings.ELEMENT_ZIRCONIUM_SYMBOL + "\n" +
                                       "41 \tNIOBIUM\t" + BuildAnAtomStrings.ELEMENT_NIOBIUM_SYMBOL + "\n" +
                                       "42 \tMOLYBDENUM\t" + BuildAnAtomStrings.ELEMENT_MOLYBDENUM_SYMBOL + "\n" +
                                       "43 \tTECHNETIUM\t" + BuildAnAtomStrings.ELEMENT_TECHNETIUM_SYMBOL + "\n" +
                                       "44 \tRUTHENIUM\t" + BuildAnAtomStrings.ELEMENT_RUTHENIUM_SYMBOL + "\n" +
                                       "45 \tRHODIUM\t" + BuildAnAtomStrings.ELEMENT_RHODIUM_SYMBOL + "\n" +
                                       "46 \tPALLADIUM\t" + BuildAnAtomStrings.ELEMENT_PALLADIUM_SYMBOL + "\n" +
                                       "47 \tSILVER\t" + BuildAnAtomStrings.ELEMENT_SILVER_SYMBOL + "\n" +
                                       "48 \tCADMIUM\t" + BuildAnAtomStrings.ELEMENT_CADMIUM_SYMBOL + "\n" +
                                       "49 \tINDIUM\t" + BuildAnAtomStrings.ELEMENT_INDIUM_SYMBOL + "\n" +
                                       "50 \tTIN\t" + BuildAnAtomStrings.ELEMENT_TIN_SYMBOL + "\n" +
                                       "51 \tANTIMONY\t" + BuildAnAtomStrings.ELEMENT_ANTIMONY_SYMBOL + "\n" +
                                       "52 \tTELLURIUM\t" + BuildAnAtomStrings.ELEMENT_TELLURIUM_SYMBOL + "\n" +
                                       "53 \tIODINE\t" + BuildAnAtomStrings.ELEMENT_IODINE_SYMBOL + "\n" +
                                       "54 \tXENON\t" + BuildAnAtomStrings.ELEMENT_XENON_SYMBOL + "\n" +
                                       "55 \tCAESIUM\t" + BuildAnAtomStrings.ELEMENT_CAESIUM_SYMBOL + "\n" +
                                       "56 \tBARIUM\t" + BuildAnAtomStrings.ELEMENT_BARIUM_SYMBOL + "\n" +
                                       "57 \tLANTHANUM\t" + BuildAnAtomStrings.ELEMENT_LANTHANUM_SYMBOL + "\n" +
                                       "58 \tCERIUM\t" + BuildAnAtomStrings.ELEMENT_CERIUM_SYMBOL + "\n" +
                                       "59 \tPRASEODYMIUM\t" + BuildAnAtomStrings.ELEMENT_PRASEODYMIUM_SYMBOL + "\n" +
                                       "60 \tNEODYMIUM\t" + BuildAnAtomStrings.ELEMENT_NEODYMIUM_SYMBOL + "\n" +
                                       "61 \tPROMETHIUM\t" + BuildAnAtomStrings.ELEMENT_PROMETHIUM_SYMBOL + "\n" +
                                       "62 \tSAMARIUM\t" + BuildAnAtomStrings.ELEMENT_SAMARIUM_SYMBOL + "\n" +
                                       "63 \tEUROPIUM\t" + BuildAnAtomStrings.ELEMENT_EUROPIUM_SYMBOL + "\n" +
                                       "64 \tGADOLINIUM\t" + BuildAnAtomStrings.ELEMENT_GADOLINIUM_SYMBOL + "\n" +
                                       "65 \tTERBIUM\t" + BuildAnAtomStrings.ELEMENT_TERBIUM_SYMBOL + "\n" +
                                       "66 \tDYSPROSIUM\t" + BuildAnAtomStrings.ELEMENT_DYSPROSIUM_SYMBOL + "\n" +
                                       "67 \tHOLMIUM\t" + BuildAnAtomStrings.ELEMENT_HOLMIUM_SYMBOL + "\n" +
                                       "68 \tERBIUM\t" + BuildAnAtomStrings.ELEMENT_ERBIUM_SYMBOL + "\n" +
                                       "69 \tTHULIUM\t" + BuildAnAtomStrings.ELEMENT_THULIUM_SYMBOL + "\n" +
                                       "70 \tYTTERBIUM\t" + BuildAnAtomStrings.ELEMENT_YTTERBIUM_SYMBOL + "\n" +
                                       "71 \tLUTETIUM\t" + BuildAnAtomStrings.ELEMENT_LUTETIUM_SYMBOL + "\n" +
                                       "72 \tHAFNIUM\t" + BuildAnAtomStrings.ELEMENT_HAFNIUM_SYMBOL + "\n" +
                                       "73 \tTANTALUM\t" + BuildAnAtomStrings.ELEMENT_TANTALUM_SYMBOL + "\n" +
                                       "74 \tTUNGSTEN\t" + BuildAnAtomStrings.ELEMENT_TUNGSTEN_SYMBOL + "\n" +
                                       "75 \tRHENIUM\t" + BuildAnAtomStrings.ELEMENT_RHENIUM_SYMBOL + "\n" +
                                       "76 \tOSMIUM\t" + BuildAnAtomStrings.ELEMENT_OSMIUM_SYMBOL + "\n" +
                                       "77 \tIRIDIUM\t" + BuildAnAtomStrings.ELEMENT_IRIDIUM_SYMBOL + "\n" +
                                       "78 \tPLATINUM\t" + BuildAnAtomStrings.ELEMENT_PLATINUM_SYMBOL + "\n" +
                                       "79 \tGOLD\t" + BuildAnAtomStrings.ELEMENT_GOLD_SYMBOL + "\n" +
                                       "80 \tMERCURY\t" + BuildAnAtomStrings.ELEMENT_MERCURY_SYMBOL + "\n" +
                                       "81 \tTHALLIUM\t" + BuildAnAtomStrings.ELEMENT_THALLIUM_SYMBOL + "\n" +
                                       "82 \tLEAD\t" + BuildAnAtomStrings.ELEMENT_LEAD_SYMBOL + "\n" +
                                       "83 \tBISMUTH\t" + BuildAnAtomStrings.ELEMENT_BISMUTH_SYMBOL + "\n" +
                                       "84 \tPOLONIUM\t" + BuildAnAtomStrings.ELEMENT_POLONIUM_SYMBOL + "\n" +
                                       "85 \tASTATINE\t" + BuildAnAtomStrings.ELEMENT_ASTATINE_SYMBOL + "\n" +
                                       "86 \tRADON\t" + BuildAnAtomStrings.ELEMENT_RADON_SYMBOL + "\n" +
                                       "87 \tFRANCIUM\t" + BuildAnAtomStrings.ELEMENT_FRANCIUM_SYMBOL + "\n" +
                                       "88 \tRADIUM\t" + BuildAnAtomStrings.ELEMENT_RADIUM_SYMBOL + "\n" +
                                       "89 \tACTINIUM\t" + BuildAnAtomStrings.ELEMENT_ACTINIUM_SYMBOL + "\n" +
                                       "90 \tTHORIUM\t" + BuildAnAtomStrings.ELEMENT_THORIUM_SYMBOL + "\n" +
                                       "91 \tPROTACTINIUM\t" + BuildAnAtomStrings.ELEMENT_PROTACTINIUM_SYMBOL + "\n" +
                                       "92 \tURANIUM\t" + BuildAnAtomStrings.ELEMENT_URANIUM_SYMBOL + "\n" +
                                       "93 \tNEPTUNIUM\t" + BuildAnAtomStrings.ELEMENT_NEPTUNIUM_SYMBOL + "\n" +
                                       "94 \tPLUTONIUM\t" + BuildAnAtomStrings.ELEMENT_PLUTONIUM_SYMBOL + "\n" +
                                       "95 \tAMERICIUM\t" + BuildAnAtomStrings.ELEMENT_AMERICIUM_SYMBOL + "\n" +
                                       "96 \tCURIUM\t" + BuildAnAtomStrings.ELEMENT_CURIUM_SYMBOL + "\n" +
                                       "97 \tBERKELIUM\t" + BuildAnAtomStrings.ELEMENT_BERKELIUM_SYMBOL + "\n" +
                                       "98 \tCALIFORNIUM\t" + BuildAnAtomStrings.ELEMENT_CALIFORNIUM_SYMBOL + "\n" +
                                       "99 \tEINSTEINIUM\t" + BuildAnAtomStrings.ELEMENT_EINSTEINIUM_SYMBOL + "\n" +
                                       "100 \tFERMIUM\t" + BuildAnAtomStrings.ELEMENT_FERMIUM_SYMBOL + "\n" +
                                       "101 \tMENDELEVIUM\t" + BuildAnAtomStrings.ELEMENT_MENDELEVIUM_SYMBOL + "\n" +
                                       "102 \tNOBELIUM\t" + BuildAnAtomStrings.ELEMENT_NOBELIUM_SYMBOL + "\n" +
                                       "103 \tLAWRENCIUM\t" + BuildAnAtomStrings.ELEMENT_LAWRENCIUM_SYMBOL + "\n" +
                                       "104 \tRUTHERFORDIUM\t" + BuildAnAtomStrings.ELEMENT_RUTHERFORDIUM_SYMBOL + "\n" +
                                       "105 \tDUBNIUM\t" + BuildAnAtomStrings.ELEMENT_DUBNIUM_SYMBOL + "\n" +
                                       "106 \tSEABORGIUM\t" + BuildAnAtomStrings.ELEMENT_SEABORGIUM_SYMBOL + "\n" +
                                       "107 \tBOHRIUM\t" + BuildAnAtomStrings.ELEMENT_BOHRIUM_SYMBOL + "\n" +
                                       "108 \tHASSIUM\t" + BuildAnAtomStrings.ELEMENT_HASSIUM_SYMBOL + "\n" +
                                       "109 \tMEITNERIUM\t" + BuildAnAtomStrings.ELEMENT_MEITNERIUM_SYMBOL + "\n" +
                                       "110 \tDARMSTADTIUM\t" + BuildAnAtomStrings.ELEMENT_DARMSTADTIUM_SYMBOL + "\n" +
                                       "111 \tROENTGENIUM\t" + BuildAnAtomStrings.ELEMENT_ROENTGENIUM_SYMBOL + "\n" +
                                       "112 \tUNUNBIUM\t" + BuildAnAtomStrings.ELEMENT_UNUNBIUM_SYMBOL + "\n";

    /**
     * Test harness.
     */
    public static void main( String[] args ) {
        String t = tableOrig;
        StringTokenizer stringTokenizer = new StringTokenizer( t, "\n" );
        while ( stringTokenizer.hasMoreElements() ) {
            String line = stringTokenizer.nextToken();
            StringTokenizer st = new StringTokenizer( line, "\t " );
            int index = Integer.parseInt( st.nextToken() );//index
            if ( index <= 112 ) {
                String name = st.nextToken();//name;
                String symbol = st.nextToken();//symbol
                String key = "element." + name.toLowerCase() + ".symbol";
                String propertiesLine = key + " = " + symbol;
                System.out.println( propertiesLine );
                String upperName = name.toUpperCase().trim();
                final String EINine = "\"" + index + " \\t" + upperName + "\\t\"+BuildAnAtomStrings.ELEMENT_" + upperName + "_SYMBOL+\"\\n\"+";
                System.out.println( EINine );
            }
        }
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public class ElementCell extends PNode {
        private final int atomicNumber;
        private final PText text;
        private final PhetPPath box;
        private boolean disabledLooking = false;

        public ElementCell( final IAtom atom, final int atomicNumber, final Color backgroundColor ) {
            this.atomicNumber = atomicNumber;
            box = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_DIMENSION, CELL_DIMENSION ),
                    backgroundColor, new BasicStroke( 1 ), Color.black );
            addChild( box );

            String abbreviation = getElementAbbreviation( atomicNumber );
            text = new PText( abbreviation );
            text.setOffset( box.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2,
                    box.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
            addChild( text );
            atom.addObserver( new SimpleObserver() {
                public void update() {
                    boolean match = atom.getNumProtons() == atomicNumber;
                    text.setFont( new PhetFont( PhetFont.getDefaultFontSize(), match ) );
                    if ( match ) {
                        box.setStroke( new BasicStroke( 2 ) );
                        box.setStrokePaint( Color.RED );
                        box.setPaint( Color.white );
                        ElementCell.this.moveToFront();
                    }
                    else {
                        if ( !disabledLooking ){
                            box.setStroke( new BasicStroke( 1 ) );
                            box.setStrokePaint( Color.BLACK );
                            box.setPaint( backgroundColor );
                        }
                        else{
                            text.setTextPaint( Color.LIGHT_GRAY );
                            box.setStrokePaint( Color.LIGHT_GRAY );
                        }
                    }
                }
            } );
        }

        public int getAtomicNumber() {
            return atomicNumber;
        }

        // TODO: This is prototype.  Should come up with a better way to make the cells look disabled once we have
        // figured out how they should look.
        public void setDisabledLooking( boolean disabledLooking ){
            this.disabledLooking = disabledLooking;
        }
    }
}

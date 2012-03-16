/*
 * ##############     class Visua v2.0     ################
 *
 * Copyright (c) Guillaume Cottenceau, 1998
 *
 * JCAMP and SPC viewer for HTML page
 *
 * Last modification : 24.4.98
 *
 * Modified to write JCAMP files on client (APT : 05-01-99)
 *
 */
package org.jmol.jcamp;
import java.applet.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import org.jmol.jcamp.utils.*;
public class Visua extends Applet implements Runnable {
   ZoneVisu My_ZoneVisu;
   Thread My_Thread;
   private GraphCharacteristics _graphDataUtils; // All data pertaining to the graphs' features
   public Vector texte;     // variable qui sont utilisees par des sous classes
   public int nbLignes;
   int shitty_starting_constant=66666; //what is this? figure it out
   public double Firstx;
   public double Lastx;
   public double YFactor;
   public int Nbpoints;
   public double nmr_observe_frequency;
   public String TexteTitre;
   public String x_units;
   public String y_units;
   public String Datatype;
   // TODO - Shravan Change 2
   Button Load_File;
   Button Zoom_In;
   Button Zoom_Back;
   Button Zoom_Out;
   Checkbox Reverse;
   Checkbox Grid;
   Checkbox Integrate;
   // END - Shravan Change 2
   Button Find_Peak;
   Button JCamp;
   Frame Fenetre_Load_File;
   SaisieDlg Dialogue_Load_File;
   AppletContext contexte;
   String clickable_peaks_frame_name ;
   boolean Flag_Zoomin=false;
   boolean Flag_Zoomback=false;
   boolean Flag_Zoomout=false;
   boolean Flag_Reverse=false;
   boolean Flag_Integrate=false;
   boolean Flag_Dialogue_File_Enabled=false;
   boolean Flag_Redraw=false;
   boolean Flag_Find_Peak=false;
   boolean Flag_Write_JCamp=false;
   boolean isActive=false, hasPrivilege=false;
   boolean inNavigator=false, inExplorer=false;
   FileDialog openDialog;
   FileDialog saveDialog;
   String Current_Error="";
   /* Applet initialization */
   public void init() {
     showStatus("Initializing jcamp/spc visualizer, please wait...");
     System.out.println("jcamp/spc visualizer v2.0.0 (c) G. Cottenceau 1998");
//
//		Netscape security for the applet RMI network comms
//		    This will launch a Java Security window
//
/* Shravan Sadasivan - Commented during developement */
// TODO - Uncomment during implementation
/*	System.out.println(System.getProperty("java.vendor"));
	System.out.println(System.getProperty("browser") + " v" +
				System.getProperty("browser.version"));
	if ((System.getProperty("java.vendor").toLowerCase().indexOf("netscape")) != -1) {
			inNavigator = true;
	} else if ((System.getProperty("java.vendor").toLowerCase().indexOf("microsoft")) != -1) {
			inExplorer = true;
	} else {
			System.out.println("\nUnknown browser vendor");
	} */
//		MSIE4.0 will give a security exception :
//		com.ms.security.SecurityExceptionEx : FileDialog creation denied
     if (inNavigator) {
  //		FileDialog needs a parent Frame
  
  	     Component c = this.getParent();
  	     while (c != null && !(c instanceof Frame)) c = c.getParent();
  //	     openDialog = new FileDialog( (Frame)c, "Open PDB", FileDialog.LOAD);
  	     saveDialog = new FileDialog( (Frame)c, "Save JCAMP", FileDialog.SAVE);
     }
     setLayout(new BorderLayout());
     Panel Mes_Boutons=new Panel();
     Mes_Boutons.setLayout(new GridLayout(1,1));
  	 if(getParameter("INTEGRATE") != null && getParameter("INTEGRATE").compareTo("TRUE") == 0){
  		 Integrate=new Checkbox("Integrate");
      	 Mes_Boutons.add(Integrate);
   	 }
     
     if (getParameter("LOAD_FILE")!=null && getParameter("LOAD_FILE").compareTo("SHOW")==0) Mes_Boutons.add(Load_File);
     add("South",Mes_Boutons);
	 
     My_ZoneVisu=new ZoneVisu();     
	   initGraphParameters();
     add("Center",My_ZoneVisu);
     layout();
     contexte=getAppletContext();
     clickable_peaks_frame_name=getParameter("CLICKABLE_PEAKS_FRAME_NAME");
     My_ZoneVisu.Y_Values=getParameter("Y_VALUES");
     My_ZoneVisu.ShowTitle=getParameter("TITLE");
     My_ZoneVisu.Flag_Clickable_Peaks=Load_Clickable_Peaks_Source_File(getParameter("CLICKABLE_PEAKS_SOURCE_FILE"));
     My_ZoneVisu.init();
     My_ZoneVisu.setGraphDataUtils(this._graphDataUtils);
     Really_Load_File(getParameter("SOURCE_FILE"));
     if (getParameter("CLICKABLE_PEAKS_FIRST_FRAME")!=null) {
           My_ZoneVisu.Flag_Load_Now_Html=true;
           My_ZoneVisu.Name_Load_Now_Html=getParameter("CLICKABLE_PEAKS_FIRST_FRAME");
     } 
    }
   
  /**
   * Method to initialize the GraphCharacteristics object in this class
   */
  private void initGraphParameters(){
  	_graphDataUtils = new GraphCharacteristics();
  	if(getParameter("GRID") != null && getParameter("GRID").compareTo("TRUE") == 0){
  			    _graphDataUtils.setGrid(true);
  		 		My_ZoneVisu.Flag_Grid = true;
  		 }else{
  				_graphDataUtils.setGrid(false);
  	}
  
  	if(getParameter("INTEGRATION_VALUES") != null){
  		_graphDataUtils.setUnsortedIntegrationValues(getParameter("INTEGRATION_VALUES"));
  	}
  
  
  	if(getParameter("REVERSE") != null && getParameter("REVERSE").compareTo("TRUE") == 0){
  		_graphDataUtils.setReverse(true);
  		Flag_Reverse = true;
  		My_ZoneVisu.reverse = true;
      }
  
  	if(getParameter("AXIS_COLOR") != null){
  	    _graphDataUtils.setAxisColor(getParameter("AXIS_COLOR"));
  	}
  
  	if(getParameter("AXIS_TEXT_COLOR") != null){
  		_graphDataUtils.setAxisTextColor(getParameter("AXIS_TEXT_COLOR"));
  	}
  
  	if(getParameter("INTEGRATE_CURVE_COLOR") != null){
  		_graphDataUtils.setIntegrateCurveColor(getParameter("INTEGRATE_CURVE_COLOR"));
  	}
  
  	if(getParameter("INTEGRATE_TEXT_COLOR") != null){
  		_graphDataUtils.setIntegrateTextColor(getParameter("INTEGRATE_TEXT_COLOR"));
  	}
  
  	if(getParameter("GRAPH_CURVE_COLOR") != null){
  		_graphDataUtils.setGraphCurveColor(getParameter("GRAPH_CURVE_COLOR"));
  	}
  
  	if(getParameter("TEXT_COLOR") != null){
  			_graphDataUtils.setTextColor(getParameter("TEXT_COLOR"));
  	}
  }
  public void start() {
   if (My_Thread==null) {
      My_Thread=new Thread(this);
      My_Thread.start();
     }
    showStatus("Ready");
   }
  public void stop() {
    if (My_Thread!=null) {
      My_Thread.stop();
      My_Thread=null;
     }
   }
                          // thread pour que le "please wait" ait le temps de s'afficher et d'autres trucs... "timer"
  public void run() {
    while (true) {
      try {
        Thread.sleep(100);
         } catch (Exception e) {}
    // TO-DO: Shravan Change 1 - Zoom in and zoom out functionality
    // To be changed for removal of buttons
    if (Flag_Zoomin) {
		  System.out.println("Zooming in!");
          My_ZoneVisu.Zoomin();
          Flag_Zoomin=false;
         }
    if (Flag_Zoomback) {
          My_ZoneVisu.Zoomback();
          Flag_Zoomback=false;
         }
    if (Flag_Zoomout) {
          My_ZoneVisu.Zoomout();
          Flag_Zoomout=false;
         }
	// END: Shravan Change 1
	/* START: Shravan - No changes required in this section */
    if (Flag_Reverse) {
          My_ZoneVisu.Reverse();
          Flag_Reverse=false;
         }
    if (Flag_Integrate) {
          My_ZoneVisu.Flag_Integrate=true;
          My_ZoneVisu.Redraw();
          Flag_Integrate=false;
         }
	/* END - no changes*/
    if (Flag_Find_Peak) {
          My_ZoneVisu.Find_Peak();
          Flag_Find_Peak=false;
         }
    if (Flag_Write_JCamp && getParameter("SOURCE_FILE").toLowerCase().endsWith("dx")){
          Write_JCamp();
          Flag_Write_JCamp=false;
         }
    if (Flag_Redraw) {
          My_ZoneVisu.Redraw();
          Flag_Redraw=false;
         }
    if (My_ZoneVisu.Flag_Load_Now_Html) {
          My_ZoneVisu.Flag_Load_Now_Html=false;
          try {
            contexte.showDocument(new URL(getDocumentBase(),My_ZoneVisu.Name_Load_Now_Html),clickable_peaks_frame_name);
           } catch (MalformedURLException e) {}
         }
    if (Flag_Dialogue_File_Enabled && Dialogue_Load_File.fin) {
         Flag_Dialogue_File_Enabled=false;
         Dialogue_Load_File.fin=false;
         if (Dialogue_Load_File.OkStatus) Really_Load_File(Dialogue_Load_File.lisSaisie());
         }
      }
   }
  public boolean Load_Clickable_Peaks_Source_File(String tam) {
     if (tam==null) return false;
     try {
      URL url=new URL(getDocumentBase(),tam);
      InputStream stream=url.openStream();
      DataInputStream fichier=new DataInputStream(stream);
      texte=new Vector();
      String s;
      while ((s=fichier.readLine())!=null) {
        texte.addElement(s);
       }
      My_ZoneVisu.Nb_Clickable_Peaks=texte.size();
     } catch (Exception e) { return false; }
     My_ZoneVisu.Peak_Start=new double[My_ZoneVisu.Nb_Clickable_Peaks];
     My_ZoneVisu.Peak_Stop=new double[My_ZoneVisu.Nb_Clickable_Peaks];
     My_ZoneVisu.Peak_Html=new String[My_ZoneVisu.Nb_Clickable_Peaks];
     int cpt_tokens=0;
     int i=0;
     StringTokenizer mon_token;
     while (cpt_tokens<My_ZoneVisu.Nb_Clickable_Peaks) {
       do {
       String mysub=(String) texte.elementAt(cpt_tokens);
       mon_token=new StringTokenizer(mysub," ");
       cpt_tokens++;
          } while (cpt_tokens<My_ZoneVisu.Nb_Clickable_Peaks && mon_token.hasMoreTokens()==false);
       if (mon_token.hasMoreTokens()==true) {
         My_ZoneVisu.Peak_Start[i]=Double.valueOf(mon_token.nextToken()).doubleValue();
         My_ZoneVisu.Peak_Stop[i]=Double.valueOf(mon_token.nextToken()).doubleValue();
         if (My_ZoneVisu.Peak_Start[i]>My_ZoneVisu.Peak_Stop[i]) {
                  double temp=My_ZoneVisu.Peak_Start[i];
                  My_ZoneVisu.Peak_Start[i]=My_ZoneVisu.Peak_Stop[i];
                  My_ZoneVisu.Peak_Stop[i]=temp;
          }
         My_ZoneVisu.Peak_Html[i]=(String) mon_token.nextToken();
        }
       i++;
      }
     return true;
  }
   
   public String Move_Points_To_Tableau() {
    int indice=0;
    int nbp=Nbpoints;
    if (Datatype.compareTo("XYDATA")==0) {
                              // se place sur la premiere ligne de donnees
      while (StringDataUtils.jcampSubString(((String) texte.elementAt(indice)),0,8).compareTo("##XYDATA")!=0) indice++;
      indice++;
      int indicetableau=0;
      String Un_Nombre;
      My_ZoneVisu.tableau_points=new double[Nbpoints];
      double tmp_tab[]=new double[Nbpoints];
      while (nbp>0 && indice<nbLignes) {
          StringTokenizer mon_token;
          do {
            String mysub=(String) texte.elementAt(indice);
            indice++;
            mon_token=new StringTokenizer(mysub," ");
            } while (indice<nbLignes && mon_token.hasMoreTokens()==false);
          if (mon_token.hasMoreTokens()) {
            Un_Nombre=mon_token.nextToken();
            while (nbp>0 && (mon_token.hasMoreTokens()==true || Un_Nombre.indexOf('-')>1)) {
              if (Un_Nombre.indexOf('-')<=1) Un_Nombre=mon_token.nextToken(); else Un_Nombre=Un_Nombre.substring(Un_Nombre.indexOf('-')+1);
                         // dans certains fichiers certains nombres sont separes par un '-'
                  while (Un_Nombre.indexOf('-')>1) {
                    nbp--;
                    try {
                       My_ZoneVisu.tableau_points[indicetableau]=Double.valueOf(Un_Nombre.substring(0,Un_Nombre.indexOf('-'))).doubleValue();
                     } catch (Exception e) { return ", wrong number format"; }
                    indicetableau++;
                    Un_Nombre=Un_Nombre.substring(Un_Nombre.indexOf('-')+1);
                    }
                    nbp--;
                    try {
                      My_ZoneVisu.tableau_points[indicetableau]=Double.valueOf(Un_Nombre).doubleValue();
                     } catch (Exception e) { return ", wrong number format"; }
                    indicetableau++;
               }
            }
        }
      
      if (Firstx>Lastx) {
          for (int i=0;i<Nbpoints;i++) tmp_tab[i]=My_ZoneVisu.tableau_points[i];
          for (int i=0;i<Nbpoints;i++) My_ZoneVisu.tableau_points[i]=tmp_tab[Nbpoints-i-1];
          double tmp=Firstx;
          Firstx=Lastx;
          Lastx=tmp;
       }
    }
   if (Datatype.compareTo("PEAK TABLE")==0) {
                              // se place sur la premiere ligne de donnees
      while (((String) texte.elementAt(indice)).substring(0,6).compareTo("##PEAK")!=0) indice++;
      indice++;
      int indicetableau=0;
      String Un_Nombre;
      My_ZoneVisu.tableau_points=new double[Nbpoints*2];
      double tmp_tab[]=new double[Nbpoints*2];
      while (nbp>0 && indice<nbLignes) {
          StringTokenizer mon_token;
          do {
            String mysub=(String) texte.elementAt(indice);
            indice++;
            mon_token=new StringTokenizer(mysub," ");
            } while (indice<nbLignes && mon_token.hasMoreTokens()==false);
          if (mon_token.hasMoreTokens()) {
            while (nbp>0 && mon_token.hasMoreTokens()==true) {
              Un_Nombre=mon_token.nextToken();
              nbp--;
              My_ZoneVisu.tableau_points[indicetableau]=Double.valueOf(Un_Nombre.substring(0,Un_Nombre.indexOf(','))).doubleValue();
              indicetableau++;
              if (Un_Nombre.indexOf(',')==Un_Nombre.length()-1)   // cas de x, y
                    My_ZoneVisu.tableau_points[indicetableau]=Double.valueOf(mon_token.nextToken()).doubleValue();
               else    // cas x,y
                    My_ZoneVisu.tableau_points[indicetableau]=Double.valueOf(Un_Nombre.substring(Un_Nombre.indexOf(',')+1)).doubleValue();
              indicetableau++;
             }
          }
        }
               // Dans certains fichiers "Peak Table" il n'y a pas Firstx/Lastx
        if (Firstx==shitty_starting_constant) Firstx=My_ZoneVisu.tableau_points[0];
        if (Lastx==shitty_starting_constant) Lastx=My_ZoneVisu.tableau_points[(Nbpoints-1)*2];
		
      if (Firstx>Lastx) {
          for (int i=0;i<Nbpoints;i++) tmp_tab[i*2]=My_ZoneVisu.tableau_points[i*2];
          for (int i=0;i<Nbpoints;i++) My_ZoneVisu.tableau_points[i*2]=tmp_tab[Nbpoints*2-i*2-1];
          for (int i=0;i<Nbpoints;i++) tmp_tab[i*2+1]=My_ZoneVisu.tableau_points[i*2+1];
          for (int i=0;i<Nbpoints;i++) My_ZoneVisu.tableau_points[i*2+1]=tmp_tab[Nbpoints*2-i*2];
          double tmp=Firstx;
          Firstx=Lastx;
          Lastx=tmp;
       }
     }
     if (nbp>0) return ", file corrupted or unsupported file format";
     return "OK";
  }
  public boolean initFile(String filename) {
    showStatus("Loading the file, please wait...");
    x_units="?";
    y_units="ARBITRARY";
    Datatype="UNKNOWN";
    if (filename.toLowerCase().endsWith(".spc")) {
             // Il s'agit d'un fichier SPC
       try {
         URL url=new URL(getDocumentBase(),filename);
         InputStream stream=url.openStream();
         DataInputStream fichier=new DataInputStream(stream);
                                // Lecture de SPCHDR (512 octets)
         byte ftflgs = fichier.readByte();          // Flag Bits
         byte fversn = fichier.readByte();          // Version
         if (((ftflgs != 0) && (ftflgs != 0x20)) || (fversn != 0x4B)) { Current_Error = ", support only Evenly Spaced new version 4B"; return false; }
         
         //Commented by Shravan Sadasivan - Variable not used
         // byte fexper = fichier.readByte();          // Instrument technique code (on s'en fout)
         byte fexp = fichier.readByte();            // Fraction scaling exponent integer
         if (fexp != 0x80) YFactor = Math.pow(2,fexp) / Math.pow(2,32);
         Nbpoints = NumericDataUtils.convToIntelInt(fichier.readInt());
		 if(Firstx == shitty_starting_constant){
			 Firstx = NumericDataUtils.convToIntelDouble(fichier.readLong());
			 Lastx = NumericDataUtils.convToIntelDouble(fichier.readLong());
	 	 }
		     //Commented by Shravan Sadasivan - Variable not used
         // int fnsub = fichier.readInt();                // Integer Number of Subfiles (on s'en fout)
         byte fxtype = fichier.readByte();
         switch (fxtype) {
            case 0 : x_units = "Arbitrary"; break;
            case 1 : x_units = "Wavenumber (cm -1)"; break;
            case 2 : x_units = "Micrometers"; break;
            case 3 : x_units = "Nanometers"; break;
            case 4 : x_units = "Seconds"; break;
            case 5 : x_units = "Minuts"; break;
            case 6 : x_units = "Hertz"; break;
            case 7 : x_units = "Kilohertz"; break;
            case 8 : x_units = "Megahertz"; break;
            case 9 : x_units = "Mass (M/z)"; break;
            case 10 : x_units = "Parts per million"; break;
            case 11 : x_units = "Days"; break;
            case 12 : x_units = "Years"; break;
            case 13 : x_units = "Raman Shift (cm -1)"; break;
            case 14 : x_units = "Electron Volt (eV)"; break;
            case 16 : x_units = "Diode Number"; break;
            case 17 : x_units = "Channel"; break;
            case 18 : x_units = "Degrees"; break;
            case 19 : x_units = "Temperature (F)"; break;
            case 20 : x_units = "Temperature (C)"; break;
            case 21 : x_units = "Temperature (K)"; break;
            case 22 : x_units = "Data Points"; break;
            case 23 : x_units = "Milliseconds (mSec)"; break;
            case 24 : x_units = "Microseconds (uSec)"; break;
            case 25 : x_units = "Nanoseconds (nSec)"; break;
            case 26 : x_units = "Gigahertz (GHz)"; break;
            case 27 : x_units = "Centimeters (cm)"; break;
            case 28 : x_units = "Meters (m)"; break;
            case 29 : x_units = "Millimeters (mm)"; break;
            case 30 : x_units = "Hours"; break;
            case -1 : x_units = "(double interferogram)"; break;
           }
         byte fytype = fichier.readByte();
         switch (fytype) {
            case 0 : y_units = "Arbitrary Intensity"; break;
            case 1 : y_units = "Interfeogram"; break;
            case 2 : y_units = "Absorbance"; break;
            case 3 : y_units = "Kubelka-Munk"; break;
            case 4 : y_units = "Counts"; break;
            case 5 : y_units = "Volts"; break;
            case 6 : y_units = "Degrees"; break;
            case 7 : y_units = "Milliamps"; break;
            case 8 : y_units = "Millimeters"; break;
            case 9 : y_units = "Millivolts"; break;
            case 10 : y_units = "Log (1/R)"; break;
            case 11 : y_units = "Percent"; break;
            case 12 : y_units = "Intensity"; break;
            case 13 : y_units = "Relative Intensity"; break;
            case 14 : y_units = "Energy"; break;
            case 16 : y_units = "Decibel"; break;
            case 19 : y_units = "Temperature (F)"; break;
            case 20 : y_units = "Temperature (C)"; break;
            case 21 : y_units = "Temperature (K)"; break;
            case 22 : y_units = "Index of Refraction [N]"; break;
            case 23 : y_units = "Extinction Coeff. [K]"; break;
            case 24 : y_units = "Real"; break;
            case 25 : y_units = "Imaginary"; break;
            case 26 : y_units = "Complex"; break;
            case -128 : y_units = "Transmission"; break;
            case -127 : y_units = "Reflectance"; break;
            case -126 : y_units = "Arbitrary or Single Beam with Valley Peaks"; break;
            case -125 : y_units = "Emission"; break;
           }
                        // lecture du reste du SPCHDR
                   // Cas general
          if (ftflgs == 0) {
            fichier.skipBytes(512-30);
           } else {
                   // Cas ou les labels des axes sont donnes textuellement
            fichier.skipBytes(188);
            byte b;
            int i = 0; x_units="";
            do { b = fichier.readByte(); x_units += (char) b; i++; } while (b != 0);
            int j = 0; y_units="";
            do { b = fichier.readByte(); y_units += (char) b; j++; } while (b != 0);
            fichier.skipBytes(512-30-188-i-j);
           }
                                // Lecture de SUBHDR (512 octets)
          fichier.skipBytes(32);         // (on s'en fout)
                                // Lecture des donnees
          My_ZoneVisu.tableau_points=new double[Nbpoints];
          if (fexp == 0x80) {
            for (int i = 0 ; i < Nbpoints ; i++) {
              My_ZoneVisu.tableau_points[i] = NumericDataUtils.convToIntelFloat(fichier.readInt());
             }
           } else {
            for (int i = 0 ; i < Nbpoints ; i++) {
              My_ZoneVisu.tableau_points[i] = NumericDataUtils.convToIntelInt(fichier.readInt());
             }
           }
        } catch (Exception e) { Current_Error = "SPC file corrupted"; return false; }
       Datatype="XYDATA";
       return true;
     } 
        // Il s'agit d'un fichier JCAMP
                // On met tout le fichier dans la variable globale "Vector texte"
    try {
      URL url=new URL(getDocumentBase(),filename);
      InputStream stream = url.openStream();
//      DataInputStream fichier = new DataInputStream(stream);
      BufferedReader fichier = new BufferedReader(new InputStreamReader(stream));
      texte=new Vector();
      String s;
      while ((s=fichier.readLine())!=null) {
        texte.addElement(s);
       }
      nbLignes=texte.size();
     } catch (Exception e) { return false; }
   int My_Counter=0;
   String uneligne="";
   while (My_Counter<nbLignes) {
     try {
       StringTokenizer mon_token;
       do {
         uneligne=(String) texte.elementAt(My_Counter);
         My_Counter++;
         mon_token=new StringTokenizer(uneligne," ");
       } while (My_Counter<nbLignes && mon_token.hasMoreTokens()==false);
       if (mon_token.hasMoreTokens()==true) {
         String keyword=mon_token.nextToken();
         if (StringDataUtils.compareStrings(keyword,"##TITLE=")==0) TexteTitre=uneligne.substring(9);
         if (StringDataUtils.compareStrings(keyword,"##FIRSTX=")==0) Firstx=Double.valueOf(mon_token.nextToken()).doubleValue();
         if (StringDataUtils.compareStrings(keyword,"##LASTX=")==0) Lastx=Double.valueOf(mon_token.nextToken()).doubleValue();
         if (StringDataUtils.compareStrings(keyword,"##YFACTOR=")==0) YFactor=Double.valueOf(mon_token.nextToken()).doubleValue();
         if (StringDataUtils.compareStrings(keyword,"##NPOINTS=")==0) Nbpoints=Integer.valueOf(mon_token.nextToken()).intValue();
         if (StringDataUtils.compareStrings(keyword,"##XUNITS=")==0) x_units=uneligne.substring(10);
         if (StringDataUtils.compareStrings(keyword,"##YUNITS=")==0) y_units=uneligne.substring(10);
         if (StringDataUtils.compareStrings(keyword,"##.OBSERVE")==0 && StringDataUtils.compareStrings(mon_token.nextToken(),"FREQUENCY=")==0)
                                                nmr_observe_frequency=Double.valueOf(mon_token.nextToken()).doubleValue();
         if (StringDataUtils.compareStrings(keyword,"##XYDATA=")==0 && StringDataUtils.compareStrings(mon_token.nextToken(),"(X++(Y..Y))")==0) Datatype="XYDATA";
         if (StringDataUtils.compareStrings(keyword,"##XYDATA=(X++(Y..Y))")==0) Datatype="XYDATA";
         if (StringDataUtils.compareStrings(keyword,"##PEAK")==0 && StringDataUtils.compareStrings(mon_token.nextToken(),"TABLE=")==0 && StringDataUtils.compareStrings(mon_token.nextToken(),"(XY..XY)")==0) Datatype="PEAK TABLE";
         if (StringDataUtils.compareStrings(keyword,"##PEAK")==0 && StringDataUtils.compareStrings(mon_token.nextToken(),"TABLE=(XY..XY)")==0) Datatype="PEAK TABLE";
                                            }
        } catch (Exception e) {}
     }
   /*if(getParameter("XUPPERLIMIT") != null && getParameter("XLOWERLIMIT") != null){
		Firstx = Double.valueOf(getParameter("XLOWERLIMIT"));
		Lastx = Double.valueOf(getParameter("XUPPERLIMIT"));
   }*/
   if (Datatype.compareTo("UNKNOWN")==0) return false;
   if (Datatype.compareTo("PEAK TABLE")==0 && x_units.compareTo("?")==0) x_units="M/Z";
              // conversion Hz --> PPM
   if (StringDataUtils.truncateEndBlanks(x_units).compareTo("HZ")==0 && nmr_observe_frequency!=shitty_starting_constant) {
            Firstx/=nmr_observe_frequency;
            Lastx/=nmr_observe_frequency;
            x_units="PPM.";
         }
   String resultat_move_points=Move_Points_To_Tableau();
   if (resultat_move_points.compareTo("OK")!=0) {
          Current_Error=resultat_move_points;
          return false;
         }
   return true;  
 }
  public void Really_Load_File(String chaine_a_lire) {
	/*if(getParameter("XUPPERLIMIT") != null && getParameter("XLOWERLIMIT") != null){
		System.out.println("In here!");
		Firstx = Double.valueOf(getParameter("XLOWERLIMIT"));
		Lastx = Double.valueOf(getParameter("XUPPERLIMIT"));
	}else{*/
		Firstx=                shitty_starting_constant;
		Lastx=                 shitty_starting_constant;
	/*}*/
	Nbpoints=              shitty_starting_constant;
    nmr_observe_frequency= shitty_starting_constant;
    TexteTitre = "";
    YFactor = 1;
    My_ZoneVisu.Draw_Texte("Drawing graphics, please wait...");
    if (initFile(chaine_a_lire)) {
              My_ZoneVisu.Init_File();
              //if (Reverse.getState()) { My_ZoneVisu.Flag_Reverse=true; Flag_Reverse=true; } else Flag_Redraw=true;
              if (Flag_Reverse) { My_ZoneVisu.Flag_Reverse=true; } else Flag_Redraw=true;
            }
       else My_ZoneVisu.Draw_Texte("Bad file or filename"+Current_Error);
  }
  public void Write_JCamp() {
//
//		Write JCAMP-DX records to new file on client
//
// Commented by Shravan Sadasivan for testing
/* if (inNavigator) {
	  if (hasPrivilege == false) {
	     System.out.println("\n\nNecessary write privilege has not been granted.");
	     return;
          }
          PrivilegeManager.enablePrivilege("UniversalFileAccess");
          PrivilegeManager.enablePrivilege("UniversalPropertyRead");
        } */
	String dirName, filName2, newLine;
	PrintWriter pw;
	filName2 = getParameter("SOURCE_FILE").toLowerCase();
	int ipos = filName2.lastIndexOf("/") + 1;
	if (ipos == -1) ipos = 0;
	filName2 = filName2.substring(ipos, filName2.length());
	System.out.println("FileName : " + filName2);
	try {
	   if (inNavigator) {
		  saveDialog.setFile(filName2);
		  saveDialog.show();
		  dirName = saveDialog.getDirectory();
		  filName2 = dirName + saveDialog.getFile();
	   }
	   pw = new PrintWriter(new FileWriter(filName2));
	   for (int ii=0; ii<texte.size(); ii++) {
		newLine = (String)texte.elementAt(ii);
		pw.println(newLine);
	   }
	   pw.close();
	} catch (Exception e) {
	    System.out.println("\nClient: Unable to write local JCAMP-DX file. " + e);
	}
  }
  public void Do_Zoomin() {
    My_ZoneVisu.Draw_Texte("Zooming in, please wait...");
    Flag_Zoomin=true;
   }
  public void Do_Zoomback() {
    My_ZoneVisu.Draw_Texte("Zooming back, please wait...");
    Flag_Zoomback=true;
   }
  public void Do_Zoomout() {
    My_ZoneVisu.Draw_Texte("Drawing whole graphics, please wait...");
    Flag_Zoomout=true;
   }
  /*
	 Shravan Sadasivan - Changes need to be made to this section
	 at the time of implementation of the parameterization of the Grid option
	 requirement.
   */
  public void Do_Grid() {
    My_ZoneVisu.Draw_Texte("Redrawing with grid, please wait...");
    //My_ZoneVisu.Flag_Grid=Grid.getState();
    Flag_Redraw=true;
   }
  public void Do_Reverse() {
    My_ZoneVisu.Draw_Texte("Reversing graphics, please wait...");
    My_ZoneVisu.Flag_Reverse=Reverse.getState();
    Flag_Reverse=true;
   }
  /*
  	 Shravan Sadasivan - Changes need to be made to this section
  	 at the time of implementation of the parameterization of the Integrate option
  	 requirement.
   */
  public void Do_Integrate() {
    if (Integrate.getState())  {
         My_ZoneVisu.Draw_Texte("Integrating peaks, please wait...");
         Flag_Integrate=true;
     } else {
         My_ZoneVisu.Flag_Integrate=false;
         My_ZoneVisu.Draw_Texte("Redrawing graphics, please wait...");
         Flag_Redraw=true;
    }
  }
  public void Do_Find_Peak() {
    My_ZoneVisu.Draw_Texte("Finding peak, please wait...");
    Flag_Find_Peak=true;
   }
  public void Do_Write_JCamp() {
    Flag_Write_JCamp=true;
   }
  public void Do_Load_File() {
      Frame Frame_Load_File=(Frame) getParent();
      Dialogue_Load_File=new SaisieDlg(Frame_Load_File,"Load file...","Enter the filename :");
      Dialogue_Load_File.show();
      Flag_Dialogue_File_Enabled=true;
  }
   public boolean handleEvent(Event evt) {
    if (evt.target==Load_File && evt.id==Event.ACTION_EVENT) {
         Do_Load_File();
         return true;
       }
    if (evt.target==Zoom_In && evt.id==Event.ACTION_EVENT) {
         Do_Zoomin();
         return true;
       }
    if (evt.target==Zoom_Back && evt.id==Event.ACTION_EVENT) {
         Do_Zoomback();
         return true;
       }
    if (evt.target==Zoom_Out && evt.id==Event.ACTION_EVENT) {
         Do_Zoomout();
         return true;
       }
    if (evt.target==Reverse && evt.id==Event.ACTION_EVENT) {
         Do_Reverse();
         return true;
       }
    if (evt.target==Grid && evt.id==Event.ACTION_EVENT) {
         Do_Grid();
         return true;
       }
    if (evt.target==Integrate && evt.id==Event.ACTION_EVENT) {
         Do_Integrate();
         return true;
       }
    if (evt.target==Find_Peak && evt.id==Event.ACTION_EVENT) {
         Do_Find_Peak();
         return true;
       }
    if (evt.target==JCamp && evt.id==Event.ACTION_EVENT) {
         Do_Write_JCamp();
         return true;
       }
    return super.handleEvent(evt);
  }
 }

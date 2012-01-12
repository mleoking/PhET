/*
 * File Name   : ZoneVisu.java
 * Description : All visual graphing elements and functionality is localized in this class
 * Author      : Shravan Sadasivan
 * Organization: Department Of Chemistry,
 *               The Ohio State University
 */
package org.jmol.jcamp;
import java.awt.*;
import java.text.DecimalFormat;
import org.jmol.jcamp.utils.*;
public class ZoneVisu extends Canvas {
   private GraphCharacteristics _graphDataUtils;
   // -Variables to fill in values from the dx files
   // -Each variable in this section has a corresponding value in the *.dx file being loaded
   public double Firstx;
   public double Lastx;
   public double Miny;
   public double Maxy;
   public double YFactor;
   public int xLowerLimit = 0;
   public int xUpperLimit = 0;
   public int nbLignes;
   public int Nbpoints;
   public String TexteTitre;
   public String x_units;
   public String y_units;
   public int typedata;
   public boolean reverse = false;
   // -Values of the variables converted to real values
   double RealFirstx;
   double RealLastx;
   double Last_RealFirstx;
   double Last_RealLastx;
   double Last_Firstx;
   double Last_Lastx;
   double Sav_Firstx;
   double Sav_Lastx;
   boolean Flag_Clickable_Peaks=false;
   int Nb_Clickable_Peaks;
   double Peak_Start[];
   double Peak_Stop[];
   String Peak_Html[]; 
   boolean Flag_Load_Now_Html=false;
   String Name_Load_Now_Html;
   int Sav_Nbpoints_a_tracer;
   int Nbpoints_a_tracer;
   int prempoint;
   String Y_Values;
   String ShowTitle;
    int shitty_starting_constant;
    double tableau_points[];
    double tableau_integrate[];
    double Incrx;             // Only used for the XYDATAS
    double Multx;             // Only used for the PEAK TABLES and FIND PEAK
    double Multy;
    double Maxintegrate;     // Only used for Integrate
    double x;
    double y;
    double ax;
    double ay;
    double xd;
    double xf;
    int largeur; //Width of the ZoneVisua component
    int hauteur; //Height of the ZoneVisua component
    int largeur_gfx; // Constant based on the value of the width of the canvas
    int hauteur_gfx; // Constant based on the value of the height of the canvas
    int decalx_gfx; // Border width
    int decaly_gfx; // Border Height
    int hauteur_bandeau;
    boolean Flag_Grid=false;
    boolean Flag_Reverse=false;
    boolean Flag_Integrate=false;
    int location_textetitre;
    Image BufImg;
    Graphics BufGra;
    Image SavBufImg;
    Graphics SavBufGra;
    Image ZoomBufImg;
    Graphics ZoomBufGra;
    int x1_zoom;
    int x2_zoom;
    int indice;
    String Un_Nombre;
  public void init() {
    largeur=size().width; //Width of the component
    hauteur=size().height; //Height of the component
    BufImg=createImage(largeur,hauteur); //Create an image of the width and height of the ZoneVisua component
    BufGra=BufImg.getGraphics();
    // Filling the color to the drawing area
    BufGra.setColor(Color.white);
    BufGra.fillRect(1,1,largeur-2,hauteur-2);
    // Filling the color to the border area of thickness 2X2
    BufGra.setColor(Color.black);
    BufGra.drawRect(0,0,largeur-1,hauteur-1);
    BufGra.setFont(new Font("TimesRoman",Font.PLAIN,12));
    hauteur_gfx=(hauteur*3)/4; // Some random calculation for a constant based on the height of the canvas
    /*
    	Shravan Sadasivan - Code present to check if the Y Values should be displayed on the screen
    						Modification required
    */
    if (Y_Values!=null && Y_Values.compareTo("HIDE")==0) {
         decalx_gfx=5;
         largeur_gfx=largeur-decalx_gfx-10;
      } else {
         decalx_gfx=60;
         largeur_gfx=largeur-decalx_gfx-15;
    }
    decaly_gfx=hauteur/8;
    location_textetitre=125+decalx_gfx/2;
    hauteur_bandeau=hauteur-hauteur_gfx-decaly_gfx; //Height of the on screen text elements
    x1_zoom=decalx_gfx;
    x2_zoom=decalx_gfx+largeur_gfx;
    //x1_zoom = xLowerLimit;
    //x2_zoom = xUpperLimit;
    SavBufImg=createImage(largeur,hauteur);
    SavBufGra=SavBufImg.getGraphics();
    ZoomBufImg=createImage(largeur,hauteur); //Variables required for zooming into the graph
    ZoomBufGra=ZoomBufImg.getGraphics(); //Variables required for zooming into the graph
   }
  public void setGraphDataUtils(GraphCharacteristics graphDataUtils){
		this._graphDataUtils = graphDataUtils;
  }
  /**
   *
   */
  public void Draw_Texte(String tam) {
    BufGra.setColor(Color.white);
    BufGra.fillRect(20,hauteur-decaly_gfx/6-10,(largeur*3)/4,14);
    if(_graphDataUtils.getAxisTextColor() == null){
    	BufGra.setColor(new Color(255,60,120));
	}else{
		setGraphicsColor(_graphDataUtils.getAxisTextColor());
	}
    BufGra.drawString(tam,20,hauteur-decaly_gfx/6);
    repaint();
  }
  public void Init_File() {
    nbLignes=((Visua)getParent()).nbLignes;
  	Firstx=((Visua)getParent()).Firstx;
  	Lastx=((Visua)getParent()).Lastx;
    YFactor=((Visua)getParent()).YFactor;
    Nbpoints=((Visua)getParent()).Nbpoints;
    TexteTitre=((Visua)getParent()).TexteTitre;
    x_units=((Visua)getParent()).x_units;
    y_units=((Visua)getParent()).y_units;
    if (((Visua)getParent()).Datatype.compareTo("XYDATA")==0) typedata=0;
    if (((Visua)getParent()).Datatype.compareTo("PEAK TABLE")==0) typedata=1;
    shitty_starting_constant=((Visua)getParent()).shitty_starting_constant;
    Flag_Integrate=false;
    RealFirstx=Firstx;
    RealLastx=Lastx;
    Last_RealFirstx=Firstx;
    Last_RealLastx=Lastx;
    Sav_Firstx=Firstx;
    Sav_Lastx=Lastx;
    Determine_Extrem_y();
    Integrate();
   }
  //Shravan Sadasivan
  //Apply changes to this section of the code for zooming in on a portion of the graph
  /**
   * 
   */
  public void Determine_Extrem_y() {
    
   if (typedata==0) {            // XYDATA
     Miny=tableau_points[0];
     Maxy=tableau_points[0];
     for (int i=0;i<Nbpoints;i++) {
       if (tableau_points[i]<Miny) Miny=tableau_points[i];
       if (tableau_points[i]>Maxy) Maxy=tableau_points[i];
     }
     Miny=Miny*YFactor;
     Maxy=Maxy*YFactor;     
    }
   
   if (typedata==1) {            // PEAK TABLE
     Miny=0;
     Maxy=tableau_points[1];
     for (int i=1;i<Nbpoints;i++) {
       if (tableau_points[i*2+1]>Maxy) Maxy=tableau_points[i*2+1];
     }
    }
  }
/**
 * 
 * @param tam
 * @return String
 */
  public String Reduce_String_EndBlanks(String tam) {
    while (tam.charAt(tam.length()-1)==' ') tam=tam.substring(0,tam.length()-1);
    return tam;
  }
/**
 * 
 *
 */
  public void drawText() {
	
    if (!(ShowTitle!=null && ShowTitle.compareTo("HIDE")==0)) { //Displaying the title of the graph
  		if(_graphDataUtils.getTextColor() == null){
             BufGra.setColor(new Color(250,60,250));
  		}else{
  		   setGraphicsColor(_graphDataUtils.getTextColor());
  		}
      BufGra.drawString(TexteTitre,location_textetitre,12);    
    }
    //Drawing the labels of the units on the X and Y axis of the graph
    BufGra.setColor(new Color(174,0,226));
    BufGra.drawString(x_units,largeur-Reduce_String_EndBlanks(x_units).length()*5-250,hauteur-decaly_gfx/8);
    BufGra.drawString(y_units,5,hauteur-hauteur_gfx-decaly_gfx-(hauteur_bandeau*11)/20);
    
  }
  /**
   * 
   * @param tam
   * @return String
   */
  public String Reduce_String_0(String tam) {
    double sav=Double.valueOf(tam).doubleValue();
    while (tam.length()>0 && tam.charAt(tam.length()-1)=='0') tam=tam.substring(0,tam.length()-1);
    if (tam.length()==0 || tam.compareTo("-")==0) return "0";
    if (tam.charAt(tam.length()-1)=='.') tam=tam.substring(0,tam.length()-1);
    if (tam.length()==0) return "0";
    if (sav!=Double.valueOf(tam).doubleValue()) return String.valueOf(sav); 
    return tam;
    
 }
  private void drawXAxis(){
    
    
  }
  
  public void drawAxis() {
    int xpoints[]=new int[20];     // The grid in numbers
    int ypoints[]=new int[20];
    int indicex=0;
    int indicey=0;
   if(_graphDataUtils.getAxisColor() == null){
     BufGra.setColor(Color.blue);
   }else{
   	 setGraphicsColor(_graphDataUtils.getAxisColor());
   }
   BufGra.drawLine((decalx_gfx*7)/8,hauteur-(decaly_gfx*15)/16,largeur,hauteur-(decaly_gfx*15)/16);
   BufGra.drawLine((decalx_gfx*15)/16,hauteur-(decaly_gfx*7)/8,(decalx_gfx*15)/16,hauteur_bandeau/2);
   if(_graphDataUtils.getAxisColor() == null){
   	BufGra.setColor(Color.blue.brighter().brighter());
   }else{
   	setGraphicsColor(_graphDataUtils.getAxisTextColor());
   }
             // X Axis
   double dixiemegap=Math.abs((RealLastx-RealFirstx)/15);
   double lmytest=5e20;
   double mytest=1e20;
   while (!(lmytest>dixiemegap && mytest<=dixiemegap) && lmytest>1e-15) {
      lmytest/=5;
      mytest/=2;
      if (!(lmytest>dixiemegap && mytest<=dixiemegap)) {
              lmytest/=2;
              mytest/=5;
           }
    }
   double Start_With;
   double Multiply_Factor_CommunicatorPC;
   if (Lastx>Firstx) {
        mytest=lmytest;
        Multiply_Factor_CommunicatorPC=1.0000000001;
                     } else {
        mytest=-lmytest;
        Multiply_Factor_CommunicatorPC=0.9999999999;
                     }
   Start_With=Math.ceil((RealFirstx+((RealFirstx-RealLastx)*decalx_gfx)/(16*largeur-decalx_gfx))/mytest)*mytest;
                       // draw the small graduations
   for (double i=Start_With-mytest;
                     ((i-RealFirstx)*largeur_gfx)/(RealLastx-RealFirstx)<largeur-decalx_gfx;
                     i+=mytest/5) {
         if (((i-RealFirstx)*largeur_gfx)/(RealLastx-RealFirstx)>0)
           BufGra.drawLine((int) Math.round(decalx_gfx+((i-RealFirstx)*largeur_gfx)/(RealLastx-RealFirstx)),
                           hauteur-(decaly_gfx*11)/12,
                           (int) Math.round(decalx_gfx+((i-RealFirstx)*largeur_gfx)/(RealLastx-RealFirstx)),
                           hauteur-(decaly_gfx*15)/16);
      }
                       // draw the large graduations and values
   for (double i=Start_With;
                     ((i-RealFirstx)*largeur_gfx)/(RealLastx-RealFirstx)<largeur-decalx_gfx;
                     i+=mytest*Multiply_Factor_CommunicatorPC) {
         xpoints[indicex]=(int) Math.round(decalx_gfx+((i-RealFirstx)*largeur_gfx)/(RealLastx-RealFirstx));
         BufGra.drawLine(xpoints[indicex],
                         hauteur-(decaly_gfx*7)/8,
                         xpoints[indicex],
                         hauteur-(decaly_gfx*15)/16);
         indicex++;
               // trop de precision sur Communicator... plein de 0 et des merdes...
         String Une_Solution=String.valueOf(i);
         if (Une_Solution.length()>10) {
               if (Une_Solution.indexOf('E')!=-1)
                  Une_Solution=Une_Solution.substring(0,Une_Solution.indexOf('E')-1)+"e"+Une_Solution.substring(Une_Solution.indexOf('E')+1);
               if (Une_Solution.indexOf('e')==-1) Une_Solution=Une_Solution.substring(0,9);
                    else Une_Solution=String.valueOf(
                                      Math.pow(10,Double.valueOf(Une_Solution.substring(Une_Solution.indexOf('e')+1)).doubleValue())*
                                      Double.valueOf(Une_Solution.substring(0,Math.min(9,Une_Solution.indexOf('e')-1))).doubleValue()
                                                      );
                                      }
         Une_Solution=Reduce_String_0(Une_Solution);
         if (Math.abs(Double.valueOf(Une_Solution).doubleValue())<1e-4 && Math.abs(mytest)>1e-3) Une_Solution="0";
         BufGra.drawString(Une_Solution,
                           (int) Math.round(decalx_gfx+((i-RealFirstx)*largeur_gfx)/(RealLastx-RealFirstx))-Une_Solution.length()*2,
                           hauteur-decaly_gfx/2);
      }
             // Y Axis
   dixiemegap=(Maxy-Miny)/15;
   lmytest=5e20;
   mytest=1e20;
   while (!(lmytest>dixiemegap && mytest<=dixiemegap) && lmytest>1e-20) {
      lmytest/=5;
      mytest/=2;
      if (!(lmytest>dixiemegap && mytest<=dixiemegap)) {
              lmytest/=2;
              mytest/=5;
           }
    }
   mytest=lmytest;
                      // draw the small graduations
   for (double i=(Math.ceil(Miny/mytest))*mytest-mytest;
                     ((i-Miny)*hauteur_gfx)/(Maxy-Miny)<hauteur-decaly_gfx-hauteur_bandeau/2;
                     i+=mytest/5) {
         if (((i-Miny)*hauteur_gfx)/(Maxy-Miny)>0)
           BufGra.drawLine((decalx_gfx*11)/12,
                           hauteur-decaly_gfx- (int) Math.round(((i-Miny)*hauteur_gfx)/(Maxy-Miny)),
                           (decalx_gfx*15)/16,
                           hauteur-decaly_gfx- (int) Math.round(((i-Miny)*hauteur_gfx)/(Maxy-Miny)));
        }
                      // draw the large graduations and values
   for (double i=(Math.ceil(Miny/mytest))*mytest;
                     ((i-Miny)*hauteur_gfx)/(Maxy-Miny)<hauteur-decaly_gfx-hauteur_bandeau/2;
                     i+=mytest*1.0000000001) {
         ypoints[indicey]=hauteur-decaly_gfx- (int) Math.round(((i-Miny)*hauteur_gfx)/(Maxy-Miny));
         BufGra.drawLine((decalx_gfx*7)/8,
                         ypoints[indicey],
                         (decalx_gfx*15)/16,
                         ypoints[indicey]);
         indicey++;
               // trop de precision sur Communicator... plein de 0 et des merdes...
         String Une_Solution=String.valueOf(i);
         if (Une_Solution.length()>10) {
               if (Une_Solution.indexOf('E')!=-1)
                  Une_Solution=Une_Solution.substring(0,Une_Solution.indexOf('E')-1)+"e"+Une_Solution.substring(Une_Solution.indexOf('E')+1);
               if (Une_Solution.indexOf('e')==-1) Une_Solution=Une_Solution.substring(0,9);
                    else Une_Solution=String.valueOf(
                                      Math.pow(10,Double.valueOf(Une_Solution.substring(Une_Solution.indexOf('e')+1)).doubleValue())*
                                      Double.valueOf(Une_Solution.substring(0,Math.min(9,Une_Solution.indexOf('e')-1))).doubleValue()
                                                      );
                                      }
         Une_Solution=Reduce_String_0(Une_Solution);
         if (Math.abs(Double.valueOf(Une_Solution).doubleValue())<1e-4 && Math.abs(mytest)>1e-3) Une_Solution="0";
         /*
           Shravan Sadasivan - Changes to be made to this section in order to rotate the Y Axis labels by 90 Degrees
           					   Changes to be made to this section for optional display of Y Axis labels based on the
           					   type of spectral data being used.
         */
         if (!(Y_Values!=null && Y_Values.compareTo("HIDE")==0))
			 if(y_units.equalsIgnoreCase("ABSORBANCE") ||
					y_units.equalsIgnoreCase("%T") ||
					y_units.equalsIgnoreCase("TRANSMITTANCE") ||
					y_units.equalsIgnoreCase("RELATIVE ABUNDANCE")){
			   BufGra.drawString(Une_Solution,
							   (decalx_gfx*3)/4-Une_Solution.length()*5,
							   hauteur-decaly_gfx+4- (int) Math.round(((i-Miny)*hauteur_gfx)/(Maxy-Miny)));
			 }
      }
   if (Flag_Grid) {
       BufGra.setColor(new Color(150,100,255));
       for (int i=0;i<indicex;i++)
         for (int j=hauteur-(decaly_gfx*15)/16;j>hauteur_bandeau/2;j-=(hauteur-(decaly_gfx*15)/16-hauteur_bandeau/2)/32)
          BufGra.drawLine(xpoints[i],j,xpoints[i],j-(hauteur-(decaly_gfx*15)/16-hauteur_bandeau/2)/64);
       for (int i=0;i<indicey;i++)
         for (int j=(decalx_gfx*15)/16;j<largeur;j+=(largeur-(decalx_gfx*15)/16)/32)
          BufGra.drawLine(j,ypoints[i],j+(largeur-(decalx_gfx*15)/16)/64,ypoints[i]);
   }
 }
 
 public void Trace_PEAK_TABLE() {
   BufGra.setColor(Color.black);
   double rxd;
   double rxf;
   if (Multx>0) { rxd=xd; rxf=xf; } else { rxd=xf; rxf=xd; }
   for (int i=0;i<Nbpoints;i++)
     if (tableau_points[i*2]>=rxd && tableau_points[i*2]<=rxf)
       BufGra.drawLine(decalx_gfx+(int)Math.round((tableau_points[i*2]-RealFirstx)*Multx),(int)Math.round(hauteur-tableau_points[i*2+1]*YFactor*Multy)-decaly_gfx,
                       decalx_gfx+(int)Math.round((tableau_points[i*2]-RealFirstx)*Multx),hauteur-decaly_gfx);
 }
 /*
 	Shravan Sadasivan - Changes should be made to this section in order to print the data provided for the peaks,
 	on top of the peaks. This is the method that traces the yellow line on top of the Integrated graph
 */
 public void Trace_Integrate() {
	String point = null;
	String integrateValue = null;
	String trueXValue = null;
	int xIntegrateValue = 0;
	int yIntegrateValue = 120;
	String lastPoint = null;
	// NumberFormat form = NumberFormat.getInstance();
	DecimalFormat decForm = new DecimalFormat("#####");
    if (Incrx>0) x=0; else x=largeur_gfx;
    ax=x;
    if (Incrx>0) prempoint=(int) Math.ceil(((xd-Firstx)*Nbpoints)/(Lastx-Firstx)); else
                 prempoint=(int) Math.ceil(((Lastx-xf)*Nbpoints)/(Lastx-Firstx));
    double Multintegrate=hauteur_gfx/Maxintegrate;
    Nbpoints_a_tracer=Sav_Nbpoints_a_tracer;
    int indicetableau=0;
    if (!Flag_Reverse) ay=tableau_integrate[indicetableau]*Multintegrate; else ay=(Maxintegrate-tableau_integrate[indicetableau])*Multintegrate;
    while (Nbpoints_a_tracer>0 && indicetableau<Nbpoints) {
		  /* Sets the color of the intergration curve */
		  if(_graphDataUtils.getIntegrateCurveColor() == null){
		      	BufGra.setColor(Color.orange);
		  }else{
		  		setGraphicsColor(_graphDataUtils.getIntegrateCurveColor());
		  }
		  if (prempoint>1) {
                  prempoint--;
              } else
          if (prempoint==1) {
                       if (!Flag_Reverse) ay=tableau_integrate[indicetableau]*Multintegrate; else ay=(Maxintegrate-tableau_integrate[indicetableau])*Multintegrate;
                       ax=x;
                       x+=Incrx;
                       prempoint--;
              } else { 		//	Control enters this loop when prempoint is 0
                       if (!Flag_Reverse) y=tableau_integrate[indicetableau]*Multintegrate; else y=(Maxintegrate-tableau_integrate[indicetableau])*Multintegrate;
                       if (y!=0 && ay!=0)
                       {
						BufGra.drawLine((int)Math.round(ax)+decalx_gfx,(int)Math.round(hauteur-ay)-decaly_gfx,
                                                 (int)Math.round(x)+decalx_gfx,(int)Math.round(hauteur-y)-decaly_gfx);
                        trueXValue = StringDataUtils.reduceDataPrecision(String.valueOf(x(RealFirstx+((x-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx)));
                        point = _graphDataUtils.getIntegrationCurveAreaValue(Double.valueOf(trueXValue));
					    if(point != null){
							if(_graphDataUtils.getIntegrateTextColor() == null){
						    	BufGra.setColor(Color.black);
							}else{
								setGraphicsColor(_graphDataUtils.getIntegrateTextColor());
							}
							System.out.println(point);
							/* xIntegrateValue = (int)Math.round(Double.valueOf(decForm.format(x)));
							yIntegrateValue = (int)Math.round(hauteur-y)-decaly_gfx-10; */
							BufGra.drawString(point,xIntegrateValue,yIntegrateValue);
							point = null;
					   	}
                       }
                   ax=x; ay=y;
                   x+=Incrx;
                   Nbpoints_a_tracer--;
              }
          indicetableau++;
      }
 }
 public void Trace_XYDATA() {
    if (Flag_Integrate) Trace_Integrate();
	if(_graphDataUtils.getGraphCurveColor() == null){
		BufGra.setColor(Color.black);
	}else{
		setGraphicsColor(_graphDataUtils.getGraphCurveColor());
	}
    if (Incrx>0) x=0; else x=largeur_gfx;
    ax=x;
    if (Incrx>0) prempoint=(int) Math.ceil(((xd-Firstx)*Nbpoints)/(Lastx-Firstx)); else
                 prempoint=(int) Math.ceil(((Lastx-xf)*Nbpoints)/(Lastx-Firstx));
    Nbpoints_a_tracer=Sav_Nbpoints_a_tracer;
    int indicetableau=0;
    ay=(tableau_points[0]*YFactor-Miny)*Multy;
    while (Nbpoints_a_tracer>=0 && indicetableau<Nbpoints) {
          if (prempoint>1) {
                  prempoint
                  --;
              } else
          if (prempoint==1) {
                       ay=(tableau_points[indicetableau]*YFactor-Miny)*Multy;
                       ax=x;
                       x+=Incrx;
                       prempoint--;
              } else {
                       y=(tableau_points[indicetableau]*YFactor-Miny)*Multy;
					   BufGra.drawLine((int)Math.round(ax)+decalx_gfx,(int)Math.round(hauteur-ay)-decaly_gfx,
									   (int)Math.round(x)+decalx_gfx,(int)Math.round(hauteur-y)-decaly_gfx);
				   	   ax=x; ay=y;
                   x+=Incrx;
                   Nbpoints_a_tracer--;
           }
          indicetableau++;
      }
 }
 public boolean drawSpectra() {
    if (Firstx==shitty_starting_constant ||
        Lastx==shitty_starting_constant ||
        Miny==shitty_starting_constant ||
        Maxy==shitty_starting_constant ||
        YFactor==shitty_starting_constant ||
        Nbpoints==shitty_starting_constant ||
        Firstx==Lastx ||
        Miny==Maxy) return false;
    Sav_Nbpoints_a_tracer=(int) Math.floor((Nbpoints*(xf-xd))/(Lastx-Firstx))+1;
    Incrx=largeur_gfx/(double)Sav_Nbpoints_a_tracer;
    if (Firstx>Lastx) Incrx=-Incrx;
    Multx=largeur_gfx/(RealLastx-RealFirstx);
    Multy=hauteur_gfx/(Maxy-Miny);
    if (typedata==0) Trace_XYDATA();
    if (typedata==1) Trace_PEAK_TABLE();
    return true;
 }
  public void Draw_Graphics(double xdeb,double xfin) {
    xd=xdeb;
    xf=xfin;
    BufGra.setColor(Color.white);
    BufGra.fillRect(1,1,largeur-2,hauteur-2);
    BufGra.setColor(Color.black);
    drawAxis();
    if (!drawSpectra()) Draw_Texte("File corrupted/unregnognized file format/datas");
    drawText();
    SavBufGra.drawImage(BufImg,0,0,this);
    repaint();
  }
  public double antecedent(double x) {
     return RealFirstx+(x*(RealLastx-RealFirstx))/largeur_gfx;
   }
  public void Find_Peak() {
   if (typedata==1) Draw_Texte("Unavailable with Peak Data files");
   if (typedata==0) {
    if (x2_zoom<x1_zoom) { int tmp=x1_zoom; x1_zoom=x2_zoom; x2_zoom=tmp; }
    double Seuil1;
    double Seuil2;
    if (Incrx>0) {
         Seuil1=RealFirstx+((x1_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx;
         Seuil2=RealFirstx+((x2_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx;
                 } else {
         Seuil2=RealFirstx+((x1_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx;
         Seuil1=RealFirstx+((x2_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx;
                        }
	// -Shravan Sadasivan
	// -Hard coded values?
    double xpeakmax=666667;
    double ypeakmax=666667;
    double xpeakmin=666667;
    double ypeakmin=666667;
    double currmax=-666667e66;
    double currmin=666667e66;
	// -
    if (Incrx>0) x=0; else x=largeur_gfx;
    if (Incrx>0) prempoint=(int) Math.floor(((xd-Firstx)*Nbpoints)/(Lastx-Firstx)); else
                 prempoint=(int) Math.floor(((Lastx-xf)*Nbpoints)/(Lastx-Firstx));
    int indicetableau=0;
    Nbpoints_a_tracer=Sav_Nbpoints_a_tracer-1;
    while (Nbpoints_a_tracer>0 && indicetableau<Nbpoints) {
      if (prempoint>0) {
             prempoint--;
         } else {
             if (antecedent(x)>=Seuil1 && antecedent(x)<=Seuil2) {
                    y=tableau_points[indicetableau]*YFactor;
                    if (y>currmax) { xpeakmax=x; ypeakmax=y; currmax=y; }
                    if (y<currmin) { xpeakmin=x; ypeakmin=y; currmin=y; }
                  }
             x+=Incrx;
             Nbpoints_a_tracer--;
           }
      indicetableau++;
     }
    double valeur_teste=(f(Seuil1)+f(Seuil2))/2;
    double xpeak=666667;
    double ypeak=666667;
    if (Math.abs(ypeakmax-valeur_teste)>Math.abs(ypeakmin-valeur_teste)) { xpeak=xpeakmax; ypeak=ypeakmax; } else { xpeak=xpeakmin; ypeak=ypeakmin; }
    Draw_Texte("Peak found at X="+StringDataUtils.reduceDataPrecision(String.valueOf(antecedent(xpeak)))+" "+Reduce_String_EndBlanks(x_units)+" (Y="+StringDataUtils.reduceDataPrecision(String.valueOf(ypeak))+")");
    BufGra.setColor(Color.green);
    BufGra.drawLine(decalx_gfx+(int)Math.round(xpeak),(int)Math.round(hauteur-ypeak*Multy)-decaly_gfx,
                    decalx_gfx+(int)Math.round(xpeak),hauteur-decaly_gfx);
    repaint();
   }
 }
  public void Integrate() {
    tableau_integrate=new double[Nbpoints];
    double current_value=0;
    double seuil=Maxy/YFactor/1000;
    for (int i=0;i<Nbpoints;i++) {
      if (tableau_points[i]>seuil) current_value+=tableau_points[i];
      tableau_integrate[i]=current_value;
    }
    Maxintegrate=0;
    for (int i=0;i<Nbpoints;i++) if (tableau_integrate[i]>Maxintegrate) Maxintegrate=tableau_integrate[i];
  }
  /*
  	Shravan Sadasivan - This code can be removed since this functionality is being phased out for V1.0
  */
  /* BEGIN - REMOVE */
  public void Zoomin() {
    Last_RealFirstx=RealFirstx;     // pour Zoomback
    Last_RealLastx=RealLastx;
    Last_Firstx=Firstx;
    Last_Lastx=Lastx;
    if (x2_zoom<x1_zoom) { int tmp=x1_zoom; x1_zoom=x2_zoom; x2_zoom=tmp; }
    double tmp=RealFirstx+((x1_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx;
    RealLastx=RealFirstx+((x2_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx;
    //RealFirstx = x1_zoom;
    //RealLastx = x2_zoom;
    RealFirstx=tmp;
	
    Draw_Graphics(RealFirstx,RealLastx);
  }
  /**
   * 
   *
   */
  public void Zoomback() {
    double tmp1=RealFirstx;
    double tmp2=RealLastx;
    double tmp3=Firstx;
    double tmp4=Lastx;
    RealFirstx=Last_RealFirstx;
    RealLastx=Last_RealLastx;
    Firstx=Last_Firstx;
    Lastx=Last_Lastx;
    Last_RealFirstx=tmp1;
    Last_RealLastx=tmp2;
    Last_Firstx=tmp3;
    Last_Lastx=tmp4;
    Draw_Graphics(RealFirstx,RealLastx);
  }
  public void Zoomout() {
    RealFirstx=Firstx;
    RealLastx=Lastx;
    Draw_Graphics(RealFirstx,RealLastx);
  }
  /* END - REMOVE */
  public void Redraw() {
    Draw_Graphics(RealFirstx,RealLastx);
  }
  public void Reverse() {
    Last_RealFirstx=RealFirstx;     // pour Zoomback
    Last_RealLastx=RealLastx;
    Last_Firstx=Firstx;
    Last_Lastx=Lastx;
    double tmp1=RealFirstx;
    double tmp2=Firstx;
    RealFirstx=RealLastx;
    Firstx=Lastx;
    RealLastx=tmp1;
    Lastx=tmp2;
    Draw_Graphics(RealFirstx,RealLastx);
  }
  public double f(double tam) {
    if (tam<Sav_Firstx || tam>Sav_Lastx) return 666;
    if (typedata==0)    // XYDATA
         return tableau_points[(int)Math.round(((tam-Sav_Firstx)*Nbpoints)/(Sav_Lastx-Sav_Firstx))]*YFactor;
    if (typedata==1) {   // PEAK TABLE
         double distance=666667e66;
         double meilleure_approx=666667;
         for (int i=0;i<Nbpoints;i++) if (Math.abs(tableau_points[i*2]-tam)<distance) { meilleure_approx=tableau_points[i*2+1]; distance=Math.abs(tableau_points[i*2]-tam); }
         return meilleure_approx*YFactor;
      }
    return 666667;
   }
  public double x(double tam) {
      if (typedata==1) return Math.round(tam); else return tam;      // peak table, valeurs entieres seulement
   }
  /**
   * 
   * @param tam
   * @return String
   */
  public String trouve_f(double tam) {
    if (tam<Sav_Firstx || tam>Sav_Lastx) return "(outside spectra)";
    if (typedata==0)    // XYDATA
         return "Y="+StringDataUtils.reduceDataPrecision(String.valueOf(tableau_points[(int)Math.round(((tam-Sav_Firstx)*Nbpoints)/(Sav_Lastx-Sav_Firstx))]*YFactor));
    if (typedata==1) {   // PEAK TABLE
         double distance=666667e66;
         double meilleure_approx_x=666667;
         double meilleure_approx_y=666667;
         for (int i=0;i<Nbpoints;i++) if (Math.abs(tableau_points[i*2]-tam)<distance) { meilleure_approx_x=tableau_points[i*2]; meilleure_approx_y=tableau_points[i*2+1]; distance=Math.abs(tableau_points[i*2]-tam); }
         return "Y("+StringDataUtils.reduceDataPrecision(String.valueOf(meilleure_approx_x))+")="+StringDataUtils.reduceDataPrecision(String.valueOf(meilleure_approx_y*YFactor));
      }
    return "666667";
  }
  public void Do_Clickable_Peaks(double xvalue) {
    for (int i=0;i<Nb_Clickable_Peaks;i++)
      if (xvalue>Peak_Start[i] && xvalue<Peak_Stop[i]) {
            Flag_Load_Now_Html=true;
            Name_Load_Now_Html=Peak_Html[i];
          }
  }
  public boolean mouseDown(Event evt, int x, int y) {
        x1_zoom=x;
        BufGra.drawImage(SavBufImg,0,0,this);
        BufGra.setColor(Color.red);
        BufGra.drawLine(x,hauteur_bandeau,x,hauteur-(decaly_gfx*15)/16-1);
        
        //Centralize this set of formulae - Shravan
        Draw_Texte("X="+StringDataUtils.reduceDataPrecision(String.valueOf(x(RealFirstx+((x-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx)))+
                 " ; "+trouve_f(RealFirstx+((x-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx));
        if (Flag_Clickable_Peaks) Do_Clickable_Peaks(RealFirstx+((x-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx);
        ZoomBufGra.drawImage(BufImg,0,0,this);
        return true;
 }
  public boolean mouseDrag(Event evt, int x, int y) {
        x2_zoom=x;
        BufGra.drawImage(ZoomBufImg,0,0,this);
        BufGra.setColor(Color.red);
        BufGra.drawLine(x,hauteur_bandeau,x,hauteur-(decaly_gfx*15)/16-1);
        
        Draw_Texte("X="+StringDataUtils.reduceDataPrecision(String.valueOf(x(RealFirstx+((x1_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx)))+
                 " ; "+trouve_f(RealFirstx+((x1_zoom-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx)+
                   "  /  X="+StringDataUtils.reduceDataPrecision(String.valueOf(x(RealFirstx+((x-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx)))+
                 " ; "+trouve_f(RealFirstx+((x-decalx_gfx)*(RealLastx-RealFirstx))/largeur_gfx));
        return true;
 }
 /**
  * Method to set the color of the graphics output
  */
 private void setGraphicsColor(String color){
	if(color.equalsIgnoreCase("RED")){
		BufGra.setColor(Color.red);
	}else if(color.equalsIgnoreCase("ORANGE")){
		BufGra.setColor(Color.orange);
	}else if(color.equalsIgnoreCase("BLACK")){
		BufGra.setColor(Color.black);
	}else if(color.equalsIgnoreCase("BLUE")){
		BufGra.setColor(Color.blue);
	}else if(color.equalsIgnoreCase("GREEN")){
		BufGra.setColor(Color.green);
	}else if(color.equalsIgnoreCase("DARKGREEN")){
		BufGra.setColor(new Color(0,100,0));
	}else if(color.equalsIgnoreCase("LIME")){
		BufGra.setColor(new Color(0,255,0));
	}else if(color.equalsIgnoreCase("NAVY")){
		BufGra.setColor(new Color(0,0,102));
	}else if(color.equalsIgnoreCase("DARKRED")){
		BufGra.setColor(new Color(128,0,0));
	}else if(color.equalsIgnoreCase("MAGENTA")){
		BufGra.setColor(new Color(255,0,255));
	}else if(color.equalsIgnoreCase("PURPLE")){
		BufGra.setColor(new Color(128,0,128));
	}else if(color.equalsIgnoreCase("YELLOW")){
		BufGra.setColor(new Color(255,255,0));
	}else{
		BufGra.setColor(Color.black);
	}
 }
 public void paint(Graphics g) {
  g.drawImage(BufImg,0,0,this);
 }
 public void update(Graphics g) {
        paint(g);
 }
 }
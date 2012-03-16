/*  xrotor.c  

   Program using X11 to make MC simulation pictures in real time.
   Solves a classical spin model for thermodynamic states,
   then touching "t" turns on langevin time evolution of the spins.

   compile on apple to produce executable = rotor:
   cc -O -o rotor rotor.c -lm -lX11 -L/usr/X11R6/lib

*/

#define DEBUG		0
#define BITMAPDEPTH	1
#define TOO_SMALL	0
#define BIG_ENOUGH	1
#define FM		-1
#define AFM		1
#define VORTEX		3
#define ANTIVORTEX	1

#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
#include <X11/Xatom.h>
#include <stdio.h>    /* for usual io  */
#include <ctype.h>    /* for tolower() */
#include <stdlib.h>   /* for malloc()  */
#include <math.h>     /* for sin, etc  */
#include <unistd.h>   /* for usleep()  */
#include "icon.bmp"
#include "ran1.c"
#include "rangauss.c"
#include "init.c"
#include "Sites.c"

/* These are used as arguments to nearly every Xlib routine, so it saves 
   routine arguments to declare them global.  If there were 
   additional source files, they would be declared extern there. */

Display *display;
int screen_num;
Window win;
unsigned int win_width, win_height;	/* window size */
unsigned int new_width, new_height;	/* window size */
XSizeHints *size_hints;
int depth,darkcolor,lightcolor,deepcolor,brightcolor,yellowcolor,black,white;
long event_mask;
XIconSize *size_list;
XWMHints *wm_hints;
XClassHint *class_hints;
XTextProperty windowName, iconName;
GC gcbw,gcpen,gcdeep,gcbrt,gcred,gcyel,gcclear;
XFontStruct *small_font, *large_font;
unsigned int small_ht,large_ht;

unsigned int keycode;
unsigned int shiftkey=0;

static char *progname;      /* name this program was invoked by */
float xmin,xmax,ymin,ymax;  /* physical coordinate axis limits. */
float XScale, YScale;       /* conversion of physical coords. to pixel coords. */
float X0, Y0;               /* graph origin (xmin,ymin) in pixel coords. */
unsigned int Ytop;          /* pixel coordinate of top of spin diagram. */
int View=3;                 /* which spin components projected towards viewer. */
int DrawVorts=0;            /* flag to draw +/- for vortices located */
int field=0;   		    /* flag to control view of magnetic field. */
int longx=1;		    /* flag shows when nx > ny. */
double Irot=0.1;	    /* rotational inertia of the spin rotors. */
double gam=1.0;		    /* damping of Langevin dynamics. passed to difeq.*/
double sigtau=0.0;	    /* variance of random torques in Langevin dynamics. */

/* Dimensions for bar graphs and frame. */
#define BAR_WIDTH 7
#define BAR_HEIGHT 100
#define BORDER 6


/* Temp arrays used for Langevin 2nd order integration. */
double *oldx, *oldy;
double *tmpx, *tmpy;

/* Functions defined within this file.  */
void openwindow();
void getGC();
void load_font();
void draw_text();
void draw_nums();
void draw_frame();
void bar_graph();
char help();
void averages();
int **twodarray();

double  ONEPI;
double  TWOPI;
#include "arrow.c"
#include "energy.c"
#include "vcount.c"
#include "Metrop.c"
#include "Wolff.c"
#include "LangevinStep.c"

int main(argc, argv)
int argc;
char **argv;
{
 int window_size = BIG_ENOUGH;	/* or TOO_SMALL to display contents */
 char window_name[128], icon_name[32];
 XEvent report;
 int buttonnum;

 unsigned int bar_X0=1,bar_Y0=1,bar_shift,bar_X[8];
 unsigned int bar_width=10,bar_height=50;
 
 char ModelName[80], onechar;
 int  nx,ny,Nsites,Ns1,nsamp=0,nskip=200,kdt=10,bc=1;
 float  *x, *y, x1, y1, x2, y2;
 int **nbrs, *phase, *vort, nv, kyes, *ClSites, *virgin;
 double *sx,  *sy,  *sz, *phi, *bx, *by, *hx=NULL, *hy=NULL;
 int i,cntr,iseed,pause,redraw,re_init,nextstep,newsys,newscale;
 int newT,newH,newB,newD,graphs,algorithm,dynamics,text,togvort,showM; 
 long lsleep=10000L; /* sleep interval in micro-secs between moves. */
 int kex=-1;			/* FM model    */
 double J[3]={-1.0,-1.0,0.0}; 	/* XY coupling */
 double Bx=0.0,By=0.0;		/* applied magnetic field. */
 double Ka=0.5;			/* boundary anisotropy strength. */
 double D=0.0;			/* dipole magnetic moment strength for internal interactions. */
 double Dext=3.5;		/* dipole magnetic moment strength for displayed fields. */
 double xcntr=0.0,ycntr=0.0;	/* center point of magnet. */
 double beta,dS;		/* inverse temperature, spin increment in MC. */
 double dt=0.005,time=0.0;	/* time step and current time in dynamics. */
 double e=0.0,e0=0.0,E=0.0,m=0.0,M=0.0,rho=0.0,accrate=0.0,C=0.0,X_M=0.0,rN=1.0;
 double Tmax,dSmax,accmax,Mmax,rhomax,Emax,Cmax,X_Mmax;
 double mx=0.0,my=0.0,mz=0.0,Mmag=0.0,Mx=0.0,My=0.0;

 FILE *file=NULL;

 if(DEBUG) file=fopen("ee","w"); /* for debugging data / energy tracking. */

 progname = argv[0];

 ONEPI=M_PI;
 TWOPI=2.0*ONEPI;
 xmin=ymin=0.0; 
 sprintf(ModelName,"%s: J = (%5.2f,%5.2f)",progname,J[0],J[1]);

 /* default starting values for system size, random seed, inverse T. */
 nx=15; ny=9; iseed=-111; beta=2.0, dS=0.5; sigtau=sqrt(2.0*gam*Irot/beta);

 sprintf(window_name,"%s - Plane rotor Monte Carlo and dynamics",progname);
 sprintf(icon_name,"%s",progname);
 openwindow(argc,argv,window_name,icon_name);
  
 /* XSynchronize(display,1); */

 Nsites=nx*ny; Ns1=Nsites+1; 

 /* Get memory for system's site, spin, and neighbor arrays. */
 x =(float *) malloc( (size_t) Ns1*sizeof(float) );
 y =(float *) malloc( (size_t) Ns1*sizeof(float) );
 sx=(double *) malloc( (size_t) Ns1*sizeof(double) );
 sy=(double *) malloc( (size_t) Ns1*sizeof(double) );
 sz=(double *) malloc( (size_t) Ns1*sizeof(double) );
 phi=(double *) malloc( (size_t) Ns1*sizeof(double) );
 bx=(double *) malloc( (size_t) Ns1*sizeof(double) );
 by=(double *) malloc( (size_t) Ns1*sizeof(double) );
 vort=(int *) malloc( (size_t) Ns1*sizeof(int) );
 phase=(int *) malloc( (size_t) Ns1*sizeof(int) );
 nbrs=twodarray(Ns1,4);
 virgin=(int *)malloc( (size_t) Ns1*sizeof(int) );
 ClSites=(int *)malloc( (size_t) Ns1*sizeof(int) );
 for(i=0; i<Ns1; i++) virgin[i]=1;
 oldx=(double *) malloc( (size_t) Ns1*sizeof(double) );
 oldy=(double *) malloc( (size_t) Ns1*sizeof(double) );
 tmpx=(double *) malloc( (size_t) Ns1*sizeof(double) );
 tmpy=(double *) malloc( (size_t) Ns1*sizeof(double) );

/* set the initial values of control flags. */
 newsys=newscale=pause=algorithm=text=showM=1;
 re_init=newT=newH=newB=newD=redraw=graphs=nextstep=togvort=dynamics=0;

 onechar=help(win,gcyel,large_font,&win_width,&win_height,ModelName); 
 if(DEBUG) printf("onechar=%c,  pause=%d,  pending=%d\n",onechar,pause,XPending(display));

 /* get events, use flags newscale, newsys 
       		to tell when system needs updating. */
 while(1)
 {
     if(pause|XPending(display))
     {
        XNextEvent(display, &report);
 	switch  (report.type) 
        {
 	case ConfigureNotify:
	/* window has been resized, change width and height to  *
         * send to help, Metrop and draw_graphics in next Expose	*/
        /*      printf("ConfigureNotify event\n");		*/
		new_width = report.xconfigure.width;
		new_height = report.xconfigure.height;
		if(new_width!=win_width || new_height!=win_height)
		{ win_width=new_width; win_height=new_height;
		  redraw=1; newscale=1; XClearWindow(display,win); 
		  if ((win_width <= size_hints->min_width) ||
			(win_height <= size_hints->min_height))
				window_size = TOO_SMALL;
			else	window_size = BIG_ENOUGH; }
		break;
	case Expose:
	/* unless this is the last contiguous expose, don't draw the window */
        /*      printf("Expose event: count = %d\n", report.xexpose.count); */
		if (report.xexpose.count != 0) break;  
                redraw=1;
                break;

	case ButtonPress:
                buttonnum=report.xbutton.button;
        /*      printf("Button Press event: ");	 */
        /*      printf("  button = %d\n",buttonnum); */
                if     (buttonnum==Button1) { ; }
                else if(buttonnum==Button2) { ; }
		else if(buttonnum==Button3) { ; }
                break;

        case KeyRelease:
        /*      printf("KeyRelease Event:  ");  */
                keycode=report.xkey.keycode;
                if(keycode==64) shiftkey=0;
                /* printf(" keycode = %d,  shiftkey=%d\n",keycode,shiftkey); */
        break;

	case KeyPress:
		keycode=report.xkey.keycode;
                onechar=(char)XKeycodeToKeysym(display,report.xkey.keycode,0);
                if(DEBUG) 
		   {  printf(" KeyPress:  keycode = %d, ", report.xkey.keycode);
                      printf(" KeySym = %c\n", onechar ); }
                if(onechar=='h' )				/* get help. */   
		{ 
		  onechar=help(win,gcyel,large_font,&win_width,&win_height,ModelName); 
		  redraw=newscale=1;
		}

                if(onechar=='q')
		{
		  XUnloadFont(display, small_font->fid);			/* quit. */
		  XUnloadFont(display, large_font->fid);			/* quit. */
		  XFreeGC(display, gcpen);
		  XCloseDisplay(display);
		  if(DEBUG) fclose(file);
		  exit(0);
		}
		else if(keycode==64) { shiftkey=1; }			/* shift was pushed. */
		else if(onechar==' ') { pause=1; nextstep=1; }		/* single step mode. */
		else if(onechar=='\r'||onechar=='p'){ pause=1-pause; }	/* pause display. */
		else if(onechar=='w')	{ beta *=0.98; newT=text=1; }	/* warmer */
		else if(onechar=='c')	{ beta /=0.98; newT=text=1; }	/* colder */
		else if(onechar=='s' && !shiftkey) { dS *=1.05; graphs=1; } /* larger dS. */
		else if(onechar=='s' &&  shiftkey) { dS *=0.95; graphs=1; } /* smaller dS. */
		else if(onechar=='r' && !shiftkey) { re_init=1;}	/* random re-initialize. */   
		else if(onechar=='r' &&  shiftkey) { re_init=2;}	/* aligned re-initialize. */   
		else if(onechar=='b') 			 		/* change Bx. */
		{ map_coords(xcntr,ymin,&x1,&y1); y1 -= 30.;
		  map_coords(xcntr+10.0*Bx,ymin,&x2,&y2);  newB=1;
		  if(Bx>=0.0) draw_arrow(win,gcclear,x1+25.,y1,x2+25.,y1,0.1,1);
	   	     else     draw_arrow(win,gcclear,x1-10.,y1,x2-10.,y1,0.1,1);
		  if(shiftkey) { Bx -=0.01; } else { Bx +=0.01; } }     /* less/more Bx. */

		else if(onechar=='k' && !shiftkey) { Ka +=0.01; text=1; } /* more Ka at boundary. */
		else if(onechar=='k' &&  shiftkey) { Ka -=0.01; text=1; } /* less Ka at boundary. */
		else if(onechar=='d' && !shiftkey) { D += 0.01; newD=1; } /* stronger dipoles. */
		else if(onechar=='d' &&  shiftkey && D>0.005) { D -=0.01; newD=1; } /* weaker dipoles. */
		else if(onechar==',') 		     { lsleep *= 1.1; } /* slowdown frames. */
		else if(onechar=='.' && lsleep>10L ) { lsleep *= 0.9; } /* speedup frames. */
		else if(onechar=='m')   { showM=1-showM; redraw=1;	/* toggle view of magnetization. */
					  /* and erase the last magnetization arrow. */
					  spinarrow(win,gcclear,xcntr,ycntr,Mx,My,0.0); }
/* lf arrow */  else if(keycode==131)   { if(nx>1)    {nx--; newsys=1;}} /* smaller nx. */
/* rt arrow */  else if(keycode==132)   { if(nx<1000) {nx++; newsys=1;}} /* bigger nx. */
/* dn arrow */  else if(keycode==133)   { if(ny>1)    {ny--; newsys=1;}} /* smaller ny. */
/* up arrow */  else if(keycode==134)   { if(ny<1000) {ny++; newsys=1;}} /* bigger ny. */
		else if(onechar=='j')	{ kex = -kex;  newH=1; }	/* toggle FM/AFM coupling. */
		else if(onechar==']')   { bc=((bc+1)%4); newH=1; }	/* toggle boundary condition. */
		else if(onechar=='[')   { bc=((bc+3)%4); newH=1; }	/* toggle boundary condition. */
/*		else if(onechar=='a')	{ algorithm=(algorithm%3)+1; text=1; }	*/
									/* change algorithm. */
		else if(onechar=='v')	{ togvort=1; }			/* toggle vortex drawing. */
		else if(onechar=='x' && View!=1) {View=1; redraw=1;	/* sx is out of page now. */
				XFillRectangle(display,win,gcclear,
				(int)X0+1,Ytop+1,win_width-(2*BORDER+2),(int)Y0-(Ytop+1));}
		else if(onechar=='z' && View!=3) {View=3; redraw=1;	/* sz is out of page now. */
				XFillRectangle(display,win,gcclear,
				(int)X0+1,Ytop+1,win_width-(2*BORDER+2),(int)Y0-(Ytop+1));}
		else if(onechar=='f') { field=(field+1)%3; redraw=1;}	  /* toggle field view. */
		else if(onechar=='t') { dynamics=1-dynamics; newT=text=1;}  /* turn on/off dynamics. */
		else if(onechar=='g' && dynamics) 
		{  if(shiftkey && gam>0.005) { gam -= 0.01; if(gam<0.0) gam=0.0; } 
		   else if(!shiftkey) { gam += 0.01; } 
		   sigtau=sqrt(2.0*gam*Irot/beta);  text=1; }		/* change Langevin damping. */

		/* trickle down into default (no break) */
	default:
       /* all events selected by StructureNotifyMask
 	* except ConfigureNotify are thrown away here,
 	* since nothing is done with them */
	break;
 	} /* end switch */

     }

/* Following if statements test what to do based on the current flags. */

     if(newsys)
     {
	Nsites=nx*ny; rN=1.0/(double)Nsites; Ns1=Nsites+1;
 	xmax=(float)(nx+1);
 	ymax=(float)(ny+1);

	/* adjust to get equal scaling of diagram in x and y directions.    */
	/* this is correct if the drawing area's width is twice its height. */
	if(nx>=ny) 
        { xmin=-(float)(3*nx)/4.+1.0;    xmax=-xmin+(float)(nx+1);     longx=1;
          ymin=(float)(4+4*ny-5*nx)/8.;  ymax=(float)(4+4*ny+5*nx)/8.; Mmag=rN*(double)nx; }
	else     
        { ymin=-(float)(ny)/8.+1.0;      ymax=-ymin+(float)(ny+1);     longx=0;
          xmin=(float)(2+2*nx-5*ny)/4.;  xmax=(float)(2+2*nx+5*ny)/4.; Mmag=rN*(double)ny; }

	/* Free up previous memory used by spin arrays. */
   	free(x); free(y); 
	free(phase); free(nbrs); free(hx);  free(hy);
	free(sx); free(sy); free(sz); free(ClSites); free(virgin);	
	free(oldx); free(oldy);
	free(tmpx); free(tmpy);

	/* Get memory for new system's site, spin, and neighbor arrays. */
	x =(float *) malloc( (size_t) Ns1*sizeof(float) );
	y =(float *) malloc( (size_t) Ns1*sizeof(float) );
	sx=(double *) malloc( (size_t) Ns1*sizeof(double) );
	sy=(double *) malloc( (size_t) Ns1*sizeof(double) );
	sz=(double *) malloc( (size_t) Ns1*sizeof(double) );
 	phi=(double *) malloc( (size_t) Ns1*sizeof(double) );
 	bx=(double *) malloc( (size_t) Ns1*sizeof(double) );
 	by=(double *) malloc( (size_t) Ns1*sizeof(double) );
 	vort=(int *) malloc( (size_t) Ns1*sizeof(int) );
	phase=(int *) malloc( (size_t) Ns1*sizeof(int) );
	nbrs=twodarray(Ns1,4);
	virgin=(int *)malloc( (size_t) Ns1*sizeof(int) );
	ClSites=(int *)malloc( (size_t) Ns1*sizeof(int) );
	/* spin dynamics arrays used by RK4Step(). */
 	oldx=(double *) malloc( (size_t) Ns1*sizeof(double) );
 	oldy=(double *) malloc( (size_t) Ns1*sizeof(double) );
 	tmpx=(double *) malloc( (size_t) Ns1*sizeof(double) );
 	tmpy=(double *) malloc( (size_t) Ns1*sizeof(double) );

	/* arrays for field plotting. */
	n1=1+(xmax-xmin)/dx_field; n2=1+(ymax-ymin)/dy_field;
 	hx=(double *) malloc( (size_t) (n1*n2)*sizeof(double) );
 	hy=(double *) malloc( (size_t) (n1*n2)*sizeof(double) );

 	/* set the x,y coordinates and near neighbor table.  */
 	Sites(bc,nx,ny,x,y,nbrs); 
	Phases(nx,ny,kex,phase);
	cntr=isite(nx/2,ny/2,nx,ny);
	xcntr=x[cntr]; ycntr=y[cntr];  /* center of magnet. */
	if(DEBUG) printf("newsys:  xcntr = %f,  ycntr = %f\n",xcntr,ycntr);
	for(i=0; i<Ns1; i++) virgin[i]=1;
        newscale=re_init=1;
        newsys=0;
     } 
     
     if(newscale)
     {
         /* Fix scaling of spin coordinates when 
            window re-sized or when system size is changed. */

	 X0=(float)(BORDER);
	 XScale=(float)(win_width-BORDER-X0)/(xmax-xmin);
	 Y0=(float)(win_height-BORDER);
	 Ytop=BAR_HEIGHT+2*BORDER+small_ht+4;
	 YScale=(Y0-Ytop)/(ymax-ymin);

         /* Quantities for bar_graph() positioning. */
         bar_shift=win_width/7;
         bar_X0=(2*bar_shift)/3;
         bar_Y0=0;
         bar_width=BAR_WIDTH;
	 bar_height=BAR_HEIGHT;
	 for(i=0; i<8; i++) bar_X[i]=bar_X0+i*bar_shift;

	 /* XClearWindow(display,win); */
	 newscale=0;
         redraw=1;
     }

     if(re_init)
     {
 	if(re_init==1) init(Nsites,--iseed,sx,sy,sz);       /* random initial condition. */
 	else if(re_init==2) align(Nsites,--iseed,sx,sy,sz); /* aligned initial condition. */
	get_demag_fields(Nsites,sx,sy,x,y,D,bx,by);
	e=energy(Nsites,sx,sy,sz,nbrs,J,Bx,By,bx,by,&mx,&my,&mz);
	e0=eground(nx,ny,Nsites,nbrs,x,y,J,D,Bx,By);
	e -= e0; time=0.0;
        
        if(DEBUG) fclose(file);
        if(DEBUG) file=fopen("ee","w"); /* for debugging data / energy tracking. */

	XFillRectangle(display,win,gcclear,
			(int)X0+1,Ytop+1,win_width-(2*BORDER+2),(int)Y0-(Ytop+1));
        newT=redraw=1;
        if(DEBUG) printf("re_init:  e-e0 = %e, mx = %f, my = %f, mz = %f\n",e,mx,my,mz);

        re_init=0;
     }
     
     if(newH)
     {
	J[0]=J[1]=kex; J[2]=0.0;
	sprintf(ModelName,"%s: J = (%5.2f,%5.2f)","xrotor",J[0],J[1]);
 	Sites(bc,nx,ny,x,y,nbrs);
	Phases(nx,ny,kex,phase);
	cntr=isite(nx/2,ny/2,nx,ny);
	xcntr=x[cntr]; ycntr=y[cntr];  /* center of magnet. */
	get_demag_fields(Nsites,sx,sy,x,y,D,bx,by);
	e=energy(Nsites,sx,sy,sz,nbrs,J,Bx,By,bx,by,&mx,&my,&mz);
	e0=eground(nx,ny,Nsites,nbrs,x,y,J,D,Bx,By);
	e -= e0; 
	nv=vcount(Nsites,x,y,nbrs,phase,sx,sy,sz,phi,vort);
	newT=text=1;
	newH=0;
     }

     if(newT)
     {
	if(!dynamics) for(i=0; i<Nsites; i++) sz[i]=0.0;
	sigtau=sqrt(2.0*gam*Irot/beta);
	e=energy(Nsites,sx,sy,sz,nbrs,J,Bx,By,bx,by,&mx,&my,&mz);
	/* printf("newT: e0=%f,  e=%f,  e-e0=%f\n",e0,e,e-e0);  */
	e -= e0; /* call energy in case it drifted at previous T. */
	E=rN*e;
        accrate=0.0; nsamp=0; C=X_M=time=0.0; 
	if(DEBUG) fprintf(file,"\n");
        graphs=1;
        newT=0;
     }

     if(newB)
     {
	e=energy(Nsites,sx,sy,sz,nbrs,J,Bx,By,bx,by,&mx,&my,&mz);
	e0=eground(nx,ny,Nsites,nbrs,x,y,J,D,Bx,By);
	e -= e0; E=rN*e; 
	/* draw an arrow representing Bx. */
	map_coords(xcntr,ymin,&x1,&y1); map_coords(xcntr+10.0*Bx,ymin,&x2,&y2); y1 -= 30.;
	if(Bx>=0.0) draw_arrow(win,gcyel,x1+25.,y1,x2+25.,y1,0.1,1);
	   else     draw_arrow(win,gcyel,x1-10.,y1,x2-10.,y1,0.1,1);
	/* spinarrow(win,gcyel,xcntr,ymin+2.0,10.*Bx,10.*By,-0.01); */
	map_coords(xcntr,ymin,&x1,&y1);
        XDrawString(display,win,gcyel,(int)x1,(int)y1-25,"Bx",2);

        if(DEBUG) printf(" D = %f,  Ka = %f,  Bx = %f\n",D,Ka,Bx); 
	graphs=text=1;
	newB=0;
     }

     if(newD)
     {
	get_demag_fields(Nsites,sx,sy,x,y,D,bx,by);
	e=energy(Nsites,sx,sy,sz,nbrs,J,Bx,By,bx,by,&mx,&my,&mz);
	e0=eground(nx,ny,Nsites,nbrs,x,y,J,D,Bx,By);
	e -= e0; E=rN*e; 
        if(DEBUG) printf(" D = %f,  Ka = %f,  Bx = %f\n",D,Ka,Bx); 
	graphs=text=1;
	newD=0;
     }

     if(text)
     {
        draw_text(win, gcpen, small_font, nx, ny, kex, beta, D, Ka, Bx, bc, algorithm, dynamics);
	draw_nums(win, gcpen, small_font, nsamp, dynamics, time);
	text=0;
     }
	
  
     if(togvort)
     {
	DrawVorts=1-DrawVorts;
	if(!DrawVorts)
	XFillRectangle(display,win,gcclear,
			(int)X0+1,Ytop+1,win_width-(2*BORDER+2),(int)Y0-(Ytop+1));
	togvort=0;
	redraw=1;
     }

     if(redraw)
     {
	/* XClearWindow(display,win); */

	/* draw the spin arrows. */
        for(i=0; i<Nsites; i++)
        { /* white line arrow heads for sz>0, blue open for sz<0. */
	  if(sz[i]>=0.0) spinarrow(win,gcbw,  x[i],y[i],sx[i],sy[i],sz[i]);
	      else       spinarrow(win,gcdeep,x[i],y[i],sx[i],sy[i],sz[i]);
        } /* end arrows */
        
	nv=vcount(Nsites,x,y,nbrs,phase,sx,sy,sz,phi,vort);
	M=sqrt(mx*mx+my*my+mz*mz)/((double)Nsites);
        rho=(double)nv/(double)Nsites;
 	Tmax=dSmax=accmax=Mmax=rhomax=Emax=Cmax=X_Mmax=0.0;
	/* place box around system. */
	draw_frame(win, gcpen, win_width, win_height); 
	draw_text(win, gcpen, small_font, nx, ny, kex, beta, D, Ka, Bx, bc, algorithm, dynamics);

	/* draw an arrow representing Bx. */
	map_coords(xcntr,ymin,&x1,&y1); map_coords(xcntr+10.0*Bx,ymin,&x2,&y2); y1 -= 30;
	if(Bx>=0.0) draw_arrow(win,gcyel,x1+25,y1,x2+25,y1,0.1,1);
	   else     draw_arrow(win,gcyel,x1-10,y1,x2-10,y1,0.1,1);
	/* spinarrow(win,gcyel,xcntr,ymin+2.0,10.*Bx,10.*By,-0.01); */
	map_coords(xcntr,ymin,&x1,&y1);
        XDrawString(display,win,gcyel,x1,y1-25,"Bx",2);

	if(showM) 
	{  spinarrow(win,gcclear,xcntr,ycntr,Mx,My,0.0);
	   Mx=Mmag*mx;  My=Mmag*my;
	   spinarrow(win,gcred,xcntr,ycntr,Mx,My,0.0); }
	ShowMagField(Nsites,nx,ny,Dext,x,y,sx,sy,hx,hy);
	graphs=1;
        redraw=0;
     } /* end redraw */

     /* Update the spins by Runge-Kutta-4 or by Monte Carlo. */
     if(pause-1 || nextstep)
     {
        usleep(lsleep);  /* sleep for lsleep us before proceeding. */

	if(D>0.005) 
	  get_demag_fields(Nsites,sx,sy,x,y,D,bx,by);

	if(dynamics)  /* spin dynamics moves */
	{
	  LangevinStep(Nsites,x,y,nbrs,sx,sy,sz,
	          J,Bx,By,bx,by,Ka,bc,kdt,dt,&time,&mx,&my,&mz);
	  if(DrawVorts)  nv=vcount(Nsites,x,y,nbrs,phase,sx,sy,sz,phi,vort);
        }

        else    /* Monte Carlo moves */
        {
	  if(algorithm&1)
	  { 
	    kyes=Metrop(Nsites,x,y,nbrs,phase,sx,sy,sz,phi,vort,
		        J,Bx,By,bx,by,Ka,bc,beta,dS,&e,&mx,&my,&mz,&nv); 
	    accrate=(float)kyes/((float)Nsites); time += (kdt*dt);
	  }

	  if(algorithm&2)  /* deactivated.  not correct at this point. */
	  {
             Wolff(Nsites,x,y,nbrs,sx,sy,sz,ClSites,virgin,J[0],beta,&e,&mx,&my);
	     if(DrawVorts) nv=vcount(Nsites,x,y,nbrs,phase,sx,sy,sz,phi,vort); 
	  }
	}

	if(dynamics || D>0.005)  /* prevent drift of the energy when demag is included. */
	  { e=energy(Nsites,sx,sy,sz,nbrs,J,Bx,By,bx,by,&mx,&my,&mz); e -= e0; }

        if(DEBUG) printf("step: e0 = %e,  e-e0 = %e\n",rN*e0,rN*e); 
	if(DEBUG) fprintf(file,"%f   %14.7f\n",time,rN*e);

	m=sqrt(mx*mx+my*my);
        rho=rN*(double)nv;

	if(nsamp%10==0) 
	{  ShowMagField(Nsites,nx,ny,Dext,x,y,sx,sy,hx,hy);    }

	if(showM) 
	{  /* erase last magnetization arrow. */
	   spinarrow(win,gcclear,xcntr,ycntr,Mx,My,0.0);
	   Mx=Mmag*mx;  My=Mmag*my;
	   /* printf("step:  Mx=%f, My=%f, M=%f\n",rN*mx,rN*my,rN*m); */
	   /* draw current magnetization arrow. */
	   spinarrow(win,gcred,xcntr,ycntr,Mx,My,0.0); }

	++nsamp; 
	if(nsamp>nskip) averages(nsamp-nskip,Nsites,beta,e,m,&C,&X_M);
	E=rN*e;
	M=rN*m;
	graphs=1;
        nextstep=0;
     }

     if(graphs)
     {
	XSetForeground(display,gcpen,lightcolor);
	draw_nums(win, gcpen, small_font, nsamp, dynamics, time);
	i=0;
	bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"T",0.0,&Tmax,1.0/beta);
	bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"dS",0.0,&dSmax,dS);
	bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"accept",0.0,&accmax,accrate);
	bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"M",0.0,&Mmax,M);
	bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"X",0.0,&X_Mmax,X_M);
	bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"E",0.0,&Emax,E);
	bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"C",0.0,&Cmax,C);
	/* bar_graph(win,bar_X[i++],bar_Y0,bar_width,bar_height,"rho",0.0,&rhomax,rho); */
        graphs=0;
     }

  } /* end while */
/*
  XUnloadFont(display, small_font->fid);
  XUnloadFont(display, large_font->fid);
  XFreeGC(display, gcpen);
  XCloseDisplay(display);
*/
} /* end main */

/*************************************************************/
void openwindow(argc,argv,window_name,icon_name)
/* a lot of this is taken from the basicwin program in the
Xlib Programming Manual */
int argc;
char **argv;
char *window_name;
char *icon_name;
{
 int x, y;
 unsigned int border_width = 4;
 unsigned int display_width, display_height;
 Pixmap icon_pixmap;
 char *display_name = NULL;
 XColor xcolor,colorcell;
 Colormap cmap;


 if (!(size_hints = XAllocSizeHints())) 
 { fprintf(stderr, "%s: failure allocating memory\n", progname); exit(0); }

 if (!(wm_hints = XAllocWMHints())) 
 { fprintf(stderr, "%s: failure allocating memory\n", progname); exit(0); }

 if (!(class_hints = XAllocClassHint())) 
 { fprintf(stderr, "%s: failure allocating memory\n", progname); exit(0); }


 /* connect to X server */
 if ( (display=XOpenDisplay(display_name)) == NULL )
 { (void) fprintf( stderr, "%s: cannot connect to X server %s\n", 
 	progname, XDisplayName(display_name)); exit( -1 ); }

 /* get screen size from display structure macro */
 screen_num = DefaultScreen(display);
 display_width = DisplayWidth(display, screen_num);
 display_height = DisplayHeight(display, screen_num);
 depth=DefaultDepth(display,screen_num);
 cmap=DefaultColormap(display,screen_num);
 /* depth=1; */  /* uncomment to test monochrome display */
 darkcolor=black=BlackPixel(display,screen_num);
 lightcolor=white=WhitePixel(display,screen_num);
 if (depth>1) /* color? This is not the right way to do it, but .... */
 { if (XAllocNamedColor(display,cmap,"SkyBlue",&colorcell,&xcolor))
   {   lightcolor=colorcell.pixel; }
   if (XAllocNamedColor(display,cmap,"gray8",&colorcell,&xcolor))
   {   darkcolor=colorcell.pixel; } 
   if (XAllocNamedColor(display,cmap,"red",&colorcell,&xcolor))
   {   brightcolor=colorcell.pixel; }
   if (XAllocNamedColor(display,cmap,"yellow",&colorcell,&xcolor))
   {   yellowcolor=colorcell.pixel; }
   if (XAllocNamedColor(display,cmap,"DeepSkyBlue",&colorcell,&xcolor))
   {   deepcolor=colorcell.pixel; }
 }

 load_font("6x10",&small_font);
 load_font("9x15",&large_font);
 large_ht=large_font->ascent+large_font->descent;
 small_ht=small_font->ascent+small_font->descent;

 /* Note that in a real application, x and y would default to 0
  * but would be settable from the command line or resource database.  */
 x = y = 0;

/* size window with enough room for text */
/*
win_width = display_width<800 ? 400: display_width/2;
win_height = win_width+BAR_HEIGHT+small_ht+4;
*/
win_width= display_width;
win_height= display_width/2+BAR_HEIGHT+small_ht+4;

/* create opaque window */
 win = XCreateSimpleWindow(display, RootWindow(display,screen_num), 
	x, y, win_width, win_height, border_width, lightcolor, darkcolor);

/* Create pixmap of depth 1 (bitmap) for icon */
 icon_pixmap = XCreateBitmapFromData(display, win, icon_bits, 
		icon_width, icon_height);

/* Set size hints for window manager.  The window manager may
 * override these settings.  Note that in a real
 * application if size or position were set by the user
 * the flags would be UPosition and USize, and these would
 * override the window manager's preferences for this window. */

/* x, y, width, and height hints are now taken from
 * the actual settings of the window when mapped. Note
 * that PPosition and PSize must be specified anyway. */

 size_hints->flags = PPosition | PSize | PMinSize;
 size_hints->min_width = 400;
 size_hints->min_height = size_hints->min_width+BORDER+BAR_HEIGHT+small_ht+4;

 /* These calls store window_name and icon_name into
  * XTextProperty structures and set their other 
  * fields properly. */
 if (XStringListToTextProperty(&window_name, 1, &windowName) == 0) 
	{ (void) fprintf( stderr, "%s: structure allocation for windowName failed.\n", 
 			progname); exit(-1); }
		
 if (XStringListToTextProperty(&icon_name, 1, &iconName) == 0) {
 	(void) fprintf( stderr, "%s: structure allocation for iconName failed.\n", 
 			progname); exit(-1); }

 wm_hints->initial_state = NormalState;
 wm_hints->input = True;
 wm_hints->icon_pixmap = icon_pixmap;
 wm_hints->flags = StateHint | IconPixmapHint | InputHint;

 class_hints->res_name = progname;
 class_hints->res_class = "Basicwin";

 XSetWMProperties(display,win,&windowName,&iconName, 
 		argv,argc,size_hints,wm_hints,class_hints);

 /* Select event types wanted */
 event_mask=ExposureMask|KeyPressMask|KeyReleaseMask|ButtonPressMask|StructureNotifyMask;
 XSelectInput(display,win,event_mask); 

/* make graphics contexts: 
   gcbw for white on black
   gcpen for various, defaults to darkcolor on lightcolor
   gcclear does the reverse. */ 

 getGC(win, &gcbw,    large_font, white,       black);
 getGC(win, &gcpen,   small_font, lightcolor,  darkcolor);
 getGC(win, &gcdeep,  large_font, deepcolor,   darkcolor);
 getGC(win, &gcyel,   large_font, yellowcolor, darkcolor);
 getGC(win, &gcbrt,   large_font, brightcolor,  darkcolor);
 getGC(win, &gcclear, small_font, darkcolor,   lightcolor);
 getGC(win, &gcred,   large_font, brightcolor, darkcolor);

 /* Display window */
 XMapWindow(display, win);
 return;
}


/*************************************************************/
void getGC(win, gc, font_info, fg_color, bg_color)
Window win;
GC *gc;
XFontStruct *font_info;
int fg_color, bg_color;
{
	unsigned long valuemask = 0; /* ignore XGCvalues and use defaults */
	XGCValues values;

/*
	unsigned int line_width;
	int line_style = LineSolid;
	int cap_style = CapRound;
	int join_style = JoinRound;

	int dash_offset = 0;
	static char dash_list[] = {12, 24};
	int list_length = 2;
*/

	/* Create default Graphics Context */
	*gc = XCreateGC(display, win, valuemask, &values);

	/* specify font */
	XSetFont(display, *gc, font_info->fid);

	/* specify foreground fg_color */
	XSetForeground(display, *gc, fg_color);

	/* specify background bg_color */
	XSetBackground(display, *gc, bg_color);

	/* set line attributes */
/*
	XSetLineAttributes(display, *gc, line_width, line_style, cap_style, join_style);
*/
	/* set dashes */
/*
	XSetDashes(display, *gc, dash_offset, dash_list, list_length);
*/
        return;
}

/*************************************************************/
void load_font(fontname,font_info)
char *fontname;
XFontStruct **font_info;
{
	/* Load font and get font information structure. */
	if ((*font_info = XLoadQueryFont(display,fontname)) == NULL)
	{
		(void) fprintf( stderr, "%s: Cannot open %s font\n", 
				progname, fontname);
		exit(-1);
	}
}
/*************************************************************/
char help(win, gc, font_info, win_width, win_height, ModelName)
Window win;
GC gc;
XFontStruct *font_info;
unsigned int *win_width, *win_height;
char *ModelName;
#define NLINES 33
{
	char *string[NLINES] = {
	"Classical simulation of XY rotor model:",
	" ",
	"    H_{n,m} =  J * S_n . S_m  ",
	" ",
	" Use arrow keys to change system size.",
	" ",
	" Touch other keys for the following actions:",
	" ",
	"    h     This message",
	"    q     quit",
	" [ or ]   change boundary conditions:",
	"          periodic/free/anisotropic/long edge",
	" r or R   random/aligned restart",
	"    w     warmer",
	"    c     colder",
	" s or S   increase/decrease spin changes dS",
	" d or D   increase/decrease dipole-dipole",
	" b or B   increase/decrease applied Bx",
	"    m     toggle net magnetization arrow",
	"    f     toggle magnetic field view",
	"    p     pause/resume action",
	" return   pause/resume action",
	"  space   single steps", 
	"    ,     slow down updates",
	"    .     speed up updates",
	"    t     turn on/off Langevin time dynamics",
	" g or G   increase/decrease Langevin damping",
	"    j     FM/AFM model",
	"    v     show vortices",
	"    x     view along Sx axis",
	"    z     view along Sz axis",
	" ",
	"wysin@phys.ksu.edu [Gary Wysin]."
	};

	int go,redraw,i,len;
	int x0,y0,width,ht;
	XEvent report;
	char onechar='h';

	go=redraw=1;
	/* need length for both XTextWidth and XDrawString */
	len=strlen(string[0]);
	/* get string width for centering first line */
	width=XTextWidth(font_info,string[0],len);
	ht=(font_info->ascent + font_info->descent);

	/* wait for any key or button event to continue. */
	do
	{
	  if(redraw)
	  {
	    XClearWindow(display,win);

            x0=(*win_width-width)>>1;
            y0=(*win_height-NLINES*ht)>>1;

	    /* output texts */
/*
	    len=strlen(ModelName);
	    XDrawString(display, win, gc, x0, y0, ModelName,len);
            y0 += ht+ht;
*/
	    for(i=0; i<NLINES; i++)
	    {  
	      len=strlen(string[i]);
	      XDrawString(display, win, gc, x0, y0, string[i],len);
	      y0 += ht;
	    }
	    redraw=0;
	  }

	  XNextEvent(display, &report);
          switch  (report.type)
	  {
	    case ConfigureNotify:
                *win_width = report.xconfigure.width;
                *win_height = report.xconfigure.height;
                redraw=1; break;
            case Expose:
                if (report.xexpose.count != 0) break;
                redraw=1; break;
	    case KeyPress:    /* send character back to main program.  */
		onechar=(char)XKeycodeToKeysym(display,report.xkey.keycode,0);
		go=0; break;
	    case ButtonPress:
		onechar='0';  /* character that presently does nothing. */
		go=0; break;
	    default: break;
	  }
/*
	 }
*/
	}  while(go);
	XClearWindow(display,win);
	return(onechar);
}

/*************************************************************************/
void draw_frame(win, gc, window_width, window_height)
Window win;
GC gc;
unsigned int window_width,window_height;
{
	int x,y;
	int width,height;

        x=(int)X0;
        y=Ytop;
        width=window_width-2*BORDER;
        height=Y0-y;
	XDrawRectangle(display,win,gc,x,y,width,height);

	x += BORDER;
	y  = Y0-BORDER;
	draw_arrow(win,gcyel,(float)x,(float)y,(float)(x+width/16),(float)y,1.0,1);
	draw_arrow(win,gcyel,(float)x,(float)y,(float)x,(float)(y-width/16),1.0,1);
	if(View==1)
	{
	   XDrawString(display,win,gcyel,x+width/16+4,y+4,"Y",1);
	   XDrawString(display,win,gcyel,x-3,y-width/16-4,"Z",1);
	}
	else
	{
	   XDrawString(display,win,gcyel,x+width/16+4,y+4,"X",1);
	   XDrawString(display,win,gcyel,x-3,y-width/16-4,"Y",1);
	}
}

/*********************************************************************************/
void draw_text(win, gc, font_info, nx, ny, kex, beta, D, Ka, Bx, bc, algorithm, dynamics)
Window win;
GC gc;
XFontStruct *font_info;
int nx,ny,kex,bc,algorithm,dynamics;
double beta,D,Ka,Bx;
{
	char buff[128];
	char *model[2]={" FM","AFM"};
	char *algor[3]={" ","Metropolis Monte Carlo","Wolff Cluster Monte Carlo"};
	char *bcs[4]={"periodic bc    ",
		      "free edge bc   ",
		      "anisotropic bc ",
		      "long edge bc   "};
	int width,height,len,ht,x,y;

	ht=font_info->ascent+font_info->descent;
        x=(int)X0;
        y=Ytop-(ht+3);
        width=win_width-2*BORDER;
        height=ht+4;

	XDrawRectangle(display,win,gc,x,y-1,width,height);
	
	sprintf(buff,"%3d x %3d %3s  T=%5.3f  D=%4.2f  Ka=%4.2f  Bx=%4.2f  %s %s + %s",
	        nx,ny,model[(kex+1)>>1],1./beta,D,Ka,Bx,bcs[bc],"Metrop. ","Wolff Cluster Monte Carlo                      ");
	len=strlen(buff);
	width=XTextWidth(font_info,buff,len);
	XFillRectangle(display,win,gcclear,x+2,y,width,ht+3); 

	if(dynamics)
	{ 
	  sprintf(buff,"%3d x %3d %3s  T=%5.3f  D=%4.2f  Ka=%4.2f  Bx=%4.2f  %s  gamma=%4.2f   %s",
                   nx,ny,model[(kex+1)>>1],1./beta,D,Ka,Bx,bcs[bc],gam,"Langevin Dynamics");
	}
	else 
        {
          if(algorithm<3)
	  {
	    sprintf(buff,"%3d x %3d %3s  T=%5.3f  D=%4.2f  Ka=%4.2f  Bx=%4.2f  %s %s",
	             nx,ny,model[(kex+1)>>1],1./beta,D,Ka,Bx,bcs[bc],algor[algorithm]);
	  }
        }

	len=strlen(buff);
	XDrawString(display,win,gc,x+2,y+ht,buff,len);

	return;
}

/*************************************************************************/
void draw_nums(win, gc, font_info, nsamp, dynamics, time)
Window win;
GC gc;
XFontStruct *font_info;
int nsamp,dynamics;
double time;
{
	char buff[128];
	int width,len,ht,x,y;

	if(dynamics) 
	{
	  sprintf(buff,"t=%6.2f",time); 
	}
	else
	{
          sprintf(buff,"n=%6d",nsamp); 
	}
	len=strlen(buff);
	width=XTextWidth(font_info,buff,len);
	ht=font_info->ascent+font_info->descent;
        x=win_width-(BORDER+width+2);
        y=Ytop-(ht+3);
	XFillRectangle(display,win,gcclear,x,y,width,ht+3); 
	XDrawString(display,win,gc,x,y+ht,buff,len);

	return;
}

/******************************************************/
double automax(fvalue,d1,d2)
double fvalue;
unsigned int *d1,*d2;
{
   double fmax,f;
   if(fvalue<0.0001) fvalue=0.00009;
   *d1=*d2=1;
   fmax=1.0;
   if((f=fvalue)<0.1)
     while(f<0.1) { f *= 10.0; fmax /= 10.0; (*d2)++; }
   else if((f=fvalue)>1.0)
     while(f>1.0) { f /= 10.0; fmax *= 10.0; (*d1)++; }
   return(fmax);
}


/************************************************************/
void bar_graph(win,x0,y0,xsize,ysize,name,fmin,fmax,fvalue)
Window win;
unsigned x0,y0,xsize,ysize; /* offsets, widths */
char *name;                 /* label of graph. */
double fmin,fvalue;         /* lower limit, value. */
double *fmax;		    /* upper limit, changed when rescaling necessary. */ 
{
#define VLEN 10
#define SPACE 3

        int itik,ytik,ntik=11;
	unsigned int d1,d2,dx,dy,tw=1;
	unsigned int sp,ht,halfh,strwidth,strmax,length;
	char vstring[VLEN],fmt[10];
	double f,newfmax,df;

        /* Shift origin to leave space for graph label. */
        /* large_ht=large_font->ascent+large_font->descent; */
        y0=y0+(3*large_ht)/2;
        ysize=ysize-(3*large_ht)/2;

	/* auto-scale the max value, and only redraw graph outline and tiks
	   if the maximum value has changed from the previous call. */

	newfmax=automax(fvalue,&d1,&d2); 
        if( newfmax != *fmax ) 
	{
	  *fmax=newfmax;
	  sprintf(fmt,"%c%d.%df",'%',(d1+1+d2),d2);
	  if(fvalue>100.0 || fvalue<0.001) sprintf(fmt,"%c%d.%de",'%',5,0);
	  strmax=XTextWidth(small_font,"8e-05",5);  

	  /* graph label. */
	  length=strlen(name);
	  strwidth=XTextWidth(large_font,name,length);
	  XSetFont(display,gcpen,large_font->fid);
	  XDrawString(display,win,gcpen,x0+((xsize-strwidth)>>1),y0-(large_ht>>1),name,length);

	  /* thermometer outline. */
	  XDrawRectangle(display,win,gcpen,x0,y0,xsize,ysize);

	  /* tick marks and numbers */
	  XSetFont(display,gcpen,small_font->fid);
	  ht=small_font->ascent+small_font->descent;
	  halfh=(ht>>1)-1; 

	  f=fmin; df=(*fmax-fmin)/(ntik-1);
	  for(itik=ntik-1; itik>=0; itik--)
	  { 
	     /* mult. needed for accuracy */
             ytik=y0+(itik*ysize)/(ntik-1);  
             sprintf(vstring,fmt,f); f += df;
             length=strlen(vstring);
             strwidth=XTextWidth(small_font,vstring,length);
	     if( itik%2==0 )
             {
	       XFillRectangle(display,win,gcclear,x0-SPACE-strmax,ytik-halfh,
                                                       SPACE+strmax,ht);
               XDrawString(display,win,gcpen,x0-SPACE-strwidth,ytik+halfh,vstring,length);
             }
	     XDrawLine(display,win,gcpen,x0,ytik,x0+tw,ytik);
             XDrawLine(display,win,gcpen,x0+xsize-tw,ytik,x0+xsize,ytik);
	  }

	}

	/* always redraw the thermometer fluid. */
	sp=tw+1;
	dx=xsize-(sp+sp+1); 
	XFillRectangle(display,win,gcclear,x0+sp+1,y0+1,dx,ysize-1);
	dy=(int)(((double)ysize)*(fvalue-fmin)/(*fmax-fmin)); 
        if(dy) XFillRectangle(display,win,gcyel,x0+sp+1,y0+1+ysize-dy,dx,dy-1);

	return;
}

/***************************************************************/
int **twodarray(n1,n2)
/* allocates memory for a two-d array[n1][n2]. */
int n1,n2;
{
  int l;
  int **ptr;
   
  ptr= (int **)malloc( (size_t) n1*sizeof(int*) );
  for(l=0; l<n1; l++)
  {
     ptr[l]=(int *)malloc( (size_t) n2*sizeof(int) );
  }
  return ptr;
}


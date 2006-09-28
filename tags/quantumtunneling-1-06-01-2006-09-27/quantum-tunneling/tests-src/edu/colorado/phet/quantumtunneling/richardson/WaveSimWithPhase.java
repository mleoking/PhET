/*
 * Extension of Richard's "WaveSim" that includes display of phase.  
 * Source: http://www.physics.brocku.ca/faculty/sternin/teaching/mirrors/qm/packet/wave-map.html
 */
package edu.colorado.phet.quantumtunneling.richardson;
import java.awt.*;

public class WaveSimWithPhase extends java.applet.Applet implements Runnable {
  int wx,wy,yoff,xpts[],ypts[],nx;
  double x0,xmin,xmax,dx,ymin,ymax,dy,xscale,yscale,tmin,t,dt;
  double hbar,mass,epsilon,width,vx,vwidth,energy,energyScale;
  complex Psi[],EtoV[],alpha,beta;
  Thread kicker = null;
  Button Restart  = new Button("Restart");
  Button Pause  = new Button("Pause");
  Button Stop = new Button("Stop");
  Choice C = new Choice();


  public void init() {

    setSize( 500, 300 );
    wx = size().width;
    wy = size().height;
    resize(wx,wy);
    setBackground(Color.white);
    nx = wx / 2;
    yoff = 50;
    wy -= yoff;
    xpts = new int[nx];
    ypts = new int[nx];
    Psi = new complex[nx];
    EtoV = new complex[nx];
    energyScale = 1;
    initPhysics();
    Panel p = new Panel();
    p.setLayout(new FlowLayout());
    p.add(Pause);
    p.add(Restart);
    p.add(Stop);
    C.addItem("Barrier V = 2*E");
    C.addItem("Barrier V = E");
    C.addItem("Barrier V = E/2");
    C.addItem("No Barrier");
    C.addItem("Well V = -E/2");
    C.addItem("Well V = -E");
    C.addItem("Well V = -2*E");
    C.select("Barrier V = E");
    p.add(C);
    add("North",p);
    Restart.disable();
    C.disable();
  }

  public void initPhysics() {
    x0 = -2;
    xmin = -3;
    xmax = 3;
    dx = (xmax-xmin)/(nx-1);
    xscale = (wx-0.5)/(xmax-xmin);
    ymin = -1.5;
    ymax = 1.5;
    dy = (ymax-ymin)/(wy-1);
    yscale = (wy-0.5)/(ymax-ymin);
    tmin = 0;
    t = tmin;
    hbar = 1;
    mass = 100;
    width = 0.50;
    vwidth = 0.25;
    vx = 0.25;
    dt = 0.8 * mass * dx * dx / hbar;
    epsilon = hbar * dt /( mass * dx * dx );
    alpha = new complex(0.5 * (1.0+Math.cos(epsilon/2))
			, -0.5 * Math.sin(epsilon/2));
    beta  = new complex((Math.sin(epsilon/4))*Math.sin(epsilon/4)
			,  0.5 * Math.sin(epsilon/2));
    energy = 0.5 * mass * vx * vx ;

    for(int x=0;x<nx;x++){
      double r,xval;
      xval = xmin + dx*x;
      xpts[x] = (int)(xscale*(xval - xmin));
      r = Math.exp(-((xval-x0)/width)*((xval-x0)/width));
      Psi[x] = new complex(r*Math.cos(mass*vx*xval/hbar),
			   r*Math.sin(mass*vx*xval/hbar) );
      r = v(xval)*dt/hbar;
      EtoV[x] = new complex( Math.cos(r), -Math.sin(r) );
    }
  }


  double v(double x) {
    return 
      (Math.abs(x) < vwidth) ? ( energy* energyScale ) : 0 ;
  }


  public void paint(Graphics g) {
    MakeGraph(g);
  }	

  Color VisZ( complex z) {
    double x,y, red,green,blue, a,b,d,r;
    x = z.re;
    y = z.im;
    r = Math.sqrt(x*x+y*y);
    a = 0.40824829046386301636 * x;
    b = 0.70710678118654752440 * y;
    d = 1.0/(1. + r*r);
    red = 0.5 + 0.81649658092772603273 * x * d;
    green = 0.5 - d * ( a - b );
    blue = 0.5 - d * ( a + b );
    d = 0.5 - r*d;
    if( r < 1 ) d = -d;
    red += d;
    green += d;
    blue += d;
    return ( new Color((float)red,(float)green,(float)blue) ) ;
  }
 
  public void MakeGraph(Graphics g) {
    int ix,iy;
    int jx,jy;
    int px[],py[];
    px = new int[4];
    py = new int[4];
    //g.setColor(Color.black);
    //g.drawRect(0,yoff,wx-1,wy-1);

    g.setColor(Color.red);
    ix = (int)(xscale*0.5*(xmax-xmin-2*vwidth));
    jx = (int)(xscale*0.5*(xmax-xmin+2*vwidth));
    iy = (int)(wy-1 - yscale*(0.5*ymax*energyScale - ymin));
    jy = (int)(wy-1 - yscale*(0  - ymin));

    g.drawLine(ix,yoff+iy,ix,yoff+jy);
    g.drawLine(jx,yoff+iy,jx,yoff+jy);
    g.drawLine(ix,yoff+iy,jx,yoff+iy);

    g.setColor(Color.blue);

    for(int x=0;x<nx;x++) 
      ypts[x] =  yoff + (int)( wy-1 -
	yscale* (Psi[x].re-ymin ) );
    for (int x = 0 ; x < nx-1 ; x++ )
      g.drawLine(xpts[x], ypts[x], xpts[x + 1], ypts[x+1]);
    
    g.setColor(Color.green);

    for(int x=0;x<nx;x++) 
      ypts[x] = yoff + (int)( wy-1 - 
	yscale* (Psi[x].im-ymin ) );
    for (int x = 0 ; x < nx-1 ; x++ )
      g.drawLine(xpts[x], ypts[x], xpts[x + 1], ypts[x+1]);
    
    g.setColor(new Color((float)0.5,(float)0.5,(float)0.5));

    for(int x=0;x<nx;x++) 
      ypts[x] =  yoff + (int)( wy-1 - 
	yscale* (Psi[x].re*Psi[x].re+Psi[x].im*Psi[x].im-ymin ) );
    py[3] = py[0] = yoff + (int)( wy-1 - yscale* (0-ymin ) );
    for (int x = 0 ; x < nx-1 ; x++ ){
      g.setColor( VisZ( Psi[x] ) );
      px[0] = xpts[x];
      px[1] = xpts[x];
      py[1] = ypts[x];
      px[2] = xpts[x+1];
      py[2] = ypts[x+1];
      px[3] = xpts[x+1];
      g.fillPolygon(px,py,4);
    }	
  }


  public void run() {
    while ( kicker != null ){
      step();step();step();
      t += dt;
      repaint();
      try {
	Thread.sleep(60);
      } catch (InterruptedException e) {
	break;
      }
    }
  }
  /**
   * Start the applet by forking an animation thread.
   */
  public void start() {
    if (kicker == null) {
      kicker = new Thread(this);
      kicker.start();
    }
  }

  /**
   * Stop the applet. The thread will exit because kicker is set to null.
   */
  public void stop() {
    if (kicker != null) {
      kicker.stop();
      kicker = null;
    }
  }

  public boolean mouseDown(java.awt.Event evt, int x, int y) {
    requestFocus();
    /*
    requestFocus();
    if( null != kicker ) stop();
    else start();
    */
    return true;
  }

  public boolean action(Event evt, Object arg) {
    if (evt.target instanceof Choice) {
      if("Barrier V = E".equals(arg)) {
	energyScale = 1;
      } else if("Barrier V = E/2".equals(arg)) {
	energyScale = 0.5;
      } else if("Barrier V = 2*E".equals(arg)) {
	energyScale = 2;
      } else if("Well V = -2*E".equals(arg)) {
	energyScale = -2;
      } else if("Well V = -E".equals(arg)) {
	energyScale = -1;
      } else if("Well V = -E/2".equals(arg)) {
	energyScale = -0.5;
      } else if("No Barrier".equals(arg)) {
	energyScale = 0;
      }
    } else {
      if ("Stop".equals(arg)) {
	stop();
	Stop.disable();
	Restart.enable();
	C.enable();
	Pause.disable();
      } else if ("Restart".equals(arg)) {
	Stop.enable();
	Restart.disable();
	C.disable();
	Pause.enable();
	Pause.setLabel("Pause");
	initPhysics();
	start();
      } else if ("Pause".equals(arg)) {
	stop();
	Pause.setLabel("Resume");
      } else if ("Resume".equals(arg)) {
	start();
	Pause.setLabel("Pause");
      } 
    }
    return true;
  }
  public void step(){
    complex x = new complex(0,0);
    complex y = new complex(0,0);
    complex w = new complex(0,0);
    complex z = new complex(0,0);

    /*
     * The time stepping algorithm used here is described in:
     *
     * Richardson, John L.,
     * Visualizing quantum scattering on the CM-2 supercomputer,
     * Computer Physics Communications 63 (1991) pp 84-94 
     */

    for(int i=0;i<nx-1;i+=2){
      x.set(Psi[i]);
      y.set(Psi[i+1]);
      w.mult(alpha,x);
      z.mult(beta,y);
      Psi[i+0].add(w,z);
      w.mult(alpha,y);
      z.mult(beta,x);
      Psi[i+1].add(w,z);
    }

    for(int i=1;i<nx-1;i+=2){
      x.set(Psi[i]);
      y.set(Psi[i+1]);
      w.mult(alpha,x);
      z.mult(beta,y);
      Psi[i+0].add(w,z);
      w.mult(alpha,y);
      z.mult(beta,x);
      Psi[i+1].add(w,z);
    }

    x.set(Psi[nx-1]);
    y.set(Psi[0]);
    w.mult(alpha,x);
    z.mult(beta,y);
    Psi[nx-1].add(w,z);
    w.mult(alpha,y);
    z.mult(beta,x);
    Psi[0   ].add(w,z);

    for(int i=0;i<nx;i++){
      x.set(Psi[i]);
      Psi[i].mult(x,EtoV[i]);
    }

    x.set(Psi[nx-1]);
    y.set(Psi[0]);
    w.mult(alpha,x);
    z.mult(beta,y);
    Psi[nx-1].add(w,z);
    w.mult(alpha,y);
    z.mult(beta,x);
    Psi[0   ].add(w,z);

    for(int i=1;i<nx-1;i+=2){
      x.set(Psi[i]);
      y.set(Psi[i+1]);
      w.mult(alpha,x);
      z.mult(beta,y);
      Psi[i+0].add(w,z);
      w.mult(alpha,y);
      z.mult(beta,x);
      Psi[i+1].add(w,z);
    }

    for(int i=0;i<nx-1;i+=2){
      x.set(Psi[i]);
      y.set(Psi[i+1]);
      w.mult(alpha,x);
      z.mult(beta,y);
      Psi[i+0].add(w,z);
      w.mult(alpha,y);
      z.mult(beta,x);
      Psi[i+1].add(w,z);
    }
  }

  public double Norm() {
    double sum = 0.0;
    for(int x=0;x<nx;x++)
      sum += Psi[x].re*Psi[x].re + Psi[x].im*Psi[x].im;
    return sum;
  }	


  class complex {
    double re,im;
    complex(double x,double y){
      re = x;
      im = y;
    }
    public void add(complex a,complex b){
      re = a.re + b.re;
      im = a.im + b.im;
    }
    public void mult(complex a,complex b){
      re = a.re * b.re - a.im * b.im;
      im = a.re * b.im + a.im * b.re;
    }
    public void set(complex a){
      re = a.re;
      im = a.im;
    }
  }
}
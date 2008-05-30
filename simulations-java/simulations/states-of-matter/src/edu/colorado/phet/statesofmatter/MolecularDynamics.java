package edu.colorado.phet.statesofmatter;


//	Molecular Dynamics Applet copyright Paul Beale, University of Colorado, 1999



// version 0.5 float variables, fast force, full force calculation, second order runge-kutta
// no interface, no statistics, 100 particles 28 frames per second, single radius, single mass


// version 0.6, double variables

// version 0.65 Lennard-Jones force, different radii and masses, 100 particles 12.5 frames per second
// reset Energy to initial value by rescaling energy, brownian particle

// version 0.70 Implement 4th order Runge-Kutta

//  ************************************ TO DO list ************************************
//
// 4th order runge-kutta
// cell method for limiting number of neighbors
// constant temperature ensemble
// constant pressure-constant temperature ensemble
// grab particle and throw
// grab piston
// radio buttons for ensemble
// show statistics in separate window
// control equibrate/collect statistics with radio button
// set temperature, pressure, density, particle size, interaction range,
//    brownian particle, particle masses, gravity, from separate window
// make window resizable
// measure diffusion constant of brownian particle
// determine and plot g(r) and S(k), measure pressure and compressibility
//   and compare with other methods
// determine and plot density profile vs height in gravitational field


import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Date;
import java.util.Random;

public class MolecularDynamics extends Applet implements Runnable
{
	Thread animatorThread=null;

	double[] x,y,vx,vy;


// temporary variables for 4th order Runge-Kutta
	double[] x1,y1,vx1,vy1,vx2,vy2,fx1,fy1,fx2,fy2,fx3,fy3;

	double piston,pistonvy;
	double v0,ke;
	//double timeStep,diameter,diameterSq,iDiameter;
	double targetEnergy;
	int n;
	double width,height,radius[],mass,massInverse[],gravity,range,rangeSquared,rangeSquaredInverse;
	double brownianParticleMass,brownianParticleRadius;
	int iWidth,iHeight,iRadius;
	Image offscreen;
	Date date;
	Random rand;
	Font font;
	FontMetrics fontMetrics;
	Color color[];
	boolean pleaseStop=false;
	boolean showDensity;
	double forceCoefficient;
	int iter=0;

	double[] densitySum,keSum;

	public void init()
	{

		String str;
		str = getParameter("n");
		if (str == null) n=20;
		else n = Integer.parseInt(str);
		str = getParameter("radius");
		if (str == null) iRadius=10;
		else iRadius = Integer.parseInt(str);
		str = getParameter("ke");
		if (str == null) ke=0.0;
		else ke = Double.valueOf(str).doubleValue();
		str = getParameter("forceCoefficient");
		if (str == null) forceCoefficient=0.000001;
		else forceCoefficient = Double.valueOf(str).doubleValue();
		str = getParameter("gravity");
		if (str == null) gravity=0.00000;
		else gravity = Double.valueOf(str).doubleValue();
		str = getParameter("range");
		if (str == null) range=Math.pow(2.0,1.0/6.0);
		else range = Double.valueOf(str).doubleValue();
		str = getParameter("mass");
		if (str == null) mass=1024.;
		else mass = Double.valueOf(str).doubleValue();

		str = getParameter("brownianParticleMass");
		if (str == null) brownianParticleMass=mass;
		else brownianParticleMass = Double.valueOf(str).doubleValue();
		str = getParameter("brownianParticleRadius");
		if (str == null) brownianParticleRadius=iRadius;
		else brownianParticleRadius = Double.valueOf(str).doubleValue();

		str = getParameter("showDensity");
		if (str == null) showDensity=false;
		else  showDensity=true;

		rangeSquared=range*range;
		rangeSquaredInverse=1.0/rangeSquared;
		v0=Math.sqrt(24.*ke/mass);

		Dimension d = this.size();
		iWidth=d.width;
		iHeight=d.height;
		offscreen=this.createImage(iWidth,iHeight);
		width=iWidth;
		height=iHeight;

		offscreen = this.createImage(iWidth,iHeight);

		date=new Date();
		for (int pt=24;pt>4;pt--)
		{
				font = new Font("TimesRoman",Font.BOLD,pt);
				fontMetrics=this.getFontMetrics(font);
				if (20+fontMetrics.stringWidth(date.toString())<iWidth) break;
		}



		x = new double[n];
		y = new double[n];
		vx = new double[n];
		vy = new double[n];
		color = new Color[n];

//	Runge-Kutta temporary variables

		 x1=new double[n];
		 y1=new double[n];
		 vx1=new double[n];
		 vy1=new double[n];
		 vx2=new double[n];
		 vy2=new double[n];
		 fx1=new double[n];
		 fy1=new double[n];
		 fx2=new double[n];
		 fy2=new double[n];
		 fx3=new double[n];
		 fy3=new double[n];





		massInverse=new double[n];
		radius=new double[n];

		piston=0.0;pistonvy=0.0;
		for (int i=0;i<n;i++) massInverse[i]=1.0/mass;
		for (int i=0;i<n;i++) radius[i]=iRadius;
		massInverse[0] = 1.0/brownianParticleMass;
		radius[0] = brownianParticleRadius;

		rand=new Random();

		insertParticlesWithoutOverlap();

		for (int i=0;i<n;i++)
		{
			vx[i]=Math.sqrt(12.*ke*massInverse[i])*(rand.nextDouble()-0.5);
			vy[i]=Math.sqrt(12.*ke*massInverse[i])*(rand.nextDouble()-0.5);


			if (i%6==0) color[i] = Color.red;
			if (i%6==1) color[i] = Color.blue;
			if (i%6==2) color[i] = Color.green;
			if (i%6==3) color[i] = Color.yellow;
			if (i%6==4) color[i] = Color.cyan;
			if (i%6==5) color[i] = Color.magenta;
		}



		targetEnergy=kineticEnergy()+potentialEnergy();

		densitySum=new double[10];
		keSum=new double[10];



	}


	public void start()
	{
		animatorThread=new Thread(this);
		animatorThread.start();
	}

	public void stop()
	{
		if (animatorThread != null) animatorThread.stop();
		animatorThread=null;
	}

	public boolean mouseDown(Event e, int x, int y)
	{
		if (animatorThread != null) pleaseStop=true;
		else
		{
			pleaseStop=false;
			start();
		}
		return true;
	}


	public void run()
	{
		while (!pleaseStop)
		{
			Dimension d=this.size();
			if (offscreen == null || iWidth != d.width || iHeight != d.height)
			{
				offscreen=this.createImage(d.width,d.height);
				width = iWidth = d.width;
				height = iHeight = d.height;


				date=new Date();
				for (int pt=24;pt>4;pt--)
				{
						font = new Font("TimesRoman",Font.BOLD,pt);
						fontMetrics=this.getFontMetrics(font);
						if (20+fontMetrics.stringWidth(date.toString())<iWidth) break;
				}

			}

			for (int iter=0;iter<3;iter++) updatePositions();
			Graphics g = offscreen.getGraphics();
			paint(g);
			g=this.getGraphics();
			g.drawImage(offscreen,0,0,this);
			try{ Thread.sleep(1);}
			catch (InterruptedException e) { }

			resetEnergy(targetEnergy);

			if (iter>500) verticalAverager();
		}
		animatorThread=null;
	}

/**************/

	public void paint(Graphics g)
	{
		drawBackground(g);
		drawBalls(g);
		drawDate(g);
		drawEnergy(g);
		drawIter(g);
		if (showDensity) drawVerticalAverages(g);
	}

	void drawDate(Graphics g)
	{
		g.setColor(Color.white);
		date = new Date();
		g.setFont(font);
		g.drawString(date.toString(),10,10+fontMetrics.getHeight() );
	}

	void drawEnergy(Graphics g)
	{
		double ke,pe;

		g.setColor(Color.white);

		ke=kineticEnergy()/n;
		pe=potentialEnergy()/n;
		g.setFont(font);
		try
		{
			g.drawString("Kinetic Energy="+Double.toString(ke).substring(0,8),10,50+fontMetrics.getHeight() );
			g.drawString("Energy="+Double.toString(ke+pe).substring(0,8),10,70+fontMetrics.getHeight() );
		}
		catch(StringIndexOutOfBoundsException e){};
	}

	void drawVerticalAverages(Graphics g)
	{
		double density=0.0;
		g.setColor(Color.white);
		g.setFont(font);

		for (int i=1;i<9;i++) density+=densitySum[i];

		g.drawString("density KE",
					400,10+fontMetrics.getHeight() );
		for (int i=1;i<9;i++)
		{
			try
			{
				if (densitySum[i]>0.0)
					g.drawString(Double.toString(densitySum[i]/density).substring(0,5)+"  "+
					Double.toString(keSum[i]/densitySum[i]).substring(0,5),
					400,10+(int)(i*height/11.0)+fontMetrics.getHeight() );
			}
			catch (StringIndexOutOfBoundsException e){};
		}
	}

	void drawIter(Graphics g)
	{
		iter++;
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString("Iterations="+Integer.toString(iter),10,90+fontMetrics.getHeight() );
		g.drawString("N="+Integer.toString(n),10,110+fontMetrics.getHeight() );
	}

	void drawBackground(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0,0,iWidth,iHeight);
	}

	void drawBalls(Graphics g)
	{
		for (int i=0;i<n;i++)
		{
			g.setColor(color[i]);
			g.fillOval((int) (x[i]-radius[i]),(int)(y[i]-radius[i]),2*(int)radius[i],2*(int)radius[i]);
		}
	}



// returns Lennard-Jones potential

	double phi(double s2)
	{
		double s4,s6,s12;

		if (s2>rangeSquared) return 0.0;

		s4=s2*s2;
		s6=s2*s4;
		s12=s6*s6;
		return 4.0/s12-4.0/s6;
	}

	double phiWall(double s2)
	{
		double s4,s6,s12;

		if (s2>1.2599210498949) return 0.0;

		s4=s2*s2;
		s6=s2*s4;
		s12=s6*s6;
		return 1.0+4.0/s12-4.0/s6;
	}

// returns Lennard-Jones force divided by scaled radius.
// it is only a function of scaled radius squared

	double phiPrime(double s2)
	{
		double s4,s6,s8,s14;

		if (s2>rangeSquared) return 0.0;

		s4=s2*s2;
		s6=s2*s4;
		s8=s4*s4;
		s14=s6*s8;
		return 24.0/s8-48.0/s14;
	}

	double phiPrimeWall(double s2)
	{
		//static final rangeSquared=Math.pow(2.0,1.0/3.0);
		double s4,s6,s8,s14;

		if (s2>1.2599210498949) return 0.0;

		s4=s2*s2;
		s6=s2*s4;
		s8=s4*s4;
		s14=s6*s8;
		return (24.0/s8-48.0/s14);
	}

	void calculateForces(double x[], double y[], double vx[], double vy[], double fx[], double fy[])
	{
		double dx,dy,s,s2,rij,rijInverseSquared,phiPr;

		for (int i=0; i<n; i++)
		{
			fx[i]=0.0; //-gamma*vx[i];
			fy[i]=gravity; //-gamma*vy[i];
		}

		// wall forces

		//left wall
		for (int i=0; i<n; i++)
		{
			dx=x[i];
			s=dx/radius[i];
			s2=s*s;
			fx[i] -= massInverse[i]*dx*phiPrimeWall(s2)/(radius[i]*radius[i]);
		}

		//right wall
		for (int i=0; i<n; i++)
		{
			dx=width-x[i];
			s=dx/radius[i];
			s2=s*s;
			fx[i] += massInverse[i]*dx*phiPrimeWall(s2)/(radius[i]*radius[i]);
		}

		//top wall
		for (int i=0; i<n; i++)
		{
			dy=y[i]-piston;
			s=dy/radius[i];
			s2=s*s;
			fy[i] -= massInverse[i]*dy*phiPrimeWall(s2)/(radius[i]*radius[i]);
		}

		//bottom wall
		for (int i=0; i<n; i++)
		{
			dy=height - y[i];
			s=dy/radius[i];
			s2=s*s;
			fy[i] += massInverse[i]*dy*phiPrimeWall(s2)/(radius[i]*radius[i]);
		}

//interactions, full force calculation
		for (int i=0;i<n;i++) for (int j=i+1;j<n;j++)
		{
			dx=x[i]-x[j];
			dy=y[i]-y[j];
			rij=radius[i]+radius[j];
			rijInverseSquared=1.0/(rij*rij);
			s2=(dx*dx+dy*dy)*rijInverseSquared;
			phiPr=phiPrime(s2)*rijInverseSquared;
			fx[i] -= dx*phiPr*massInverse[i];
			fy[i] -= dy*phiPr*massInverse[i];
			fx[j] += dx*phiPr*massInverse[j];
			fy[j] += dy*phiPr*massInverse[j];
		}



	}


	/*
	// second order Runge-Kutta
	void updatePositions()
	{

		// half step for second order runga-kutta

		calculateForces(x,y,vx,vy,fx,fy);

		for (int i=0;i<n;i++)
		{
			xtemp[i]=x[i]+0.5*vx[i];
			ytemp[i]=y[i]+0.5*vy[i];
			vxtemp[i]=vx[i]+0.5*fx[i];
			vytemp[i]=vy[i]+0.5*fy[i];
		}

		// second half step for second order runga-kutta
		calculateForces(xtemp,ytemp,vxtemp,vytemp,fx,fy);

		for (int i=0;i<n;i++)
		{
			x[i]+=vxtemp[i];
			y[i]+=vytemp[i];
			vx[i]+=fx[i];
			vy[i]+=fy[i];
		}
	}
	*/


	// 4th order Runge-Kutta integration
	void updatePositions()
	{

		double oneSixth=1.0/6.0;


		// step 1 for second order runga-kutta

		calculateForces(x,y,vx,vy,fx1,fy1);

		for (int i=0;i<n;i++)
		{
			x1[i]=x[i]+0.5*vx[i];
			y1[i]=y[i]+0.5*vy[i];
			vx1[i]=vx[i]+0.5*fx1[i];
			vy1[i]=vy[i]+0.5*fy1[i];
		}


		// step 2 for second order runga-kutta

		calculateForces(x1,y1,vx1,vy1,fx2,fy2);

		for (int i=0;i<n;i++)
		{
			x1[i]=x[i]+0.5*vx1[i];
			y1[i]=y[i]+0.5*vy1[i];
			vx2[i]=vx[i]+0.5*fx2[i];
			vy2[i]=vy[i]+0.5*fy2[i];
		}

		// step 3 for second order runga-kutta

		calculateForces(x1,y1,vx2,vy2,fx3,fy3);

		for (int i=0;i<n;i++)
		{
			x1[i]=x[i]+vx2[i];
			y1[i]=y[i]+vy2[i];

			vx1[i] += vx2[i];
			vy1[i] += vy2[i];

			vx2[i]=vx[i]+fx3[i];
			vy2[i]=vy[i]+fy3[i];

			fx2[i] += fx3[i];
			fy2[i] += fy3[i];
		}

		// step 4 for second order runga-kutta

		calculateForces(x1,y1,vx2,vy2,fx3,fy3);


		for (int i=0;i<n;i++)
		{
			x[i] += oneSixth*(vx[i]+2*vx1[i]+vx2[i]);
			y[i] += oneSixth*(vy[i]+2*vy1[i]+vy2[i]);
			vx[i] += oneSixth*(fx1[i]+2*fx2[i]+fx3[i]);
			vy[i] += oneSixth*(fy1[i]+2*fy2[i]+fy3[i]);
		}


	}

	double kineticEnergy()
	{
		double ke=0.0;
		for (int i=0;i<n;i++) ke += (vx[i]*vx[i]+vy[i]*vy[i])/(massInverse[i]);
		ke *= 0.5;
		return ke;
	}


	double potentialEnergy()
	{
		double dx,dy,s2;
		double pe=0.0;

		for (int i=0;i<n;i++) pe += (height-y[i])*gravity/massInverse[i];

		for (int i=0;i<n;i++)
		{
			s2=x[i]*x[i]/(radius[i]*radius[i]);
			pe += phiWall(s2);
			s2=(width-x[i])*(width-x[i])/(radius[i]*radius[i]);
			pe += phiWall(s2);
			s2=y[i]*y[i]/(radius[i]*radius[i]);
			pe += phiWall(s2);
			s2=(height-y[i])*(height-y[i])/(radius[i]*radius[i]);
			pe += phiWall(s2);
		}

		for (int i=0;i<n;i++) for (int j=i+1;j<n;j++)
		{
			dx=x[i]-x[j];
			dy=y[i]-y[j];
			s2=(dx*dx+dy*dy)/((radius[i]+radius[j])*(radius[i]+radius[j]));
			pe += phi(s2);
		}

		return pe;

	}


	void resetEnergy(double targetEnergy)
	{
		double ke,pe,lamda;
		ke=kineticEnergy();
		pe=potentialEnergy();
		lamda=Math.sqrt((targetEnergy-pe)/ke);
		for (int i=0;i<n;i++)
		{
			vx[i] *= lamda;
			vy[i] *= lamda;
		}

	}

	void insertParticlesWithoutOverlap()
	{

		boolean ok;
		double dx,dy,r2;
		int iter=0,i=0;

		while (i<n && iter<1000*n)
		{
			x[i] = (width-2.0*radius[i]*Math.pow(2.,1./6.))*rand.nextDouble()
					+radius[i]*Math.pow(2.,1./6.);
			y[i] = piston+(height-piston-2.0*radius[i]*Math.pow(2.,1./6.))*rand.nextDouble()
					+radius[i]*Math.pow(2.,1./6.);

			ok=true;
			for (int j=0;j<i;j++)
			{
				dx=x[i]-x[j];
				dy=y[i]-y[j];
				r2=(dx*dx+dy*dy)/((radius[i]+radius[j])*(radius[i]+radius[j]));
				if (r2 < Math.pow(2.,1./3.))
				{
					ok = false;
					iter++;
					break;
				}
			}

			if (ok) i++;
		}

		n=i;

	}


	void verticalAverager()
	{
		int heightBin;

		for (int i=0;i<n;i++)
		{
			heightBin=(int)(10.0*y[i]/height);
			densitySum[heightBin]+=1.0;
			keSum[heightBin]+=0.5*(vx[i]*vx[i]+vy[i]*vy[i])/massInverse[i];
		}
	}

}

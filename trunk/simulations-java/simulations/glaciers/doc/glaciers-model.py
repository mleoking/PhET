"""
Basic Glacier Model

Wed Jan 30 01:15:03 MST 2008

- all variables are defined on a grid of 1000 evenly-spaced x-values
- requires import of numerical libraries (pylab) from matplotlib:
    http://matplotlib.sourceforge.net
"""

import os
from pylab import *

################################################################################

class Mountain:

    def __init__(self, num_x=1000, x_max=80e3, headwater_width=5e3 ):
        # set x coordinate, meters
        dx = x_max/num_x
        x = arange(0.0,x_max,dx)
        self.x = x
        self.num_x = num_x
        # set F (elevation of valley floor) in meters
        self.F = 4e3 - x/30. + exp(-(x-5e3)/1e3) 
        # set W (width of valley) in meters
        self.W = 1e3 + headwater_width*exp( -((x-5e3)/2e3)**2 )   # large headwaters

    def plot(self,fignum=2):
        figure(fignum)
        plot( self.x/1e3, self.F/1e3 )
        xlabel('x, horizontal distance (km)')
        ylabel('profile (km)')
        grid(True)

################################################################################

class Climate:

    def __init__(self, F, init_snowfall_lapse_rate=5e-4, init_ref_temp=0.0,
                 snowfall_max=2.0, melt_v_elev=-0.011, melt_v_temp=1.3, melt_temp0=45. ):
        self.z = F                         # z (meters of elevation) valley floor elevation
        self.snowfall_max = snowfall_max   # maximum possible snowfall, m/yr
        self.melt_v_elev = melt_v_elev     # ablation rate per meter elevation
        self.melt_v_temp = melt_v_temp     # ablation curve shift per degree celcius
        self.melt_temp0 = melt_temp0       # ablation rate at zero temp difference
        self.set_new_climate( init_snowfall_lapse_rate, init_ref_temp )

    def set_new_climate(self,snowfall_lapse_rate,temp):
        self.snowfall_lapse_rate = snowfall_lapse_rate  # measure of snowfall amount
        self.ref_temp = temp                # temperature difference from present, celcius 
        self.temperature = 20.+temp - 6.5*self.z/1e3 # temp vs altitude (celcius)
        self._set_mass_balance()

    def _set_mass_balance(self,z=None):
        "only used internally"
        if z is None: z = self.z
        # find mass balance at all elevations
        ab = self.melt_v_elev*z + self.melt_temp0 + self.melt_v_temp*self.ref_temp
        self.ablation = clip( ab, 0, 9e9 )
        ac = z * self.snowfall_lapse_rate
        self.accumulation = clip( ac, 0, self.snowfall_max )
        self.mass_balance = self.accumulation - self.ablation
        # find ELA (elevation of zero mass balance)
        self.ela_index = argmin( abs(self.mass_balance) )
        self.ela = self.z[self.ela_index]   

    def plot(self,fignum=1):
        figure(fignum)
        xx = self.z/1e3
        plot( xx, self.accumulation, label='accumulation' )
        plot( xx, -self.ablation, label='-(ablation)' )
        plot( xx, self.mass_balance, label='mass balance' )
        xlabel('z, elevation (km)')
        ylabel('mass balance (m/yr)')
        grid(True)
        ylim(ymax=1.5*self.snowfall_max)
        legend(loc='lower right')

################################################################################

class Glacier:

    def __init__(self, m, max_thickness=500.0 ):
        """
        m is a Mountain object
        """
        self.m = m  # mountain object (has x-coordinates, valley floor height and width)
        self.max_thickness = max_thickness
        # calculate dx in case x is not uniformly spaced:
        self.dx = m.x.copy()
        self.dx[1:] -= m.x[:-1]
        self.dx[0]   = self.dx[1] 
        #
        self.H = zeros( m.x.shape, Float ) # initial glacier height set to zero
        self.set_ice_velocities()

    def set_volume_flux( self, B ):
        """
        find volume flux function, Q(x)
        B (mass balance) must be defined at self.x points
            mass balance is a function of elevation; B is the same, but as a function of x
        volume_flux = Q = integral( W*B*dx )
        """
        # do integral:
        Q = cumsum( self.m.W * B * self.dx )  # cumulative sum
        self.Q = clip( Q, 0.0, 9e9 )

    def set_equilibrium( self, B ):
        """
        find steady state (equilibrium) glacier height, H(x)
        for mass balance B (defined at self.x points)
        """
        self.set_volume_flux(B)   # defines self.Q
        max_Q = maximum.reduce( self.Q )  # maximum value of Q
        terminus_index = argmin( self.Q )  # x-value of glacier terminus (endpoint)
        profile_scale = (self.max_thickness/max_Q) * (terminus_index/float(self.m.num_x))
        self.H = self.Q * profile_scale
        self.x_terminus = self.m.x[terminus_index]
        self.terminus_index = terminus_index

    def set_ice_velocities( self ):
        # constant sliding velocity:
        self.u_slide = zeros( self.H.shape, Float ) + 20.
        # variable (verically-averaged) deformation velocity:
        u0 = self.H**2
        u0ave = average(u0[:self.terminus_index])
        if u0ave != 0: u0 *= 20 / u0ave
        self.u_deform_ave = u0
        # total vertically-averaged velocity:
        self.u_ave = self.u_deform_ave + self.u_slide

    def vertical_velocities( self, x_index, num_pts=100 ):
        """
        vertical velocity profile
        x_index: specify the x-index at which to return the velocities
        """
        zz = arange(0.,1.,1./num_pts)
        u_s = self.u_slide[x_index]
        u_d = self.u_deform_ave[x_index]
        velocity = u_s + u_d*5.*( zz - 1.5*zz**2 + zz**3 - 0.25*zz**4 )
        return zz,velocity

    def plot(self,fignum=2):
        figure(fignum)
        plot( self.m.x/1e3, (self.m.F+self.H)/1e3 )
        #xlim(0,1.5*self.x_terminus/1e3)
        xlabel('x, horizontal distance (km)')
        grid(True)

################################################################################
# climate change timescale

def timescale( ela ):
    """
    timescale depends only on ELA
    estimated from Kessler et al. (figure 9)
    """
    slope,intercept = -35.7/300., 484.6
    clipmin,clipmax = 20.,50.
    tau = slope*array(ela) + intercept
    return clip( tau, clipmin, clipmax )

def timescale( ela_init, ela_final ):
    t0 = timescale(  ela_init )
    t1 = timescale( ela_final )
    return average([t0,t1])

################################################################################
################################################################################
# test code:

if 0:  
    # instantiate objects
    m = Mountain( headwater_width=0. )
    c = Climate( m.F )
    g = Glacier( m )

    # set climate
    c.set_new_climate( 5e-4, 0. )
    g.set_equilibrium( c.mass_balance )

    # make plots
    c.plot()
    m.plot()
    g.plot()

    # new climate
    c.set_new_climate( 10e-4, -5. )
    g.set_equilibrium( c.mass_balance )
    c.plot()
    g.plot()

    # look at temperature-ELA relationship
    temp_range = arange( -15.0, 10.0, 1.0 )
    ela_v_temp = []
    for temperature in temp_range:
        c.set_new_climate( 5e-4, temperature )
        ela_v_temp.append( c.ela )
    if 0:    # plot it
        figure(4)
        clf()
        plot( temp_range, array(ela_v_temp)/1e3 )
        grid()
        xlabel('reference temperature')
        ylabel('ELA (km)')






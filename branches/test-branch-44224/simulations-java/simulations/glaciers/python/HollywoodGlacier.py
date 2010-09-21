"""
version 5.0

author: Archie Paulson
last modified: Sun May 25 15:34:14 MDT 2008

This module defines the class the Hollywood glacier models
    - this model is used in the sim
    - this model is based on the finite element results, by fitting simple
      functions to the shapes 
    - given an ELA, the object computes its shape
"""

from pylab import *
from lib import derivative

###############################################################################
# constants

yts = 365*24*60*60. # year to seconds conversion
rho = 1e3  # density of ice, kg/m^3
gg = 9.8    # gravitational acceleration, m/s^2

# from Kessler et al (eq 8):
AA = 6.8e-24 * yts  # units: 1/(Pa^3.yr)
# from Kessler et al (eq 9):
Uc = 20.      # reference ice velocity, m/yr
tau_c = 1e5   # reference gravitational stress, Pa

###############################################################################

class HollywoodGlacier:
    """
    This is the Hollywood version, which just takes an ELA and computes
    the resulting steady-state glacier shape.
    """

    def __init__(self, m, init_ela=4e3 ):
        """
        init args:
            m is a Mountain object, with information on x-positions and
                elevations of each element
        """
        self.m = m
        self.x = m.x
        self.H = zeros(self.x.shape,'d')
        self.plot = self.plot_profile
        self.__set_constants__()
        # set inital climate and find ela:
        self.ela = self.max_F
        self.set_new_climate(init_ela) # no args => don't change temp or precip

    def __set_constants__(self):
        "called by __init__() to set constants once."
        # prep for x_term calculation:
        self.max_F = max(self.m.F)  # maximum elevation of valley floor
        self.min_ela = 1500.           # ELA can never go below this (meters)
        self.max_xterm = 108e3         # x_terminus when ELA=min_ELA (meters)
        self.xterm_ela_slope = -self.max_xterm/(self.max_F-self.min_ela)
        self.xterm_ela_intcpt = -self.max_F * self.xterm_ela_slope
        # prep for h_max & terminus calculations:
        self.hmax_scale = 2.3
        self.elax_terminus = 0.6
        # prep for elax calculation:
        self.elax_m0 = -55630/(self.max_F-2.7e3)
        self.elax_b0 = 138248
        # prep for time calculation:
        self.ela_tolerance = 0.01  # any lower qela speed => equilib (m/yr)
                
    def get_q_advance_limit(self,qela):
        terminus_advance_limit = -0.06*qela + 300
        return terminus_advance_limit* self.elax_terminus / self.elax_m0

    def get_h_max(self,elax):
        """
        This finds H_max (max glacier height) as a function of ELA_x.
        """
        xx = clip( elax, 0, 9e9 )
        hmax = self.hmax_scale * sqrt(xx)
        return hmax

    def get_x_terminus(self,elax):
        """
        This finds x-terminus (length of glacier), given the ELA_x.
        """
        xterm =  elax / self.elax_terminus
        return clip( xterm, 0, 9e9 )

    def get_ela_x(self,ela):
        """
        This finds the x-coordinate of the ELA on the glacier
        surface (called elax or ELA_x), given the ELA.
        """
        elax = self.elax_b0 + self.elax_m0*ela
        return clip( elax, 0.0, 9e9 )

    def set_new_climate(self,ela):
        """
        Set new (steady state) glacier profile according to ELA.
        """
        self.prev_ela = self.ela
        self.ela = ela
        self.elax = self.get_ela_x(ela)
        x_terminus = self.get_x_terminus(self.elax)
        H_max = self.get_h_max(self.elax)
        x_peak = 0.5 * x_terminus
        # calculate array of ice heights, H:
        for i,x in enumerate(self.x):
            if x < x_peak: 
                p = max(42-0.01*ela,1.5)
                f = 1.5
                r = f*x_peak
                self.H[i] = sqrt(r**2 - (x-x_peak)**2) * H_max/r
                self.H[i] *= ( x_peak**p - (abs(x-x_peak)**p))/ x_peak**p
            elif x < x_terminus:
                self.H[i] = sqrt(x_peak**2 - (x-x_peak)**2) * H_max/x_peak
            else:
                self.H[i] = 0.0
        self.terminus_index = searchsorted(self.x,x_terminus)
        self.x_terminus = x_terminus
        self.x_peak = x_peak
        self.H_max = H_max

    def set_ice_velocities( self, tau=None ):
        """
        sets (self.) tau, u_slide, u_deform_ave, u_ave
        depends on H and F only
        """
        # set basal shear stress:  tau = rho.g.H. d(H+F)/dx    units of Pascals
        self.tau = rho*gg*self.H* abs(derivative( self.m.x, self.m.F+self.H ))
        tau1 = clip( self.tau, 1., 9e99 )  # for safe division
        factor = where( self.tau==0, 0.0, exp( 1.-tau_c/tau1 ) )
        # sliding velocity:
        self.u_slide = ones( self.H.shape, 'd' ) * Uc * factor
        # variable (verically-averaged) deformation velocity:
        u0 = 0.4*AA * self.H * (self.tau**3)
        self.u_deform_ave = u0
        # total vertically-averaged velocity:
        self.u_ave = self.u_deform_ave + self.u_slide

    def set_timescale( self ):
        if self.ela > self.prev_ela:  # retreating
            timescale =  -0.22*self.ela + 1026
        else:                         # advancing
            timescale =   0.35*self.ela - 1139
        self.timescale = clip( timescale, 50, 300 )

    def time_dependence( self, dt=1. ):
        """
        returns time-array and quasi-ELA as a funtion of time
        pass in dt, the time between steps (years)
        """
        if self.ela > self.prev_ela:  # retreating
            advancing = False
        else:                         # advancing
            advancing = True
        self.set_timescale()
        times,qelas = [],[]
        f = 1. - exp(-dt/self.timescale)
        t,qela = 0.0,self.prev_ela
        dq = 9e9
        while abs(dq)>self.ela_tolerance:
            times.append(t)
            qelas.append(qela)
            dq = (self.ela-qela)*f
            if advancing: dq = max(dq, dt*self.get_q_advance_limit(qela) )
            qela += dq
            t += dt
        return array(times),array(qelas)


    #################################################################
    # the following methods are only for plotting and data inpection:

    def __repr__(self):
        "return string representation of object"
        s  = "HollywoodGlacier object:\n"
        s += "length: ".rjust(20) + '%0.2f km'%(self.x_terminus/1e3)+'\n'
        s += "peak height: ".rjust(20) + '%0.2f m'%(self.H_max)+'\n'
        return s

    def plot_profile(self,fignum=2,label=None):
        figure(fignum)
        if self.m.madeplot: 
            #yy = (self.m.F+self.H)/1e3
            yy = (self.m.F+self.H)
            y_label = 'glacier/valley profile (km)'
            ylim(3.5,4.7)
        else:               
            yy = array(self.H)
            y_label = 'glacier profile (m)'
        plot( self.m.x/1e3, yy, label='ela=%0.2f'%(self.ela/1e3) )
        #xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        if 0: #self.m.madeplot: 
            ylim(self.m.F[int(self.terminus_index*1.3)]/1e3,4.7)
        ylabel(y_label)
        xlabel('x, horizontal distance (km)')
        legend()
        grid(True)

    def plot_stress(self,fignum=4,label=None):
        figure(fignum)
        plot( self.m.x/1e3, self.tau/1e5, label=label )
        xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        ylabel('basal shear stress (bars)')
        xlabel('x, horizontal distance (km)')
        if label: legend()
        grid(True)

    def plot_velocities(self,fignum=3,):
        figure(fignum)
        self.set_ice_velocities()
        plot( self.m.x/1e3, self.u_slide, label='sliding' )
        plot( self.m.x/1e3, self.u_deform_ave, label='deformation' )
        plot( self.m.x/1e3, self.u_ave, label='total' )
        xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        ylabel('velocity (m/yr)')
        xlabel('x, horizontal distance (km)')
        legend()
        grid(True)


###############################################################################
# test code:


if 0:    # if 1:
    from Mountain import Mountain
    mountain_config = {
                        'headwater_width':0.0, 
                        'headwall_length':800., 
                        'headwall_steepness':5e3,
                        'bump':False, 
                        'bump_attenuation':30., 
                        'scoop':500., 
                        'bump_position':30.,
                       }
    domain_config =    {
                        'num_x':1000,   # num ice columns in whole domain
                        'x_max':80e3,        # edge of simulation (m)
                       }
    m = Mountain( mountain_config, domain_config )
    g = Glacier( m, 4295.)


if 0:    # if 1:
    g.plot_time_dep()
    g.set_new_climate(4295.)
    g.plot_time_dep()



"""
Basic Glacier Model
version 3

author: Archie Paulson
creation date: Feb 19 2008

- this version attempts to do everything locally; no global arrays, instead a
  lot of ice columns that communicate with their immediate neighbors
  (ie, a poorman's finite element code)
- this works, but requires very small (~0.2 yr) timestep for nummerical
  stability (this is because the col-to-col velocities become large enough to
  make the neighboring column equal height in less than one year, which should
  stop the col-to-col ice flow)
- requires import of numerical libraries (pylab) from matplotlib:
  http://matplotlib.sourceforge.net
- see test code (bottom) for example of operation

"""

from pylab import *

################################################################################
# constants

yts = 365*24*60*60. # year to seconds conversion
rho = 1e3  # density of ice, kg/m^3
gg = 9.8    # gravitational acceleration, m/s^2

# from wikipedia:
temp_lapse_rate = 6.5  # degrees C per meter elevation

# from Kessler et al (eq 8):
AA = 6.8e-24 * yts  # units: 1/(Pa^3.yr)
# from Kessler et al (eq 9):
Uc = 20.      # reference ice velocity, m/yr
tau_c = 1e5   # reference gravitational stress, Pa

################################################################################

class Mountain:
    """
    Mountain object describes the valley geometry with the functions 
      get_valley_width(x)
      get_valley_elevation(x)
    """

    def __init__(self,config):
        self.config = config

    def get_valley_width(self,x):
        """
        returns W (width of valley) in meters
        x is horizontal coordinate; can be scalar or array
        """
        return 1e3 + self.config['headwater_width']*exp( -((x-5e3)/2e3)**2 ) 

    def get_valley_elevation(self,x):
        """
        returns F (height of valley floor) in meters
        x is horizontal coordinate; can be scalar or array
        """
        F = 4e3 - x/30.         # simple linear slope
        # some extra initial steepness:
        F += exp(-(x-self.config['headwall_steepness'])
                   / self.config['headwall_length'])  
        # put a bump near the top:
        if self.config['bump']:
            F += (x-self.config['scoop'])*(0.5-(1./pi)           \
                    * arctan(x/100-self.config['bump_position']))\
                    / self.config['bump_attenuation']
        return F

################################################################################

class Glacier:
    """
    A Glacier object has an ordered list of IceColumn objects, which keep
    track of the local ice thickness and velocity.
    """

    def __init__(self, m, climate, domain_config, ):
        """
        init args:
            m is a Mountain object, with information on x-positions and
                elevations of each element
            climate is a ClimateCalculator object, for calculating
                accum&ablation
            domain_config is a dictionaries of config data
        these members may be changed at will:
            self.dt (float) length in years of one timestep
        """
        self.climate = climate # ClimateCalculator object
        self.dt =    domain_config['default_dt']
        self.num_columns = domain_config['num_columns']
        self.x_max =       domain_config['x_max']
        self.H_limit = domain_config['H_limit']
        # scalar member data:
        self.time = 0.0
        self.ela_index = 0
        # instantiate column elements:
        self.columns = []
        x,dx = 0.0, self.x_max/self.num_columns
        for i in range(self.num_columns):
            F = m.get_valley_elevation(x)
            W = m.get_valley_width(x)
            new_column =  IceColumn(x,F,W,climate) 
            self.columns.append( new_column )
            x += dx
        self.dx = dx
        # set column neighbors for non-endpt columns:
        for i in range(1, self.num_columns-1):
            self.columns[i].init_neighbors(self.columns[i-1], self.columns[i+1])
        # icolumns is a list of the interior columns (the ones with neighbors):
        self.icolumns = self.columns[1:-1]
        # set inital climate and find ela:
        self.set_new_climate() # no args => don't change temp or precip

    def set_shape(self,shape):
        """
        Force the glacier to a certain shape (to start or restart).
        shape is a dictionary of parameters
        """
        a = shape['max_height']
        b = 0.5*shape['length']
        if  shape['form']=='quadratic':
            for c in self.columns: 
                H = a*( b**2 - ((c.x-b)**2))/ b**2 
                c.H = max( 0.0, H )
        elif shape['form']=='powersix':
            for c in self.columns: 
                H = a*( b**6 - ((c.x-b)**6))/ b**6 
                c.H = max( 0.0, H )
        elif shape['form']=='combo':
            for c in self.columns: 
                if c.x < b: H = a*( b**2 - ((c.x-b)**2))/ b**2 
                else:       H = a*( b**6 - ((c.x-b)**6))/ b**6 
                c.H = max( 0.0, H )

    def set_new_dt(self,dt):
        self.dt = dt

    def set_new_climate(self,t0=None,p0=None,pmax=None,ela=None):
        """
        updates ClimateCalculator and all ice columns, and finds the ELA
        """
        # special (for testing):
        if t0=='off': # turn off climate
            for i in range(self.num_columns):
                self.columns[i].acc = 0.0
                self.columns[i].abl = 0.0
            self.climate.state = 'off'
            return
        # change the state of the (global) ClimateCalculator object:
        self.climate.set_new_climate(t0,p0,pmax,ela)
        min_massbal = 9e9
        for i in range(self.num_columns):
            c = self.columns[i]
            c.get_mass_bal()  # sets local 'acc' and 'abl' for current climate
            massbal = abs(c.acc-c.abl) 
            if massbal < min_massbal:  # found candidate for ELA
                min_massbal = massbal
                self.ela_index = i
        self.ela = self.columns[self.ela_index].F
        self.ela_x = self.columns[self.ela_index].x
        self.climate.state = 'on'


    def step(self,num=1):
        """
        steps the glacier one or more timesteps (dt) ahead (default is 1)
        """
        for i in range(num):
            # first compute next H based on current H:
            for c in self.icolumns:  
                c.compute_next_H( self.dt ) 
                # check from problems:
                if c.next_H > self.H_limit: 
                    raise ValueError,'next height exceeds %0.1f m!'%self.H_limit
            # now assign current H from the next H:
            for c in self.icolumns:  c.set_next_H()    
            # advance clock
            self.time += self.dt

    def find_terminus_index(self):
        """
        returns index of first zero height, or None if not found
        """
        for i in range(1,self.num_columns):
            if self.columns[i].H==0.0: return i
        return None

    # the following methods are only for plotting and data inpection:

    def __repr__(self):
        "return string representation of object"
        s  = "Climate:".rjust(10) + '\n'
        s += str(self.climate)
        s += "Eq Line:".rjust(10) + '\n'
        s += "index: ".rjust(20) + '%d '%(self.ela_index)+'\n'
        s += "elevation: ".rjust(20) + '%0.2f km'%(self.ela/1e3)+'\n'
        s += "x: ".rjust(20) + '%0.2f km'%(self.ela_x/1e3)+'\n'
        return s

    def __getitem__(self,item):
        """
        get values from ice columns, return as a single array (for plotting,
        inspection, etc)
        """
        value = array( [getattr(c,item) for c in self.columns] )
        setattr(self,item,value)
        return value

    def plot(self, plotattr='H', fignum=2, scale=1.0, label=None ):
        figure(fignum)
        if not hasattr(self,'x'): self.x = self['x']  # sets array self.x
        if not label: label = '%s @ t=%g'%(plotattr,self.time)
        yy = self[plotattr]*scale
        if plotattr=='H': yy += self['F']*scale
        plot( self.x/1e3, yy, label=label )
        xlabel('x, horizontal distance (km)')
        legend()
        grid(True)

    def plot_massbal(self, fignum=10, ):
        figure(fignum)
        yy = self['F']/1e3
        acc,abl = self['acc'], self['abl']
        plot( acc, yy, label='accumulation' )
        plot( -abl, yy, label='- ablation' )
        plot( acc-abl, yy, label='mass balance' )
        ylabel('elevation (km)')
        xlabel('mass balance (m/yr)')
        text( average(xlim()), self.ela/1e3, '-- ELA %0.1fkm --'%(self.ela/1e3),
              verticalalignment='center',backgroundcolor='white')
        xlim(ymax=1.5*self.climate.pmax)
        legend(loc='upper left')
        grid(True)

################################################################################

class IceColumn:
    """
    An IceColumn object will vary in height depending only on the local climate
    and its two neighbors.
    """

    def __init__(self, x, F, W, mbcalc ):
        # scalars (floats):
        self.x = x         # column x-position (m)
        self.F = F         # valley floor elevation at this x (m)
        self.W = W         # valley (and column) width at this x (m)
        # object:
        self.mbcalc = mbcalc  # ClimateCalculator object 
        # member data (all are scalars):
        self.H = 0.0       # column height (m)
        self.Q = 0.0       # local ice volume_flux (m^3/yr)
        self.u_s = 0.0     # sliding ice velocity (m/yr)
        self.u_d = 0.0     # deformation ice velocity (m/yr)
        self.u   = 0.0     # total ice velocity (u=u_s+u_d)
        self.acc = 0.0     # local accumulation (m/yr)
        self.abl = 0.0     # local ablation (m/yr)
        self.stress = 0.0  # basal shear stress (Pa)
        self.next_H = 0.0  # temporary column height (m)
        self.next_Q = 0.0  # temporary local ice volume_flux (m^3/yr)
        self.dx = 0.0      # column width (x2)
        self.dFdx = 0.0    # (negative of) slope of valley floor
        self.area = 1.0    # constant cross-sectional area in x-y plane (m^2)

    def init_neighbors(self,nbr_L,nbr_R):
        """
        nbr_L and nbr_R are neighboring IceColumn instances (left and right
        sides). This routine should be executed right after all columns are
        instantiated.
        """
        self.nbr_L = nbr_L
        self.nbr_R = nbr_R
        self.dx = self.x - self.nbr_L.x  # 
        self.dFdx = ( self.nbr_L.F - self.F ) / self.dx
        self.area = self.W * self.dx  # cross-sectional area in x-y plane (m^2)

    def get_mass_bal(self):
        """
        sets (self.) acc and abl depending on current state of the
        ClimateCalculator and on the local elevation of this ice column
        """
        self.acc = self.mbcalc.accumulation(self.F)
        self.abl = self.mbcalc.ablation(self.F)

    def compute_velocities(self):
        """
        velocities are computed from height of self and self's two neighbors
        sets (self.) stress, u_s, u_d, u
        """
        dHdx = ( self.H - self.nbr_R.H )  / self.dx
        self.stress = rho*gg* self.H * ( dHdx + self.dFdx )
        # Kessler et.al. eq 9:
        self.u_s = Uc * exp( 1. - tau_c/self.stress )
        # Kessler et.al. eq 8:
        self.u_d = 0.4*AA*self.H* self.stress**3
        self.u = (self.u_s + self.u_d) 

    def compute_next_H(self,dt):
        """
        Computes what H will be in the future (in time 'dt'), based on current
        height.
        sets (self.) Q and next_H
        """
        # find u from current height (of self&neighbors):
        self.compute_velocities()
        # find current volume flux:
        self.Q = self.H * self.W * self.u
        # sum all mass fluxes into&outof this column:
        dHdt  = self.acc - self.abl  # flux from snowfall & melt
        # flow in from left neighbor:
        dHdt += self.nbr_L.Q / self.nbr_L.area
        # flow out to right neighbor:
        dHdt -= self.Q / self.area
        # dH is height change:
        dH = dHdt * dt
        #if dH > ( self.nbr_L.H + self.nbr_L.F - self.H - self.F ): 
        #    print '%g'%self.x,
        # compute H for next step:
        self.next_H = max( 0.0, self.H + dH )

    def set_next_H(self):
        self.H = self.next_H

################################################################################

class ClimateCalculator:
    """
    This object will calculate the local mass balance given either
        - t0 (temperature), p0 (snowfall precipitation), and pmax (snowfall_max)
          or
        - ela and pmax (snowfall_max)
    """

    def __init__(self, config ):
        self.t0 =        config['default_t0']
        self.p0 =        config['default_p0']
        self.pmax =      config['default_pmax']
        self.snow_transition_width = config['snow_transition_width']
        self.melt_v_elev = config['melt_v_elev']   # abl rate per meter elev
        self.melt_v_temp = config['melt_v_temp']   # abl curve shift per celcius
        self.z0,self.z1 = config['z0'],config['z1']
        self.state = 'on'  # is climate turned on/off (for testing)

    def set_new_climate(self, t0=None, p0=None, pmax=None, ela=None ):
        """
        if ela is set, t0 and p0 are ignored 
        unset arguments keep their previous values
        """
        if ela:  t0,p0 = self.ela_2_TP(ela)
        if t0:   self.t0 = t0
        if p0:   self.p0 = p0
        if pmax: self.pmax = pmax

    def accumulation(self,z):
        """
        z is elevation in meters (can be scalar or array)
        uses current value of self.p0
        """
        tmp_ac = .5+(1./pi)* arctan((z-self.p0)/self.snow_transition_width)
        return self.pmax*tmp_ac

    def ablation(self,z):
        """
        z is elevation in meters (can be scalar or array)
        uses current value of self.t0
        """
        ab = self.melt_v_elev*\
             (1.-sin((z-self.z0-(self.t0-20)*self.melt_v_temp)/
                 ((self.z1-self.z0)*2/pi)) )
        # elev of min ablation
        min_ablation_elev= (self.t0-20)*self.melt_v_temp + self.z1 
        if z>min_ablation_elev: ab = 0.
        offset = arctan(self.t0/2.5)/3.+0.5
        return ab + offset

    def ela_2_TP(self,ela):
        """
        returns t0 and p0
        for the given ela 
        """
        # get a reasonable t0
        t0 = (ela-4e3)/200.
        # find ablation at ela with that t0
        ablation_ela = self.get_ablation( t0, ela )
        assert 0.< ablation_ela < self.snowfall_max
        # solve for p0 given the ela and this ablation
        tmp = tan( pi*( ablation_ela/self.pmax - 0.5 ) )
        p0 = ela - self.snow_transition_width * tmp
        return t0,p0 

    def __repr__(self):
        "return string representation of object"
        s  = "state: ".rjust(20) + self.state + '\n'
        s += "t0: ".rjust(20) + '%0.1f C\n'%self.t0 
        s += "p0: ".rjust(20) + '%0.1f m\n'%self.p0 
        s += "pmax: ".rjust(20) +'%0.1f m/yr\n'%self.pmax
        return s

################################################################################
################################################################################
# test code:

climate_config = {
                    'default_t0':20.,      # modern temp at sea level (celcius)
                    'default_p0':4e3,      # elevation of half-max snowfall (m)
                    'default_pmax':2.0,    # max precip (m/yr)
                    'snow_transition_width':300., # (m)
                    'melt_v_elev':30.,
                    'melt_v_temp':200.,
                    'z0':1300., 
                    'z1':4200.,
                  }
mountain_config = {
                    'headwater_width':0.0, 
                    'headwall_length':800., 
                    'headwall_steepness':5e3,
                    'bump':False, 
                    'bump_attenuation':30., 
                    'scoop':500., 
                    'bump_position':30.,
                   }
init_glacier_config =  {
                    #'form':'quadratic',     # parabola
                    #'form':'powersix',     # x^6 shape
                    'form':'combo',     # half parabola, half x^6
                    'max_height':200.,     # m
                    'length':27e3,         # m
                   }
domain_config =    {
                    'default_dt':0.02,   # years in one timestep (yrs)
                    'num_columns':100,   # num ice columns in whole domain
                    'x_max':80e3,        # edge of simulation (m)
                    'H_limit':1e3,       # stop if H exceeds this height
                   }

if 1:    # if 1:
    m = Mountain( mountain_config )
    c = ClimateCalculator( climate_config )
    g = Glacier( m, c, domain_config )
    g.set_shape( init_glacier_config )  
    g.dt = 0.02
    if 0:   g.set_new_climate( t0=15., p0=2e3 )  # for very long glacier
    elif 0: g.set_new_climate( t0=20., p0=4e3 )  # for short glacier
    elif 1: g.set_new_climate( t0=18., p0=3e3 )  # for medium glacier
    else: g.set_new_climate('off')             # turn off accum/ablation

if 0:    # make plots:      # if 1:
    g.plot('F',label='valley floor')
    g.plot('H')
    for i in range(10):
        g.step(2000)
        g.plot('H')
        #g.plot('stress',fignum=4,scale=1e-5)
        #g.plot('u',fignum=5)
    g.set_new_climate( t0=15., p0=2e3 )  # for very long glacier
    for i in range(10):
        g.step(2000)
        g.plot('H')



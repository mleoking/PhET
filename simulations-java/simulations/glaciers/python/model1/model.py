"""
Basic Glacier Model

- requires import of numerical libraries (pylab) from matplotlib:
    http://matplotlib.sourceforge.net
- assumes F is monotonically decreasing
"""

from pylab import *
from lib import derivative

################################################################################
# constants

yts = 365*24*60*60. # year to seconds conversion
rho = 1e3  # density of ice, kg/m^3
gg = 9.8    # gravitational acceleration, m/s^2
AA = 6.8e-24 * yts  # units: 1/(Pa^3.yr)

# from wikipedia:
temp_lapse_rate = 6.5  # degrees C per meter elevation

# from Kessler et al (eq 9):
Uc = 20.      # reference ice velocity, m/yr
tau_c = 1e5   # reference gravitational stress, Pa

################################################################################

class Mountain:

    def __init__(self, num_x=1000, x_max=80e3, 
            headwater_width=5e3, 
            headwall_length=800., headwall_steepness=5e3,
            bump=True, bump_attenuation=30., scoop=500., bump_position=30.,
            ):
        # set x coordinate, meters
        dx = x_max/num_x
        x = arange(0.0,x_max,dx)
        self.x = x
        self.num_x = num_x
        # set F (elevation of valley floor) in meters
        self.F = 4e3 - x/30.         # simple linear slope
        # some extra initial steepness:
        self.F += exp(-(x-headwall_steepness)/headwall_length)  
        if bump:# put a bump near the top
            self.F += (x-scoop)*(0.5-(1./pi)* arctan(x/100-bump_position))/bump_attenuation
        # set W (width of valley) in meters
        self.W = 1e3 + headwater_width*exp( -((x-5e3)/2e3)**2 ) 
        # for glacier plots:
        self.madeplot = False

    def plot(self,fignum=2):
        figure(fignum)
        plot( self.x/1e3, self.F/1e3, label='valley floor' )
        xlabel('x, horizontal distance (km)')
        ylabel('profile (km)')
        self.madeplot = True
        grid(True)

################################################################################

class Climate:

    def __init__(self, F, 
                 init_ref_temp,
                 init_snow_ref_elev,   # elevation of half-max snowfall
                 init_snowfall_max, 
                 default_temp,
                 snow_transition_width, # 
                 default_snow_level,
                 melt_v_elev,
                 melt_v_temp,
                 z0, z1
                 ):
        self.z = F                         # z (meters of elevation) valley floor elevation
        self.snow_transition_width = snow_transition_width
        self.default_temp = default_temp     # 
        self.default_snow_level = default_snow_level     # 
        self.melt_v_elev = melt_v_elev     # ablation rate per meter elevation
        self.melt_v_temp = melt_v_temp     # ablation curve shift per degree celcius
        self.z0,self.z1 = z0,z1
        self.ref_temp = init_ref_temp  # temperature difference from present, celcius
        self.snow_transition_elev = init_snow_ref_elev + self.default_snow_level
        self.set_new_climate( init_ref_temp, init_snow_ref_elev, init_snowfall_max )

    def set_new_climate(self,ref_temp=0.0,snow_ref_elev=0.0,snowfall_max=None,ela=None):
        """
        with no arguments, goes to default climate.
        if ela is set, ref_temp and snow_ref_elev are ignored (ref_temp is set
            to accomodate ela and snow_ref_elev is unchanged)
        """
        if snowfall_max is not None: 
            self.snowfall_max = snowfall_max
        if ela is not None: 
            self.ela_2_temp_snow(ela)
        else: 
            self.ref_temp = ref_temp  # temperature difference from present, celcius
            self.snow_transition_elev = snow_ref_elev + self.default_snow_level
        # for temperature gauge  (temp vs elevation in celcius):
        self.temp_v_z = self.default_temp+ref_temp - temp_lapse_rate*self.z/1e3 
        # set mass balance:
        self._set_mass_balance()

    def get_accumulation(self,snow_transition_elev,z):
        return self.snowfall_max* (.5+(1./pi)*
                arctan((z-snow_transition_elev)/self.snow_transition_width))

    def get_ablation(self,ref_temp,z):
        ab = self.melt_v_elev*\
             (1.-sin((z-self.z0-ref_temp*self.melt_v_temp)/((self.z1-self.z0)*2/pi)) )
        min_ablation_elev= ref_temp*self.melt_v_temp + self.z1 # elev of min ablation
        if iterable(z):
            min_abl_index = argmin(abs( min_ablation_elev - self.z ))
            ab[:min_abl_index] *= 0.
        elif z>min_ablation_elev:
            ab = 0.
        return ab + arctan(ref_temp/2.5)/3.+0.5


    def ela_2_temp_snow(self,ela):
        """
        sets self.ref_temp and self.snow_transition_elev
        for the given ela 
        """
        # get a reasonable ref_temp
        snow_level = (self.snow_transition_elev-self.default_snow_level)/1e3
        slope = 0.2+ (0.55-0.2)/(3.5+4.) * (snow_level+4)
        intercept = 3.6+ (9.8-3.6)/(3.5+4.) * (snow_level+4)
        ref_temp = (ela-4e3)/200.
        # find ablation at ela with that ref_temp
        ablation_ela = self.get_ablation( ref_temp, ela )
        assert 0.< ablation_ela < self.snowfall_max
        # solve for snow_transition_elev given the ela and this ablation
        tmp = tan( pi*( ablation_ela/self.snowfall_max - 0.5 ) )
        snow_trans = ela - self.snow_transition_width * tmp
        # set variables
        self.ref_temp = ref_temp
        self.snow_transition_elev = snow_trans

    def _set_mass_balance(self):
        """
        sets (self.) accumulation, ablation, massbalance, ela_index, ela
        only used internally
        """
        z = self.z
        # find mass balance at all elevations
        self.ablation = self.get_ablation( self.ref_temp, self.z )
        self.accumulation = self.get_accumulation( self.snow_transition_elev, self.z )
        self.mass_balance = self.accumulation - self.ablation
        # find ELA (elevation of zero mass balance)
        self.ela_index = argmin( abs(self.mass_balance) )
        self.ela = self.z[self.ela_index]   

    def plot(self,fignum=7):
        figure(fignum)
        yy = self.z/1e3
        plot( self.accumulation, yy, label='accumulation' )
        plot( -self.ablation, yy, label='- ablation' )
        plot( self.mass_balance, yy, label='mass balance' )
        ylabel('elevation (km)')
        xlabel('mass balance (m/yr)')
        text( average(xlim()), self.ela/1e3, '-- ELA %0.1fkm --'%(c.ela/1e3),
              verticalalignment='center',backgroundcolor='white')
        grid(True)
        xlim(ymax=1.5*self.snowfall_max)
        legend(loc='upper left')

    def report_climate(self):
        print
        print "ref temp:".rjust(20), self.ref_temp, 'C'
        print "snow level:".rjust(20), self.snow_transition_elev, 'm'
        print
        print "ELA:".rjust(20), '%0.2f'%(self.ela/1e3), 'km'
        print "snow max:".rjust(20), self.snowfall_max, 'm/yr'
        print

################################################################################

class Glacier:

    def __init__(self, m ):
        """
        m is a Mountain object
        """
        self.m = m  # mountain object (has x-coordinates, valley floor height and width)
        # calculate dx in case x is not uniformly spaced:
        self.dx = m.x.copy()
        self.dx[1:] -= m.x[:-1]
        self.dx[0]   = self.dx[1] 
        #
        self.H = zeros( m.x.shape, 'd' ) # initial glacier height set to zero
        #self.set_ice_velocities()
        self.plot = self.plot_profile

    def set_volume_flux( self, B ):
        """
        find volume flux function, Q(x)
        B (mass balance) must be defined at self.x points
            mass balance is a function of elevation; B is the same, but as a function of x
        volume_flux = Q = integral( W*B*dx )
        also sets self.terminux_index
        """
        # do integral:
        Q = cumsum( self.m.W * B * self.dx )  # cumulative sum
        self.Q = clip( Q, 0.0, 9e9 )
        # terminus at first nonzero value of Q:
        self.terminus_index = max( self.Q.nonzero()[0] )

    def set_eq_height_const_tau( self, climate ):
        """
        find steady state (equilibrium) glacier height, H(x) from climate object by:
            - find Q from the mass balance
            - set shear stress tau=1e5
            - compute resulting ice velocity and height
        """
        self.set_volume_flux(climate.mass_balance)   # defines self.Q
        beta = ( 0.4*AA*tau_c**3 )/ Uc  # scalar
        self.H = 1/(2*beta)*( -1.+ sqrt(1.+ ( 4.*beta*self.Q )/(self.m.W*Uc) ) )
        self.set_ice_velocities() 

    def set_eq_height( self, climate ):
        """
        find steady state (equilibrium) glacier height, H(x) from climate object by:
            - assume some H has already been calculated
            - find Q from the mass balance
            - find ice velocites from present height
            - compute resulting ice height
        """
        self.set_volume_flux(climate.mass_balance)   # defines self.Q
        self.set_ice_velocities()                    # defines self.u_ave
        self.H = array(self.Q) 
        self.H[:self.terminus_index] *= 1.0 / ( self.m.W[:self.terminus_index]
                                            * self.u_ave[:self.terminus_index]  )

    def set_eq_height_from_slope( self ):
        """
        constrain to constant tau, using   tau = 1e5 = rho.g.H.dZ/dx
        assumes some H has already been calculated (to get tau)
        (also calls set_ice_velocities with new height)
        """
        dzdx = abs( derivative( self.m.x, (self.m.F+self.H) ))
        self.H = 1e5/(rho*gg*dzdx)
        self.H[self.terminus_index+1:] *= 0.0
        self.set_ice_velocities() 

    def incr_height( self, incr=0.2 ):
        tau = self.shear_stress()
        dtau = incr*(tau-1e5)
        self.set_ice_velocities( 1e5 + dtau )
        self.H = array(self.Q) 
        self.H[:self.terminus_index+1] *= 1.0 / ( self.m.W[:self.terminus_index+1]
                                            * self.u_ave[:self.terminus_index+1]  )

    def shear_stress( self ):
        """
        basal shear stress:  tau = rho.g.H. d(H+F)/dx       units of Pascals
        returns correct tau
        """
        tau = rho*gg*self.H* abs(derivative( self.m.x, self.m.F+self.H ))
        return tau 

    def set_ice_velocities( self, tau=None ):
        """
        sets (self.) tau, u_slide, u_deform_ave, u_ave
        depends on H and F only
        """
        if tau is None:
            # set basal shear stress:  tau = rho.g.H. d(H+F)/dx       units of Pascals
            self.tau = rho*gg*self.H* abs(derivative( self.m.x, self.m.F+self.H ))
            tau1 = clip( self.tau, 1., 9e99 )  # for safe division
            factor = where( self.tau==0, 0.0, exp( 1.-tau_c/tau1 ) )
        elif tau is 1:
            self.tau = ones(self.H.shape,'d') * 1e5
            factor = 1.0
        else:
            self.tau = array(tau)
            tau1 = clip( self.tau, 1., 9e99 )  # for safe division
            factor = where( self.tau==0, 0.0, exp( 1.-tau_c/tau1 ) )
        # sliding velocity:
        self.u_slide = ones( self.H.shape, 'd' ) * Uc * factor
        # variable (verically-averaged) deformation velocity:
        u0 = 0.4*AA * self.H * (self.tau**3)
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

    def plot_profile(self,fignum=2,addF=False,label=None):
        figure(fignum)
        if self.m.madeplot: 
            yy = (self.m.F+self.H)/1e3
            y_label = 'glacier/valley profile (km)'
            ylim(3.5,4.7)
        else:               
            yy = self.H
            y_label = 'glacier profile (m)'
        plot( self.m.x/1e3, yy, label=label )
        xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        if self.m.madeplot: ylim(self.m.F[int(self.terminus_index*1.3)]/1e3,4.7)
        ylabel(y_label)
        xlabel('x, horizontal distance (km)')
        if label: legend()
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
        plot( self.m.x/1e3, self.u_slide, label='sliding' )
        plot( self.m.x/1e3, self.u_deform_ave, label='deformation' )
        plot( self.m.x/1e3, self.u_ave, label='total' )
        xlim(0,1.2*self.m.x[self.terminus_index]/1e3)
        ylabel('velocity (m/yr)')
        xlabel('x, horizontal distance (km)')
        legend()
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

if 1:    # if 1:
    # instantiate objects
    m = Mountain( headwater_width=0. )
    c = Climate( m.F,
                 init_ref_temp=0.0,
                 init_snow_ref_elev=0.0,   # elevation of half-max snowfall
                 init_snowfall_max=2.0, 
                 default_temp=20., # modern temp at sea level (celcius)
                 snow_transition_width=300., # 
                 default_snow_level=4e3,  # elevation of accum transition
                 melt_v_elev=30.,
                 melt_v_temp=200.,
                 z0=1300., z1=4200.,
                 )
    g = Glacier( m )

    # set climate
    #c.set_new_climate( 0.,3e3 )
    g.set_eq_height_const_tau( c )

    # make plots
    if 0: c.plot()
    if 0: m.plot()
    if 0: g.plot(label='ELA %0.1fkm'%(c.ela/1e3))

elif 0:   # for Bob
    # instantiate objects
    m = Mountain( headwater_width=0., bump=True, bump_attenuation=30., 
                  scoop=500., bump_position=30. )
    c = Climate( m.F,
                 init_ref_temp=0.0,
                 init_snow_ref_elev=0.0,   # elevation of half-max snowfall
                 init_snowfall_max=2.0, 
                 default_temp=20., # modern temp at sea level (celcius)
                 snow_transition_width=300., # 
                 default_snow_level=4e3,  # elevation of accum transition
                 melt_v_elev=30.,
                 melt_v_temp=200. )
    g = Glacier( m )
    # set to modern climate
    c.set_new_climate( 0., 0. )
    g.set_eq_height_const_tau( c )
    c.plot()
    savefig('figs/massbalance_1.png')
    clf()
    m.plot()
    g.plot(label='ELA %0.1fkm'%(c.ela/1e3))
    savefig('figs/H_1.png')
    g.plot_stress()
    savefig('figs/tau_1.png')
    clf()
    g.plot_velocities()
    savefig('figs/vel_1.png')
    clf()
    # set to colder&wetter climate
    c.set_new_climate( -5., -1e3 )
    g.set_eq_height_const_tau( c )
    c.plot()
    savefig('figs/massbalance_2.png')
    g.plot(label='ELA %0.1fkm'%(c.ela/1e3))
    savefig('figs/H_2.png')
    g.plot_stress()
    savefig('figs/tau_2.png')
    g.plot_velocities()
    savefig('figs/vel_2.png')

if 0:   # if 1:
    # look at temperature-ELA relationship
    figure(5)
    clf()
    temp_range = arange( -15.0, 10.0, 1.0 )
    snow_range = arange( -4e3, 3.6e3, 1.5e3 )
    for snow_ref_elev in snow_range:
        ela_v_temp = []
        for temperature in temp_range:
            c.set_new_climate( temperature, snow_ref_elev )
            ela_v_temp.append( c.ela )
        if 1:    # plot it
            label = 'snow level %0.1fkm'%(snow_ref_elev/1e3)
            plot( temp_range, array(ela_v_temp)/1e3, label=label )
        grid()
        xlabel('reference temperature')
        ylabel('ELA (km)')
        legend(loc='lower right')

if 0:   # if 1:
    # look at snowfall-ELA relationship
    figure(6)
    clf()
    temp_range = arange( -10.0, 5.1, 5.0 )
    snow_range = arange( -4e3, 3.1e3, 0.2e3 )
    for temperature in temp_range:
        ela_v_snow = []
        for snow_ref_elev in snow_range:
            c.set_new_climate( temperature, snow_ref_elev )
            ela_v_snow.append( c.ela )
        if 1:    # plot it
            label = 'ref temp %0.1f'%(temperature)
            plot( snow_range/1e3, array(ela_v_snow)/1e3, label=label )
        grid()
        xlabel('snow level (km)')
        ylabel('ELA (km)')
        legend(loc='upper left')




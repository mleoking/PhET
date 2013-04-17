"""
this version is for the Hollywood Glacier only
"""

from pylab import *

###############################################################################
# constants

# from wikipedia:
temp_lapse_rate = 6.5  # degrees C per km elevation

###############################################################################

class Climate:
    """
    This object will calculate the local mass balance given
        - temp (temperature), 
        - snow (snowfall precipitation), and 
    """

    def __init__(self, m, config ):
        self.temp =   config['default_temp'] # 13 -> 20
        self.snow =   config['default_snow'] # 0 -> 1
        self.snow_max = config['snow_max']
        self.snow_min_elev = config['snow_min_elev']
        self.snow_max_elev = config['snow_max_elev']
        self.snow_transition_width = config['snow_transition_width']
        self.melt_v_elev = config['melt_v_elev']   # abl rate per meter elev
        self.melt_v_temp = config['melt_v_temp']   # abl curve shift per celcius
        self.melt_z0,self.melt_z1 = config['melt_z0'],config['melt_z1']
        self.local_only = False   # will be set to True if this is for 
                                  # the FE method
        self.state = 'on'  # is climate turned on/off (for FE testing)
        #  m is a mountain-domain object
        self.m = m 
        self.z = m.F # valley floor evelvations
        # 
        self.set_new_climate()

    def set_new_climate(self, temp=None, snow=None ):
        """
        """
        if temp is not None:   self.temp = temp
        if snow is not None:   self.snow = snow
        # for temperature gauge  (temp vs elevation in celcius):
        self.temp_v_z = self.temp - temp_lapse_rate*self.z/1e3 
        # set mass balance (ela):
        self._set_mass_balance()

    def _set_mass_balance(self):
        """
        sets (self.) accumulation, ablation, massbalance, ela_index, ela
        """
        # find mass balance at all elevations
        self.ablation = self.get_ablation( self.z )
        self.accumulation = self.get_accumulation( self.z )
        self.mass_balance = self.accumulation - self.ablation
        # find ELA (elevation of zero mass balance)
        self.ela_index = argmin( abs(self.mass_balance) )
        self.ela = self.z[self.ela_index]   

    def get_accumulation(self,z):
        """
        z is elevation in meters (can be scalar or array)
        uses current value of self.snow
        """
        #snow = sqrt(self.snow)
        snow = self.snow
        p0 = ( self.snow_max_elev - 
                (self.snow_max_elev-self.snow_min_elev)*(snow))
        pmax = self.snow_max*snow
        tmp_ac = .5+(1./pi)* arctan((z-p0)/self.snow_transition_width)
        return pmax*tmp_ac

    def get_ablation(self,z):
        """
        z is elevation in meters (can be scalar or array)
        uses current value of self.temp
        """
        ab = self.melt_v_elev*\
             (1.-sin((z-self.melt_z0-(self.temp-20)*self.melt_v_temp)/
                 ((self.melt_z1-self.melt_z0)*2/pi)) )
        # elev of min ablation
        min_ablation_elev= (self.temp-20)*self.melt_v_temp + self.melt_z1 
        if iterable(z):
            min_abl_index = argmin(abs( min_ablation_elev - self.z ))
            ab[:min_abl_index] *= 0.
        elif z>min_ablation_elev:
            ab = 0.
        #offset = 2.*(arctan((self.temp-20)/2.5)/3.+0.5)
        offset = 1.5*(arctan((self.temp-20)/2.5)/3.+0.5)
        return ab + offset

    def __repr__(self):
        "return string representation of object"
        if self.state=='off': return "off".rjust(20) + '\n'
        s  = "temp: ".rjust(20) + '%0.1f C\n'%self.temp 
        s += "snow: ".rjust(20) + '%0.1f m\n'%self.snow
        if not self.local_only:  # ela only calculated for non-FE models
            s += "ela: ".rjust(20) + '%0.3f km\n'%(self.ela/1e3)
        return s

    def plot(self,fignum=7):
        figure(fignum)
        yy = self.z/1e3
        plot( self.accumulation, yy, label='accumulation' )
        plot( -self.ablation, yy, label='- ablation' )
        plot( self.mass_balance, yy, label='mass balance' )
        ylabel('elevation (km)')
        xlabel('mass balance (m/yr)')
        text( average(xlim()), self.ela/1e3, '-- ELA %0.1fkm --'%(self.ela/1e3),
              verticalalignment='center',backgroundcolor='white')
        grid(True)
        #xlim(ymax=1.5*self.pmax)
        legend(loc='upper left')

###############################################################################
# test code:

if 0:    # if 1:
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

    climate_config = {
                        'default_temp':20., # modern temp at sea level (celcius)
                        'default_p0':4e3,   # elevation of half-max snowfall (m)
                        'default_pmax':2.0, # max precip (m/yr)
                        'snow_transition_width':300., # (m)
                        'melt_v_elev':30.,
                        'melt_v_temp':200.,
                        'z0':1300., 
                        'melt_z1':4200.,
                      }
    from Mountain import Mountain
    m = Mountain( mountain_config, domain_config )
    c = Climate( m, climate_config )



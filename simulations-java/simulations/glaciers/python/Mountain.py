"""
        Mountain object defines the domains, so it's used to instantiate the
          other two

"""

from pylab import *

###############################################################################

class Mountain:
    """
    Mountain object describes the valley geometry and basic domain of
    computation.
    """

    def __init__(self,mountain_config,domain_config):
        self.config = mountain_config
        self.num_x = domain_config['num_x']
        self.x_max = domain_config['x_max']
        # set x coordinate, meters
        dx = self.x_max/self.num_x
        x = arange(0.0,self.x_max,dx)
        self.x = x
        # set F and W (valley geometry)
        self.F = self.get_valley_elevation(self.x)
        self.W = self.get_valley_width(self.x)
        # for glacier plots:
        self.madeplot = False

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

    def plot(self,fignum=2):
        figure(fignum)
        #plot( self.x/1e3, self.F/1e3, label='valley floor' )
        plot( self.x/1e3, self.F, label='valley floor' )
        xlabel('x, horizontal distance (km)')
        ylabel('profile (m)')
        self.madeplot = True
        grid(True)

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
    m = Mountain( mountain_config, domain_config )


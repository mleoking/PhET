#! /usr/bin/env python
"""
Run script for hollywood glacier model.
"""

from Mountain import Mountain
from Climate import Climate
from HollywoodGlacier import HollywoodGlacier

import os,cPickle,time
from pylab import *

################################################################################
# config

climate_config = {
                'default_temp':20.,  # modern temp at sea level (celcius)
                'default_snow':0.5,  # 
                'snow_max':2.0, # max precip (m/yr)
                'snow_min_elev':2500., # (m)
                'snow_max_elev':5000., # (m)
                'snow_transition_width':300., # (m)
                'melt_v_elev':30., 'melt_v_temp':200.,
                'melt_z0':1300.,   'melt_z1':5200.,
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
domain_config =    {
                    'num_x':1000,   # num ice columns in whole domain
                    'x_max':80e3,           # edge of simulation (m)
                    # for FE only:
                    'default_dt':0.001,  # years in one timestep (yrs) (FE)
                    'H_limit':1e3,      # stop if H exceeds this height (FE)
                   }
init_glacier_config =  {
                    'max_height':300.,     # m
                    'length':52e3,         # m
                   }

################################################################################
# run

if __name__=='__main__':
    m = Mountain( mountain_config, domain_config )
    c = Climate( m, climate_config )
    g = HollywoodGlacier( m, c.ela )
    self = g

    ###################################################
    # testing

    # check glacier shape
    if 1:    # if 1:
        m.plot()
        c.set_new_climate(temp=19,snow=0.8)
        g.set_new_climate(c.ela)
        g.plot()
        c.set_new_climate(temp=17,snow=0.6)
        g.set_new_climate(c.ela)
        g.plot()

        # check time-dependence
        if 1:    # if 1:
            figure(33)
            times,qelas = g.time_dependence(dt=1)
            terminus = [ g.get_x_terminus(g.get_ela_x(q)) for q in qelas ]
            plot( times, terminus )


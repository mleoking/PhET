
import os.path as path

# Post IOM base url
BASE_URL = 'http://localhost/PhET-postiom/website/simulations/sims.php?sim='
#BASE_URL = 'http://localhost/PhET/website/simulations/sims.php?sim='

SIM_ENCODED_NAMES = [
    'Alpha_Decay',
    'Arithmetic',
    'Balloons_and_Buoyancy',
    'Balloons_and_Static_Electricity',
    'Band_Structure',
    'Battery_Voltage',
    'BatteryResistor_Circuit',
    'Blackbody_Spectrum',
    'Charges_and_Fields',
    'Circuit_Construction_Kit_ACDC',
    'Circuit_Construction_Kit_DC_Only',
    'Color_Vision',
    'Conductivity',
    'Curve_Fitting',
    'DavissonGermer_Electron_Diffraction',
    'Double_Wells_and_Covalent_Bonds',
    'Eating_and_Exercise',
    'Electric_Field_Hockey',
    'Electric_Field_of_Dreams',
    'Energy_Skate_Park',
    'Equation_Grapher',
    'Estimation',
    'Faradays_Electromagnetic_Lab',
    'Faradays_Law',
    'Forces_in_1_Dimension',
    'Fourier_Making_Waves',
    'Friction',
    'Gas_Properties',
    'Generator',
    'Geometric_Optics',
    'Glaciers',
    'The_Greenhouse_Effect',
    'John_Travoltage',
    'Ladybug_Revolution',
    'Lasers',
    'Lunar_Lander',
    'Magnet_and_Compass',
    'Magnets_and_Electromagnets',
    'Masses_and_Springs',
    'Maze_Game',
    'Microwaves',
    'Models_of_the_Hydrogen_Atom',
    'Molecular_Motors',
    'Motion_in_2D',
    'The_Moving_Man',
    'My_Solar_System',
    'Neon_Lights_and_Other_Discharge_Lamps',
    'Nuclear_Fission',
    'Ohms_Law',
    'Optical_Quantum_Control',
    'Optical_Tweezers_and_Applications',
    'Pendulum_Lab',
    'pH_Scale',
    'Photoelectric_Effect',
    'Plinko_Probability',
    'Projectile_Motion',
    'Quantum_Bound_States',
    'Quantum_Tunneling_and_Wave_Packets',
    'Quantum_Wave_Interference',
    'Radio_Waves_and_Electromagnetic_Fields',
    'The_Ramp',
    'Reactions_and_Rates',
    'Resistance_in_a_Wire',
    'Reversible_Reactions',
    'Rutherford_Scattering',
    'Salts_and_Solubility',
    'SelfDriven_Particle_Model',
    'Semiconductors',
    'Signal_Circuit',
    'Simplified_MRI',
    'Sound',
    'States_of_Matter',
    'SternGerlach_Experiment',
    'Stretching_DNA',
    'Torque',
    'Vector_Addition',
    'Wave_Interference',
    'Wave_on_a_String'
    ]

def local_sim_filename(dir, sim):
    
    return path.join(dir, 'sim_'+sim+'.html')

def get_url(url):
    import re

    re_addr = re.compile('([a-z]+)://([-a-zA-Z0-9.]+)(.*)')
    match = re_addr.match(url)
    if not match:
        raise RuntimeError('Bad url:', url)
    host = match.group(2)
    file = match.group(3)
    if file == '':
        file = '/'
    
    import httplib
    conn = httplib.HTTPConnection(host)
    conn.request('GET', file)
    r1 = conn.getresponse()
    if r1.status != 200:
        raise RuntimeError('Error %d %s getting url %s' % \
                               (r1.status, r1.reason, url))
    data = r1.read()
    conn.close()

    return data

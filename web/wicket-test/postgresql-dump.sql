--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

ALTER TABLE ONLY public.category_mapping DROP CONSTRAINT fkfce89e8daab0afa5;
ALTER TABLE ONLY public.category_mapping DROP CONSTRAINT fkfce89e8d14e28f05;
ALTER TABLE ONLY public.keyword_mapping DROP CONSTRAINT fkfbcce478ba10a9af;
ALTER TABLE ONLY public.keyword_mapping DROP CONSTRAINT fkfbcce478aab0afa5;
ALTER TABLE ONLY public.simulation DROP CONSTRAINT fkb3012607b7b702c7;
ALTER TABLE ONLY public.user_translation_mapping DROP CONSTRAINT fk50f116ccf968c22f;
ALTER TABLE ONLY public.user_translation_mapping DROP CONSTRAINT fk50f116cc5062452c;
ALTER TABLE ONLY public.topics_mapping DROP CONSTRAINT fk47534b3ba10a9af;
ALTER TABLE ONLY public.topics_mapping DROP CONSTRAINT fk47534b3aab0afa5;
ALTER TABLE ONLY public.localized_simulation DROP CONSTRAINT fk311e4d4b6b081b59;
ALTER TABLE ONLY public.category DROP CONSTRAINT fk302bcfe34a093d9;
ALTER TABLE ONLY public.translated_string DROP CONSTRAINT fk1854a79ac047ff77;
ALTER TABLE ONLY public.user_translation_mapping DROP CONSTRAINT user_translation_mapping_pkey;
ALTER TABLE ONLY public.translation DROP CONSTRAINT translation_pkey;
ALTER TABLE ONLY public.translated_string DROP CONSTRAINT translated_string_pkey;
ALTER TABLE ONLY public.topics_mapping DROP CONSTRAINT topics_mapping_pkey;
ALTER TABLE ONLY public.simulation DROP CONSTRAINT simulation_pkey;
ALTER TABLE ONLY public.project DROP CONSTRAINT project_pkey;
ALTER TABLE ONLY public.phet_user DROP CONSTRAINT phet_user_pkey;
ALTER TABLE ONLY public.localized_simulation DROP CONSTRAINT localized_simulation_pkey;
ALTER TABLE ONLY public.keyword DROP CONSTRAINT keyword_pkey;
ALTER TABLE ONLY public.keyword_mapping DROP CONSTRAINT keyword_mapping_pkey;
ALTER TABLE ONLY public.category DROP CONSTRAINT category_pkey;
ALTER TABLE ONLY public.category_mapping DROP CONSTRAINT category_mapping_pkey;
DROP TABLE public.user_translation_mapping;
DROP TABLE public.translation;
DROP TABLE public.translated_string;
DROP TABLE public.topics_mapping;
DROP TABLE public.simulation;
DROP TABLE public.project;
DROP TABLE public.phet_user;
DROP TABLE public.localized_simulation;
DROP TABLE public.keyword_mapping;
DROP TABLE public.keyword;
DROP TABLE public.category_mapping;
DROP TABLE public.category;
DROP PROCEDURAL LANGUAGE plpgsql;
DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: category; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE category (
    id integer NOT NULL,
    name character varying(255),
    auto boolean,
    root boolean,
    parent_id integer,
    subcategory_idx integer
);


ALTER TABLE public.category OWNER TO phet;

--
-- Name: category_mapping; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE category_mapping (
    simulation_id integer NOT NULL,
    category_id integer NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE public.category_mapping OWNER TO phet;

--
-- Name: keyword; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE keyword (
    id integer NOT NULL,
    key character varying(255)
);


ALTER TABLE public.keyword OWNER TO phet;

--
-- Name: keyword_mapping; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE keyword_mapping (
    simulation_id integer NOT NULL,
    keyword_id integer NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE public.keyword_mapping OWNER TO phet;

--
-- Name: localized_simulation; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE localized_simulation (
    id integer NOT NULL,
    locale character varying(255),
    title character varying(1024),
    description character varying(4048),
    simulation integer NOT NULL
);


ALTER TABLE public.localized_simulation OWNER TO phet;

--
-- Name: phet_user; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE phet_user (
    id bigint NOT NULL,
    email character varying(255),
    password character varying(255),
    teammember boolean
);


ALTER TABLE public.phet_user OWNER TO phet;

--
-- Name: project; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE project (
    id integer NOT NULL,
    name character varying(255),
    versionmajor integer,
    versionminor integer,
    versiondev integer,
    versionrevision integer,
    versiontimestamp bigint
);


ALTER TABLE public.project OWNER TO phet;

--
-- Name: simulation; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE simulation (
    id integer NOT NULL,
    name character varying(255),
    type integer,
    project integer,
    designteam character varying(255),
    libraries character varying(255),
    thanksto character varying(255)
);


ALTER TABLE public.simulation OWNER TO phet;

--
-- Name: topics_mapping; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE topics_mapping (
    simulation_id integer NOT NULL,
    keyword_id integer NOT NULL,
    idx integer NOT NULL
);


ALTER TABLE public.topics_mapping OWNER TO phet;

--
-- Name: translated_string; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE translated_string (
    id integer NOT NULL,
    key character varying(255),
    value character varying(4048),
    createdat timestamp without time zone,
    updatedat timestamp without time zone,
    translation integer NOT NULL
);


ALTER TABLE public.translated_string OWNER TO phet;

--
-- Name: translation; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE translation (
    id integer NOT NULL,
    locale character varying(255),
    visible boolean
);


ALTER TABLE public.translation OWNER TO phet;

--
-- Name: user_translation_mapping; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE user_translation_mapping (
    translation_id integer NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.user_translation_mapping OWNER TO phet;

--
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY category (id, name, auto, root, parent_id, subcategory_idx) FROM stdin;
1	root	t	t	\N	\N
2	featured	f	f	1	0
3	new	f	f	1	1
4	physics	t	f	1	2
12	biology	f	f	1	3
13	chemistry	f	f	1	4
14	earth-science	f	f	1	5
15	math	t	f	1	6
18	cutting-edge-research	f	f	1	7
5	motion	f	f	4	0
6	sound-and-waves	f	f	4	1
7	work-energy-and-power	f	f	4	2
8	heat-and-thermodynamics	f	f	4	3
9	quantum-phenomena	f	f	4	4
10	light-and-radiation	f	f	4	5
11	electricity-magnets-and-circuits	f	f	4	6
16	tools	f	f	15	0
17	applications	f	f	15	1
\.


--
-- Data for Name: category_mapping; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY category_mapping (simulation_id, category_id, idx) FROM stdin;
8	2	0
36	2	1
22	2	2
32	2	3
51	2	4
61	2	5
72	2	6
28	2	7
19	2	8
40	2	9
56	2	10
43	2	11
12	3	0
38	3	1
22	3	2
70	3	3
60	3	4
32	5	0
67	5	1
46	5	2
15	5	3
70	5	4
45	5	5
72	5	6
68	5	7
64	5	8
66	5	9
20	5	10
29	5	11
31	5	12
56	6	0
37	6	1
43	6	2
21	6	3
52	6	4
76	6	5
30	6	6
17	7	0
19	7	1
67	7	2
68	7	3
15	7	4
25	7	5
26	7	6
58	8	0
64	8	1
25	8	2
26	8	3
44	8	4
23	8	5
30	8	6
27	8	7
39	9	0
40	9	1
6	9	2
42	9	3
28	9	4
11	9	5
21	9	6
33	9	7
24	9	8
74	9	9
10	9	10
49	9	11
47	9	12
41	9	13
58	9	14
5	9	15
4	9	16
36	10	0
9	10	1
35	10	2
65	10	3
37	10	4
58	10	5
21	10	6
23	10	7
28	10	8
11	10	9
39	10	10
43	10	11
56	10	12
30	10	13
42	10	14
33	10	15
24	10	16
18	11	0
16	11	1
17	11	2
1	11	3
8	11	4
7	11	5
14	11	6
59	11	7
19	11	8
63	11	9
55	11	10
43	11	11
69	11	12
3	11	13
2	11	14
13	11	15
73	11	16
10	11	17
49	11	18
50	11	19
12	12	0
44	12	1
33	12	2
36	12	3
60	12	4
71	12	5
37	12	6
9	12	7
52	12	8
1	12	9
51	12	10
38	12	11
58	12	12
35	12	13
1	13	0
38	13	1
51	13	2
44	13	3
11	13	4
76	13	5
43	13	6
58	13	7
24	13	8
25	13	9
47	13	10
26	13	11
23	13	12
39	13	13
30	13	14
27	13	15
22	14	0
26	14	1
25	14	2
1	14	3
52	14	4
23	14	5
76	14	6
58	14	7
38	14	8
56	14	9
68	14	10
61	16	0
75	16	1
73	16	2
21	16	3
58	16	4
60	16	5
57	16	6
71	16	7
69	16	8
62	16	9
32	17	0
72	17	1
70	17	2
68	17	3
31	17	4
29	17	5
29	17	6
67	17	7
46	17	8
25	17	9
20	17	10
38	17	11
76	17	12
45	17	13
37	18	0
35	18	1
36	18	2
34	18	3
28	18	4
48	18	5
\.


--
-- Data for Name: keyword; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY keyword (id, key) FROM stdin;
1	keyword.electricity
2	keyword.circuits
3	keyword.current
4	keyword.voltage
5	keyword.resistance
6	keyword.voltmeter
7	keyword.ammeter
8	keyword.lightBulbs
9	keyword.battery
10	keyword.resistor
11	keyword.ohmsLaw
12	keyword.kirchoffsLaw
13	keyword.seriesCircuit
14	keyword.parallelCircuit
15	keyword.rcCircuit
16	keyword.opticalTweezers
17	keyword.laser
19	keyword.optical
20	keyword.dna
21	keyword.molecularMotors
22	keyword.climateChange
23	keyword.glaciers
24	keyword.motion
25	keyword.velocity
26	keyword.acceleration
27	keyword.position
28	keyword.graphing
29	keyword.kinematics
30	keyword.vectors
31	keyword.arithmetic
32	keyword.multiplication
33	keyword.division
34	keyword.factoring
35	keyword.calculation
36	keyword.math
37	keyword.gas
38	keyword.thermodynamics
39	keyword.pressure
40	keyword.temperature
41	keyword.volume
42	keyword.buoyancy
43	keyword.chemistry
44	keyword.atmosphere
45	keyword.boylesLaw
46	keyword.staticElectricity
47	keyword.charges
48	keyword.electricForce
49	keyword.coulombsLaw
50	keyword.polarization
51	keyword.electrostatics
52	keyword.quantumMechanics
53	keyword.energyLevels
54	keyword.probabilityDensity
55	keyword.waveFunction
56	keyword.conductivity
57	keyword.electrons
58	keyword.potentialWells
59	keyword.energyBand
60	keyword.energyGap
61	keyword.radiation
62	keyword.light
63	keyword.spectrum
64	keyword.blackbody
65	keyword.thermalEnergy
66	keyword.electricField
67	keyword.electricPotential
68	keyword.equipotential
69	keyword.pointCharge
70	keyword.dipole
71	keyword.acCircuits
72	keyword.capacitance
73	keyword.capacitor
74	keyword.induction
75	keyword.inductor
76	keyword.alternatingCurrent
77	keyword.rlcCircuit
78	keyword.lcCircuit
79	keyword.photons
80	keyword.whiteLight
81	keyword.monochromatic
82	keyword.insulators
83	keyword.conductors
84	keyword.semiconductors
85	keyword.photoconductors
86	keyword.solidState
87	keyword.polynomials
88	keyword.statistics
89	keyword.errorAnalysis
90	keyword.curves
91	keyword.correlation
92	keyword.chiSquare
93	keyword.chiSquared
94	keyword.chi
95	keyword.electronDiffraction
96	keyword.atomicStructure
97	keyword.interference
98	keyword.waveParticleDuality
99	keyword.tunneling
100	keyword.covalentBonds
101	keyword.bonds
102	keyword.superposition
103	keyword.eating
104	keyword.exercise
105	keyword.diet
106	keyword.calories
107	keyword.weight
108	keyword.energy
109	keyword.conservationOfEnergy
110	keyword.kineticEnergy
111	keyword.potentialEnergy
112	keyword.friction
113	keyword.work
114	keyword.gravity
115	keyword.gravitationalForce
116	keyword.algebra
117	keyword.estimation
118	keyword.area
119	keyword.length
120	keyword.orderOfMagnitude
121	keyword.magnetism
122	keyword.magneticField
123	keyword.faradaysLaw
124	keyword.power
125	keyword.electromagnet
126	keyword.magnets
127	keyword.transformer
128	keyword.compass
129	keyword.generator
130	keyword.turbine
131	keyword.force
132	keyword.newtonsLaws
133	keyword.dynamics
134	keyword.1d
135	keyword.harmonicMotion
136	keyword.wavelength
137	keyword.amplitude
138	keyword.frequency
139	keyword.fourierSeries
140	keyword.fourierAnalysis
141	keyword.period
142	keyword.uncertaintyPrinciple
143	keyword.wavePackets
144	keyword.heat
145	keyword.pvWork
146	keyword.idealGasLaw
147	keyword.boltzmannDistribution
148	keyword.refraction
149	keyword.lens
150	keyword.vision
151	keyword.images
152	keyword.indexOfRefraction
153	keyword.principleRays
154	keyword.focalLength
155	keyword.greenhouseEffect
156	keyword.climate
157	keyword.infrared
158	keyword.thermalEquilibrium
159	keyword.rotation
160	keyword.circularRevolution
161	keyword.angularPosition
162	keyword.angularVelocity
163	keyword.angularAcceleration
164	keyword.stimulatedEmission
165	keyword.spontaneousEmission
166	keyword.absorption
167	keyword.emission
168	keyword.excitation
169	keyword.cathodeRayTube
170	keyword.moon
171	keyword.mass
172	keyword.springs
173	keyword.hookesLaw
174	keyword.springConstant
175	keyword.microwaves
176	keyword.fields
177	keyword.hydrogenAtom
178	keyword.bohrModel
180	keyword.deBroglieWavelength
181	keyword.schrodingerModel
182	keyword.schrodingerEquation
183	keyword.atoms
184	keyword.plumPuddingModel
185	keyword.circularMotion
186	keyword.rotationalMotion
187	keyword.linearMotion
188	keyword.ellipticalMotion
189	keyword.planets
190	keyword.satellites
191	keyword.astronomy
192	keyword.resonance
193	keyword.pendulum
194	keyword.simpleHarmonicMotion
195	keyword.simpleHarmonicOscillator
196	keyword.pH
198	keyword.hydronium
199	keyword.concentration
200	keyword.hydroxide
201	keyword.moles
202	keyword.molarity
203	keyword.dilution
204	keyword.workFunction
205	keyword.stoppingPotential
206	keyword.intensity
207	keyword.probability
208	keyword.binomialDistribution
209	keyword.galton
210	keyword.projectileMotion
211	keyword.airResistance
212	keyword.potentialBarrier
213	keyword.quantumMeasurement
214	keyword.particles
215	keyword.doubleSlit
216	keyword.radioWaves
217	keyword.wavePropagation
218	keyword.rateCoefficients
219	keyword.activationEnergy
220	keyword.chemicalEquilibrium
221	keyword.leChateliersPrinciple
222	keyword.catalysts
223	keyword.resistivity
224	keyword.inverse
225	keyword.reaction
226	keyword.arrheniusParameters
227	keyword.atomicNuclei
228	keyword.rutherfordScattering
229	keyword.solubility
230	keyword.salt
231	keyword.solutions
232	keyword.saturation
233	keyword.chemicalFormula
234	keyword.ksp
235	keyword.ionicCompounds
236	keyword.dynamicEquilibrium
237	keyword.solubilityProduct
238	keyword.orderParameter
239	keyword.criticalParameter
240	keyword.criticalExponent
241	keyword.diodes
242	keyword.transistors
243	keyword.leds
244	keyword.doping
245	keyword.mri
246	keyword.magneticMoment
247	keyword.spin
248	keyword.sound
249	keyword.pitch
250	keyword.speakers
251	keyword.torque
252	keyword.momentOfInertia
253	keyword.angularMomentum
254	keyword.vectorAddition
255	keyword.angle
256	keyword.vectorComponents
257	keyword.diffraction
258	keyword.transverseWaves
259	keyword.longitudinalWaves
260	keyword.waves
261	keyword.waveSpeed
262	keyword.sun
263	keyword.switches
264	keyword.acVoltage
265	keyword.rainbows
266	keyword.splitting
267	keyword.ammonia
268	keyword.spinHalfParticles
269	keyword.biology
270	keyword.food
271	keyword.sines
272	keyword.cosines
273	keyword.charlesLaw
274	keyword.greenhouseGases
275	keyword.photonBeams
276	keyword.lunarLanding
277	keyword.thrust
278	keyword.fuelConsumption
279	keyword.measurement
280	keyword.molecules
281	keyword.pulses
282	keyword.periodicMotion
283	keyword.acids
284	keyword.bases
285	keyword.histograms
286	keyword.initialSpeed
287	keyword.quantumParticles
288	keyword.equilibrium
289	keyword.alphaParticles
290	keyword.signalCircuits
\.


--
-- Data for Name: keyword_mapping; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY keyword_mapping (simulation_id, keyword_id, idx) FROM stdin;
8	1	0
8	2	1
8	3	2
8	4	3
8	5	4
8	6	5
8	7	6
8	8	7
8	9	8
8	10	9
8	11	10
8	12	11
41	98	5
5	52	0
12	108	5
5	53	1
14	1	0
5	55	2
14	66	1
5	100	3
8	13	12
14	47	2
8	14	13
5	58	4
8	15	14
22	22	0
22	23	1
32	24	0
32	25	1
32	26	2
32	27	3
32	28	4
32	29	5
32	30	6
37	16	0
37	20	1
37	17	2
37	21	3
37	19	4
57	31	0
57	32	1
57	33	2
57	34	3
57	35	4
57	36	5
26	37	0
26	38	1
26	39	2
26	40	3
26	41	4
26	42	5
26	43	6
26	44	7
26	45	8
1	1	0
1	46	1
1	47	2
1	48	3
1	49	4
1	50	5
1	51	6
4	52	0
14	48	3
36	21	5
5	101	5
36	20	4
14	30	4
36	19	3
5	102	6
36	17	2
5	99	7
36	16	0
36	52	1
4	53	1
4	54	2
4	55	3
4	56	4
4	57	5
4	58	6
4	59	7
4	60	8
3	1	0
3	4	1
3	57	2
3	9	3
2	2	0
2	1	1
2	3	2
2	5	3
2	4	4
2	9	5
2	57	6
2	11	7
2	10	8
58	61	0
58	38	1
58	62	2
58	63	3
58	40	4
58	64	5
58	65	6
58	52	7
58	43	8
59	47	0
59	66	1
59	67	2
59	4	3
59	1	4
59	68	5
59	49	6
59	69	7
59	70	8
7	2	0
7	8	1
7	9	2
7	7	3
7	6	4
7	4	5
7	5	6
7	3	7
7	1	8
7	71	9
7	72	10
7	73	11
7	74	12
7	75	13
7	76	14
7	15	15
7	11	16
7	12	17
7	13	18
7	14	19
7	77	20
7	78	21
9	62	0
9	61	1
9	63	2
9	79	3
9	80	4
9	81	5
10	56	0
10	53	1
10	52	2
10	82	3
10	83	4
10	84	5
10	85	6
10	86	7
60	87	0
60	88	1
60	89	2
60	36	3
60	90	4
60	91	5
60	92	6
60	93	7
60	94	8
41	95	0
41	53	1
41	96	2
41	97	3
41	57	4
14	49	5
13	1	0
12	105	0
13	66	1
13	30	2
12	104	3
12	106	1
12	103	2
12	107	4
13	47	3
15	109	0
15	110	1
15	111	2
15	112	3
15	113	4
15	24	5
15	114	6
15	115	7
61	28	0
61	87	1
61	36	2
61	116	3
61	90	4
62	117	0
62	36	1
62	41	2
62	118	3
62	119	4
62	120	5
19	121	0
19	122	1
19	123	2
19	1	3
19	124	4
19	113	5
19	125	6
19	74	7
19	126	8
19	127	9
19	128	10
19	129	11
19	130	12
63	121	0
63	122	1
63	1	2
63	123	3
63	124	4
63	113	5
63	125	6
63	74	7
63	126	8
20	131	0
20	24	1
20	112	2
20	27	3
20	25	4
20	26	5
20	114	6
20	30	7
20	132	8
20	133	9
20	134	10
21	52	0
21	135	1
21	136	2
21	137	3
21	138	4
21	139	5
21	140	6
21	102	7
21	141	8
21	142	9
21	143	10
64	38	0
64	144	1
64	40	2
64	112	3
25	37	0
25	39	1
25	41	2
25	40	3
25	113	4
25	145	5
25	38	6
25	144	7
25	114	8
25	45	9
25	146	10
25	147	11
25	43	12
17	129	0
17	121	1
17	122	2
17	123	3
17	1	4
17	124	5
17	113	6
17	125	7
17	74	8
17	126	9
17	127	10
17	130	11
17	128	12
65	148	0
65	149	1
65	150	2
9	150	6
65	62	3
65	151	4
65	19	5
65	152	6
65	153	7
65	154	8
23	38	0
23	144	1
23	22	2
23	155	3
23	156	4
23	37	5
23	62	6
23	61	7
23	44	8
23	157	9
23	158	10
23	43	11
55	4	0
55	46	1
55	1	2
55	47	3
55	99	4
55	51	5
46	159	0
46	24	1
46	160	2
46	161	3
46	162	4
46	163	5
46	30	6
28	62	0
28	61	1
28	17	2
28	79	3
28	53	4
28	164	5
28	165	6
28	166	7
28	167	8
28	168	9
28	52	10
28	169	11
66	24	0
66	114	1
66	170	2
66	171	3
66	131	4
66	132	5
66	30	6
66	115	7
66	133	8
18	121	0
18	122	1
18	126	2
18	128	3
16	121	0
16	122	1
16	125	2
16	126	3
16	128	4
67	171	0
67	172	1
67	131	2
67	114	3
67	135	4
67	24	5
67	112	6
67	111	7
67	110	8
67	144	9
67	65	10
67	173	11
67	174	12
67	132	13
67	109	14
29	27	0
29	25	1
29	26	2
29	30	3
29	24	4
29	29	5
30	175	0
30	65	1
30	144	2
30	176	3
30	61	4
30	66	5
30	70	6
30	43	7
24	52	0
24	177	1
24	180	2
24	43	3
24	181	4
24	182	5
24	79	6
24	62	7
24	63	8
24	53	9
24	96	10
24	183	11
24	55	12
24	184	13
35	16	0
35	19	1
35	52	2
35	20	3
35	17	4
35	21	5
31	24	0
31	26	1
31	25	2
31	185	3
31	30	4
31	135	5
31	29	6
31	186	7
31	187	8
68	24	0
68	26	1
68	25	2
68	185	3
68	160	4
68	188	5
68	30	6
68	135	7
68	29	8
68	186	9
68	114	10
68	189	11
68	190	12
68	191	13
68	115	14
11	62	0
11	61	1
11	37	2
11	53	3
11	52	4
11	57	5
11	183	6
11	79	7
11	168	8
11	167	9
11	169	10
11	63	11
11	43	12
69	11	0
69	2	1
69	5	2
69	1	3
69	4	4
69	3	5
69	36	6
69	116	7
34	52	0
34	62	1
34	53	2
34	63	3
34	61	4
34	192	5
70	24	0
70	193	1
70	194	2
70	195	3
70	141	4
70	114	5
70	109	6
38	196	0
38	198	1
38	199	2
38	200	3
38	201	4
38	202	5
38	203	6
38	96	7
38	43	8
39	62	0
39	52	1
39	79	2
39	57	3
39	53	4
39	204	5
39	205	6
39	206	7
39	138	8
39	136	9
39	2	10
39	4	11
39	3	12
39	169	13
39	43	14
71	207	0
71	88	1
71	208	2
71	209	3
51	202	10
51	237	11
72	114	0
51	43	12
51	235	2
51	229	3
72	25	1
48	214	0
48	238	1
72	171	7
48	239	2
72	26	6
48	240	3
72	27	5
49	84	0
72	24	4
72	211	2
72	210	3
72	29	8
6	52	0
6	53	1
6	111	2
6	55	3
6	58	4
6	54	5
6	195	6
6	177	7
6	102	8
6	142	9
40	52	0
40	55	1
40	111	2
40	99	3
40	54	4
40	142	5
40	143	6
40	212	7
40	180	8
40	213	9
42	52	0
42	79	1
42	57	2
42	214	3
42	62	4
42	98	5
42	215	6
42	55	7
42	97	8
42	213	9
43	135	0
43	61	1
43	30	2
43	216	3
43	66	4
43	48	5
43	138	6
43	136	7
43	137	8
43	217	9
43	43	10
44	218	0
44	219	1
44	220	2
44	221	3
44	222	4
44	43	5
73	223	0
73	5	1
73	11	2
73	36	3
73	116	4
73	224	5
27	108	0
27	111	1
27	225	2
27	38	3
27	144	4
27	226	5
27	43	6
47	52	0
47	227	1
47	228	2
47	184	3
47	43	4
49	53	1
49	2	2
51	231	0
49	1	3
49	52	4
49	241	5
51	220	1
52	250	8
49	242	6
51	221	8
49	243	7
49	244	8
49	59	9
49	60	10
50	2	0
50	1	1
74	52	0
51	230	4
50	3	2
51	233	7
51	234	5
51	232	6
51	236	9
33	245	0
33	227	1
33	30	2
33	122	3
33	246	4
33	247	5
33	192	6
52	248	0
52	97	1
52	39	2
52	136	3
52	138	4
52	249	5
52	41	6
52	137	7
74	247	1
74	213	2
74	126	3
45	159	0
45	251	1
45	252	2
45	253	3
75	30	0
75	254	1
75	255	2
75	256	3
75	36	4
56	66	0
56	97	1
56	257	2
56	215	3
56	138	4
56	136	5
56	137	6
56	249	7
56	39	8
56	250	9
56	258	10
56	259	11
56	62	12
76	97	0
76	135	1
76	138	2
76	137	3
76	260	4
76	261	5
76	112	6
76	217	7
76	43	8
\.


--
-- Data for Name: localized_simulation; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY localized_simulation (id, locale, title, description, simulation) FROM stdin;
1	cs	Balonek a statická elektřina	Proč se balonek přichytí k Vašemu svetru? Třete balonkem o svetr, potom ho nechte volně letět a on se přichytí na Váš svetr.Sledujte el.náboje na svetru, balonku a stěně.	1
2	fr	Ballons et Electricité statique	Pourquoi un ballon se colle-t-il à votre pull? Frottez un ballon sur un pull, ensuite lâchez le ballon et il ira se coller au pull. Visualisez les charges électriques du pull, des ballons, et du mur.	1
3	es	Globos y Electricidad Estática	¿Por qué pega un globo a su suéter?  Frote un globo en un suéter, dejelo, y vuela y se pega en el suéter.  Mire las cargas en el suéter, los globos, y en la pared.	1
4	et	Õhupallid ja staatiline elekter	Miks tõmbub õhupall sinu sviitri külge? Hõõru õhupalli vastu sviitrit, lase pallist lahti ja pall lendab sinu poole ning tõmbub sinu sviitri külge.	1
5	nl	Ballonnen en statische elektriciteit	Wrijf een ballon langs de sweater en laat hem dan los. De ballon 'kleeft' aan je sweater. Waarom blijft de ballon aan je sweater hangen?\nKijk naar de ladingen in je sweater, de ballonnen en de muur en ontdek het antwoord.	1
6	it	Elettricità statica	Perchè il palloncino viene attratto dal maglione? Strofina il palloncino sul maglione, poi trascinalo lontano e lascialo libero. Volerà nuovamente verso il maglione. Osserva le cariche sul maglione, sui palloncini e sul muro.\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	1
7	sk	Balón a statická elektrina	Prečo sa balón prichytí k vášmu svetru? Pošúchajte balón o sveter, potom ho nechajte voľne  letieť a on sa prichytí na váš sveter. Sledujte el. náboje na svetri, balóne a stene.	1
8	sl	Statična elektrika pri balonu.	Zakaj se balon oprime vašega puloverja?\n\nNe vemo, ali se na balonih nabere pozitiven ali negativen naboj. Vemo pa, da je na obeh istovrstni naboj, ker sta balona enaka.	1
9	iw	בלונים וחשמל סטטי	מדוע הבלון נדבק לסוודר? שפשף את הבלון בסוודר, שחרר את הבלון וראה כיצד הוא עף ונדבק לסוודר. ראה את המטענים בסוודר בבלונים ובקיר	1
10	uk	Електризація та взаємодія зарядів	Why does a balloon stick to your sweater? Rub a balloon on a sweater, then let go of the balloon and it flies over and sticks to the sweater. View the charges in the sweater, balloons, and the wall.	1
11	pt	Balões e eletricidade estática	<html>Porque o balão cola em sua blusa?<br>Esfregue o balão na blusa.<br>Visualize as cargas na blusa, no balão e na parede.	1
12	tr	Balloons and Static Electricity	Why does a balloon stick to your sweater? Rub a balloon on a sweater, then let go of the balloon and it flies over and sticks to the sweater. View the charges in the sweater, balloons, and the wall.	1
13	ru	Шарики и статическое электричество	Почему шарик прилипает к вашему свитеру? Потрите шарик о свитер, затем отпустите шарик и он полетит и прилипнет к свитеру. Изучите заряды на свитере, шариках и стене.	1
14	ga	Balloons and Static Electricity	Why does a balloon stick to your sweater? Rub a balloon on a sweater, then let go of the balloon and it flies over and sticks to the sweater. View the charges in the sweater, balloons, and the wall.	1
15	de	Ballons und statische Elektrizität	Warum bleibt ein Ballon an deinem Pullover hängen? Reibe einen Ballon an einem Pullover, lasse ihn los und er wird hinüberfliegen und am Pullover hängen bleiben. Betrachte die Ladungen am Pullover, am Ball und an der Wand.	1
16	fi	Ilmapallot ja hankaussähköä 	Miksi ilmapallo hakeutuu villapaitaasi? Hiero ilmapalloa villapaitaan. Sen jälkeen päästä irti ilmapallosta ja se lentää villapaitaan. Katso varauksia villapaidassa, ilmapallossa ja seinässä. 	1
17	el	Μπαλόνια και Στατικός Ηλεκτρισμός	Γιατί κολλάει ένα μπαλόνι στο πουλόβερ σας; Τρίψτε ένα μπολόνι στο πουλόβερ, αφήστε το και αυτό έρχεται και κολλάει πάλι στο πουλόβερ. Παρατηρείστε τα φορτία στο πουλόβερ, τα μπαλόνια και τον τοίχο.	1
18	ar	Balloons and Static Electricity	Why does a balloon stick to your sweater? Rub a balloon on a sweater, then let go of the balloon and it flies over and sticks to the sweater. View the charges in the sweater, balloons, and the wall.	1
19	in	Balon dan listrik statis	Kenapa balon menempel pada sweater kamu? Gosok balon pada sweater, kemudian lepaskan balon dan balon akan terbang menuju sweater. Lihat muatan pada sweater, balon, dan dinding	1
20	ro	Baloanele si Electricitatea Statica	De ce se lipeste un balon de pulover? Freaca un balon de pulover, apoi da-i drumul si acesta zboara si se lipeste de pulover. Vezi sarcinile puloverului, baloanelor si zidului.	1
21	pt_BR	Balões e eletricidade estática	Porque o balão cola em sua blusa?\nEsfregue o balão na blusa.\nVisualize as cargas na blusa, no balão e na parede.	1
22	ak	Balloons and Static Electricity	Why does a balloon stick to your sweater? Rub a balloon on a sweater, then let go of the balloon and it flies over and sticks to the sweater. View the charges in the sweater, balloons, and the wall.	1
23	zh_CN	气球和静电	为什么毛衣吸引气球？用气球摩擦你的毛衣并且松开气球，会发现气球向毛衣靠拢。看毛衣 气球 墙壁上的电荷	1
24	hr	Statički elektricitet	Trljanjem možemo električki nabiti balon. Ova virtualna igra je mnogo zanimljivija sa stvarnim balonima!	1
25	zh_TW	氣球和靜電引力	為什麼氣球會黏在你的毛衣上？用毛衣摩擦氣球後放手，氣球會飄晃一下，然後黏在毛衣上。查看一下毛衣、氣球和牆壁的電荷。	1
26	pl	Balony i statyczne pole elektryczne	Dlaczego balon przylega do twojego swetra? Potrzyj balon o sweter; balon odlatuje a następ[nie przylega do swetra. Jak wyglądają ładunki na swetrze  balonie oraz scianie 	1
27	en	Balloons and Static Electricity	Why does a balloon stick to your sweater? Rub a balloon on a sweater, then let go of the balloon and it flies over and sticks to the sweater. View the charges in the sweater, balloons, and the wall.	1
28	sk	Obvod s batériou a rezistorom	An exploration of Ohm's Law and how it applies to the relationship between volts, amps and ohms.	2
29	uk	Battery-Resistor Circuit	An exploration of Ohm's Law and how it applies to the relationship between volts, amps and ohms.	2
30	el	Κύκλωμα Μπαταριας-Αντιστάτη	Διερεύηση του Ν. Ohm και πως εφαρμόζεται στη σχέση μεταξύ Volt, Ampere και Ohm	2
216	hu	Elektromos hoki	Elektromos hoki-Ruth Chabay	14
31	pt	Circuito Bateria-Resistor	Uma exploração da Lei de Ohm e sua aplicação nas relações entre voltagem, corrente e resistência.	2
32	ru	Цепь батарейка - сопротивление	Исследование закона Ома, и как он применяется для поиска соотношений между Вольтами, Амперами и Омами.	2
33	es	Ley de Ohm	Una exploración de la Ley de Ohm y cómo se aplica a la relación entre voltios, amperios y omnios.	2
34	de	Batterie-Widerstandsstromkreis	Eine Erklärung des Ohm'schen Gesetzes und des Zusammenhangs zwischen Spannung, Strom und Widerstand.	2
35	nl	Battery-Resistor Circuit	An exploration of Ohm's Law and how it applies to the relationship between volts, amps and ohms.	2
36	ro	Battery-Resistor Circuit	An exploration of Ohm's Law and how it applies to the relationship between volts, amps and ohms.	2
37	hu	Battery-Resistor Circuit	An exploration of Ohm's Law and how it applies to the relationship between volts, amps and ohms.	2
38	zh_CN	电池-电阻电路	用来解释欧姆定律中的电压、电流、电阻关系	2
39	hr	Jednostavni strujni krug	Jednostavni strujni krug. Ohmov zakon i definicija jakosti struje, otpora i napona.	2
40	zh_TW	電池-電阻 電路	歐姆定律用於探討伏特、安培和歐姆的關聯	2
41	ar	دارة مدخرة-مقاومة	إستكشاف قانون أوم و كيف يطبق على العلاقة بين الفولطات ، الأمبيرات و الأوم.	2
42	pl	Obwód bateria-rezystor	Badanie prawa Ohma; jego zastosowanie do pokazania relacji między napięciem, natężeniem i rezystancją	2
43	ko	전지-저항 회로	전압, 전류, 저항의 관계를 보여 주는 '옴의 법칙'을 살며 보자.	2
44	en	Battery-Resistor Circuit	An exploration of Ohm's Law and how it applies to the relationship between volts, amps and ohms.	2
45	es	Voltaje de bateria	Una ilustracion del voltaje dentro de una bateria	3
46	de	Batteriespannung	Eine Darstellung der Spannung in einer Batterie	3
47	nl	Spanning batterij	Een simulatie van de spanning van een batterij	3
48	sk	Napätie batérie	An illustration of voltage within a battery	3
49	uk	Battery Voltage	An illustration of voltage within a battery	3
50	el	Τάση Μπαταρίας	Επεξήγηση της εμφάνισης τάσης σε μια μπαταρία	3
51	pt	Voltagem de bateria	Uma ilustração da voltagem em uma bateria	3
52	ru	Напряжение батарейки	Иллюстрация напряжения внутри батарейки	3
53	ro	Voltaj Baterie	O ilustratie a voltajului intr-o baterie	3
54	fr	Tension de la pile	Une illustration de la tension à l'intérieur d'une pile	3
55	hu	Elem feszültség	Az elem feszültségének bemutatása	3
56	it	d.d.p. in una Pila	Una illustrazione della differenza di potenziale in una pila	3
57	zh_CN	电池电压	电池内电压说明	3
58	hr	Napon (elektromotorna sila) baterije	Elektromotorna sila beterije	3
59	zh_TW	電池電壓	電池內部電壓圖解	3
60	pl	napięcie ogniwa	Ilustracja napięcia wewnętrznego ogniwa	3
61	ko	전지 전압	내부의 전위차로 인해 전압을 발생시키는 전지의 원리를 보여 준다.	3
62	en	Battery Voltage	An illustration of voltage within a battery	3
63	es	Estructura de Bandas	Explore el origen de las bandas de energía en cristales de átomos. La estructura de estas bandas determinan cómo los materiales conducen la electricidad.	4
64	el	Δομή Ζωνών	Διερευνήστε την προέλευση των ενεργειακών ζωνών στους κρυστάλλους. Η δομή αυτών των ζωνών καθορίζει την ηλεκτρική αγωγιμότητα των υλικών	4
65	pt	Estrutura de Bandas	<html>Explore a origem das bandas de energia em estruturas cristalinas.<br>A estrutura de banda determina o comportamento elétrico destes compostos cristalinos.</html>	4
66	mn	Долгионы бүтэц	Explore the origin of energy bands in crystals of atoms. The structure of these bands determines how materials conduct electricity.	4
67	nl	Bandstructuur	Onderzoek het ontstaan van energieniveaus in kristallen van atomaire stoffen. De structuur van deze energieniveaus bepaalt de geleidbaarheid van het materiaal.	4
68	ar	تركيب النطاق	اكتشف أصل نطاق الطاقة في بلورات الذرة. تركيب هذه الأشرطة يحدد كيف توصل المواد الكهرباء	4
69	hr	Struktura Vrpci	Otkrijte porijeklo energijskih vrpci u kristalima. Struktura vrpci određuje električna svojstva tvari.	4
70	zh_TW	能帶結構	探求晶體中原子們的能帶源由。這些能帶結構決定這些材料的導電性質	4
71	en	Band Structure	Explore the origin of energy bands in crystals of atoms. The structure of these bands determines how materials conduct electricity.	4
72	es	Pozos Dobles y Enlaces Covalentes	Explore la partición tunelada en potenciales de pozo doble. Este clásico problema describe muchos sistemas físicos, incluyendo enlaces covalentes, las ensambladuras de Josephson, y los sistemas del dos estado tales como el spin de 1/2 partículas y  las moléculas del amoníaco.	5
73	el	Διπλά Πηγάδια και Ομοιοπολικοί Δεσμοί	Διερευνήστε το διαχωρισμό σύραγγας σε διπλά πηγάδια δυναμικού. Αυτό το κλασσικό πρόβλημα πολλά φυσικά συστήματα όπως ομοιοπολικούς δεσμούς, επαφές Josephson και συστήματα δυο καταστάσεων όπως σωματίδια με spin  1/2 και μόρια Αμμωνίας	5
74	pt	Poços duplos e Ligações Covalentes	<html>Explore desdobramento por tunelamento em poços quânticos duplos. Este problema<br>clássico descreve vários sistemas físicos reais, incluindo ligações covalestes,<br>Junções Josephson  e sistemas de dois níveis, como partículas de spin 1/2 em campo magnético<br>e moléculas de Amônia.</html>	5
75	mn	Хоёр нүх ба 	Explore tunneling splitting in double well potentials. This classic problem describes many physical systems, including covalent bonds, Josephson junctions, and two-state systems such as spin 1/2 particles and Ammonia molecules.	5
76	nl	Covalente bindingen (dubbele potentiaalputten)	Onderzoek het opsplitsen in dubbele potentiaalputten. Dit klassieke probleem beschrijft fysische systemen waaronder covalente bindingen en ammoniakmoleculen.	5
77	ar	الأبار المضاعفة والترابط الإسهامي	اكتشف الانفصال النفقي في جهود البئر المضاعف. هذه المسألة التقليدية تصف  عدد من الأنظمة الحقيقية مثل الترابط الإسهامي، موصلات جوزيفسون، وانظمة الحالتين مثل الجسيمات ذات دوران النصف وجزيئات الأمونيا.	5
78	hr	Dvostuka jama i kovalentna veza	Otkrijte tuneliranje. Ovaj model opisuje puno različitih fizikalnih sustava: kovalentnu vezu, Josephsonov spoj, sustav s dva stanja kao što su sustavi spina 1/2 i molekula amonijaka.	5
79	zh_TW	雙位能井與共價鍵	探求雙位能井中穿隧效應之光譜分裂。此經典問題描述許多物理系統,包括共價鍵, 約瑟夫森結,以及諸如自旋1/2粒子與氨分子等之兩態系統	5
80	en	Double Wells and Covalent Bonds	Explore tunneling splitting in double well potentials. This classic problem describes many physical systems, including covalent bonds, Josephson junctions, and two-state systems such as spin 1/2 particles and Ammonia molecules.	5
81	es	Estados Ligados Cuánticos	Explore las propiedades de "partículas" cuánticas ligadas en pozos de potencial. Vea cómo las funciones de onda y las densidades de probabilidad que las describen evolucionan (o no evolucionan) a lo largo del tiempo.	6
82	el	Δεσμευμένες Κβαντικές Καταστάσεις	Διερευνήστε τις ιδιότητες κβαντικών "σωματιδίων" δεσμευμένων σε πηγάδια δυναμικού. Δείτε πως οι κυμματοσυναρτήσεις και οι πυκνότητες πιθανότητας που τις περιγράφουν εξελίσονται (ή δεν εξελίσονται) με τον χρόνο.	6
83	pt	Estados Quânticos Ligados	<html>Explore as propriedades de "partículas quânticas" ligadas em poço de potencial.<br>Veja a evolução temporal de suas funções de onda e densidades de probabilidade.<br></html>	6
84	mn	Quantum Bound States	Explore the properties of quantum "particles" bound in potential wells. See how the wave functions and probability densities that describe them evolve (or don't evolve) over time.	6
85	nl	Quantum bindingstoestanden	Onderzoek de eigenschappen van 'quantumdeeltjes' gebonden in potentiaal putten. Merk op hoe de golffuncties en waarschijnlijkheidsverdelingen de deeltjes beschrijven in de tijd.	6
86	ar	حالات الربط الكمي	اكتشف خصائص الجسيمات الكمية في بئر الجهد. انظر كيف تنبعث أو لاتنبعث دوال الموجة والكثافات الاحتمالية التي تصفهم مع الزمن.	6
87	hr	Vezana stanja (kvantnomehanički koncept)	Istražite svojstva kvanata vezanih u potencijalnoj jami. Vizualizirajte kako  valna funkcija i funkcija gustoće vjerojatnosti opisuju ponašanje kvanata u vremenu.	6
88	zh_TW	量子束縛態	探求在位能井中量子"粒子"的性質。觀察描述這些量子粒子的波函數以及機率密度如何隨著(或不隨著)時間演變。	6
89	en	Quantum Bound States	Explore the properties of quantum "particles" bound in potential wells. See how the wave functions and probability densities that describe them evolve (or don't evolve) over time.	6
90	fi	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
91	el	Εργαστήριο κατασκευής κυκλωμάτων συνεχούς κι εναλλασσομένου ρεύματος	Κατασκευάστε κυκλώματα μ' αντιστάτες, λαμπτήρες, διακόπτες, πηνία, πυκνωτές κι ηλεκτρικές πηγές AC ή DC. Πάρτε μετρήσεις μ' αμπερόμετρο και βολτόμετρο και κατασκευάστε τα διαγράμματα τάσης κι έντασης \nσυναρτήσει του χρόνου. Δείτε το κύκλωμα με σύμβολα ή μ' αληθοφανή όψη.\nΣώστε για το μέλλον, τα κυκλώματα που κατασκευάσατε !!\n\nEλληνική απόδοση : Κώστας Δακανάλης.	7
92	ar	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	7
93	fr	Circuit Construction Kit (DC and AC)	Créez votre propre circuit, jouez avec les électrons, faites briller une ampoule.	7
94	es	Kit de Construccion de Circuitos (CC y CA)	Esta nueva versión de CCK ¡añade los capacitores, inductores y las fuentes de voltaje a tu caja de herramientas! Ahora se puede sacar gráficos de corriente y de voltaje en función del tiempo.	7
95	et	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
96	nl	Schakeling bouwdoos (Gelijk~ en wisselspanning)	Deze nieuwe versie van CCK heeft condensatoren, inductoren en wisselspanningsbronnen aan de gereedschappen toegevoegd! Je kan grafieken van stroom en spanning in de tijd maken.	7
97	it	Circuit Construction Kit (DC and AC)	Questa nuova versione aggiunge alla toolbox condensatori, induttori e sorgenti a correnti alternate! E' possibile visualizzare il grafico della tensione e corrente in funzione del tempo.\n\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	7
98	hu	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
99	sk	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
100	sl	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
101	uk	Електричні кола постійного та змінного струму	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
102	pt	Circuitos AC/DC (DC e AC)	<html>Nova versão do simulador de circuitos que possui elementos e fontes de corrente alternada!<br>Agora você pode visualizar a corrente e a tensão num gráfico em função do tempo.</html>	7
103	da	Kredsløbssamlesæt (jævn- og vekselstrøm)	I denne nye version af CCK er der tilføjet kondensatorer, induktionsspoler og vekselstrømkilde til din 'værktøjskasse'!	7
217	it	Hockey Elettrico	Hockey Elettrico - da un lavoro di Ruth Chabay	14
218	sk	Elektrický hokej	Electric Hockey - odvodený z práce Ruth Chabay	14
104	ru	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
105	ga	Circuit Construction Kit (DC and AC)	Tóg do ciorcad fhéin, Imir le leictreoin, las suas bolgán.	7
106	de	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
107	ja	直流・交流回路キット	新回路キットはコンデンサーとインダクターと交流電源が加わりました。電流と電圧の時間変化のグラフとして表すことができます。	7
108	vi	Thi nghiem mach dien (DC và AC)	Phiiên bản mới này của CCK có thêm tụ, vật dẫn điện và nguồn xoay chiều trong hộp công cụ. Bây giờ, bạn có thể có được đồ thị dòng điện và điện áp theo thời gian.	7
109	nb	Circuit Construction Kit (likestrøm og vekselstrøm)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
110	ro	Trusa de Constructie Circuite (DC si AC)	Versiunea noua CCK adauga Cutiei de Ustensile condensatoare, inductori si surse voltaj AC! Acum poti reprezenta cutentul si voltajul ca o functie a timpului.	7
111	sr	Комплет за једносмерну струју	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
112	tr	Devre Yapım Kiti (AC ve DC)	Devre yapım kitinin yeni versiyonu ile kapasitör, indüktör ve alternatif akım seçenekleri kullanılabilecektir. 	7
113	ak	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	7
114	hr	Dizajniranje srujnih krugova	Nova inačica CCK sadrži kapacitore, induktancije i izvore izmjeničnog napona! Sada možete vidjeti ovisnost struje i napona u ovisnosti o vremenu.	7
115	zh_TW	電路組裝套件(直流和 交流)	新版的CCK在工具箱中加入電容、電感和交流電源。現在可以用圖表表示時間函數中的電流與電壓。	7
116	pl	Zestaw symulacyjny obwodów - napięcia stałe i zmienne	Ta nowa wersja obwodu CCK zawiera dodatkowe kondensatory, indukcyjności, napięcia zmienne; są w skrzynce narzedziowej. Możesz również uzyskać wykresy prądu i napięcia w funkcji czasu	7
117	en	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
118	fi	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
119	el	Εργαστήριο κατασκευής κυκλωμάτων συνεχούς ρεύματος	Κατασκευάστε κυκλώματα μ' αντιστάτες, λαμπτήρες, διακόπτες και μπαταρίες. Δείτε το κύκλωμα με σύμβολα ή μ' αληθοφανή όψη.\nΣώστε για το μέλλον, τα κυκλώματα που κατασκευάσατε !!\nEλληνική απόδοση : Κώστας Δακανάλης.	8
120	ar	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	8
121	fr	Circuit Construction Kit (DC Only)	Créez votre propre circuit, jouez avec les électrons, faites briller une ampoule.	8
122	es	Kit de Construcción de Circuitos (CC sólo)	¡Un kit electrónico en tu ordenador! Contruya circuitos con resistencias, bombillas, baterías e interruptores. Haz mediciones con los amperímetros y voltímetros realistas. Ve el circuito como un diagrama esquemático o alterna a una vista casi realista.	8
123	et	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
124	nl	Schakeling bouwdoos (Gelijkspanning)	Een elektrische bouwdoos in je computer! Bouw schakelingen met weerstanden, lampjes, batterijen en schakelaars. Doe metingen met realistische stroommeter en voltmeter. Bekijk de schakeling als een schema of als een levensecht voorbeeld.	8
125	it	Circuit Construction Kit (DC Only)	Un kit elettronico nel tuo computer! Realizza circuiti con resistenze, lampadine, batterie e interruttori. Fai misure realistiche con amperometri e voltmetri. Vedi il circuito in modalità schema elettrico o in modalità simile alla realtà.\n\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	8
126	hu	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
127	sk	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
128	sl	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
129	uk	Електричні кола постійного струму	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
130	pt	Circuitos de Corrente Contínua (DC)	<html>Um kit de experimentação de eletrônica em seu computador!<br>Construa circuitos com resistores, lâmpadas, baterias e interruptores. <br>Realize medidas com amperímetros e voltímetros.<br>Visualize o circuito no formato de diagrama esquemático ou no formato de visão natural.</html>	8
131	da	Kredsløbssamlesæt (kun jævnstrøm)	Et elektronisk samlesæt i din komputer!\nByg kredsløb med modstande, pærer, batterier og kontakter.\nForetag målinger med realistiske amperemetre og voltmetre.\nSe kredsløbene i diagramform eller tegnet rigtigt.	8
170	de	Leitfähigkeit	Experiment zur Leitfähigkeit in Metallen, Plastik und Photoleitern. Beobachte, warum Metalle leiten und Plastik nicht und, warum einige Materialien nur leiten, wenn Du Licht auf sie fallen läßt.	10
132	ru	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
133	ga	Circuit Construction Kit (DC Only)	Tóg do ciorcad fhéin, Imir le leictreoin, las suas bolgán.	8
134	de	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
135	ja	直流回路キット	電子回路キット 抵抗、電球、電池、スイッチを使って回路を作ましょう電流計と電圧計で測定しよう。 \\u3000回路を実物図にしたり、配線図にしたりしてみましょう。	8
136	vi	Mạch điện (một chiều)	Bạn hãy lắp mạch điện theo mục đích của mình, mạch gồm có bóng đèn, điện trở, công tắc và nguồn điện 1 chiều. Sau đó, sử dụng dụng cụ đo Ampe kế để đo cường độ dòng điện trong mạch, Vôn kế để đo điện áp giữa 2 đầu đoạn mạch...	8
137	nb	Circuit Construction Kit (kun likestrøm)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
138	ro	Trusa de Constructie Circuite (doar DC)	O trusa electronica pe computerul dvs.! Construiti circuite cu rezistori, becuri electrice, baterii si comutatoare. Faceti masuratori cu ampermetrii si voltmetrii realistici. Vizualizati circuitul ca o diagrama schematica, sau schimbati vizualizarea in Animatie realista.	8
139	sr	Комплет за једносмерну струју	Комплет за једносмерну струју у Вашем рачунару! Саставите кола са отпорницима, сијалицама, батеријама и прекидачима. Мерите са реалистичним амперметрима и волтметрима. Прикажите коло као шематски дијаграм, или реалистично.	8
140	tr	Devre Yapım Kiti (sadece DC)	Bilgisayarındaki elektronik kit	8
141	ak	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	8
142	hr	Dizajniranje srujnih krugova (samo DC)	konstruirajte svoje strujne krugove i mjerite vrijednosti struja u virtualnom eksperimentu.	8
143	zh_TW	電路組裝套件(直流)	在電腦中的電子學套件！以電阻、燈泡、電池和開關組成電路。以擬真的電流錶與電壓錶進行測量。可以符號或圖像檢視電路圖。	8
144	pl	Zestaw symulacyjny obwodów - tylko napięcia stałe	Zestaw elektroniczny w Twoim komputerze. Buduj obwody elektryczne złożone Z baterii, przewodów, żarówek, wyłączników etc. Zmierz napięcia i prądy. Obejrz obwód, tak jak wygląda naprawdę oraz w postaci schematu.	8
145	en	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
146	it	Color Vision	Come funziona la visione  dei colori. Come funzionano i filtri per i colori.\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	9
147	sk	Farebné videnie	Ako vnímame farby a funkcia farebných filtrov	9
148	vi	Quan sát màu sắc	Cách quan sát màu sắc và lọc sắc	9
149	uk	Колір, що його бачить людина	How color vision and filters work	9
150	el	Έγχρωμη όραση	Λειτουργία της έγχρωμης όρασης και των φίλτρων	9
151	pt	Percepção de cor	Como funcionam a percepção de cor e os filtros de cores?	9
152	cs	Barevné vidění	Jak funguje barevné vidění a filtry	9
153	es	Visión del Color	Como funcionan la visión del color y los filtros	9
154	fr	Visions des couleurs	Comment voit-on les couleurs et comment fonctionne un filtre ?	9
155	de	Farbwahrnehmung	Wie die Farbwahrnehmung und die Filter funktionieren.	9
156	nl	KLEUR	Hoe kleuren en filters werken	9
157	ru	Цветовое зрение	Как работает цветовое зрение и светофильтры	9
158	zh_CN	光的混合	光的混合和滤光片的作用	9
159	hr	Kako vidimo boje?	Kako vidimo boje?	9
160	zh_TW	彩色視覺	彩色視覺和濾鏡是如何起作用的	9
161	pl	Widzenie kolorowe	Jak działają filtry; ich wpływ na widzenie	9
162	et	Värvuse tajumine	Kuidas töötavad nägemine ja valgusfiltrid?	9
163	en	Color Vision	How color vision and filters work	9
164	nl	Geleidingsvermogen	Waarom geleidt een mataal wel en een isolator niet?\nHoe werkt een LDR?\nDat ontdek je met deze simulatie.\nZet spanning op een geleider (metaal), isolator (plastic) of LDR (= lichtgevoelige geleider of fotogeleider). en onderzoek of er een stroom gaat lopen.\nOnderzoek of de snelheid en energie van de elektronen  verandert als je de spanning van de batterij verandert of met de lamp op het geleidende materiaal schijnt.\n	10
165	vi	Conductivity	Experiment with conductivity in metals, plastics and photoconductors. See why metals conduct and plastics don't, and why some materials conduct only when you shine a flashlight on them.	10
166	uk	Електропровідність	Experiment with conductivity in metals, plastics and photoconductors. See why metals conduct and plastics don't, and why some materials conduct only when you shine a flashlight on them.	10
167	el	Αγωγιμότητα	Διερευνήστε την αγωγιμότητα των μετάλλων , των μονωτών και των φωτοαγώγιμων υλικών. Δείτε γιατί τα μέταλλα άγουν ενώ τα πλαστικά όχι και γιατί κάποια υλικά άγουν μόνο όταν φωτοβολούνται 	10
168	pt	Conductividade	Estude a corrente elétrica aplicada a um condutor, isolante ou foto-condutor.	10
169	es	Conductividad	Experimente con la conductividad en metales, plásticos y fotoconductores. Vea por qué los metales conducen y los plásticos no, y por qué algunos materiales conducen sólo cuando se aplica una luz sobre ellos.	10
214	de	Elektrisches Hockey	Elektrisches Hockey von Ruth Chabay	14
215	nl	Elektrischveldhockey	Elektrischveldhockey  -  vrij naar het werk van Ruth Chabay	14
171	ru	Проводимость	Экспериментируйте с проводимостью в металлах, пластмассах и фотопроводниках. Узнайте, почему металлы проводят электричество, а пластмассы нет, и почему некоторые материалы проводят только если посветить на них ярким светом.	10
172	ro	Conductivitate	Experimenteaza conductivitatea metalelor, plasticelor si fotoconductorilor. Vezi de ce metalele conduc electricitatea si plasticele nu, si de ce unele conduc doar cand aplici lumina pe ele	10
173	zh_CN	物体导电性能实验	使用该实验可以观看金属、塑料和半导体材料导电性能（光敏电阻：当有光照射时电阻很小）。	10
174	hr	Vodljivost	Koncept vodljivosti u metalima, izolatorima i fotoosjetljvim materijalima	10
175	zh_TW	導電率	以金屬、塑膠和光導體進行導電率實驗。留意為什麼金屬能導電而塑膠則否，而某些材質只有在照光時才能導電。	10
176	pl	Przewodnictwo	Eksperymentuj ze zjawiskiem przewodnictwa elektrycznego metali, tworzyw sztucznych, fotoprzewodników. Zobacz, dlaczego metale przewodza prąd elektryczny a tworzywa sztuczne nie. Dlaczgo niektóre materiały przewodzą gdy są oświetlone?	10
177	en	Conductivity	Experiment with conductivity in metals, plastics and photoconductors. See why metals conduct and plastics don't, and why some materials conduct only when you shine a flashlight on them.	10
178	sk	Neónové svetlá a ostatné výbojky 	Skúmanie ako fungujú svetelné výbojky	11
179	uk	Неонова та інші газоразрядні лампи	An exploration of how discharge lamps work	11
180	pt	Neon Lights & Other Discharge Lamps	An exploration of how discharge lamps work	11
181	es	Luces de Neón y otras Lámparas de Descarga	Una exploración de cómo funcionan las lámparas de descarga.	11
182	de	Neonlicht und andere Entladungslampen	Eine Untersuchung wie eine Entladungslampe arbeitet.	11
183	nl	Neonverlichting en andere ontladingslampen	Een onderzoek naar de werking van een gasontladingslamp	11
184	zh_CN	氖灯和其它霓虹灯原理	解释霓虹灯的工作原理	11
185	hr	Neonsak svjetiljka & druge izbojne lampe	Objašnjenje izboja u plinovima i kako rade svjetiljke na tom principu	11
186	zh_TW	氖氣燈和其它氣體放電燈	探討氣體放電燈如何運作	11
187	pl	Lampa neonowa i inne lampy wyładowawcze	Badanie lamp wyładowawczych	11
188	en	Neon Lights & Other Discharge Lamps	An exploration of how discharge lamps work	11
189	es	Comida y Ejercicio	¿Cuántas calorías hay en sus comidas favoritas? ¿Cuánto ejercicio debe hacer para quemar estas calorías? ¿Qué relación hay entre las calorías y el peso? Explore estos temas escogiendo la dieta y el ejercicio y manteniendo la vista en su peso.	12
190	pt_BR	Comer & Exercitar-se	Quantas calorias têm em seus alimentos favoritos? Quanto exercício  você tem que fazer para queimar essas calorias? Qual é a relação entre calorias e peso? Explore estas questões optando por dieta e exercício físico e fique de olho em seu peso.	12
191	ar	التغذية والتمرين 	ما عدد السعرات الحرارية في الأطعمة المفضلة لديك؟ ما مقدار التمرين اللازم لحرق هذه السعرات؟ ما العلاقة بين الوزن والسعرات؟ إستكشف هذه القضايا عن طريق اختيار نظام غذائي معين وممارسة الرياضة بينما تحافظ على مراقبة وزنك	12
192	hu	Étkezés és tevékenység	Hány kalória van kedvenc élelmiszerekben? Mennyit kell mozogni, hogy ezt a kalóriát elégessük ? Milyen kapcsolat van a kalória és a tömeg között? Fedezze fel ezekre a kérdésekre választ diéta és a testmozgás segítségével, valamint kísérje figyelemmel a tömeget.\n\t\t	12
193	nl	Eten en bewegen	Hoeveel calorieën zitten er in jouw voedsel?  \nHoeveel beweging moet je hebben om deze calorieën weer te verbranden?\nWat is het verband tussen calorieën en gewicht?\nOnderzoek het door voedsel en beweging te kiezen en op ge gewicht te letten.	12
194	el	Διατροφή & Άσκηση	Πόσες θερμίδες υπάρχουν στα αγαπημένα σας φαγητά; Πόση άσκηση απαιτείται για να 'κάψετε' αυτές τις θερμίδες; Ποια είναι η σχέση μεταξύ θερμίδων και βάρους; Μελετήστε αυτά τα ζητήματα επιλέγοντας διατροφή και άσκηση και παρακολουθώντας το βάρος σας.	12
195	zh_TW	Eating & Exercise 飲食與運動	您知道您最愛的食物有多少熱量? 您必須做多少運動才能燃燒這些熱量? 熱量與體重之間有什麼關係? \n讓我們共同來探索這些議題, 藉由選擇飲食與運動來注意體重可能的變化.	12
196	en	Eating & Exercise	How many calories are in your favorite foods?How much exercise would you have to do to burn off these calories?What is the relationship between calories and weight?  Explore these issues by choosing diet and exercise and keeping an eye on your weight.	12
197	es	campo e	campo e	13
198	nl	Elveld	Elveld	13
199	it	CampoE	CampoE	13
200	vi	điện trường	điện trường	13
201	uk	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
202	el	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
203	pt	campo-e	Campo Elétrico	13
204	ro	camp e	camp e	13
205	ar	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
206	zh_CN	电场、电荷	这个实验探究电荷怎样形成电场和电场对电荷的作用	13
207	hr	Električno polje naboja	Kako naboji stvaraju električno polje i kako električno polje djeluje na naboje!	13
208	pl	Electric Field of Dreams	Zbadaj, jak pole elektryczne jest wytwarzane przy pomocy ładunków elektrycznych i jak te ładunki reaguja na pole elektryczne	13
209	zh_TW	電場	探索如何以電荷產生電場，及電荷與電場的相互作用。	13
210	en	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
211	pt	Hockey Elétrico	Hockey Elétrico - baseado do trabalho de Ruth Chabay	14
212	es	Hockey Electrico	Hockey Electrico - derivado del trabajo de Ruth Chabay	14
213	fr	Electric Field Hockey - derived from work by Ruth Chabay	Electric Hockey - derived from work by Ruth Chabay	14
219	uk	Електричний хокей	Електричний хокей - derived from work by Ruth Chabay	14
220	el	Ηλεκτροστατικό Χόκεϋ	Ηλεκτροστατικό Χόκεϋ - δημιουργήθηκε από την εργασία της Ruth Chabay	14
221	ro	Hockey Electric	Hockey Electric - derivat din lucru de Ruth Chabay	14
222	ar	لعبة الهوكي الكهربائية - مستلة من عمل روث تشابي	لعبة الهوكي الكهربائية - مستلة من عمل روث تشابي	14
223	zh_CN	电荷曲棍球	电荷曲棍球	14
224	hr	Električni hokej! Naboj umjesto paka!	Električni hokej! Naboj umjesto paka!	14
225	pl	Elektryczny hokej - zapożyczony z pracy Ruth Chabay	Elektryczny hokej - zapożyczony z pracy Ruth Chabay	14
226	zh_TW	電場曲棍球	電場曲棍球 - 學習自 Ruth Chabay 教授 (美國北卡州立大學物理系) 的同名創作	14
227	en	Electric Field Hockey - derived from work by Ruth Chabay	Electric Hockey - derived from work by Ruth Chabay	14
228	ru	Энергетический каток	Модель Энергетический каток описывает законы сохранения	15
229	de	Energieskatepark	Der Energieskatepark macht die Energieerhaltung anschaulich.	15
230	ja	Energy Skate Park　(スケート遊園地）	Energy Skate Park　はエネルギー保存則のシミュレーションです。	15
231	pl	Energia w Skate Parku	Animacja demonstruje zasadę zachowania energii	15
232	fr	SKATE PARC DU MONT-CHALATS	"Skate parc du Mont-Chalâts" simule la conservation de l'énergie mécanique.	15
233	es	Pista de patinar "Energía"	Pista de patinar "Energía" muestra la conservación de energía	15
234	et	Energia rula park	Energia rulapargi simulatsioon kirjeldab energia jäävuse seadust	15
235	nl	EnergieSkatepark	Deze simulatie van een skater toont behoud van energie aan.	15
236	it	Energy Skate Park	Simulazione di un parco di pattinaggio per descrivere il principio di conservazione dell'energia. \nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	15
237	sk	Energetický skejt park	Energy Skate Park Simulation depicts Energy Conservation	15
238	sl	Energy Skate Park	Energy Skate Park Simulation depicts Energy Conservation	15
239	uk	Energy Skate Park	Energy Skate Park Simulation depicts Energy Conservation	15
240	lt	Energijos riedlenčių parkas	Energijos riedlenčių parko simuliacijos pavaizdavimas energijos tvermę	15
241	iw	פארק שעשועים	הדמיה - פארק שעשועים 	15
242	pt	Trilha de Skate	Um estudo de conservação de energia mecânica numa trilha de Skate.	15
243	ar	منتزه الطاقة للتزحلق	محاكاة الطاقة في منتزه التزحلق لإيضاح حفظ الطاقة	15
244	hr	Zakon sačuvanja energije	Zakon sačuvanja energije za skejtere!	15
245	zh_TW	能量滑板競技場	能量滑板競技場模擬程式描述能量守恆定律	15
246	en	Energy Skate Park	Energy Skate Park Simulation depicts Energy Conservation	15
247	fr	Aimants et Electro-aimants	Explorez les interactions entre un aimant et une boussole. Découvrez comment utiliser une pile et du fil électrique pour faire un aimant. Pouvez-vous faire un aimant plus puissant? Pouvez-vous inverser le champ magnétique?	16
248	es	Imanes y Electroimanes	Explora las interacciones entre un imán y una brújula. ¡Descubre cómo puedes usar una pila y un alambre para hacer un imán! ¿Puedes hacer un imán fuerte? ¿Puedes invertir el campo magnético?	16
249	et	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
250	nl	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
251	hu	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
252	sk	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
253	sl	Magnet in elektromagnet	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
254	vi	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
255	lt	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
256	uk	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
257	pt	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
258	da	Magneter og elektromagneter	Forklarer sammenhængen mellem et kompas og en stangmagnet. Viser hvordan du kan bruge et batteri og en ledning til at frembringe en magnet!\nKan du gøre magneten stærkere?\nKan du få det magnetiske felt til at skifte retning?	16
259	tr	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
260	ru	Магниты и электромагниты	Исследуйте взаимодействия между компасом и постоянным магнитом. Откройте для себя как сделать магнит из батарейки и куска провода. Как сделать магнит сильнее? Как поменять направление магнитного поля?	16
503	hu	Mikrohullámok	Szimuláció annak bemutatására, hogyan melegítik fel a mikrohullámok a dolgokat.	30
261	de	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
262	el	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
263	ar	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
264	it	Magneti ed elettromagneti	Esplora l'interazione tra bussola e magnete. Scopri come puoi usare una batteria e un filo per fare un magnete. Puoi fare un magnete più forte? Puoi fare invertire il campo magnetico?	16
265	ro	Magneti si Electromagneti	Experimenteaza interactiunile dintre o busola si un magnet bara. Descopera cum poti folosi o baterie si un fir pentru a face un magnet! Poti face un magnet mai puternic? Poti face campul magnetic reversibil?	16
266	hr	Magneti i Elektromagneti	Ispitaj međudjelovanje kompasa i magneta!	16
267	zh_CN	磁场和电流磁效应	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
268	ak	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
269	pl	Magnesy i elektromagnesy	Obserwuj oddziaływanie magnesu na igłę magnetyczną. Zobacz jak z drutu i bateri mozna zrobić magnes (elektromagnes)? Co zrobić by ten elektromagnes był silniejszy? Czy możesz zmienić bieguny tego magnesu?	16
270	zh_TW	磁鐵和電磁鐵	探索指南針和棒狀磁鐵的交互作用，發現你可使用電池和電線製作磁鐵。你可以製作較強的磁鐵嗎？你可以顛倒磁場的方向嗎？	16
271	ko	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
272	en	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
273	fr	Alternateur	Produisez de l'èlectricité avec un aimant. Découvrez l'électromagnétisme en étudiant les aimants et comment les utiliser pour faire briller une ampoule.	17
274	es	Generador	¡Genera electricidad con una barra imantada! Descubre los principios físicos tras el fenómeno explorando los imanes y cómo puedes usarlos para hacer que una bombilla se encienda. 	17
275	et	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
276	nl	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
277	hu	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
278	sk	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
279	sl	Generator	Primer delovanja električnega generatorja, ki pretvarja mehansko energijo v električno. Generatorji velikih moči se nahajajo v elektrarnah. 	17
280	vi	Generator	Hãy tạo ra điện bằng một thanh nam châm. Hãy khám phá ý nghĩa vật lý ẩn sau các hiện tượng bằng cách dùng nam châm để thắp sáng một bóng đèn 	17
281	lt	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
282	uk	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
283	pt	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
284	da	Generator	Lav elektricitet ved hjælp af en stangmagnet!\nOpdag fysikken bag fænomenet ved at udforske magneter og hvordan du kan bruge dem til at få en pære til at lyse.	17
285	tr	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
286	ru	Генератор	Генерируйте электричесто с помощью постоянного магнита! Узнайте физику явления с помощью исследования магнитов и того, как можно их использовать, чтобы заставить лампочку светиться.	17
287	de	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
288	el	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
289	ar	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
290	it	Generatore	Genera elettricità con un magnete! Scopri la fisica che sta dietro alla generazione della corrente alternata, che permette di far funzionare anche la lampadina di casa tua.	17
291	ro	Generator	Genereaza electricitate cu un magnet bara! Descopera fizica din spatele fenomenului explorand cu magnetii si modul in care pot fi folositi pentru a face un bec sa lumineze.	17
292	hr	Generator	Proizvedi struju pomću štapićastog magneta! Pokušaj objasniti kako nastaje svjetlost u žarulji!	17
293	zh_CN	发电机	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
294	ak	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
295	pl	Generator	Wygeneruj nap[ięcie poruszając magnesem! Poznaj fizykę zjawisk elektromagnetycznych i jak je mozna zastosować do zaświecenia żarówki.	17
744	en	Sim1 Name	Sim1 Description	54
296	zh_TW	發電機	利用棒狀磁鐵發電！藉由探索磁鐵和如何讓燈泡發光的過程，發現現象後的物理學知識。	17
297	ko	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
298	en	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
299	fr	Aimant et boussole	Vous etes vous jamais demandé comment une boussole vous indiquait le nord? Explorez les intéractions entre une boussole et un aimant,  ajoutez la terre et trouvez de curieuses réponses! Changez l'intensité de l'aimant et voyez comment les choses changent à l'intérieur et à l'extérieur. Utilisez le teslamètre pour mesurer les variations du champ magnétique.	18
300	es	Imán y Brújula	¿Te has preguntado cómo funciona una brújula para que apunte al Ártico? Explora la interacción entre una brújula y una barra imantada, y luego ¡añade la Tierra y encuentra respuestas sorprendentes! Varía la fuerza magnética y observa cómo cambian las cosas dentro y fuera. Usa el medidor de campo para medir los cambios del campo magnético.	18
301	et	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
302	nl	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
303	hu	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
304	sk	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
305	sl	Magnet in kompas	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
306	vi	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
307	lt	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
308	uk	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
309	pt	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
310	da	Magnet og kompas	Har du nogensinde undret dig over hvordan et kompas virker og hvorfor det peger mod Nord? Programmet forklarer sammenhængen mellem et kompas og en stangmagnet. Man kan vise jorden og se den overraskende sammenhæng.\nVarier magnetens styrke og se hvordan ting ændres, både indeni og udenfor.\nBrug feltmeter for at måle hvordan det magnetiske felt ændre sig.	18
311	tr	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
312	ru	Магнит и компас	Никогда не задумывались почему компас всегда показывает на север? Исследуйте взаимодействие между компасом и постоянным магнитом, затем добавьте Землю и найдите неожиданный ответ! Меняйте силу магнита и смотрите, что при этом меняется снаружи и внутри. используйте измеритель поля чтобы померять, как меняется напряженность магнитного поля.	18
313	de	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
314	el	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
315	ar	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
316	it	Magnete e Bussola	Ti sei mai chiesto come funziona una bussola e perché punta verso il polo Nord? Esplora le interazioni tra l'ago della bussola e un magnete. Varia l'intensità del magnete e guarda come cambiano le cose sia dentro che fuori. Usa il gaussmetro per misurare come variano i campi magnetici	18
404	sk	Model atómu vodíka	Ako vedci určili stavbu atómu bez možnosti vidieť do atómu? Vyskúšajte rôzne modely atómu pri ožarovaní svetlom. Skontrolujte ako sa predpoklad správania modelu zhoduje s experimentálnymi výsledkami.	24
317	ro	Magnet si Busola	V-ati intrebat vreodata cum functioneaza o busola pentru a va indica Arcticul? Experimenteaza interactiunile dintre busola si un magnet bara, apoi adauga Pamantul si afla raspunsul surprinzator! Variaza puterea magnetului si vezi cum se schimba lucrurile atat in interior, cat si in exterior. Foloseste masuratorul de camp pentru a masura schimbarile campului magnetic	18
318	hr	Magnet i Kompas	Kako magnetska igle pronalazi sjever?	18
319	zh_CN	磁体和指南针	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
320	ak	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
321	pl	Magnes i kompas	Czy kiedykolwiek zastanawiałeś sie, jak ustawi się igła kompasu na Arktyce? Sprawdź oddziaływanie magnesu na igłę magnetyczną, dodaj Ziemię i... uzyskasz zaskakująca odpowiedź. Zmieniaj "siłę" magnesu i obserwuj te zmiany wewnątrz i na zewnątrz. Uzywająć miernika pola magnetycznego zbadaj, jak zmienia 	18
322	zh_TW	磁鐵和指南針	納悶指南針為何指引你朝向北極？探索指南針和棒狀磁鐵的交互作用，再加入地球此因素，發現驚奇的答案！改變磁鐵的強度，並觀察內部和外部磁場的變化，使用磁場儀器測量磁場的改變。	18
323	ko	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
324	en	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
325	fr	Labo d'électromagnétisme de Faraday	Démonstrations d'application de la loi de Faraday	19
326	es	Laboratorio electromágnetico de Faraday	Juega con una barra imantada, una espiral de alambre y una bombilla para hacer encender la bombilla y aprender sobre la ley de Faraday. Explora cómo puedes hacer que la bombilla brille mas o menos. Juega también con electroimanes, generadores y transformadores!	19
327	et	Faraday's Electromagnetic Lab	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
328	nl	Faraday's Elektromagnetisch Lab	Deze simulatie maakt de wet van Faraday aan	19
329	hu	Faraday elektromágnes laborja	Faraday törvényének bemutatása	19
330	sk	Faradayove magnetické laboratórium	Ukážka použitia Faradayovho zákona 	19
331	sl	Faraday's Electromagnetic Lab	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
332	vi	Phòng thí nghiêm của FARADAY	Hãy chơi đùa với nam châm, cuộn dây, nam châm điện, máy biến thế,  thắp sáng bóng đèn... đồng thời học thêm về các định luật Faraday. Thử tìm cách thay đổi độ sáng bóng đèn!	19
333	lt	Faradėjaus elektromagnetinė labaratorija	Demonstruoja  Faradėjaus dėsnio taikymą	19
334	uk	Лабораторія електромагнітизму	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
335	pt	Laboratório de Eletromagnetismo	Demonstra a aplicação das Leis de Faraday	19
336	da	Faradays elektromagnetiske laboratorie.	Leg me en stangmagnet, spoler og  pærer og få pærer til at lyse og lær mere om Faradays lov. Programmet viser hvordan du kan få pærer til at lyse stærkere eller svagere. Man kan også lege med elektromagneter, generatorer og transformatorer!	19
337	tr	Faraday Elektromanyetik Lab.	Faraday Kanunu Uygulama Gösterisi	19
338	ru	Электромагнитная лаборатория Фарадея	Поиграйтесь с постоянным магнитом, катушкой провода и лампой, чтобы заставить лампочку светиться и узнать о законе Фарадея. Исследуйте, как можно сделать свечение лампы ярче или слабее. Также поиграйтесь с электромагнитами, генераторами и трансформаторами.	19
339	de	Faradays Elektromagnetisches Labor	Zeigt Anwendungen des Induktionsgesetzes.	19
340	el	Εργαστήριο Ηλεκτρομαγνητισμού Faraday	Επίδειξη των εφαρμογών του Νόμου του Faraday	19
341	ar	مختبر فراداي للمغناطيس الكهربائي	اثبات تطبيق قانون فاراداي	19
342	it	Laboratorio elettromagnetico di Faraday	Divertiti con magneti e bobine per accendere una lampadina e imparare la legge di Faraday. Esplora come puoi rendere la luce della lampadina più o meno brillante. Gioca con elettromagneti, generatori e trasformatori imparando come funzionano.	19
343	ro	Laboratorul Electromagnetic al lui Faraday	Joaca-te cu un magnet bara, fire serpentina si un bec pentru a face un bec sa lumineze si sa inveti despre legea lui Faraday. Experimenteaza cum poti face becul sa lumineze mai puternic sau mai slab. De asemenea joaca-te cu electromagneti, generatori si transformatori!	19
344	hr	Faradayeva elektromagnetska indukcija	Igranje s magnetom, zavojnicom i žaruljom može produbiti vaše razumijevanje elektromagnetske indukcije. Otkrijte kao se sjaj žarulje može povećati!	19
345	zh_CN	法拉第电磁感应实验	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
346	ak	مختبر فراداي للمغناطيس الكهربائي	اثبات تطبيق قانون فاراداي	19
494	zh_CN	迷宫小游戏	一个速度和加速度的模拟程序	29
495	hr	Zanimacje s brzinom i akceleracijom	Brzina i akceleracija	29
347	pl	Elektromagnetyczne laboratorium Pana Faradya	Eksperymentuj z magnesem, zwojnicą i żarówką tak by żarówka się świeciła. Poznaj prawo Faraday'a. Zobacz, kiedy żąrówka świeci jaśniej lub ciemniej. Zbadaj działanie elektromagnesów, generatorów i transformatorów!	19
348	zh_TW	法拉第的電磁實驗室	利用棒狀磁鐵、線圈和燈泡等物品，使燈泡發光並學習法拉弟定律，研究如何使燈泡更亮或變暗。同樣地，利用電磁鐵、發電機和變壓器來進行實驗！	19
349	ko	Faraday's Electromagnetic Lab	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
350	en	Faraday's Electromagnetic Lab	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
351	ja	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
352	sk	Sila-1d	Sila-1d	20
353	vi	Các lực trên 1 trục tọa độ	Hãy khảo sát các lực khi bạn cố gắng đẩy một cái tủ hồ sơ. Tạo ra một lực tác dụng, quan sát các lực ma sát, hợp lực tác dụng vào tủ. Sau đó vẽ đồ thị theo thời gian của lực tác dụng, tọa độ vật, vận tốc và gia tốc. Và xem hình vẽ của tất cả các lực tác dụng vào vật nặng (gồm cả trọng lực và các lực khác).	20
354	sl	sila -1d	sila -1d	20
355	uk	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
356	el	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
357	pt	Aplicação de Forças em uma Dimensão	Estude as forças presentes quando você tenta arrastar um objeto.	20
358	it	Forze in una dimensione	Esplora le forze al lavoro mentre provi a spingere un mobile. Imprimi una forza e vedi la forza di attrito e la forza risultante che agiscono sul mobile. I grafici mostrano le forze, la posizione, la velocità, l'accelerazione in funzione del tempo. Osserva il diagramma delle forze.	20
359	es	Fuerzas en 1 Dimensión	Explore las fuerzas en el trabajo cuando tratas de empujar un archivador. Crea una fuerza aplicada y observa la fuerza de fricción resultante y la fuerza total sobre el cajón. Los diagramas muestran las fuerzas, posición, velocidad y aceleración frente al tiempo. Vea un Diagrama de Cuerpo Libre de todas las fuerzas (incluyendo las fuerzas gravitacional y normal).	20
360	et	jõud-1d	jõud-1d	20
361	nl	Krachten langs een lijn	Krachten langs een lijn	20
362	hr	Koncept sile	Otkrijte djelovanje sile. Kada će sila prouzročiti gibanje, a kada neće!	20
363	zh_TW	一維空間的作用力	當你推一個滿滿的檔案櫃時，探討力的作用方式。施一作用力在檔案櫃上，並查看作用在檔案櫃的摩擦力和合力。顯示力、位置、速度和加速度對時間的圖表。觀察所有力的自由體受力圖（包含重力和正向力）。	20
364	en	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
365	pt	Séries de Fourier: Fazendo Ondas	Aprenda a gerar ondas de todas as formas pela superposição de senos e cossenos. Gere ondas no tempo e no espaço e realize a medida do comprimento de onda e do período. Observe a alteração das ondas pela mudança de  amplitude dos harmônicos e compare as expressões matemáticas de sua decomposição.	21
366	es	Fourier: Fabricacion de Ondas	Aprenda cómo hacer las ondas de todas las diversas formas agregando encima de senos o de cosenos. Haga las ondas en espacio y tiempo y mida su longitud de onda y período. Vea cómo cambiar las amplitudes de diversos armónicos cambia las ondas. Compare diversas expresiones matemáticas para sus ondas.	21
367	it	Fourier: Costruire le onde	Impara a costruire onde di tutte le forme aggiungendo funzioni seno e coseno. Crea onde nello spazio e nel tempo e misura la loro lunghezza d'onda e il loro periodo. Vedi come, cambiando l'ampiezza delle diverse armoniche, cambiano le onde risultanti. Confronta le diverse espressioni matematiche che descrivono le onde.	21
368	el	Fourier: Δημιουργία Κυμάτων	Μάθετε πως να δημιουργείτε κυματομορφές όλων των ειδών προσθέτοντας ημίτονα και συνημίτονα. Δημιουργείστε κύματα στο χώρο και το χρόνο και μετρήστε τα μήκη κύματος και τις περιόδου. Παρατηρήστε πως αλλάζοντας τα πλάτη των διαφορετικών αρμονικών μεταβάλλονται και τα κύματα. Συγκρίνετε διαφορετικές μαθηματικές εκφράσεις για τα κύματά σας	21
369	nl	Boventonen	Leer hoe je golven van allerlei vormen kunt maken door sinussen en cosinussen op te tellen. Zie wat boventonen doen.	21
370	hr	Kompozicija valova (Fourierova sinteza)	Naučite kako napraviti različite valove komponirajući samo sinusne i kosinusne oblike. Napravi val i izmjeri njegovu valnu dužinu i period. Istraži kako mijenjanjem amplituda različitim harmonicima mjenjamo valni oblik.	21
371	zh_TW	傅力葉：產生一個波	學著如何由一個正弦與餘弦的函數之疊加來產生一個波。測量波長及週期，並改變各不同諧波的振幅。比較數學展開式的不同。	21
372	pl	Fourier: Konstruowanie fal	Naucz się, jak konstruować fale o różnym kształcie poprzez dodawanie prostych przebiegów sinusoidalnych/cosinusoidalnych. Zbadaj zmienność przestrzenną, jak i czasową fal. Zobacz, jak zmiany amplitudy harmonicznychh wpływają na kształt fali. Porównaj uzyskane wyniki z ich zapisem matematycznym. To naprawdę ciekawa symulacja, która wiele cię nauczy.	21
496	pl	Labirynt - Gra	Symulacja prędkości i przyspieszenia	29
373	en	Fourier: Making Waves	Learn how to make waves of all different shapes by adding up sines or cosines. Make waves in space and time and measure their wavelengths and periods. See how changing the amplitudes of different harmonics changes the waves. Compare different mathematical expressions for your waves.	21
374	hu	Gleccserek	Állítsa be a havazást ill. a hőmérsékletet a gleccser növekedéséhez és csökkenéséhez. Használjon tudományos mérőeszközöket a sűrűség, sebesség, jegesedés megállapításához.	22
375	es	Glaciares	Ajuste la caida de nieve y la temperatura para ver crecer o encoger al glaciar. Use herramientas científicas para medir el espesor, la velocidad y el estiramiento del glaciar.	22
376	sk	Ľadovec	Zmeňte množstvo snehu na horách a teplotu na úrovni mora a sledujte či sa ľadovec zväčšuje alebo zmenšuje. Použite vedecké prístroje na zmeranie hrúbky, rýchlosti a ročnej bilancie ľadovca. 	22
377	nl	Gletsjers	Pas sneeuwval en temperatuur op zeeniveau aan en onderzoek hoe een gletsjer groeit en afsmelt. Gebruik de instrumenten om de dikte, positie, snelheid en ijsoverschot te meten.\nAccumulatie (aangroei) vindt plaats boven de evenwichtslijn en ablatie (afsmelting) onder de evenwichtslijn.\n	22
378	it	Ghiacciai	Regola la nevicata in montagna e la temperatura per osservare come il ghiacciaio cresce e si ritrae. Usa gli strumenti scientifici per misurare spessore, velocità e il bilangio di massa del ghiacciaio.	22
379	hr	Ledanjaci	Uskladi snježne padavine u gorju i temperaturu da bi prikazao topljenje ili stvaranje ledenjaka. Izmjeri debljinu, brzinu i prinos leda u ledenjaku.	22
380	el	Παγετώνες	Ρύθμιση χιονόπτωσης και θερμοκρασίας βουνού για να δείτε τον παγετώνα να μεγαλώνει και να συρρικνώνεται	22
381	zh_TW	Glacier 冰河	調整山區的降雪及溫度, 看看冰河會成長還是倒退. 可以使用科學工具去測量厚度, 速度, 冰河收支計算等	22
382	pl	Lodowce	Ustaw opady śniegu w górach oraz temperaturę, by zobaczyć jak lodowiec rośnie i kurczy się. Urzyj narzędzi naukowych by zmierzyć grubość lodowca, jego prędkość oraz  budget (równowagę między przyrostem a zanikiem lodu)	22
383	en	Glaciers	Adjust mountain snowfall and temperature to see the glacier grow and shrink. Use scientific tools to measure thickness, velocity and glacial budget.	22
384	nl	Het broeikaseffect	Bij deze simulatie vergelijk je de atmosfeer met broeikasgassen met een glazen broeikas.  Je ontdekt zo de relatie tussen de concentratie  broeikasgassen, het zonlicht en het opwarmen van de atmosfeer.	23
385	ja	大気の温室効果	温室効果ガス、大気、太陽光が\nどのような相互作用するかを\n探究するシミュレーション。	23
386	sk	Skleníkový efekt	Simulácia skúmajúca ako skleníkové plyny \npôsobia v atmosfére na slnečné svetlo.	23
387	fi	kasvihuone	kasvihuone	23
388	uk	The Greenhouse Effect	Just how do greenhouse gases change the climate? Select the level of atmospheric greenhouse gases during an ice age, in the year 1750, today, or some time in the future and see how the Earth's temperature changes. Add clouds or panes of glass.	23
389	el	Θερμοκήπιο	Θερμοκήπιο	23
390	hr	Učinak staklenika	Kako staklenički plinovi utječu na klimu? Istražite kako se mijenja temperatura Zemljine atmosfere u ovisnosti o emisiji stakleničkih plinova!	23
391	es	El Efecto de Invernadero	Una simulación para explorar cómo\ninteractúan los gases de invernadero con la atmósfera y la luz del sol.	23
392	zh_TW	溫室效應	溫室效應和大氣層及陽光交互作用的模擬實驗	23
393	zh_CN	温室效应	温室效应气体与大气层及阳光的相互作用模拟，温室气体是怎样改变气候的，通过增加云块或玻璃层，选择冰川期、公元1750年、现在或将来看地球温度的变化	23
394	pl	Efekt cieplarniany	Jak gazy cieplarniane zmieniaj ą klimat? Ustaw poziom gazów cieplarnianych w atmosferze w czasie epoko lodowcowej, w roku 1750, dzisiaj oraz kiedykolwiek w przyszłości. Zobacz, jaki to ma wpływ na temperaturę atmosfery Ziemi. Dodaj chmury lub szklane panele	23
395	et	Kasvuhooneefekt	Kuidas muudavad kasvuhoonegaaside kontsentratsiooni muutus kliimat? Vali atmosfäär (jääaeg, aasta 1750, tänapäev või tulevik), vaata, kuidas muutub Maal olev temperatuur. Liisa süsteemile pilvi või klaaspaneele!	23
396	en	The Greenhouse Effect	Just how do greenhouse gases change the climate? Select the level of atmospheric greenhouse gases during an ice age, in the year 1750, today, or some time in the future and see how the Earth's temperature changes. Add clouds or panes of glass.	23
397	el	Μοντέλα του Ατόμου του Υδρογόνου	Πως κατάφεραν οι επιστήμονες να ανακαλύψουν τη δομή των ατόμων χωρίς να τα βλέπουν; Δοκιμάστε διαφορετικά μοντέλα ακτινοβολώντας τα άτομα. Ελέγξτε πως η πρόβλεψη για το μοντέλο ταιριάζει με τα πειραματικά αποτελέσματα.	24
398	pt	Modelos do Átomo de Hidrogênio	Como será que os cientistas conseguem descobrir a estrutura dos átomos sem vê-los? Experimente diferentes modelos de átomos fazendo incidir luz sobre eles e verificando o resultado e compatrando-os com as previsões.	24
399	es	Modelos del Átomo del Hidrógeno	¿Cómo calcularon los científicos  la estructura de los átomos sin mirarlos? Pruebe diversos modelos disparando luz al átomo. Compruebe cómo la predicción del modelo se compara a los resultados experimentales.	24
400	de	Modelle des Wasserstoffatoms	Wie erkannten die Wissenschaftler den Aufbau der Atome, ohne sie sehen zu können? Probiere verschiedene Modelle aus durch Bestrahlung mit Licht. Überprüfe, ob die Vorhersagen des Modells den experimentellen Ergebnissen entsprechen.	24
401	nl	Modellen van een waterstofatoom	Hoe kwamen wetenschappers achter de structuur van de atomen zonder dat ze atomen konden zien?\nTest de verschillende modellen van het waterstofatoom door ze te beschieten met licht.  Vergelijk steeds de voorspelling met het resultaat van je experiment.\n	24
402	ja	水素原子モデル。	見えない原子の形を科学者はどうやって見つけたのでしょう。原子に光を当ててそれぞれのモデルを見てみましょう。　モデルから予測されることと実験結果が、どのくらい合うかみましょう。	24
403	hu	A hidrogén atom modelljei	Hogyan képzelték el a tudósok az atom szerkezetét? Próbáld ki a különböző modelleket fotonok kilövésével. Figyeld meg hogyan viszonyul a modellek jóslata a kísérleti tapasztalatokhoz.	24
497	en	Maze Game	A simluation of velocity and acceleration.	29
498	pt	Microondas	Uma simulação para explorar como micro-ondas aquecem as substâncias	30
405	uk	Моделі атома водню	How did scientists figure out the structure of atoms without looking at them? Try out different models by shooting light at the atom. Check how the prediction of the model matches the experimental results.	24
406	ru	Модели атома водорода	Как учёные определяют внутреннюю структуру атомов не глядя на них? Опробуйте разные модели путём облучения их светом. Проверьте, как теоретические предсказания совпадают с экспериментальными результатами.	24
407	fr	Modèles de l'atome d'Hydrogène	Comment les scientifiques ont-ils conçu la structure des atomes sans les voir? Testez différents modèles en éclairant un atome. Confrontez la validité de la prédiction aux résultats expérimentaux.	24
408	it	Modelli dell'atomo di Idrogeno	Come fecero gli scienziati a immaginarsi la struttura atomica senza vederla? Prova diversi modelli mandando della luce agli atomi. Verifica se la previsione si adatta ai risultati sperimentali.	24
409	hr	Model Atoma Vodika	Kako znanstvenici iznalaze strukturu submikroskopskih čestaca? Pokušajte testirate različite modele submikroskopske strukture različitim pretpostavkama!	24
410	zh_TW	氫原子的模型	科學家如何在看不見原子的情況下找出原子內的結構呢？方法為觀察光線照射原子的實驗，而對照從不同原子模型所做的預測來看那一個最符合實驗的結果。	24
411	ar	نماذج من ذرّة الهيدروجين	كيف فهم العلماء تركيب الذرّات بدون مشاهدة ذلك ؟ اختر نماذج مختلفة بإطلاق الضوء على الذرّة. تحقق كيف تم التنبؤ بالنموذج حتى يجاري النتائج التجريبية.	24
412	pl	Modele atomu wodoru	Jak uczeni poznają strukturę atomu nie mogąc obejrzeć go bezpośrednio? Wypróbuj różne modele atomu stosując rózne oswietlenia. Sprawdź jak przewidywania danego modelu sprawdzają sie z doświadczeniem	24
413	en	Models of the Hydrogen Atom	How did scientists figure out the structure of atoms without looking at them? Try out different models by shooting light at the atom. Check how the prediction of the model matches the experimental results.	24
414	es	Propiedades del gas	Bombée moléculas de gas en una caja y vea qué ocurre cuando cambia el volumen, añade o quita calor, cambia la gravedad, y más. Mida la temperatura y presión, y descubra cómo varían las propiedades del gas en relación unas con otras.	25
415	fr	Propriétés d'un gaz parfait	Propriétés d'un gaz parfait	25
416	de	Gaseigenschaften	Pumpe Gasmoleküle in einen Kasten und schaue, was geschieht, wenn Du das Volumen änderst, Wärme zu- oder abführst, das Gewicht änderst und mehr. Messe die Temperatur und den Druck und erforsche wie die Eigenschaften des Gases sich gegenseitig verändern. 	25
417	et	Gas Properties	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
418	nl	Eigenschappen van gassen	Pomp gas in een ruimte en kijk wat er gebeurt als je het volume verandert, warmte toe- of afvoert, de zwaartekracht verandert, enz. \nMeet de temperatuur en druk en ontdek hoe de eigenschappen van een gas van elkaar afhangen.	25
419	hu	gázrészecskék	Pumpálj gázmolekulákat a dobozba és nézd mi történik, ha változtatod a térfogatot, növeled, vagy csökkented a hőmérsékletet, változtatod a gravitációt, stb. Mérd a hőmérsékletet és a nyomást és figyeld meg hogyan reagálnak a gáz részecskéi.	25
420	sk	Vlastnosti plynu	Pumpujte plyn do komory a sledujte jeho stav ak zmeníte objem, pridáte alebo odoberiete teplo, zmeníte gravitáciu alebo ďalšie parametre. Zmerajte teplotu a tlak plynu a zistite ako tieto veličiny spolu  súvisia.	25
421	sl	Lastnosti plina	S tlačilko spustimo plin v posodo in opazujemo spremembe pri spreminjaju prostornine, gravitacije, tlaka, ...	25
422	el	Ιδιότητες Αερίου	Αντλήστε μόρια αερίου σε ένα κουτί και δείτε τι συμβαίνει καθώς μεταβάλλετε τον όγκο η/και αφαιρείτε θερμότητα, μεταβάλλετε την ένταση της βαρύτητας κ.λ.π. Μετρήστε την θερμοκρασία και την πίεση και ανακαλύψτε πως μεταβάλλονται οι ιδιότητες του αερίου η μια σε σχέση με την άλλη. 	25
423	tn	Gase e e seng ya Nnete & Bueyensi (Ideal Gas & Buoyancy)	Gase_e e seng ya nnete_&_Bueyensi	25
424	pt	Propriedades do gás	Bombeie moléculas de gás para um reservatório e veja  que acontece quando há alteração do volume. adicione e remova calor, altere a gravidade, etc.Verifique as medidas de temperatura e pressão, e descubra como as propriedades do gás variam em relação aos outros gases.	25
425	ru	Свойства газа	Накачайте молекул газа в коробку и смотрите, что поменяется при изменении её объёма, нагреве или охлаждении, изменении силы тяжести, и т.д. Измерьте температуру и давление и узнайте, как свойства газа меняются по отношению друг к другу.	25
426	cs	Vlastnosti plynu	\t\nNapumpujte molekuly plynů do uzavřeného prostoru a podívejte se, co se stane, pokud budete měnit tlak, přidávat nebo odebírat teplo, měnit gravitaci, a další. Změřte teplotu a tlak, a zjistěte, jak se vlastnosti zemního plynu  liší ve vztahu k sobě navzájem.	25
427	nb	Gassegenskaper	Pump gassmolekyler inn i en boks, og se hva som skjer når du endrer volum, legger til eller fjerner varme, endrer tyngdekraft og mer. Mål temperatur og trykk, og utforsk hvordan egenskapene i gassen endres i relasjon til hverandre.	25
428	zh_TW	氣體特性	放置氣體分子於箱子內，並改變體積、溫度、重力及其它變因，同時測量溫度和壓力，觀察這些變因和氣體的溫度及壓力的關係。	25
429	ar	خواص الغاز	ضخ جزيئات غاز في صندوق وراقب ماالذي يحصل عندنا تغير الحجم، تضيف أو تزيل الحرارة، تغير الجاذبية،وغير ذلك. قس درجة الحرارة والضغط واكتشف كيف تتغير خواص الغاز بارتباطها مع بعض.	25
430	zh_CN	气体属性	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
499	es	Microondas	Una simulación para explorar cómo las microondas calientan cosas.	30
500	de	Mikrowellen	Eine Simulation, die erklärt, wie Mikrowellen Dinge erwärmen	30
431	hr	Svojstva Plinova	Punjenjem posude plinom, grijanjem plina u posudi i dodavanjem utjecaja sile teže mijenjajte karakteristične veličine: volumen, tlak i temperaturu plina. Izmjerite vrijednosti tih veličina! Oktrijte pravilnostii!	25
432	pl	Właściwości	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
433	en	Gas Properties	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
434	es	Globos y flotación	Experimente con un globo de helio, un globo de aire caliente, o una esfera rígida llena de gases distintos. Descubra qué es lo que hace flotar a algunos globos y hundirse a otros.	26
435	fr	Ballons et Flottabilité	Ballons et Flottabilité	26
436	de	Ballons & Auftrieb	Experimente mit einem Heliumballon, einem Heißluftballon oder einer festen Hohlkugel gefüllt mit verschiedenen Gasen. Entdecke, warum manche Ballons steigen und andere sinken.	26
437	et	Balloons & Buoyancy	Experiment with a helium balloon, a hot air balloon, or a rigid sphere filled with different gases. Discover what makes some balloons float and others sink.	26
438	nl	Ballonnen en opwaartse kracht	Experimenteer met heteluchtbalonnen, heliumballonnen en bollen met verschiillende gassen. Ontdek waardoor ballonnen stijgen en dalen.	26
439	hu	Léggömb és felhajtóerő	Kísérlet hélium léggömbbel, hőlégbalonnal, vagy merev tér megtöltése különböző gázokkal. Fedezd fel melyik ballon  lebeg vagy süllyed.	26
440	sk	Balóny a  vztlak	Pokusy s balónom naplneným héliom , teplým vzduchom, alebo pevnou guľou naplnenou rôznymi plynmi. Objavte prečo niektoré balóny sa vznášajú a iné klesajú.	26
441	sl	Balon in vzgon	Simulacija kaža kaj omogoča določenim plinom da lebdijo in drugim da potonejo. 	26
442	el	Μπαλόνια & Άνωση	Πειραματιστείτε με ένα μπαλόνι Ηλίου, ένα μπαλόνι θερμού αέρα ή μια στερεή σφαίρα γεμάτα με διαφορετικά αέρια. Ανακαλύψτε τι κάνει κάποια μπαλόνια να επιπλέουν και άλλα να βυθίζονται	26
443	tn	Gase e e seng ya Nnete & Bueyensi (Ideal Gas & Buoyancy)	Gase_e e seng ya nnete_&_Bueyensi	26
444	pt	Balões e Flutuação	Faça experiências com balões de hélio, balão de ar quente, ou uma esfera rigida cheia com gases diferentes. Descubra o que faz alguns balões flutuarem e outros cairem.	26
445	ru	Воздушные шары и подъёмная сила	Экспериментируйте с шаром с гелием, шаром с горячим воздухом и с жесткой сферой, заполненной различными газами. Узнайте, что заставляет один шар лететь, а другой падать вниз.	26
446	cs	Balóny a Archimedův zákon	Pokus s balónem naplněným héliem, horkým vzduchem nebo pevná koule která se může plnit různými plyny. Zjistit jak se tyto balóny chovají, kdy klesají nebo se vznáší v prostoru.	26
447	nb	Ballonger og oppdrift	Eksperimenter med en heliumballong, en varmluftsballong eller en rigid sfære fylt med ulike gasser. Finn ut hva som gjør at enkelte ballonger stiger, mens andre synker.	26
448	zh_TW	氣球和浮力	以氦氣球，熱氣球或中空球體中填充不用的氣體來做實驗；探討是什麼因素讓一些氣球會飄浮而其它則否。	26
449	ar	المناطيد والطفو	جرب مع منطاد الهيليوم ومنطاد الهواء الساخن أو كرة قاسية مليئة بغازات مختلفة. اكتشف ماالذي يجعل بعض المناطيد تطفو والأخرى تغرق.	26
450	zh_CN	气球和浮力	以氦气球，热气球或中空球体中填充不用的气体來做实验；探讨是什么因素让一些气球会漂浮而其它则不能。	26
451	hr	Plinski Zakoni	Eksperimenti s balonima napunjenim helijem ili toplim zrakom. Plin u boci. Otkrijte što je uzrok da nešto tone a nešto pliva!	26
452	pl	Balony i siła wyporu	Experiment with a helium balloon, a hot air balloon, or a rigid sphere filled with different gases. Discover what makes some balloons float and others sink.	26
453	en	Balloons & Buoyancy	Experiment with a helium balloon, a hot air balloon, or a rigid sphere filled with different gases. Discover what makes some balloons float and others sink.	26
454	es	Reacciones reversibles	Observe cómo transcurre una reacción en el tiempo. ¿Cómo afecta la energía total a la velocidad de reacción? Cambie la temperatura, altura de barrera y energías potenciales. Registre concentraciones y tiempos para extraer los parámetros de Arrhenius. Es mejor usar esta simulación bajo guía de un maestro pues presenta una analogía de reacciones químicas.	27
455	fr	Diffusion d'un gaz avec barrière	Diffusion d'un gaz avec barrière	27
456	de	Reversible Reaktionen	Beobachte den Reaktionsverlauf. Wie beeinflusst die Gesamtenergie die Reaktionsgeschwindigkeit? Ändere die Temperatur, die Barrierehöhe und die potentiellen Energien. Zeichne den Konzentrationsverlauf auf, um die Reaktionsgeschwindigkeitskoeffizienten zu bestimmen. Stelle temperaturabhängige Überlegungen an, um die Arrhenius-Parameter zu gewinnen. Diese Simulation wird am besten mit Lehreranleitung benutzt, weil sich eine Analogie zu chemischen Reaktionen zeigt. 	27
457	et	Reversible Reactions	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
458	nl	Omkeerbare reacties	Met deze simulatie kun je de het onderwerp reactiesnelheid verduidelijken. \nKijk naar wat er gebeurt tijdens een reactie. Hoe beïnvloedt de totale energie de reactiesnelheid?\nVerander de temperatuur, activeringsenergie en de potentiële energie en let op de snelheid van de reactie. \nVraag je docent om hulp als je er niet uit komt.	27
459	hu	Megfordítható reakciók	Figyeld a reakció időbeli lefolyását. Hogyan befolyásolja a teljes energia a reakció sebességét? Változtasd a hőmérsékletet, a gát magasságát és a helyzeti energiát. Ezt a szimulációt ajánlott tanári magyarázattal használni, ill. kémiai reakciókkkal párhuzamosan bemutatni.	27
501	nl	Magnetrons	Deze simulatie laat zien hoe een magenetron iets verwarmd.	30
502	sk	Mikrovlnka	Simulácia skúmajúca ako mikrovlny zohrievajú rôzne predmety.	30
460	sk	Vratné reakcie	Pozorujte priebeh reakcie. Ako ovplyvňuje celková energia priebeh reakcie? Zmeňte teplotu, výšku bariéry a potenciálnu energiu. Zaznamenajte koncentráciu a čas reakcie pre určenie koeficientu reakcie. Skúmajte závislosť medzi teplotou a Arrheiniusovým koeficientom.  	27
461	sl	Ponovi predvajanje	Dogodke snemamo kot film, ki si ga kasneje lahko še enkrat ogledamo. Simulacija je primernejša kot predstavitev iz strani učitelja, ker je veliko parametrov, ki jih lahko speminjamo.	27
462	el	Αντιστρεπτές Αντιδράσεις	Παρακολουθείστε την πρόοδο μιας αντίδρασης με τον χρόνο. Πως επηρρεάζει η ολική ενέργεια τον ρυθμό της αντίδρασης; Μεταβάλλετε το ύψος του φραγμού θερμοκρασίας και των δυναμικών ενεργειών. Καταγράψτε συγκεντρώσεις και χρόνο ώστε να εξάγετε συντελεστές ρυθμού. Πραγματοποιήστε μελέτες εξαρτόμενες από τη θερμοκρασία για να εξάγετε τις παραμέτρους Arrhenius. Αυτή η προσομοίωση χρησιμοποιείται καλύτερα με την παρουσία του εκπαιδευτικού διότι παρουσιάζει αναλογία με χημικές αντιδράσεις.  	27
463	tn	Gase e e seng ya Nnete & Bueyensi (Ideal Gas & Buoyancy)	Gase_e e seng ya nnete_&_Bueyensi	27
464	pt	Reações reversíveis	Observe uma reação ao longo do tempo. Como a energia total afeta a taxa de reação? Varie a temperatura, a altura da barreira e a energia potencial. Registre concentrações e o tempo para extrair os coeficientes de taxa. Faça estudos de dependência de temperatura para extrair os parâmetros de Arrhenius. Esta simulação é melhor utilizada com a orientação de professores porque ela apresenta uma analogia de reações químicas.	27
465	ru	Обратимые реакции	Пронаблюдайте как протекает реакция со временем. Как полная энергия влияет на скорость протекания реакции? Меняйте температуру, высоту потенциального барьера и потенциальные энергии. Запишите концентрации и время чтобы получить постоянные скорости протекания реакции. Проведите измерения, зависящие от температуры, чтобы получить параметры Аррениуса. Эту демонстрацию лучше использовать под руководством учителя, поскольку она представляет аналогию химическим реакциям.	27
466	cs	Vratné reakce	Sledujte reakce jak postupují v čase.\nTuto simulaci je nejlépe sledovat  pod vedením učitelů, protože se zde pracuje i s chemickými reakcemi.	27
467	nb	Reversible reaksjoner	Følg med på hvordan en reaksjon forløper over tid. Hvordan blir reaksjonsraten påvirket av den totale energien? Varier temperatur, barrierehøyde og potensiell energi. Registrer konsentrasjoner og tid for å beregne reaksjonsratekoeffisienter. Gjør studier av temperaturavhengighet for å finne Arrhenius-parametre. Denne simuleringen er best å gjøre med lærerveiledning, fordi den representerer en analogi til kjemiske reaksjoner.	27
468	zh_TW	可逆反應	隨著時間觀察反應的進行。是否總能量會影響反應速度？嘗試改變溫度、活化能及焓；記錄濃度及時間以便計算速率係數。做與溫度變因相依的實驗用來計算阿忍尼士 (Arrhenius)參數。使用本模擬實的最佳方法是和教師手冊中的類似化學反應一起教學。	27
469	ar	تفاعلات قابلة للعكس	راقب التفاعل مع الزمن. كيف تؤثر الطاقة الكلية على معدل التفاعل. تحكم في درجة الحرارة، ارتفاع الحاجز، الطاقات الكامنة. سجل الزمن والتراكيز حتى تحصل على معاملات المعدل. هذه المحاكاة تستخدم بشكل أفضل بوجود المعلم لأنها تمثل  قياس التفاعلات الكيميائية	27
470	zh_CN	可逆反应	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
471	hr	Reverzibilan proces	Promatrajte proces u vremenu!	27
472	pl	Reakcje odwracalne	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
473	en	Reversible Reactions	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
474	tr	laserler	Laserlerin nasıl çalıştığının bir incelemesi	28
475	ru	Лазеры	Исследование работы лазера	28
476	es	Lasers	Una exploración de cómo los lasers funcionan	28
477	hu	Lézerek	A lézer működésének vizsgálata	28
478	sk	laser	Skúmanie ako pracuje laser	28
479	uk	Lasers	An exploration of how lasers work	28
480	pt	Lasers	Um estudo de como o Laser funciona	28
481	nl	Lasers	Een uitleg met experimenten over de werking van lasers	28
482	mn	Лазерууд	Лазерийг хэрхэн ажилдагийг судлах	28
483	pt_BR	Lasers	An exploration of how lasers work	28
484	hr	Laseri	Kako rade laseri!	28
485	pl	Lasery	Badanie zasady działania lasera	28
486	zh_TW	雷射	說明雷射是如何產生作用的	28
487	en	Lasers	An exploration of how lasers work	28
488	el	Λαβύρινθος	Προσομοίωση Ταχύτητας και Επιτάχυνσης	29
489	nl	Doolhofspel	Een simulatie van snelheid en versnelling	29
490	pt	Jogo do Labirinto	Uma simulação de Velocidade e Aceleração.	29
491	es	Maze Game	A simluation of velocity and acceleration.	29
492	ar	لعبة المتاهة	محاكاة السرعة والتسارع	29
493	zh_TW	迷宮遊戲	速度與加速度的模擬	29
504	hr	Mikrovalovi	Simulacija proučava kako mikrovalovi griju!	30
505	zh_TW	微波	探索微波如何加熱物體	30
506	pl	Mikrofale	Symulacja pokazująca ogrzewanie przy pomocy mikrofal	30
507	en	Microwaves	A simluation for exploring how microwaves heat things.	30
508	tr	iki boyutta hareket	hiz ve ivme arasindaki iliskinin gosterilmesi	31
509	es	PhET Movimiento2D	Una exploracion de como la aceleración y la velocidad se relacionan.	31
510	bg	Движение в 2D	Едно изследване на връзката между ускорение и скорост	31
511	uk	Motion in 2D	Try the new "Ladybug Motion 2D" simulation for the latest updated version. Learn about position, velocity, and acceleration vectors. Move the ball with the mouse or let the simulation move the ball in four types of motion (2 types of linear, simple harmonic, circle).	31
512	el	Κίνηση σε 2Δ	Διερεύνηση για το πως σχετίζονται επιτάχυνση και ταχύτητα.	31
513	pt	Movimento em 2D	Um estudo da relação entre velocidade e aceleração.	31
514	nl	Beweging in een vlak	Onderzoek de afhankelijkheid tussen snelheid en versnelling.	31
515	hr	Odnos brzine i akceleracije	Pomakni kuglicu mišem i otkrij kako se odnose sila i akceleracija!	31
516	zh_TW	二維移動	顯示速度與加速度的關係	31
517	pl	Ruch dwuwymiarowy	Sprawdzenie zależności między przyspieszeniem a prędkością.	31
518	en	Motion in 2D	Try the new "Ladybug Motion 2D" simulation for the latest updated version. Learn about position, velocity, and acceleration vectors. Move the ball with the mouse or let the simulation move the ball in four types of motion (2 types of linear, simple harmonic, circle).	31
519	es	El Hombre Móvil	La aplicacion del Hombre Móvil.	32
520	et	The Moving Man	Õpi tundma asukoha, kiiruse ja kiirenduse graafikuid. Liiguta meest hiire abil edasi-tagasi ja kuva tema liikumise graafikud. Sisesta positsiooni, kiiruse ja kiirenduse parameetrid ning lase simulatsioonil meest liigutada.	32
521	fa	The Moving Man	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
522	nl	Het bewegende mannetje	Bewegingen worden weergegeven in plaats-tijd, snelheid-tijd en versnelling-tijd diagrammen. Beweeg dit mannetje met de muis over het scherm en bekijk de diagrammen van de beweging. Stel plaats, tijd en versnelling in en laat het mannetje lopen terwijl de diagrammen verschijnen.	32
523	sk	MotionMan	Nau&#269;te sa rozumie&#357; grafom polohy, r&#253;chlosti a zr&#253;chlenia. Pomocou my&#353;ky pohybujte &#269;lovie&#269;ikom (MotionManom) sem a tam a sledujte grafy jeho pohybu. Alebo si nastavte poz&#237;ciu, r&#253;chlos&#357; alebo zr&#253;chlenie a dovo&#318;te po&#269;ita&#269;u, aby pohyboval MotionManom za V&#225;s.	32
524	sl	Gibanje človeka	Z gibanjem človeka odkriješ spreminjanje poti, hitrosti in pospeška.	32
525	uk	The Moving Man	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
526	el	Ο Κινούμενος Άνδρας	Μάθετε για τα γραφήματα θέσης, ταχύτητας και επιτάχυνσης. Μετακινήστε τον μικρό άνδρα μπροστά και πίσω με το ποντίκι και παραστήστε γραφικά την κινησή του. Καθορίστε τη θέση, την ταχύτητά του ή την επιτάχυνσή του και αφήστε την προσομοίωση να κινήσει τον άνδρα για εσάς. Μετάφραση: Βαγγέλης Κολτσάκης.	32
527	tn	Monna yo o tsamayang	Tiriso ya monna yo o tsamayang	32
528	de	Der bewegte Mann	Lernen Sie etwas über Weg-Zeit; Geschwindigkeits-Zeit und Beschleunigungs-Zeit-Diagramme. Bewegen Sie den kleinen Mann mit der Maus hin und her und verpassen Sie ihm eine Bewegung. Legen Sie Ort, Geschwindigkeit oder Beschleunigung fest und lassen Sie den kleinen Mann durch die Simulation bewegen.	32
529	ar	الرجل المتحرك	تعلم عن الرسوم البيانية للمكان ،للسرعة، للتسارع. حرك الرجل الصغير ذهابا وإيابا بالفأرة وارسم حركته. حدد المكان، السرعة والتسارع واسمح للمحاكاة أن تحرك الرجل لك.	32
530	pt	Movimento	Estude os gráficos de posição, velocidade e aceleração. Mova um pequeno boneco e registre seu movimento. Configure a posiçãp, velocidade e aceleração e deixe a simulação realizar a movimentação automática do boneco.	32
531	zh_CN	运动图像	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
532	hr	Gibanje	Nauči nešto o položaju, putu, brzini i akceleraciji! Pomakni čovječuljka mišem i pogledaj grafički prikaz njegova gibanja! Odaberi položaj, početnu brzinu ili akceleraciju čovječuljka i simulacije će napraviti zadano!	32
533	zh_TW	移動的人形	學習關於位置、速度和加速度圖表。以滑鼠來來回回地移動人形並繪製他運動的圖。設定位置、速度或加速動，並讓模擬程式為你移動圖上的人形。	32
534	en	The Moving Man	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
535	es	MRI Simplificado	Un modelo simplificado de la proyección de imagen de resonancia magnética	33
536	sk	Magnetická rezonancia	Princíp zobrazovania magnetickou rezonanciou	33
537	el	Μαγνητική Τομογραφία	Απλοποιημένο Μοντέλο Μαγνητικής Τομογραφίας	33
538	pt	MRI Simplificada	Um modelo simplificado para Imagem por Ressonância Magnética	33
539	nl	Vereenvoudigd MRI	Een vereenvoudigd model van beeldvorming door magnetische resonantie 	33
540	hr	Shema MRI	Jednostavan model snimanja magnetskom rezonancijom	33
541	pl	Uproszczony MRI	Uproszczony model magnetycznego rezonansu jądrowego	33
542	zh_TW	簡化的磁共振造影MRI	磁共振造影的簡化模型	33
543	en	Simplified MRI	A simplified model of magnetic resonance imaging	33
656	ar	العزم	تحقق كيف يسبب العزم دوران جسم. إكتشف العلاقات بين التسارع الزاوي وعزم القصور، وكمية الدفع الزاوي والعزم 	45
544	el	Οπτικός Κβαντικός Έλεγχος	Δημιούργησε έναν  παλμό, μεταβάλλοντας μεμονωμένα τα χρώματα του παλμού εισόδου. Δημιούργησε προσεκτικά το σωστό σχήμα παλμού για να διασπάσεις ένα μόριο. 	34
545	pt	Controle Quântico-Óptico	Construa uma forma de pulso de luz pela manipulação individual das cores de um pulso luminoso. Crie cuidadosamente a forma perfeita de pulso para desintegrar uma molécula.	34
546	ru	Оптический квантовый контроль	Изменяйте цвета светового импульса по отдельности. Точно воссоздайте форму имплульса чтобы разрушить молекулу.	34
547	es	Control Óptico De Quantum	Haga una forma de pulso del diseñador individualmente manipulando los colores de un pulso ligero. Cree cuidadosamente el pulso formado perfecto para romper aparte una molécula.	34
548	nl	Optisch manipuleren van lichtpulsen	Geef een lichtpuls de gewenste vorm door alle kleuren van de lichtpuls apart te manipuleren. Geef zo de lichtpuls de juiste vorm (=energie)  om de bindingen in het molocuul te verbreken.	34
549	hr	Kvantna optika	Ručno uredi puls. Pravim pulsom razbit ćete molekulu.	34
550	zh_TW	光量子控制	藉由逐個地操作光脈衝波的顏色，進行脈衝波波形的設計，謹慎地產生完美的脈衝波波形使得分子被打散。	34
551	en	Optical Quantum Control	Make a designer pulse shape by individually manipulating the colors of a light pulse. Carefully create the perfect shaped pulse to break apart a molecule.	34
552	el	Μοριακοί Κινητήρες	Ανακαλύψτε τι ελέγχει πώς γρήγοροι μικροσκοπικοί κινητήρες στο σώμα μας έλκουν μια μονή αλυσίδα DNA. Πόσο δυνατά μπορεί να "τραβήξει" ο κινητήραςαντιστεκόμενος στο τράβηγμα των οπτικών λαβίδων; Ανακαλύψτε τι τον ενισχύει. Συμπεριφέρονται όλοι οι μοριακοί κινητήρες με τον ίδιο τρόπο; 	35
553	pt	Molecular Motors	Discover what controls how fast tiny molecular motors in our body pull through a single strand of DNA. How hard can the motor pull in a tug of war with the optical tweezers? Discover what helps it pull harder. Do all molecular motors behave the same?	35
554	ru	Молекулярные моторы	Откройте, что управляет тем, как быстро миниатюрные молекулярные моторы в нашем теле тянут вдоль отдельную нить ДНК. Как сильно может тянуть  мотор в перетягивании с оптическим пинцетом? Узнайте, что помогает им тянуть сильнее. Все ли молекулярные моторы ведут себя одинаково?	35
555	es	Motores Moleculares	Descubre qué es lo que controla la rapidez con minúsculos motores moleculares en nuestro cuerpo a través de tirar un solo filamento de la ADN. ¿Qué tan difícil puede tirar el motor en un tira y afloja con el pinzas ópticas? Descubra lo que le ayuda a tirar con más fuerza. ¿Todos los motores moleculares comportarse de la misma manera?	35
556	nl	Moleculaire motors	Ontdek hoe moleculaire motors over een enkele DNA-streng bewegen. Laat de motor en het optisch pincet 'touwtje trekken'.\nOntdek hoe de motor harder kan trekken.\nGedragen alle moleculaire motors zich hetzelfde?	35
557	hr	Djelovanje enzima	Otkrijte kao sićišne molekule, enzimi, upravljaju velikom molekulom DNA.	35
558	zh_TW	分子馬達	由單一DNA鍵來拉伸微小的分子馬達要如何控制？分子馬達在光鑷中受到許多力量彼此拉鋸？分子馬達的動作是否相同？	35
559	pl	Silniki molekularne	Zobacz, jak można sterować maleńkim silnikiem molekularnym w ciele człowieka. Zobacz to na modelu pojedunczej nitki DNA.  Porównaj działanie silnika i "optycznej pensety". Zobacz, co działa silniej. Czy wszystkie silniki molekularne zacchowują się tak samo?	35
560	en	Molecular Motors	Discover what controls how fast tiny molecular motors in our body pull through a single strand of DNA. How hard can the motor pull in a tug of war with the optical tweezers? Discover what helps it pull harder. Do all molecular motors behave the same?	35
561	el	Τέντωμα DNA	Διερευνήστε το τέντωμα μιας μονής αλυσίδας DNA με οπτικές λαβίδες ή ροή ρευστού. Πειραματιστείτε με τις εμπλεκόμενες δυνάμεις και μετρήστε την απαιτούμενη δύναμη για να παραμείνει τεντωμένη. Το DNA συμπεριφέρεται περισσότερο σαν αλυσίδα ή σαν σκοινί;	36
562	pt	Stretching DNA	Explore stretching just a single strand of DNA using optical tweezers or fluid flow. Experiment with the forces involved and measure the relationship between the stretched DNA length and the force required to keep it stretched. Is DNA more like a rope or like a spring?	36
563	ru	Растяжение ДНК	Исследуйте растяжение отдельной нити ДНК под действием оптического пинцета или потока жидкости. Экспериментируйте с задействованными силами и измерьте соотношение между длиной растянутой ДНК и силой, требующейся для этого. Что сильнее напоминает ДНК - верёвку или пружину?	36
564	es	Estirar la DNA	Explora estiramiento de un solo filamento de la ADN utilizando pinzas ópticas o flujo de fluidos. Experimente con las fuerzas que intervienen y medir la relación entre la longitud estirada de ADN y la fuerza necesaria que le mantenga estirado. DNA es más parecido a una cuerda o como una primavera?	36
565	nl	DNA strekken	Onderzoek het strekken van een enkele streng DNA met een optisch pincet of in een vloeistofstroom. Verander de betrokken krachten en meet de relatie tussen de lengte van de streng DNA en de kracht die nodig is om het DNA te strekken. Gedraagt de streng zich als een draad of als een veer?	36
566	hr	Ispravljanje DNA	Otkrijte kao sićišne molekule, enzimi, upravljaju velikom molekulom DNA.	36
567	zh_TW	DNA拉長	探索使用光鑷或流力來來拉伸單鍵DNA。透過實驗來觀測力與DNA拉伸的長度之關係。DNA是否向一個繩子或彈簧？	36
568	pl	Rozciąganie DNA	Zobacz, jak można wydłużyć pojedynczą nitkę DNA uzywająć "pensety optyczne"j i przepływu cieczy. Zbadaj siły, jakie tu występują. Zmierz zależność między rozciągniętym DNA a siłami, które pozwalają na pozostawanie w tym stanie. Czy DNA przypomina bardziej linę czy sprężynę?	36
745	ru	Травольтаж	Изучение статического электричества	55
569	en	Stretching DNA	Explore stretching just a single strand of DNA using optical tweezers or fluid flow. Experiment with the forces involved and measure the relationship between the stretched DNA length and the force required to keep it stretched. Is DNA more like a rope or like a spring?	36
570	el	Οπτικές Λαβίδες και Εφαρμογές	Φανταστήκατε ποτέ ότι μπορείτε να χρησιμοποιήσετε φως για να μετακινήσετε ένα μικροσκοπικό πλασικό σφαιρίδιο; Διερευνήστε τις δυνάμεις στο σφαιρίδιο ή επιβραδύνετε το χρόνο για να παρατηρήσετε την αλληλεπίδραση με το ηλεκτρικό πεδίο του laser. Μπορείτε να κάνετε την αλυσίδα του DNA εντελώς ευθεία ή να σταματήσετε τον μοριακό κινητήρα;	37
571	pt	Pinça Óptica	Já imaginou alguma vez que você poderia utilizar a luz para mover uma bolinha microscópica de plástico e esticar uma única molécula de DNA? Explore as forças que atuam sobre uma amostra, uma pequena bolinha de plástico, utilizando a Pinça Óptica. Utilize a simulação em câmara lenta para visualizar as interações da amostra com o campo elétrico do Laser. Tente esticar uma cadeia de DNA com a sua Pinça Óptica - Você é capaz de esticá-lo completamente? 	37
572	ru	Оптические пинцеты и их применение	Вы когда-нибудь представляли себе, что свет можно использовать для передвижения микроскопического пластикового шарика? Исследуйте силы, действующие на шарик или замедлите время чтобы посмотреть на взаимодействие с электрическим полем лазера. Используйте оптические пинцеты чтобы управлять отдельной нитью ДНК и узнайте физику миниатюрных молекулярных моторов. Можно ли полностью выпрямить ДНК или остановить молекулярный мотор?	37
573	es	Pinzas Ópticas Y Sus Aplicaciones	¿Alguna vez imagino que usted puede utilizar para mover una luz plásticas microscópicas talón? Explora las fuerzas sobre el talón o de la lentitud de tiempo para ver la interacción con el láser del campo eléctrico. Utilice las pinzas ópticas de manipular un solo capítulo de la DNA y explorar la física de diminutos motores moleculares. Se puede obtener el DNA completamente recta o detener los motores moleculares?	37
574	nl	Optisch pincet (tweezers) en toepassingen van het pincet.	Heb je ooit bedacht dat je licht kunt gebruiken om microscopisch kleine plastic deeltjes te bewegen? Onderzoek de krachten die op het deeltje werken of vertraag de tijd om de interactie te zien tussen het deeltje en het elektrisch veld van een laser. \nGebruik het optisch pincet om een streng DNA vast te houden en te strekken. Onderzoek zo de krachten van moleculaire motors. \nKun je de streng helemaal recht krijgen of de motor stoppen?	37
575	hr	Optička pinceta i primjene	Jeste li znali da možete elektromagnetski pomicati sitne mikroskopske plastične probe.	37
576	zh_TW	光學鑷子及應用	你是否曾想像利用光來移動微小的塑膠粒子？利用光鑷來操作單鍵DNA並發現微小的分子馬達之物理現象。	37
577	pl	Optyczna penseta i jej zastosowania	Czy myślałeś kiedyś, że uda ci się przesunąć mikroskopijne podłoże oglądane pod mikroskopem przy pomocy światła? Zbadaj działające siły; możesz spowolnić czas, by dokładniej zapoznać się ze zjawiskiem - dzialanie pola elektrycznego wiążki lasera. Użyj "optycznej pensety" do rozciągania molekuly DNA. Czy możesz ją całkowicie wyprostować?	37
578	en	Optical Tweezers and Applications	Did you ever imagine that you can use light to move a microscopic plastic bead? Explore the forces on the bead or slow time to see the interaction with the laser's electric field. Use the optical tweezers to manipulate a single strand of DNA and explore the physics of tiny molecular motors. Can you get the DNA completely straight or stop the molecular motor?	37
579	es	Escala de pH	Ensaye el pH de cosas tales como café, saliva y jabón, para determinar si son ácidas, básicas o neutras. Visualise las cantidades relativas de iones hidróxido e hidronio en solución. Investigue si el cambiar el volumen o diluir con agua afecta al pH. ¡O puede diseñar su propio líquido!	38
580	fr	Echelle de pH	Testez le pH de liquide comme le café, le crachat ou le savon et déterminer pour chacun s'il est acide, basique, ou neutre. Visualisez le nombre relatif d'ions hydroxyde et d'ions hydronium en solution. Commutez entre les échelles logarithmiques et linéaires. Étudiez l'impact sur le pH d'une variation de volume ou de la dilution. Ou alors particularisez  votre propre liquide !	38
581	fi	pH asteikko	Testaa ph erilaisista liuoksista kuten kahvi, sylki ja saippua, päättele ovatko luokset neutraaleja, happoja vai emäksiä. Tarkastele hydroksini ja hydroniumionien määrää liuoksessa. Vertaa logaritmisiä ja lineeaarisia asteikkoja. Testaa miten liuoksen laimennos vaikuttaa pH:on.	38
582	ru	шкала pH	Протестируйте pH разных веществ, таких как кофе, слюна и мыло, чтобы определить щелочные они, кислые или нейтральные. Визуализируйте относительное число ионов гидроксида и ионов водорода, соединенный с молекулой воды в растворе. Переключайтесь между логарифмическим и линейным масштабами. Исследуйте, как изменение объёма и разбавление водой влияет на pH. Или создайте свою жидкость!	38
583	ar	الأس الهيدروجيني 	اختبر الأس الهيدروجيني لأشياء مثل القهوة، اللعاب، والصابون لتحدد الحمض و القاعدة و المتعادل. تصور العدد النسبي لأيونات الهيدروكسيد وأيونات الهيدرونيوم في المحلول. بدل بين المقياس الخطي والمقياس اللوغاريتمي. حدد ماإذا كان تغيير الحجم أو تخفيف التركيز بالماء يؤثر على الأس الهيدروجيني. أو بإمكانك تصميم واختيار السائل الذي تريده.	38
584	nl	pH-schaal	Onderzoek of de zuurgraad (pH) van stoffen als koffie, speeksel en zeep. Zijn de oplossingen van deze stoffen zuur of basisch?\nOnderzoek of de zuurgraad verandert als je meer of minder vloeistof neemt of als je de oplossing verdunt.\nLaat zien in welke verhouding H<sub>3</sub>O<sup>+</sup> en OH<sup>-</sup> ionen in de oplossingen voorkomen. \nMaak een nieuwe vloeistof!	38
614	el	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
838	in	faradays-law	\N	63
585	pl	skala pH	Sprawdź pH płynów: kawy, śliny i mydła aby określić czy są kwaśne, zasadowe lub obojętne. Zmierz względną liczbę jonów hydroksylowych i wodorowych w roztworze. Przełącz się między skalą logarytniczną i linową. Zbadaj czy zmiana objętości lub rozcieńczanie wodą wpływa na pH. Stwórz swój własny płyn !	38
586	pt_BR	Escala de pH	Teste o pH de materiais como café, cuspe, e sabonete para determinar se são ácidos, alcalinos ou neutros. Visualize o número relativo de íons hidróxido e hidrônio em solução. Alterne em as escalas logarítmica e linear. Investigue como a mudança de volume ou diluição em água afeta o pH. Ou invente um líquido qualquer!	38
587	zh_TW	pH 刻度	檢查咖啡、汽水及肥皂的pH值決定其是否為酸性、鹼性或中性。視覺化的觀查溶液中氫氧根離子及氫離子的比例。並用對數刻度及線性刻度來呈現。改變溶液體積及稀釋溶液是否影響pH值。或者也可選用你指定的液體來做模擬實驗。	38
588	hr	pH	Istraži kiselost (pH) stvari kao što je kava, sapun, pljuvačka i sl. Vizualiziraj relativni broj hidronij i hidroksid iona u otopini.	38
589	el	Κλίμακα pH	Ελέγξτε το pH διαφόρων υλικών, όπως ο καφές, το σάλιο και το σαπούνι για να εξακριβώσετε εάν καθένα από αυτά είναι όξινο, αλκαλικό ή ουδέτερο. Οπτικοποιήστε τους σχετικούς αριθμούς των ιόντων του υδροξειδίου και του υδρονίου. Κάντε εναλλαγές μεταξύ λογαριθμικής και γραμμικής κλίμακας. Διερευνήστε εάν η αλλαγή του όγκου ή η διάλυση με νερό επηρεάζει το pH. Ή μπορείτε να δημιουργήσετε το δικό σας υγρό! 	38
590	et	pH skaala	Katseta, milline on erinevate vedelike pH (kas happeline, aluseline või neutraalne). Vaata kui palju on arvuliselt lahuses hüdroksiid- ja hüdrooniumioone. Vaatle graafikuid logaritmiliselt ja lineaarselt. Leia, kas vee lisamine või vähendamine muudab lahuse pH-d. Sul on võimalus luua ka enda lahus!	38
591	en	pH Scale	Test the pH of things like coffee, spit, and soap to determine whether each is acidic, basic, or neutral. Visualize the relative number of hydroxide ions and hydronium ions in solution. Switch between logarithmic and linear scales. Investigate whether changing the volume or diluting with water affects the pH. Or you can design your own liquid!	38
592	pt	O Efeito Fotoeléctrico	See how light knocks electrons off a metal target, and recreate the experiment that spawned the field of quantum mechanics.	39
593	fa	اثر فوتوالکتریک	نمایش فرود نور بر فلز هدف و خلق الکترونهای فوتوالکتریک با کندن آنها از سطح فلز و نیز نمایش آزمایشی برای بسط گستره مکانیک کوانتم.	39
594	nl	Fotoelektrisch effect	Kijk hoe licht elektronen uit een metalen plaat vrijmaakt en herbeleef het experiment waarmee de quantummechanica een vliegende start maakte.	39
595	es	El Efecto Fotoeléctrico	Observa como la luz hace desprender electrones de una muestra metálica, y recrea el experimento que dió origen al campo de la mecánica cuántica.	39
596	de	Der Photoelektrischer Effekt	Sehen wie licht brechen dem Elektronen ab von eines metallisches Target, und  erfrischen sie das Experiment der hat den Quantenmechanischefeld erzuegt.	39
597	sk	Fotoelektrický jav	Sledujte ako dopadajúce svetlo uvoľňuje elektróny z kovovej platne 	39
598	hr	Fotoelektrični efekt	Mehanizam kako svjetlost izbacije elektrone iz metala ocrtava eksperiment koji je otvorio put razvoju kvantne mehanike.	39
599	hu	Fotoeffektus	Figyeld, ahogy a fény elektronokat üt ki a céltárgyként használt fémből. Reprodukáld a kvantummechanika megszületését kiváltó kísérletet.	39
600	zh_TW	光電效應	觀察電子是如何被撞擊離開金屬標靶，並且重現大量產生量子力場的實驗。	39
601	pl	Efekt fotoelektryczny	Zobacz, jak fotony uderzają fotokatodę i wybijają z niej elektrony. Objaśnij wyniki doświadczenia nagruncie mechaniki kwantowej.	39
602	en	The Photoelectric Effect	See how light knocks electrons off a metal target, and recreate the experiment that spawned the field of quantum mechanics.	39
603	pt	Tunelamento Quântico	<html>Observe "partículas quânticas" tunelando através de barreiras de energia.<br>Explore as propriedades das funções de onda que descrevem estas partículas.<br></html>	40
604	ru	Квантовое туннелирование и волновые пакеты	Пронаблюдайте туннелирование квантовых "частиц" через барьер. Исследуйте свойства волновых функций, описывающих эти частицы.	40
605	es	Penetración Mecánico-Cuántica y Paquete de Ondas	Mire las partículas cuánticas penetrar las barreras. Explore las características de las funciones de onda que describen estas partículas.	40
606	el	Φαινόμενο Σήραγγας και Κυματοδέματα	Παρατηρήστε το φαινόμενο σήραγγας κβαντομηχανικών "σωματιδίων". Διερευνήστε τις ιδιότητες των κυματοσυναρτήσεων που περιγράφουν αυτά τα σωματίδια.	40
607	mn	Квант тунель ба Долгионы багцууд	Watch quantum "particles" tunnel through barriers. Explore the properties of the wave functions that describe these particles.	40
608	nl	Kwantumtunnelling en Golfdeeltjes	Let op hoe 'kwantumdeeltjes' door barrieres gaan.\nOnderzoek de eigenschappen van golffuncties die de eigenschappen van deze deeltjes beschrijven.	40
609	hr	Kvantno tuneliranje i valni paketi	Otkrijte kako kvanti tuneliraju kroz energijsku barijeru. Izučite kako valnom funkcijom možemo objasniti ovaj fenomen.	40
610	hu	Alagúthatás és hullámcsomagok	Figyeld, ahogy a "kvantumrészecskék" áthatolnak az akadályokon. Tanulmányozd a részecskéket leíró hullámfüggvényeket. 	40
611	pl	Tunelowanie kwantowe i paczki falowe	Obserwuj jak cząstka przechodzi przez bariery potencjału (tunelowanie). Zbadaj właściwosci funkcji falowej, która opisuje zachowanie się cząstki.	40
612	en	Quantum Tunneling and Wave Packets	Watch quantum "particles" tunnel through barriers. Explore the properties of the wave functions that describe these particles.	40
613	sk	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
657	nl	Koppel	Onderzoek hoe het moment een voorwerp laat roteren. Ontdek de relatie tussen hoekversnelling, traagheidsmoment, hoekmoment en koppel.	45
615	pt	Experimento de Davisson-Germer	Simulação da experiência original que provou que eletrons podem comportar uma onda. Assista a difração de elétrons em uma estrutura cristalina, com auto-interferência  para criar picos e vales, definindo franjas de probabilidade de ocorrência.	41
616	ru	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
617	es	Experimento Davisson-Germer	Simula el experimento original que probó que los electrones pueden comportarse como ondas. Observe a los electrones difractarse en un cristal de átomos, interfiriendose con ellos mismos para crear picos y valles de probabilidad.	41
618	nl	Davisson-Germer Experiment	Simuleer het experiment dat bewees dat elektronen zich bewegen als golven. Zie de diffractie van elektronen van een kristal, hoe ze met elkaar interfereren en zo waarschijnlijkheidspieken en -dalen vormen.	41
619	hr	Eksperiment Davissona i Germera	Simulacija originalnog eksperimenta koji pokazuje da se elektroni mogu ponašati kao valovi. Promatramo  difrakciju elektrona na čvornim atomima kristalne rešetke!	41
620	pl	Doświadczenie Davisson-Germera	Przeprowadż symulacje doświadczenia, które dowodzi, że elektron może zachowywać się, jak fala. Obserwuj dyfrakcję elektronów na krysztale, wzajemną interferencję, zobacz stosowne prawdopodobieństwa	41
621	en	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
622	sk	Quantum Wave Interference	Quantum Wave Interference	42
623	el	Quantum Wave Interference	Quantum Wave Interference	42
624	pt	Interferência Quântica	Interferência Quântica	42
625	ru	Quantum Wave Interference	Quantum Wave Interference	42
626	es	Interferencia de Onda Cuántica	Interferencia de Onda Cuántica	42
627	nl	Kwantum golfinterferentie	Kwantum golfinterferentie	42
628	hr	Kvantna interferencija valova	Kvantna interferencija valova	42
629	pl	Kwatowa interferencja fal	Kwatowa interferencja fal	42
630	en	Quantum Wave Interference	Quantum Wave Interference	42
631	hu	Rádióhullámok	Annak vizsgálata, hogyan keletkeznek az elektromágneses hullámok, hogyan mozognak a térben és milyen hatásaik vannak.	43
632	sk	Rádiové vlny	Ukážka ako sa tvoria elektromagnetické vlny, ako sa šíria priestorom, a aký majú účinok.	43
633	pt	Ondas de Rádio	Uma exploração de como as ondas eletro-magnéticas são criadas, como se movimentam através do espaço, e seus efeitos.	43
634	tr	Radyo dalgaları	Elektromanyetik dalgaların nasıl üretiler, hava ortamında nasıl ilerler ve diğer etkilerini açıklayıcı simülasyon	43
635	es	Ondas del radio	Una exploración de cómo se crean las ondas electromagnéticas, cómo se mueven a través de espacio, y sus efectos	43
636	nl	Radiogolven	Radiogolven zijn elektromagnetische golven. \nHoe ontstaan deze goven? \nHoe verplaatsen deze golven zich? 	43
637	it	Onde Radio	Come sono generate le onde radio in una antenna? Come si propagano le onde radio? Che effetti hanno le onde sulle antenne riceventi?	43
638	ro	Undele Radio	O explorare a modului de creare a undelor electro-magnetice, cum se misca prin spatiu si efectele lor	43
639	in	Gelombang Radio	Mengeksplorasi bagaimana medan elektromagnetik dihasilkan, bagaimana perambatannya dalam ruang, dan efeknya	43
640	hr	Radio Valovi	Primjer kako nastaju i kako se prostiru elektromagnetski valovi	43
641	zh_TW	無線電波	電磁波如何產生，及其傳輸方式與效應	43
642	pl	Fale radiowe	Badanie pola elektromagnetycznego. Jak jest wytwarzane, jak porusza się w przestrzeni i jakie są jego efelty/	43
643	en	Radio Waves	An exploration of how electro-magnetic waves are created, how they move through space, and their effects	43
644	el	Αντιδράσεις και Ταχύτητα Αντίδρασης	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
645	pt	Reações & taxas	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
646	es	Reacciones y Velocidades	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
647	fr	Réaction et cinétique	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
648	nl	Reacties en reactiesnelheid	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
649	ar	التفاعلات وسرعتها(معدلها)	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
650	zh_TW	化學反應和反應速率	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
651	pl	Reakcje i ich szybkości	Co powoduje rozpoczęcie reakcji? Znajdź co i jak wpływa na szybkośc reakcji? Przeprowadż własne doświadczenia; zbierz dane i oblicz współczynnik szybkości reakcji. Wypróbuj różne reakcje, stężenia i energie.	44
652	en	Reactions & Rates	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
653	pt	Torque	Simulação de Torque	45
654	hu	Forgatónyomaték	Vizsgálja meg, hogyan készteti forgásra a nyomaték a testeket. Állapítsa meg a szöggyorsulás, tehetetlenségi nyomaték és a nyomatékok kapcsolatát.	45
655	es	Torsión	Investigue cómo la torsión provoca la rotación de un objeto. Descubra las relaciones entre la aceleración angular, el momento de inercia, el momento angular y la torsión.	45
658	hr	Moment sile	Zašto tijela rotiraju. Otkrijte odnose brzine i kutne brzine, kutne akceleracije i momenta tromosti...	45
659	zh_TW	力矩	研究力矩如何使一物體轉動，發現角加速度、轉動慣量、角動量和力矩之間的關係。	45
660	en	Torque	Investigate how torque causes an object to rotate.  Discover the relationships between angular acceleration, moment of inertia, angular momentum and torque.	45
661	pt	Revolução da joaninha	Junção da joaninha em uma exploração de movimento rotacional. Rotacione o carrosel para mudar seu ângulo, ou escolha uma velocidade angular constante ou aceleração angular. Explore como movimento circular relaciona o inseto com as posições x,y, velocidade e aceleração usando vetores ou gráficos.	46
662	hu	Katicabogár forgás	Használja a katicabogarat a körmozgás vizsgálatára. Forgassa meg a korongot vagy válasszon állandó sebességet ill. gyorsulást. Vizsgálja meg a bogár x,y elmozdulását, sebességét, gyorsulását és használja a vektorokat ill. grafikonokat.	46
663	es	Revolución Mariquita	Únase a la mariquita en una exploración al movimiento de rotaición. Haga rotar el giradiscos al cambiar su ángulo, o escogiendo una velocidad angular constante o determinada aceleración angular. Explore el movimiento circular viendo su relación con la posición x,y del bicho, la velocidad y la aceleración usando vectores o diagramas.	46
664	ar	دوران الخنفساء	رافق الخنفساء الصغيرة في إستكشاف الحركة الدورانية. أدر لعبة الدوامة لتغيير زاويتها، أو اختر سرعة زاويّة ثابتة أو تسارع زاويّ.  استكشف كيف ترتبط الحركة الدائرية بموقع وسرعة وتسارع  الخنفساء باستخدام المتجهات أو الرسم البياني	46
665	nl	Lieveheersbeestje-draaien	Onderzoek met het lieveheersbeestje de draaibeweging. Laat de draaimolen draaien met verschillende hoeksnelheden en hoekversnellingen.\nOnderzoek hoe de draaibeweging zich verhoudt tot het insects x- en y-positie, snelheid en versnelling. Gebruik daarbij de vectoren en de diagrammen.	46
666	hr	Luna park za bube i žohare	Pridružite se raznim bubama u njihovom svjetovima (koordinatnim sustavima) i otkrijte kako oni vide svoj svjet i koje sile osjećaju.	46
667	zh_TW	旋轉的瓢蟲	參與瓢蟲在旋轉移動的探索。轉動旋轉木馬改變它的角度，或指定一定值的角速度或角加速度。使用向量或圖表，探索圓週運動和甲蟲的x,y位置、速度與加速度的關係。	46
668	en	Ladybug Revolution	Join the ladybug in an exploration of rotational motion.  Rotate the merry-go-round to change its angle, or choose a constant angular velocity or angular acceleration.  Explore how circular motion relates to the bug's x,y position, velocity, and acceleration using vectors or graphs.	46
669	nl	Rutherford verstrooiing	Ook Rutherford kon geen atomen zien. Toch ontdekte hij met zijn beroemd  geworden experiment de atoomkern.  Hoe deed hij dat?\nMet deze simulatie doe jij Rutherford's  beroemde experiment na en ontdek je ook dat bijna alle massa van het atoom in de heel kleine kern moet zitten. Een atoom zou wel een  vreemde krentenbol zijn: al het brood in een klein puntje en de krenten daar ver vandaan!\nHet model van een atoom als een kleine kern en elektronen op grote afstand daaromheen noem je het atoommodel van Rutherford.	47
670	hu	Rutherford-szórás	Hogyan jött rá Rutherford, hogy az atomnak magja van, anélkül, hogy belelátott volna? Szimuláld a híres kísérletet, mely bebizonyította, hogy az atom mazsoláspuding-modellje helytelen. Az alfa-részecskék olykori eltérülése ugyanis azt jelezte, hogy az atomnak apró, de nehéz magja lehet, pozitív töltéssel. (Az "olykori" valójában nagyon ritka eseményt jelent, hiszen a jelentősebb eltérülések "ablaka", melyet a Rutherford-atom animációja kitár előttünk, 1/1000 része sincs az atom átmérőjének.) Az atommag kiindulási összetétele (az egykori mérések emlékére) az Au-197 nuklidét tükrözi, mely az arany egyetlen stabil izotópja.	47
671	sk	Rutherfordov pokus 	Ako Rutherford určil existenciu atómového jadra?  Simuláciou slávneho pokusu, ktorý vyvrátil správnosť pudingového modelu atómu, zistíte že alfa častice sa odrážajú od atómu tak, ako by mal malé jadro.	47
672	vi	Tan xa Rutherford	Rutherford đã khám phá ra hạt nhân nguyên tử như thế nào? Các hạt nhân alpha khi được bắn vào các nguyên tử thì bị chệch hướng, chứng tỏ nguyên tử có một lõi tích điện dương. Điều này bác bỏ giả thuyết cho rằng nguyên tử có dạng như chiếc bánh Pudding nho.	47
673	uk	Резерфордівське розсіяння	How did Rutherford figure out the structure of the atomic nucleus without looking at it?  Simulate the famous experiment in which he disproved the Plum Pudding model of the atom by observing alpha particles bouncing off atoms and determining that they must have a small core.	47
674	el	Σκέδαση Rutherford	Πως ανακάλυψε ο Rutherford τη δομή του πυρήνα του ατόμου χωρίς να τον βλέπει; Προσομοιώστε το περίφημο πείραμα με το οποίο κατέρριψε το ατομικό  μοντέλο του "Σταφιδόψωμου" συμπεραίνοντας ότι τα άτομα πρέπει να έχουν πυρήνα, παρατηρώντας σωμάτια άλφα να εκπέμπονται  από τα άτομα.	47
675	pt	Dispersão de Rutherford	Como Rutherford descobriu a estrutura do núcleo atômico sem vê-lo?  Simule o famoso experimento no qual ele não dispunha do modelo atômico pudim de ameixas para observar as particulas alpha saltitantes dos átomos.	47
676	es	Dispersión de Rutherford	¿Cómo descubrió Rutherford  la estructura del núcleo atómico sin mirarlo? Simule el famoso experimento en el cual refutó el modelo de budín de ciruelas del átomo, observando partículas alfa rebotando de átomos y determinando que estos deben tener un núcleo pequeño.	47
677	hr	Rutherfordovo raspršenje	Kako znamo da atomi izgledaju upravo onako nako nas uče: mala kompaktna pozitivno nabijena jezgra koja nosi gotovo svu masu atoma i veliki elektronski oblak oko nje u kojem se nalaze lagani negativno nabijeni elektroni. Rutherford je eksperimentalno utvrdio ovu sliku! Ideju eksperimenta dočarava i ova simulacija.	47
678	zh_TW	拉塞福散射實驗	拉塞福是如何在看不見原子的情況下找出原子具有原子核的結構？這裡模擬了拉塞福著名的實驗，他觀察alpha 柆子撞擊原子而推翻了李子布丁原子模型確立了原子核模型。	47
679	pl	Doświadczenie Rutherforda 	Jak Rutheerford odkrył budowę atomu bez mozliwości zajrzenia do jego wnętrza? Przeprowadż symulacje słynnego doświadczenia. Pokaż, że model atomu- ciasta ze śliwkami nie odpowiada rzeczywistości. Zobacz, jak wyniki dowodzą, że atom ma bardzo male ( w porównaniu z całościa ) jądro.	47
742	zh_TW	Sim1 Name (zh_TW)	Sim1 Description (zh_TW)	54
743	el	Sim1 Name (el)	Sim1 Description (el)	54
680	en	Rutherford Scattering	How did Rutherford figure out the structure of the atomic nucleus without looking at it?  Simulate the famous experiment in which he disproved the Plum Pudding model of the atom by observing alpha particles bouncing off atoms and determining that they must have a small core.	47
681	pt	Modelo de auto-impulso da partícula	Tutorial do modelo de auto-impulso da partícula	48
682	es	Modelo de Partículas Auto-impulsadas (DSWeb)	Tutorial de Modelo de Partículas Auto-impuladas (DSWeb)	48
683	nl	Zelf aangedreven deeltjesmodel (DSWeb)	Tutorial voor Zelf aangedreven deeltjesmodel (DSWeb)	48
684	zh_TW	自驅式粒子模型(DSWeb)	教導自驅式粒子模型(DSWeb)	48
685	en	Self-Driven Patricle Model (DSWeb)	Self-Driven Patricle Model Tutorial (DSWeb)	48
686	el	Semiconductors	Προσθέστε προσμίξεις στον ημιαγωγό για να δημιουργήσετε μια δίοδο ή τρανζίστορ. Παρακολουθείστε τα ηλεκτρόνια να μεταβάλλουν τη θέση και την ενέργειά τους.	49
687	pt	Semicondutores	Dose o semicondutor para criar um diodo ou transistor. Veja a posição da carga dos elétrons e a energia.	49
688	es	Semiconductores	Semiconductores	49
689	ro	Semiconductori	Dopeaza semiconductorul pentru a crea o dioda sau un tranzistor. Urmareste cum electronii isi schimba pozitia si energia.	49
690	nl	Halfgeleiders	Doteer het halfgeleidermateriaal (zuiver Si, paars gebied) met dopants (e-doner voor N-type en e-acceptor voor P=type) en maak een diode of transistor. Let op stroom, de spanning en hoe de elektronen van plaats en energie veranderen.	49
691	hr	Poluvodiči	Otkrij koncept poluvodiča.Dopriaj poluvodič i konstruiraj diodu ili tranzistor.	49
692	pl	Półprzewodniki	Wprowadź domieszki do półprzewodnika w celu utworzenia diody lub tranzystora. Obserwuj jak elektrony zmieniają swoje położenie oraz energię	49
693	zh_TW	半導體	添加半導體以製作二極體或電晶體，並觀察電子改變位置與能量。	49
694	en	Semiconductors	Dope the semiconductor to create a diode or transistor. Watch the electrons change position and energy.	49
695	el	Σήμα - Κύκλωμα	Διερεύνηση της μετάδοσης σημάτων σε ηλεκτικά κυκλώματα	50
696	pt	Sinais de circuito	Uma exploração de como os sinais de circuito trabalham	50
697	es	Signal Circuit	An exploration of how signal circuits work	50
698	de	Signal-Stromkreis	Eine Erklärung wie Signal-Stromkreise funktionieren	50
699	nl	Stroomkirng	Onderzoek hoe het komt dat een lamp direct brandt als je een schakelaar aanzet.	50
700	sk	Signál v obvode	An exploration of how signal circuits work	50
701	uk	Signal Circuit	An exploration of how signal circuits work	50
702	ro	Semnal Circuit	Explorarea modului de functionare a semnalului circuitelor	50
703	hr	Struja i signal	Kako se signal propagira kroz električne voiče!	50
704	pl	Obwód elektryczny	Badanie działania prostego obwodu elektrycznego	50
705	zh_TW	信號電路	探索信號電路如何產生作用	50
706	en	Signal Circuit	An exploration of how signal circuits work	50
707	ru	Растворимость солей	Сильно и слаборастворимые соли и как они соотносятся с коэффициентом растворимости Ksp	51
708	es	Sales y Solubilidad	Sales muy y poco solubles, cómo se relacionan con Kps.	51
709	nl	Zouten en Oplosbaarheid	Goed en matig oplosbare zouten: wat is hun relatie met Ks?	51
710	it	Sali & Solubilità	Sali altamente e debolmente solubili. Prodotto di solubilità Kps.\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	51
711	el	Άλατα & Διαλυτότητα	Ευδιάλυτα και Δυσδιάλυτα άλατα, πως σχετίζονται με το Ksp	51
712	pt	Sais e Solubilidade	Sais muito e ligeiramente solúveis, e como eles se relacionam com Ks	51
713	fr	Solubilité et sel	Sels légèrement et hautement solubles (leur rapport avec Ksp.)	51
714	ar	الأملاح والذوبانية	أملاح عالية وقليلة الذوبان، كيف تتعلق بقيمة منتج الذوبان Ksp	51
715	pl	Sole i rozpuszczalność	Sole dobrze i słabo rozpuszczalne, wspólczynniki rozpuszczlności K(sp)	51
716	pt_BR	Sais e Solubilidade	Sais muito e pouco solúveis, e como eles se relacionam com Kps	51
717	hr	Soli i njihova topljivost	Topljivost soli ...	51
718	zh_TW	鹽類和溶解度	可溶及微溶性盬類和 Ksp 的關係。	51
719	et	Soolade lahustumine	Kuidas eristada hästi- ja vähelahustuvaid soolasid.	51
720	en	Salts & Solubility	Highly- and slightly-soluble salts, a how they relate to Ksp.	51
721	es	Ondas Acusticas	Cómo las ondas acústicas trabajan y se oyen	52
722	ga	Tonnta Fuaime	An bealach an oibríonn tonnta fuaime agus a caoi a cloistear iad	52
723	et	Helilained	Kuidas helilained levivad ja on kuuldavad	52
724	nl	Geluidsgolven	Eigenschappen van geluidsgolven en het horen van geluid	52
725	sk	Zvukové vlny	Ako sa zvukové vlny šíria a ako ich počujeme.	52
726	uk	Sound Waves	How sound waves work and are heard	52
727	pt	Ondas Sonoras	Como as ondas sonoras trabalham e como são ouvidas.	52
728	it	Onde sonore	Come funzionano e come si sentono le onde sonore	52
729	el	Ηχητικά κύματα	Πως συμπεριφέρονται τα ηχητικά κύματα και πως ακούγονται	52
730	fr	Ondes sonores	Comment se déplacent et sont perçues  les ondes sonores	52
731	hr	Zvuk	Kao čujemo zvučne valove	52
732	in	Gelombang Suara	Bagaimana gelombang suara bekerja dan dapat didengar	52
733	zh_TW	聲波	聲音是如何產生作用和被聽見的	52
734	pl	Fale dźwiękowe	Fale dźwiękowe i słyszenie	52
735	ko	음파	음파는 어떻게 작용하고, 어떻게 들려 질까요?	52
736	en	Sound Waves	How sound waves work and are heard	52
737	ak	Sim2 Name (el)	Sim2 Description (el)	53
738	zh_TW	Sim2 Name (zh_TW)	Sim2 Description (zh_TW)	53
739	el	Sim2 Name (el)	Sim2 Description (el)	53
740	en	Sim2 Name	Sim2 Description	53
741	ak	Sim1 Name (el)	Sim1 Description (el)	54
746	es	Travoltaje	Una exploración de la electricidad estática.	55
747	et	Travoltage	Staatilise elektri uurimine.	55
748	nl	Travolta - statische elektriciteit	Een simulatie van statische elektriciteit	55
749	sk	John Tra-volt	Skúmanie statickej elektriny.	55
750	el	Travoltage	Διερεύνηση του στατικού ηλεκτρισμού	55
751	pt	Travoltagem	Uma explicação da eletriciadade estática	55
752	ro	Travoltaj	O explorare a electricitatii statice.	55
753	fr	Travolt ... aahh	A la découverte de l'électricité statique	55
754	hu	Travoltage	Az elektrosztatika tanulmányozása	55
755	hr	Johntra Volta	Koncept nastanka statičkog elektriciteta!	55
756	pl	Jasio Woltuś	Elektryczność statyczna	55
757	zh_TW	Travoltage	靜電的探索	55
758	en	Travoltage	An exploration of static electricity.	55
759	sr	Interferencija talasa	Симулација интерференције таласа	56
760	tr	Wave Interference	Wave Interference Simulation	56
761	ru	Интерференция волн	Модель интерференции волн	56
762	es	Interferencia De la Onda	Simulación De Interferencia De la Onda	56
763	de	Wave Interference	Wave Interference Simulation	56
764	sv	Våginterferens	Simulering av våginterferens	56
765	nl	Golven: interferentie	Simulatie simuleert in terferentie van golven	56
766	it	Interferenza di onde	Simulazione dell'interferenza di onde sull'acqua, onde sonore e luce.	56
767	sk	Interferencia vlnenia	Simulácia interferencie vlnenia	56
768	iw	Wave Interference	Wave Interference Simulation	56
769	uk	Wave Interference	Wave Interference Simulation	56
770	pt	Interferência de Onda	Simulação de Interferência de Onda	56
771	el	Συμβολή κυμάτων	Προσομοίωση συμβολής κυμάτων	56
772	cs	Vlnová interference	Simulace vlnové interference	56
773	hr	Interferencija valova	Simulacija Interferencije Valova	56
774	zh_TW	水波干涉	水波干涉模擬	56
775	pl	Interferencja fal	Symulacja interferencji fal	56
776	ar	تداخل الموجة	تمثيل تداخل الموجة	56
777	ko	파동의 간섭	파동의 간섭 시뮬레이션	56
778	en	Wave Interference	Wave Interference Simulation	56
779	en	Arithmetic	Remember your multiplication tables? ... me neither. Brush up on your multiplication, division, and factoring skills with this exciting game. No calculators allowed!	57
780	es	Aritmética	\N	57
781	nl	Rekenen	\N	57
782	ar	arithmetic	\N	57
783	hr	Aritmetika	\N	57
784	el	Αριθμητικός	\N	57
785	zh_TW	算數	\N	57
786	pl	Arytmetyka	\N	57
787	pt_BR	blackbody-spectrum	\N	58
788	en	Blackbody Spectrum	How does the blackbody spectrum of the sun compare to visible light? Learn about the blackbody spectrum of the sun, a light bulb, an oven, and the earth. Adjust the temperature to see the wavelength and intensity of the spectrum change. View the color of the peak of the spectral curve.	58
789	es	blackbody-spectrum	\N	58
790	it	blackbody-spectrum	\N	58
791	nl	blackbody-spectrum	\N	58
792	vi	blackbody-spectrum	\N	58
793	sk	blackbody-spectrum	\N	58
794	hr	Zračenje crnog tijela	\N	58
795	zh_TW	黑體幅射光譜	\N	58
796	pl	Spektrum ciała doskonale czarnego	\N	58
797	el	charges-and-fields	\N	59
798	en	Charges and Fields	Move point charges around on the playing field and then view the electric field, voltages, equipotential lines, and more. It's colorful, it's dynamic, it's free.	59
799	es	charges-and-fields	\N	59
800	nl	charges-and-fields	\N	59
801	pt_BR	charges-and-fields	\N	59
802	sk	charges-and-fields	\N	59
803	ro	Sarcini si Campuri Electrice	\N	59
804	ar	الشحنات والمجالات	\N	59
805	hr	Naboj i Električno polje	\N	59
806	zh_TW	電荷及電場	\N	59
807	pl	Ładunki i pola elektryczne	\N	59
808	nl	Functie van een grafiek bepalen	\N	60
809	en	Curve Fitting	With your mouse, drag data points and their error bars, and watch the best-fit polynomial curve update instantly. You choose the type of fit: linear, quadratic, cubic, or quartic. The reduced chi-square statistic shows you when the fit is good. Or you can try to find the best fit by manually adjusting fit parameters.	60
810	es	curve-fitting	\N	60
811	pt_BR	curve-fitting	\N	60
812	hr	Prilagodba podataka polinomom	\N	60
813	zh_TW	Curve Fitting 曲線配適	\N	60
814	pl	Krzywa dopasowania	\N	60
815	nl	Grafieken van vergelijkingen tekenen	\N	61
816	en	Equation Grapher	Learn about graphing polynomials. The shape of the curve changes as the constants are adjusted. View the curves for the individual terms (e.g. y=bx ) to see how they add to generate the polynomial curve.	61
817	es	equation-grapher	\N	61
818	si	equation-grapher	\N	61
819	vi	equation-grapher	\N	61
820	pt_BR	equation-grapher	\N	61
821	hr	Kvadratna jednadžba	\N	61
822	sq	grafiku i ekuacionit	\N	61
823	zh_TW	Equation Grapher 方程式繪圖	\N	61
824	pl	Kreator wykresu równania	\N	61
825	en	Estimation	Explore size estimation in one, two and three dimensions! Multiple levels of difficulty allow for progressive skill improvement.	62
826	es	Estimación	\N	62
827	pt_BR	estimation	\N	62
828	nl	Schatting	\N	62
829	hr	Procjena	\N	62
830	pl	Oszacowania	\N	62
831	zh_TW	估計	\N	62
832	da	faradays-law	\N	63
833	en	Faraday's Law	Light a light bulb by waving a magnet. This demonstration of Faraday's Law shows you how to reduce your power bill at the expense of your grocery bill.	63
834	es	faradays-law	\N	63
835	nl	faradays-law	\N	63
836	vi	faradays-law	\N	63
837	ro	Legea lui Faraday	\N	63
839	zh_CN	法拉第电磁感应定律	\N	63
840	hr	Faradayev zakon indukcije	\N	63
841	pl	Prawo Faradaya	\N	63
842	zh_TW	法拉第定律	\N	63
843	en	Friction	Learn how friction causes a material to heat up and melt. Rub two objects together and they heat up. When one reaches the melting temperature, particles break free as the material melts away.	64
844	vi	friction	\N	64
845	nl	Wrijving	\N	64
846	hr	Trenje	\N	64
847	pl	Tarcie	\N	64
848	zh_TW	摩擦	\N	64
849	en	Geometric Optics	How does a lens form an image? See how light rays are refracted by a lens. Watch how the image changes when you adjust the focal length of the lens, move the object, move the lens, or move the screen.	65
850	es	geometric-optics	\N	65
851	it	geometric-optics	\N	65
852	nl	geometric-optics	\N	65
853	sk	geometric-optics	\N	65
854	vi	geometric-optics	\N	65
855	cs	geometric-optics	\N	65
856	el	geometric-optics	\N	65
857	sq	geometric-optics	\N	65
858	sl	geometric-optics	\N	65
859	pl	geometric-optics	\N	65
860	hr	Optička Klupa	\N	65
861	zh_TW	幾何光學	\N	65
862	pt	geometric-optics	\N	65
863	en	Lunar Lander	Can you avoid the boulder field and land safely, just before your fuel runs out, as Neil Armstrong did in 1969? Our version of this classic video game accurately simulates the real motion of the lunar lander with the correct mass, thrust, fuel consumption rate, and lunar gravity. The real lunar lander is very hard to control.	66
864	es	lunar-lander	\N	66
865	nl	Maanlander	\N	66
866	fr	Alunisseur	\N	66
867	hr	Mjesečeva sonda	\N	66
868	pl	Lądownik księżycowy	\N	66
869	et	Kuul maanduja	\N	66
870	zh_TW	月球登陸器	\N	66
871	en	Masses & Springs	A realistic mass and spring laboratory. Hang masses from springs and adjust the spring stiffness and damping. You can even slow time. Transport the lab to different planets. A chart shows the kinetic, potential, and thermal energy for each spring.	67
872	es	mass-spring-lab	\N	67
873	it	mass-spring-lab	\N	67
874	ja	mass-spring-lab	\N	67
875	nl	mass-spring-lab	\N	67
876	sk	mass-spring-lab	\N	67
877	sl	mass-spring-lab	\N	67
878	fr	mass-spring-lab	\N	67
879	el	mass-spring-lab	\N	67
880	vi	mass-spring-lab	\N	67
881	zh_TW	mass-spring-lab	\N	67
882	ar	الكتل والزنبرك	\N	67
883	zh_CN	重物和弹簧	\N	67
884	cs	Závaží a pružiny	\N	67
885	hr	mass-spring-lab	\N	67
886	pl	Ciężarki na sprężynach	\N	67
887	et	Koormised ja vedrud	\N	67
888	en	My Solar System	Build your own system of heavenly bodies and watch the gravitational ballet. With this orbit simulator, you can set initial positions, velocities, and masses of 2, 3, or 4 bodies, and then see them orbit each other.	68
889	es	my-solar-system	\N	68
890	nl	my-solar-system	\N	68
891	it	My Solar System	\N	68
892	hr	Solarni sustavi	\N	68
893	el	Το ηλιακό μου σύστημα	\N	68
894	pl	Mój Układ Słoneczny	\N	68
895	zh_TW	我的太陽系	\N	68
896	en	Ohm's Law	See how the equation form of Ohm's law relates to a simple circuit. Adjust the voltage and resistance, and see the current change according to Ohm's law. The sizes of the symbols in the equation change to match the circuit diagram.	69
897	es	ohms-law	\N	69
898	mn	ohms-law	\N	69
899	nl	ohms-law	\N	69
900	vi	ohms-law	\N	69
901	nb	ohms-law	\N	69
902	ro	Legea lui Ohm	\N	69
903	sk	ohms-law	\N	69
904	fi	ohms-law	\N	69
905	it	ohms-law	\N	69
906	pl	Prawo Ohma	\N	69
907	hr	Ohmov Zakon	\N	69
908	zh_TW	歐姆定律	\N	69
909	de	pendulum-lab	\N	70
910	en	Pendulum Lab	Play with one or two pendulums and discover how the period of a simple pendulum depends on the length of the string, the mass of the pendulum bob, and the amplitude of the swing. It's easy to measure the period using the photogate timer. You can vary friction and the strength of gravity. Use the pendulum to find the value of g on planet X. Notice the anharmonic behavior at large amplitude.	70
911	es	pendulum-lab	\N	70
912	ja	pendulum-lab	\N	70
913	nl	pendulum-lab	\N	70
914	sk	pendulum-lab	\N	70
915	hr	pendulum-lab	\N	70
916	cs	pendulum-lab	\N	70
917	fr	pendulum-lab	\N	70
918	el	pendulum-lab	\N	70
919	sl	pendulum-lab	\N	70
920	zh_TW	pendulum-lab	\N	70
921	en	Plinko Probability	Play Plinko and develop your knowledge of statistics. Drops balls through a triangular grid of pegs and see the balls random walk through the lattice. Watch the histogram of final positions build up and approach the binomial distribution. Inspired by the Virtual Lab in Probability and Statistics at U. Alabama in Huntsville (www.math.uah.edu/stat)	71
922	es	plinko-probability	\N	71
923	nl	Knikkerspel en waarschijnlijkheidsverdeling	\N	71
924	hr	Pascalov trokut	\N	71
925	pl	Deska Galtona -prawdopodobieństwo	\N	71
926	zh_TW	二項分佈彈珠台機率	\N	71
927	en	Projectile Motion	Blast a Buick out of a cannon! Learn about projectile motion by firing various objects. Set the angle, initial speed, and mass. Add air resistance. Make a game out of this simulation by trying to hit a target.	72
928	es	projectile-motion	\N	72
929	sk	projectile-motion	\N	72
930	tr	projectile-motion	\N	72
931	vi	projectile-motion	\N	72
932	hr	Gibanje projektila	\N	72
933	nl	Beweging projectiel	\N	72
934	fr	Mouvement d'un projectile	\N	72
935	pl	Rzuty	\N	72
936	zh_CN	拋物線運動	\N	72
937	zh_TW	拋物線運動	\N	72
938	ko	포사체 운동	\N	72
939	en	Resistance in a Wire	Learn about the physics of resistance in a wire. Change its resistivity, length, and area to see how they affect the wire's resistance. The sizes of the symbols in the equation change along with the diagram of a wire.	73
940	es	resistance-in-a-wire	\N	73
941	mn	resistance-in-a-wire	\N	73
942	nl	resistance-in-a-wire	\N	73
943	ro	Rezistenta intr-un cablu	\N	73
944	sk	resistance-in-a-wire	\N	73
945	it	resistance-in-a-wire	\N	73
946	hr	Otpor vodiča	\N	73
947	pl	Rezystancja przewodnika	\N	73
948	zh_TW	電線的電阻	\N	73
949	en	Stern-Gerlach Experiment	The classic Stern-Gerlach Experiment shows that atoms have a property called spin. Spin is a kind of intrinsic angular momentum, which has no classical counterpart. When the z-component of the spin is measured, one always gets one of two values: spin up or spin down.	74
950	es	Experimento de Stern-Gerlach	\N	74
951	nl	stern-gerlach	\N	74
952	hr	Stern-Gerlachov eksperiment	\N	74
953	en	Vector Addition	Learn how to add vectors. Drag vectors onto a graph, change their length and angle, and sum them together. The magnitude, angle, and components of each vector can be displayed in several formats.	75
954	es	vector-addition	\N	75
955	sk	vector-addition	\N	75
956	vi	vector-addition	\N	75
957	nl	Optellen van Vectoren	\N	75
958	hr	Zbrajanje vektora	\N	75
959	zh_TW	向量加法	\N	75
960	pl	Dodawanie wektorów	\N	75
961	de	wave-on-a-string	\N	76
962	en	Wave on a String	Watch a string vibrate in slow motion. Wiggle the end of the string and make waves, or adjust the frequency and amplitude of an oscillator. Adjust the damping and tension. The end can be fixed, loose, or open.	76
963	es	wave-on-a-string	\N	76
964	it	wave-on-a-string	\N	76
965	nl	wave-on-a-string	\N	76
966	sk	wave-on-a-string	\N	76
967	el	wave-on-a-string	\N	76
968	vi	wave-on-a-string	\N	76
969	hr	Valovi na žici	\N	76
970	zh_TW	弦波	\N	76
971	pl	Fale na linie	\N	76
\.


--
-- Data for Name: phet_user; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY phet_user (id, email, password, teammember) FROM stdin;
1	olsonsjc@gmail.com	WH39ah79fP15QF79Tv0pOv0b/SY=	t
2	testguest@phet.colorado.edu	G/z9BCEXRv0cSv0l/XoD/P1n/Q==	f
3	tester@phet.colorado.edu	/g/9KAYhAhP9/iL9/kf9SC4o/Q==	f
\.


--
-- Data for Name: project; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY project (id, name, versionmajor, versionminor, versiondev, versionrevision, versiontimestamp) FROM stdin;
1	balloons	1	13	0	31180	1241117055
2	battery-resistor-circuit	1	2	0	30589	1240274417
3	battery-voltage	1	2	0	30589	1240274601
4	bound-states	1	5	0	30589	1240274831
5	circuit-construction-kit	3	17	13	33522	1248239502
6	color-vision	1	2	0	30589	1240276318
7	conductivity	1	3	0	30589	1240276585
8	discharge-lamps	1	9	0	30589	1240276792
9	eating-and-exercise	1	1	0	30589	1240277017
10	efield	1	2	0	30589	1240277227
11	electric-hockey	1	6	0	30610	1240281119
12	energy-skate-park	2	5	6	32959	1246495791
13	faraday	2	2	0	30610	1240281675
14	forces-1d	1	20	0	30610	1240282988
15	fourier	3	3	0	30610	1240283271
16	glaciers	2	1	6	33564	1248380493
17	greenhouse	2	6	0	30610	1240283623
18	hydrogen-atom	1	8	0	30610	1240283863
19	ideal-gas	3	10	0	30667	1240319270
20	lasers	4	6	0	30671	1240321011
21	maze-game	1	5	0	30745	1240345893
22	microwaves	1	3	0	30671	1240321412
23	motion-2d	1	5	0	33426	1247971439
24	moving-man	1	24	1	33383	1247773218
25	mri	1	6	0	30671	1240322048
26	optical-quantum-control	1	2	0	30671	1240322813
27	optical-tweezers	2	2	0	30671	1240322973
28	ph-scale	1	1	0	30671	1240323313
29	photoelectric	1	7	0	30671	1240323735
30	quantum-tunneling	1	10	0	30671	1240323990
31	quantum-wave-interference	1	9	0	30671	1240324180
32	radio-waves	1	7	0	30671	1240324500
33	reactions-and-rates	1	3	11	33960	1249929000
34	rotation	1	7	0	30671	1240324921
35	rutherford-scattering	1	2	0	30671	1240325221
36	self-driven-particle-model	1	2	0	30671	1240325411
37	semiconductor	1	4	0	30671	1240325557
38	signal-circuit	1	3	0	30671	1240325691
39	soluble-salts	1	5	0	30671	1240326047
40	sound	2	12	0	30671	1240326315
41	test-project	2	3	0	33245	1247347154
42	travoltage	1	7	0	30671	1240327527
43	wave-interference	1	7	0	30671	1240327754
44	arithmetic	2	0	2	32390	1244614342
45	blackbody-spectrum	2	0	0	30671	1240328241
46	charges-and-fields	2	2	2	30828	1240501930
47	curve-fitting	2	0	0	30671	1240328531
48	equation-grapher	2	0	0	30671	1240328615
49	estimation	2	0	0	30671	1240328721
50	faradays-law	2	0	0	30671	1240328799
51	friction	2	0	0	30671	1240328976
52	geometric-optics	2	3	0	32366	1244578551
53	lunar-lander	2	0	0	30671	1240329219
54	mass-spring-lab	2	0	3	31921	1242753086
55	my-solar-system	2	0	0	30671	1240329497
56	ohms-law	2	0	0	30671	1240329577
57	pendulum-lab	2	0	6	32966	1246530543
58	plinko-probability	2	0	0	30671	1240329888
59	projectile-motion	2	0	1	32075	1243513790
60	resistance-in-a-wire	2	0	0	30671	1240330056
61	stern-gerlach	2	0	0	30671	1240330187
62	vector-addition	2	0	0	30671	1240330323
63	wave-on-a-string	2	0	0	30671	1240330416
\.


--
-- Data for Name: simulation; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY simulation (id, name, type, project, designteam, libraries, thanksto) FROM stdin;
2	battery-resistor-circuit	0	2	\N	\N	\N
3	battery-voltage	0	3	\N	\N	\N
4	band-structure	0	4	\N	\N	\N
5	covalent-bonds	0	4	\N	\N	\N
6	bound-states	0	4	\N	\N	\N
7	circuit-construction-kit-ac	0	5	\N	\N	\N
8	circuit-construction-kit-dc	0	5	\N	\N	\N
9	color-vision	0	6	\N	\N	\N
10	conductivity	0	7	\N	\N	\N
11	discharge-lamps	0	8	\N	\N	\N
12	eating-and-exercise	0	9	\N	\N	\N
13	efield	0	10	\N	\N	\N
14	electric-hockey	0	11	\N	\N	\N
15	energy-skate-park	0	12	\N	\N	\N
16	magnets-and-electromagnets	0	13	\N	\N	\N
17	generator	0	13	\N	\N	\N
18	magnet-and-compass	0	13	\N	\N	\N
19	faraday	0	13	\N	\N	\N
20	forces-1d	0	14	\N	\N	\N
21	fourier	0	15	\N	\N	\N
23	greenhouse	0	17	\N	\N	\N
24	hydrogen-atom	0	18	\N	\N	\N
25	gas-properties	0	19	\N	\N	\N
26	balloons-and-buoyancy	0	19	\N	\N	\N
27	reversible-reactions	0	19	\N	\N	\N
28	lasers	0	20	\N	\N	\N
29	maze-game	0	21	\N	\N	\N
30	microwaves	0	22	\N	\N	\N
31	motion-2d	0	23	\N	\N	\N
32	moving-man	0	24	\N	\N	\N
33	mri	0	25	\N	\N	\N
34	optical-quantum-control	0	26	\N	\N	\N
35	molecular-motors	0	27	\N	\N	\N
36	stretching-dna	0	27	\N	\N	\N
37	optical-tweezers	0	27	\N	\N	\N
38	ph-scale	0	28	\N	\N	\N
39	photoelectric	0	29	\N	\N	\N
40	quantum-tunneling	0	30	\N	\N	\N
41	davisson-germer	0	31	\N	\N	\N
42	quantum-wave-interference	0	31	\N	\N	\N
43	radio-waves	0	32	\N	\N	\N
44	reactions-and-rates	0	33	\N	\N	\N
45	torque	0	34	\N	\N	\N
46	rotation	0	34	\N	\N	\N
47	rutherford-scattering	0	35	\N	\N	\N
48	self-driven-particle-model	0	36	\N	\N	\N
49	semiconductor	0	37	\N	\N	\N
50	signal-circuit	0	38	\N	\N	\N
51	soluble-salts	0	39	\N	\N	\N
52	sound	0	40	\N	\N	\N
53	sim2	0	41	\N	\N	\N
54	sim1	0	41	\N	\N	\N
55	travoltage	0	42	\N	\N	\N
56	wave-interference	0	43	\N	\N	\N
58	blackbody-spectrum	1	45	\N	\N	\N
59	charges-and-fields	1	46	\N	\N	\N
60	curve-fitting	1	47	\N	\N	\N
61	equation-grapher	1	48	\N	\N	\N
62	estimation	1	49	\N	\N	\N
63	faradays-law	1	50	\N	\N	\N
64	friction	1	51	\N	\N	\N
65	geometric-optics	1	52	\N	\N	\N
66	lunar-lander	1	53	\N	\N	\N
67	mass-spring-lab	1	54	\N	\N	\N
68	my-solar-system	1	55	\N	\N	\N
69	ohms-law	1	56	\N	\N	\N
70	pendulum-lab	1	57	\N	\N	\N
71	plinko-probability	1	58	\N	\N	\N
72	projectile-motion	1	59	\N	\N	\N
73	resistance-in-a-wire	1	60	\N	\N	\N
74	stern-gerlach	1	61	\N	\N	\N
75	vector-addition	1	62	\N	\N	\N
76	wave-on-a-string	1	63	\N	\N	\N
57	arithmetic	1	44	Michael Dubson (developer/lead)<br/>Trish Loeblein	\N	\N
1	balloons	0	1	Sam Reid (developer/lead)<br/>Interviewer: Wendy Adams	\N	\N
22	glaciers	0	16	Wendy Adams<br/>Bob Anderson<br/>Andrea Blair<br/>Chris Malley (developer)<br/>Archie Paulson (lead)<br/>Kathy Perkins<br/>Dylan Ward	JFreeChart<br/>Piccolo	\N
\.


--
-- Data for Name: topics_mapping; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY topics_mapping (simulation_id, keyword_id, idx) FROM stdin;
57	32	0
26	37	0
26	42	1
1	46	0
4	59	0
4	56	1
4	1	2
3	4	0
3	9	1
3	57	2
2	10	0
2	4	1
2	9	2
2	57	3
58	64	0
58	262	1
58	62	2
58	52	3
59	66	0
59	4	1
59	68	2
7	2	0
7	8	1
7	9	2
7	263	3
7	7	4
7	6	5
7	73	6
7	75	7
7	264	8
8	2	0
8	8	1
8	9	2
8	263	3
8	7	4
8	6	5
9	79	0
9	265	1
9	81	2
9	80	3
10	56	0
60	87	0
60	89	1
41	95	0
5	99	0
5	266	1
42	183	3
5	268	2
5	267	3
12	269	0
12	270	1
12	104	2
12	108	3
14	1	0
14	47	1
14	66	2
13	1	0
13	47	1
13	66	2
15	108	0
15	109	1
15	110	2
15	111	3
15	112	4
61	28	0
61	87	1
61	90	2
62	117	0
19	123	0
19	122	1
19	126	2
63	123	0
63	122	1
63	126	2
20	131	0
20	27	1
20	25	2
20	26	3
21	260	0
21	271	1
21	272	2
64	112	0
64	38	1
64	144	2
25	37	0
25	144	1
25	38	2
25	114	3
25	39	4
25	146	5
25	45	6
25	273	7
17	129	0
17	123	1
17	122	2
17	74	3
65	148	0
65	149	1
65	19	2
22	23	0
22	156	1
23	155	0
23	274	1
23	144	2
23	38	3
23	156	4
55	46	0
28	17	0
28	275	1
28	52	2
66	276	0
66	170	1
66	171	2
66	277	3
66	278	4
66	114	5
18	122	0
18	126	1
18	128	2
16	122	0
16	126	1
16	125	2
67	172	0
67	173	1
67	109	2
67	279	3
29	27	0
29	25	1
29	26	2
30	175	0
30	144	1
30	38	2
30	280	3
24	52	0
24	177	1
24	178	2
24	180	3
24	181	4
31	24	0
31	26	1
31	25	2
32	27	0
32	25	1
32	26	2
68	24	0
68	26	1
68	25	2
68	27	3
68	114	4
11	62	0
11	57	1
69	11	0
69	2	1
34	19	0
34	281	1
70	193	0
70	282	1
38	196	0
38	203	1
38	283	2
38	284	3
39	62	0
39	52	1
39	79	2
71	207	0
71	285	1
72	210	0
72	255	1
72	286	2
72	171	3
72	211	4
6	287	0
6	58	1
6	260	2
6	54	3
40	287	0
40	99	1
42	52	0
42	79	1
42	57	2
42	214	4
42	260	5
43	216	0
44	225	0
44	29	1
44	199	2
44	288	3
73	223	0
73	5	1
27	38	0
27	40	1
27	144	2
27	37	3
27	225	4
27	65	5
47	52	0
47	227	1
47	96	2
47	183	3
47	289	4
47	47	5
47	48	6
47	228	7
47	184	8
51	229	0
51	230	1
51	231	2
51	220	3
51	232	4
51	233	5
51	234	6
51	221	7
48	214	0
49	84	0
49	241	1
49	242	2
50	290	0
50	2	1
50	263	2
33	245	0
52	248	0
52	260	1
74	52	0
74	247	1
74	213	2
45	159	0
45	251	1
45	252	2
45	253	3
75	30	0
75	254	1
56	260	0
56	248	1
76	260	0
76	138	1
76	137	2
\.


--
-- Data for Name: translated_string; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY translated_string (id, key, value, createdat, updatedat, translation) FROM stdin;
1	language.dir	ltr	2009-08-17 03:48:19.896	2009-08-17 03:48:19.896	1
1052	footer.someRightsReserved	Some rights reserved.	2009-09-03 13:17:29.436	2009-09-03 13:17:29.436	1
3	home.title	PhET: Free online physics, chemistry, biology, earth science and math simulations	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
4	home.header	Interactive Science Simulations	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
870	language.dir	rtl	2009-09-01 16:02:55.361	2009-09-01 16:02:55.361	4
6	home.playWithSims	Play with sims... >	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
7	home.runOurSims	Run our Simulations	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
8	home.onLine	On Line	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
9	home.fullInstallation	Full Installation	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
10	home.oneAtATime	One at a Time	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
11	home.teacherIdeasAndActivities	Teacher Ideas & Activities	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
12	home.workshops	Workshops	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
13	home.contribute	Contribute	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
14	home.supportPhet	Support PhET	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
15	home.translateSimulations	Translate Simulations	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
16	home.browseSims	Browse Sims	2009-08-17 03:48:19.897	2009-08-17 03:48:19.897	1
17	home.simulations	Simulations	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
1698	get-phet.header	Three Ways to Run Our Free Simulations	2009-09-03 23:19:38.994	2009-09-03 23:19:38.994	1
19	simulationDisplay.title	{0} - PhET Simulations	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
20	simulationMainPanel.translatedVersions	Translated Versions:	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
21	simulationMainPanel.screenshot.alt	{0} Screenshot	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
22	simulationMainPanel.version	Version: {0}	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
23	simulationMainPanel.kilobytes	{0} kB	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
24	simulationMainPanel.runOffline	Download	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
25	simulationMainPanel.runOnline	Run Now!	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
26	simulationMainPanel.topics	Topics	2009-08-17 03:48:19.899	2009-08-17 03:48:19.899	1
27	simulationMainPanel.mainTopics	Main Topics	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
28	simulationMainPanel.keywords	Related Topics	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
29	nav.home	Home	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
30	nav.simulations	Simulations	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
31	nav.featured	Featured Sims	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
32	nav.new	New Sims	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
33	nav.physics	Physics	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
34	nav.motion	Motion	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
35	nav.sound-and-waves	Sound & Waves	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
36	nav.work-energy-and-power	Work, Energy & Power	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
37	nav.heat-and-thermodynamics	Heat & Thermo	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
38	nav.quantum-phenomena	Quantum Phenomena	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
39	nav.light-and-radiation	Light & Radiation	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
40	nav.electricity-magnets-and-circuits	Electricity, Magnets & Circuits	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
41	nav.biology	Biology	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
42	nav.chemistry	Chemistry	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
43	nav.earth-science	Earth Science	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
44	nav.math	Math	2009-08-17 03:48:19.9	2009-08-17 03:48:19.9	1
45	nav.tools	Tools	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
46	nav.applications	Applications	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
47	nav.cutting-edge-research	Cutting Edge Research	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
48	nav.all	All Sims	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
49	nav.troubleshooting.main	Troubleshooting	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
50	nav.about	About PhET	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
51	troubleshooting.main.title	Troubleshooting - PhET Simulations	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
52	troubleshooting.main.intro	This page will help you solve some of the problems people commonly have running our programs. If you can''t solve your problem here, please notify us by email at the following email address: {0}.	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
53	troubleshooting.main.java	Java Installation and Troubleshooting	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
54	troubleshooting.main.flash	Flash Installation and Troubleshooting	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
55	troubleshooting.main.javascript	JavaScript Troubleshooting (note: this is for your browser, not the simulations)	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
56	troubleshooting.main.faqs	FAQs	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
57	troubleshooting.main.q1.title	Why can I run some of the simulations but not all?	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
58	troubleshooting.main.q1.answer	<p>Some of our simulations are Java Web Start based applications and others use Macromedia''s Flash player. Flash comes with most computers while Java Web Start is a free application that can be downloaded from Sun Microsystems. To run the Java-based simulations you must have Java version 1.5 or higher installed on your computer.</p><p><a {0}>Learn about Java installation and Troubleshooting here</a>.</p>	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
59	troubleshooting.main.q2.title	What are the System Requirements for running PhET simulations?	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
60	troubleshooting.main.q2.answer	<p><strong>Windows Systems</strong><br/>Intel Pentium processor<br/>Microsoft Windows 98SE/2000/XP/Vista<br/>256MB RAM minimum<br/>Approximately 97 MB available disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Sun Java 1.5.0_15 or later<br/>Macromedia Flash 8 or later<br/>Microsoft Internet Explorer 6 or later, Firefox 2 or later</p><p><strong>Macintosh Systems</strong><br/>G3, G4, G5 or Intel processor<br/>OS 10.4 or later<br/>256MB RAM minimum<br/>Approximately 86 MB available disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Apple Java 1.5.0_19 or later<br/>Macromedia Flash 8 or later<br/>Safari 2 or later, Firefox 2 or later</p><p><strong>Linux Systems</strong><br/>Intel Pentium processor<br/>256MB RAM minimum<br/>Approximately 81 MB disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Sun Java 1.5.0_15 or later<br/>Macromedia Flash 8 or later<br/>Firefox 2 or later<br/></p><p><strong>Support Software</strong></p><p>Some of our simulations use Java, and some use Flash. Both of these are available as free downloads, and our downloadable <a {0}>PhET Offline Website Installer</a> includes Java for those who need it.</p>	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
61	troubleshooting.main.q4.title	I use Internet Explorer and the simulations do not run on my computer.	2009-08-17 03:48:19.901	2009-08-17 03:48:19.901	1
102	nav.work-energy-and-power	能源和电力工作	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
103	nav.heat-and-thermodynamics	热和热力学	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
104	nav.quantum-phenomena	量子现象	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
105	nav.light-and-radiation	光及辐射	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
117	sponsors.principalSponsors	主要提案国	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
62	troubleshooting.main.q4.answer	<p>We <strong>strongly</strong> recommend you use the latest version of Internet Explorer (IE8).</p><p><strong>Internet Explorer Security Settings</strong></p><p>Some installations of Internet Explorer, particularly under Windows XP SP2, have default security settings which can impede some aspects of how your locally installed PhET interface functions. For the best user experience while using our simulations installed on your computer, we recommend following the steps below:</p><ol><li>In Internet Explorer on your local workstation, choose Tools &gt; Internet Options.</li><li>Choose the Advanced tab, then scroll to the Security section.</li><li>Enable "Allow active content to run in files on my computer".</li><li>Choose OK.</li></ol>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
63	troubleshooting.main.q5.title	Why don't Flash simulations run on my computer?	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
64	troubleshooting.main.q5.answer	<p><strong>QuickTime™ and Flash™ compatibility</strong></p><p>It has come to our attention that some of our users are unable to use our Flash-based simulations due to a compatibility issue between Apple Computer''s QuickTime&trade; and the Flash&trade; player. Some users have reported that uninstalling QuickTime resolves the issue.</p><p>We are aware that this is not an acceptable solution and are working to resolve this issue. If you are experiencing this problem, please contact us at at {0} and regularly check back here for more information.</p>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
65	troubleshooting.main.q6.title	What is the ideal screen resolution to run PhET simulations?	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
66	troubleshooting.main.q6.answer	<p>PhET simulations work best at a screen resolution of 1024 x 768 pixels. (Some of them are written so that they cannot be resized.) At lower resolution (e.g. 800 x 600), all the controls may not fit on your screen. At higher resolution (e.g. 1280 x 1024), you may not be able to make the simulation fill the whole screen, or if you do, it may slow down performance. To change your screen resolution, follow the directions below:</p><p><strong>Windows Vista</strong></p><ol><li>From Start menu, click on "Control Panel."</li><li>Press "Adjust screen resolution" under "Appearance and Personalization."</li><li>Use the "Screen resolution" slider to select a resolution and click "OK."</li></ol><p><strong>Windows 98SE/2000/XP</strong></p><ol><li>From Start menu, click on "Control Panel."</li><li>Double click on "Display" icon.</li><li>Select the "Settings" tab.</li><li>Use the "Screen resolution" slider to select a resolution and click "OK."</li></ol><p><strong>Macintosh</strong></p><ol><li>Open the System Preferences (either from the Dock or from the Apple menu).</li><li>Open the Displays Panel and choose the Display tab.</li><li>On the left of the Displays tab you can select one of the Resolutions from the list.</li><li>Quit or close the System Preferences when done.</li></ol>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
67	troubleshooting.main.q7.title	I have Windows 2000 and can run Flash simulations but the Java based simulations do not work.	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
68	troubleshooting.main.q7.answer	<p>Some Windows 2000 systems have been reported to lack part of the necessary Java configuration. These systems will typically start our Flash-based simulations reliably, but will appear to do nothing when launching our Java-based simulations.</p><p><strong>To resolve this situation, please perform the following steps:</strong></p><ol><li>From the desktop or start menu, open "My Computer"</li><li>Click on the "Folder Options" item in the "Tools" menu</li><li>Click on the "File Types" tab at the top of the window that appears</li><li>Locate "JNLP" in the "extensions" column, and click once on it to select the item</li><li>Click on the "change" button</li><li>When asked to choose which program to use to open JNLP files, select "Browse"</li><li>Locate the program "javaws" or "javaws.exe" in your Java installation folder (typically "C:\\Program Files\\Java\\j2re1.xxxx\\javaws", where "xxxx" is a series of numbers indicating the software version; choose the latest version)</li><li>Select the program file and then click "Open" to use the "javaws" program to open JNLP files.</li></ol><p>Java-based simulations should now function properly.</p><p>Please contact us by email at {0} if you have any further difficulties.</p>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
69	troubleshooting.main.q8.title	Why do PhET simulations run slower on my laptop than on a desktop?	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
70	troubleshooting.main.q8.answer	<p>On some laptop computers, simulations may appear to run much slower than anticipated and/or exhibit unexpected graphics problems. This may be due to power management settings that affect how the computer''s graphics system runs and can be corrected by either a) changing the computer''s power management configuration, or b) using the laptop computer while plugged in to an AC power source.</p><p>Many laptop computers are configured to reduce the amount of battery power used by the graphics/video system while the computer is running on battery power. If you must use the laptop while it is not plugged in, we suggest changing your computer''s power management settings to "maximize performance" while unplugged. This should ensure that the graphics system runs at its peak speed. The location of this setting varies from one manufacturer to the next and we suggest contacting your computer vendor if you have difficulty locating it. Please contact us at {0} if you continue to encounter problems.</p>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
71	troubleshooting.main.q9.title	Why does my computer crash when I run one of the simulations that has sound?	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
72	troubleshooting.main.q9.answer	<p>Simulations that use sound can be unstable when run on computers using old device driver software. If you are encountering crashes or other undesirable behavior with any of our simulations that use sound, we advise updating your sound drivers, as this may solve the problem. For assistance with updating your sound drivers, contact your computer vendor or audio hardware manufacturer. Contact us at {0} if you continue to encounter difficulty. </p>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
73	troubleshooting.main.q10.title	I would like to translate PhET Simulations into another Language. Can this be easily done?	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
74	troubleshooting.main.q10.answer	<p>The PhET simulations have been written so that they are easily translated to languages other than English. Please <a {0}>click here</a> for more information.</p>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
75	troubleshooting.main.q11.title	I have downloaded and installed the PhET Offline Website Installer, and I get a warning on every page. Why?	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
106	nav.electricity-magnets-and-circuits	电力和磁铁和电路	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
107	nav.biology	生物学	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
108	nav.chemistry	化学	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
109	nav.earth-science	地球科学	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
110	nav.math	数学	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
111	nav.tools	工具	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
112	nav.applications	应用	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
113	nav.cutting-edge-research	前沿研究	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
114	nav.all	所有模拟	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
115	nav.about	关于PhET	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
116	simulationMainPanel.translatedVersions	翻译文本	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
76	troubleshooting.main.q11.answer	<p>The <a {0}>PhET Offline Website Installer</a> creates a local copy of the current version of the PhET website on your computer. When you access this locally installed copy, your computer will use your default browser, which for many people is Internet Explorer. If the security settings are set to their default values, you may get an error that says <em>"To help protect your security, Internet Explorer has restricted this webpage from running scripts or ActiveX controls that could access your computer. Click here for options..."</em> (or something similar). This is a security feature of Internet Explorer version 6 and later, and is meant to warn users about running active content locally. The PhET simulations present no danger to your computer, and running them locally is no different than running them from the web site.</p><p>If you wish to disable this warning, you can do so by adjusting your browser''s security settings. For IE versions 6 and 7, the way to do this is to go into Tools->Internet Options->Advanced, find the "Security" heading, and check "Allow active content to run in files on My Computer". Note that you will need to restart Internet Explorer to get this change to take effect. You should only do this if feel confident that there is no other off-line content that you may run on your computer that could be malicious.</p><p>Alternatively, you could use a different browser (such as Firefox) that does not have this issue.</p>	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
77	troubleshooting.main.q12.title	When I run simulations from the PhET Offline Website Installer, I am seeing a dialog that says "The application's digital signature has been verified. Do you want to run the application?" (or something similar). What does this mean?	2009-08-17 03:48:19.902	2009-08-17 03:48:19.902	1
78	troubleshooting.main.q12.answer	<p>The PhET simulations that are distributed with the installer include a "digital certificate" that verifies that these simulations were actually created by PhET. This is a security measure that helps to prevent an unscrupulous individual from creating applications that claim to be produced by PhET but are not. If the certificate acceptance dialog says that the publisher is "PhET, University of Colorado", and the dialog also says that the signature was validated by a trusted source, you can have a high degree of confidence that the application was produced by the PhET team.</p><p>On most systems, it is possible to permanently accept the PhET certificate and thereby prevent this dialog from appearing each time a simulation is run locally. Most Windows and Max OSX systems have a check box on the certificate acceptance dialog that says "Always trust content from this publisher". Checking this box will configure your system in such a way that the dialog will no longer appear when starting up PhET simulations.</p>	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
79	troubleshooting.main.q13.title	(MAC users) When I click "run now" to start the simulation all I get is a text file that opens?	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
80	troubleshooting.main.q13.answer	<p>This problem will affect mac users who recently installed Apple''s "Java for Mac OS X 10.5 Update 4". The update will typically be done via Software Update, or automatically. After installing this update, the problem appears: clicking on JNLP files in Safari or FireFox caused the JNLP file to open in TextEdit, instead of starting Java Web Start. </p><p>The fix is:<br/>1. Go to http://support.apple.com/downloads/Java_for_Mac_OS_X_10_5_Update_4<br/>2. Click Download to download a .dmg file<br/>3. When the .dmg has downloaded, double-click on it (if it doesn''t mount automatically)<br/>4. Quit all applications<br/>5. Run the update installer</p>	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
81	troubleshooting.main.licensingRequirements	What are Licensing requirements?	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
82	about.title	About PhET	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
83	about.p1	PhET Interactive Simulations is an ongoing effort to provide an extensive suite of simulations to improve the way that physics, chemistry, biology, earth science and math are taught and learned. The <a {0}>simulations</a> are interactive tools that enable students to make connections between real life phenomena and the underlying science which explains such phenomena. Our team of scientists, software engineers and science educators use a <a {1}>research-based approach</a> - incorporating findings from prior research and our own testing - to create simulations that support student engagement with and understanding of scientific concepts.	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
84	about.p2	In order to help students visually comprehend these concepts, PhET simulations animate what is invisible to the eye through the use of graphics and intuitive controls such as click-and-drag manipulation, sliders and radio buttons.  In order to further encourage quantitative exploration, the simulations also offer measurement instruments including rulers, stop-watchs, voltmeters and thermometers.  As the user manipulates these interactive tools, responses are immediately animated thus effectively illustrating cause-and-effects relationships as well as multiple linked representations (motion of the objects, graphs, number readouts, etc...).	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
85	about.p3	To ensure educational effectiveness and usability, all of the simulations are extensively tested and evaluated.  These tests include student interviews in addition to actual utilization of the simulations in a variety of settings, including lectures, group work, homework and lab work.   Our <a {0}>rating system</a> indicates what level of testing has been completed on each simulation.	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
86	about.p4	All PhET simulations are freely available from the <a {0}>PhET website</a> and are easy to use and incorpate into the classroom. They are written in <a {1}>Java</a> and <a {2}>Flash</a>, and can be run using a standard web browser as long as <a {2}>Flash</a> and <a {1}>Java</a> are installed.	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
87	sponsors.principalSponsors	Principal Sponsors	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
88	sponsors.hewlett	Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
89	sponsors.nsf	An independent federal agency created by Congress in 1950 to promote the progress of science.	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
90	sponsors.ksu	King Saud University seeks to become a leader in educational and technological innovation, scientific discovery and creativity through fostering an atmosphere of intellectual inspiration and partnership for the prosperity of society.	2009-08-17 03:48:19.903	2009-08-17 03:48:19.903	1
91	home.header	互动科学模拟	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
92	nav.motion	运动	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
707	sponsors.otherSponsors	Other Sponsors &gt;&gt;	2009-08-31 19:24:42.677	2009-08-31 19:24:42.677	1
94	home.subheader	有趣，互动，以研究为基础的模拟物理现象从碧项目在科罗拉多大学	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
95	home.playWithSims	玩模拟... >	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
96	nav.home	家	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
97	nav.simulations	模拟	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
98	nav.featured	精选模拟	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
99	nav.new	新的模拟	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
100	nav.physics	物理	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
101	nav.sound-and-waves	健全和波	2009-08-17 03:48:19.904	2009-08-17 03:48:19.904	2
118	sponsors.hewlett	使赠款，以解决最严重的社会和环境问题所面临的社会，风险资本，负责任的投资，可能会发挥作用随着时间的推移	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
119	sponsors.nsf	一个独立的联邦机构由美国国会创办于1950年，以促进科学的进步	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
120	sponsors.ksu	沙特国王大学力求成为一个领先的教育和技术创新，科学发现和创造性通过促进的气氛中进行的智力启迪和伙伴关系，社会的繁荣	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
121	home.title	PhET: 免费在线物理，化学，生物，地球科学和数学模拟	2009-08-17 03:48:19.905	2009-08-17 03:48:19.905	2
122	simulationDisplay.title	{0} - PhET 模拟	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
123	simulationPage.title	{0} ({1})	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
124	language.dir	ltr	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
125	nav.troubleshooting.main	疑难解答	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
126	troubleshooting.main.title	疑难解答- PhET模拟	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
127	troubleshooting.main.intro	此页将帮助您解决一些问题，人们普遍有运行我们的程序。如果你根本无法解决您的问题，请通过电子邮件通知我们在以下的电子邮件地址 {0}	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
128	troubleshooting.main.java	Java的安装和故障检修	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
129	troubleshooting.main.flash	闪光安装和故障检修	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
130	troubleshooting.main.javascript	JavaScript的疑难解答（注：这是您的浏览器，而不是模拟）	2009-08-17 03:48:19.906	2009-08-17 03:48:19.906	2
131	troubleshooting.main.q2.title	的系统要求是什么运行碧模拟？	2009-08-17 03:48:19.907	2009-08-17 03:48:19.907	2
132	keyword.electricity	Electricity	2009-08-17 03:49:50.719	2009-08-17 03:49:50.719	1
133	keyword.circuits	Circuits	2009-08-17 03:50:00.576	2009-08-17 03:50:00.576	1
134	keyword.current	Current	2009-08-17 03:50:16.573	2009-08-17 03:50:16.573	1
135	keyword.voltage	Voltage	2009-08-17 03:50:24.614	2009-08-17 03:50:24.614	1
136	keyword.resistance	Resistance	2009-08-17 03:50:32.845	2009-08-17 03:50:32.845	1
137	keyword.voltmeter	Voltmeter	2009-08-17 03:50:43.377	2009-08-17 03:50:43.377	1
138	keyword.ammeter	Ammeter	2009-08-17 03:50:54.17	2009-08-17 03:50:54.17	1
139	keyword.lightBulbs	Light Bulbs	2009-08-17 03:51:00.408	2009-08-17 03:51:00.408	1
141	keyword.resistor	Resistor	2009-08-17 03:51:15.968	2009-08-17 03:51:15.968	1
142	keyword.ohmsLaw	Ohm&#039;s Law	2009-08-17 03:51:25.474	2009-08-17 03:51:25.474	1
143	keyword.kirchoffsLaw	Kirchoff&#039;s Law	2009-08-17 03:51:42.176	2009-08-17 03:51:42.176	1
144	keyword.seriesCircuit	Series Circuit	2009-08-17 03:51:53.958	2009-08-17 03:51:53.958	1
145	keyword.parallelCircuit	Parallel Circuit	2009-08-17 03:52:05.481	2009-08-17 03:52:05.481	1
146	keyword.rcCircuit	RC Circuit	2009-08-17 03:52:17.583	2009-08-17 03:52:17.583	1
147	keyword.battery	电池	2009-08-17 05:01:29.401	2009-08-17 05:01:35.435	2
2	language.name	English	2009-08-17 03:48:19.897	2009-08-17 05:11:51.155	1
93	language.name	中文	2009-08-17 03:48:19.904	2009-08-17 05:12:01.023	2
148	keyword.opticalTweezers	Optical Tweezers	2009-08-17 05:23:52.505	2009-08-17 05:23:52.505	1
149	keyword.laser	Laser	2009-08-17 05:24:16.534	2009-08-17 05:24:16.534	1
871	language.name	العربية	2009-09-03 10:36:18.312	2009-09-03 10:36:18.312	4
152	keyword.dna	DNA	2009-08-17 05:25:51.932	2009-08-17 05:25:51.932	1
153	keyword.molecularMotors	Molecular Motors	2009-08-17 05:26:07.814	2009-08-17 05:26:07.814	1
154	keyword.climateChange	Climate Change	2009-08-17 05:26:43.99	2009-08-17 05:26:43.99	1
155	keyword.glaciers	Glaciers	2009-08-17 05:26:50.343	2009-08-17 05:26:50.343	1
156	keyword.motion	Motion	2009-08-17 05:27:45.715	2009-08-17 05:27:45.715	1
157	keyword.velocity	Velocity	2009-08-17 05:27:51.195	2009-08-17 05:27:51.195	1
158	keyword.acceleration	Acceleration	2009-08-17 05:28:07.337	2009-08-17 05:28:07.337	1
159	keyword.position	Position	2009-08-17 05:28:11.923	2009-08-17 05:28:11.923	1
160	keyword.graphing	Graphing	2009-08-17 05:28:29.24	2009-08-17 05:28:29.24	1
161	keyword.kinematics	Kinematics	2009-08-17 05:28:41.809	2009-08-17 05:28:41.809	1
162	keyword.vectors	Vectors	2009-08-17 05:28:56.29	2009-08-17 05:28:56.29	1
163	keyword.acceleration	加速度	2009-08-17 05:32:54.613	2009-08-17 05:33:04.115	2
164	keyword.arithmetic	Arithmetic	2009-08-17 14:16:27.728	2009-08-17 14:16:27.728	1
165	keyword.multiplication	Multiplication	2009-08-17 14:16:35.754	2009-08-17 14:16:35.754	1
166	keyword.division	Division	2009-08-17 14:16:43.737	2009-08-17 14:16:43.737	1
167	keyword.factoring	Factoring	2009-08-17 14:16:48.865	2009-08-17 14:16:48.865	1
168	keyword.calculation	Calculation	2009-08-17 14:16:57.454	2009-08-17 14:16:57.454	1
169	keyword.math	Math	2009-08-17 14:17:05.309	2009-08-17 14:17:05.309	1
170	keyword.gas	Gas	2009-08-17 14:18:27.916	2009-08-17 14:18:27.916	1
171	keyword.thermodynamics	Thermodynamics	2009-08-17 14:18:42.917	2009-08-17 14:18:42.917	1
172	keyword.pressure	Pressure	2009-08-17 14:18:59.521	2009-08-17 14:18:59.521	1
173	keyword.temperature	Temperature	2009-08-17 14:19:05.164	2009-08-17 14:19:05.164	1
174	keyword.volume	Volume	2009-08-17 14:19:28.342	2009-08-17 14:19:28.342	1
175	keyword.buoyancy	Buoyancy	2009-08-17 14:19:49.752	2009-08-17 14:19:49.752	1
176	keyword.chemistry	Chemistry	2009-08-17 14:20:05.579	2009-08-17 14:20:05.579	1
177	keyword.atmosphere	Atmosphere	2009-08-17 14:20:20.281	2009-08-17 14:20:20.281	1
178	keyword.boylesLaw	Boyle&#039;s Law	2009-08-17 14:20:33.71	2009-08-17 14:20:33.71	1
179	keyword.staticElectricity	Static Electricity	2009-08-17 14:21:16.525	2009-08-17 14:21:16.525	1
192	keyword.energyBand	Energy Bands	2009-08-17 14:35:07.139	2009-08-17 14:35:07.139	1
181	keyword.electricForce	Electric Force	2009-08-17 14:21:58.914	2009-08-17 14:21:58.914	1
182	keyword.coulombsLaw	Coulomb&#039;s Law	2009-08-17 14:22:14.905	2009-08-17 14:22:14.905	1
183	keyword.polarization	Polarization	2009-08-17 14:22:35.417	2009-08-17 14:22:35.417	1
184	keyword.electrostatics	Electrostatics	2009-08-17 14:23:06.768	2009-08-17 14:23:06.768	1
185	keyword.quantumMechanics	Quantum Mechanics	2009-08-17 14:24:22.662	2009-08-17 14:24:22.662	1
186	keyword.energyLevels	Energy Levels	2009-08-17 14:32:21.939	2009-08-17 14:32:21.939	1
187	keyword.probabilityDensity	Probability Density	2009-08-17 14:32:55.697	2009-08-17 14:32:55.697	1
188	keyword.waveFunction	Wave Function	2009-08-17 14:33:22.849	2009-08-17 14:33:22.849	1
189	keyword.conductivity	Conductivity	2009-08-17 14:33:47.731	2009-08-17 14:33:47.731	1
190	keyword.electrons	Electrons	2009-08-17 14:34:02.566	2009-08-17 14:34:02.566	1
191	keyword.potentialWells	Potential Wells	2009-08-17 14:34:19.643	2009-08-17 14:34:19.643	1
193	keyword.energyGap	Energy Gap	2009-08-17 14:35:19.641	2009-08-17 14:35:19.641	1
194	keyword.radiation	Radiation	2009-08-17 14:38:20.137	2009-08-17 14:38:20.137	1
195	keyword.light	Light	2009-08-17 14:38:41.744	2009-08-17 14:38:41.744	1
196	keyword.spectrum	Spectrum	2009-08-17 14:38:55.074	2009-08-17 14:38:55.074	1
197	keyword.blackbody	Blackbody	2009-08-17 14:39:18.676	2009-08-17 14:39:18.676	1
198	keyword.thermalEnergy	Thermal Energy	2009-08-17 14:39:53.203	2009-08-17 14:39:53.203	1
199	keyword.electricField	Electric Field	2009-08-17 14:41:20.333	2009-08-17 14:41:20.333	1
200	keyword.electricPotential	Electric Potential	2009-08-17 14:41:37.153	2009-08-17 14:41:37.153	1
201	keyword.equipotential	Equipotential	2009-08-17 14:42:09.305	2009-08-17 14:42:09.305	1
202	keyword.pointCharge	Point Charge	2009-08-17 14:42:37.995	2009-08-17 14:42:37.995	1
151	keyword.optical	Optics	2009-08-17 05:25:34.157	2009-08-17 15:30:00.037	1
140	keyword.battery	Batteries	2009-08-17 03:51:08.067	2009-08-31 20:58:33.195	1
1076	language.names.ja_JP	Japanese (Japan)	2009-09-03 18:56:40.944	2009-09-03 18:56:40.944	1
203	keyword.dipole	Dipole	2009-08-17 14:42:47.818	2009-08-17 14:42:47.818	1
204	keyword.acCircuits	AC Circuits	2009-08-17 14:45:04.666	2009-08-17 14:45:04.666	1
205	keyword.capacitance	Capacitance	2009-08-17 14:45:21.691	2009-08-17 14:45:21.691	1
206	keyword.capacitor	Capacitor	2009-08-17 14:45:33.339	2009-08-17 14:45:33.339	1
207	keyword.induction	Induction	2009-08-17 14:45:49.673	2009-08-17 14:45:49.673	1
208	keyword.inductor	Inductor	2009-08-17 14:45:58.756	2009-08-17 14:45:58.756	1
209	keyword.alternatingCurrent	Alternating Current	2009-08-17 14:46:20.047	2009-08-17 14:46:20.047	1
210	keyword.rlcCircuit	RLC Circuit	2009-08-17 14:47:14.55	2009-08-17 14:47:14.55	1
211	keyword.lcCircuit	LC Circuit	2009-08-17 14:47:32.007	2009-08-17 14:47:32.007	1
212	keyword.photons	Photons	2009-08-17 14:50:56.912	2009-08-17 14:50:56.912	1
213	keyword.whiteLight	White Light	2009-08-17 14:51:08.563	2009-08-17 14:51:08.563	1
872	home.header	علوم المحاكاة التفاعلية	2009-09-03 10:37:15.522	2009-09-03 10:37:15.522	4
215	keyword.insulators	Insulators	2009-08-17 14:52:31.844	2009-08-17 14:52:31.844	1
216	keyword.conductors	Conductors	2009-08-17 14:52:47.547	2009-08-17 14:52:47.547	1
217	keyword.semiconductors	Semiconductors	2009-08-17 14:53:01.418	2009-08-17 14:53:01.418	1
218	keyword.photoconductors	Photoconductors	2009-08-17 14:53:16.189	2009-08-17 14:53:16.189	1
219	keyword.solidState	Solid State	2009-08-17 14:53:56.45	2009-08-17 14:53:56.45	1
220	keyword.polynomials	Polynomials	2009-08-17 14:54:29.156	2009-08-17 14:54:29.156	1
221	keyword.statistics	Statistics	2009-08-17 14:54:45.893	2009-08-17 14:54:45.893	1
222	keyword.errorAnalysis	Error Analysis	2009-08-17 14:55:02.208	2009-08-17 14:55:02.208	1
223	keyword.curves	Curves	2009-08-17 14:55:21.938	2009-08-17 14:55:21.938	1
224	keyword.correlation	Correlation	2009-08-17 14:56:19.504	2009-08-17 14:56:19.504	1
225	keyword.chiSquare	Chi-square	2009-08-17 14:57:00.716	2009-08-17 14:57:00.716	1
226	keyword.chiSquared	Chi-squared	2009-08-17 14:57:08.836	2009-08-17 14:57:08.836	1
227	keyword.chi	Chi	2009-08-17 14:57:17.206	2009-08-17 14:57:17.206	1
228	keyword.electronDiffraction	Electron Diffraction	2009-08-17 14:57:54.728	2009-08-17 14:57:54.728	1
229	keyword.atomicStructure	Atomic Structure	2009-08-17 14:58:16.688	2009-08-17 14:58:16.688	1
230	keyword.interference	Interference	2009-08-17 14:58:33.504	2009-08-17 14:58:33.504	1
231	keyword.waveParticleDuality	Wave-Particle Duality	2009-08-17 14:58:59.454	2009-08-17 14:58:59.454	1
232	keyword.tunneling	Tunneling	2009-08-17 14:59:36.121	2009-08-17 14:59:36.121	1
233	keyword.covalentBonds	Covalent Bonds	2009-08-17 15:00:13.694	2009-08-17 15:00:13.694	1
234	keyword.bonds	Bonds	2009-08-17 15:00:26.905	2009-08-17 15:00:26.905	1
235	keyword.superposition	Superposition	2009-08-17 15:00:38.503	2009-08-17 15:00:38.503	1
236	keyword.eating	Eating	2009-08-17 15:01:10.467	2009-08-17 15:01:10.467	1
237	keyword.exercise	Exercise	2009-08-17 15:01:19.453	2009-08-17 15:01:19.453	1
238	keyword.diet	Diet	2009-08-17 15:01:26.166	2009-08-17 15:01:26.166	1
239	keyword.calories	Calories	2009-08-17 15:01:38.874	2009-08-17 15:01:38.874	1
240	keyword.weight	Weight	2009-08-17 15:01:53.556	2009-08-17 15:01:53.556	1
241	keyword.energy	Energy	2009-08-17 15:02:02.887	2009-08-17 15:02:02.887	1
180	keyword.charges	Electric Charges	2009-08-17 14:21:49.334	2009-08-17 15:03:38.976	1
242	keyword.conservationOfEnergy	Conservation of Energy	2009-08-17 15:06:00.69	2009-08-17 15:06:00.69	1
243	keyword.kineticEnergy	Kinetic Energy	2009-08-17 15:06:13.711	2009-08-17 15:06:13.711	1
244	keyword.potentialEnergy	Potential Energy	2009-08-17 15:06:33.652	2009-08-17 15:06:33.652	1
245	keyword.friction	Friction	2009-08-17 15:07:01.8	2009-08-17 15:07:01.8	1
246	keyword.work	Work	2009-08-17 15:07:17.228	2009-08-17 15:07:17.228	1
247	keyword.gravity	Gravity	2009-08-17 15:07:30.914	2009-08-17 15:07:30.914	1
248	keyword.gravitationalForce	Gravitational Force	2009-08-17 15:07:43.555	2009-08-17 15:07:43.555	1
249	keyword.algebra	Algebra	2009-08-17 15:08:16.721	2009-08-17 15:08:16.721	1
250	keyword.estimation	Estimation	2009-08-17 15:08:39.891	2009-08-17 15:08:39.891	1
251	keyword.area	Area	2009-08-17 15:15:52.588	2009-08-17 15:15:52.588	1
252	keyword.length	Length	2009-08-17 15:16:01.068	2009-08-17 15:16:01.068	1
253	keyword.orderOfMagnitude	Order of Magnitude	2009-08-17 15:16:15.326	2009-08-17 15:16:15.326	1
254	keyword.magnetism	Magnetism	2009-08-17 15:16:54.703	2009-08-17 15:16:54.703	1
255	keyword.magneticField	Magnetic Field	2009-08-17 15:17:05.025	2009-08-17 15:17:05.025	1
256	keyword.faradaysLaw	Faraday&#039;s Law	2009-08-17 15:17:23.989	2009-08-17 15:17:23.989	1
257	keyword.power	Power	2009-08-17 15:17:36.231	2009-08-17 15:17:36.231	1
259	keyword.magnets	Magnets	2009-08-17 15:18:20.327	2009-08-17 15:18:20.327	1
260	keyword.transformer	Transformer	2009-08-17 15:18:35.113	2009-08-17 15:18:35.113	1
261	keyword.compass	Compass	2009-08-17 15:18:49.345	2009-08-17 15:18:49.345	1
262	keyword.generator	Generator	2009-08-17 15:19:02.661	2009-08-17 15:19:02.661	1
263	keyword.turbine	Turbine	2009-08-17 15:19:11.139	2009-08-17 15:19:11.139	1
264	keyword.force	Force	2009-08-17 15:20:52.482	2009-08-17 15:20:52.482	1
265	keyword.newtonsLaws	Newton&#039;s Laws	2009-08-17 15:21:36.026	2009-08-17 15:21:36.026	1
266	keyword.dynamics	Dynamics	2009-08-17 15:21:48.227	2009-08-17 15:21:48.227	1
267	keyword.1d	1D	2009-08-17 15:22:01.245	2009-08-17 15:22:01.245	1
268	keyword.harmonicMotion	Harmonic Motion	2009-08-17 15:22:36.768	2009-08-17 15:22:36.768	1
269	keyword.wavelength	Wavelength	2009-08-17 15:22:50.939	2009-08-17 15:22:50.939	1
270	keyword.amplitude	Amplitude	2009-08-17 15:23:13.462	2009-08-17 15:23:13.462	1
271	keyword.frequency	Frequency	2009-08-17 15:23:22.076	2009-08-17 15:23:22.076	1
272	keyword.fourierSeries	Fourier Series	2009-08-17 15:23:33.04	2009-08-17 15:23:33.04	1
273	keyword.fourierAnalysis	Fourier Analysis	2009-08-17 15:23:45.575	2009-08-17 15:23:45.575	1
274	keyword.period	Period	2009-08-17 15:24:01.276	2009-08-17 15:24:01.276	1
275	keyword.uncertaintyPrinciple	Uncertainty Principle	2009-08-17 15:24:20.427	2009-08-17 15:24:20.427	1
276	keyword.wavePackets	Wave Packets	2009-08-17 15:24:32.007	2009-08-17 15:24:32.007	1
277	keyword.heat	Heat	2009-08-17 15:25:03.115	2009-08-17 15:25:03.115	1
278	keyword.pvWork	PV Work	2009-08-17 15:26:00.816	2009-08-17 15:26:00.816	1
279	keyword.idealGasLaw	Ideal Gas Law	2009-08-17 15:26:35.829	2009-08-17 15:26:35.829	1
280	keyword.boltzmannDistribution	Boltzmann Distribution	2009-08-17 15:26:55.134	2009-08-17 15:26:55.134	1
281	keyword.refraction	Refraction	2009-08-17 15:28:45.352	2009-08-17 15:28:45.352	1
282	keyword.lens	Lens	2009-08-17 15:28:51.971	2009-08-17 15:28:51.971	1
283	keyword.vision	Vision	2009-08-17 15:29:06.503	2009-08-17 15:29:06.503	1
284	keyword.images	Images	2009-08-17 15:29:32.699	2009-08-17 15:29:32.699	1
285	keyword.indexOfRefraction	Index of Refraction	2009-08-17 15:30:16.03	2009-08-17 15:30:16.03	1
286	keyword.principleRays	Principle Rays	2009-08-17 15:30:31.775	2009-08-17 15:30:31.775	1
287	keyword.focalLength	Focal Length	2009-08-17 15:30:45.168	2009-08-17 15:30:45.168	1
288	keyword.greenhouseEffect	Greenhouse Effect	2009-08-17 15:32:28.415	2009-08-17 15:32:28.415	1
289	keyword.climate	Climate	2009-08-17 15:32:35.567	2009-08-17 15:32:35.567	1
290	keyword.infrared	Infrared	2009-08-17 15:33:05.389	2009-08-17 15:33:05.389	1
291	keyword.thermalEquilibrium	Thermal Equilibrium	2009-08-17 15:33:21.79	2009-08-17 15:33:21.79	1
292	keyword.rotation	Rotation	2009-08-17 15:34:58.949	2009-08-17 15:34:58.949	1
293	keyword.circularRevolution	Circular Revolution	2009-08-17 15:35:25.477	2009-08-17 15:35:25.477	1
294	keyword.angularPosition	Angular Position	2009-08-17 15:35:38.062	2009-08-17 15:35:38.062	1
295	keyword.angularVelocity	Angular Velocity	2009-08-17 15:36:00.982	2009-08-17 15:36:00.982	1
296	keyword.angularAcceleration	Angular Acceleration	2009-08-17 15:36:13.144	2009-08-17 15:36:13.144	1
297	keyword.stimulatedEmission	Stimulated Emission	2009-08-17 15:37:05.475	2009-08-17 15:37:05.475	1
298	keyword.spontaneousEmission	Spontaneous Emission	2009-08-17 15:37:41.303	2009-08-17 15:37:41.303	1
299	keyword.absorption	Absorption	2009-08-17 15:37:55.413	2009-08-17 15:37:55.413	1
300	keyword.emission	Emission	2009-08-17 15:38:04.259	2009-08-17 15:38:04.259	1
301	keyword.excitation	Excitation	2009-08-17 15:38:18.213	2009-08-17 15:38:18.213	1
302	keyword.cathodeRayTube	Cathode Ray Tube	2009-08-17 15:38:38.786	2009-08-17 15:38:38.786	1
303	keyword.moon	Moon	2009-08-17 15:39:23.141	2009-08-17 15:39:23.141	1
304	keyword.mass	Mass	2009-08-17 15:39:31.606	2009-08-17 15:39:31.606	1
305	keyword.springs	Springs	2009-08-17 15:41:35.092	2009-08-17 15:41:35.092	1
306	keyword.hookesLaw	Hooke&#039;s Law	2009-08-17 15:42:26.9	2009-08-17 15:42:26.9	1
307	keyword.springConstant	Spring Constant	2009-08-17 15:42:39.97	2009-08-17 15:42:39.97	1
308	keyword.microwaves	Microwaves	2009-08-17 15:44:56.359	2009-08-17 15:44:56.359	1
309	keyword.fields	Fields	2009-08-17 15:45:14.772	2009-08-17 15:45:14.772	1
310	keyword.hydrogenAtom	Hydrogen Atom	2009-08-17 15:46:06.053	2009-08-17 15:46:06.053	1
311	keyword.bohrModel	Bohr Model	2009-08-17 15:46:17.106	2009-08-17 15:46:20.993	1
312	keyword.deBroglieWavelength	DeBroglie Wavelength	2009-08-17 15:46:34.225	2009-08-17 15:47:37.238	1
313	keyword.schrodingerModel	Schrodinger Model	2009-08-17 15:47:57.386	2009-08-17 15:47:57.386	1
314	keyword.schrodingerEquation	Schrodinger Equation	2009-08-17 15:48:09.727	2009-08-17 15:48:09.727	1
315	keyword.atoms	Atoms	2009-08-17 15:48:40.676	2009-08-17 15:48:40.676	1
316	keyword.plumPuddingModel	Plum Pudding Model	2009-08-17 15:48:59.352	2009-08-17 15:48:59.352	1
317	keyword.circularMotion	Circular Motion	2009-08-17 15:50:24.45	2009-08-17 15:50:24.45	1
318	keyword.rotationalMotion	Rotational Motion	2009-08-17 15:50:53.968	2009-08-17 15:50:53.968	1
319	keyword.linearMotion	Linear Motion	2009-08-17 15:51:05.88	2009-08-17 15:51:05.88	1
320	keyword.ellipticalMotion	Elliptical Motion	2009-08-17 15:52:09.569	2009-08-17 15:52:09.569	1
321	keyword.planets	Planets	2009-08-17 15:52:40.572	2009-08-17 15:52:40.572	1
322	keyword.satellites	Satellites	2009-08-17 15:52:52.775	2009-08-17 15:52:52.775	1
323	keyword.astronomy	Astronomy	2009-08-17 15:53:03.728	2009-08-17 15:53:03.728	1
324	keyword.resonance	Resonance	2009-08-17 15:56:38.49	2009-08-17 15:56:38.49	1
325	keyword.pendulum	Pendulum	2009-08-17 15:57:48.571	2009-08-17 15:57:48.571	1
326	keyword.simpleHarmonicMotion	Simple Harmonic Motion	2009-08-17 15:58:02.471	2009-08-17 15:58:02.471	1
327	keyword.simpleHarmonicOscillator	Simple Harmonic Oscillator	2009-08-17 15:58:17.848	2009-08-17 15:58:17.848	1
328	keyword.pH	pH	2009-08-17 15:58:54.94	2009-08-17 15:59:02.662	1
329	keyword.hydronium	Hydronium	2009-08-17 16:00:01.342	2009-08-17 16:00:01.342	1
330	keyword.concentration	Concentration	2009-08-17 16:00:17.211	2009-08-17 16:00:17.211	1
331	keyword.hydroxide	Hydroxide	2009-08-17 16:00:29.036	2009-08-17 16:00:29.036	1
332	keyword.moles	Moles	2009-08-17 16:00:41.485	2009-08-17 16:00:41.485	1
333	keyword.molarity	Molarity	2009-08-17 16:00:51.854	2009-08-17 16:00:51.854	1
334	keyword.dilution	Dilution	2009-08-17 16:01:08.635	2009-08-17 16:01:08.635	1
335	keyword.workFunction	Work Function	2009-08-17 16:02:17.532	2009-08-17 16:02:17.532	1
336	keyword.stoppingPotential	Stopping Potential	2009-08-17 16:02:31.453	2009-08-17 16:02:31.453	1
337	keyword.intensity	Intensity	2009-08-17 16:02:47.903	2009-08-17 16:02:47.903	1
338	keyword.probability	Probability	2009-08-17 16:04:19.053	2009-08-17 16:04:19.053	1
339	keyword.binomialDistribution	Binomial Distribution	2009-08-17 16:04:39.407	2009-08-17 16:04:39.407	1
340	keyword.galton	Galton	2009-08-17 16:04:50.579	2009-08-17 16:04:50.579	1
341	keyword.projectileMotion	Projectile Motion	2009-08-17 16:05:35.322	2009-08-17 16:05:35.322	1
342	keyword.airResistance	Air Resistance	2009-08-17 16:06:19.201	2009-08-17 16:06:19.201	1
343	keyword.potentialBarrier	Potential Barrier	2009-08-17 16:08:34.066	2009-08-17 16:08:34.066	1
344	keyword.quantumMeasurement	Quantum Measurement	2009-08-17 16:08:54.725	2009-08-17 16:08:54.725	1
345	keyword.particles	Particles	2009-08-17 16:09:27.209	2009-08-17 16:09:27.209	1
346	keyword.doubleSlit	Double Slit	2009-08-17 16:09:49.188	2009-08-17 16:09:49.188	1
347	keyword.radioWaves	Radio Waves	2009-08-17 16:10:39.909	2009-08-17 16:10:39.909	1
348	keyword.wavePropagation	Wave Propagation	2009-08-17 16:11:11.814	2009-08-17 16:11:11.814	1
349	keyword.rateCoefficients	Rate Coefficients	2009-08-17 16:12:00.238	2009-08-17 16:12:00.238	1
350	keyword.activationEnergy	Activation Energy	2009-08-17 16:12:11.012	2009-08-17 16:12:11.012	1
351	keyword.chemicalEquilibrium	Chemical Equilibrium	2009-08-17 16:12:26.568	2009-08-17 16:12:26.568	1
352	keyword.leChateliersPrinciple	Le Chatelier&#039;s Principle	2009-08-17 16:12:44.402	2009-08-17 16:12:44.402	1
353	keyword.catalysts	Catalysts	2009-08-17 16:12:59.25	2009-08-17 16:12:59.25	1
354	keyword.resistivity	Resistivity	2009-08-17 16:13:31.618	2009-08-17 16:13:31.618	1
355	keyword.inverse	Inverse	2009-08-17 16:13:57.647	2009-08-17 16:13:57.647	1
356	keyword.reaction	Reaction	2009-08-17 16:14:35.142	2009-08-17 16:14:35.142	1
357	keyword.arrheniusParameters	Arrhenius Parameters	2009-08-17 16:14:57.427	2009-08-17 16:14:57.427	1
358	keyword.atomicNuclei	Atomic Nuclei	2009-08-17 16:15:38.801	2009-08-17 16:15:38.801	1
359	keyword.rutherfordScattering	Rutherford Scattering	2009-08-17 16:15:54.664	2009-08-17 16:15:54.664	1
360	keyword.solubility	Solubility	2009-08-17 16:16:28.084	2009-08-17 16:16:28.084	1
361	keyword.salt	Salt	2009-08-17 16:16:38.729	2009-08-17 16:16:38.729	1
362	keyword.solutions	Solutions	2009-08-17 16:16:47.925	2009-08-17 16:16:47.925	1
363	keyword.saturation	Saturation	2009-08-17 16:17:10.238	2009-08-17 16:17:10.238	1
364	keyword.chemicalFormula	Chemical Formula	2009-08-17 16:17:22.84	2009-08-17 16:17:22.84	1
365	keyword.ksp	Ksp	2009-08-17 16:17:31.992	2009-08-17 16:17:31.992	1
366	keyword.ionicCompounds	Ionic Componds	2009-08-17 16:18:30.535	2009-08-17 16:18:30.535	1
367	keyword.dynamicEquilibrium	Dynamic Equilibrium	2009-08-17 16:19:18.027	2009-08-17 16:19:18.027	1
368	keyword.solubilityProduct	Solubility Product	2009-08-17 16:19:36.303	2009-08-17 16:19:36.303	1
369	keyword.orderParameter	Order Parameter	2009-08-17 16:20:40.537	2009-08-17 16:20:40.537	1
370	keyword.criticalParameter	Critical Parameter	2009-08-17 16:20:51.971	2009-08-17 16:20:51.971	1
371	keyword.criticalExponent	Critical Exponent	2009-08-17 16:21:03.36	2009-08-17 16:21:03.36	1
372	keyword.diodes	Diodes	2009-08-17 16:21:49.727	2009-08-17 16:21:49.727	1
373	keyword.transistors	Transistors	2009-08-17 16:22:10.672	2009-08-17 16:22:10.672	1
374	keyword.leds	LEDs	2009-08-17 16:22:31.032	2009-08-17 16:22:31.032	1
375	keyword.doping	Doping	2009-08-17 16:22:41.452	2009-08-17 16:22:41.452	1
376	keyword.mri	MRI	2009-08-17 16:25:18.669	2009-08-17 16:25:18.669	1
377	keyword.magneticMoment	Magnetic Moment	2009-08-17 16:25:42.136	2009-08-17 16:25:42.136	1
378	keyword.spin	Spin	2009-08-17 16:25:50.844	2009-08-17 16:25:50.844	1
379	keyword.sound	Sound	2009-08-17 16:26:17.501	2009-08-17 16:26:17.501	1
380	keyword.pitch	Pitch	2009-08-17 16:26:39.193	2009-08-17 16:26:39.193	1
381	keyword.speakers	Speakers	2009-08-17 16:27:03.11	2009-08-17 16:27:03.11	1
382	keyword.torque	Torque	2009-08-17 16:28:33.166	2009-08-17 16:28:33.166	1
383	keyword.momentOfInertia	Moment of Inertia	2009-08-17 16:28:46.713	2009-08-17 16:28:46.713	1
384	keyword.angularMomentum	Angular Momentum	2009-08-17 16:28:59.898	2009-08-17 16:28:59.898	1
385	keyword.vectorAddition	Vector Addition	2009-08-17 16:29:23.681	2009-08-17 16:29:23.681	1
386	keyword.angle	Angle	2009-08-17 16:29:33.372	2009-08-17 16:29:33.372	1
387	keyword.vectorComponents	Vector Components	2009-08-17 16:29:45.446	2009-08-17 16:29:45.446	1
388	keyword.diffraction	Diffraction	2009-08-17 16:30:24.594	2009-08-17 16:30:24.594	1
389	keyword.transverseWaves	Transverse Waves	2009-08-17 16:31:02.658	2009-08-17 16:31:02.658	1
390	keyword.longitudinalWaves	Longitudinal Waves	2009-08-17 16:31:22.949	2009-08-17 16:31:22.949	1
391	keyword.waves	Waves	2009-08-17 16:32:06.187	2009-08-17 16:32:06.187	1
392	keyword.waveSpeed	Wave Speed	2009-08-17 16:32:17.224	2009-08-17 16:32:17.224	1
393	nav.workshops	Workshops	2009-08-17 19:07:55.301	2009-08-17 19:07:55.301	1
394	workshops.title	Workshops by PhET - How to Teach With Simulations	2009-08-17 19:08:23.885	2009-08-17 19:08:23.885	1
395	nav.contribute	Contribute	2009-08-17 20:10:54.911	2009-08-17 20:10:54.911	1
396	contribute.title	Contribute to PhET	2009-08-17 20:11:03.067	2009-08-17 20:11:03.067	1
397	get-phet.title	Three Ways to Run Our Free Simulations	2009-08-17 20:39:14.373	2009-08-17 20:39:14.373	1
398	nav.get-phet	Run our Simulations	2009-08-17 20:39:28.316	2009-08-17 20:39:28.316	1
399	nav.get-phet.on-line	On Line	2009-08-17 22:13:52.583	2009-08-17 22:13:52.583	1
400	nav.get-phet.full-install	Full Install	2009-08-17 22:27:16.13	2009-08-17 22:27:16.13	1
401	nav.get-phet.one-at-a-time	One at a Time	2009-08-17 22:27:33.19	2009-08-17 22:27:33.19	1
402	get-phet.one-at-a-time.title	Launch PhET Simulations One at a Time	2009-08-17 22:27:53.843	2009-08-17 22:27:53.843	1
403	get-phet.full-install.title	PhET Offline Installer	2009-08-17 22:28:13.663	2009-08-17 22:28:13.663	1
18	simulationPage.title	{0} - {1}, {2}, {3} - PhET	2009-08-17 03:48:19.899	2009-08-17 23:15:24.156	1
404	research.title	Research	2009-08-19 00:19:29.708	2009-08-19 00:19:29.708	1
405	nav.research	Research	2009-08-19 00:19:35.118	2009-08-19 00:19:35.118	1
406	troubleshooting.java.title	Troubleshooting Java	2009-08-19 00:38:14.998	2009-08-19 00:38:14.998	1
407	troubleshooting.flash.title	Troubleshooting Flash	2009-08-19 00:38:23.703	2009-08-19 00:38:23.703	1
408	troubleshooting.javascript.title	Troubleshooting JavaScript	2009-08-19 00:38:35.355	2009-08-19 00:38:35.355	1
409	nav.troubleshooting.javascript	JavaScript	2009-08-19 00:38:54.069	2009-08-19 00:38:54.069	1
410	nav.troubleshooting.java	Java	2009-08-19 00:38:58.745	2009-08-19 00:38:58.745	1
411	nav.troubleshooting.flash	Flash	2009-08-19 00:39:06.905	2009-08-19 00:39:06.905	1
412	about.source-code.title	PhET Source Code	2009-08-19 01:32:35.704	2009-08-19 01:32:35.704	1
413	nav.about.source-code	Source Code	2009-08-19 01:32:46.865	2009-08-19 01:32:46.865	1
1054	simulationMainPanel.softwareRequirements	Software Requirements	2009-09-03 15:06:57.928	2009-09-03 15:06:57.928	1
416	nav.about.contact	Contact	2009-08-19 01:34:06.066	2009-08-19 01:34:06.066	1
417	about.contact.title	Contact	2009-08-19 01:34:10.998	2009-08-19 01:34:10.998	1
418	about.who-we-are.title	Who We Are	2009-08-19 01:34:21.682	2009-08-19 01:34:21.682	1
419	nav.about.who-we-are	Who We Are	2009-08-19 01:34:32.249	2009-08-19 01:34:32.249	1
420	about.licensing.title	Licensing	2009-08-19 01:34:54.444	2009-08-19 01:34:54.444	1
421	nav.about.licensing	Licensing	2009-08-19 01:35:02.894	2009-08-19 01:35:02.894	1
422	home.runOurSims	我们的模拟运行	2009-08-19 02:06:50.006	2009-08-19 02:06:50.006	2
423	home.onLine	在线	2009-08-19 02:07:04	2009-08-19 02:07:04	2
424	home.fullInstallation	完全安装	2009-08-19 02:07:13.454	2009-08-19 02:07:13.454	2
425	home.oneAtATime	一次一个	2009-08-19 02:08:18.114	2009-08-19 02:08:18.114	2
426	home.teacherIdeasAndActivities	教师的思想和活动	2009-08-19 02:08:23.563	2009-08-19 02:08:23.563	2
427	home.workshops	讲习班	2009-08-19 02:08:29.765	2009-08-19 02:08:29.765	2
428	home.contribute	贡献	2009-08-19 02:08:35.619	2009-08-19 02:08:35.619	2
429	home.supportPhet	支持PhET	2009-08-19 02:08:48.702	2009-08-19 02:08:48.702	2
431	home.browseSims	浏览模拟	2009-08-19 02:09:39.57	2009-08-19 02:09:39.57	2
432	home.simulations	模拟	2009-08-19 02:09:46.567	2009-08-19 02:09:46.567	2
430	home.translateSimulations	UNUSED	2009-08-19 02:09:33.757	2009-08-19 02:09:51.593	2
433	nav.workshops	讲习班	2009-08-19 02:10:31.586	2009-08-19 02:10:31.586	2
434	nav.get-phet	我们的模拟运行	2009-08-19 02:10:40.306	2009-08-19 02:10:40.306	2
436	nav.get-phet.full-install	完全安装	2009-08-19 02:10:52.851	2009-08-19 02:11:04.296	2
435	nav.get-phet.on-line	在线	2009-08-19 02:10:45.777	2009-08-19 02:11:11.01	2
437	nav.contribute	贡献	2009-08-19 02:11:19.388	2009-08-19 02:11:19.388	2
438	nav.get-phet.one-at-a-time	一次一个	2009-08-19 02:11:28.145	2009-08-19 02:11:28.145	2
5	home.subheader	Fun, interactive, <a {0}>research-based</a> simulations of physical phenomena from the PhET project at the University of Colorado.	2009-08-17 03:48:19.897	2009-08-30 01:09:02.419	1
1055	simulationMainPanel.sampleLearningGoals	Sample Learning Goals	2009-09-03 15:16:26.07	2009-09-03 15:16:26.07	1
771	troubleshooting.java.faqs	FAQs	2009-09-01 01:08:44.28	2009-09-01 01:08:44.28	1
1056	simulationMainPanel.credits	Credits	2009-09-03 15:19:12.891	2009-09-03 15:19:12.891	1
832	troubleshooting.flash.intro	This page will help you solve some of the problems people commonly have running our programs. If you can't solve your problem here, please notify us by email at the following address:<a href="mailto:phethelp@colorado.edu"><span class="red">phethelp@colorado.edu</span></a>.	2009-09-01 01:23:54.848	2009-09-01 01:23:54.848	1
1057	simulationMainPanel.designTeam	Design Team	2009-09-03 15:31:37.976	2009-09-03 15:31:37.976	1
1058	simulationMainPanel.thirdPartyLibraries	Third-party Libraries	2009-09-03 15:31:48.164	2009-09-03 15:31:48.164	1
833	troubleshooting.flash.toRun	To run the Flash-based simulations you must have Macromedia Flash 8 (available free) or newer installed on your computer.	2009-09-01 01:24:38.131	2009-09-01 01:24:38.131	1
834	troubleshooting.flash.blankWindow	If you get a blank window when you try to launch a Flash simulation, you probably need a new version of the Flash player.	2009-09-01 01:25:20.954	2009-09-01 01:25:20.954	1
835	troubleshooting.flash.olderVersions	Older versions of the Flash Player can cause problems. Updating your flash player is recommended if you receive an error similar to:	2009-09-01 01:26:22.296	2009-09-01 01:26:22.296	1
836	troubleshooting.javascript.intro	JavaScript is a programming language that is mostly used in web pages, usually to add features that make the web page more interactive. JavaScript is required to submit information to PhET.	2009-09-01 01:33:01.375	2009-09-01 01:33:01.375	1
874	home.playWithSims	لعب مع المحاكاة	2009-09-03 10:39:29.188	2009-09-03 10:39:29.188	4
841	troubleshooting.javascript.q1.no	<p>JavaScript is <strong>NOT</strong> enabled.</p>	2009-09-01 01:40:05.595	2009-09-01 01:40:05.595	1
873	home.subheader	متعة ، والتفاعلية ، <a {0}>القائمة على البحوث </a> محاكاة الظواهر الفيزيائية من المشروع PhET في جامعة كولورادو	2009-09-03 10:37:47.308	2009-09-03 10:39:11.615	4
875	home.runOurSims	لدينا تشغيل المحاكاة	2009-09-03 10:43:07.536	2009-09-03 10:43:07.536	4
876	home.onLine	لدينا تشغيل المحاكاة	2009-09-03 10:43:23.455	2009-09-03 10:43:23.455	4
877	home.fullInstallation	التثبيت الكامل	2009-09-03 10:43:43.845	2009-09-03 10:43:43.845	4
839	troubleshooting.javascript.faqs	FAQs	2009-09-01 01:36:51.51	2009-09-01 01:36:51.51	1
840	troubleshooting.javascript.q1.yes	<p>JavaScript <strong>IS</strong> enabled.</p>	2009-09-01 01:39:59.768	2009-09-01 01:40:21.8	1
838	troubleshooting.javascript.notify	If you can''t solve your problem here, please notify us by email at the following address: {0}.	2009-09-01 01:36:15.385	2009-09-01 02:41:08.035	1
837	troubleshooting.javascript.notJava	JavaScript is completely different than Sun Microsystems'' Java Web Start that we use on our simulations. For assistance with Sun Microsystems'' Java, <a {0}>go here</a>.	2009-09-01 01:35:48.665	2009-09-01 02:41:18.006	1
1077	language.names.es_PE	Spanish (Peru)	2009-09-03 18:56:41.249	2009-09-03 18:56:41.249	1
1078	language.names.en	English	2009-09-03 18:56:41.303	2009-09-03 18:56:41.303	1
842	troubleshooting.javascript.q1.title	Is JavaScript enabled on my browser?	2009-09-01 01:53:18.343	2009-09-01 01:53:18.343	1
843	troubleshooting.javascript.q2.title	Why is JavaScript needed?	2009-09-01 01:53:41.85	2009-09-01 02:15:04.37	1
844	troubleshooting.javascript.q3.title	How do I enable JavaScript on Firefox?	2009-09-01 02:15:17.275	2009-09-01 02:15:17.275	1
845	troubleshooting.javascript.q4.title	How do I enable JavaScript on Internet Explorer?	2009-09-01 02:15:25.227	2009-09-01 02:15:25.227	1
846	troubleshooting.javascript.q5.title	How do I enable JavaScript on Safari?	2009-09-01 02:15:35.122	2009-09-01 02:15:35.122	1
847	troubleshooting.javascript.q2.answer	JavaScript is used with many features on the website, including filtering routines and allowing for multiple inputs of data. Howevery, many pages will work fine without JavaScript enabled.	2009-09-01 02:17:56.2	2009-09-01 02:17:56.2	1
848	troubleshooting.javascript.q3.answer	<ol><li>Go to the "Edit" menu and select "Preferences"</li><li>Select the "Content" tab at the top of the Preferences window</li><li>Make sure the "Enable JavaScript" checkbox is checked</li><li>Reload this page, and see <a {0}>question 1</a></li></ol>	2009-09-01 02:19:23.196	2009-09-01 02:19:23.196	1
849	troubleshooting.javascript.q4.answer	<ol><li>Go to the <strong>Tools</strong> menu and select <strong>Internet Options...</strong></li><li>Select the <strong>Security</strong> tab</li><li>Select on the <strong>Custom Level...</strong> button</li><li>Scroll down until you see <strong>Active Scripting</strong> under the <strong>Scripting</strong>heading</li><li>Make sure that it is set to <strong>Enabled</strong></li><li>You may be asked if you are sure <em>you want change the security settings for this zone.</em> You will want to select the <strong>Yes</strong> button</li><li>Select the <strong>OK</strong> button on the original <strong>Internet Options</strong> window</li><li>Reload the page (you can check if it worked by reading <a {0}>this question</a>)</li></ol>	2009-09-01 02:23:24.623	2009-09-01 02:23:24.623	1
850	troubleshooting.javascript.q5.answer	<ol><li>Select Preferences from the Safari menu.</li><li>Click Security.</li><li>Check Enable JavaScript.</li><li>Close the window.</li><li>Click Reload.</li></ol>	2009-09-01 02:23:46.429	2009-09-01 02:23:46.429	1
1059	simulationMainPanel.thanksTo	Thanks To	2009-09-03 15:31:55.46	2009-09-03 15:31:56.771	1
709	nav.sponsors	Sponsors	2009-08-31 20:04:14.363	2009-08-31 20:04:14.363	1
1699	get-phet.online.header	Use the PhET website while connected to the web	2009-09-03 23:31:03.794	2009-09-03 23:31:03.794	1
710	sponsors.title	PhET Sponsors	2009-08-31 20:04:26.799	2009-08-31 20:04:26.799	1
1700	get-phet.install.header	Download the <strong>entire</strong> website onto your computer, USB, or CD	2009-09-03 23:31:17.126	2009-09-03 23:31:17.126	1
1701	get-phet.offline.header	Download <strong>one or more</strong> simulations to your computer, USB, or CD	2009-09-03 23:31:29.645	2009-09-03 23:31:29.645	1
1702	get-phet.clickHere	Click Here &gt;&gt;	2009-09-03 23:32:01.079	2009-09-03 23:32:01.079	1
1703	get-phet.row.howToGet	How do I get the simulations?	2009-09-03 23:32:17.257	2009-09-03 23:32:17.257	1
1704	get-phet.online.howToGet	Click on the simulation icon on the web page of the simulation you want to run.	2009-09-03 23:32:28.358	2009-09-03 23:32:28.358	1
1705	get-phet.row.whatSims	What simulations are installed?	2009-09-03 23:32:40.659	2009-09-03 23:32:40.659	1
878	home.oneAtATime	في وقت واحد	2009-09-03 10:45:56.788	2009-09-03 10:45:56.788	4
879	home.teacherIdeasAndActivities	المعلم الأفكار والنشاطا	2009-09-03 10:46:05.404	2009-09-03 10:46:05.404	4
880	home.workshops	حلقات العمل	2009-09-03 10:46:13.826	2009-09-03 10:46:13.826	4
881	home.contribute	المساهمة	2009-09-03 10:46:21.164	2009-09-03 10:46:21.164	4
882	home.supportPhet	PhET الدعم	2009-09-03 10:46:35.008	2009-09-03 10:46:35.008	4
883	home.translateSimulations	ترجمة المحاكاة	2009-09-03 10:46:46.243	2009-09-03 10:46:46.243	4
884	home.browseSims	استعرض سيمز	2009-09-03 10:46:55.144	2009-09-03 10:46:55.144	4
1706	get-phet.online.whatSims	The simulations you use are temporarily installed.	2009-09-03 23:32:50.412	2009-09-03 23:32:50.412	1
1707	get-phet.install.whatSims	All simulations.	2009-09-03 23:33:00.778	2009-09-03 23:33:00.778	1
1708	get-phet.offline.whatSims	Only the ones you want.	2009-09-03 23:33:10.081	2009-09-03 23:33:10.081	1
1709	get-phet.row.howLarge	How large is the download?	2009-09-03 23:33:19.679	2009-09-03 23:33:19.679	1
1710	get-phet.online.howLarge	Under 1.5 MB for each simulation.	2009-09-03 23:33:28.595	2009-09-03 23:33:28.595	1
1711	get-phet.install.howLarge	Approximately 40MB if you already have Java, or 65MB if you do not.	2009-09-03 23:33:38.656	2009-09-03 23:33:38.656	1
1712	get-phet.offline.howLarge	Under 1.5 MB for each simulation.	2009-09-03 23:33:48.31	2009-09-03 23:33:48.31	1
1713	get-phet.row.howOften	How often are updates made available?	2009-09-03 23:33:56.396	2009-09-03 23:33:56.396	1
1714	get-phet.online.howOften	As soon as new or updated simulations are posted on the web site.	2009-09-03 23:34:09.137	2009-09-03 23:34:09.137	1
1715	get-phet.install.howOften	Three or four times a year.	2009-09-03 23:34:20.809	2009-09-03 23:34:29.14	1
1716	get-phet.offline.howOften	If you connect to the Internet, new or updated simulations are available.	2009-09-03 23:34:40.074	2009-09-03 23:34:40.074	1
1717	get-phet.row.needInternet	Is an Internet connection required to run simulations?	2009-09-03 23:34:49.746	2009-09-03 23:34:49.746	1
1718	get-phet.online.needInternet	Yes	2009-09-03 23:34:59.871	2009-09-03 23:34:59.871	1
885	home.simulations	المحاكاة	2009-09-03 10:47:03.361	2009-09-03 10:47:03.361	4
886	nav.home	منزل	2009-09-03 10:47:33.133	2009-09-03 10:47:33.133	4
887	nav.simulations	المحاكاة	2009-09-03 10:47:39.228	2009-09-03 10:47:39.228	4
888	nav.featured	ظهرت المحاكاة	2009-09-03 10:47:48.252	2009-09-03 10:47:48.252	4
889	nav.new	المحاكاة الجديدة	2009-09-03 10:47:56.835	2009-09-03 10:47:56.835	4
890	nav.physics	الفيزياء	2009-09-03 10:48:04.112	2009-09-03 10:48:04.112	4
891	nav.motion	اقتراح	2009-09-03 10:48:11.638	2009-09-03 10:48:11.638	4
892	nav.sound-and-waves	وصوت الأموا	2009-09-03 10:48:24.96	2009-09-03 10:48:24.96	4
893	nav.work-energy-and-power	العمل الطاقة والكهرباء	2009-09-03 10:48:43.427	2009-09-03 10:48:43.427	4
894	nav.heat-and-thermodynamics	الحرارة والديناميكا الحرارية	2009-09-03 10:48:55.023	2009-09-03 10:48:55.023	4
895	nav.quantum-phenomena	الكم الظواهر	2009-09-03 10:49:02.407	2009-09-03 10:49:02.407	4
896	nav.light-and-radiation	الضوء والإشعا	2009-09-03 10:49:12.947	2009-09-03 10:49:12.947	4
897	nav.electricity-magnets-and-circuits	الكهرباء والمغناطيس الدوائر	2009-09-03 10:49:24.537	2009-09-03 10:49:24.537	4
898	nav.biology	علم الأحيا	2009-09-03 10:49:41.406	2009-09-03 10:49:41.406	4
899	nav.chemistry	الكيمياء	2009-09-03 10:49:49.682	2009-09-03 10:49:49.682	4
900	nav.earth-science	علوم الأر	2009-09-03 10:49:57.842	2009-09-03 10:49:57.842	4
901	nav.math	الرياضيات	2009-09-03 10:50:06.19	2009-09-03 10:50:06.19	4
902	nav.tools	أدوات	2009-09-03 10:50:14.437	2009-09-03 10:50:14.437	4
903	nav.applications	تطبيقات	2009-09-03 10:50:23.046	2009-09-03 10:50:23.046	4
904	nav.cutting-edge-research	أحدث البحوث	2009-09-03 10:50:25.689	2009-09-03 10:50:32.946	4
905	nav.all	جميع المحاكاة	2009-09-03 10:50:42.9	2009-09-03 10:50:42.9	4
906	nav.workshops	حلقات العمل	2009-09-03 10:50:52.438	2009-09-03 10:50:52.438	4
711	keyword.sun	Sun	2009-08-31 20:55:52.066	2009-08-31 20:55:52.066	1
712	keyword.switches	Switches	2009-08-31 20:58:55.33	2009-08-31 20:58:55.33	1
713	keyword.acVoltage	AC Voltage	2009-08-31 20:59:32.861	2009-08-31 20:59:32.861	1
1060	simulationMainPanel.language	Language	2009-09-03 15:52:50.58	2009-09-03 15:52:50.58	1
1061	simulationMainPanel.languageTranslated	Language (Translated)	2009-09-03 15:53:02.934	2009-09-03 15:53:02.934	1
1062	simulationMainPanel.simulationTitleTranslated	Simulation Title (Translated)	2009-09-03 15:53:11.797	2009-09-03 15:53:11.797	1
1719	get-phet.install.needInternet	No	2009-09-03 23:35:25.222	2009-09-03 23:35:25.222	1
1720	get-phet.offline.needInternet	No	2009-09-03 23:35:41.37	2009-09-03 23:35:41.37	1
1721	get-phet.row.whereCanISave	Where can I save them to on my computer?	2009-09-03 23:35:51.922	2009-09-03 23:35:51.922	1
907	nav.get-phet	لدينا تشغيل المحاكاة	2009-09-03 10:50:59.481	2009-09-03 10:50:59.481	4
908	nav.get-phet.on-line	على الانترن	2009-09-03 10:51:08.403	2009-09-03 10:51:08.403	4
909	nav.get-phet.full-install	التثبيت الكامل	2009-09-03 10:51:18.07	2009-09-03 10:51:18.07	4
910	nav.get-phet.one-at-a-time	في وقت واحد	2009-09-03 10:51:26.7	2009-09-03 10:51:26.7	4
911	nav.troubleshooting.main	مخرج	2009-09-03 10:51:34.107	2009-09-03 10:51:34.107	4
912	nav.troubleshooting.java	Java	2009-09-03 10:51:41.987	2009-09-03 10:51:41.987	4
913	nav.troubleshooting.flash	Flash	2009-09-03 10:51:45.969	2009-09-03 10:51:45.969	4
914	nav.troubleshooting.javascript	JavaScript	2009-09-03 10:51:48.195	2009-09-03 10:51:48.195	4
915	nav.contribute	المساهمة	2009-09-03 10:51:55.648	2009-09-03 10:51:55.648	4
916	nav.research	بحث	2009-09-03 10:52:02.535	2009-09-03 10:52:02.535	4
917	nav.about	حول PhET	2009-09-03 10:52:15.4	2009-09-03 10:52:15.4	4
714	keyword.rainbows	Rainbows	2009-08-31 21:01:04.609	2009-08-31 21:01:04.609	1
214	keyword.monochromatic	Monochromatic Light	2009-08-17 14:51:23.734	2009-08-31 21:01:49.574	1
918	nav.about.source-code	مصدر القانون	2009-09-03 10:52:22.839	2009-09-03 10:52:22.839	4
715	keyword.splitting	Splitting	2009-08-31 21:03:52.187	2009-08-31 21:03:52.187	1
716	keyword.ammonia	Ammonia Molecules	2009-08-31 21:04:19.467	2009-08-31 21:04:19.467	1
717	keyword.spinHalfParticles	Spin 1/2 Particles	2009-08-31 21:04:51.944	2009-08-31 21:04:51.944	1
718	keyword.biology	Biology	2009-08-31 21:05:28.818	2009-08-31 21:05:28.818	1
719	keyword.food	Food	2009-08-31 21:05:44.549	2009-08-31 21:05:44.549	1
720	keyword.sines	Sines	2009-08-31 21:09:48.04	2009-08-31 21:09:48.04	1
721	keyword.cosines	Cosines	2009-08-31 21:09:52.659	2009-08-31 21:09:52.659	1
722	keyword.charlesLaw	Charles&#039; Law	2009-08-31 21:11:12.264	2009-08-31 21:11:12.264	1
723	keyword.greenhouseGases	Greenhouse Gases	2009-08-31 21:13:59.617	2009-08-31 21:13:59.617	1
724	keyword.photonBeams	Photon Beams	2009-08-31 21:14:55.787	2009-08-31 21:14:55.787	1
725	keyword.lunarLanding	Lunar Landing	2009-08-31 21:15:28.512	2009-08-31 21:15:28.512	1
726	keyword.thrust	Thrust	2009-08-31 21:15:48.829	2009-08-31 21:15:48.829	1
727	keyword.fuelConsumption	Fuel Consumption	2009-08-31 21:16:05.756	2009-08-31 21:16:05.756	1
258	keyword.electromagnet	Electromagnets	2009-08-17 15:17:56.273	2009-08-31 21:17:14.557	1
728	keyword.measurement	Measurement	2009-08-31 21:19:12.102	2009-08-31 21:19:12.102	1
729	keyword.molecules	Molecules	2009-08-31 21:21:59.294	2009-08-31 21:21:59.294	1
730	keyword.pulses	Pulses	2009-08-31 21:25:13.851	2009-08-31 21:25:13.851	1
731	keyword.periodicMotion	Periodic Motion	2009-08-31 21:26:00.778	2009-08-31 21:26:00.778	1
732	keyword.acids	Acids	2009-08-31 21:27:04.55	2009-08-31 21:27:04.55	1
733	keyword.bases	Bases	2009-08-31 21:27:10.323	2009-08-31 21:27:10.323	1
734	keyword.histograms	Histograms	2009-08-31 21:28:21.048	2009-08-31 21:28:21.048	1
735	keyword.initialSpeed	Initial Speed	2009-08-31 21:28:52.897	2009-08-31 21:28:52.897	1
736	keyword.quantumParticles	Quantum Particles	2009-08-31 21:29:29.48	2009-08-31 21:29:29.48	1
737	keyword.equilibrium	Equilibrium	2009-08-31 21:31:50.172	2009-08-31 21:31:50.172	1
738	keyword.alphaParticles	Alpha Particles	2009-08-31 21:33:20.743	2009-08-31 21:33:20.743	1
739	keyword.signalCircuits	Signal Circuits	2009-08-31 21:35:35.057	2009-08-31 21:35:35.057	1
919	nav.about.contact	جهة اتصال	2009-09-03 10:52:30.719	2009-09-03 10:52:30.719	4
920	nav.about.who-we-are	من نحن	2009-09-03 10:52:38.623	2009-09-03 10:52:38.623	4
921	nav.about.licensing	الترخيص	2009-09-03 10:52:46.145	2009-09-03 10:52:46.145	4
922	nav.sponsors	مقدمو	2009-09-03 10:52:54.721	2009-09-03 10:52:54.721	4
923	simulationMainPanel.translatedVersions	النسخ المترجمة	2009-09-03 10:53:13.538	2009-09-03 10:53:13.538	4
924	simulationMainPanel.screenshot.alt	{0} قطة	2009-09-03 10:53:24.299	2009-09-03 10:53:24.299	4
925	simulationMainPanel.version	الإصدا {0}	2009-09-03 10:53:36.74	2009-09-03 10:53:36.74	4
926	simulationMainPanel.kilobytes	{0} kB	2009-09-03 10:53:44.621	2009-09-03 10:53:44.621	4
927	simulationMainPanel.runOffline	تنزيل	2009-09-03 10:53:56.323	2009-09-03 10:53:56.323	4
928	simulationMainPanel.runOnline	تشغيل الآ	2009-09-03 10:54:07.434	2009-09-03 10:54:07.434	4
929	simulationMainPanel.topics	المواضيع	2009-09-03 10:54:16.133	2009-09-03 10:54:16.133	4
930	simulationMainPanel.mainTopics	موضوعات رئيسية	2009-09-03 10:54:25.083	2009-09-03 10:54:25.083	4
931	simulationMainPanel.keywords	موضوعات ذات صلة	2009-09-03 10:54:35.247	2009-09-03 10:54:35.247	4
1018	contribute.main	Our philosophy is to make PhET simulations freely available to all users around the world. They have now been run millions of times from our web site, and the full PhET suite has been installed on thousands of computers. But while the simulations may be free to users, they are expensive for us to create, test and maintain, and our financial support is limited. If you would like to help make it possible for us to develop more and better simulations, please contact {0} or phone (303-492-4367) to find out how you can make a tax deductible contribution.	2009-09-03 12:36:45.601	2009-09-03 12:37:07.289	1
1019	contribute.thanks	PhET would like to thank <a {0}>our sponsors</a>, and <a {1}>Royal Interactive</a> for original site design and layout.	2009-09-03 12:40:14.829	2009-09-03 12:40:14.829	1
1020	sponsors.header	PhET would like to thank the following organizations, companies, and individuals for their support:	2009-09-03 12:42:38.421	2009-09-03 12:42:38.421	1
1021	sponsors.financialSupport	Financial Support	2009-09-03 12:43:24.389	2009-09-03 12:43:24.389	1
1022	sponsors.platinum	Platinum Level Contributors ($100k+)	2009-09-03 12:44:05.007	2009-09-03 12:44:05.007	1
1023	sponsors.gold	Gold Level Contributors ($10k - $100k)	2009-09-03 12:44:44.592	2009-09-03 12:44:44.592	1
1024	sponsors.bronze	Bronze Level Contributors ($0 - $10k)	2009-09-03 12:46:51.067	2009-09-03 12:46:51.067	1
1025	sponsors.technicalSupport	Technical Support	2009-09-03 12:47:23.811	2009-09-03 12:47:23.811	1
1026	sponsors.piccolo.desc	An open source graphics library	2009-09-03 12:47:59.623	2009-09-03 12:47:59.623	1
1027	sponsors.jfreechart.desc	An open source chart library	2009-09-03 12:48:34.015	2009-09-03 12:48:34.015	1
1028	sponsors.jade.desc	A dynamics engine	2009-09-03 12:49:03.667	2009-09-03 12:49:03.667	1
1029	sponsors.sourceforge.desc	For hosting our source code repository	2009-09-03 12:49:39.901	2009-09-03 12:49:39.901	1
1030	sponsors.proguard.desc	An open source tool for code shrinking	2009-09-03 12:50:09.933	2009-09-03 12:50:09.933	1
1031	sponsors.jetbrains.desc	For providing for our Java development environment	2009-09-03 12:50:39.214	2009-09-03 12:50:39.214	1
439	language.dir	ltr	2009-08-31 14:12:59.195	2009-08-31 17:32:04.41	3
1032	sponsors.ej.desc	For providing for our Java profilers	2009-09-03 12:51:08.797	2009-09-03 12:51:08.797	1
932	workshops.upcomingWorkshops	القادمة ورشات عمل	2009-09-03 11:04:18.619	2009-09-03 11:04:18.619	4
933	workshops.intro	أعضاء فريق PhET بانتظام تقديم ورش عمل حول كيفية تدريس مع شركائنا في جميع أنحاء ولاية كولورادو ، والمحاكاة في اجتماعات وطنية.	2009-09-03 11:04:22.132	2009-09-03 11:04:22.132	4
742	workshops.intro	Members of the PhET team regularly offer workshops on how to teach with our simulations throughout Colorado and at national meetings.	2009-08-31 23:56:26.054	2009-08-31 23:56:26.054	1
743	workshops.upcomingWorkshops	Upcoming Workshops	2009-08-31 23:57:07.094	2009-08-31 23:57:07.094	1
744	workshops.materials	Workshop Materials	2009-08-31 23:58:00.723	2009-08-31 23:58:00.723	1
745	workshops.pastWorkshops	Schedule of Past Workshops	2009-08-31 23:58:41.953	2009-08-31 23:58:41.953	1
934	workshops.materials	مواد ورشة عمل	2009-09-03 11:04:24.13	2009-09-03 11:04:24.13	4
935	workshops.pastWorkshops	الجدول الزمني لحلقات العمل السابقة	2009-09-03 11:04:25.499	2009-09-03 11:04:25.499	4
936	sponsors.principalSponsors	قائمة الجهات الرئيسية الراعية	2009-09-03 11:04:30.096	2009-09-03 11:04:30.096	4
937	sponsors.hewlett	منحا للتصدي لأخطر المشاكل الاجتماعية والبيئية التي تواجه المجتمع ، حيث رأس المال المخاطر ، استثمرت بمسؤولية ، يمكن أن تحدث فرقا على مر الزمن.	2009-09-03 11:04:32.257	2009-09-03 11:04:32.257	4
938	sponsors.nsf	وكالة فدرالية مستقلة أنشأها الكونغرس في عام 1950 لتعزيز تقدم العلم.	2009-09-03 11:04:34.108	2009-09-03 11:04:34.108	4
939	sponsors.ksu	جامعة الملك سعود تسعى إلى أن تصبح الشركة الرائدة في الابتكار في مجال التعليم والتكنولوجية والاكتشافات العلمية والإبداع من خلال تهيئة الاجواء للإلهام الفكري والشراكة من أجل رخاء المجتمع.	2009-09-03 11:04:36.49	2009-09-03 11:04:36.49	4
940	sponsors.otherSponsors	ومن الرعاة الآخرين&gt;&gt;	2009-09-03 11:04:38.786	2009-09-03 11:04:56.296	4
944	troubleshooting.main.intro	هذه الصفحة سوف تساعدك على حل بعض مشاكل الناس عادة يكون لدينا برامج تشغيل. إذا كنت حل مشكلتك هنا ، يرجى إعلامنا عن طريق البريد الالكتروني على العنوان الالكتروني التالي : {0}	2009-09-03 11:06:38.552	2009-09-03 11:07:32.182	4
945	troubleshooting.main.java	جافا تثبيت واستكشاف الأخطاء وإصلاحها	2009-09-03 11:07:36.615	2009-09-03 11:07:36.615	4
941	get-phet.one-at-a-time.runningSims	انقر على اسم أي محاكاة لتحميل محاكاة على الكمبيوتر. لتشغيل المحاكاة ، انقر نقرا مزدوجا فوقه. إذا كان لديك صعوبات ، يرجى الاطلاع على <a {0}> الدعم التقني </a> صفحة.	2009-09-03 11:05:47.402	2009-09-03 11:06:17.053	4
942	get-phet.one-at-a-time.downloadingSims	كنت قد نسخ المحاكاة إلى أي مكان على الكمبيوتر. يمكنك أيضا نسخ المحاكاة إلى محرك الأقراص المضغوطة ،... حملة ، أو موقع في شبكة مشتركة.	2009-09-03 11:06:21.034	2009-09-03 11:06:21.034	4
1722	get-phet.online.whereCanISave	Flash applets cannot be saved. Java applications are automatically stored in your computer's WebStart cache, but cannot be moved.	2009-09-03 23:36:15.573	2009-09-03 23:36:15.573	1
1079	language.names.ja_JP_JP	Japanese (Japan,JP)	2009-09-03 18:56:41.33	2009-09-03 18:56:41.33	1
1723	get-phet.install.whereCanISave	CD, USB or hard drive.	2009-09-03 23:36:25.244	2009-09-03 23:36:25.244	1
1080	language.names.es_PA	Spanish (Panama)	2009-09-03 18:56:41.36	2009-09-03 18:56:41.36	1
1081	language.names.sr_BA	Serbian (Bosnia and Herzegovina)	2009-09-03 18:56:41.436	2009-09-03 18:56:41.436	1
943	troubleshooting.main.title	مخرج -- PhET المحاكاة	2009-09-03 11:06:31.034	2009-09-03 11:06:31.034	4
946	troubleshooting.main.flash	فلاش تثبيت واستكشاف الأخطاء وإصلاحها	2009-09-03 11:07:39.286	2009-09-03 11:07:39.286	4
950	troubleshooting.main.q1.answer	<p>بعض من عمليات المحاكاة هي بدء جاوة الويب وغيرها من التطبيقات القائمة على استخدام Macromedia' فلاش لاعب. فلاش يأتي مع معظم أجهزة الكمبيوتر في حين بدء جاوة الويب هو تطبيق المجانية التي يمكن تحميلها من صن مايكروسيستمز. لتشغيل جافا القائمة على المحاكاة يجب أن يكون جافا الإصدار 1.5 أو أعلى مثبتة على جهاز الكمبيوتر الخاص بك.</p><p><a {0}>تعلم جافا التثبيت واستكشاف هنا</a></p>	2009-09-03 11:08:29.152	2009-09-03 11:08:29.152	4
947	troubleshooting.main.javascript	أخطاء جافا سكريبت (ملاحظة : هذا هو المتصفح لديك ، وليس المحاكاة)	2009-09-03 11:07:40.561	2009-09-03 11:07:40.561	4
948	troubleshooting.main.faqs	أسئلة وأجوبة	2009-09-03 11:07:42.766	2009-09-03 11:07:42.766	4
949	troubleshooting.main.q1.title	لماذا يمكنني تشغيل بعض من عمليات المحاكاة ولكن ليس كلها؟	2009-09-03 11:07:44.534	2009-09-03 11:07:44.534	4
951	troubleshooting.main.q2.title	ما هي متطلبات النظام لتشغيل محاكيات PhET؟	2009-09-03 11:08:38.67	2009-09-03 11:08:38.67	4
1033	sponsors.bitrock.desc	For providing for our installer builder	2009-09-03 12:51:39.675	2009-09-03 12:51:39.675	1
1082	language.names.mk	Macedonian	2009-09-03 18:56:41.535	2009-09-03 18:56:41.535	1
1083	language.names.es_GT	Spanish (Guatemala)	2009-09-03 18:56:41.569	2009-09-03 18:56:41.569	1
1084	language.names.ar_AE	Arabic (United Arab Emirates)	2009-09-03 18:56:41.578	2009-09-03 18:56:41.578	1
1085	language.names.no_NO	Norwegian (Norway)	2009-09-03 18:56:41.591	2009-09-03 18:56:41.591	1
1086	language.names.sq_AL	Albanian (Albania)	2009-09-03 18:56:41.608	2009-09-03 18:56:41.608	1
1087	language.names.bg	Bulgarian	2009-09-03 18:56:41.619	2009-09-03 18:56:41.619	1
1088	language.names.ar_IQ	Arabic (Iraq)	2009-09-03 18:56:41.63	2009-09-03 18:56:41.63	1
1089	language.names.ar_YE	Arabic (Yemen)	2009-09-03 18:56:41.642	2009-09-03 18:56:41.642	1
1090	language.names.hu	Hungarian	2009-09-03 18:56:41.675	2009-09-03 18:56:41.675	1
1091	language.names.pt_PT	Portuguese (Portugal)	2009-09-03 18:56:41.681	2009-09-03 18:56:41.681	1
1092	language.names.el_CY	Greek (Cyprus)	2009-09-03 18:56:41.687	2009-09-03 18:56:41.687	1
1093	language.names.ar_QA	Arabic (Qatar)	2009-09-03 18:56:41.705	2009-09-03 18:56:41.705	1
1094	language.names.mk_MK	Macedonian (Macedonia)	2009-09-03 18:56:41.71	2009-09-03 18:56:41.71	1
1095	language.names.sv	Swedish	2009-09-03 18:56:41.722	2009-09-03 18:56:41.722	1
1096	language.names.de_CH	German (Switzerland)	2009-09-03 18:56:41.727	2009-09-03 18:56:41.727	1
1097	language.names.en_US	English (United States)	2009-09-03 18:56:41.736	2009-09-03 18:56:41.736	1
1098	language.names.fi_FI	Finnish (Finland)	2009-09-03 18:56:41.743	2009-09-03 18:56:41.743	1
1099	language.names.is	Icelandic	2009-09-03 18:56:41.753	2009-09-03 18:56:41.753	1
1100	language.names.cs	Czech	2009-09-03 18:56:41.758	2009-09-03 18:56:41.758	1
1101	language.names.en_MT	English (Malta)	2009-09-03 18:56:41.765	2009-09-03 18:56:41.765	1
1102	language.names.sl_SI	Slovenian (Slovenia)	2009-09-03 18:56:41.77	2009-09-03 18:56:41.77	1
1724	get-phet.offline.whereCanISave	CD, USB or hard drive.	2009-09-03 23:36:30.413	2009-09-03 23:36:30.413	1
1725	get-phet.install.howToGet	<a {0}>Click here</a> to go to the download page.	2009-09-03 23:50:22.947	2009-09-03 23:50:22.947	1
1726	get-phet.offline.howToGet	<a {0}>Click here</a> to go to the all-simulations download page.	2009-09-03 23:50:45.494	2009-09-03 23:50:45.494	1
1103	language.names.sk_SK	Slovak (Slovakia)	2009-09-03 18:56:41.778	2009-09-03 18:56:41.778	1
1745	get-phet.online.howOften	حالما جديدة أو محدثة المحاكاة يتم نشرها على موقع على شبكة الإنترنت.	2009-09-04 00:05:37.283	2009-09-04 00:05:37.283	4
1746	get-phet.online.howToGet	انقر على أيقونة المحاكاة في صفحة الويب من المحاكاة التي تريد تشغيلها.	2009-09-04 00:05:38.599	2009-09-04 00:05:38.599	4
1747	get-phet.online.needInternet	نعم	2009-09-04 00:05:39.628	2009-09-04 00:05:39.628	4
1727	get-phet.clickHere	&lt;&lt; انقر هنا	2009-09-04 00:02:20.781	2009-09-04 00:02:54.267	4
1728	get-phet.header	ثلاثة لدينا وسائل لتشغيل مجاني المحاكاة	2009-09-04 00:03:00.514	2009-09-04 00:03:00.514	4
1748	get-phet.online.whatSims	ويمكنك استخدام المحاكاة بشكل مؤقت مثبت.	2009-09-04 00:05:41.329	2009-09-04 00:05:41.329	4
1729	get-phet.install.header	تحميل <strong>كامل</strong> موقع على جهاز الكمبيوتر الخاص بك ، [أوسب] ، أو مؤتمر نزع السلاح	2009-09-04 00:03:35.458	2009-09-04 00:03:53.585	4
1730	get-phet.install.howLarge	حوالي 40MB إذا كان لديك جافا ، أو 65MB إذا كنت لا.	2009-09-04 00:03:57.425	2009-09-04 00:03:57.425	4
1731	get-phet.install.howOften	ثلاث أو أربع مرات في السنة.	2009-09-04 00:03:58.902	2009-09-04 00:03:58.902	4
1749	get-phet.online.whereCanISave	يمكن للتطبيقات فلاش لا يتم حفظها. تطبيقات جافا يتم تلقائيا المخزنة في جهاز الكمبيوتر الخاص بك WebStart مخبأ ، ولكن لا يمكن نقلها.	2009-09-04 00:05:44.057	2009-09-04 00:05:44.057	4
1750	get-phet.row.howLarge	ما هو حجم التحميل؟	2009-09-04 00:05:45.477	2009-09-04 00:05:45.477	4
1732	get-phet.install.howToGet	<a {0}> انقر هنا </a> للذهاب إلى صفحة التحميل.	2009-09-04 00:04:07.285	2009-09-04 00:04:21.849	4
1733	get-phet.install.needInternet	لا	2009-09-04 00:04:24.697	2009-09-04 00:04:26.03	4
1734	get-phet.install.whatSims	جميع عمليات المحاكاة.	2009-09-04 00:04:27.531	2009-09-04 00:04:27.531	4
1735	get-phet.install.whereCanISave	مؤتمر نزع السلاح ، [أوسب أو القرص الصلب.	2009-09-04 00:04:29.457	2009-09-04 00:04:29.457	4
1751	get-phet.row.howOften	كيف كثيرا ما هي التحديثات المتاحة؟	2009-09-04 00:05:46.998	2009-09-04 00:05:46.998	4
1736	get-phet.offline.header	تنزيل <strong>واحد أو أكثر</strong> المحاكاة بالكمبيوتر ، [أوسب] ، أو مؤتمر نزع السلاح	2009-09-04 00:04:37.437	2009-09-04 00:04:54.518	4
1737	get-phet.offline.howLarge	تحت 1.5 ميغابايت لكل المحاكاة.	2009-09-04 00:04:59.867	2009-09-04 00:04:59.867	4
1738	get-phet.offline.howOften	إذا كنت متصلا بشبكة الإنترنت ، جديدة أو محدثة المحاكاة المتوفرة.	2009-09-04 00:05:01.987	2009-09-04 00:05:01.987	4
1752	get-phet.row.howToGet	كيف يمكنني الحصول على المحاكاة؟	2009-09-04 00:05:48.464	2009-09-04 00:05:48.464	4
1739	get-phet.offline.howToGet	<a {0}>انقر هنا</a> للذهاب إلى جميع محاكاة صفحة التحميل.	2009-09-04 00:05:09.349	2009-09-04 00:05:23.868	4
1740	get-phet.offline.needInternet	لا	2009-09-04 00:05:28.85	2009-09-04 00:05:28.85	4
1741	get-phet.offline.whatSims	فقط تلك التي تريد.	2009-09-04 00:05:30.011	2009-09-04 00:05:30.011	4
1742	get-phet.offline.whereCanISave	مؤتمر نزع السلاح ، [أوسب أو القرص الصلب.	2009-09-04 00:05:30.86	2009-09-04 00:05:30.86	4
1743	get-phet.online.header	استخدام الموقع PhET بينما تكون متصلا على شبكة الإنترنت	2009-09-04 00:05:32.92	2009-09-04 00:05:32.92	4
1744	get-phet.online.howLarge	تحت 1.5 ميغابايت لكل المحاكاة.	2009-09-04 00:05:34.621	2009-09-04 00:05:34.621	4
1753	get-phet.row.needInternet	هو اتصال بإنترنت المطلوبة لتشغيل المحاكاة؟	2009-09-04 00:05:50.507	2009-09-04 00:05:50.507	4
1754	get-phet.row.whatSims	ما المحاكاة المثبتة؟	2009-09-04 00:05:51.582	2009-09-04 00:05:51.582	4
1755	get-phet.row.whereCanISave	أين أستطيع أن ينقذهم من على الكمبيوتر؟	2009-09-04 00:05:53.181	2009-09-04 00:05:53.181	4
1104	language.names.it	Italian	2009-09-03 18:56:41.783	2009-09-03 18:56:41.783	1
1105	language.names.tr_TR	Turkish (Turkey)	2009-09-03 18:56:41.789	2009-09-03 18:56:41.789	1
1106	language.names.zh	Chinese	2009-09-03 18:56:41.823	2009-09-03 18:56:41.823	1
1107	language.names.th	Thai	2009-09-03 18:56:41.831	2009-09-03 18:56:41.831	1
1108	language.names.ar_SA	Arabic (Saudi Arabia)	2009-09-03 18:56:41.836	2009-09-03 18:56:41.836	1
952	troubleshooting.main.q4.title	كنت تستخدم انترنت اكسبلورر والمحاكاة لا تعمل على جهاز الكمبيوتر الخاص بي.	2009-09-03 11:08:45.411	2009-09-03 11:08:45.411	4
953	troubleshooting.main.q5.title	لماذا لا يتم تشغيل فلاش محاكاة على الكمبيوتر؟	2009-09-03 11:08:48.148	2009-09-03 11:08:48.148	4
954	troubleshooting.main.q6.title	ما هو المثل الأعلى دقة الشاشة لتشغيل محاكيات PhET؟	2009-09-03 11:08:50.511	2009-09-03 11:08:50.511	4
955	troubleshooting.main.q7.title	لقد ويندوز 2000 ويمكن تشغيل محاكيات فلاش وجافا ولكن المحاكاة لا تعمل.	2009-09-03 11:08:53.415	2009-09-03 11:08:53.415	4
956	troubleshooting.main.q8.title	لماذا لا يعمل بشكل أبطأ PhET محاكاة على الكمبيوتر المحمول من على سطح المكتب؟	2009-09-03 11:08:56.148	2009-09-03 11:08:56.148	4
957	troubleshooting.main.q9.title	لماذا لا تعطل جهاز الكمبيوتر الخاص بي عندما أقوم بتشغيل واحدة من عمليات المحاكاة التي الصوت؟	2009-09-03 11:08:58.711	2009-09-03 11:08:58.711	4
958	troubleshooting.main.q10.title	وأود أن تترجم PhET المحاكاة الى لغات اخرى. وهذا يمكن القيام به بسهولة؟	2009-09-03 11:09:02.549	2009-09-03 11:09:02.549	4
959	troubleshooting.main.q11.title	لقد تم تحميل وتثبيت موقع PhET دون المثبت ، وأحصل على إنذار على كل صفحة. لماذا؟	2009-09-03 11:09:04.763	2009-09-03 11:09:04.763	4
1034	sponsors.royalinteractive.desc	For original site concept	2009-09-03 12:52:11.04	2009-09-03 12:52:11.04	1
1035	sponsors.dynamicalSystems.desc	For providing one of their tutorials	2009-09-03 12:52:50.087	2009-09-03 12:52:50.087	1
702	about.source-code.location	The source code for all PhET simulations is hosted at <a {0}>Unfuddle</a> (login with username: guest and password: guest). The latest version can always be found there. To checkout the source code, you will need a Subversion client such as <a {1}>TortoiseSVN</a> (Windows-only) or <a {2}>SmartSVN</a> (all platforms), or a command-line client. To checkout the source code for all of PhET's Java simulations using a command-line client, use the following command:	2009-08-31 15:28:43.142	2009-08-31 18:27:04.021	1
703	about.source-code.flash-simulations	To checkout the source code for all of PhET's Flash simulations, replace "simulations-java" with "simulations-flash" in the above command.	2009-08-31 15:30:00.02	2009-08-31 18:27:36.059	1
750	get-phet.one-at-a-time.downloadingSims	You may copy the simulation to any place on your computer. You may also copy the simulation to a CD-ROM Drive, USB Drive, or shared network location.	2009-09-01 00:20:51.729	2009-09-01 00:20:51.729	1
751	get-phet.one-at-a-time.runningSims	Click on the name of any simulation to download the simulation to your computer. To run the simulation, double-click it. If you have difficulties, please see our <a {0}>technical support</a> page.	2009-09-01 00:22:55.028	2009-09-01 00:22:55.028	1
1756	research.intro	We direct our research at assessing the effectiveness of our interactive simulations in a variety of educational environments, particularly physics courses, and as stand-alone, informal educational tools.	2009-09-04 00:14:48.234	2009-09-04 00:14:48.234	1
1757	research.additional	In addition, we are focusing our efforts to better understand how students learn from the simulations, and what implications this may have for designing effective in-class activities, homework and labs. Please see our <a {0}>PhET Look and Feel</a> guide.	2009-09-04 00:16:41.132	2009-09-04 00:16:41.132	1
1758	research.immediateInterests	Our immediate interests are:	2009-09-04 00:17:16.515	2009-09-04 00:17:16.515	1
1759	research.simsAffectStudents	How the simulations affect the students'...	2009-09-04 00:17:52.5	2009-09-04 00:17:52.5	1
1760	research.simsAffectStudents.point1	ability to solve conceptual and quantitative problems;	2009-09-04 00:19:27.084	2009-09-04 00:19:27.084	1
1761	research.simsAffectStudents.point2	attitudes about learning physics; and	2009-09-04 00:19:37.756	2009-09-04 00:19:37.756	1
1762	research.simsAffectStudents.point3	perceptions of their own learning and of the simulations themselves.	2009-09-04 00:19:44.449	2009-09-04 00:19:44.449	1
1763	research.simsEnvironments	Simulation effectiveness in different environments	2009-09-04 00:20:17.868	2009-09-04 00:20:17.868	1
1764	research.simsEnvironments.point1	How might these simulations be used in labs? Can they replace traditional hands-on experiments?	2009-09-04 00:21:19.898	2009-09-04 00:21:19.898	1
1765	research.simsEnvironments.point2	When students use simulations as preparation activities for class, how do they compare to traditional class preparation activities such as reading the text or doing homework problems?	2009-09-04 00:21:35.979	2009-09-04 00:21:35.979	1
1766	research.simsEnvironments.point3	Are the simulations more effective standalone (as an open play area) or wrapped with a guiding tutorial?	2009-09-04 00:21:43.25	2009-09-04 00:21:43.25	1
1767	research.publicationsAndPresentations	Publications and Presentations	2009-09-04 00:22:18.503	2009-09-04 00:22:18.503	1
1768	research.aboutPhet	About PhET	2009-09-04 00:22:45.652	2009-09-04 00:22:45.652	1
1769	research.effectivenessStudies	Studies of PhET Effectiveness	2009-09-04 00:23:44.481	2009-09-04 00:23:44.481	1
960	troubleshooting.main.q12.title	عند تشغيل محاكيات من دون PhET موقع المثبت ، وأنا رؤية الحوار الذي يقول &quot;إن تطبيق التوقيع الرقمي قد تحققت. هل تريد تشغيل التطبيق؟&quot; (أو ما شابه). ماذا يعني هذا؟	2009-09-03 11:09:08.88	2009-09-03 11:09:20.607	4
701	about.source-code.header	Where can I get the source code for the PhET simulations?	2009-08-31 15:25:46.913	2009-08-31 15:25:46.913	1
1770	research.studentBeliefs	Student Beliefs and Learning	2009-09-04 00:24:31.034	2009-09-04 00:24:31.034	1
961	troubleshooting.main.q13.title	(مستخدمي ماك) عندما أقوم بالنقر فوق &quot;تشغيل الآن&quot; لبدء محاكاة كل ما نحصل عليه هو أن يفتح ملف نصي؟	2009-09-03 11:09:25.98	2009-09-03 11:09:34.023	4
962	troubleshooting.main.licensingRequirements	ما هي متطلبات الترخيص؟	2009-09-03 11:09:37.483	2009-09-03 11:09:37.483	4
963	troubleshooting.java.intro	PhET جاوة القائم على المحاكاة استخدام صن مايكروسيستمز 'جافا ويب ستارت لإطلاق تكنولوجيا المحاكاة. هذه الصفحة سوف تساعدك على ضمان أن يكون لديك جافا مثبتة بشكل صحيح ، ومعالجة بعض المشاكل التي تشغل الناس قد يكون لدينا برامج. إذا كنت لا تستطيع حل مشكلتك هنا ، يرجى إعلامنا عن طريق البريد الالكتروني على العنوان التال <a {0}><span class="red">phethelp@colorado.edu</span></a>.	2009-09-03 11:10:05.298	2009-09-03 11:10:05.298	4
964	troubleshooting.java.faqs	أسئلة وأجوبة	2009-09-03 11:10:22.524	2009-09-03 11:10:22.524	4
965	troubleshooting.java.q1.title	(مستخدمي أجهزة ماكنتوش) عندما أقوم بالنقر فوق &quot;تشغيل الآن&quot; لبدء محاكاة كل ما نحصل عليه هو أن يفتح ملف نصي؟	2009-09-03 11:10:25.496	2009-09-03 11:10:34.599	4
966	troubleshooting.java.q2.title	كيف يمكنني الحصول على جافا؟	2009-09-03 11:10:37.663	2009-09-03 11:10:37.663	4
1109	language.names.no	Norwegian	2009-09-03 18:56:41.841	2009-09-03 18:56:41.841	1
474	nav.all	@nav.all@	2009-08-31 14:15:38.723	2009-08-31 14:15:38.723	3
967	troubleshooting.java.q3.title	لماذا استخدام جافا ويب ستارت بدلا من جافا؟	2009-09-03 11:10:40.472	2009-09-03 11:10:40.472	4
968	troubleshooting.java.q4.title	هل هناك أي مشاكل في تشغيل المحاكاة على ماك؟	2009-09-03 11:10:42.748	2009-09-03 11:10:42.748	4
752	troubleshooting.java.intro	PhET's Java-based simulations use Sun Microsystems' Java Web Start technology to launch the simulations. This page will help you ensure that you have Java installed properly, and address some of the problems people might have running our programs. If you can't solve your problem here, please notify us by email at the following address: <a {0}><span class="red">phethelp@colorado.edu</span></a>.	2009-09-01 00:34:04.883	2009-09-01 00:34:04.883	1
753	troubleshooting.java.q1.title	(Mac users) When I click "run now" to start the simulation all I get is a text file that opens?	2009-09-01 00:37:43.557	2009-09-01 00:37:43.557	1
754	troubleshooting.java.q2.title	How do I get Java?	2009-09-01 00:38:23.297	2009-09-01 00:38:23.297	1
755	troubleshooting.java.q3.title	Why do you use Java Web Start instead of Java?	2009-09-01 00:39:11.115	2009-09-01 00:39:11.115	1
756	troubleshooting.java.q4.title	Are there any problems running the simulations on a Mac?	2009-09-01 00:39:19.461	2009-09-01 00:39:19.461	1
757	troubleshooting.java.q5.title	How do I check my computer's current version of Java?	2009-09-01 00:39:45.162	2009-09-01 00:39:45.162	1
758	troubleshooting.java.q6.title	I have Windows 2000 and I can only get the Flash based simulations to work.	2009-09-01 00:39:52.882	2009-09-01 00:39:52.882	1
759	troubleshooting.java.q7.title	General Java troubleshooting	2009-09-01 00:40:01.158	2009-09-01 00:40:01.158	1
760	troubleshooting.java.q8.title	Troubleshooting tips for networked computers	2009-09-01 00:40:08.334	2009-09-01 00:40:08.334	1
761	troubleshooting.java.q1.answer	<p>{0} This problem will affect mac users who recently installed Apple's "Java for Mac OS X 10.5 Update 4". The update will typically be done via Software Update, or automatically. After installing this update, the problem appears: clicking on JNLP files in Safari or FireFox caused the JNLP file to open in TextEdit, instead of starting Java Web Start.</p><p>The fix is:<br/>1. Go to http://support.apple.com/downloads/Java_for_Mac_OS_X_10_5_Update_4<br/>2. Click Download to download a .dmg file<br/>3. When the .dmg has downloaded, double-click on it (if it doesn't mount automatically)<br/>4. Quit all applications<br/>5. Run the update installer</p>	2009-09-01 00:44:20.655	2009-09-01 00:46:41.035	1
762	troubleshooting.java.q2.answer	<p>To run the Java-based simulations you must have Java version 1.5 or higher installed on your computer. You can obtain the free downloads by clicking on the button below:<br/>{0}</p><p><strong>Note for Netscape Users</strong></p><p>After you have installed Java Web Start, you will need to close and re-open your browser for Java Web Start to work.</p>	2009-09-01 00:52:48.664	2009-09-01 00:52:48.664	1
763	troubleshooting.java.q3.answer	<p>We use Java Web Start technology rather than Java Applets. Java Web Start, which is a free mechanism from Sun, is a more robust way of launching Java programs over the Web than applets. If you have had problems running applets in the past, you may well find that Java Web Start works much better. If you should have any problems, we will be happy to help you solve them. If the information on this page doesnít help, just send us email at {0}.</p>	2009-09-01 00:53:21.054	2009-09-01 00:53:21.054	1
764	troubleshooting.java.q4.answer	<p>Macintosh OS X 10.4 or higher is required to run our Java programs. You must have the latest version of Java (which will already be installed if you are using the auto-update feature of OS X). See {0} for details.</p><p>Versions of Apple's Safari browser below 2.0 (included with OS X 10.4) do not launch our Java-based applications reliably.</p><p>If when you click "Run Now" to start a simulation and all you get is a text file that opens, see <a {1}>this answer</a>.</p>	2009-09-01 00:55:21.501	2009-09-01 00:55:21.501	1
765	troubleshooting.java.q5.answer	<p><strong><em>Windows Vista users:</em></strong><br/>From a command line (Start&nbsp;menu-&gt;All&nbsp;Programs-&gt;Accessories-&gt;Command&nbsp;Prompt), type: <strong>javaws&nbsp;-viewer</strong> followed by the enter key. You should see some text which will include something like "(build 1.4.2_05_05-b04)"; this number is the version of Java you are using. If you receive an error, Java is not properly installed (see above to reinstall).</p><p><strong><em>Windows 98SE/2000/XP users:</em></strong><br/>From a command line (Start&nbsp;menu-&gt;All&nbsp;Programs-&gt;Accessories-&gt;Command&nbsp;Prompt), type: <strong>java&nbsp;-version</strong> followed by the enter key. You should see some text which will include something like "(build 1.4.2_05_05-b04)"; this number is the version of Java you are using. If you receive an error, Java is not properly installed (see above to reinstall).</p><p><strong><em>Macintosh users:</em></strong><br/>From the Apple menu, click on "About this Mac", then on "More Info...". You will find the Java software version under "Software-&gt;Applications".</p>	2009-09-01 00:57:19.556	2009-09-01 00:57:19.556	1
766	troubleshooting.java.q6.answer	<p>Some Windows 2000 systems have been reported to lack part of the necessary Java configuration. These systems will typically start our Flash-based simulations reliably, but will appear to do nothing when launching our Java-based simulations.</p><p><strong>To resolve this situation, please perform the following steps:</strong></p><ol><li>From the desktop or start menu, open "My Computer"</li><li>Click on the "Folder Options" item in the "Tools" menu</li><li>Click on the "File Types" tab at the top of the window that appears</li><li>Locate "JNLP" in the "extensions" column, and click once on it to select the item</li><li>Click on the "change" button</li><li>When asked to choose which program to use to open JNLP files, select "Browse"</li><li>Locate the program "javaws" or "javaws.exe" in your Java installation folder (typically "C:\\Program Files\\Java\\j2re1.xxxx\\javaws", where "xxxx" is a series of numbers indicating the software version; choose the latest version)</li><li>Select the program file and then click "Open" to use the "javaws" program to open JNLP files.</li></ol>	2009-09-01 00:58:52.394	2009-09-01 00:58:52.394	1
767	troubleshooting.java.q7.answer	<p>The following are some general steps that you may wish to perform when attempting to solve Java-related problems:</p><ul><li><strong>Empty Your Browser's Cache</strong><br/><table><thead><tr><td>Internet Explorer</td><td>Firefox</td></tr></thead><tbody><tr><td>Select "Tools-&gt;Options", then click on the "Delete Files..." button under "Temporary Internet Files"</td><td>Select "Tools-&gt;Options-&gt;Privacy", then click the "Clear" button next to "Cache".</td></tr></tbody></table></li><li><strong>Empty Java Web Start's Cache</strong><br/>From Windows' Start menu, select "Java Web Start", then select "Preferences" from the File menu. Click "Advanced", then click the "Clear Folder" button.</li><li><strong>Make Sure Windows can find Java and Java Web Start</strong><br/>From a command line (Start menu-&gt;Accessories-&gt;Command Prompt), type <em>java -version</em>followed by the enter key. If you receive an error, Java is not properly installed. Please try reinstalling Java (see above) after uninstalling any Java entries in your list of installed programs (Control Panel -&gt;Add/Remove Programs).</li></ul>	2009-09-01 01:00:17.961	2009-09-01 01:00:17.961	1
969	troubleshooting.java.q5.title	كيف يمكنني التحقق من جهاز الكمبيوتر الخاص بي في الإصدار الحالي من جافا؟	2009-09-03 11:10:45.78	2009-09-03 11:10:45.78	4
970	troubleshooting.java.q6.title	لقد ويندوز 2000 وأنا لا يمكن إلا أن الحصول على محاكاة تستند فلاش للعمل.	2009-09-03 11:10:49.918	2009-09-03 11:10:49.918	4
1771	research.intro	علينا أن نوجه البحوث في تقييم فعالية محاكاة تفاعلية لدينا في مجموعة متنوعة من البيئات التعليمية ، وخاصة مقررات الفيزياء ، وقائمة بذاتها ، أدوات تعليمية غير رسمية.	2009-09-04 00:33:13.536	2009-09-04 00:33:13.536	4
1775	research.simsAffectStudents.point1	القدرة على حل المشاكل المفاهيمية والكمية ؛	2009-09-04 00:33:47.877	2009-09-04 00:33:47.877	4
1772	research.additional	بالإضافة إلى ذلك ، نحن نركز جهودنا من أجل فهم أفضل لكيفية تعلم الطلاب من المحاكاة ، وهذا ما قد يكون لها آثار لتصميم فعال في أنشطة الفئة ، والمختبرات المدرسية. يرجى الاطلاع على <a {0}>PhET الشكل والمظهر</a> دليل.	2009-09-04 00:33:22.662	2009-09-04 00:33:40.069	4
1773	research.immediateInterests	مصالحنا المباشرة هي :	2009-09-04 00:33:42.981	2009-09-04 00:33:42.981	4
1774	research.simsAffectStudents	كيف تؤثر على محاكاة الطلاب...	2009-09-04 00:33:44.818	2009-09-04 00:33:44.818	4
1776	research.simsAffectStudents.point2	المواقف حول تعلم الفيزياء ، و	2009-09-04 00:33:49.441	2009-09-04 00:33:49.441	4
1777	research.simsAffectStudents.point3	تصورات تعلمهم وعمليات المحاكاة نفسها.	2009-09-04 00:33:51.559	2009-09-04 00:33:51.559	4
1778	research.simsEnvironments	محاكاة فعالية في مختلف البيئات.	2009-09-04 00:33:52.975	2009-09-04 00:33:52.975	4
1779	research.simsEnvironments.point1	كيف يمكن أن تستخدم هذه المحاكاة في المختبرات؟ فإنها يمكن أن تحل محل الأيدي التقليدية على تجارب؟	2009-09-04 00:33:56.345	2009-09-04 00:33:56.345	4
1780	research.simsEnvironments.point2	عندما يستخدم الطلاب المحاكاة بوصفها أنشطة التحضير للفئة ، كيف يمكن مقارنتها مع الأنشطة التقليدية في إعداد الطبقة مثل قراءة النص أو القيام المشاكل المنزلية؟	2009-09-04 00:33:57.897	2009-09-04 00:33:57.897	4
1781	research.simsEnvironments.point3	المحاكاة هي أكثر فعالية قائمة بذاتها (كما هو مفتوح منطقة اللعب) أو مغلفة تعليمي التوجيهية؟	2009-09-04 00:34:00.213	2009-09-04 00:34:00.213	4
1110	language.names.en_GB	English (United Kingdom)	2009-09-03 18:56:41.847	2009-09-03 18:56:41.847	1
768	troubleshooting.java.q8.answer	<p><strong>Why do simulations run for admin but not all users?</strong></p><p>Java simulations may also fail to start on networked computers running Windows for some users while running properly for administrators. When Java Web Start tries to launch a simulation, it attempts to cache certain files in a "cache folder". This folder (or "directory") is determined when Java is installed, and may point to a folder that non-administrator accounts do not have access to, resulting in a launch failure. Correct this problem by setting the cache directory to one of your own choosing: Log on as an administrator.</p><p>Launch the Java Web Start Application Manager. This can be done in one of two ways. a) If the Java installation placed an icon either on your desktop or in the Start Menu -&gt; All Programs list titled "Java Web Start", click it to start the application. b) Use the Windows search function (located in the Start Menu) to search for the program "javaws.exe". Once it is found, click on the file to run the application. Once the Java Web Start Application Manager is up and running, choose File&gt;Preferences. In the Preferences dialog that appears, click on the Advanced tab. In this tab, you will find a text field labeled "Applications Folder". In this text field, put the name of an empty directory to which all users have write privileges. Note: This directory MUST be empty. All users should now be able to launch the Java simulations.</p><p><strong>Issue 3: Web Proxy Settings</strong><br/>Java simulations may fail to start if the proxy settings in Java Web Start's configuration panel (Start Menu-&gt;Java Web Start, File Menu-&gt;Preferences) do not match those your system is currently using. To identify settings being used on your system, see your web browser's proxy settings, or contact you network administrator.</p><p><strong>Local Install on Boot Drive Only</strong><br/>If you are operating in a networked environment, please ensure that both Java and the PhET simulations (if installed locally rather than running from our web site) are installed on local, non-networked drives. The PhET simulations must be installed on the boot drive.</p><p><strong>Network Firewalls</strong><br/>Please ensure that your firewall is configured to allow both Java and Java Web Start to communicate through the firewall.</p>	2009-09-01 01:01:45.087	2009-09-01 01:01:45.087	1
1111	language.names.sr_CS	Serbian (Serbia and Montenegro)	2009-09-03 18:56:41.852	2009-09-03 18:56:41.852	1
971	troubleshooting.java.q7.title	عموما جافا مخرج	2009-09-03 11:10:53.282	2009-09-03 11:10:53.282	4
972	troubleshooting.java.q8.title	نصائح للمخرج شبكات الكمبيوتر	2009-09-03 11:10:55.773	2009-09-03 11:10:55.773	4
973	troubleshooting.flash.toRun	فلاش لتشغيل نماذج المحاكاة التي تعتمد عليك ماكروميديا فلاش 8 (متاحة مجانا) أو أحدث مثبتا على جهاز الكمبيوتر.	2009-09-03 11:11:08.411	2009-09-03 11:11:08.411	4
974	troubleshooting.flash.blankWindow	إذا كنت تحصل على نافذة فارغة عند محاولة اطلاق محاكاة فلاش ، وربما كنت بحاجة لنسخة جديدة من مشغل فلاش.	2009-09-03 11:11:10.5	2009-09-03 11:11:10.5	4
975	troubleshooting.flash.olderVersions	الإصدارات القديمة من فلاش لاعب يمكن أن يسبب مشاكل. تحديث مشغل فلاش بك يوصى إذا كنت تتلقى خطأ مشابهة إلى ما يلي :	2009-09-03 11:11:12.076	2009-09-03 11:11:12.076	4
976	troubleshooting.flash.intro	هذه الصفحة سوف تساعدك على حل بعض مشاكل الناس عادة يكون لدينا برامج تشغيل. إذا كنت لا تستطيع حل مشكلتك هنا ، يرجى إعلامنا عن طريق البريد الالكتروني على العنوان التالي <a href="mailto:phethelp@colorado.edu"><span class="red">phethelp@colorado.edu</span></a>.	2009-09-03 11:11:32.674	2009-09-03 11:11:32.674	4
977	troubleshooting.javascript.intro	جافا سكريبت هي لغة البرمجة التي تستخدم في الغالب في صفحات الويب ، وعادة ما يكون لإضافة الميزات التي تجعل من صفحة الويب أكثر تفاعلا. مطلوب جافا سكريبت لتقديم معلومات إلى PhET.	2009-09-03 11:11:39.565	2009-09-03 11:11:39.565	4
978	troubleshooting.javascript.notJava	جافا سكريبت هي مختلفة تماما عن شركة صن مايكروسيستمز جافا ويب ستارت التي نحن على استخدام المحاكاة. للحصول على المساعدة مع شركة صن مايكروسيستمز Java, <a {0}>اذهب هنا</a>.	2009-09-03 11:12:03.164	2009-09-03 11:12:15.777	4
979	troubleshooting.javascript.notify	إذا كنت لا تستطيع حل مشكلتك هنا ، يرجى إعلامنا عن طريق البريد الالكتروني على العنوان التالي {0}.	2009-09-03 11:12:35.051	2009-09-03 11:12:35.051	4
980	troubleshooting.javascript.faqs	أسئلة وأجوبة	2009-09-03 11:12:38.523	2009-09-03 11:12:38.523	4
981	troubleshooting.javascript.q1.title	هو سكريبت في المتصفح الخاص بي؟	2009-09-03 11:12:40.744	2009-09-03 11:12:40.744	4
982	troubleshooting.javascript.q2.title	لماذا جافا سكريبت هو المطلوب؟	2009-09-03 11:12:46.167	2009-09-03 11:12:46.167	4
983	troubleshooting.javascript.q3.title	كيف يمكنني تمكين جافا سكريبت على فايرفوكس؟	2009-09-03 11:12:47.548	2009-09-03 11:12:47.548	4
984	troubleshooting.javascript.q4.title	كيف يمكنني تمكين جافا سكريبت في إنترنت إكسبلورر؟	2009-09-03 11:12:50.222	2009-09-03 11:12:50.222	4
985	troubleshooting.javascript.q5.title	كيف يمكنني تمكين جافا سكريبت على سفري؟	2009-09-03 11:12:54.29	2009-09-03 11:12:54.29	4
986	about.p2	من أجل مساعدة الطلاب على فهم هذه المفاهيم بصريا ، PhET محاكاة تحريك ما هو غير مرئية للعين من خلال استخدام الرسومات وضوابط بديهية مثل النقر والسحب والتلاعب ، والمتزلجون وأزرار الراديو. من أجل مزيد من تشجيع التنقيب عن الكمية ، وعمليات المحاكاة كما تقدم أدوات القياس بما في ذلك الحكام وتوقف watchs ، الفولتميترات والحرارة. كمستخدم تعالج هذه الأدوات التفاعلية ، والاستجابات على الفور الرسوم المتحركة بشكل فعال مما يوضح السبب والآثار ، فضلا عن العلاقات تمثيلات مرتبطة متعددة (الحركة من الأشياء ، والرسوم البيانية ، وقراءات عدة ، الخ..).	2009-09-03 11:13:16.004	2009-09-03 11:13:16.004	4
989	about.p4	جميع المحاكاة PhET هي متاحة بحرية من <a {0}> PhET الموقع </a> وتعتبر سهلة الاستخدام إدراج في الصفوف الدراسية. أنها مكتوبة باللغة <a {1}> جافا </a> و <a {2}> فلاش </a> ، ويمكن تشغيلها باستخدام متصفح الإنترنت القياسية طالما <a {2}> فلاش </a> و <a {1}> جافا </a> يتم تثبيتها.	2009-09-03 11:15:21.528	2009-09-03 11:16:37.496	4
990	about.source-code.header	أين يمكنني الحصول على شفرة المصدر لمحاكاة PhET؟	2009-09-03 11:16:42.517	2009-09-03 11:16:42.517	4
987	about.p1	PhET المحاكاة التفاعلية هو جهد مستمر لتوفير مجموعة واسعة من عمليات المحاكاة لتحسين الطريقة التي الفيزياء والكيمياء والبيولوجيا وعلوم الأرض والرياضيات وتعليمه وتعلمها. أل <a {0}> المحاكاة </a> هي الأدوات التفاعلية التي تمكن الطلبة من إجراء اتصالات بين ظواهر الحياة الحقيقية والكامنة وراء العلم الذي يفسر هذه الظواهر. لدينا فريق من العلماء والمهندسين والبرمجيات ومدرسي العلوم استخدام <a {1}> البحوث النهج القائم </a> -- إدماج النتائج المستخلصة من البحوث السابقة وتجاربنا الخاصة -- لخلق المحاكاة التي تدعم التعامل مع الطلاب وفهم المفاهيم العلمية.	2009-09-03 11:13:44.758	2009-09-03 11:14:12.153	4
988	about.p3	لضمان فعالية التعليم والاستخدام ، وجميع من محاكاة على نطاق واسع اختبارها وتقييمها. هذه التجارب تشمل مقابلات الطالب بالإضافة إلى الاستخدام الفعلي للمحاكاة في مجموعة متنوعة من البيئات ، بما في ذلك المحاضرات وفريق العمل ، والواجبات المنزلية والعمل في المعمل. ملكنا <a {0}> نظام التصنيف </a> ما يشير إلى مستوى الاختبار قد اكتملت على كل المحاكاة.	2009-09-03 11:14:24.896	2009-09-03 11:14:38.968	4
994	simulationPage.title	{0} - {1}, {2}, {3} - PhET	2009-09-03 11:18:21.481	2009-09-03 11:18:21.481	4
991	about.source-code.flash-simulations	اطلب الآن شفرة المصدر لجميع PhET لمحاكاة فلاش ، يستعاض عن عبارة &quot;المحاكاة - جافا&quot; مع &quot;المحاكاة فلاش&quot; في الأمر أعلاه.	2009-09-03 11:16:45.794	2009-09-03 11:17:02.534	4
995	simulationDisplay.title	{0} - PhET Simulations	2009-09-03 11:18:34.969	2009-09-03 11:18:34.969	4
992	about.source-code.location	شفرة المصدر لجميع عمليات المحاكاة PhET يتم استضافتها في <a {0}> Unfuddle </a> (اسم المستخدم : كلمة المرور والضيف : الضيف). ويمكن أن يكون دائما أحدث نسخة وجدت هناك. اطلب الآن شفرة المصدر ، سوف تحتاج إلى العميل مثل التخريب <a {1}> تورتويز إس </a> (ويندوز فقط) أو <a {2}> SmartSVN </a> (جميع المنابر) ، أو سطر الأوامر العميل. اطلب الآن شفرة المصدر لجميع من عمليات المحاكاة PhET جاوة باستخدام سطر الأوامر العميل ، استخدم الأمر التالي :	2009-09-03 11:17:25.695	2009-09-03 11:18:04.214	4
993	home.title	PhET : مجاني على الانترنت الفيزياء والكيمياء والبيولوجيا وعلوم الارض والرياضيات المحاكاة	2009-09-03 11:18:11.227	2009-09-03 11:18:11.227	4
996	about.title	حول PhET	2009-09-03 11:18:37.635	2009-09-03 11:18:37.635	4
997	about.source-code.title	PhET المصدر مدونة	2009-09-03 11:18:38.795	2009-09-03 11:18:38.795	4
998	about.contact.title	جهة اتصال	2009-09-03 11:18:39.8	2009-09-03 11:18:39.8	4
999	about.who-we-are.title	من نحن	2009-09-03 11:18:42.49	2009-09-03 11:18:42.49	4
1000	about.licensing.title	الترخيص	2009-09-03 11:18:43.237	2009-09-03 11:18:43.237	4
1001	sponsors.title	PhET الرعاة	2009-09-03 11:18:44.077	2009-09-03 11:18:44.077	4
1002	workshops.title	حلقات عمل عن طريق PhET -- كيفية تدريس والمحاكاة	2009-09-03 11:18:45.635	2009-09-03 11:18:45.635	4
1003	contribute.title	المساهمة في PhET	2009-09-03 11:18:48.3	2009-09-03 11:18:48.3	4
1004	get-phet.title	ثلاثة لدينا وسائل لتشغيل مجاني المحاكاة	2009-09-03 11:18:49.862	2009-09-03 11:18:49.862	4
1005	get-phet.one-at-a-time.title	إطلاق PhET المحاكاة واحد في وقت	2009-09-03 11:18:50.706	2009-09-03 11:18:50.706	4
1006	get-phet.full-install.title	PhET اتصال المثبت	2009-09-03 11:18:51.706	2009-09-03 11:18:51.706	4
1007	research.title	بحث	2009-09-03 11:18:54.276	2009-09-03 11:18:54.276	4
1008	troubleshooting.java.title	مخرج جافا	2009-09-03 11:18:55.235	2009-09-03 11:18:55.235	4
1009	troubleshooting.flash.title	مخرج فلاش	2009-09-03 11:18:56.606	2009-09-03 11:18:56.606	4
1010	troubleshooting.javascript.title	مخرج جافا سكريبت	2009-09-03 11:18:57.772	2009-09-03 11:18:57.772	4
1011	keyword.1d	1D	2009-09-03 11:19:06.702	2009-09-03 11:19:06.702	4
1012	keyword.absorption	الاستيعاب	2009-09-03 11:19:12.26	2009-09-03 11:19:12.26	4
1013	keyword.acCircuits	دوائرالتيارالمتردد	2009-09-03 11:19:16.044	2009-09-03 11:19:16.044	4
1014	keyword.acVoltage	جهد التيار المتناوب	2009-09-03 11:19:18.935	2009-09-03 11:19:18.935	4
1782	research.publicationsAndPresentations	المنشورات والعروض	2009-09-04 00:34:01.512	2009-09-04 00:34:01.512	4
1783	research.aboutPhet	حول PhET	2009-09-04 00:37:34.159	2009-09-04 00:37:34.159	4
1784	research.effectivenessStudies	دراسات PhET فعالية	2009-09-04 00:37:36.978	2009-09-04 00:37:36.978	4
1785	research.studentBeliefs	طالب المعتقدات والتعلم	2009-09-04 00:37:38.438	2009-09-04 00:37:38.438	4
1015	keyword.acceleration	تسارع	2009-09-03 11:19:22.276	2009-09-03 11:19:22.276	4
1016	keyword.acids	الأحماض	2009-09-03 11:19:26.27	2009-09-03 11:19:26.27	4
1112	language.names.lt	Lithuanian	2009-09-03 18:56:41.857	2009-09-03 18:56:41.857	1
1113	language.names.ro	Romanian	2009-09-03 18:56:41.862	2009-09-03 18:56:41.862	1
1114	language.names.en_NZ	English (New Zealand)	2009-09-03 18:56:41.867	2009-09-03 18:56:41.867	1
1115	language.names.no_NO_NY	Norwegian (Norway,Nynorsk)	2009-09-03 18:56:41.872	2009-09-03 18:56:41.872	1
1116	language.names.lt_LT	Lithuanian (Lithuania)	2009-09-03 18:56:41.877	2009-09-03 18:56:41.877	1
1117	language.names.es_NI	Spanish (Nicaragua)	2009-09-03 18:56:41.891	2009-09-03 18:56:41.891	1
1118	language.names.nl	Dutch	2009-09-03 18:56:41.9	2009-09-03 18:56:41.9	1
1119	language.names.ga_IE	Irish (Ireland)	2009-09-03 18:56:41.937	2009-09-03 18:56:41.937	1
1120	language.names.fr_BE	French (Belgium)	2009-09-03 18:56:41.943	2009-09-03 18:56:41.943	1
1121	language.names.es_ES	Spanish (Spain)	2009-09-03 18:56:41.95	2009-09-03 18:56:41.95	1
1122	language.names.ar_LB	Arabic (Lebanon)	2009-09-03 18:56:41.956	2009-09-03 18:56:41.956	1
1123	language.names.ko	Korean	2009-09-03 18:56:41.961	2009-09-03 18:56:41.961	1
1124	language.names.fr_CA	French (Canada)	2009-09-03 18:56:41.965	2009-09-03 18:56:41.965	1
1125	language.names.et_EE	Estonian (Estonia)	2009-09-03 18:56:41.974	2009-09-03 18:56:41.974	1
1126	language.names.ar_KW	Arabic (Kuwait)	2009-09-03 18:56:41.978	2009-09-03 18:56:41.978	1
1127	language.names.es_US	Spanish (United States)	2009-09-03 18:56:41.983	2009-09-03 18:56:41.983	1
1128	language.names.es_MX	Spanish (Mexico)	2009-09-03 18:56:41.993	2009-09-03 18:56:41.993	1
1129	language.names.ar_SD	Arabic (Sudan)	2009-09-03 18:56:42.002	2009-09-03 18:56:42.002	1
1130	language.names.in_ID	Indonesian (Indonesia)	2009-09-03 18:56:42.008	2009-09-03 18:56:42.008	1
1131	language.names.ru	Russian	2009-09-03 18:56:42.013	2009-09-03 18:56:42.013	1
1132	language.names.lv	Latvian	2009-09-03 18:56:42.019	2009-09-03 18:56:42.019	1
1133	language.names.es_UY	Spanish (Uruguay)	2009-09-03 18:56:42.024	2009-09-03 18:56:42.024	1
1134	language.names.lv_LV	Latvian (Latvia)	2009-09-03 18:56:42.03	2009-09-03 18:56:42.03	1
1135	language.names.iw	Hebrew	2009-09-03 18:56:42.035	2009-09-03 18:56:42.035	1
1137	language.names.ar_SY	Arabic (Syria)	2009-09-03 18:56:42.045	2009-09-03 18:56:42.045	1
1138	language.names.hr	Croatian	2009-09-03 18:56:42.051	2009-09-03 18:56:42.051	1
1139	language.names.et	Estonian	2009-09-03 18:56:42.056	2009-09-03 18:56:42.056	1
1140	language.names.es_DO	Spanish (Dominican Republic)	2009-09-03 18:56:42.061	2009-09-03 18:56:42.061	1
1141	language.names.fr_CH	French (Switzerland)	2009-09-03 18:56:42.072	2009-09-03 18:56:42.072	1
1142	language.names.hi_IN	Hindi (India)	2009-09-03 18:56:42.077	2009-09-03 18:56:42.077	1
1143	language.names.es_VE	Spanish (Venezuela)	2009-09-03 18:56:42.082	2009-09-03 18:56:42.082	1
1144	language.names.ar_BH	Arabic (Bahrain)	2009-09-03 18:56:42.087	2009-09-03 18:56:42.087	1
1145	language.names.en_PH	English (Philippines)	2009-09-03 18:56:42.092	2009-09-03 18:56:42.092	1
1146	language.names.ar_TN	Arabic (Tunisia)	2009-09-03 18:56:42.101	2009-09-03 18:56:42.101	1
1147	language.names.fi	Finnish	2009-09-03 18:56:42.106	2009-09-03 18:56:42.106	1
1148	language.names.de_AT	German (Austria)	2009-09-03 18:56:42.111	2009-09-03 18:56:42.111	1
1149	language.names.es	Spanish	2009-09-03 18:56:42.117	2009-09-03 18:56:42.117	1
1150	language.names.nl_NL	Dutch (Netherlands)	2009-09-03 18:56:42.121	2009-09-03 18:56:42.121	1
1151	language.names.es_EC	Spanish (Ecuador)	2009-09-03 18:56:42.126	2009-09-03 18:56:42.126	1
1152	language.names.zh_TW	Chinese (Taiwan)	2009-09-03 18:56:42.134	2009-09-03 18:56:42.134	1
1153	language.names.ar_JO	Arabic (Jordan)	2009-09-03 18:56:42.139	2009-09-03 18:56:42.139	1
1154	language.names.be	Belarusian	2009-09-03 18:56:42.144	2009-09-03 18:56:42.144	1
1155	language.names.is_IS	Icelandic (Iceland)	2009-09-03 18:56:42.149	2009-09-03 18:56:42.149	1
1156	language.names.es_CO	Spanish (Colombia)	2009-09-03 18:56:42.154	2009-09-03 18:56:42.154	1
1157	language.names.es_CR	Spanish (Costa Rica)	2009-09-03 18:56:42.158	2009-09-03 18:56:42.158	1
1158	language.names.es_CL	Spanish (Chile)	2009-09-03 18:56:42.162	2009-09-03 18:56:42.162	1
1159	language.names.ar_EG	Arabic (Egypt)	2009-09-03 18:56:42.167	2009-09-03 18:56:42.167	1
1160	language.names.en_ZA	English (South Africa)	2009-09-03 18:56:42.172	2009-09-03 18:56:42.172	1
1161	language.names.th_TH	Thai (Thailand)	2009-09-03 18:56:42.176	2009-09-03 18:56:42.176	1
1162	language.names.el_GR	Greek (Greece)	2009-09-03 18:56:42.181	2009-09-03 18:56:42.181	1
1163	language.names.it_IT	Italian (Italy)	2009-09-03 18:56:42.185	2009-09-03 18:56:42.185	1
1164	language.names.ca	Catalan	2009-09-03 18:56:42.19	2009-09-03 18:56:42.19	1
1165	language.names.hu_HU	Hungarian (Hungary)	2009-09-03 18:56:42.198	2009-09-03 18:56:42.198	1
1166	language.names.fr	French	2009-09-03 18:56:42.205	2009-09-03 18:56:42.205	1
1167	language.names.en_IE	English (Ireland)	2009-09-03 18:56:42.21	2009-09-03 18:56:42.21	1
1168	language.names.uk_UA	Ukrainian (Ukraine)	2009-09-03 18:56:42.216	2009-09-03 18:56:42.216	1
1169	language.names.pl_PL	Polish (Poland)	2009-09-03 18:56:42.221	2009-09-03 18:56:42.221	1
1170	language.names.fr_LU	French (Luxembourg)	2009-09-03 18:56:42.225	2009-09-03 18:56:42.225	1
1171	language.names.nl_BE	Dutch (Belgium)	2009-09-03 18:56:42.23	2009-09-03 18:56:42.23	1
1172	language.names.en_IN	English (India)	2009-09-03 18:56:42.234	2009-09-03 18:56:42.234	1
1173	language.names.ca_ES	Catalan (Spain)	2009-09-03 18:56:42.246	2009-09-03 18:56:42.246	1
1174	language.names.ar_MA	Arabic (Morocco)	2009-09-03 18:56:42.251	2009-09-03 18:56:42.251	1
1175	language.names.es_BO	Spanish (Bolivia)	2009-09-03 18:56:42.255	2009-09-03 18:56:42.255	1
1176	language.names.en_AU	English (Australia)	2009-09-03 18:56:42.261	2009-09-03 18:56:42.261	1
1177	language.names.sr	Serbian	2009-09-03 18:56:42.268	2009-09-03 18:56:42.268	1
1178	language.names.zh_SG	Chinese (Singapore)	2009-09-03 18:56:42.276	2009-09-03 18:56:42.276	1
1179	language.names.pt	Portuguese	2009-09-03 18:56:42.299	2009-09-03 18:56:42.299	1
1180	language.names.uk	Ukrainian	2009-09-03 18:56:42.306	2009-09-03 18:56:42.306	1
1181	language.names.es_SV	Spanish (El Salvador)	2009-09-03 18:56:42.313	2009-09-03 18:56:42.313	1
1182	language.names.ru_RU	Russian (Russia)	2009-09-03 18:56:42.319	2009-09-03 18:56:42.319	1
1183	language.names.ko_KR	Korean (South Korea)	2009-09-03 18:56:42.327	2009-09-03 18:56:42.327	1
1184	language.names.vi	Vietnamese	2009-09-03 18:56:42.339	2009-09-03 18:56:42.339	1
1185	language.names.ar_DZ	Arabic (Algeria)	2009-09-03 18:56:42.344	2009-09-03 18:56:42.344	1
1186	language.names.vi_VN	Vietnamese (Vietnam)	2009-09-03 18:56:42.35	2009-09-03 18:56:42.35	1
1187	language.names.sq	Albanian	2009-09-03 18:56:42.355	2009-09-03 18:56:42.355	1
1188	language.names.ar_LY	Arabic (Libya)	2009-09-03 18:56:42.359	2009-09-03 18:56:42.359	1
1189	language.names.ar	Arabic	2009-09-03 18:56:42.363	2009-09-03 18:56:42.363	1
1190	language.names.zh_CN	Chinese (China)	2009-09-03 18:56:42.369	2009-09-03 18:56:42.369	1
1191	language.names.be_BY	Belarusian (Belarus)	2009-09-03 18:56:42.373	2009-09-03 18:56:42.373	1
1192	language.names.zh_HK	Chinese (Hong Kong)	2009-09-03 18:56:42.378	2009-09-03 18:56:42.378	1
1193	language.names.ja	Japanese	2009-09-03 18:56:42.382	2009-09-03 18:56:42.382	1
772	troubleshooting.java.intro	@troubleshooting.java.intro@	2009-09-01 01:09:04.462	2009-09-01 01:09:27.109	3
1017	contribute.financialContributions	Financial Contributions	2009-09-03 12:29:21.388	2009-09-03 12:29:21.388	1
1194	language.names.iw_IL	Hebrew (Israel)	2009-09-03 18:56:42.387	2009-09-03 18:56:42.387	1
1195	language.names.bg_BG	Bulgarian (Bulgaria)	2009-09-03 18:56:42.403	2009-09-03 18:56:42.403	1
1196	language.names.in	Indonesian	2009-09-03 18:56:42.408	2009-09-03 18:56:42.408	1
1197	language.names.mt_MT	Maltese (Malta)	2009-09-03 18:56:42.433	2009-09-03 18:56:42.433	1
1198	language.names.es_PY	Spanish (Paraguay)	2009-09-03 18:56:42.439	2009-09-03 18:56:42.439	1
1199	language.names.sl	Slovenian	2009-09-03 18:56:42.446	2009-09-03 18:56:42.446	1
1200	language.names.fr_FR	French (France)	2009-09-03 18:56:42.453	2009-09-03 18:56:42.453	1
1201	language.names.cs_CZ	Czech (Czech Republic)	2009-09-03 18:56:42.461	2009-09-03 18:56:42.461	1
1202	language.names.it_CH	Italian (Switzerland)	2009-09-03 18:56:42.467	2009-09-03 18:56:42.467	1
1203	language.names.ro_RO	Romanian (Romania)	2009-09-03 18:56:42.473	2009-09-03 18:56:42.473	1
1204	language.names.es_PR	Spanish (Puerto Rico)	2009-09-03 18:56:42.48	2009-09-03 18:56:42.48	1
1205	language.names.en_CA	English (Canada)	2009-09-03 18:56:42.485	2009-09-03 18:56:42.485	1
1206	language.names.de_DE	German (Germany)	2009-09-03 18:56:42.489	2009-09-03 18:56:42.489	1
1207	language.names.ga	Irish	2009-09-03 18:56:42.495	2009-09-03 18:56:42.495	1
1208	language.names.de_LU	German (Luxembourg)	2009-09-03 18:56:42.502	2009-09-03 18:56:42.502	1
1209	language.names.de	German	2009-09-03 18:56:42.515	2009-09-03 18:56:42.515	1
1210	language.names.es_AR	Spanish (Argentina)	2009-09-03 18:56:42.522	2009-09-03 18:56:42.522	1
1211	language.names.sk	Slovak	2009-09-03 18:56:42.527	2009-09-03 18:56:42.527	1
1212	language.names.ms_MY	Malay (Malaysia)	2009-09-03 18:56:42.533	2009-09-03 18:56:42.533	1
1213	language.names.hr_HR	Croatian (Croatia)	2009-09-03 18:56:42.539	2009-09-03 18:56:42.539	1
1214	language.names.en_SG	English (Singapore)	2009-09-03 18:56:42.544	2009-09-03 18:56:42.544	1
1215	language.names.da	Danish	2009-09-03 18:56:42.55	2009-09-03 18:56:42.55	1
1216	language.names.mt	Maltese	2009-09-03 18:56:42.558	2009-09-03 18:56:42.558	1
1217	language.names.pl	Polish	2009-09-03 18:56:42.565	2009-09-03 18:56:42.565	1
1218	language.names.ar_OM	Arabic (Oman)	2009-09-03 18:56:42.572	2009-09-03 18:56:42.572	1
1219	language.names.tr	Turkish	2009-09-03 18:56:42.577	2009-09-03 18:56:42.577	1
1220	language.names.th_TH_TH	Thai (Thailand,TH)	2009-09-03 18:56:42.582	2009-09-03 18:56:42.582	1
1221	language.names.el	Greek	2009-09-03 18:56:42.625	2009-09-03 18:56:42.625	1
1222	language.names.ms	Malay	2009-09-03 18:56:42.654	2009-09-03 18:56:42.654	1
1223	language.names.sv_SE	Swedish (Sweden)	2009-09-03 18:56:42.659	2009-09-03 18:56:42.659	1
1224	language.names.da_DK	Danish (Denmark)	2009-09-03 18:56:42.682	2009-09-03 18:56:42.682	1
1225	language.names.es_HN	Spanish (Honduras)	2009-09-03 18:56:42.693	2009-09-03 18:56:42.693	1
1136	language.names.pt_BR	Brazilian Portuguese	2009-09-03 18:56:42.041	2009-09-03 19:47:50.492	1
1321	macwarning.title	Warning for Mac users	2009-09-03 20:06:27.035	2009-09-03 20:06:27.035	1
1322	macwarning.problem	Users who installed Apple's recent "<strong>Java for Mac OS X 10.5 Update 4</strong>" may have a problem with simulations appearing as text files. Apple knows about this problem and is working to fix it.	2009-09-03 20:12:48.784	2009-09-03 20:12:48.784	1
1323	macwarning.solution	You can work around this problem by following the directions in this question in our FAQ:	2009-09-03 20:13:01.955	2009-09-03 20:13:01.955	1
1368	keyword.graphing	الرسوم البيانية	2009-09-03 21:30:51.195	2009-09-03 21:31:03.986	4
1425	keyword.energyGap	الطاقة جاب	2009-09-03 21:57:00.116	2009-09-03 21:57:00.116	4
1426	keyword.energyLevels	مستويات الطاقة	2009-09-03 21:57:00.987	2009-09-03 21:57:00.987	4
1427	keyword.equilibrium	التوازن	2009-09-03 21:57:02.868	2009-09-03 21:57:02.868	4
1428	keyword.errorAnalysis	تحليل الخطأ	2009-09-03 21:57:04.808	2009-09-03 21:57:04.808	4
1429	keyword.estimation	تقدير	2009-09-03 21:57:07.031	2009-09-03 21:57:07.031	4
1430	keyword.excitation	الإثارة	2009-09-03 21:57:09.997	2009-09-03 21:57:09.997	4
1431	keyword.exercise	ممارسة	2009-09-03 21:57:12.538	2009-09-03 21:57:12.538	4
1432	keyword.factoring	العوملة	2009-09-03 21:57:13.322	2009-09-03 21:57:13.322	4
1433	keyword.faradaysLaw	قانون فاراداي	2009-09-03 21:57:14.276	2009-09-03 21:57:14.276	4
1434	keyword.force	القوة	2009-09-03 21:57:16.279	2009-09-03 21:57:16.279	4
1435	keyword.fourierAnalysis	تحليل فورييه	2009-09-03 21:57:17.1	2009-09-03 21:57:17.1	4
1436	keyword.fourierSeries	سلسلة فورييه	2009-09-03 21:57:17.962	2009-09-03 21:57:17.962	4
1437	keyword.frequency	تردد	2009-09-03 21:57:18.741	2009-09-03 21:57:18.741	4
1438	keyword.friction	الاحتكاك	2009-09-03 21:57:19.58	2009-09-03 21:57:19.58	4
1439	keyword.fuelConsumption	استهلاك الوقود	2009-09-03 21:57:21.761	2009-09-03 21:57:21.761	4
1440	keyword.galton	غالتون	2009-09-03 21:57:22.565	2009-09-03 21:57:22.565	4
1441	keyword.gas	الغاز	2009-09-03 21:57:23.508	2009-09-03 21:57:23.508	4
1442	keyword.generator	مولد	2009-09-03 21:57:24.315	2009-09-03 21:57:24.315	4
1443	keyword.glaciers	الأنهار الجليدية	2009-09-03 21:57:25.133	2009-09-03 21:57:25.133	4
1444	keyword.gravitationalForce	قوة الجاذبية	2009-09-03 21:57:27.343	2009-09-03 21:57:27.343	4
1445	keyword.gravity	الجاذبية	2009-09-03 21:57:28.302	2009-09-03 21:57:28.302	4
1446	keyword.greenhouseEffect	الاحتباس الحراري	2009-09-03 21:57:29.117	2009-09-03 21:57:29.117	4
1447	keyword.greenhouseGases	غازات الدفيئة	2009-09-03 21:57:29.949	2009-09-03 21:57:29.949	4
1448	keyword.harmonicMotion	الحركة التوافقية	2009-09-03 21:57:30.72	2009-09-03 21:57:30.72	4
1449	keyword.heat	حرارة	2009-09-03 21:57:31.601	2009-09-03 21:57:31.601	4
1450	keyword.histograms	المدرج الإحصائي	2009-09-03 21:57:33.625	2009-09-03 21:57:33.625	4
1451	keyword.hookesLaw	قانون هوك	2009-09-03 21:57:34.413	2009-09-03 21:57:34.413	4
1452	keyword.hydrogenAtom	ذرة الهيدروجين	2009-09-03 21:57:35.317	2009-09-03 21:57:35.317	4
1453	keyword.hydronium	الهيدرونيوم	2009-09-03 21:57:36.151	2009-09-03 21:57:36.151	4
1454	keyword.hydroxide	هيدروكسيد	2009-09-03 21:57:36.968	2009-09-03 21:57:36.968	4
1455	keyword.idealGasLaw	قانون الغاز المثالي	2009-09-03 21:57:37.888	2009-09-03 21:57:37.888	4
1456	keyword.images	الصور	2009-09-03 21:57:39.958	2009-09-03 21:57:39.958	4
1457	keyword.indexOfRefraction	مؤشر الانكسار	2009-09-03 21:57:41.068	2009-09-03 21:57:41.068	4
1458	keyword.induction	الحث	2009-09-03 21:57:41.92	2009-09-03 21:57:41.92	4
1459	keyword.inductor	مغو	2009-09-03 21:57:42.979	2009-09-03 21:57:42.979	4
1460	keyword.infrared	الأشعة تحت الحمراء	2009-09-03 21:57:45.099	2009-09-03 21:57:45.099	4
1461	keyword.initialSpeed	السرعة الأولي	2009-09-03 21:57:45.964	2009-09-03 21:57:45.964	4
1462	keyword.insulators	عوازل	2009-09-03 21:57:46.879	2009-09-03 21:57:46.879	4
1463	keyword.intensity	كثافة	2009-09-03 21:57:47.82	2009-09-03 21:57:47.82	4
1464	keyword.interference	التدخل	2009-09-03 21:57:48.83	2009-09-03 21:57:48.83	4
1465	keyword.inverse	معكوس	2009-09-03 21:57:49.865	2009-09-03 21:57:49.865	4
1466	keyword.ionicCompounds	الأيونية Componds	2009-09-03 21:57:52.019	2009-09-03 21:57:52.019	4
1467	keyword.kinematics	الكينماتيكا	2009-09-03 21:57:52.821	2009-09-03 21:57:52.821	4
1468	keyword.kineticEnergy	الطاقة الحركية	2009-09-03 21:57:53.662	2009-09-03 21:57:53.662	4
1469	keyword.kirchoffsLaw	كيرتشوف قانون	2009-09-03 21:57:54.42	2009-09-03 21:57:54.42	4
1470	keyword.ksp	Ksp	2009-09-03 21:57:55.422	2009-09-03 21:57:55.422	4
1471	keyword.laser	ليزر	2009-09-03 21:57:57.202	2009-09-03 21:57:57.202	4
1472	keyword.lcCircuit	ش حلبة	2009-09-03 21:57:57.769	2009-09-03 21:57:57.769	4
1473	keyword.leChateliersPrinciple	لو Chatelier في المبدأ	2009-09-03 21:58:00.112	2009-09-03 21:58:00.112	4
1474	keyword.leds	الأضواء	2009-09-03 21:58:01.122	2009-09-03 21:58:01.122	4
1475	keyword.length	طول	2009-09-03 21:58:02.075	2009-09-03 21:58:02.075	4
1226	footer.someRightsReserved	بعض الحقوق محفوظة.	2009-09-03 19:19:34.187	2009-09-03 19:19:34.187	4
1227	contribute.financialContributions	المساهمات المالية	2009-09-03 19:19:39.665	2009-09-03 19:19:39.665	4
1228	language.names.ar	العربية	2009-09-03 19:19:49.087	2009-09-03 19:19:49.087	4
1230	language.names.ar_BH	العربية (البحرين)	2009-09-03 19:19:54.501	2009-09-03 19:19:54.501	4
1229	language.names.ar_AE	العربية (الإمارات العربية المتحدة)	2009-09-03 19:19:51.87	2009-09-03 19:20:05.248	4
1231	language.names.ar_DZ	العربية (الجزائر)	2009-09-03 19:20:07.778	2009-09-03 19:20:07.778	4
1232	language.names.ar_EG	العربية (مصر)	2009-09-03 19:20:10.53	2009-09-03 19:20:10.53	4
1233	language.names.ar_IQ	العربية (العراق)	2009-09-03 19:20:14.332	2009-09-03 19:20:14.332	4
1234	language.names.ar_JO	العربية (الأردن)	2009-09-03 19:20:17.7	2009-09-03 19:20:17.7	4
1235	language.names.ar_KW	العربية (الكويت)	2009-09-03 19:20:19.708	2009-09-03 19:20:19.708	4
1236	language.names.ar_LB	العربية (لبنان)	2009-09-03 19:20:22.543	2009-09-03 19:20:22.543	4
1237	language.names.ar_LY	العربية (ليبيا)	2009-09-03 19:20:25.091	2009-09-03 19:20:25.091	4
1238	language.names.ar_MA	العربية (المغرب)	2009-09-03 19:20:27.649	2009-09-03 19:20:27.649	4
1324	macwarning.title	تحذير لمستخدمي ماك	2009-09-03 20:20:35.173	2009-09-03 20:20:35.173	4
1325	macwarning.solution	يمكنك العمل حول هذه المشكلة عن طريق اتباع الإرشادات الموجودة في هذه المسألة في الأسئلة الشائعة لدينا :	2009-09-03 20:20:38.082	2009-09-03 20:20:38.082	4
1485	keyword.magnets	مغناطيس	2009-09-03 21:58:13.583	2009-09-03 21:58:13.583	4
1326	macwarning.problem	المستخدمين الذين تثبيت آبل الأخيرة <strong>جافا لماكنتوش 10.5 تحديث 4</strong> قد تكون المشكلة مع عمليات المحاكاة التي تظهر على شكل ملفات نصية. التفاح ويعرف عن هذه المشكلة وتعمل على حلها.	2009-09-03 20:20:59.73	2009-09-03 20:21:16.773	4
1327	language.names.bg_BG	البلغارية (بلغاريا)	2009-09-03 20:23:46.963	2009-09-03 20:23:46.963	4
1328	language.names.ca_ES	الكاتالوينية (اسبانيا)	2009-09-03 20:23:50.768	2009-09-03 20:23:50.768	4
1329	keyword.activationEnergy	طاقة التنشيط	2009-09-03 20:24:09.838	2009-09-03 20:24:09.838	4
1330	keyword.airResistance	مقاومة الهواء	2009-09-03 20:24:13.916	2009-09-03 20:24:13.916	4
1331	keyword.algebra	علم الجبر	2009-09-03 20:24:18.166	2009-09-03 20:24:18.166	4
1332	keyword.alphaParticles	جسيمات ألفا	2009-09-03 20:24:22.082	2009-09-03 20:24:22.082	4
1333	keyword.alternatingCurrent	التيار المتردد	2009-09-03 20:24:25.683	2009-09-03 20:24:25.683	4
1334	keyword.ammeter	مقياس التيار الكهربائي	2009-09-03 20:24:30.223	2009-09-03 20:24:30.223	4
1335	keyword.ammonia	جزيئات النشادر	2009-09-03 20:24:34.223	2009-09-03 20:24:34.223	4
1336	keyword.amplitude	السعة	2009-09-03 20:24:39.851	2009-09-03 20:24:39.851	4
1337	keyword.angle	زاوية	2009-09-03 20:24:43.494	2009-09-03 20:24:43.494	4
1338	keyword.angularAcceleration	التسارع الزاوي	2009-09-03 20:24:47.567	2009-09-03 20:24:47.567	4
1339	keyword.angularMomentum	الزخم الزاوي	2009-09-03 20:24:51.721	2009-09-03 20:24:51.721	4
1340	keyword.angularPosition	المركز الزاوي	2009-09-03 20:24:56.367	2009-09-03 20:24:56.367	4
1341	keyword.angularVelocity	الزاوي السرعة	2009-09-03 20:25:02.309	2009-09-03 20:25:02.309	4
1342	keyword.area	المنطقة	2009-09-03 20:25:06.588	2009-09-03 20:25:06.588	4
1343	keyword.arithmetic	حسابي	2009-09-03 20:25:12.371	2009-09-03 20:25:12.371	4
1344	keyword.arrheniusParameters	أرهينيس معلمات	2009-09-03 20:25:16.204	2009-09-03 20:25:16.204	4
1345	keyword.astronomy	علم الفلك	2009-09-03 20:25:20.862	2009-09-03 20:25:20.862	4
1346	keyword.atmosphere	الغلاف الجوي	2009-09-03 20:25:25.745	2009-09-03 20:25:25.745	4
1347	keyword.atomicNuclei	نوى ذرية	2009-09-03 20:25:37.135	2009-09-03 20:25:37.135	4
1348	keyword.atomicStructure	الهيكل الذري	2009-09-03 20:25:42.649	2009-09-03 20:25:42.649	4
1349	keyword.atoms	ذرات	2009-09-03 20:25:47.862	2009-09-03 20:25:47.862	4
1350	keyword.bases	قواعد	2009-09-03 20:25:52.549	2009-09-03 20:25:52.549	4
1351	keyword.battery	بطاريات	2009-09-03 20:25:57.946	2009-09-03 20:25:57.946	4
1352	keyword.binomialDistribution	توزيع ذات الحدين	2009-09-03 20:26:02.671	2009-09-03 20:26:02.671	4
1353	keyword.biology	علم الأحياء	2009-09-03 20:26:09.805	2009-09-03 20:26:09.805	4
1354	keyword.blackbody	الأسود	2009-09-03 20:26:14.916	2009-09-03 20:26:14.916	4
1355	keyword.bohrModel	نموذج بور	2009-09-03 20:29:33.725	2009-09-03 20:29:33.725	4
1356	keyword.boltzmannDistribution	توزيع بولتزمان	2009-09-03 20:29:44.792	2009-09-03 20:29:44.792	4
1357	keyword.bonds	السندات	2009-09-03 20:31:36.52	2009-09-03 20:31:36.52	4
1358	keyword.boylesLaw	قانون بويل	2009-09-03 20:32:28.869	2009-09-03 20:32:28.869	4
1359	keyword.buoyancy	الطفو	2009-09-03 20:34:01.591	2009-09-03 20:34:01.591	4
1369	keyword.fields	الميادين	2009-09-03 21:35:41.77	2009-09-03 21:35:41.77	4
1370	keyword.food	طعام	2009-09-03 21:35:56.496	2009-09-03 21:35:56.496	4
1371	keyword.focalLength	البعد البؤري	2009-09-03 21:35:59.389	2009-09-03 21:35:59.389	4
1476	keyword.lens	عدسة	2009-09-03 21:58:03.316	2009-09-03 21:58:03.316	4
1477	keyword.light	ضوء	2009-09-03 21:58:05.577	2009-09-03 21:58:05.577	4
1478	keyword.lightBulbs	ضوء لمبات	2009-09-03 21:58:06.532	2009-09-03 21:58:06.532	4
1479	keyword.linearMotion	الحركة الخطية	2009-09-03 21:58:07.171	2009-09-03 21:58:07.171	4
1480	keyword.longitudinalWaves	موجات طولية	2009-09-03 21:58:08.004	2009-09-03 21:58:08.004	4
1481	keyword.lunarLanding	هبوط على سطح القمر	2009-09-03 21:58:08.82	2009-09-03 21:58:08.82	4
1482	keyword.magneticField	الحقل المغناطيسي	2009-09-03 21:58:09.851	2009-09-03 21:58:09.851	4
1483	keyword.magneticMoment	لحظة المغناطيسي	2009-09-03 21:58:11.904	2009-09-03 21:58:11.904	4
1484	keyword.magnetism	المغناطيسية	2009-09-03 21:58:12.717	2009-09-03 21:58:12.717	4
1486	keyword.mass	الشامل	2009-09-03 21:58:14.496	2009-09-03 21:58:14.496	4
1487	keyword.math	الرياضيات	2009-09-03 21:58:15.467	2009-09-03 21:58:15.467	4
1488	keyword.measurement	القياس	2009-09-03 21:58:20.467	2009-09-03 21:58:20.467	4
1489	keyword.microwaves	الميكروويف	2009-09-03 21:58:21.325	2009-09-03 21:58:21.325	4
1490	keyword.molarity	Molarity	2009-09-03 21:58:22.17	2009-09-03 21:58:22.17	4
1491	keyword.molecularMotors	الجزيئية موتورز	2009-09-03 21:58:23.387	2009-09-03 21:58:23.387	4
1492	keyword.molecules	جزيئات	2009-09-03 21:58:24.811	2009-09-03 21:58:24.811	4
1493	keyword.moles	حيوانات الخلد	2009-09-03 21:58:26.992	2009-09-03 21:58:26.992	4
1494	keyword.momentOfInertia	لحظة من القصور الذاتي	2009-09-03 21:58:27.972	2009-09-03 21:58:27.972	4
1495	keyword.monochromatic	الضوء الأحادي	2009-09-03 21:58:28.944	2009-09-03 21:58:28.944	4
1496	keyword.moon	قمر	2009-09-03 21:58:31.578	2009-09-03 21:58:31.578	4
1497	keyword.motion	اقتراح	2009-09-03 21:58:32.276	2009-09-03 21:58:32.276	4
1498	keyword.mri	التصوير بالرنين المغناطيسي	2009-09-03 21:58:33.116	2009-09-03 21:58:33.116	4
1499	keyword.multiplication	الضرب	2009-09-03 21:58:35.333	2009-09-03 21:58:35.333	4
1500	keyword.newtonsLaws	قوانين نيوتن	2009-09-03 21:58:36.611	2009-09-03 21:58:36.611	4
1501	keyword.ohmsLaw	قانون أوم	2009-09-03 21:58:37.403	2009-09-03 21:58:37.403	4
1502	keyword.optical	البصريات	2009-09-03 21:58:38.371	2009-09-03 21:58:38.371	4
1239	language.names.ar_OM	العربية (عمان)	2009-09-03 19:20:31.027	2009-09-03 19:20:31.027	4
1240	language.names.ar_QA	العربية (قطر)	2009-09-03 19:20:33.003	2009-09-03 19:20:33.003	4
1241	language.names.ar_SA	العربية (المملكة العربية السعودية)	2009-09-03 19:20:35.102	2009-09-03 19:20:35.102	4
1242	language.names.ar_SD	العربية (السودان)	2009-09-03 19:20:37.263	2009-09-03 19:20:37.263	4
1243	language.names.ar_SY	العربية (سوريا)	2009-09-03 19:20:40.692	2009-09-03 19:20:40.692	4
1244	language.names.ar_TN	العربية (تونس)	2009-09-03 19:20:42.868	2009-09-03 19:20:42.868	4
1519	keyword.polarization	الاستقطاب	2009-09-03 21:58:59.127	2009-09-03 21:58:59.127	4
1520	keyword.polynomials	متعددو الحدود	2009-09-03 21:59:00.013	2009-09-03 21:59:00.013	4
1521	keyword.position	موقف	2009-09-03 21:59:00.954	2009-09-03 21:59:00.954	4
1522	keyword.potentialBarrier	حاجز المحتملة	2009-09-03 21:59:01.878	2009-09-03 21:59:01.878	4
1523	keyword.potentialEnergy	طاقة كامنة	2009-09-03 21:59:02.826	2009-09-03 21:59:02.826	4
1361	keyword.calories	السعرات الحرارية	2009-09-03 21:05:04.655	2009-09-03 21:05:25.053	4
1362	keyword.capacitance	مواسعة	2009-09-03 21:05:34.509	2009-09-03 21:05:34.509	4
1363	keyword.capacitor	مكثف	2009-09-03 21:05:37.403	2009-09-03 21:05:37.403	4
1364	keyword.catalysts	المواد الحفازة	2009-09-03 21:05:44.988	2009-09-03 21:05:44.988	4
1524	keyword.potentialWells	آبار المحتملة	2009-09-03 21:59:04.882	2009-09-03 21:59:04.882	4
1372	keyword.doping	المنشطات	2009-09-03 21:38:46.23	2009-09-03 21:39:20.101	4
1373	keyword.dna	الحمض النووي	2009-09-03 21:38:49.611	2009-09-03 21:39:25.175	4
1360	keyword.calculation	الحساب	2009-09-03 21:04:24.725	2009-09-03 21:39:30.319	4
1374	keyword.charges	رسوم الكهرباء	2009-09-03 21:40:26.421	2009-09-03 21:40:26.421	4
1375	keyword.charlesLaw	تشارلز القانون	2009-09-03 21:40:27.525	2009-09-03 21:40:27.525	4
1376	keyword.chemicalEquilibrium	التوازن الكيميائي	2009-09-03 21:40:28.421	2009-09-03 21:40:28.421	4
1377	keyword.chemicalFormula	الصيغة الكيميائية	2009-09-03 21:40:29.303	2009-09-03 21:40:29.303	4
1378	keyword.chemistry	الكيمياء	2009-09-03 21:40:30.478	2009-09-03 21:40:30.478	4
1379	keyword.chi	تشي	2009-09-03 21:40:32.683	2009-09-03 21:40:32.683	4
1525	keyword.power	طاقة	2009-09-03 21:59:05.644	2009-09-03 21:59:05.644	4
1380	troubleshooting.javascript.q1.yes	<p><strong>يتم تمكين جافا سكريبت</strong></p>	2009-09-03 21:41:06.39	2009-09-03 21:41:46.355	4
1526	keyword.pressure	الضغط	2009-09-03 21:59:06.515	2009-09-03 21:59:06.515	4
1381	troubleshooting.javascript.q1.no	<p><strong><span style="color: red;">لم </span></strong></p>يتم تمكين جافا سكريبت	2009-09-03 21:42:14.128	2009-09-03 21:42:58.798	4
1527	keyword.principleRays	مبدأ الأشعة	2009-09-03 21:59:07.426	2009-09-03 21:59:07.426	4
1528	keyword.probability	احتمال	2009-09-03 21:59:08.3	2009-09-03 21:59:08.3	4
1529	keyword.probabilityDensity	احتمال الكثافة	2009-09-03 21:59:10.909	2009-09-03 21:59:10.909	4
1530	keyword.projectileMotion	مقذوف موشن	2009-09-03 21:59:11.707	2009-09-03 21:59:11.707	4
1382	contribute.thanks	PhET أود أن أشكر <a {0}> الرعاة </a> و <a {1}> المالكة التفاعلية </a> التصميم الأصلي للموقع وتخطيط.	2009-09-03 21:43:35.202	2009-09-03 21:44:48.681	4
1383	keyword.chiSquare	شي مربع	2009-09-03 21:45:03.945	2009-09-03 21:45:03.945	4
1384	keyword.chiSquared	شي التربيعية	2009-09-03 21:45:05.276	2009-09-03 21:45:05.276	4
1385	keyword.circularMotion	الحركة الدائرية	2009-09-03 21:45:06.508	2009-09-03 21:45:06.508	4
1386	keyword.circularRevolution	التعميم الثورة	2009-09-03 21:45:08.937	2009-09-03 21:45:08.937	4
1387	keyword.climate	المناخ	2009-09-03 21:45:10.293	2009-09-03 21:45:10.293	4
1388	keyword.climateChange	تغير المناخ	2009-09-03 21:45:11.44	2009-09-03 21:45:11.44	4
1389	keyword.compass	البوصلة	2009-09-03 21:45:12.352	2009-09-03 21:45:12.352	4
1390	keyword.concentration	تركيز	2009-09-03 21:45:14.529	2009-09-03 21:45:14.529	4
1391	keyword.conductivity	الموصلية	2009-09-03 21:45:16.506	2009-09-03 21:45:16.506	4
1503	keyword.opticalTweezers	البصري الملقط	2009-09-03 21:58:40.322	2009-09-03 21:58:40.322	4
1504	keyword.orderOfMagnitude	الحوسبي	2009-09-03 21:58:41.297	2009-09-03 21:58:41.297	4
1505	keyword.orderParameter	النظام معلمة	2009-09-03 21:58:42.135	2009-09-03 21:58:42.135	4
1506	keyword.pH	الرقم الهيدروجيني	2009-09-03 21:58:43.104	2009-09-03 21:58:43.104	4
1507	keyword.parallelCircuit	الدوائر المتوازية	2009-09-03 21:58:44.074	2009-09-03 21:58:44.074	4
1508	keyword.particles	جسيمات	2009-09-03 21:58:45.4	2009-09-03 21:58:45.4	4
1509	keyword.pendulum	رقاص الساعة	2009-09-03 21:58:47.568	2009-09-03 21:58:47.568	4
1510	keyword.period	الفترة	2009-09-03 21:58:48.564	2009-09-03 21:58:48.564	4
1511	keyword.periodicMotion	الدوري موشن	2009-09-03 21:58:49.491	2009-09-03 21:58:49.491	4
1512	keyword.photoconductors	Photoconductors	2009-09-03 21:58:50.398	2009-09-03 21:58:50.398	4
1513	keyword.photonBeams	الفوتون الكمرات	2009-09-03 21:58:51.465	2009-09-03 21:58:51.465	4
1514	keyword.photons	الفوتونات	2009-09-03 21:58:53.418	2009-09-03 21:58:53.418	4
1515	keyword.pitch	الملعب	2009-09-03 21:58:54.366	2009-09-03 21:58:54.366	4
1516	keyword.planets	الكواكب	2009-09-03 21:58:55.134	2009-09-03 21:58:55.134	4
1517	keyword.plumPuddingModel	البرقوق مهلبية النموذجي	2009-09-03 21:58:56.04	2009-09-03 21:58:56.04	4
1518	keyword.pointCharge	نقطة المسؤول	2009-09-03 21:58:57.044	2009-09-03 21:58:57.044	4
1531	keyword.pulses	البقول	2009-09-03 21:59:12.612	2009-09-03 21:59:12.612	4
1532	keyword.pvWork	الفلطاضوئية العمل	2009-09-03 21:59:13.592	2009-09-03 21:59:13.592	4
1533	keyword.quantumMeasurement	القياس الكمي	2009-09-03 21:59:14.765	2009-09-03 21:59:14.765	4
1534	keyword.quantumMechanics	ميكانيكا الكم	2009-09-03 21:59:15.635	2009-09-03 21:59:15.635	4
1535	keyword.quantumParticles	كمية الجسيمات	2009-09-03 21:59:16.563	2009-09-03 21:59:16.563	4
1536	keyword.radiation	الإشعاع	2009-09-03 21:59:19.145	2009-09-03 21:59:19.145	4
1537	keyword.radioWaves	موجات الراديو	2009-09-03 21:59:20.136	2009-09-03 21:59:20.136	4
1538	keyword.rainbows	أقواس قزح	2009-09-03 21:59:21.16	2009-09-03 21:59:21.16	4
1539	keyword.rateCoefficients	معدل معاملات	2009-09-03 21:59:22.144	2009-09-03 21:59:22.144	4
1540	keyword.rcCircuit	الصليب الأحمر حلبة	2009-09-03 21:59:23.282	2009-09-03 21:59:23.282	4
1541	keyword.reaction	ردود الفعل	2009-09-03 21:59:24.185	2009-09-03 21:59:24.185	4
1542	keyword.refraction	الانكسار	2009-09-03 21:59:25.086	2009-09-03 21:59:25.086	4
1543	keyword.resistance	المقاومة	2009-09-03 22:00:16.184	2009-09-03 22:00:16.184	4
1544	keyword.resistivity	المقاومية	2009-09-03 22:00:17.601	2009-09-03 22:00:17.601	4
1545	keyword.resistor	المقاوم	2009-09-03 22:00:19.042	2009-09-03 22:00:19.042	4
1546	keyword.resonance	الرنين	2009-09-03 22:00:20.33	2009-09-03 22:00:20.33	4
1547	keyword.rlcCircuit	مدينة رأس لفان حلبة	2009-09-03 22:00:21.399	2009-09-03 22:00:21.399	4
1548	keyword.rotation	تناوب	2009-09-03 22:00:22.636	2009-09-03 22:00:22.636	4
1549	keyword.rotationalMotion	التناوب موشن	2009-09-03 22:00:38.538	2009-09-03 22:00:38.538	4
1550	keyword.rutherfordScattering	روثرفورد الناثرات	2009-09-03 22:00:44.155	2009-09-03 22:00:44.155	4
1551	keyword.salt	الملح	2009-09-03 22:00:45.068	2009-09-03 22:00:45.068	4
1552	keyword.satellites	الأقمار الصناعية	2009-09-03 22:00:46.155	2009-09-03 22:00:46.155	4
1553	keyword.saturation	التشبع	2009-09-03 22:00:46.928	2009-09-03 22:00:46.928	4
1245	language.names.ar_YE	العربية (اليمنية)	2009-09-03 19:24:24.401	2009-09-03 19:24:24.401	4
1246	language.names.be	البيلوروسية	2009-09-03 19:24:34.551	2009-09-03 19:24:34.551	4
1247	language.names.be_BY	البيلوروسية (بيلاروس)	2009-09-03 19:24:38.103	2009-09-03 19:24:38.103	4
1248	language.names.bg	البلغارية	2009-09-03 19:24:41.615	2009-09-03 19:24:41.615	4
1249	language.names.ca	الكاتالوينية	2009-09-03 19:24:45.25	2009-09-03 19:24:45.25	4
1250	language.names.cs	التشيكية	2009-09-03 19:24:56.606	2009-09-03 19:24:56.606	4
1251	language.names.da	الدانماركية	2009-09-03 19:25:00.323	2009-09-03 19:25:00.323	4
1252	language.names.de	الألمانية	2009-09-03 19:25:03.877	2009-09-03 19:25:03.877	4
1253	language.names.el	اليونانية	2009-09-03 19:25:08.424	2009-09-03 19:25:08.424	4
1254	language.names.en	الانجليزية	2009-09-03 19:25:11.689	2009-09-03 19:25:11.689	4
1255	language.names.es	الأسبانية	2009-09-03 19:25:18.215	2009-09-03 19:25:18.215	4
1256	language.names.et	الأستونية	2009-09-03 19:25:41.004	2009-09-03 19:25:41.004	4
1257	language.names.fi	الفنلندية	2009-09-03 19:25:43.268	2009-09-03 19:25:43.268	4
1258	language.names.fr	الفرنسية	2009-09-03 19:25:46.807	2009-09-03 19:25:46.807	4
1259	language.names.ga	الأيرلندية	2009-09-03 19:25:50.511	2009-09-03 19:25:50.511	4
1260	language.names.hi_IN	الهندية (الهند)	2009-09-03 19:25:53.4	2009-09-03 19:25:53.4	4
1261	language.names.hr	الكرواتية	2009-09-03 19:25:58.161	2009-09-03 19:25:58.161	4
1262	language.names.hu	الهنغارية	2009-09-03 19:26:01.674	2009-09-03 19:26:01.674	4
1263	language.names.in	الأندونيسية	2009-09-03 19:26:04.102	2009-09-03 19:26:04.102	4
1264	language.names.is	الأيسلاندية	2009-09-03 19:26:07.799	2009-09-03 19:26:07.799	4
1265	language.names.it	الايطالية	2009-09-03 19:26:10.971	2009-09-03 19:26:10.971	4
1266	language.names.it_IT	الايطالية (ايطاليا)	2009-09-03 19:26:24.29	2009-09-03 19:26:24.29	4
1267	language.names.it_CH	الايطالية (سويسرا)	2009-09-03 19:26:30.607	2009-09-03 19:26:30.607	4
1268	language.names.iw	العبرية	2009-09-03 19:26:34.38	2009-09-03 19:26:34.38	4
1269	language.names.iw_IL	العبرية (إسرائيل)	2009-09-03 19:26:36.762	2009-09-03 19:26:36.762	4
1270	language.names.ja	اليابانية	2009-09-03 19:26:39.31	2009-09-03 19:26:39.31	4
1271	language.names.ko	الكورية	2009-09-03 19:26:43.312	2009-09-03 19:26:43.312	4
1272	language.names.lt	اللتوانية	2009-09-03 19:26:46.189	2009-09-03 19:26:46.189	4
1273	language.names.mk	المقدونية	2009-09-03 19:26:50.184	2009-09-03 19:26:50.184	4
1274	language.names.mt	المالطية	2009-09-03 19:26:54.22	2009-09-03 19:26:54.22	4
1275	language.names.nl	الهولندية	2009-09-03 19:26:56.809	2009-09-03 19:26:56.809	4
1276	language.names.no	النرويجية	2009-09-03 19:27:00.807	2009-09-03 19:27:00.807	4
1277	language.names.pl	البولندية	2009-09-03 19:27:03.396	2009-09-03 19:27:03.396	4
1278	language.names.pt	البرتغالية	2009-09-03 19:27:07.16	2009-09-03 19:27:07.16	4
1280	language.names.ro	الرومانية	2009-09-03 19:27:32.061	2009-09-03 19:27:32.061	4
516	troubleshooting.main.q5.title	@troubleshooting.main.q5.title@	2009-08-31 14:18:28.732	2009-08-31 14:18:28.732	3
1281	language.names.ru	الروسية	2009-09-03 19:27:37.03	2009-09-03 19:27:37.03	4
1282	language.names.sk	السلوفاكية	2009-09-03 19:27:40.047	2009-09-03 19:27:40.047	4
1283	language.names.sl	السلوفانية	2009-09-03 19:27:44.105	2009-09-03 19:27:44.105	4
1284	language.names.sq	الألبانية	2009-09-03 19:27:47.982	2009-09-03 19:27:47.982	4
1285	language.names.sr	الصربية	2009-09-03 19:27:51.716	2009-09-03 19:27:51.716	4
1286	language.names.sv	السويدية	2009-09-03 19:27:55.907	2009-09-03 19:27:55.907	4
1287	language.names.th	التايلاندية	2009-09-03 19:28:00.353	2009-09-03 19:28:00.353	4
1288	language.names.tr	التركية	2009-09-03 19:28:04.515	2009-09-03 19:28:04.515	4
1289	language.names.uk	الأوكرانية	2009-09-03 19:28:08.618	2009-09-03 19:28:08.618	4
1290	language.names.vi	الفيتنامية	2009-09-03 19:28:12.769	2009-09-03 19:28:12.769	4
1291	language.names.zh	الصينية	2009-09-03 19:28:15.716	2009-09-03 19:28:15.716	4
1292	language.names.zh_CN	الصينية (الصين)	2009-09-03 19:28:20.338	2009-09-03 19:28:20.338	4
1293	language.names.zh_HK	الصينية (هونج كونج)	2009-09-03 19:28:23.968	2009-09-03 19:28:23.968	4
1294	language.names.zh_SG	الصينية (سنغافورة)	2009-09-03 19:28:27.374	2009-09-03 19:28:27.374	4
1295	language.names.zh_TW	الصينية (تايوان)	2009-09-03 19:28:34.794	2009-09-03 19:28:34.794	4
1296	simulationMainPanel.softwareRequirements	متطلبات البرنامج	2009-09-03 19:29:03.984	2009-09-03 19:29:03.984	4
1297	simulationMainPanel.sampleLearningGoals	نموذج التعلم للألفية	2009-09-03 19:29:06.645	2009-09-03 19:29:06.645	4
1298	simulationMainPanel.credits	ائتمانات	2009-09-03 19:29:09.541	2009-09-03 19:29:09.541	4
1299	simulationMainPanel.language	لغة	2009-09-03 19:29:11.845	2009-09-03 19:29:11.845	4
1300	simulationMainPanel.languageTranslated	لغة (مترجم)	2009-09-03 19:29:14.398	2009-09-03 19:29:14.398	4
1301	simulationMainPanel.simulationTitleTranslated	محاكاة العنوان (مترجم)	2009-09-03 19:29:17.203	2009-09-03 19:29:17.203	4
1302	simulationMainPanel.designTeam	فريق التصميم	2009-09-03 19:29:19.867	2009-09-03 19:29:19.867	4
1303	simulationMainPanel.thirdPartyLibraries	المكتبات خارجية	2009-09-03 19:29:22.833	2009-09-03 19:29:22.833	4
1304	simulationMainPanel.thanksTo	بفضل	2009-09-03 19:29:25.744	2009-09-03 19:29:25.744	4
1305	sponsors.header	PhET نود أن نشكر المنظمات التالية ، والشركات ، والأفراد لدعمها :	2009-09-03 19:29:50.301	2009-09-03 19:29:50.301	4
1306	sponsors.financialSupport	الدعم المالي	2009-09-03 19:29:52.844	2009-09-03 19:29:52.844	4
1307	sponsors.platinum	البلاتين مستوى المساهمون ($ 100k +)	2009-09-03 19:29:55.956	2009-09-03 19:29:55.956	4
1308	sponsors.gold	الذهب المستوى المساهمون ($ 10k -- $ 100k)	2009-09-03 19:29:58.75	2009-09-03 19:29:58.75	4
1309	sponsors.bronze	برونزية المستوى المساهمون ($ 0 -- $ 10k)	2009-09-03 19:30:01.189	2009-09-03 19:30:01.189	4
1310	sponsors.technicalSupport	الدعم التقني	2009-09-03 19:30:04.252	2009-09-03 19:30:04.252	4
1311	sponsors.piccolo.desc	مفتوح المصدر الرسومات المكتبة	2009-09-03 19:30:07.706	2009-09-03 19:30:07.706	4
1312	sponsors.jfreechart.desc	مفتوح المصدر التخطيط المكتبة	2009-09-03 19:30:13.563	2009-09-03 19:30:13.563	4
1313	sponsors.jade.desc	محرك ديناميات	2009-09-03 19:30:16.538	2009-09-03 19:30:16.538	4
1314	sponsors.sourceforge.desc	لاستضافة اجتماعنا مستودع شفرة المصدر	2009-09-03 19:30:20.631	2009-09-03 19:30:20.631	4
1315	sponsors.proguard.desc	مفتوح المصدر أداة لرمز انكماش	2009-09-03 19:30:22.923	2009-09-03 19:30:22.923	4
1316	sponsors.jetbrains.desc	لدينا لتوفير بيئة تطوير جافا	2009-09-03 19:30:25.417	2009-09-03 19:30:25.417	4
1317	sponsors.ej.desc	من أجل توفير لدينا المحللون جافا	2009-09-03 19:30:29.392	2009-09-03 19:30:29.392	4
1318	sponsors.bitrock.desc	لدينا لتوفير البناء المثبت	2009-09-03 19:30:31.753	2009-09-03 19:30:31.753	4
1319	sponsors.royalinteractive.desc	المفهوم الأصلي للموقع	2009-09-03 19:30:34.639	2009-09-03 19:30:34.639	4
1320	sponsors.dynamicalSystems.desc	لتقديم واحدة من الدروس	2009-09-03 19:30:37.28	2009-09-03 19:30:37.28	4
1365	keyword.cathodeRayTube	أنبوب أشعة الكاثود	2009-09-03 21:13:34.014	2009-09-03 21:13:34.014	4
1366	keyword.circuits	الدوائر	2009-09-03 21:19:49.364	2009-09-03 21:19:49.364	4
1367	keyword.equipotential	الكمون	2009-09-03 21:23:00.246	2009-09-03 21:23:00.246	4
1392	keyword.conductors	الموصلات	2009-09-03 21:56:15.137	2009-09-03 21:56:15.137	4
1393	keyword.conservationOfEnergy	الحفاظ على الطاقة	2009-09-03 21:56:16.524	2009-09-03 21:56:16.524	4
1394	keyword.correlation	ارتباط	2009-09-03 21:56:17.371	2009-09-03 21:56:17.371	4
1395	keyword.cosines	جيب التمام	2009-09-03 21:56:19.632	2009-09-03 21:56:19.632	4
1396	keyword.coulombsLaw	قانون كولوم	2009-09-03 21:56:20.368	2009-09-03 21:56:20.368	4
1397	keyword.covalentBonds	الرابطة التساهمية	2009-09-03 21:56:21.294	2009-09-03 21:56:21.294	4
1398	keyword.criticalExponent	الأس الحرجة	2009-09-03 21:56:22.326	2009-09-03 21:56:22.326	4
1399	keyword.criticalParameter	المعلمة الحرجة	2009-09-03 21:56:23.309	2009-09-03 21:56:23.309	4
1400	keyword.current	حالي	2009-09-03 21:56:24.244	2009-09-03 21:56:24.244	4
1401	keyword.curves	المنحنيات	2009-09-03 21:56:26.395	2009-09-03 21:56:26.395	4
1402	keyword.deBroglieWavelength	DeBroglie الموجي	2009-09-03 21:56:30.232	2009-09-03 21:56:30.232	4
1403	keyword.diet	حمية	2009-09-03 21:56:31.87	2009-09-03 21:56:31.87	4
1404	keyword.diffraction	الحيود	2009-09-03 21:56:33.164	2009-09-03 21:56:33.164	4
1405	keyword.dilution	تمييع	2009-09-03 21:56:33.994	2009-09-03 21:56:33.994	4
1406	keyword.diodes	الثنائيات	2009-09-03 21:56:35.661	2009-09-03 21:56:35.661	4
1407	keyword.dipole	ثنائي القطب	2009-09-03 21:56:38.066	2009-09-03 21:56:38.066	4
1408	keyword.division	التقسيم	2009-09-03 21:56:38.857	2009-09-03 21:56:38.857	4
1409	keyword.doubleSlit	فتحة مزدوجة	2009-09-03 21:56:40.163	2009-09-03 21:56:40.163	4
1410	keyword.dynamicEquilibrium	التوازن الديناميكي	2009-09-03 21:56:41.004	2009-09-03 21:56:41.004	4
1411	keyword.dynamics	ديناميات	2009-09-03 21:56:43.179	2009-09-03 21:56:43.179	4
1412	keyword.eating	الأكل	2009-09-03 21:56:44.07	2009-09-03 21:56:44.07	4
1413	keyword.electricField	حقل كهربائي	2009-09-03 21:56:44.86	2009-09-03 21:56:44.86	4
1414	keyword.electricForce	القوة الكهربائية	2009-09-03 21:56:45.956	2009-09-03 21:56:45.956	4
1415	keyword.electricPotential	الجهد الكهربائي	2009-09-03 21:56:47.363	2009-09-03 21:56:47.363	4
1416	keyword.electricity	الكهرباء	2009-09-03 21:56:48.649	2009-09-03 21:56:48.649	4
1417	keyword.electromagnet	الكهربائية	2009-09-03 21:56:50.551	2009-09-03 21:56:50.551	4
1418	keyword.electronDiffraction	الإلكترون الإنحراف	2009-09-03 21:56:51.468	2009-09-03 21:56:51.468	4
1419	keyword.electrons	الالكترونات	2009-09-03 21:56:52.341	2009-09-03 21:56:52.341	4
1420	keyword.electrostatics	الكهربائية الساكنة	2009-09-03 21:56:53.387	2009-09-03 21:56:53.387	4
1421	keyword.ellipticalMotion	اهليلجي موشن	2009-09-03 21:56:56.045	2009-09-03 21:56:56.045	4
1422	keyword.emission	الانبعاثات	2009-09-03 21:56:56.88	2009-09-03 21:56:56.88	4
1423	keyword.energy	الطاقة	2009-09-03 21:56:57.932	2009-09-03 21:56:57.932	4
1424	keyword.energyBand	الطاقة والانظمة	2009-09-03 21:56:58.924	2009-09-03 21:56:58.924	4
1554	keyword.schrodingerEquation	معادلة شرودنغر	2009-09-03 22:00:47.677	2009-09-03 22:00:47.677	4
1555	keyword.schrodingerModel	شرودنجر النموذجي	2009-09-03 22:00:49.105	2009-09-03 22:00:49.105	4
1556	keyword.semiconductors	أشباه الموصلات	2009-09-03 22:00:49.899	2009-09-03 22:00:49.899	4
1557	keyword.seriesCircuit	سلسلة حلبة	2009-09-03 22:00:50.812	2009-09-03 22:00:50.812	4
1558	keyword.signalCircuits	إشارة الدوائر	2009-09-03 22:00:52.92	2009-09-03 22:00:52.92	4
1559	keyword.simpleHarmonicMotion	الحركة التوافقية البسيطة	2009-09-03 22:00:53.839	2009-09-03 22:00:53.839	4
1560	keyword.simpleHarmonicOscillator	بسيطة متناسق المذبذب	2009-09-03 22:00:54.692	2009-09-03 22:00:54.692	4
1561	keyword.sines	الجيوب	2009-09-03 22:00:55.563	2009-09-03 22:00:55.563	4
1562	keyword.solidState	الصلبة الدولة	2009-09-03 22:00:56.549	2009-09-03 22:00:56.549	4
1563	keyword.solubility	الذوبان	2009-09-03 22:00:57.356	2009-09-03 22:00:57.356	4
1564	keyword.solubilityProduct	ذوبان المنتج	2009-09-03 22:00:59.159	2009-09-03 22:00:59.159	4
1565	keyword.solutions	الحلول	2009-09-03 22:01:00.194	2009-09-03 22:01:00.194	4
1566	keyword.sound	الصوت	2009-09-03 22:01:01.095	2009-09-03 22:01:01.095	4
1567	keyword.speakers	المتحدثون	2009-09-03 22:01:02.227	2009-09-03 22:01:02.227	4
1568	keyword.spectrum	الطيف	2009-09-03 22:01:03.163	2009-09-03 22:01:03.163	4
1569	keyword.spin	تدور	2009-09-03 22:01:04.116	2009-09-03 22:01:04.116	4
1570	keyword.spinHalfParticles	تدور 1 / 2 الجسيمات	2009-09-03 22:01:06.081	2009-09-03 22:01:06.081	4
1571	keyword.splitting	تقسيم	2009-09-03 22:01:06.886	2009-09-03 22:01:06.886	4
1572	keyword.spontaneousEmission	الانبعاث العفوي	2009-09-03 22:01:07.705	2009-09-03 22:01:07.705	4
1573	keyword.springConstant	ربيع ثابت	2009-09-03 22:01:09.127	2009-09-03 22:01:09.127	4
1574	keyword.springs	الينابيع	2009-09-03 22:01:14.519	2009-09-03 22:01:14.519	4
1575	keyword.staticElectricity	الكهرباء الساكنة	2009-09-03 22:01:15.769	2009-09-03 22:01:15.769	4
1576	keyword.statistics	إحصاءات	2009-09-03 22:01:16.78	2009-09-03 22:01:16.78	4
1577	keyword.stimulatedEmission	انبعاث محفز	2009-09-03 22:01:18.203	2009-09-03 22:01:18.203	4
1578	keyword.stoppingPotential	وقف المحتملة	2009-09-03 22:01:19.11	2009-09-03 22:01:19.11	4
1579	keyword.sun	شمس	2009-09-03 22:01:20.989	2009-09-03 22:01:20.989	4
1580	keyword.superposition	تراكب	2009-09-03 22:01:22.005	2009-09-03 22:01:22.005	4
1581	keyword.switches	التبديل	2009-09-03 22:01:23.01	2009-09-03 22:01:23.01	4
1582	keyword.temperature	درجة الحرارة	2009-09-03 22:01:23.901	2009-09-03 22:01:23.901	4
1583	keyword.thermalEnergy	الطاقة الحرارية	2009-09-03 22:01:25.248	2009-09-03 22:01:25.248	4
1584	keyword.thermalEquilibrium	الاتزان الحراري	2009-09-03 22:01:27.275	2009-09-03 22:01:27.275	4
1585	keyword.thermodynamics	الديناميكا الحرارية	2009-09-03 22:01:28.294	2009-09-03 22:01:28.294	4
1586	keyword.thrust	الفحوى	2009-09-03 22:01:29.189	2009-09-03 22:01:29.189	4
1587	keyword.torque	عزم الدوران	2009-09-03 22:01:30.071	2009-09-03 22:01:30.071	4
1588	keyword.transformer	المحولات	2009-09-03 22:01:31.856	2009-09-03 22:01:31.856	4
1589	keyword.transistors	الترانزستورات	2009-09-03 22:01:32.554	2009-09-03 22:01:32.554	4
1590	keyword.transverseWaves	مستعرض يفز	2009-09-03 22:01:33.658	2009-09-03 22:01:33.658	4
1591	keyword.tunneling	نفق	2009-09-03 22:01:34.446	2009-09-03 22:01:34.446	4
1592	keyword.turbine	التوربينات	2009-09-03 22:01:36.336	2009-09-03 22:01:36.336	4
1593	keyword.uncertaintyPrinciple	مبدأ عدم اليقين	2009-09-03 22:01:37.062	2009-09-03 22:01:37.062	4
1594	keyword.vectorAddition	وبالإضافة إلى ناقل	2009-09-03 22:01:37.861	2009-09-03 22:01:37.861	4
1595	keyword.vectorComponents	ناقلات مكونات	2009-09-03 22:01:38.598	2009-09-03 22:01:38.598	4
1596	keyword.vectors	نواقل	2009-09-03 22:01:39.46	2009-09-03 22:01:39.46	4
1597	keyword.velocity	السرعة	2009-09-03 22:01:40.279	2009-09-03 22:01:40.279	4
1598	keyword.vision	الرؤية	2009-09-03 22:01:42.38	2009-09-03 22:01:42.38	4
1599	keyword.voltage	الجهد	2009-09-03 22:01:43.057	2009-09-03 22:01:43.057	4
1600	keyword.voltmeter	الفولتميتر	2009-09-03 22:01:44.035	2009-09-03 22:01:44.035	4
1601	keyword.volume	حجم	2009-09-03 22:01:44.724	2009-09-03 22:01:44.724	4
1602	keyword.waveFunction	موجة الدالة	2009-09-03 22:01:45.564	2009-09-03 22:01:45.564	4
1603	keyword.wavePackets	موجة حزم	2009-09-03 22:01:46.689	2009-09-03 22:01:46.689	4
1604	keyword.waveParticleDuality	الموجة الجسيمات ازدواجية	2009-09-03 22:01:48.846	2009-09-03 22:01:48.846	4
1605	keyword.wavePropagation	الموجات	2009-09-03 22:01:49.502	2009-09-03 22:01:49.502	4
1606	keyword.waveSpeed	سرعة الموجة	2009-09-03 22:01:50.319	2009-09-03 22:01:50.319	4
1607	keyword.wavelength	الطول الموجي	2009-09-03 22:01:51.166	2009-09-03 22:01:51.166	4
1608	keyword.waves	موجات	2009-09-03 22:01:52.881	2009-09-03 22:01:52.881	4
1609	keyword.weight	وزن	2009-09-03 22:01:53.759	2009-09-03 22:01:53.759	4
1610	keyword.whiteLight	الأبيض الخفيف	2009-09-03 22:01:54.65	2009-09-03 22:01:54.65	4
1611	keyword.work	عمل	2009-09-03 22:01:56.251	2009-09-03 22:01:56.251	4
1612	keyword.workFunction	العمل الدالة	2009-09-03 22:01:58.454	2009-09-03 22:01:58.454	4
1613	language.names.cs_CZ	التشيكية (الجمهورية التشيكية)	2009-09-03 22:02:09.261	2009-09-03 22:02:09.261	4
1614	language.names.da_DK	الدانماركية (الدنمارك)	2009-09-03 22:02:10.534	2009-09-03 22:02:10.534	4
1615	language.names.de_AT	الألمانية (النمسا)	2009-09-03 22:02:11.746	2009-09-03 22:02:11.746	4
1616	language.names.de_CH	الألمانية (سويسرا)	2009-09-03 22:02:12.835	2009-09-03 22:02:12.835	4
1617	language.names.de_DE	الألمانية (ألمانيا)	2009-09-03 22:02:14.11	2009-09-03 22:02:14.11	4
1618	language.names.de_LU	الألمانية (لوكسمبورغ)	2009-09-03 22:02:15.293	2009-09-03 22:02:15.293	4
1619	language.names.el_CY	اليونانية (قبرص)	2009-09-03 22:02:17.476	2009-09-03 22:02:17.476	4
1620	language.names.el_GR	اليونانية (اليونان)	2009-09-03 22:02:18.386	2009-09-03 22:02:18.386	4
1621	language.names.en_AU	الانجليزية (استراليا)	2009-09-03 22:02:19.482	2009-09-03 22:02:19.482	4
1622	language.names.en_CA	الانجليزية (كندا)	2009-09-03 22:02:20.632	2009-09-03 22:02:20.632	4
1623	language.names.en_GB	الانجليزية (المملكة المتحدة)	2009-09-03 22:02:23.302	2009-09-03 22:02:23.302	4
1624	language.names.en_IE	الانجليزية (ايرلندا)	2009-09-03 22:02:24.111	2009-09-03 22:02:24.111	4
1625	language.names.en_IN	الانجليزية (الهند)	2009-09-03 22:02:24.966	2009-09-03 22:02:24.966	4
1626	language.names.en_MT	الانجليزية (مالطا)	2009-09-03 22:02:25.747	2009-09-03 22:02:25.747	4
1627	language.names.en_NZ	الانجليزية (نيوزيلندا)	2009-09-03 22:02:27.46	2009-09-03 22:02:27.46	4
1628	language.names.en_PH	الانجليزية (الفلبين)	2009-09-03 22:02:28.199	2009-09-03 22:02:28.199	4
1629	language.names.en_SG	الانجليزية (سنغافورة)	2009-09-03 22:02:29.183	2009-09-03 22:02:29.183	4
1630	language.names.en_US	الإنكليزية (الولايات المتحدة)	2009-09-03 22:02:30.072	2009-09-03 22:02:30.072	4
1631	language.names.en_ZA	الانجليزية (جنوب أفريقيا)	2009-09-03 22:02:31.283	2009-09-03 22:02:31.283	4
1632	language.names.es_AR	الأسبانية (الأرجنتين)	2009-09-03 22:02:33.319	2009-09-03 22:02:33.319	4
1633	language.names.es_BO	الأسبانية (بوليفيا)	2009-09-03 22:02:34.093	2009-09-03 22:02:34.093	4
1634	language.names.es_CL	الأسبانية (تشيلي)	2009-09-03 22:02:35.003	2009-09-03 22:02:35.003	4
1635	language.names.es_CO	الأسبانية (كولومبيا)	2009-09-03 22:02:36.012	2009-09-03 22:02:36.012	4
1636	language.names.es_CR	الأسبانية (كوستاريكا)	2009-09-03 22:02:38.168	2009-09-03 22:02:38.168	4
1637	language.names.es_DO	الأسبانية (جمهورية الدومينيكان)	2009-09-03 22:02:38.978	2009-09-03 22:02:38.978	4
1638	language.names.es_EC	الأسبانية (الاكوادور)	2009-09-03 22:02:39.815	2009-09-03 22:02:39.815	4
1639	language.names.es_ES	الإسبانية (إسبانيا)	2009-09-03 22:02:40.87	2009-09-03 22:02:40.87	4
1640	language.names.es_GT	الأسبانية (غواتيمالا)	2009-09-03 22:02:41.714	2009-09-03 22:02:41.714	4
1641	language.names.es_HN	الأسبانية (هندوراس)	2009-09-03 22:02:42.867	2009-09-03 22:02:42.867	4
1642	language.names.es_MX	الأسبانية (المكسيك)	2009-09-03 22:02:44.094	2009-09-03 22:02:44.094	4
1643	language.names.es_NI	الأسبانية (نيكاراغوا)	2009-09-03 22:02:46.02	2009-09-03 22:02:46.02	4
1644	language.names.es_PA	الأسبانية (بنما)	2009-09-03 22:02:46.828	2009-09-03 22:02:46.828	4
1645	language.names.es_PE	الأسبانية (بيرو)	2009-09-03 22:02:47.681	2009-09-03 22:02:47.681	4
1646	language.names.es_PR	الأسبانية (بورتو ريكو)	2009-09-03 22:02:48.655	2009-09-03 22:02:48.655	4
1647	language.names.es_PY	الأسبانية (باراغواي)	2009-09-03 22:02:49.942	2009-09-03 22:02:49.942	4
1648	language.names.es_SV	الأسبانية (السلفادور)	2009-09-03 22:02:51.755	2009-09-03 22:02:51.755	4
1649	language.names.es_US	الأسبانية (الولايات المتحدة)	2009-09-03 22:02:52.625	2009-09-03 22:02:52.625	4
1650	language.names.es_UY	الأسبانية (أوروغواي)	2009-09-03 22:02:53.575	2009-09-03 22:02:53.575	4
1651	language.names.es_VE	الأسبانية (فنزويلا)	2009-09-03 22:02:54.702	2009-09-03 22:02:54.702	4
1652	language.names.et_EE	الأستونية (استونيا)	2009-09-03 22:02:57.022	2009-09-03 22:02:57.022	4
1653	language.names.fi_FI	الفنلندية (فنلندا)	2009-09-03 22:02:57.841	2009-09-03 22:02:57.841	4
1654	language.names.fr_BE	الفرنسية (بلجيكا)	2009-09-03 22:02:58.775	2009-09-03 22:02:58.775	4
1655	language.names.fr_CA	الفرنسية (كندا)	2009-09-03 22:02:59.584	2009-09-03 22:02:59.584	4
1656	language.names.fr_CH	الفرنسية (سويسرا)	2009-09-03 22:03:01.518	2009-09-03 22:03:01.518	4
1657	language.names.fr_FR	الفرنسية (فرنسا)	2009-09-03 22:03:02.313	2009-09-03 22:03:02.313	4
1658	language.names.fr_LU	الفرنسية (لوكسمبورغ)	2009-09-03 22:03:03.215	2009-09-03 22:03:03.215	4
1659	language.names.ga_IE	الأيرلندية (أيرلندا)	2009-09-03 22:03:37.917	2009-09-03 22:03:37.917	4
1660	language.names.hr_HR	الكرواتية (كرواتيا)	2009-09-03 22:03:41.25	2009-09-03 22:03:41.25	4
1661	language.names.hu_HU	الهنغارية (المجر)	2009-09-03 22:03:42.818	2009-09-03 22:03:42.818	4
1662	language.names.in_ID	الأندونيسية (اندونيسيا)	2009-09-03 22:03:44.378	2009-09-03 22:03:44.378	4
1663	language.names.is_IS	الأيسلاندية (ايسلندا)	2009-09-03 22:03:46.553	2009-09-03 22:03:46.553	4
1664	language.names.ja_JP	اليابانية (اليابان)	2009-09-03 22:03:48.836	2009-09-03 22:03:48.836	4
1665	language.names.ja_JP_JP	اليابانية (اليابان ، وجيه بي)	2009-09-03 22:03:50.379	2009-09-03 22:03:50.379	4
1666	language.names.ko_KR	الكورية (كوريا الجنوبية)	2009-09-03 22:03:51.756	2009-09-03 22:03:51.756	4
1667	language.names.lt_LT	اللتوانية (ليتوانيا)	2009-09-03 22:03:52.917	2009-09-03 22:03:52.917	4
1668	language.names.lv	اللاتفية	2009-09-03 22:03:55.703	2009-09-03 22:03:55.703	4
1669	language.names.lv_LV	اللاتفية (لاتفيا)	2009-09-03 22:03:56.731	2009-09-03 22:03:56.731	4
1670	language.names.mk_MK	المقدونية (مقدونيا)	2009-09-03 22:03:57.892	2009-09-03 22:03:57.892	4
1671	language.names.ms	لغة الملايو	2009-09-03 22:03:59.169	2009-09-03 22:03:59.169	4
1672	language.names.ms_MY	لغة الملايو (ماليزيا)	2009-09-03 22:04:00.347	2009-09-03 22:04:00.347	4
1673	language.names.mt_MT	المالطية (مالطا)	2009-09-03 22:04:02.513	2009-09-03 22:04:02.513	4
1674	language.names.nl_BE	الهولندية (بلجيكا)	2009-09-03 22:04:03.523	2009-09-03 22:04:03.523	4
1675	language.names.nl_NL	الهولندية (هولندا)	2009-09-03 22:04:04.641	2009-09-03 22:04:04.641	4
1676	language.names.no_NO	النرويجية (النرويج)	2009-09-03 22:04:06.39	2009-09-03 22:04:06.39	4
1677	language.names.no_NO_NY	النرويجية (النرويج ، نينورسك)	2009-09-03 22:04:07.344	2009-09-03 22:04:07.344	4
1678	language.names.pl_PL	البولندية (بولندا)	2009-09-03 22:04:09.281	2009-09-03 22:04:09.281	4
1279	language.names.pt_BR	البرتغالية البرازيلية	2009-09-03 19:27:20.896	2009-09-03 22:04:15.427	4
1679	language.names.pt_PT	البرتغالية (البرتغال)	2009-09-03 22:04:17.128	2009-09-03 22:04:17.128	4
1680	language.names.ro_RO	الرومانية (رومانيا)	2009-09-03 22:04:18.972	2009-09-03 22:04:18.972	4
1681	language.names.ru_RU	الروسية (روسيا)	2009-09-03 22:04:20.373	2009-09-03 22:04:20.373	4
1682	language.names.sk_SK	السلوفاكية (سلوفاكيا)	2009-09-03 22:04:22.218	2009-09-03 22:04:22.218	4
1683	language.names.sl_SI	السلوفانية (سلوفينيا)	2009-09-03 22:04:23.158	2009-09-03 22:04:23.158	4
1684	language.names.sq_AL	الألبانية (ألبانيا)	2009-09-03 22:04:25.465	2009-09-03 22:04:25.465	4
1685	language.names.sr_BA	الصربية (البوسنة والهرسك)	2009-09-03 22:04:26.467	2009-09-03 22:04:26.467	4
1686	language.names.sr_CS	الصربية (صربيا والجبل الأسود)	2009-09-03 22:04:27.869	2009-09-03 22:04:27.869	4
1687	language.names.sv_SE	السويدية (السويد)	2009-09-03 22:04:29.16	2009-09-03 22:04:29.16	4
1688	language.names.th_TH	التايلاندية (تايلاند)	2009-09-03 22:04:31.025	2009-09-03 22:04:31.025	4
1689	language.names.th_TH_TH	التايلاندية (تايلاند ، ث)	2009-09-03 22:04:33.464	2009-09-03 22:04:33.464	4
1690	language.names.tr_TR	التركية (تركيا)	2009-09-03 22:04:35.597	2009-09-03 22:04:35.597	4
1691	language.names.uk_UA	الأوكرانية (أوكرانيا)	2009-09-03 22:04:37.264	2009-09-03 22:04:37.264	4
1692	language.names.vi_VN	الفيتنامية (فيتنام)	2009-09-03 22:04:39.123	2009-09-03 22:04:39.123	4
783	troubleshooting.java.q6.answer	@troubleshooting.java.q6.answer@	2009-09-01 01:10:15.3	2009-09-01 01:10:15.3	3
784	troubleshooting.java.q7.title	@troubleshooting.java.q7.title@	2009-09-01 01:10:17.074	2009-09-01 01:10:17.074	3
785	troubleshooting.java.q7.answer	@troubleshooting.java.q7.answer@	2009-09-01 01:10:17.934	2009-09-01 01:10:17.934	3
786	troubleshooting.java.q8.title	@troubleshooting.java.q8.title@	2009-09-01 01:10:19.923	2009-09-01 01:10:19.923	3
643	keyword.gravity	@keyword.gravity@	2009-08-31 14:55:27.287	2009-08-31 14:55:27.287	3
587	keyword.conservationOfEnergy	@keyword.conservationOfEnergy@	2009-08-31 14:41:46.427	2009-08-31 14:41:46.427	3
588	keyword.conductors	@keyword.conductors@	2009-08-31 14:41:50.172	2009-08-31 14:41:50.172	3
589	keyword.correlation	@keyword.correlation@	2009-08-31 14:41:55.189	2009-08-31 14:41:55.189	3
590	keyword.coulombsLaw	@keyword.coulombsLaw@	2009-08-31 14:41:58.712	2009-08-31 14:41:58.712	3
591	keyword.covalentBonds	@keyword.covalentBonds@	2009-08-31 14:42:02.652	2009-08-31 14:42:02.652	3
592	keyword.criticalExponent	@keyword.criticalExponent@	2009-08-31 14:42:07.597	2009-08-31 14:42:07.597	3
593	keyword.criticalParameter	@keyword.criticalParameter@	2009-08-31 14:42:11.248	2009-08-31 14:42:11.248	3
594	keyword.current	@keyword.current@	2009-08-31 14:42:15.014	2009-08-31 14:42:15.014	3
595	keyword.curves	@keyword.curves@	2009-08-31 14:42:18.198	2009-08-31 14:42:18.198	3
1693	contribute.main	فلسفتنا هو جعل المحاكاة PhET متاحة مجانا لجميع المستخدمين في جميع أنحاء العالم. لديهم الآن تم تشغيل الملايين من المرات من موقعنا على الشبكة العالمية ، وكامل PhET جناح وقد تم تثبيتها على الالاف من اجهزة الكمبيوتر. لكن في حين أن المحاكاة قد تكون مجانية للمستخدمين ، فهي غالية الثمن بالنسبة لنا لإنشاء واختبار وصيانة ، ودعمنا المالي محدود. إذا كنت ترغب في مساعدة تجعل من الممكن بالنسبة لنا للمزيد من تطوير وتحسين عمليات المحاكاة ، يرجى الاتصال {0} أو الهاتف (303-492-4367) لمعرفة كيف يمكن أن تقدم مساهمة معفاة من الضرائب.	2009-09-03 22:08:45.92	2009-09-03 22:08:58.654	4
1694	troubleshooting.javascript.q2.answer	جافا سكريبت يتم استخدامه مع الكثير من الميزات الموجودة على الموقع ، بما في ذلك إجراءات الترشيح ، والسماح للمدخلات متعددة من البيانات. Howevery ، العديد من الصفحات وسوف تعمل بشكل جيد بدون جافا سكريبت.	2009-09-03 22:09:11.347	2009-09-03 22:09:11.347	4
532	troubleshooting.main.q13.title	@troubleshooting.main.q13.title@	2009-08-31 14:19:47.361	2009-08-31 14:19:47.361	3
1053	footer.someRightsReserved	@footer.someRightsReserved@	2009-09-03 13:20:28.773	2009-09-03 13:20:28.773	3
533	troubleshooting.main.q13.answer	@troubleshooting.main.q13.answer@	2009-08-31 14:19:49.07	2009-08-31 14:19:49.07	3
534	troubleshooting.main.licensingRequirements	@troubleshooting.main.licensingRequirements@	2009-08-31 14:19:51.99	2009-08-31 14:19:51.99	3
527	troubleshooting.main.q10.answer	@troubleshooting.main.q10.answer@	2009-08-31 14:19:34.268	2009-08-31 14:20:10.145	3
537	keyword.acCircuits	@keyword.acCircuits@	2009-08-31 14:22:54.374	2009-08-31 14:22:54.374	3
538	keyword.acceleration	@keyword.acceleration@	2009-08-31 14:22:56.987	2009-08-31 14:22:56.987	3
539	keyword.activationEnergy	@keyword.activationEnergy@	2009-08-31 14:23:00.078	2009-08-31 14:23:00.078	3
540	keyword.airResistance	@keyword.airResistance@	2009-08-31 14:23:03.131	2009-08-31 14:23:03.131	3
541	keyword.algebra	@keyword.algebra@	2009-08-31 14:23:05.421	2009-08-31 14:23:05.421	3
674	keyword.linearMotion	@keyword.linearMotion@	2009-08-31 14:57:30.825	2009-08-31 14:57:30.825	3
675	keyword.longitudinalWaves	@keyword.longitudinalWaves@	2009-08-31 14:57:34.349	2009-08-31 14:57:34.349	3
676	keyword.magneticField	@keyword.magneticField@	2009-08-31 14:57:38.937	2009-08-31 14:57:38.937	3
677	keyword.magneticMoment	@keyword.magneticMoment@	2009-08-31 14:57:42.765	2009-08-31 14:57:42.765	3
678	keyword.magnetism	@keyword.magnetism@	2009-08-31 14:57:45.893	2009-08-31 14:57:45.893	3
679	keyword.magnets	@keyword.magnets@	2009-08-31 14:57:50	2009-08-31 14:57:50	3
680	about.p1	@about.p1@	2009-08-31 14:57:56.527	2009-08-31 14:57:56.527	3
681	about.p2	@about.p2@	2009-08-31 14:57:58.798	2009-08-31 14:57:58.798	3
682	about.p3	@about.p3@	2009-08-31 14:58:01.086	2009-08-31 14:58:01.086	3
683	about.p4	@about.p4@	2009-08-31 14:58:03.941	2009-08-31 14:58:03.941	3
684	home.title	@home.title@	2009-08-31 14:58:08.464	2009-08-31 14:58:08.464	3
685	simulationPage.title	@simulationPage.title@	2009-08-31 14:58:12.027	2009-08-31 14:58:12.027	3
686	simulationDisplay.title	@simulationDisplay.title@	2009-08-31 14:58:14.465	2009-08-31 14:58:14.465	3
687	about.title	@about.title@	2009-08-31 14:58:16.919	2009-08-31 14:58:16.919	3
688	about.source-code.title	@about.source-code.title@	2009-08-31 14:58:19.166	2009-08-31 14:58:19.166	3
689	about.contact.title	@about.contact.title@	2009-08-31 14:58:22.712	2009-08-31 14:58:22.712	3
690	about.who-we-are.title	@about.who-we-are.title@	2009-08-31 14:58:24.724	2009-08-31 14:58:24.724	3
691	about.licensing.title	@about.licensing.title@	2009-08-31 14:58:27.206	2009-08-31 14:58:27.206	3
692	workshops.title	@workshops.title@	2009-08-31 14:58:29.565	2009-08-31 14:58:29.565	3
694	get-phet.title	@get-phet.title@	2009-08-31 14:58:34.294	2009-08-31 14:58:34.294	3
542	keyword.alternatingCurrent	@keyword.alternatingCurrent@	2009-08-31 14:23:08.542	2009-08-31 14:23:08.542	3
543	keyword.ammeter	@keyword.ammeter@	2009-08-31 14:39:05.771	2009-08-31 14:39:05.771	3
1063	simulationMainPanel.softwareRequirements	@simulationMainPanel.softwareRequirements@	2009-09-03 16:14:57.512	2009-09-03 16:14:57.512	3
1064	simulationMainPanel.sampleLearningGoals	@simulationMainPanel.sampleLearningGoals@	2009-09-03 16:15:00.102	2009-09-03 16:15:00.102	3
1065	simulationMainPanel.credits	@simulationMainPanel.credits@	2009-09-03 16:15:02.092	2009-09-03 16:15:02.092	3
740	sponsors.title	@sponsors.title@	2009-08-31 23:40:21.203	2009-08-31 23:40:21.203	3
1066	simulationMainPanel.language	@simulationMainPanel.language@	2009-09-03 16:15:03.865	2009-09-03 16:15:03.865	3
1067	simulationMainPanel.languageTranslated	@simulationMainPanel.languageTranslated@	2009-09-03 16:15:05.543	2009-09-03 16:15:05.543	3
812	keyword.monochromatic	@keyword.monochromatic@	2009-09-01 01:12:31.227	2009-09-01 01:12:31.227	3
1068	simulationMainPanel.simulationTitleTranslated	@simulationMainPanel.simulationTitleTranslated@	2009-09-03 16:15:07.58	2009-09-03 16:15:07.58	3
1069	simulationMainPanel.designTeam	@simulationMainPanel.designTeam@	2009-09-03 16:15:09.599	2009-09-03 16:15:09.599	3
1070	simulationMainPanel.thirdPartyLibraries	@simulationMainPanel.thirdPartyLibraries@	2009-09-03 16:15:12.426	2009-09-03 16:15:12.426	3
1071	simulationMainPanel.thanksTo	@simulationMainPanel.thanksTo@	2009-09-03 16:15:14.909	2009-09-03 16:15:14.909	3
1072	contribute.financialContributions	@contribute.financialContributions@	2009-09-03 16:15:20.506	2009-09-03 16:15:20.506	3
1073	contribute.main	@contribute.main@	2009-09-03 16:15:23.119	2009-09-03 16:15:23.119	3
1074	contribute.thanks	@contribute.thanks@	2009-09-03 16:15:24.953	2009-09-03 16:15:24.953	3
1075	keyword.photonBeams	@keyword.photonBeams@	2009-09-03 16:15:40.623	2009-09-03 16:15:40.623	3
470	nav.math	@nav.math@	2009-08-31 14:15:29.188	2009-08-31 14:15:29.188	3
781	troubleshooting.java.q5.answer	@troubleshooting.java.q5.answer@	2009-09-01 01:10:12.896	2009-09-01 01:10:12.896	3
782	troubleshooting.java.q6.title	@troubleshooting.java.q6.title@	2009-09-01 01:10:14.472	2009-09-01 01:10:14.472	3
1695	macwarning.title	@macwarning.title@	2009-09-03 22:29:32.794	2009-09-03 22:29:32.794	3
1696	macwarning.problem	@macwarning.problem@	2009-09-03 22:29:35.976	2009-09-03 22:29:35.976	3
1697	macwarning.solution	@macwarning.solution@	2009-09-03 22:29:38.111	2009-09-03 22:29:38.111	3
596	keyword.deBroglieWavelength	@keyword.deBroglieWavelength@	2009-08-31 14:42:21.586	2009-08-31 14:42:21.586	3
597	keyword.diet	@keyword.diet@	2009-08-31 14:42:24.981	2009-08-31 14:42:24.981	3
598	keyword.diffraction	@keyword.diffraction@	2009-08-31 14:42:28.55	2009-08-31 14:42:28.55	3
599	keyword.dilution	@keyword.dilution@	2009-08-31 14:42:32.288	2009-08-31 14:42:32.288	3
600	keyword.diodes	@keyword.diodes@	2009-08-31 14:42:35.933	2009-08-31 14:42:39.272	3
601	keyword.dipole	@keyword.dipole@	2009-08-31 14:42:44.054	2009-08-31 14:42:44.054	3
602	keyword.division	@keyword.division@	2009-08-31 14:42:47.168	2009-08-31 14:42:47.168	3
603	keyword.dna	@keyword.dna@	2009-08-31 14:42:50.778	2009-08-31 14:42:50.778	3
604	keyword.doping	@keyword.doping@	2009-08-31 14:42:54.497	2009-08-31 14:42:54.497	3
605	keyword.doubleSlit	@keyword.doubleSlit@	2009-08-31 14:42:58.459	2009-08-31 14:42:58.459	3
851	troubleshooting.flash.intro	@troubleshooting.flash.intro@	2009-09-01 02:42:05.095	2009-09-01 02:42:05.095	3
852	troubleshooting.flash.toRun	@troubleshooting.flash.toRun@	2009-09-01 02:42:06.187	2009-09-01 02:42:06.187	3
853	troubleshooting.flash.blankWindow	@troubleshooting.flash.blankWindow@	2009-09-01 02:42:07.514	2009-09-01 02:42:07.514	3
854	troubleshooting.flash.olderVersions	@troubleshooting.flash.olderVersions@	2009-09-01 02:42:08.722	2009-09-01 02:42:08.722	3
855	troubleshooting.javascript.intro	@troubleshooting.javascript.intro@	2009-09-01 02:42:11.018	2009-09-01 02:42:11.018	3
856	troubleshooting.javascript.notJava	@troubleshooting.javascript.notJava@	2009-09-01 02:42:12.327	2009-09-01 02:42:12.327	3
830	troubleshooting.java.q3.title	@troubleshooting.java.q3.title@	2009-09-01 01:18:02.665	2009-09-01 01:18:11.914	3
831	troubleshooting.java.q3.answer	@troubleshooting.java.q3.answer@	2009-09-01 01:18:21.073	2009-09-01 01:18:21.073	3
857	troubleshooting.javascript.notify	@troubleshooting.javascript.notify@	2009-09-01 02:42:13.713	2009-09-01 02:42:13.713	3
858	troubleshooting.javascript.faqs	@troubleshooting.javascript.faqs@	2009-09-01 02:42:15.736	2009-09-01 02:42:15.736	3
859	troubleshooting.javascript.q1.title	@troubleshooting.javascript.q1.title@	2009-09-01 02:42:16.785	2009-09-01 02:42:16.785	3
860	troubleshooting.javascript.q1.yes	@troubleshooting.javascript.q1.yes@	2009-09-01 02:42:17.865	2009-09-01 02:42:17.865	3
861	troubleshooting.javascript.q1.no	@troubleshooting.javascript.q1.no@	2009-09-01 02:42:19.681	2009-09-01 02:42:19.681	3
862	troubleshooting.javascript.q2.title	@troubleshooting.javascript.q2.title@	2009-09-01 02:42:21.569	2009-09-01 02:42:21.569	3
863	troubleshooting.javascript.q2.answer	@troubleshooting.javascript.q2.answer@	2009-09-01 02:42:22.811	2009-09-01 02:42:22.811	3
864	troubleshooting.javascript.q3.title	@troubleshooting.javascript.q3.title@	2009-09-01 02:42:24.73	2009-09-01 02:42:24.73	3
865	troubleshooting.javascript.q3.answer	@troubleshooting.javascript.q3.answer@	2009-09-01 02:42:26.104	2009-09-01 02:42:26.104	3
866	troubleshooting.javascript.q4.title	@troubleshooting.javascript.q4.title@	2009-09-01 02:42:27.907	2009-09-01 02:42:27.907	3
867	troubleshooting.javascript.q4.answer	@troubleshooting.javascript.q4.answer@	2009-09-01 02:42:30.467	2009-09-01 02:42:30.467	3
868	troubleshooting.javascript.q5.title	@troubleshooting.javascript.q5.title@	2009-09-01 02:42:35.372	2009-09-01 02:42:35.372	3
869	troubleshooting.javascript.q5.answer	@troubleshooting.javascript.q5.answer@	2009-09-01 02:42:37.41	2009-09-01 02:42:37.41	3
811	keyword.momentOfInertia	@keyword.momentOfInertia@	2009-09-01 01:12:27.733	2009-09-01 01:12:27.733	3
606	keyword.dynamicEquilibrium	@keyword.dynamicEquilibrium@	2009-08-31 14:48:53.315	2009-08-31 14:48:53.315	3
607	keyword.dynamics	@keyword.dynamics@	2009-08-31 14:48:56.342	2009-08-31 14:48:56.342	3
608	keyword.eating	@keyword.eating@	2009-08-31 14:48:59.702	2009-08-31 14:48:59.702	3
609	keyword.electricField	@keyword.electricField@	2009-08-31 14:49:03.536	2009-08-31 14:49:03.536	3
610	keyword.electricForce	@keyword.electricForce@	2009-08-31 14:49:06.463	2009-08-31 14:49:06.463	3
611	keyword.electricPotential	@keyword.electricPotential@	2009-08-31 14:49:09.482	2009-08-31 14:49:09.482	3
612	keyword.electricity	@keyword.electricity@	2009-08-31 14:49:12.517	2009-08-31 14:49:12.517	3
614	keyword.electronDiffraction	@keyword.electronDiffraction@	2009-08-31 14:49:19.409	2009-08-31 14:49:19.409	3
615	keyword.electrons	@keyword.electrons@	2009-08-31 14:49:23.325	2009-08-31 14:49:23.325	3
616	keyword.electrostatics	@keyword.electrostatics@	2009-08-31 14:49:26.336	2009-08-31 14:49:26.336	3
617	keyword.ellipticalMotion	@keyword.ellipticalMotion@	2009-08-31 14:49:29.661	2009-08-31 14:49:29.661	3
618	keyword.emission	@keyword.emission@	2009-08-31 14:49:32.379	2009-08-31 14:49:32.379	3
619	keyword.energy	@keyword.energy@	2009-08-31 14:49:35.521	2009-08-31 14:49:35.521	3
704	about.source-code.header	@about.source-code.header@	2009-08-31 16:37:48.847	2009-08-31 18:20:19.651	3
585	keyword.conductivity	@keyword.conductivity@	2009-08-31 14:41:38.736	2009-08-31 14:41:38.736	3
706	about.source-code.flash-simulations	@about.source-code.flash-simulations@	2009-08-31 16:37:58.454	2009-09-01 01:18:39.255	3
441	home.header	@home.header@	2009-08-31 14:13:34.21	2009-08-31 14:13:34.21	3
620	keyword.energyBand	@keyword.energyBand@	2009-08-31 14:49:38.456	2009-08-31 14:49:38.456	3
621	keyword.energyGap	@keyword.energyGap@	2009-08-31 14:49:41.539	2009-08-31 14:49:41.539	3
622	keyword.energyLevels	@keyword.energyLevels@	2009-08-31 14:49:44.477	2009-08-31 14:49:44.477	3
741	nav.sponsors	@nav.sponsors@	2009-08-31 23:40:34.721	2009-08-31 23:40:34.721	3
813	keyword.moon	@keyword.moon@	2009-09-01 01:12:34.214	2009-09-01 01:12:34.214	3
814	keyword.motion	@keyword.motion@	2009-09-01 01:12:37.417	2009-09-01 01:12:37.417	3
815	keyword.mri	@keyword.mri@	2009-09-01 01:12:40.6	2009-09-01 01:12:40.6	3
816	keyword.multiplication	@keyword.multiplication@	2009-09-01 01:12:43.936	2009-09-01 01:12:43.936	3
817	keyword.newtonsLaws	@keyword.newtonsLaws@	2009-09-01 01:12:47.068	2009-09-01 01:12:47.068	3
818	keyword.ohmsLaw	@keyword.ohmsLaw@	2009-09-01 01:12:50.336	2009-09-01 01:12:50.336	3
819	keyword.optical	@keyword.optical@	2009-09-01 01:12:53.478	2009-09-01 01:12:53.478	3
820	keyword.opticalTweezers	@keyword.opticalTweezers@	2009-09-01 01:12:56.497	2009-09-01 01:12:56.497	3
821	keyword.orderOfMagnitude	@keyword.orderOfMagnitude@	2009-09-01 01:12:59.736	2009-09-01 01:12:59.736	3
628	keyword.factoring	@keyword.factoring@	2009-08-31 14:54:20.402	2009-08-31 14:54:20.402	3
629	keyword.faradaysLaw	@keyword.faradaysLaw@	2009-08-31 14:54:23.953	2009-08-31 14:54:23.953	3
630	keyword.fields	@keyword.fields@	2009-08-31 14:54:27.564	2009-08-31 14:54:27.564	3
631	keyword.focalLength	@keyword.focalLength@	2009-08-31 14:54:30.921	2009-08-31 14:54:30.921	3
632	keyword.force	@keyword.force@	2009-08-31 14:54:34.456	2009-08-31 14:54:34.456	3
633	keyword.fourierAnalysis	@keyword.fourierAnalysis@	2009-08-31 14:54:37.788	2009-08-31 14:54:37.788	3
634	keyword.fourierSeries	@keyword.fourierSeries@	2009-08-31 14:54:40.788	2009-08-31 14:54:40.788	3
635	keyword.frequency	@keyword.frequency@	2009-08-31 14:54:43.78	2009-08-31 14:54:43.78	3
636	keyword.friction	@keyword.friction@	2009-08-31 14:54:47.461	2009-08-31 14:54:47.461	3
637	keyword.galton	@keyword.galton@	2009-08-31 14:54:53.229	2009-08-31 14:54:57.823	3
638	keyword.gas	@keyword.gas@	2009-08-31 14:55:01.466	2009-08-31 14:55:01.466	3
639	keyword.generator	@keyword.generator@	2009-08-31 14:55:04.427	2009-08-31 14:55:04.427	3
640	keyword.glaciers	@keyword.glaciers@	2009-08-31 14:55:07.09	2009-08-31 14:55:07.09	3
497	simulationMainPanel.topics	@simulationMainPanel.topics@	2009-08-31 14:16:50.1	2009-08-31 14:16:50.1	3
498	simulationMainPanel.mainTopics	@simulationMainPanel.mainTopics@	2009-08-31 14:16:53.053	2009-08-31 14:16:53.053	3
499	simulationMainPanel.keywords	@simulationMainPanel.keywords@	2009-08-31 14:16:55.898	2009-08-31 14:16:55.898	3
641	keyword.graphing	@keyword.graphing@	2009-08-31 14:55:16.312	2009-08-31 14:55:16.312	3
673	keyword.lightBulbs	@keyword.lightBulbs@	2009-08-31 14:57:26.872	2009-08-31 14:57:26.872	3
693	contribute.title	@contribute.title@	2009-08-31 14:58:31.872	2009-08-31 14:58:31.872	3
695	get-phet.one-at-a-time.title	@get-phet.one-at-a-time.title@	2009-08-31 14:58:36.828	2009-08-31 14:58:36.828	3
696	get-phet.full-install.title	@get-phet.full-install.title@	2009-08-31 14:58:39.181	2009-08-31 14:58:39.181	3
697	research.title	@research.title@	2009-08-31 14:58:41.569	2009-08-31 14:58:41.569	3
698	troubleshooting.java.title	@troubleshooting.java.title@	2009-08-31 14:58:46.922	2009-08-31 14:58:46.922	3
650	keyword.hydroxide	@keyword.hydroxide@	2009-08-31 14:55:59.094	2009-08-31 14:55:59.094	3
651	keyword.idealGasLaw	@keyword.idealGasLaw@	2009-08-31 14:56:02.546	2009-08-31 14:56:02.546	3
652	keyword.images	@keyword.images@	2009-08-31 14:56:06.496	2009-08-31 14:56:06.496	3
653	keyword.indexOfRefraction	@keyword.indexOfRefraction@	2009-08-31 14:56:10.349	2009-08-31 14:56:10.349	3
654	keyword.induction	@keyword.induction@	2009-08-31 14:56:14.139	2009-08-31 14:56:14.139	3
796	keyword.equilibrium	@keyword.equilibrium@	2009-09-01 01:11:23.775	2009-09-01 01:11:23.775	3
797	keyword.food	@keyword.food@	2009-09-01 01:11:29.576	2009-09-01 01:11:29.576	3
798	keyword.fuelConsumption	@keyword.fuelConsumption@	2009-09-01 01:11:35.034	2009-09-01 01:11:35.034	3
799	keyword.greenhouseGases	@keyword.greenhouseGases@	2009-09-01 01:11:39.76	2009-09-01 01:11:39.76	3
800	keyword.histograms	@keyword.histograms@	2009-09-01 01:11:43.621	2009-09-01 01:11:43.621	3
801	keyword.initialSpeed	@keyword.initialSpeed@	2009-09-01 01:11:48.33	2009-09-01 01:11:48.33	3
802	keyword.lunarLanding	@keyword.lunarLanding@	2009-09-01 01:11:55.857	2009-09-01 01:11:55.857	3
803	keyword.mass	@keyword.mass@	2009-09-01 01:12:00.207	2009-09-01 01:12:00.207	3
804	keyword.math	@keyword.math@	2009-09-01 01:12:03.809	2009-09-01 01:12:03.809	3
805	keyword.measurement	@keyword.measurement@	2009-09-01 01:12:07.132	2009-09-01 01:12:07.132	3
806	keyword.microwaves	@keyword.microwaves@	2009-09-01 01:12:10.436	2009-09-01 01:12:10.436	3
807	keyword.molarity	@keyword.molarity@	2009-09-01 01:12:13.905	2009-09-01 01:12:13.905	3
808	keyword.molecularMotors	@keyword.molecularMotors@	2009-09-01 01:12:17.534	2009-09-01 01:12:17.534	3
809	keyword.molecules	@keyword.molecules@	2009-09-01 01:12:21.018	2009-09-01 01:12:21.018	3
708	sponsors.otherSponsors	@sponsors.otherSponsors@	2009-08-31 19:25:15.689	2009-08-31 19:25:33.756	3
810	keyword.moles	@keyword.moles@	2009-09-01 01:12:24.422	2009-09-01 01:12:24.422	3
626	keyword.excitation	@keyword.excitation@	2009-08-31 14:49:57.611	2009-08-31 14:49:57.611	3
627	keyword.exercise	@keyword.exercise@	2009-08-31 14:54:16.455	2009-08-31 14:54:16.455	3
440	language.name	@language.name@	2009-08-31 14:13:12.184	2009-08-31 14:47:19.092	3
461	nav.sound-and-waves	@nav.sound-and-waves@	2009-08-31 14:14:30.606	2009-08-31 14:14:30.606	3
462	nav.work-energy-and-power	@nav.work-energy-and-power@	2009-08-31 14:15:07.38	2009-08-31 14:15:07.38	3
463	nav.heat-and-thermodynamics	@nav.heat-and-thermodynamics@	2009-08-31 14:15:10.32	2009-08-31 14:15:10.32	3
464	nav.quantum-phenomena	@nav.quantum-phenomena@	2009-08-31 14:15:13.015	2009-08-31 14:15:13.015	3
465	nav.light-and-radiation	@nav.light-and-radiation@	2009-08-31 14:15:15.353	2009-08-31 14:15:15.353	3
466	nav.electricity-magnets-and-circuits	@nav.electricity-magnets-and-circuits@	2009-08-31 14:15:17.786	2009-08-31 14:15:17.786	3
467	nav.biology	@nav.biology@	2009-08-31 14:15:21.177	2009-08-31 14:15:21.177	3
468	nav.chemistry	@nav.chemistry@	2009-08-31 14:15:23.642	2009-08-31 14:15:23.642	3
469	nav.earth-science	@nav.earth-science@	2009-08-31 14:15:26.523	2009-08-31 14:15:26.523	3
471	nav.tools	@nav.tools@	2009-08-31 14:15:31.639	2009-08-31 14:15:31.639	3
472	nav.applications	@nav.applications@	2009-08-31 14:15:34.117	2009-08-31 14:15:34.117	3
473	nav.cutting-edge-research	@nav.cutting-edge-research@	2009-08-31 14:15:36.254	2009-08-31 14:15:36.254	3
475	nav.workshops	@nav.workshops@	2009-08-31 14:15:40.554	2009-08-31 14:15:40.554	3
476	nav.get-phet	@nav.get-phet@	2009-08-31 14:15:42.478	2009-08-31 14:15:42.478	3
699	troubleshooting.flash.title	@troubleshooting.flash.title@	2009-08-31 14:58:49.293	2009-08-31 14:58:49.293	3
700	troubleshooting.javascript.title	@troubleshooting.javascript.title@	2009-08-31 14:58:51.913	2009-08-31 14:58:51.913	3
442	home.subheader	@home.subheader@	2009-08-31 14:13:36.195	2009-08-31 14:13:36.195	3
443	home.playWithSims	@home.playWithSims@	2009-08-31 14:13:38.529	2009-08-31 14:13:38.529	3
444	home.runOurSims	@home.runOurSims@	2009-08-31 14:13:40.596	2009-08-31 14:13:40.596	3
445	home.onLine	@home.onLine@	2009-08-31 14:13:43.237	2009-08-31 14:13:43.237	3
446	home.fullInstallation	@home.fullInstallation@	2009-08-31 14:13:45.6	2009-08-31 14:13:45.6	3
447	home.oneAtATime	@home.oneAtATime@	2009-08-31 14:13:47.98	2009-08-31 14:13:47.98	3
448	home.teacherIdeasAndActivities	@home.teacherIdeasAndActivities@	2009-08-31 14:13:50.386	2009-08-31 14:13:50.386	3
449	home.workshops	@home.workshops@	2009-08-31 14:13:55.218	2009-08-31 14:13:55.218	3
450	home.contribute	@home.contribute@	2009-08-31 14:13:58.055	2009-08-31 14:13:58.055	3
451	home.supportPhet	@home.supportPhet@	2009-08-31 14:14:00.164	2009-08-31 14:14:00.164	3
452	home.translateSimulations	@home.translateSimulations@	2009-08-31 14:14:03.787	2009-08-31 14:14:03.787	3
453	home.browseSims	@home.browseSims@	2009-08-31 14:14:05.896	2009-08-31 14:14:05.896	3
454	home.simulations	@home.simulations@	2009-08-31 14:14:09.274	2009-08-31 14:14:09.274	3
455	nav.home	@nav.home@	2009-08-31 14:14:15.842	2009-08-31 14:14:15.842	3
456	nav.simulations	@nav.simulations@	2009-08-31 14:14:18.273	2009-08-31 14:14:18.273	3
457	nav.featured	@nav.featured@	2009-08-31 14:14:20.786	2009-08-31 14:14:20.786	3
459	nav.physics	@nav.physics@	2009-08-31 14:14:25.485	2009-08-31 14:14:25.485	3
460	nav.motion	@nav.motion@	2009-08-31 14:14:27.953	2009-08-31 14:14:27.953	3
746	workshops.intro	@workshops.intro@	2009-09-01 00:08:58.54	2009-09-01 00:08:58.54	3
623	keyword.equipotential	@keyword.equipotential@	2009-08-31 14:49:47.856	2009-08-31 14:49:47.856	3
624	keyword.errorAnalysis	@keyword.errorAnalysis@	2009-08-31 14:49:50.861	2009-08-31 14:49:50.861	3
655	keyword.inductor	@keyword.inductor@	2009-08-31 14:56:17.936	2009-08-31 14:56:17.936	3
656	keyword.infrared	@keyword.infrared@	2009-08-31 14:56:22.097	2009-08-31 14:56:22.097	3
747	workshops.upcomingWorkshops	@workshops.upcomingWorkshops@	2009-09-01 00:09:05.428	2009-09-01 00:09:05.428	3
477	nav.get-phet.on-line	@nav.get-phet.on-line@	2009-08-31 14:15:45.329	2009-08-31 14:15:45.329	3
478	nav.get-phet.full-install	@nav.get-phet.full-install@	2009-08-31 14:15:47.515	2009-08-31 14:15:47.515	3
479	nav.get-phet.one-at-a-time	@nav.get-phet.one-at-a-time@	2009-08-31 14:15:53.668	2009-08-31 14:15:53.668	3
480	nav.troubleshooting.main	@nav.troubleshooting.main@	2009-08-31 14:15:55.739	2009-08-31 14:15:55.739	3
481	nav.troubleshooting.java	@nav.troubleshooting.java@	2009-08-31 14:15:57.475	2009-08-31 14:15:57.475	3
482	nav.troubleshooting.flash	@nav.troubleshooting.flash@	2009-08-31 14:16:00.509	2009-08-31 14:16:00.509	3
483	nav.troubleshooting.javascript	@nav.troubleshooting.javascript@	2009-08-31 14:16:02.483	2009-08-31 14:16:02.483	3
484	nav.contribute	@nav.contribute@	2009-08-31 14:16:05.413	2009-08-31 14:16:05.413	3
485	nav.research	@nav.research@	2009-08-31 14:16:08.181	2009-08-31 14:16:08.181	3
486	nav.about	@nav.about@	2009-08-31 14:16:10.516	2009-08-31 14:16:10.516	3
487	nav.about.source-code	@nav.about.source-code@	2009-08-31 14:16:12.327	2009-08-31 14:16:12.327	3
488	nav.about.contact	@nav.about.contact@	2009-08-31 14:16:14.817	2009-08-31 14:16:14.817	3
489	nav.about.who-we-are	@nav.about.who-we-are@	2009-08-31 14:16:17.32	2009-08-31 14:16:17.32	3
490	nav.about.licensing	@nav.about.licensing@	2009-08-31 14:16:19.803	2009-08-31 14:16:19.803	3
491	simulationMainPanel.translatedVersions	@simulationMainPanel.translatedVersions@	2009-08-31 14:16:30.157	2009-08-31 14:16:30.157	3
492	simulationMainPanel.screenshot.alt	@simulationMainPanel.screenshot.alt@	2009-08-31 14:16:34.073	2009-08-31 14:16:34.073	3
493	simulationMainPanel.version	@simulationMainPanel.version@	2009-08-31 14:16:37.96	2009-08-31 14:16:37.96	3
494	simulationMainPanel.kilobytes	@simulationMainPanel.kilobytes@	2009-08-31 14:16:40.792	2009-08-31 14:16:40.792	3
495	simulationMainPanel.runOffline	@simulationMainPanel.runOffline@	2009-08-31 14:16:44.563	2009-08-31 14:16:44.563	3
496	simulationMainPanel.runOnline	@simulationMainPanel.runOnline@	2009-08-31 14:16:47.776	2009-08-31 14:16:47.776	3
642	keyword.gravitationalForce	@keyword.gravitationalForce@	2009-08-31 14:55:23.721	2009-08-31 14:55:23.721	3
500	sponsors.principalSponsors	@sponsors.principalSponsors@	2009-08-31 14:17:04.468	2009-08-31 14:17:04.468	3
501	sponsors.hewlett	@sponsors.hewlett@	2009-08-31 14:17:07.955	2009-08-31 14:17:07.955	3
502	sponsors.nsf	@sponsors.nsf@	2009-08-31 14:17:11.966	2009-08-31 14:17:11.966	3
503	sponsors.ksu	@sponsors.ksu@	2009-08-31 14:17:15.095	2009-08-31 14:17:15.095	3
504	troubleshooting.main.title	@troubleshooting.main.title@	2009-08-31 14:17:22.059	2009-08-31 14:17:22.059	3
505	troubleshooting.main.intro	@troubleshooting.main.intro@	2009-08-31 14:17:24.346	2009-08-31 14:17:28.103	3
506	troubleshooting.main.java	@troubleshooting.main.java@	2009-08-31 14:17:30.757	2009-08-31 14:17:30.757	3
507	troubleshooting.main.flash	@troubleshooting.main.flash@	2009-08-31 14:17:32.923	2009-08-31 14:17:32.923	3
508	troubleshooting.main.javascript	@troubleshooting.main.javascript@	2009-08-31 14:17:35.866	2009-08-31 14:17:35.866	3
509	troubleshooting.main.faqs	@troubleshooting.main.faqs@	2009-08-31 14:17:38.304	2009-08-31 14:17:38.304	3
769	get-phet.one-at-a-time.runningSims	@get-phet.one-at-a-time.runningSims@	2009-09-01 01:08:12.606	2009-09-01 01:08:12.606	3
775	troubleshooting.java.q1.answer	@troubleshooting.java.q1.answer@	2009-09-01 01:10:04.705	2009-09-01 01:10:04.705	3
770	get-phet.one-at-a-time.downloadingSims	@get-phet.one-at-a-time.downloadingSims@	2009-09-01 01:08:24.162	2009-09-01 01:08:24.162	3
773	troubleshooting.java.faqs	@troubleshooting.java.faqs@	2009-09-01 01:10:01.78	2009-09-01 01:10:01.78	3
774	troubleshooting.java.q1.title	@troubleshooting.java.q1.title@	2009-09-01 01:10:03.238	2009-09-01 01:10:03.238	3
586	keyword.concentration	@keyword.concentration@	2009-08-31 14:41:42.096	2009-08-31 14:41:42.096	3
625	keyword.estimation	@keyword.estimation@	2009-08-31 14:49:54.227	2009-08-31 14:49:54.227	3
776	troubleshooting.java.q2.title	@troubleshooting.java.q2.title@	2009-09-01 01:10:06.646	2009-09-01 01:10:06.646	3
777	troubleshooting.java.q2.answer	@troubleshooting.java.q2.answer@	2009-09-01 01:10:07.766	2009-09-01 01:10:07.766	3
778	troubleshooting.java.q4.title	@troubleshooting.java.q4.title@	2009-09-01 01:10:09.145	2009-09-01 01:10:09.145	3
779	troubleshooting.java.q4.answer	@troubleshooting.java.q4.answer@	2009-09-01 01:10:10.661	2009-09-01 01:10:10.661	3
780	troubleshooting.java.q5.title	@troubleshooting.java.q5.title@	2009-09-01 01:10:11.724	2009-09-01 01:10:11.724	3
644	keyword.greenhouseEffect	@keyword.greenhouseEffect@	2009-08-31 14:55:31.648	2009-08-31 14:55:31.648	3
645	keyword.harmonicMotion	@keyword.harmonicMotion@	2009-08-31 14:55:36.784	2009-08-31 14:55:36.784	3
646	keyword.heat	@keyword.heat@	2009-08-31 14:55:40.371	2009-08-31 14:55:40.371	3
647	keyword.hookesLaw	@keyword.hookesLaw@	2009-08-31 14:55:44.731	2009-08-31 14:55:44.731	3
648	keyword.hydrogenAtom	@keyword.hydrogenAtom@	2009-08-31 14:55:49.902	2009-08-31 14:55:49.902	3
649	keyword.hydronium	@keyword.hydronium@	2009-08-31 14:55:55.063	2009-08-31 14:55:55.063	3
521	troubleshooting.main.q7.answer	@troubleshooting.main.q7.answer@	2009-08-31 14:19:01.114	2009-08-31 14:19:01.114	3
705	about.source-code.location	@about.source-code.location@	2009-08-31 16:37:56.22	2009-09-01 01:18:37.067	3
522	troubleshooting.main.q8.title	@troubleshooting.main.q8.title@	2009-08-31 14:19:03.721	2009-08-31 14:19:03.721	3
523	troubleshooting.main.q8.answer	@troubleshooting.main.q8.answer@	2009-08-31 14:19:07.717	2009-08-31 14:19:07.717	3
524	troubleshooting.main.q9.title	@troubleshooting.main.q9.title@	2009-08-31 14:19:10.152	2009-08-31 14:19:10.152	3
525	troubleshooting.main.q9.answer	@troubleshooting.main.q9.answer@	2009-08-31 14:19:19.609	2009-08-31 14:19:19.609	3
526	troubleshooting.main.q10.title	@troubleshooting.main.q10.title@	2009-08-31 14:19:25.261	2009-08-31 14:19:25.261	3
528	troubleshooting.main.q11.title	@troubleshooting.main.q11.title@	2009-08-31 14:19:37.15	2009-08-31 14:19:37.15	3
535	keyword.1d	@keyword.1d@	2009-08-31 14:22:48.856	2009-08-31 14:22:48.856	3
510	troubleshooting.main.q1.title	@troubleshooting.main.q1.title@	2009-08-31 14:17:40.155	2009-08-31 14:17:40.155	3
511	troubleshooting.main.q1.answer	@troubleshooting.main.q1.answer@	2009-08-31 14:17:54.331	2009-08-31 14:17:54.331	3
512	troubleshooting.main.q2.title	@troubleshooting.main.q2.title@	2009-08-31 14:18:06.032	2009-08-31 14:18:06.032	3
748	workshops.materials	@workshops.materials@	2009-09-01 00:09:12.601	2009-09-01 00:09:12.601	3
749	workshops.pastWorkshops	@workshops.pastWorkshops@	2009-09-01 00:09:18.913	2009-09-01 00:09:18.913	3
558	keyword.battery	@keyword.battery@	2009-08-31 14:39:54.347	2009-09-01 01:10:58.238	3
822	keyword.orderParameter	@keyword.orderParameter@	2009-09-01 01:13:10.664	2009-09-01 01:13:13.216	3
823	keyword.pH	@keyword.pH@	2009-09-01 01:13:18.351	2009-09-01 01:13:18.351	3
824	keyword.parallelCircuit	@keyword.parallelCircuit@	2009-09-01 01:13:22.035	2009-09-01 01:13:22.035	3
825	keyword.particles	@keyword.particles@	2009-09-01 01:13:26.136	2009-09-01 01:13:26.136	3
826	keyword.pendulum	@keyword.pendulum@	2009-09-01 01:13:29.772	2009-09-01 01:13:29.772	3
827	keyword.period	@keyword.period@	2009-09-01 01:13:36.872	2009-09-01 01:13:36.872	3
828	keyword.periodicMotion	@keyword.periodicMotion@	2009-09-01 01:13:40.665	2009-09-01 01:13:40.665	3
829	keyword.photoconductors	@keyword.photoconductors@	2009-09-01 01:13:45.519	2009-09-01 01:13:45.519	3
514	troubleshooting.main.q4.title	@troubleshooting.main.q4.title@	2009-08-31 14:18:12.806	2009-08-31 14:18:12.806	3
515	troubleshooting.main.q4.answer	@troubleshooting.main.q4.answer@	2009-08-31 14:18:18.85	2009-08-31 14:18:18.85	3
657	keyword.insulators	@keyword.insulators@	2009-08-31 14:56:26.5	2009-08-31 14:56:26.5	3
658	keyword.intensity	@keyword.intensity@	2009-08-31 14:56:30.295	2009-08-31 14:56:30.295	3
659	keyword.interference	@keyword.interference@	2009-08-31 14:56:34.034	2009-08-31 14:56:34.034	3
660	keyword.inverse	@keyword.inverse@	2009-08-31 14:56:38.429	2009-08-31 14:56:38.429	3
661	keyword.ionicCompounds	@keyword.ionicCompounds@	2009-08-31 14:56:41.813	2009-08-31 14:56:41.813	3
662	keyword.kinematics	@keyword.kinematics@	2009-08-31 14:56:45.15	2009-08-31 14:56:45.15	3
663	keyword.kineticEnergy	@keyword.kineticEnergy@	2009-08-31 14:56:49.913	2009-08-31 14:56:49.913	3
664	keyword.kirchoffsLaw	@keyword.kirchoffsLaw@	2009-08-31 14:56:53.8	2009-08-31 14:56:53.8	3
665	keyword.ksp	@keyword.ksp@	2009-08-31 14:56:57.613	2009-08-31 14:56:57.613	3
666	keyword.laser	@keyword.laser@	2009-08-31 14:57:02.05	2009-08-31 14:57:02.05	3
667	keyword.lcCircuit	@keyword.lcCircuit@	2009-08-31 14:57:05.325	2009-08-31 14:57:05.325	3
668	keyword.leChateliersPrinciple	@keyword.leChateliersPrinciple@	2009-08-31 14:57:08.746	2009-08-31 14:57:08.746	3
669	keyword.leds	@keyword.leds@	2009-08-31 14:57:11.843	2009-08-31 14:57:11.843	3
670	keyword.length	@keyword.length@	2009-08-31 14:57:15.764	2009-08-31 14:57:15.764	3
671	keyword.lens	@keyword.lens@	2009-08-31 14:57:19.322	2009-08-31 14:57:19.322	3
672	keyword.light	@keyword.light@	2009-08-31 14:57:23.306	2009-08-31 14:57:23.306	3
787	troubleshooting.java.q8.answer	@troubleshooting.java.q8.answer@	2009-09-01 01:10:21.13	2009-09-01 01:10:21.13	3
788	keyword.acVoltage	@keyword.acVoltage@	2009-09-01 01:10:35.173	2009-09-01 01:10:35.173	3
789	keyword.acids	@keyword.acids@	2009-09-01 01:10:38.779	2009-09-01 01:10:38.779	3
790	keyword.alphaParticles	@keyword.alphaParticles@	2009-09-01 01:10:42.658	2009-09-01 01:10:42.658	3
791	keyword.ammonia	@keyword.ammonia@	2009-09-01 01:10:46.405	2009-09-01 01:10:46.405	3
792	keyword.bases	@keyword.bases@	2009-09-01 01:10:51.563	2009-09-01 01:10:51.563	3
793	keyword.biology	@keyword.biology@	2009-09-01 01:11:01.456	2009-09-01 01:11:01.456	3
794	keyword.charlesLaw	@keyword.charlesLaw@	2009-09-01 01:11:06.502	2009-09-01 01:11:06.502	3
795	keyword.cosines	@keyword.cosines@	2009-09-01 01:11:12.531	2009-09-01 01:11:12.531	3
613	keyword.electromagnet	@keyword.electromagnet@	2009-08-31 14:49:16.122	2009-09-01 01:11:18.936	3
1037	sponsors.financialSupport	@sponsors.financialSupport@	2009-09-03 13:12:44.489	2009-09-03 13:13:08.995	3
1038	sponsors.platinum	@sponsors.platinum@	2009-09-03 13:12:46.455	2009-09-03 13:12:46.455	3
1036	sponsors.header	@sponsors.header@	2009-09-03 13:12:42.273	2009-09-03 13:12:54.513	3
1039	sponsors.gold	@sponsors.gold@	2009-09-03 13:13:16.218	2009-09-03 13:13:16.218	3
1040	sponsors.bronze	@sponsors.bronze@	2009-09-03 13:13:18.803	2009-09-03 13:13:18.803	3
1041	sponsors.technicalSupport	@sponsors.technicalSupport@	2009-09-03 13:13:20.311	2009-09-03 13:13:20.311	3
1042	sponsors.piccolo.desc	@sponsors.piccolo.desc@	2009-09-03 13:13:21.773	2009-09-03 13:13:21.773	3
1043	sponsors.jfreechart.desc	@sponsors.jfreechart.desc@	2009-09-03 13:13:23.375	2009-09-03 13:13:23.375	3
1044	sponsors.jade.desc	@sponsors.jade.desc@	2009-09-03 13:13:25.966	2009-09-03 13:13:25.966	3
1045	sponsors.sourceforge.desc	@sponsors.sourceforge.desc@	2009-09-03 13:13:28.032	2009-09-03 13:13:28.032	3
1046	sponsors.proguard.desc	@sponsors.proguard.desc@	2009-09-03 13:13:29.516	2009-09-03 13:13:29.516	3
1047	sponsors.jetbrains.desc	@sponsors.jetbrains.desc@	2009-09-03 13:13:31.138	2009-09-03 13:13:31.138	3
1048	sponsors.ej.desc	@sponsors.ej.desc@	2009-09-03 13:13:33.164	2009-09-03 13:13:33.164	3
1049	sponsors.bitrock.desc	@sponsors.bitrock.desc@	2009-09-03 13:13:34.761	2009-09-03 13:13:34.761	3
1050	sponsors.royalinteractive.desc	@sponsors.royalinteractive.desc@	2009-09-03 13:13:36.386	2009-09-03 13:13:36.386	3
1051	sponsors.dynamicalSystems.desc	@sponsors.dynamicalSystems.desc@	2009-09-03 13:13:38.077	2009-09-03 13:13:38.077	3
513	troubleshooting.main.q2.answer	@troubleshooting.main.q2.answer@	2009-08-31 14:18:10.379	2009-08-31 14:18:22.189	3
458	nav.new	@nav.new@	2009-08-31 14:14:23.376	2009-08-31 14:14:23.376	3
582	keyword.climate	@keyword.climate@	2009-08-31 14:41:28.357	2009-08-31 14:41:28.357	3
517	troubleshooting.main.q5.answer	@troubleshooting.main.q5.answer@	2009-08-31 14:18:34.132	2009-08-31 14:18:37.813	3
518	troubleshooting.main.q6.title	@troubleshooting.main.q6.title@	2009-08-31 14:18:40.743	2009-08-31 14:18:40.743	3
519	troubleshooting.main.q6.answer	@troubleshooting.main.q6.answer@	2009-08-31 14:18:45.308	2009-08-31 14:18:45.308	3
520	troubleshooting.main.q7.title	@troubleshooting.main.q7.title@	2009-08-31 14:18:57.404	2009-08-31 14:18:57.404	3
546	keyword.angularAcceleration	@keyword.angularAcceleration@	2009-08-31 14:39:16.277	2009-08-31 14:39:16.277	3
547	keyword.angularMomentum	@keyword.angularMomentum@	2009-08-31 14:39:19.144	2009-08-31 14:39:19.144	3
548	keyword.angularPosition	@keyword.angularPosition@	2009-08-31 14:39:22.717	2009-08-31 14:39:22.717	3
549	keyword.angularVelocity	@keyword.angularVelocity@	2009-08-31 14:39:25.19	2009-08-31 14:39:25.19	3
550	keyword.area	@keyword.area@	2009-08-31 14:39:28.671	2009-08-31 14:39:28.671	3
551	keyword.arithmetic	@keyword.arithmetic@	2009-08-31 14:39:31.302	2009-08-31 14:39:31.302	3
552	keyword.arrheniusParameters	@keyword.arrheniusParameters@	2009-08-31 14:39:34.381	2009-08-31 14:39:34.381	3
553	keyword.astronomy	@keyword.astronomy@	2009-08-31 14:39:37.612	2009-08-31 14:39:37.612	3
554	keyword.atmosphere	@keyword.atmosphere@	2009-08-31 14:39:40.684	2009-08-31 14:39:40.684	3
555	keyword.atomicNuclei	@keyword.atomicNuclei@	2009-08-31 14:39:43.362	2009-08-31 14:39:43.362	3
556	keyword.atomicStructure	@keyword.atomicStructure@	2009-08-31 14:39:47.553	2009-08-31 14:39:47.553	3
557	keyword.atoms	@keyword.atoms@	2009-08-31 14:39:51.479	2009-08-31 14:39:51.479	3
559	keyword.binomialDistribution	@keyword.binomialDistribution@	2009-08-31 14:39:57.116	2009-08-31 14:39:57.116	3
560	keyword.blackbody	@keyword.blackbody@	2009-08-31 14:40:00.463	2009-08-31 14:40:00.463	3
561	keyword.bohrModel	@keyword.bohrModel@	2009-08-31 14:40:03.119	2009-08-31 14:40:03.119	3
562	keyword.boltzmannDistribution	@keyword.boltzmannDistribution@	2009-08-31 14:40:06.685	2009-08-31 14:40:06.685	3
563	keyword.bonds	@keyword.bonds@	2009-08-31 14:40:09.825	2009-08-31 14:40:09.825	3
564	keyword.boylesLaw	@keyword.boylesLaw@	2009-08-31 14:40:12.552	2009-08-31 14:40:12.552	3
565	keyword.buoyancy	@keyword.buoyancy@	2009-08-31 14:40:15.535	2009-08-31 14:40:15.535	3
566	keyword.calculation	@keyword.calculation@	2009-08-31 14:40:26.722	2009-08-31 14:40:26.722	3
567	keyword.calories	@keyword.calories@	2009-08-31 14:40:35.234	2009-08-31 14:40:35.234	3
568	keyword.capacitance	@keyword.capacitance@	2009-08-31 14:40:38.446	2009-08-31 14:40:38.446	3
569	keyword.capacitor	@keyword.capacitor@	2009-08-31 14:40:41.428	2009-08-31 14:40:41.428	3
570	keyword.catalysts	@keyword.catalysts@	2009-08-31 14:40:46.064	2009-08-31 14:40:46.064	3
571	keyword.cathodeRayTube	@keyword.cathodeRayTube@	2009-08-31 14:40:49.544	2009-08-31 14:40:49.544	3
572	keyword.charges	@keyword.charges@	2009-08-31 14:40:52.74	2009-08-31 14:40:52.74	3
573	keyword.chemicalEquilibrium	@keyword.chemicalEquilibrium@	2009-08-31 14:40:56.32	2009-08-31 14:40:56.32	3
574	keyword.chemicalFormula	@keyword.chemicalFormula@	2009-08-31 14:40:59.962	2009-08-31 14:40:59.962	3
575	keyword.chemistry	@keyword.chemistry@	2009-08-31 14:41:03.487	2009-08-31 14:41:03.487	3
576	keyword.chi	@keyword.chi@	2009-08-31 14:41:06.872	2009-08-31 14:41:06.872	3
577	keyword.chiSquare	@keyword.chiSquare@	2009-08-31 14:41:10.417	2009-08-31 14:41:10.417	3
578	keyword.chiSquared	@keyword.chiSquared@	2009-08-31 14:41:13.847	2009-08-31 14:41:13.847	3
579	keyword.circuits	@keyword.circuits@	2009-08-31 14:41:18.388	2009-08-31 14:41:18.388	3
580	keyword.circularMotion	@keyword.circularMotion@	2009-08-31 14:41:21.536	2009-08-31 14:41:21.536	3
581	keyword.circularRevolution	@keyword.circularRevolution@	2009-08-31 14:41:25.044	2009-08-31 14:41:25.044	3
529	troubleshooting.main.q11.answer	@troubleshooting.main.q11.answer@	2009-08-31 14:19:40.579	2009-08-31 14:19:40.579	3
530	troubleshooting.main.q12.title	@troubleshooting.main.q12.title@	2009-08-31 14:19:42.71	2009-08-31 14:19:42.71	3
531	troubleshooting.main.q12.answer	@troubleshooting.main.q12.answer@	2009-08-31 14:19:44.362	2009-08-31 14:19:44.362	3
536	keyword.absorption	@keyword.absorption@	2009-08-31 14:22:51.834	2009-08-31 14:22:51.834	3
544	keyword.amplitude	@keyword.amplitude@	2009-08-31 14:39:09.896	2009-08-31 14:39:09.896	3
545	keyword.angle	@keyword.angle@	2009-08-31 14:39:13.409	2009-08-31 14:39:13.409	3
583	keyword.climateChange	@keyword.climateChange@	2009-08-31 14:41:32.39	2009-08-31 14:41:32.39	3
584	keyword.compass	@keyword.compass@	2009-08-31 14:41:35.152	2009-08-31 14:41:35.152	3
\.


--
-- Data for Name: translation; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY translation (id, locale, visible) FROM stdin;
1	en	t
4	ar	t
2	zh_CN	f
3	it	t
\.


--
-- Data for Name: user_translation_mapping; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY user_translation_mapping (translation_id, user_id) FROM stdin;
3	1
4	1
\.


--
-- Name: category_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY category_mapping
    ADD CONSTRAINT category_mapping_pkey PRIMARY KEY (category_id, idx);


--
-- Name: category_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- Name: keyword_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY keyword_mapping
    ADD CONSTRAINT keyword_mapping_pkey PRIMARY KEY (simulation_id, idx);


--
-- Name: keyword_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY keyword
    ADD CONSTRAINT keyword_pkey PRIMARY KEY (id);


--
-- Name: localized_simulation_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY localized_simulation
    ADD CONSTRAINT localized_simulation_pkey PRIMARY KEY (id);


--
-- Name: phet_user_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY phet_user
    ADD CONSTRAINT phet_user_pkey PRIMARY KEY (id);


--
-- Name: project_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- Name: simulation_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY simulation
    ADD CONSTRAINT simulation_pkey PRIMARY KEY (id);


--
-- Name: topics_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY topics_mapping
    ADD CONSTRAINT topics_mapping_pkey PRIMARY KEY (simulation_id, idx);


--
-- Name: translated_string_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY translated_string
    ADD CONSTRAINT translated_string_pkey PRIMARY KEY (id);


--
-- Name: translation_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY translation
    ADD CONSTRAINT translation_pkey PRIMARY KEY (id);


--
-- Name: user_translation_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: phet; Tablespace: 
--

ALTER TABLE ONLY user_translation_mapping
    ADD CONSTRAINT user_translation_mapping_pkey PRIMARY KEY (user_id, translation_id);


--
-- Name: fk1854a79ac047ff77; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY translated_string
    ADD CONSTRAINT fk1854a79ac047ff77 FOREIGN KEY (translation) REFERENCES translation(id) ON DELETE CASCADE;


--
-- Name: fk302bcfe34a093d9; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY category
    ADD CONSTRAINT fk302bcfe34a093d9 FOREIGN KEY (parent_id) REFERENCES category(id);


--
-- Name: fk311e4d4b6b081b59; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY localized_simulation
    ADD CONSTRAINT fk311e4d4b6b081b59 FOREIGN KEY (simulation) REFERENCES simulation(id);


--
-- Name: fk47534b3aab0afa5; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY topics_mapping
    ADD CONSTRAINT fk47534b3aab0afa5 FOREIGN KEY (simulation_id) REFERENCES simulation(id);


--
-- Name: fk47534b3ba10a9af; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY topics_mapping
    ADD CONSTRAINT fk47534b3ba10a9af FOREIGN KEY (keyword_id) REFERENCES keyword(id);


--
-- Name: fk50f116cc5062452c; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY user_translation_mapping
    ADD CONSTRAINT fk50f116cc5062452c FOREIGN KEY (user_id) REFERENCES phet_user(id);


--
-- Name: fk50f116ccf968c22f; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY user_translation_mapping
    ADD CONSTRAINT fk50f116ccf968c22f FOREIGN KEY (translation_id) REFERENCES translation(id);


--
-- Name: fkb3012607b7b702c7; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY simulation
    ADD CONSTRAINT fkb3012607b7b702c7 FOREIGN KEY (project) REFERENCES project(id);


--
-- Name: fkfbcce478aab0afa5; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY keyword_mapping
    ADD CONSTRAINT fkfbcce478aab0afa5 FOREIGN KEY (simulation_id) REFERENCES simulation(id);


--
-- Name: fkfbcce478ba10a9af; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY keyword_mapping
    ADD CONSTRAINT fkfbcce478ba10a9af FOREIGN KEY (keyword_id) REFERENCES keyword(id);


--
-- Name: fkfce89e8d14e28f05; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY category_mapping
    ADD CONSTRAINT fkfce89e8d14e28f05 FOREIGN KEY (category_id) REFERENCES category(id);


--
-- Name: fkfce89e8daab0afa5; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY category_mapping
    ADD CONSTRAINT fkfce89e8daab0afa5 FOREIGN KEY (simulation_id) REFERENCES simulation(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--


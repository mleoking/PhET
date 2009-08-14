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
ALTER TABLE ONLY public.localized_simulation DROP CONSTRAINT fk311e4d4b6b081b59;
ALTER TABLE ONLY public.category DROP CONSTRAINT fk302bcfe34a093d9;
ALTER TABLE ONLY public.translated_string DROP CONSTRAINT fk1854a79ac047ff77;
ALTER TABLE ONLY public.translation DROP CONSTRAINT translation_pkey;
ALTER TABLE ONLY public.translated_string DROP CONSTRAINT translated_string_pkey;
ALTER TABLE ONLY public.simulation DROP CONSTRAINT simulation_pkey;
ALTER TABLE ONLY public.project DROP CONSTRAINT project_pkey;
ALTER TABLE ONLY public.phet_user DROP CONSTRAINT phet_user_pkey;
ALTER TABLE ONLY public.localized_simulation DROP CONSTRAINT localized_simulation_pkey;
ALTER TABLE ONLY public.keyword DROP CONSTRAINT keyword_pkey;
ALTER TABLE ONLY public.keyword_mapping DROP CONSTRAINT keyword_mapping_pkey;
ALTER TABLE ONLY public.category DROP CONSTRAINT category_pkey;
ALTER TABLE ONLY public.category_mapping DROP CONSTRAINT category_mapping_pkey;
DROP TABLE public.translation;
DROP TABLE public.translated_string;
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
    id bigint NOT NULL,
    key character varying(255)
);


ALTER TABLE public.keyword OWNER TO phet;

--
-- Name: keyword_mapping; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE keyword_mapping (
    simulation_id integer NOT NULL,
    keyword_id bigint NOT NULL,
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
    project integer
);


ALTER TABLE public.simulation OWNER TO phet;

--
-- Name: translated_string; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE translated_string (
    id integer NOT NULL,
    key character varying(255),
    value character varying(4048),
    translation integer NOT NULL
);


ALTER TABLE public.translated_string OWNER TO phet;

--
-- Name: translation; Type: TABLE; Schema: public; Owner: phet; Tablespace: 
--

CREATE TABLE translation (
    id integer NOT NULL,
    locale character varying(255)
);


ALTER TABLE public.translation OWNER TO phet;

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
\.


--
-- Data for Name: keyword_mapping; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY keyword_mapping (simulation_id, keyword_id, idx) FROM stdin;
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
614	el	Quantum Wave Interference	Quantum Wave Interference	42
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
43	en	Battery-Resistor Circuit	An exploration of Ohm's Law and how it applies to the relationship between volts, amps and ohms.	2
44	es	Voltaje de bateria	Una ilustracion del voltaje dentro de una bateria	3
45	de	Batteriespannung	Eine Darstellung der Spannung in einer Batterie	3
46	nl	Spanning batterij	Een simulatie van de spanning van een batterij	3
47	sk	Napätie batérie	An illustration of voltage within a battery	3
48	uk	Battery Voltage	An illustration of voltage within a battery	3
49	el	Τάση Μπαταρίας	Επεξήγηση της εμφάνισης τάσης σε μια μπαταρία	3
50	pt	Voltagem de bateria	Uma ilustração da voltagem em uma bateria	3
51	ru	Напряжение батарейки	Иллюстрация напряжения внутри батарейки	3
52	ro	Voltaj Baterie	O ilustratie a voltajului intr-o baterie	3
53	fr	Tension de la pile	Une illustration de la tension à l'intérieur d'une pile	3
54	hu	Elem feszültség	Az elem feszültségének bemutatása	3
55	it	d.d.p. in una Pila	Una illustrazione della differenza di potenziale in una pila	3
56	zh_CN	电池电压	电池内电压说明	3
57	hr	Napon (elektromotorna sila) baterije	Elektromotorna sila beterije	3
58	zh_TW	電池電壓	電池內部電壓圖解	3
59	pl	napięcie ogniwa	Ilustracja napięcia wewnętrznego ogniwa	3
60	en	Battery Voltage	An illustration of voltage within a battery	3
61	es	Estructura de Bandas	Explore el origen de las bandas de energía en cristales de átomos. La estructura de estas bandas determinan cómo los materiales conducen la electricidad.	4
62	el	Δομή Ζωνών	Διερευνήστε την προέλευση των ενεργειακών ζωνών στους κρυστάλλους. Η δομή αυτών των ζωνών καθορίζει την ηλεκτρική αγωγιμότητα των υλικών	4
63	pt	Estrutura de Bandas	<html>Explore a origem das bandas de energia em estruturas cristalinas.<br>A estrutura de banda determina o comportamento elétrico destes compostos cristalinos.</html>	4
64	mn	Долгионы бүтэц	Explore the origin of energy bands in crystals of atoms. The structure of these bands determines how materials conduct electricity.	4
65	nl	Bandstructuur	Onderzoek het ontstaan van energieniveaus in kristallen van atomaire stoffen. De structuur van deze energieniveaus bepaalt de geleidbaarheid van het materiaal.	4
66	ar	تركيب النطاق	اكتشف أصل نطاق الطاقة في بلورات الذرة. تركيب هذه الأشرطة يحدد كيف توصل المواد الكهرباء	4
67	hr	Struktura Vrpci	Otkrijte porijeklo energijskih vrpci u kristalima. Struktura vrpci određuje električna svojstva tvari.	4
68	en	Band Structure	Explore the origin of energy bands in crystals of atoms. The structure of these bands determines how materials conduct electricity.	4
69	es	Pozos Dobles y Enlaces Covalentes	Explore la partición tunelada en potenciales de pozo doble. Este clásico problema describe muchos sistemas físicos, incluyendo enlaces covalentes, las ensambladuras de Josephson, y los sistemas del dos estado tales como el spin de 1/2 partículas y  las moléculas del amoníaco.	5
70	el	Διπλά Πηγάδια και Ομοιοπολικοί Δεσμοί	Διερευνήστε το διαχωρισμό σύραγγας σε διπλά πηγάδια δυναμικού. Αυτό το κλασσικό πρόβλημα πολλά φυσικά συστήματα όπως ομοιοπολικούς δεσμούς, επαφές Josephson και συστήματα δυο καταστάσεων όπως σωματίδια με spin  1/2 και μόρια Αμμωνίας	5
71	pt	Poços duplos e Ligações Covalentes	<html>Explore desdobramento por tunelamento em poços quânticos duplos. Este problema<br>clássico descreve vários sistemas físicos reais, incluindo ligações covalestes,<br>Junções Josephson  e sistemas de dois níveis, como partículas de spin 1/2 em campo magnético<br>e moléculas de Amônia.</html>	5
72	mn	Хоёр нүх ба 	Explore tunneling splitting in double well potentials. This classic problem describes many physical systems, including covalent bonds, Josephson junctions, and two-state systems such as spin 1/2 particles and Ammonia molecules.	5
73	nl	Covalente bindingen (dubbele potentiaalputten)	Onderzoek het opsplitsen in dubbele potentiaalputten. Dit klassieke probleem beschrijft fysische systemen waaronder covalente bindingen en ammoniakmoleculen.	5
74	ar	الأبار المضاعفة والترابط الإسهامي	اكتشف الانفصال النفقي في جهود البئر المضاعف. هذه المسألة التقليدية تصف  عدد من الأنظمة الحقيقية مثل الترابط الإسهامي، موصلات جوزيفسون، وانظمة الحالتين مثل الجسيمات ذات دوران النصف وجزيئات الأمونيا.	5
75	hr	Dvostuka jama i kovalentna veza	Otkrijte tuneliranje. Ovaj model opisuje puno različitih fizikalnih sustava: kovalentnu vezu, Josephsonov spoj, sustav s dva stanja kao što su sustavi spina 1/2 i molekula amonijaka.	5
76	en	Double Wells and Covalent Bonds	Explore tunneling splitting in double well potentials. This classic problem describes many physical systems, including covalent bonds, Josephson junctions, and two-state systems such as spin 1/2 particles and Ammonia molecules.	5
77	es	Estados Ligados Cuánticos	Explore las propiedades de "partículas" cuánticas ligadas en pozos de potencial. Vea cómo las funciones de onda y las densidades de probabilidad que las describen evolucionan (o no evolucionan) a lo largo del tiempo.	6
78	el	Δεσμευμένες Κβαντικές Καταστάσεις	Διερευνήστε τις ιδιότητες κβαντικών "σωματιδίων" δεσμευμένων σε πηγάδια δυναμικού. Δείτε πως οι κυμματοσυναρτήσεις και οι πυκνότητες πιθανότητας που τις περιγράφουν εξελίσονται (ή δεν εξελίσονται) με τον χρόνο.	6
79	pt	Estados Quânticos Ligados	<html>Explore as propriedades de "partículas quânticas" ligadas em poço de potencial.<br>Veja a evolução temporal de suas funções de onda e densidades de probabilidade.<br></html>	6
80	mn	Quantum Bound States	Explore the properties of quantum "particles" bound in potential wells. See how the wave functions and probability densities that describe them evolve (or don't evolve) over time.	6
81	nl	Quantum bindingstoestanden	Onderzoek de eigenschappen van 'quantumdeeltjes' gebonden in potentiaal putten. Merk op hoe de golffuncties en waarschijnlijkheidsverdelingen de deeltjes beschrijven in de tijd.	6
82	ar	حالات الربط الكمي	اكتشف خصائص الجسيمات الكمية في بئر الجهد. انظر كيف تنبعث أو لاتنبعث دوال الموجة والكثافات الاحتمالية التي تصفهم مع الزمن.	6
83	hr	Vezana stanja (kvantnomehanički koncept)	Istražite svojstva kvanata vezanih u potencijalnoj jami. Vizualizirajte kako  valna funkcija i funkcija gustoće vjerojatnosti opisuju ponašanje kvanata u vremenu.	6
84	en	Quantum Bound States	Explore the properties of quantum "particles" bound in potential wells. See how the wave functions and probability densities that describe them evolve (or don't evolve) over time.	6
85	fi	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
86	el	Εργαστήριο κατασκευής κυκλωμάτων συνεχούς κι εναλλασσομένου ρεύματος	Κατασκευάστε κυκλώματα μ' αντιστάτες, λαμπτήρες, διακόπτες, πηνία, πυκνωτές κι ηλεκτρικές πηγές AC ή DC. Πάρτε μετρήσεις μ' αμπερόμετρο και βολτόμετρο και κατασκευάστε τα διαγράμματα τάσης κι έντασης \nσυναρτήσει του χρόνου. Δείτε το κύκλωμα με σύμβολα ή μ' αληθοφανή όψη.\nΣώστε για το μέλλον, τα κυκλώματα που κατασκευάσατε !!\n\nEλληνική απόδοση : Κώστας Δακανάλης.	7
87	ar	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	7
88	fr	Circuit Construction Kit (DC and AC)	Créez votre propre circuit, jouez avec les électrons, faites briller une ampoule.	7
89	es	Kit de Construccion de Circuitos (CC y CA)	Esta nueva versión de CCK ¡añade los capacitores, inductores y las fuentes de voltaje a tu caja de herramientas! Ahora se puede sacar gráficos de corriente y de voltaje en función del tiempo.	7
90	et	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
91	nl	Schakeling bouwdoos (Gelijk~ en wisselspanning)	Deze nieuwe versie van CCK heeft condensatoren, inductoren en wisselspanningsbronnen aan de gereedschappen toegevoegd! Je kan grafieken van stroom en spanning in de tijd maken.	7
92	it	Circuit Construction Kit (DC and AC)	Questa nuova versione aggiunge alla toolbox condensatori, induttori e sorgenti a correnti alternate! E' possibile visualizzare il grafico della tensione e corrente in funzione del tempo.\n\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	7
93	hu	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
94	sk	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
95	sl	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
96	uk	Електричні кола постійного та змінного струму	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
97	pt	Circuitos AC/DC (DC e AC)	<html>Nova versão do simulador de circuitos que possui elementos e fontes de corrente alternada!<br>Agora você pode visualizar a corrente e a tensão num gráfico em função do tempo.</html>	7
98	da	Kredsløbssamlesæt (jævn- og vekselstrøm)	I denne nye version af CCK er der tilføjet kondensatorer, induktionsspoler og vekselstrømkilde til din 'værktøjskasse'!	7
99	ru	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
100	ga	Circuit Construction Kit (DC and AC)	Tóg do ciorcad fhéin, Imir le leictreoin, las suas bolgán.	7
101	de	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
102	ja	直流・交流回路キット	新回路キットはコンデンサーとインダクターと交流電源が加わりました。電流と電圧の時間変化のグラフとして表すことができます。	7
169	hr	Vodljivost	Koncept vodljivosti u metalima, izolatorima i fotoosjetljvim materijalima	10
615	pt	Interferência Quântica	Interferência Quântica	42
103	vi	Thi nghiem mach dien (DC và AC)	Phiiên bản mới này của CCK có thêm tụ, vật dẫn điện và nguồn xoay chiều trong hộp công cụ. Bây giờ, bạn có thể có được đồ thị dòng điện và điện áp theo thời gian.	7
104	nb	Circuit Construction Kit (likestrøm og vekselstrøm)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
105	ro	Trusa de Constructie Circuite (DC si AC)	Versiunea noua CCK adauga Cutiei de Ustensile condensatoare, inductori si surse voltaj AC! Acum poti reprezenta cutentul si voltajul ca o functie a timpului.	7
106	sr	Комплет за једносмерну струју	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
107	tr	Devre Yapım Kiti (AC ve DC)	Devre yapım kitinin yeni versiyonu ile kapasitör, indüktör ve alternatif akım seçenekleri kullanılabilecektir. 	7
108	ak	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	7
109	hr	Dizajniranje srujnih krugova	Nova inačica CCK sadrži kapacitore, induktancije i izvore izmjeničnog napona! Sada možete vidjeti ovisnost struje i napona u ovisnosti o vremenu.	7
110	zh_TW	電路組裝套件(直流和 交流)	新版的CCK在工具箱中加入電容、電感和交流電源。現在可以用圖表表示時間函數中的電流與電壓。	7
111	pl	Zestaw symulacyjny obwodów - napięcia stałe i zmienne	Ta nowa wersja obwodu CCK zawiera dodatkowe kondensatory, indukcyjności, napięcia zmienne; są w skrzynce narzedziowej. Możesz również uzyskać wykresy prądu i napięcia w funkcji czasu	7
112	en	Circuit Construction Kit (DC and AC)	This new version of the CCK adds capacitors, inductors and AC voltage sources to your toolbox! Now you can graph the current and voltage as a function of time.	7
113	fi	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
114	el	Εργαστήριο κατασκευής κυκλωμάτων συνεχούς ρεύματος	Κατασκευάστε κυκλώματα μ' αντιστάτες, λαμπτήρες, διακόπτες και μπαταρίες. Δείτε το κύκλωμα με σύμβολα ή μ' αληθοφανή όψη.\nΣώστε για το μέλλον, τα κυκλώματα που κατασκευάσατε !!\nEλληνική απόδοση : Κώστας Δακανάλης.	8
115	ar	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	8
116	fr	Circuit Construction Kit (DC Only)	Créez votre propre circuit, jouez avec les électrons, faites briller une ampoule.	8
117	es	Kit de Construcción de Circuitos (CC sólo)	¡Un kit electrónico en tu ordenador! Contruya circuitos con resistencias, bombillas, baterías e interruptores. Haz mediciones con los amperímetros y voltímetros realistas. Ve el circuito como un diagrama esquemático o alterna a una vista casi realista.	8
118	et	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
119	nl	Schakeling bouwdoos (Gelijkspanning)	Een elektrische bouwdoos in je computer! Bouw schakelingen met weerstanden, lampjes, batterijen en schakelaars. Doe metingen met realistische stroommeter en voltmeter. Bekijk de schakeling als een schema of als een levensecht voorbeeld.	8
120	it	Circuit Construction Kit (DC Only)	Un kit elettronico nel tuo computer! Realizza circuiti con resistenze, lampadine, batterie e interruttori. Fai misure realistiche con amperometri e voltmetri. Vedi il circuito in modalità schema elettrico o in modalità simile alla realtà.\n\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	8
121	hu	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
122	sk	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
123	sl	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
124	uk	Електричні кола постійного струму	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
125	pt	Circuitos de Corrente Contínua (DC)	<html>Um kit de experimentação de eletrônica em seu computador!<br>Construa circuitos com resistores, lâmpadas, baterias e interruptores. <br>Realize medidas com amperímetros e voltímetros.<br>Visualize o circuito no formato de diagrama esquemático ou no formato de visão natural.</html>	8
126	da	Kredsløbssamlesæt (kun jævnstrøm)	Et elektronisk samlesæt i din komputer!\nByg kredsløb med modstande, pærer, batterier og kontakter.\nForetag målinger med realistiske amperemetre og voltmetre.\nSe kredsløbene i diagramform eller tegnet rigtigt.	8
127	ru	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
128	ga	Circuit Construction Kit (DC Only)	Tóg do ciorcad fhéin, Imir le leictreoin, las suas bolgán.	8
129	de	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
130	ja	直流回路キット	電子回路キット 抵抗、電球、電池、スイッチを使って回路を作ましょう電流計と電圧計で測定しよう。 \\u3000回路を実物図にしたり、配線図にしたりしてみましょう。	8
854	nl	Maanlander	\N	66
131	vi	Mạch điện (một chiều)	Bạn hãy lắp mạch điện theo mục đích của mình, mạch gồm có bóng đèn, điện trở, công tắc và nguồn điện 1 chiều. Sau đó, sử dụng dụng cụ đo Ampe kế để đo cường độ dòng điện trong mạch, Vôn kế để đo điện áp giữa 2 đầu đoạn mạch...	8
132	nb	Circuit Construction Kit (kun likestrøm)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
133	ro	Trusa de Constructie Circuite (doar DC)	O trusa electronica pe computerul dvs.! Construiti circuite cu rezistori, becuri electrice, baterii si comutatoare. Faceti masuratori cu ampermetrii si voltmetrii realistici. Vizualizati circuitul ca o diagrama schematica, sau schimbati vizualizarea in Animatie realista.	8
134	sr	Комплет за једносмерну струју	Комплет за једносмерну струју у Вашем рачунару! Саставите кола са отпорницима, сијалицама, батеријама и прекидачима. Мерите са реалистичним амперметрима и волтметрима. Прикажите коло као шематски дијаграм, или реалистично.	8
135	tr	Devre Yapım Kiti (sadece DC)	Bilgisayarındaki elektronik kit	8
136	ak	طقم ادوات انشاء دائرة كهربائية	انئ دائرتك والعب مع الالكترونات والمصباح المضئ	8
137	hr	Dizajniranje srujnih krugova (samo DC)	konstruirajte svoje strujne krugove i mjerite vrijednosti struja u virtualnom eksperimentu.	8
138	zh_TW	電路組裝套件(直流)	在電腦中的電子學套件！以電阻、燈泡、電池和開關組成電路。以擬真的電流錶與電壓錶進行測量。可以符號或圖像檢視電路圖。	8
139	pl	Zestaw symulacyjny obwodów - tylko napięcia stałe	Zestaw elektroniczny w Twoim komputerze. Buduj obwody elektryczne złożone Z baterii, przewodów, żarówek, wyłączników etc. Zmierz napięcia i prądy. Obejrz obwód, tak jak wygląda naprawdę oraz w postaci schematu.	8
140	en	Circuit Construction Kit (DC Only)	An electronics kit in your computer! Build circuits with resistors, light bulbs, batteries, and switches. Take measurements with the realistic ammeter and voltmeter. View the circuit as a schematic diagram, or switch to a life-like view.	8
141	it	Color Vision	Come funziona la visione  dei colori. Come funzionano i filtri per i colori.\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	9
142	sk	Farebné videnie	Ako vnímame farby a funkcia farebných filtrov	9
143	vi	Quan sát màu sắc	Cách quan sát màu sắc và lọc sắc	9
144	uk	Колір, що його бачить людина	How color vision and filters work	9
145	el	Έγχρωμη όραση	Λειτουργία της έγχρωμης όρασης και των φίλτρων	9
146	pt	Percepção de cor	Como funcionam a percepção de cor e os filtros de cores?	9
147	cs	Barevné vidění	Jak funguje barevné vidění a filtry	9
148	es	Visión del Color	Como funcionan la visión del color y los filtros	9
149	fr	Visions des couleurs	Comment voit-on les couleurs et comment fonctionne un filtre ?	9
150	de	Farbwahrnehmung	Wie die Farbwahrnehmung und die Filter funktionieren.	9
151	nl	KLEUR	Hoe kleuren en filters werken	9
152	ru	Цветовое зрение	Как работает цветовое зрение и светофильтры	9
153	zh_CN	光的混合	光的混合和滤光片的作用	9
154	hr	Kako vidimo boje?	Kako vidimo boje?	9
155	zh_TW	彩色視覺	彩色視覺和濾鏡是如何起作用的	9
156	pl	Widzenie kolorowe	Jak działają filtry; ich wpływ na widzenie	9
157	et	Värvuse tajumine	Kuidas töötavad nägemine ja valgusfiltrid?	9
158	en	Color Vision	How color vision and filters work	9
159	nl	Geleidingsvermogen	Waarom geleidt een mataal wel en een isolator niet?\nHoe werkt een LDR?\nDat ontdek je met deze simulatie.\nZet spanning op een geleider (metaal), isolator (plastic) of LDR (= lichtgevoelige geleider of fotogeleider). en onderzoek of er een stroom gaat lopen.\nOnderzoek of de snelheid en energie van de elektronen  verandert als je de spanning van de batterij verandert of met de lamp op het geleidende materiaal schijnt.\n	10
160	vi	Conductivity	Experiment with conductivity in metals, plastics and photoconductors. See why metals conduct and plastics don't, and why some materials conduct only when you shine a flashlight on them.	10
161	uk	Електропровідність	Experiment with conductivity in metals, plastics and photoconductors. See why metals conduct and plastics don't, and why some materials conduct only when you shine a flashlight on them.	10
162	el	Αγωγιμότητα	Διερευνήστε την αγωγιμότητα των μετάλλων , των μονωτών και των φωτοαγώγιμων υλικών. Δείτε γιατί τα μέταλλα άγουν ενώ τα πλαστικά όχι και γιατί κάποια υλικά άγουν μόνο όταν φωτοβολούνται 	10
163	pt	Conductividade	Estude a corrente elétrica aplicada a um condutor, isolante ou foto-condutor.	10
164	es	Conductividad	Experimente con la conductividad en metales, plásticos y fotoconductores. Vea por qué los metales conducen y los plásticos no, y por qué algunos materiales conducen sólo cuando se aplica una luz sobre ellos.	10
165	de	Leitfähigkeit	Experiment zur Leitfähigkeit in Metallen, Plastik und Photoleitern. Beobachte, warum Metalle leiten und Plastik nicht und, warum einige Materialien nur leiten, wenn Du Licht auf sie fallen läßt.	10
166	ru	Проводимость	Экспериментируйте с проводимостью в металлах, пластмассах и фотопроводниках. Узнайте, почему металлы проводят электричество, а пластмассы нет, и почему некоторые материалы проводят только если посветить на них ярким светом.	10
167	ro	Conductivitate	Experimenteaza conductivitatea metalelor, plasticelor si fotoconductorilor. Vezi de ce metalele conduc electricitatea si plasticele nu, si de ce unele conduc doar cand aplici lumina pe ele	10
168	zh_CN	物体导电性能实验	使用该实验可以观看金属、塑料和半导体材料导电性能（光敏电阻：当有光照射时电阻很小）。	10
616	ru	Quantum Wave Interference	Quantum Wave Interference	42
170	zh_TW	導電率	以金屬、塑膠和光導體進行導電率實驗。留意為什麼金屬能導電而塑膠則否，而某些材質只有在照光時才能導電。	10
171	pl	Przewodnictwo	Eksperymentuj ze zjawiskiem przewodnictwa elektrycznego metali, tworzyw sztucznych, fotoprzewodników. Zobacz, dlaczego metale przewodza prąd elektryczny a tworzywa sztuczne nie. Dlaczgo niektóre materiały przewodzą gdy są oświetlone?	10
172	en	Conductivity	Experiment with conductivity in metals, plastics and photoconductors. See why metals conduct and plastics don't, and why some materials conduct only when you shine a flashlight on them.	10
173	sk	Neónové svetlá a ostatné výbojky 	Skúmanie ako fungujú svetelné výbojky	11
174	uk	Неонова та інші газоразрядні лампи	An exploration of how discharge lamps work	11
175	pt	Neon Lights & Other Discharge Lamps	An exploration of how discharge lamps work	11
176	es	Luces de Neón y otras Lámparas de Descarga	Una exploración de cómo funcionan las lámparas de descarga.	11
177	de	Neonlicht und andere Entladungslampen	Eine Untersuchung wie eine Entladungslampe arbeitet.	11
178	nl	Neonverlichting en andere ontladingslampen	Een onderzoek naar de werking van een gasontladingslamp	11
179	zh_CN	氖灯和其它霓虹灯原理	解释霓虹灯的工作原理	11
180	hr	Neonsak svjetiljka & druge izbojne lampe	Objašnjenje izboja u plinovima i kako rade svjetiljke na tom principu	11
181	zh_TW	氖氣燈和其它氣體放電燈	探討氣體放電燈如何運作	11
182	pl	Lampa neonowa i inne lampy wyładowawcze	Badanie lamp wyładowawczych	11
183	en	Neon Lights & Other Discharge Lamps	An exploration of how discharge lamps work	11
184	es	Comida y Ejercicio	¿Cuántas calorías hay en sus comidas favoritas? ¿Cuánto ejercicio debe hacer para quemar estas calorías? ¿Qué relación hay entre las calorías y el peso? Explore estos temas escogiendo la dieta y el ejercicio y manteniendo la vista en su peso.	12
185	pt_BR	Comer & Exercitar-se	Quantas calorias têm em seus alimentos favoritos? Quanto exercício  você tem que fazer para queimar essas calorias? Qual é a relação entre calorias e peso? Explore estas questões optando por dieta e exercício físico e fique de olho em seu peso.	12
186	ar	التغذية والتمرين 	ما عدد السعرات الحرارية في الأطعمة المفضلة لديك؟ ما مقدار التمرين اللازم لحرق هذه السعرات؟ ما العلاقة بين الوزن والسعرات؟ إستكشف هذه القضايا عن طريق اختيار نظام غذائي معين وممارسة الرياضة بينما تحافظ على مراقبة وزنك	12
187	hu	Étkezés és tevékenység	Hány kalória van kedvenc élelmiszerekben? Mennyit kell mozogni, hogy ezt a kalóriát elégessük ? Milyen kapcsolat van a kalória és a tömeg között? Fedezze fel ezekre a kérdésekre választ diéta és a testmozgás segítségével, valamint kísérje figyelemmel a tömeget.\n\t\t	12
188	nl	Eten en bewegen	Hoeveel calorieën zitten er in jouw voedsel?  \nHoeveel beweging moet je hebben om deze calorieën weer te verbranden?\nWat is het verband tussen calorieën en gewicht?\nOnderzoek het door voedsel en beweging te kiezen en op ge gewicht te letten.	12
189	el	Διατροφή & Άσκηση	Πόσες θερμίδες υπάρχουν στα αγαπημένα σας φαγητά; Πόση άσκηση απαιτείται για να 'κάψετε' αυτές τις θερμίδες; Ποια είναι η σχέση μεταξύ θερμίδων και βάρους; Μελετήστε αυτά τα ζητήματα επιλέγοντας διατροφή και άσκηση και παρακολουθώντας το βάρος σας.	12
190	zh_TW	Eating & Exercise 飲食與運動	您知道您最愛的食物有多少熱量? 您必須做多少運動才能燃燒這些熱量? 熱量與體重之間有什麼關係? \n讓我們共同來探索這些議題, 藉由選擇飲食與運動來注意體重可能的變化.	12
191	en	Eating & Exercise	How many calories are in your favorite foods?How much exercise would you have to do to burn off these calories?What is the relationship between calories and weight?  Explore these issues by choosing diet and exercise and keeping an eye on your weight.	12
192	es	campo e	campo e	13
193	nl	Elveld	Elveld	13
194	it	CampoE	CampoE	13
195	vi	điện trường	điện trường	13
196	uk	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
197	el	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
198	pt	campo-e	Campo Elétrico	13
199	ro	camp e	camp e	13
200	ar	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
201	zh_CN	电场、电荷	这个实验探究电荷怎样形成电场和电场对电荷的作用	13
202	hr	Električno polje naboja	Kako naboji stvaraju električno polje i kako električno polje djeluje na naboje!	13
203	pl	Electric Field of Dreams	Zbadaj, jak pole elektryczne jest wytwarzane przy pomocy ładunków elektrycznych i jak te ładunki reaguja na pole elektryczne	13
204	zh_TW	電場	探索如何以電荷產生電場，及電荷與電場的相互作用。	13
205	en	Electric Field of Dreams	An exploration of how electric fields are created by charges, and how charges react to electric fields.	13
206	pt	Hockey Elétrico	Hockey Elétrico - baseado do trabalho de Ruth Chabay	14
207	es	Hockey Electrico	Hockey Electrico - derivado del trabajo de Ruth Chabay	14
208	fr	Electric Field Hockey - derived from work by Ruth Chabay	Electric Hockey - derived from work by Ruth Chabay	14
209	de	Elektrisches Hockey	Elektrisches Hockey von Ruth Chabay	14
210	nl	Elektrischveldhockey	Elektrischveldhockey  -  vrij naar het werk van Ruth Chabay	14
211	hu	Elektromos hoki	Elektromos hoki-Ruth Chabay	14
212	it	Hockey Elettrico	Hockey Elettrico - da un lavoro di Ruth Chabay	14
213	sk	Elektrický hokej	Electric Hockey - odvodený z práce Ruth Chabay	14
214	uk	Електричний хокей	Електричний хокей - derived from work by Ruth Chabay	14
215	el	Ηλεκτροστατικό Χόκεϋ	Ηλεκτροστατικό Χόκεϋ - δημιουργήθηκε από την εργασία της Ruth Chabay	14
216	ro	Hockey Electric	Hockey Electric - derivat din lucru de Ruth Chabay	14
619	hr	Kvantna interferencija valova	Kvantna interferencija valova	42
217	ar	لعبة الهوكي الكهربائية - مستلة من عمل روث تشابي	لعبة الهوكي الكهربائية - مستلة من عمل روث تشابي	14
218	zh_CN	电荷曲棍球	电荷曲棍球	14
219	hr	Električni hokej! Naboj umjesto paka!	Električni hokej! Naboj umjesto paka!	14
220	pl	Elektryczny hokej - zapożyczony z pracy Ruth Chabay	Elektryczny hokej - zapożyczony z pracy Ruth Chabay	14
221	zh_TW	電場曲棍球	電場曲棍球 - 學習自 Ruth Chabay 教授 (美國北卡州立大學物理系) 的同名創作	14
222	en	Electric Field Hockey - derived from work by Ruth Chabay	Electric Hockey - derived from work by Ruth Chabay	14
223	ru	Энергетический каток	Модель Энергетический каток описывает законы сохранения	15
224	de	Energieskatepark	Der Energieskatepark macht die Energieerhaltung anschaulich.	15
225	ja	Energy Skate Park　(スケート遊園地）	Energy Skate Park　はエネルギー保存則のシミュレーションです。	15
226	pl	Energia w Skate Parku	Animacja demonstruje zasadę zachowania energii	15
227	fr	SKATE PARC DU MONT-CHALATS	"Skate parc du Mont-Chalâts" simule la conservation de l'énergie mécanique.	15
228	es	Pista de patinar "Energía"	Pista de patinar "Energía" muestra la conservación de energía	15
229	et	Energia rula park	Energia rulapargi simulatsioon kirjeldab energia jäävuse seadust	15
230	nl	EnergieSkatepark	Deze simulatie van een skater toont behoud van energie aan.	15
231	it	Energy Skate Park	Simulazione di un parco di pattinaggio per descrivere il principio di conservazione dell'energia. \nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	15
232	sk	Energetický skejt park	Energy Skate Park Simulation depicts Energy Conservation	15
233	sl	Energy Skate Park	Energy Skate Park Simulation depicts Energy Conservation	15
234	uk	Energy Skate Park	Energy Skate Park Simulation depicts Energy Conservation	15
235	lt	Energijos riedlenčių parkas	Energijos riedlenčių parko simuliacijos pavaizdavimas energijos tvermę	15
236	iw	פארק שעשועים	הדמיה - פארק שעשועים 	15
237	pt	Trilha de Skate	Um estudo de conservação de energia mecânica numa trilha de Skate.	15
238	ar	منتزه الطاقة للتزحلق	محاكاة الطاقة في منتزه التزحلق لإيضاح حفظ الطاقة	15
239	hr	Zakon sačuvanja energije	Zakon sačuvanja energije za skejtere!	15
240	zh_TW	能量滑板競技場	能量滑板競技場模擬程式描述能量守恆定律	15
241	en	Energy Skate Park	Energy Skate Park Simulation depicts Energy Conservation	15
242	fr	Aimants et Electro-aimants	Explorez les interactions entre un aimant et une boussole. Découvrez comment utiliser une pile et du fil électrique pour faire un aimant. Pouvez-vous faire un aimant plus puissant? Pouvez-vous inverser le champ magnétique?	16
243	es	Imanes y Electroimanes	Explora las interacciones entre un imán y una brújula. ¡Descubre cómo puedes usar una pila y un alambre para hacer un imán! ¿Puedes hacer un imán fuerte? ¿Puedes invertir el campo magnético?	16
244	et	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
245	nl	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
246	hu	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
247	sk	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
248	sl	Magnet in elektromagnet	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
249	vi	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
250	lt	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
251	uk	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
252	pt	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
253	da	Magneter og elektromagneter	Forklarer sammenhængen mellem et kompas og en stangmagnet. Viser hvordan du kan bruge et batteri og en ledning til at frembringe en magnet!\nKan du gøre magneten stærkere?\nKan du få det magnetiske felt til at skifte retning?	16
254	tr	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
255	ru	Магниты и электромагниты	Исследуйте взаимодействия между компасом и постоянным магнитом. Откройте для себя как сделать магнит из батарейки и куска провода. Как сделать магнит сильнее? Как поменять направление магнитного поля?	16
256	de	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
257	el	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
620	pl	Kwatowa interferencja fal	Kwatowa interferencja fal	42
258	ar	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
259	it	Magneti ed elettromagneti	Esplora l'interazione tra bussola e magnete. Scopri come puoi usare una batteria e un filo per fare un magnete. Puoi fare un magnete più forte? Puoi fare invertire il campo magnetico?	16
260	ro	Magneti si Electromagneti	Experimenteaza interactiunile dintre o busola si un magnet bara. Descopera cum poti folosi o baterie si un fir pentru a face un magnet! Poti face un magnet mai puternic? Poti face campul magnetic reversibil?	16
261	hr	Magneti i Elektromagneti	Ispitaj međudjelovanje kompasa i magneta!	16
262	zh_CN	磁场和电流磁效应	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
263	ak	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
264	pl	Magnesy i elektromagnesy	Obserwuj oddziaływanie magnesu na igłę magnetyczną. Zobacz jak z drutu i bateri mozna zrobić magnes (elektromagnes)? Co zrobić by ten elektromagnes był silniejszy? Czy możesz zmienić bieguny tego magnesu?	16
265	zh_TW	磁鐵和電磁鐵	探索指南針和棒狀磁鐵的交互作用，發現你可使用電池和電線製作磁鐵。你可以製作較強的磁鐵嗎？你可以顛倒磁場的方向嗎？	16
266	en	Magnets and Electromagnets	Explore the interactions between a compass and bar magnet. Discover how you can use a battery and wire to make a magnet! Can you make it a stronger magnet? Can you make the magnetic field reverse?	16
267	fr	Alternateur	Produisez de l'èlectricité avec un aimant. Découvrez l'électromagnétisme en étudiant les aimants et comment les utiliser pour faire briller une ampoule.	17
268	es	Generador	¡Genera electricidad con una barra imantada! Descubre los principios físicos tras el fenómeno explorando los imanes y cómo puedes usarlos para hacer que una bombilla se encienda. 	17
269	et	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
270	nl	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
271	hu	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
272	sk	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
273	sl	Generator	Primer delovanja električnega generatorja, ki pretvarja mehansko energijo v električno. Generatorji velikih moči se nahajajo v elektrarnah. 	17
274	vi	Generator	Hãy tạo ra điện bằng một thanh nam châm. Hãy khám phá ý nghĩa vật lý ẩn sau các hiện tượng bằng cách dùng nam châm để thắp sáng một bóng đèn 	17
275	lt	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
276	uk	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
277	pt	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
278	da	Generator	Lav elektricitet ved hjælp af en stangmagnet!\nOpdag fysikken bag fænomenet ved at udforske magneter og hvordan du kan bruge dem til at få en pære til at lyse.	17
279	tr	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
280	ru	Генератор	Генерируйте электричесто с помощью постоянного магнита! Узнайте физику явления с помощью исследования магнитов и того, как можно их использовать, чтобы заставить лампочку светиться.	17
281	de	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
282	el	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
283	ar	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
284	it	Generatore	Genera elettricità con un magnete! Scopri la fisica che sta dietro alla generazione della corrente alternata, che permette di far funzionare anche la lampadina di casa tua.	17
285	ro	Generator	Genereaza electricitate cu un magnet bara! Descopera fizica din spatele fenomenului explorand cu magnetii si modul in care pot fi folositi pentru a face un bec sa lumineze.	17
286	hr	Generator	Proizvedi struju pomću štapićastog magneta! Pokušaj objasniti kako nastaje svjetlost u žarulji!	17
287	zh_CN	发电机	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
288	ak	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
289	pl	Generator	Wygeneruj nap[ięcie poruszając magnesem! Poznaj fizykę zjawisk elektromagnetycznych i jak je mozna zastosować do zaświecenia żarówki.	17
290	zh_TW	發電機	利用棒狀磁鐵發電！藉由探索磁鐵和如何讓燈泡發光的過程，發現現象後的物理學知識。	17
291	en	Generator	Generate electricity with a bar magnet! Discover the physics behind the phenomena by exploring magnets and how you can use them to make a bulb light.	17
292	fr	Aimant et boussole	Vous etes vous jamais demandé comment une boussole vous indiquait le nord? Explorez les intéractions entre une boussole et un aimant,  ajoutez la terre et trouvez de curieuses réponses! Changez l'intensité de l'aimant et voyez comment les choses changent à l'intérieur et à l'extérieur. Utilisez le teslamètre pour mesurer les variations du champ magnétique.	18
855	fr	Alunisseur	\N	66
293	es	Imán y Brújula	¿Te has preguntado cómo funciona una brújula para que apunte al Ártico? Explora la interacción entre una brújula y una barra imantada, y luego ¡añade la Tierra y encuentra respuestas sorprendentes! Varía la fuerza magnética y observa cómo cambian las cosas dentro y fuera. Usa el medidor de campo para medir los cambios del campo magnético.	18
294	et	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
295	nl	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
296	hu	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
297	sk	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
298	sl	Magnet in kompas	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
299	vi	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
300	lt	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
301	uk	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
302	pt	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
303	da	Magnet og kompas	Har du nogensinde undret dig over hvordan et kompas virker og hvorfor det peger mod Nord? Programmet forklarer sammenhængen mellem et kompas og en stangmagnet. Man kan vise jorden og se den overraskende sammenhæng.\nVarier magnetens styrke og se hvordan ting ændres, både indeni og udenfor.\nBrug feltmeter for at måle hvordan det magnetiske felt ændre sig.	18
304	tr	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
305	ru	Магнит и компас	Никогда не задумывались почему компас всегда показывает на север? Исследуйте взаимодействие между компасом и постоянным магнитом, затем добавьте Землю и найдите неожиданный ответ! Меняйте силу магнита и смотрите, что при этом меняется снаружи и внутри. используйте измеритель поля чтобы померять, как меняется напряженность магнитного поля.	18
306	de	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
307	el	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
308	ar	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
309	it	Magnete e Bussola	Ti sei mai chiesto come funziona una bussola e perché punta verso il polo Nord? Esplora le interazioni tra l'ago della bussola e un magnete. Varia l'intensità del magnete e guarda come cambiano le cose sia dentro che fuori. Usa il gaussmetro per misurare come variano i campi magnetici	18
310	ro	Magnet si Busola	V-ati intrebat vreodata cum functioneaza o busola pentru a va indica Arcticul? Experimenteaza interactiunile dintre busola si un magnet bara, apoi adauga Pamantul si afla raspunsul surprinzator! Variaza puterea magnetului si vezi cum se schimba lucrurile atat in interior, cat si in exterior. Foloseste masuratorul de camp pentru a masura schimbarile campului magnetic	18
311	hr	Magnet i Kompas	Kako magnetska igle pronalazi sjever?	18
312	zh_CN	磁体和指南针	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
313	ak	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
314	pl	Magnes i kompas	Czy kiedykolwiek zastanawiałeś sie, jak ustawi się igła kompasu na Arktyce? Sprawdź oddziaływanie magnesu na igłę magnetyczną, dodaj Ziemię i... uzyskasz zaskakująca odpowiedź. Zmieniaj "siłę" magnesu i obserwuj te zmiany wewnątrz i na zewnątrz. Uzywająć miernika pola magnetycznego zbadaj, jak zmienia 	18
315	zh_TW	磁鐵和指南針	納悶指南針為何指引你朝向北極？探索指南針和棒狀磁鐵的交互作用，再加入地球此因素，發現驚奇的答案！改變磁鐵的強度，並觀察內部和外部磁場的變化，使用磁場儀器測量磁場的改變。	18
316	en	Magnet and Compass	Ever wonder how a compass worked to point you to the Arctic? Explore the interactions between a compass and bar magnet, and then add the earth and find the surprising answer! Vary the magnet's strength, and see how things change both inside and outside. Use the field meter to measure how the magnetic field changes.	18
317	fr	Labo d'électromagnétisme de Faraday	Démonstrations d'application de la loi de Faraday	19
318	es	Laboratorio electromágnetico de Faraday	Juega con una barra imantada, una espiral de alambre y una bombilla para hacer encender la bombilla y aprender sobre la ley de Faraday. Explora cómo puedes hacer que la bombilla brille mas o menos. Juega también con electroimanes, generadores y transformadores!	19
319	et	Faraday's Electromagnetic Lab	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
320	nl	Faraday's Elektromagnetisch Lab	Deze simulatie maakt de wet van Faraday aan	19
321	hu	Faraday elektromágnes laborja	Faraday törvényének bemutatása	19
322	sk	Faradayove magnetické laboratórium	Ukážka použitia Faradayovho zákona 	19
323	sl	Faraday's Electromagnetic Lab	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
324	vi	Phòng thí nghiêm của FARADAY	Hãy chơi đùa với nam châm, cuộn dây, nam châm điện, máy biến thế,  thắp sáng bóng đèn... đồng thời học thêm về các định luật Faraday. Thử tìm cách thay đổi độ sáng bóng đèn!	19
325	lt	Faradėjaus elektromagnetinė labaratorija	Demonstruoja  Faradėjaus dėsnio taikymą	19
326	uk	Лабораторія електромагнітизму	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
327	pt	Laboratório de Eletromagnetismo	Demonstra a aplicação das Leis de Faraday	19
328	da	Faradays elektromagnetiske laboratorie.	Leg me en stangmagnet, spoler og  pærer og få pærer til at lyse og lær mere om Faradays lov. Programmet viser hvordan du kan få pærer til at lyse stærkere eller svagere. Man kan også lege med elektromagneter, generatorer og transformatorer!	19
329	tr	Faraday Elektromanyetik Lab.	Faraday Kanunu Uygulama Gösterisi	19
330	ru	Электромагнитная лаборатория Фарадея	Поиграйтесь с постоянным магнитом, катушкой провода и лампой, чтобы заставить лампочку светиться и узнать о законе Фарадея. Исследуйте, как можно сделать свечение лампы ярче или слабее. Также поиграйтесь с электромагнитами, генераторами и трансформаторами.	19
331	de	Faradays Elektromagnetisches Labor	Zeigt Anwendungen des Induktionsgesetzes.	19
332	el	Εργαστήριο Ηλεκτρομαγνητισμού Faraday	Επίδειξη των εφαρμογών του Νόμου του Faraday	19
333	ar	مختبر فراداي للمغناطيس الكهربائي	اثبات تطبيق قانون فاراداي	19
334	it	Laboratorio elettromagnetico di Faraday	Divertiti con magneti e bobine per accendere una lampadina e imparare la legge di Faraday. Esplora come puoi rendere la luce della lampadina più o meno brillante. Gioca con elettromagneti, generatori e trasformatori imparando come funzionano.	19
335	ro	Laboratorul Electromagnetic al lui Faraday	Joaca-te cu un magnet bara, fire serpentina si un bec pentru a face un bec sa lumineze si sa inveti despre legea lui Faraday. Experimenteaza cum poti face becul sa lumineze mai puternic sau mai slab. De asemenea joaca-te cu electromagneti, generatori si transformatori!	19
336	hr	Faradayeva elektromagnetska indukcija	Igranje s magnetom, zavojnicom i žaruljom može produbiti vaše razumijevanje elektromagnetske indukcije. Otkrijte kao se sjaj žarulje može povećati!	19
337	zh_CN	法拉第电磁感应实验	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
338	ak	مختبر فراداي للمغناطيس الكهربائي	اثبات تطبيق قانون فاراداي	19
339	pl	Elektromagnetyczne laboratorium Pana Faradya	Eksperymentuj z magnesem, zwojnicą i żarówką tak by żarówka się świeciła. Poznaj prawo Faraday'a. Zobacz, kiedy żąrówka świeci jaśniej lub ciemniej. Zbadaj działanie elektromagnesów, generatorów i transformatorów!	19
340	zh_TW	法拉第的電磁實驗室	利用棒狀磁鐵、線圈和燈泡等物品，使燈泡發光並學習法拉弟定律，研究如何使燈泡更亮或變暗。同樣地，利用電磁鐵、發電機和變壓器來進行實驗！	19
341	en	Faraday's Electromagnetic Lab	Play with a bar magnet, wire coil, and bulb to make a light bulb glow and learn about Faraday's law. Explore how you can make the bulb glow brighter or dimmer. Also play with electromagnets, generators and transformers!	19
342	ja	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
343	sk	Sila-1d	Sila-1d	20
344	vi	Các lực trên 1 trục tọa độ	Hãy khảo sát các lực khi bạn cố gắng đẩy một cái tủ hồ sơ. Tạo ra một lực tác dụng, quan sát các lực ma sát, hợp lực tác dụng vào tủ. Sau đó vẽ đồ thị theo thời gian của lực tác dụng, tọa độ vật, vận tốc và gia tốc. Và xem hình vẽ của tất cả các lực tác dụng vào vật nặng (gồm cả trọng lực và các lực khác).	20
345	sl	sila -1d	sila -1d	20
346	uk	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
347	el	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
348	pt	Aplicação de Forças em uma Dimensão	Estude as forças presentes quando você tenta arrastar um objeto.	20
349	it	Forze in una dimensione	Esplora le forze al lavoro mentre provi a spingere un mobile. Imprimi una forza e vedi la forza di attrito e la forza risultante che agiscono sul mobile. I grafici mostrano le forze, la posizione, la velocità, l'accelerazione in funzione del tempo. Osserva il diagramma delle forze.	20
350	es	Fuerzas en 1 Dimensión	Explore las fuerzas en el trabajo cuando tratas de empujar un archivador. Crea una fuerza aplicada y observa la fuerza de fricción resultante y la fuerza total sobre el cajón. Los diagramas muestran las fuerzas, posición, velocidad y aceleración frente al tiempo. Vea un Diagrama de Cuerpo Libre de todas las fuerzas (incluyendo las fuerzas gravitacional y normal).	20
351	et	jõud-1d	jõud-1d	20
352	nl	Krachten langs een lijn	Krachten langs een lijn	20
353	hr	Koncept sile	Otkrijte djelovanje sile. Kada će sila prouzročiti gibanje, a kada neće!	20
354	zh_TW	一維空間的作用力	當你推一個滿滿的檔案櫃時，探討力的作用方式。施一作用力在檔案櫃上，並查看作用在檔案櫃的摩擦力和合力。顯示力、位置、速度和加速度對時間的圖表。觀察所有力的自由體受力圖（包含重力和正向力）。	20
355	en	Forces in 1 Dimension	Explore the forces at work when you try to push a filing cabinet. Create an applied force and see the resulting friction force and total force acting on the cabinet. Charts show the forces, position, velocity, and acceleration vs. time. View a Free Body Diagram of all the forces (including gravitational and normal forces).	20
356	pt	Séries de Fourier: Fazendo Ondas	Aprenda a gerar ondas de todas as formas pela superposição de senos e cossenos. Gere ondas no tempo e no espaço e realize a medida do comprimento de onda e do período. Observe a alteração das ondas pela mudança de  amplitude dos harmônicos e compare as expressões matemáticas de sua decomposição.	21
357	es	Fourier: Fabricacion de Ondas	Aprenda cómo hacer las ondas de todas las diversas formas agregando encima de senos o de cosenos. Haga las ondas en espacio y tiempo y mida su longitud de onda y período. Vea cómo cambiar las amplitudes de diversos armónicos cambia las ondas. Compare diversas expresiones matemáticas para sus ondas.	21
358	it	Fourier: Costruire le onde	Impara a costruire onde di tutte le forme aggiungendo funzioni seno e coseno. Crea onde nello spazio e nel tempo e misura la loro lunghezza d'onda e il loro periodo. Vedi come, cambiando l'ampiezza delle diverse armoniche, cambiano le onde risultanti. Confronta le diverse espressioni matematiche che descrivono le onde.	21
359	el	Fourier: Δημιουργία Κυμάτων	Μάθετε πως να δημιουργείτε κυματομορφές όλων των ειδών προσθέτοντας ημίτονα και συνημίτονα. Δημιουργείστε κύματα στο χώρο και το χρόνο και μετρήστε τα μήκη κύματος και τις περιόδου. Παρατηρήστε πως αλλάζοντας τα πλάτη των διαφορετικών αρμονικών μεταβάλλονται και τα κύματα. Συγκρίνετε διαφορετικές μαθηματικές εκφράσεις για τα κύματά σας	21
360	nl	Boventonen	Leer hoe je golven van allerlei vormen kunt maken door sinussen en cosinussen op te tellen. Zie wat boventonen doen.	21
361	hr	Kompozicija valova (Fourierova sinteza)	Naučite kako napraviti različite valove komponirajući samo sinusne i kosinusne oblike. Napravi val i izmjeri njegovu valnu dužinu i period. Istraži kako mijenjanjem amplituda različitim harmonicima mjenjamo valni oblik.	21
362	zh_TW	傅力葉：產生一個波	學著如何由一個正弦與餘弦的函數之疊加來產生一個波。測量波長及週期，並改變各不同諧波的振幅。比較數學展開式的不同。	21
363	pl	Fourier: Konstruowanie fal	Naucz się, jak konstruować fale o różnym kształcie poprzez dodawanie prostych przebiegów sinusoidalnych/cosinusoidalnych. Zbadaj zmienność przestrzenną, jak i czasową fal. Zobacz, jak zmiany amplitudy harmonicznychh wpływają na kształt fali. Porównaj uzyskane wyniki z ich zapisem matematycznym. To naprawdę ciekawa symulacja, która wiele cię nauczy.	21
364	en	Fourier: Making Waves	Learn how to make waves of all different shapes by adding up sines or cosines. Make waves in space and time and measure their wavelengths and periods. See how changing the amplitudes of different harmonics changes the waves. Compare different mathematical expressions for your waves.	21
365	hu	Gleccserek	Állítsa be a havazást ill. a hőmérsékletet a gleccser növekedéséhez és csökkenéséhez. Használjon tudományos mérőeszközöket a sűrűség, sebesség, jegesedés megállapításához.	22
366	es	Glaciares	Ajuste la caida de nieve y la temperatura para ver crecer o encoger al glaciar. Use herramientas científicas para medir el espesor, la velocidad y el estiramiento del glaciar.	22
367	sk	Ľadovec	Zmeňte množstvo snehu na horách a teplotu na úrovni mora a sledujte či sa ľadovec zväčšuje alebo zmenšuje. Použite vedecké prístroje na zmeranie hrúbky, rýchlosti a ročnej bilancie ľadovca. 	22
368	nl	Gletsjers	Pas sneeuwval en temperatuur op zeeniveau aan en onderzoek hoe een gletsjer groeit en afsmelt. Gebruik de instrumenten om de dikte, positie, snelheid en ijsoverschot te meten.\nAccumulatie (aangroei) vindt plaats boven de evenwichtslijn en ablatie (afsmelting) onder de evenwichtslijn.\n	22
369	it	Ghiacciai	Regola la nevicata in montagna e la temperatura per osservare come il ghiacciaio cresce e si ritrae. Usa gli strumenti scientifici per misurare spessore, velocità e il bilangio di massa del ghiacciaio.	22
370	hr	Ledanjaci	Uskladi snježne padavine u gorju i temperaturu da bi prikazao topljenje ili stvaranje ledenjaka. Izmjeri debljinu, brzinu i prinos leda u ledenjaku.	22
371	el	Παγετώνες	Ρύθμιση χιονόπτωσης και θερμοκρασίας βουνού για να δείτε τον παγετώνα να μεγαλώνει και να συρρικνώνεται	22
372	zh_TW	Glacier 冰河	調整山區的降雪及溫度, 看看冰河會成長還是倒退. 可以使用科學工具去測量厚度, 速度, 冰河收支計算等	22
373	pl	Lodowce	Ustaw opady śniegu w górach oraz temperaturę, by zobaczyć jak lodowiec rośnie i kurczy się. Urzyj narzędzi naukowych by zmierzyć grubość lodowca, jego prędkość oraz  budget (równowagę między przyrostem a zanikiem lodu)	22
374	en	Glaciers	Adjust mountain snowfall and temperature to see the glacier grow and shrink. Use scientific tools to measure thickness, velocity and glacial budget.	22
375	nl	Het broeikaseffect	Bij deze simulatie vergelijk je de atmosfeer met broeikasgassen met een glazen broeikas.  Je ontdekt zo de relatie tussen de concentratie  broeikasgassen, het zonlicht en het opwarmen van de atmosfeer.	23
376	ja	大気の温室効果	温室効果ガス、大気、太陽光が\nどのような相互作用するかを\n探究するシミュレーション。	23
377	sk	Skleníkový efekt	Simulácia skúmajúca ako skleníkové plyny \npôsobia v atmosfére na slnečné svetlo.	23
378	fi	kasvihuone	kasvihuone	23
379	uk	The Greenhouse Effect	Just how do greenhouse gases change the climate? Select the level of atmospheric greenhouse gases during an ice age, in the year 1750, today, or some time in the future and see how the Earth's temperature changes. Add clouds or panes of glass.	23
380	el	Θερμοκήπιο	Θερμοκήπιο	23
381	hr	Učinak staklenika	Kako staklenički plinovi utječu na klimu? Istražite kako se mijenja temperatura Zemljine atmosfere u ovisnosti o emisiji stakleničkih plinova!	23
382	es	El Efecto de Invernadero	Una simulación para explorar cómo\ninteractúan los gases de invernadero con la atmósfera y la luz del sol.	23
383	zh_TW	溫室效應	溫室效應和大氣層及陽光交互作用的模擬實驗	23
384	zh_CN	温室效应	温室效应气体与大气层及阳光的相互作用模拟，温室气体是怎样改变气候的，通过增加云块或玻璃层，选择冰川期、公元1750年、现在或将来看地球温度的变化	23
385	pl	Efekt cieplarniany	Jak gazy cieplarniane zmieniaj ą klimat? Ustaw poziom gazów cieplarnianych w atmosferze w czasie epoko lodowcowej, w roku 1750, dzisiaj oraz kiedykolwiek w przyszłości. Zobacz, jaki to ma wpływ na temperaturę atmosfery Ziemi. Dodaj chmury lub szklane panele	23
386	et	Kasvuhooneefekt	Kuidas muudavad kasvuhoonegaaside kontsentratsiooni muutus kliimat? Vali atmosfäär (jääaeg, aasta 1750, tänapäev või tulevik), vaata, kuidas muutub Maal olev temperatuur. Liisa süsteemile pilvi või klaaspaneele!	23
387	en	The Greenhouse Effect	Just how do greenhouse gases change the climate? Select the level of atmospheric greenhouse gases during an ice age, in the year 1750, today, or some time in the future and see how the Earth's temperature changes. Add clouds or panes of glass.	23
388	el	Μοντέλα του Ατόμου του Υδρογόνου	Πως κατάφεραν οι επιστήμονες να ανακαλύψουν τη δομή των ατόμων χωρίς να τα βλέπουν; Δοκιμάστε διαφορετικά μοντέλα ακτινοβολώντας τα άτομα. Ελέγξτε πως η πρόβλεψη για το μοντέλο ταιριάζει με τα πειραματικά αποτελέσματα.	24
389	pt	Modelos do Átomo de Hidrogênio	Como será que os cientistas conseguem descobrir a estrutura dos átomos sem vê-los? Experimente diferentes modelos de átomos fazendo incidir luz sobre eles e verificando o resultado e compatrando-os com as previsões.	24
390	es	Modelos del Átomo del Hidrógeno	¿Cómo calcularon los científicos  la estructura de los átomos sin mirarlos? Pruebe diversos modelos disparando luz al átomo. Compruebe cómo la predicción del modelo se compara a los resultados experimentales.	24
391	de	Modelle des Wasserstoffatoms	Wie erkannten die Wissenschaftler den Aufbau der Atome, ohne sie sehen zu können? Probiere verschiedene Modelle aus durch Bestrahlung mit Licht. Überprüfe, ob die Vorhersagen des Modells den experimentellen Ergebnissen entsprechen.	24
392	nl	Modellen van een waterstofatoom	Hoe kwamen wetenschappers achter de structuur van de atomen zonder dat ze atomen konden zien?\nTest de verschillende modellen van het waterstofatoom door ze te beschieten met licht.  Vergelijk steeds de voorspelling met het resultaat van je experiment.\n	24
393	ja	水素原子モデル。	見えない原子の形を科学者はどうやって見つけたのでしょう。原子に光を当ててそれぞれのモデルを見てみましょう。　モデルから予測されることと実験結果が、どのくらい合うかみましょう。	24
394	hu	A hidrogén atom modelljei	Hogyan képzelték el a tudósok az atom szerkezetét? Próbáld ki a különböző modelleket fotonok kilövésével. Figyeld meg hogyan viszonyul a modellek jóslata a kísérleti tapasztalatokhoz.	24
395	sk	Model atómu vodíka	Ako vedci určili stavbu atómu bez možnosti vidieť do atómu? Vyskúšajte rôzne modely atómu pri ožarovaní svetlom. Skontrolujte ako sa predpoklad správania modelu zhoduje s experimentálnymi výsledkami.	24
396	uk	Моделі атома водню	How did scientists figure out the structure of atoms without looking at them? Try out different models by shooting light at the atom. Check how the prediction of the model matches the experimental results.	24
397	ru	Модели атома водорода	Как учёные определяют внутреннюю структуру атомов не глядя на них? Опробуйте разные модели путём облучения их светом. Проверьте, как теоретические предсказания совпадают с экспериментальными результатами.	24
398	fr	Modèles de l'atome d'Hydrogène	Comment les scientifiques ont-ils conçu la structure des atomes sans les voir? Testez différents modèles en éclairant un atome. Confrontez la validité de la prédiction aux résultats expérimentaux.	24
399	it	Modelli dell'atomo di Idrogeno	Come fecero gli scienziati a immaginarsi la struttura atomica senza vederla? Prova diversi modelli mandando della luce agli atomi. Verifica se la previsione si adatta ai risultati sperimentali.	24
400	hr	Model Atoma Vodika	Kako znanstvenici iznalaze strukturu submikroskopskih čestaca? Pokušajte testirate različite modele submikroskopske strukture različitim pretpostavkama!	24
401	zh_TW	氫原子的模型	科學家如何在看不見原子的情況下找出原子內的結構呢？方法為觀察光線照射原子的實驗，而對照從不同原子模型所做的預測來看那一個最符合實驗的結果。	24
402	ar	نماذج من ذرّة الهيدروجين	كيف فهم العلماء تركيب الذرّات بدون مشاهدة ذلك ؟ اختر نماذج مختلفة بإطلاق الضوء على الذرّة. تحقق كيف تم التنبؤ بالنموذج حتى يجاري النتائج التجريبية.	24
617	es	Interferencia de Onda Cuántica	Interferencia de Onda Cuántica	42
403	pl	Modele atomu wodoru	Jak uczeni poznają strukturę atomu nie mogąc obejrzeć go bezpośrednio? Wypróbuj różne modele atomu stosując rózne oswietlenia. Sprawdź jak przewidywania danego modelu sprawdzają sie z doświadczeniem	24
404	en	Models of the Hydrogen Atom	How did scientists figure out the structure of atoms without looking at them? Try out different models by shooting light at the atom. Check how the prediction of the model matches the experimental results.	24
405	es	Propiedades del gas	Bombée moléculas de gas en una caja y vea qué ocurre cuando cambia el volumen, añade o quita calor, cambia la gravedad, y más. Mida la temperatura y presión, y descubra cómo varían las propiedades del gas en relación unas con otras.	25
406	fr	Propriétés d'un gaz parfait	Propriétés d'un gaz parfait	25
407	de	Gaseigenschaften	Pumpe Gasmoleküle in einen Kasten und schaue, was geschieht, wenn Du das Volumen änderst, Wärme zu- oder abführst, das Gewicht änderst und mehr. Messe die Temperatur und den Druck und erforsche wie die Eigenschaften des Gases sich gegenseitig verändern. 	25
408	et	Gas Properties	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
409	nl	Eigenschappen van gassen	Pomp gas in een ruimte en kijk wat er gebeurt als je het volume verandert, warmte toe- of afvoert, de zwaartekracht verandert, enz. \nMeet de temperatuur en druk en ontdek hoe de eigenschappen van een gas van elkaar afhangen.	25
410	hu	gázrészecskék	Pumpálj gázmolekulákat a dobozba és nézd mi történik, ha változtatod a térfogatot, növeled, vagy csökkented a hőmérsékletet, változtatod a gravitációt, stb. Mérd a hőmérsékletet és a nyomást és figyeld meg hogyan reagálnak a gáz részecskéi.	25
411	sk	Vlastnosti plynu	Pumpujte plyn do komory a sledujte jeho stav ak zmeníte objem, pridáte alebo odoberiete teplo, zmeníte gravitáciu alebo ďalšie parametre. Zmerajte teplotu a tlak plynu a zistite ako tieto veličiny spolu  súvisia.	25
412	sl	Lastnosti plina	S tlačilko spustimo plin v posodo in opazujemo spremembe pri spreminjaju prostornine, gravitacije, tlaka, ...	25
413	el	Ιδιότητες Αερίου	Αντλήστε μόρια αερίου σε ένα κουτί και δείτε τι συμβαίνει καθώς μεταβάλλετε τον όγκο η/και αφαιρείτε θερμότητα, μεταβάλλετε την ένταση της βαρύτητας κ.λ.π. Μετρήστε την θερμοκρασία και την πίεση και ανακαλύψτε πως μεταβάλλονται οι ιδιότητες του αερίου η μια σε σχέση με την άλλη. 	25
414	tn	Gase e e seng ya Nnete & Bueyensi (Ideal Gas & Buoyancy)	Gase_e e seng ya nnete_&_Bueyensi	25
415	pt	Propriedades do gás	Bombeie moléculas de gás para um reservatório e veja  que acontece quando há alteração do volume. adicione e remova calor, altere a gravidade, etc.Verifique as medidas de temperatura e pressão, e descubra como as propriedades do gás variam em relação aos outros gases.	25
416	ru	Свойства газа	Накачайте молекул газа в коробку и смотрите, что поменяется при изменении её объёма, нагреве или охлаждении, изменении силы тяжести, и т.д. Измерьте температуру и давление и узнайте, как свойства газа меняются по отношению друг к другу.	25
417	cs	Vlastnosti plynu	\t\nNapumpujte molekuly plynů do uzavřeného prostoru a podívejte se, co se stane, pokud budete měnit tlak, přidávat nebo odebírat teplo, měnit gravitaci, a další. Změřte teplotu a tlak, a zjistěte, jak se vlastnosti zemního plynu  liší ve vztahu k sobě navzájem.	25
418	nb	Gassegenskaper	Pump gassmolekyler inn i en boks, og se hva som skjer når du endrer volum, legger til eller fjerner varme, endrer tyngdekraft og mer. Mål temperatur og trykk, og utforsk hvordan egenskapene i gassen endres i relasjon til hverandre.	25
419	zh_TW	氣體特性	放置氣體分子於箱子內，並改變體積、溫度、重力及其它變因，同時測量溫度和壓力，觀察這些變因和氣體的溫度及壓力的關係。	25
420	ar	خواص الغاز	ضخ جزيئات غاز في صندوق وراقب ماالذي يحصل عندنا تغير الحجم، تضيف أو تزيل الحرارة، تغير الجاذبية،وغير ذلك. قس درجة الحرارة والضغط واكتشف كيف تتغير خواص الغاز بارتباطها مع بعض.	25
421	zh_CN	气体属性	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
422	hr	Svojstva Plinova	Punjenjem posude plinom, grijanjem plina u posudi i dodavanjem utjecaja sile teže mijenjajte karakteristične veličine: volumen, tlak i temperaturu plina. Izmjerite vrijednosti tih veličina! Oktrijte pravilnostii!	25
423	pl	Właściwości	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
424	en	Gas Properties	Pump gas molecules to a box and see what happens as you change the volume, add or remove heat, change gravity, and more.  Measure the temperature and pressure, and discover how the properties of the gas vary in relation to each other.	25
425	es	Globos y flotación	Experimente con un globo de helio, un globo de aire caliente, o una esfera rígida llena de gases distintos. Descubra qué es lo que hace flotar a algunos globos y hundirse a otros.	26
426	fr	Ballons et Flottabilité	Ballons et Flottabilité	26
427	de	Ballons & Auftrieb	Experimente mit einem Heliumballon, einem Heißluftballon oder einer festen Hohlkugel gefüllt mit verschiedenen Gasen. Entdecke, warum manche Ballons steigen und andere sinken.	26
428	et	Balloons & Buoyancy	Experiment with a helium balloon, a hot air balloon, or a rigid sphere filled with different gases. Discover what makes some balloons float and others sink.	26
429	nl	Ballonnen en opwaartse kracht	Experimenteer met heteluchtbalonnen, heliumballonnen en bollen met verschiillende gassen. Ontdek waardoor ballonnen stijgen en dalen.	26
430	hu	Léggömb és felhajtóerő	Kísérlet hélium léggömbbel, hőlégbalonnal, vagy merev tér megtöltése különböző gázokkal. Fedezd fel melyik ballon  lebeg vagy süllyed.	26
431	sk	Balóny a  vztlak	Pokusy s balónom naplneným héliom , teplým vzduchom, alebo pevnou guľou naplnenou rôznymi plynmi. Objavte prečo niektoré balóny sa vznášajú a iné klesajú.	26
856	hr	Mjesečeva sonda	\N	66
432	sl	Balon in vzgon	Simulacija kaža kaj omogoča določenim plinom da lebdijo in drugim da potonejo. 	26
433	el	Μπαλόνια & Άνωση	Πειραματιστείτε με ένα μπαλόνι Ηλίου, ένα μπαλόνι θερμού αέρα ή μια στερεή σφαίρα γεμάτα με διαφορετικά αέρια. Ανακαλύψτε τι κάνει κάποια μπαλόνια να επιπλέουν και άλλα να βυθίζονται	26
434	tn	Gase e e seng ya Nnete & Bueyensi (Ideal Gas & Buoyancy)	Gase_e e seng ya nnete_&_Bueyensi	26
435	pt	Balões e Flutuação	Faça experiências com balões de hélio, balão de ar quente, ou uma esfera rigida cheia com gases diferentes. Descubra o que faz alguns balões flutuarem e outros cairem.	26
436	ru	Воздушные шары и подъёмная сила	Экспериментируйте с шаром с гелием, шаром с горячим воздухом и с жесткой сферой, заполненной различными газами. Узнайте, что заставляет один шар лететь, а другой падать вниз.	26
437	cs	Balóny a Archimedův zákon	Pokus s balónem naplněným héliem, horkým vzduchem nebo pevná koule která se může plnit různými plyny. Zjistit jak se tyto balóny chovají, kdy klesají nebo se vznáší v prostoru.	26
438	nb	Ballonger og oppdrift	Eksperimenter med en heliumballong, en varmluftsballong eller en rigid sfære fylt med ulike gasser. Finn ut hva som gjør at enkelte ballonger stiger, mens andre synker.	26
439	zh_TW	氣球和浮力	以氦氣球，熱氣球或中空球體中填充不用的氣體來做實驗；探討是什麼因素讓一些氣球會飄浮而其它則否。	26
440	ar	المناطيد والطفو	جرب مع منطاد الهيليوم ومنطاد الهواء الساخن أو كرة قاسية مليئة بغازات مختلفة. اكتشف ماالذي يجعل بعض المناطيد تطفو والأخرى تغرق.	26
441	zh_CN	气球和浮力	以氦气球，热气球或中空球体中填充不用的气体來做实验；探讨是什么因素让一些气球会漂浮而其它则不能。	26
442	hr	Plinski Zakoni	Eksperimenti s balonima napunjenim helijem ili toplim zrakom. Plin u boci. Otkrijte što je uzrok da nešto tone a nešto pliva!	26
443	pl	Balony i siła wyporu	Experiment with a helium balloon, a hot air balloon, or a rigid sphere filled with different gases. Discover what makes some balloons float and others sink.	26
444	en	Balloons & Buoyancy	Experiment with a helium balloon, a hot air balloon, or a rigid sphere filled with different gases. Discover what makes some balloons float and others sink.	26
445	es	Reacciones reversibles	Observe cómo transcurre una reacción en el tiempo. ¿Cómo afecta la energía total a la velocidad de reacción? Cambie la temperatura, altura de barrera y energías potenciales. Registre concentraciones y tiempos para extraer los parámetros de Arrhenius. Es mejor usar esta simulación bajo guía de un maestro pues presenta una analogía de reacciones químicas.	27
446	fr	Diffusion d'un gaz avec barrière	Diffusion d'un gaz avec barrière	27
447	de	Reversible Reaktionen	Beobachte den Reaktionsverlauf. Wie beeinflusst die Gesamtenergie die Reaktionsgeschwindigkeit? Ändere die Temperatur, die Barrierehöhe und die potentiellen Energien. Zeichne den Konzentrationsverlauf auf, um die Reaktionsgeschwindigkeitskoeffizienten zu bestimmen. Stelle temperaturabhängige Überlegungen an, um die Arrhenius-Parameter zu gewinnen. Diese Simulation wird am besten mit Lehreranleitung benutzt, weil sich eine Analogie zu chemischen Reaktionen zeigt. 	27
448	et	Reversible Reactions	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
449	nl	Omkeerbare reacties	Met deze simulatie kun je de het onderwerp reactiesnelheid verduidelijken. \nKijk naar wat er gebeurt tijdens een reactie. Hoe beïnvloedt de totale energie de reactiesnelheid?\nVerander de temperatuur, activeringsenergie en de potentiële energie en let op de snelheid van de reactie. \nVraag je docent om hulp als je er niet uit komt.	27
450	hu	Megfordítható reakciók	Figyeld a reakció időbeli lefolyását. Hogyan befolyásolja a teljes energia a reakció sebességét? Változtasd a hőmérsékletet, a gát magasságát és a helyzeti energiát. Ezt a szimulációt ajánlott tanári magyarázattal használni, ill. kémiai reakciókkkal párhuzamosan bemutatni.	27
451	sk	Vratné reakcie	Pozorujte priebeh reakcie. Ako ovplyvňuje celková energia priebeh reakcie? Zmeňte teplotu, výšku bariéry a potenciálnu energiu. Zaznamenajte koncentráciu a čas reakcie pre určenie koeficientu reakcie. Skúmajte závislosť medzi teplotou a Arrheiniusovým koeficientom.  	27
452	sl	Ponovi predvajanje	Dogodke snemamo kot film, ki si ga kasneje lahko še enkrat ogledamo. Simulacija je primernejša kot predstavitev iz strani učitelja, ker je veliko parametrov, ki jih lahko speminjamo.	27
453	el	Αντιστρεπτές Αντιδράσεις	Παρακολουθείστε την πρόοδο μιας αντίδρασης με τον χρόνο. Πως επηρρεάζει η ολική ενέργεια τον ρυθμό της αντίδρασης; Μεταβάλλετε το ύψος του φραγμού θερμοκρασίας και των δυναμικών ενεργειών. Καταγράψτε συγκεντρώσεις και χρόνο ώστε να εξάγετε συντελεστές ρυθμού. Πραγματοποιήστε μελέτες εξαρτόμενες από τη θερμοκρασία για να εξάγετε τις παραμέτρους Arrhenius. Αυτή η προσομοίωση χρησιμοποιείται καλύτερα με την παρουσία του εκπαιδευτικού διότι παρουσιάζει αναλογία με χημικές αντιδράσεις.  	27
454	tn	Gase e e seng ya Nnete & Bueyensi (Ideal Gas & Buoyancy)	Gase_e e seng ya nnete_&_Bueyensi	27
455	pt	Reações reversíveis	Observe uma reação ao longo do tempo. Como a energia total afeta a taxa de reação? Varie a temperatura, a altura da barreira e a energia potencial. Registre concentrações e o tempo para extrair os coeficientes de taxa. Faça estudos de dependência de temperatura para extrair os parâmetros de Arrhenius. Esta simulação é melhor utilizada com a orientação de professores porque ela apresenta uma analogia de reações químicas.	27
503	el	Κίνηση σε 2Δ	Διερεύνηση για το πως σχετίζονται επιτάχυνση και ταχύτητα.	31
504	pt	Movimento em 2D	Um estudo da relação entre velocidade e aceleração.	31
505	nl	Beweging in een vlak	Onderzoek de afhankelijkheid tussen snelheid en versnelling.	31
456	ru	Обратимые реакции	Пронаблюдайте как протекает реакция со временем. Как полная энергия влияет на скорость протекания реакции? Меняйте температуру, высоту потенциального барьера и потенциальные энергии. Запишите концентрации и время чтобы получить постоянные скорости протекания реакции. Проведите измерения, зависящие от температуры, чтобы получить параметры Аррениуса. Эту демонстрацию лучше использовать под руководством учителя, поскольку она представляет аналогию химическим реакциям.	27
457	cs	Vratné reakce	Sledujte reakce jak postupují v čase.\nTuto simulaci je nejlépe sledovat  pod vedením učitelů, protože se zde pracuje i s chemickými reakcemi.	27
458	nb	Reversible reaksjoner	Følg med på hvordan en reaksjon forløper over tid. Hvordan blir reaksjonsraten påvirket av den totale energien? Varier temperatur, barrierehøyde og potensiell energi. Registrer konsentrasjoner og tid for å beregne reaksjonsratekoeffisienter. Gjør studier av temperaturavhengighet for å finne Arrhenius-parametre. Denne simuleringen er best å gjøre med lærerveiledning, fordi den representerer en analogi til kjemiske reaksjoner.	27
459	zh_TW	可逆反應	隨著時間觀察反應的進行。是否總能量會影響反應速度？嘗試改變溫度、活化能及焓；記錄濃度及時間以便計算速率係數。做與溫度變因相依的實驗用來計算阿忍尼士 (Arrhenius)參數。使用本模擬實的最佳方法是和教師手冊中的類似化學反應一起教學。	27
460	ar	تفاعلات قابلة للعكس	راقب التفاعل مع الزمن. كيف تؤثر الطاقة الكلية على معدل التفاعل. تحكم في درجة الحرارة، ارتفاع الحاجز، الطاقات الكامنة. سجل الزمن والتراكيز حتى تحصل على معاملات المعدل. هذه المحاكاة تستخدم بشكل أفضل بوجود المعلم لأنها تمثل  قياس التفاعلات الكيميائية	27
461	zh_CN	可逆反应	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
462	hr	Reverzibilan proces	Promatrajte proces u vremenu!	27
463	pl	Reakcje odwracalne	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
464	en	Reversible Reactions	Watch a reaction proceed over time. How does total energy affect a reaction rate? Vary temperature, barrier height, and potential energies. Record concentrations and time in order to extract rate coefficients. Do temperature dependent studies to extract Arrhenius parameters. This simulation is best used with teacher guidance because it presents an analogy of chemical reactions.	27
465	tr	laserler	Laserlerin nasıl çalıştığının bir incelemesi	28
466	ru	Лазеры	Исследование работы лазера	28
467	es	Lasers	Una exploración de cómo los lasers funcionan	28
468	hu	Lézerek	A lézer működésének vizsgálata	28
469	sk	laser	Skúmanie ako pracuje laser	28
470	uk	Lasers	An exploration of how lasers work	28
471	pt	Lasers	Um estudo de como o Laser funciona	28
472	nl	Lasers	Een uitleg met experimenten over de werking van lasers	28
473	mn	Лазерууд	Лазерийг хэрхэн ажилдагийг судлах	28
474	pt_BR	Lasers	An exploration of how lasers work	28
475	hr	Laseri	Kako rade laseri!	28
476	pl	Lasery	Badanie zasady działania lasera	28
477	zh_TW	雷射	說明雷射是如何產生作用的	28
478	en	Lasers	An exploration of how lasers work	28
479	el	Λαβύρινθος	Προσομοίωση Ταχύτητας και Επιτάχυνσης	29
480	nl	Doolhofspel	Een simulatie van snelheid en versnelling	29
481	pt	Jogo do Labirinto	Uma simulação de Velocidade e Aceleração.	29
482	es	Maze Game	A simluation of velocity and acceleration.	29
483	ar	لعبة المتاهة	محاكاة السرعة والتسارع	29
484	zh_TW	迷宮遊戲	速度與加速度的模擬	29
485	zh_CN	迷宫小游戏	一个速度和加速度的模拟程序	29
486	hr	Zanimacje s brzinom i akceleracijom	Brzina i akceleracija	29
487	pl	Labirynt - Gra	Symulacja prędkości i przyspieszenia	29
488	en	Maze Game	A simluation of velocity and acceleration.	29
489	pt	Microondas	Uma simulação para explorar como micro-ondas aquecem as substâncias	30
490	es	Microondas	Una simulación para explorar cómo las microondas calientan cosas.	30
491	de	Mikrowellen	Eine Simulation, die erklärt, wie Mikrowellen Dinge erwärmen	30
492	nl	Magnetrons	Deze simulatie laat zien hoe een magenetron iets verwarmd.	30
493	sk	Mikrovlnka	Simulácia skúmajúca ako mikrovlny zohrievajú rôzne predmety.	30
494	hu	Mikrohullámok	Szimuláció annak bemutatására, hogyan melegítik fel a mikrohullámok a dolgokat.	30
495	hr	Mikrovalovi	Simulacija proučava kako mikrovalovi griju!	30
496	zh_TW	微波	探索微波如何加熱物體	30
497	pl	Mikrofale	Symulacja pokazująca ogrzewanie przy pomocy mikrofal	30
498	en	Microwaves	A simluation for exploring how microwaves heat things.	30
499	tr	iki boyutta hareket	hiz ve ivme arasindaki iliskinin gosterilmesi	31
500	es	PhET Movimiento2D	Una exploracion de como la aceleración y la velocidad se relacionan.	31
501	bg	Движение в 2D	Едно изследване на връзката между ускорение и скорост	31
502	uk	Motion in 2D	Try the new "Ladybug Motion 2D" simulation for the latest updated version. Learn about position, velocity, and acceleration vectors. Move the ball with the mouse or let the simulation move the ball in four types of motion (2 types of linear, simple harmonic, circle).	31
506	hr	Odnos brzine i akceleracije	Pomakni kuglicu mišem i otkrij kako se odnose sila i akceleracija!	31
507	zh_TW	二維移動	顯示速度與加速度的關係	31
508	pl	Ruch dwuwymiarowy	Sprawdzenie zależności między przyspieszeniem a prędkością.	31
509	en	Motion in 2D	Try the new "Ladybug Motion 2D" simulation for the latest updated version. Learn about position, velocity, and acceleration vectors. Move the ball with the mouse or let the simulation move the ball in four types of motion (2 types of linear, simple harmonic, circle).	31
510	es	El Hombre Móvil	La aplicacion del Hombre Móvil.	32
511	et	The Moving Man	Õpi tundma asukoha, kiiruse ja kiirenduse graafikuid. Liiguta meest hiire abil edasi-tagasi ja kuva tema liikumise graafikud. Sisesta positsiooni, kiiruse ja kiirenduse parameetrid ning lase simulatsioonil meest liigutada.	32
512	fa	The Moving Man	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
513	nl	Het bewegende mannetje	Bewegingen worden weergegeven in plaats-tijd, snelheid-tijd en versnelling-tijd diagrammen. Beweeg dit mannetje met de muis over het scherm en bekijk de diagrammen van de beweging. Stel plaats, tijd en versnelling in en laat het mannetje lopen terwijl de diagrammen verschijnen.	32
514	sk	MotionMan	Nau&#269;te sa rozumie&#357; grafom polohy, r&#253;chlosti a zr&#253;chlenia. Pomocou my&#353;ky pohybujte &#269;lovie&#269;ikom (MotionManom) sem a tam a sledujte grafy jeho pohybu. Alebo si nastavte poz&#237;ciu, r&#253;chlos&#357; alebo zr&#253;chlenie a dovo&#318;te po&#269;ita&#269;u, aby pohyboval MotionManom za V&#225;s.	32
515	sl	Gibanje človeka	Z gibanjem človeka odkriješ spreminjanje poti, hitrosti in pospeška.	32
516	uk	The Moving Man	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
517	el	Ο Κινούμενος Άνδρας	Μάθετε για τα γραφήματα θέσης, ταχύτητας και επιτάχυνσης. Μετακινήστε τον μικρό άνδρα μπροστά και πίσω με το ποντίκι και παραστήστε γραφικά την κινησή του. Καθορίστε τη θέση, την ταχύτητά του ή την επιτάχυνσή του και αφήστε την προσομοίωση να κινήσει τον άνδρα για εσάς. Μετάφραση: Βαγγέλης Κολτσάκης.	32
518	tn	Monna yo o tsamayang	Tiriso ya monna yo o tsamayang	32
519	de	Der bewegte Mann	Lernen Sie etwas über Weg-Zeit; Geschwindigkeits-Zeit und Beschleunigungs-Zeit-Diagramme. Bewegen Sie den kleinen Mann mit der Maus hin und her und verpassen Sie ihm eine Bewegung. Legen Sie Ort, Geschwindigkeit oder Beschleunigung fest und lassen Sie den kleinen Mann durch die Simulation bewegen.	32
520	ar	الرجل المتحرك	تعلم عن الرسوم البيانية للمكان ،للسرعة، للتسارع. حرك الرجل الصغير ذهابا وإيابا بالفأرة وارسم حركته. حدد المكان، السرعة والتسارع واسمح للمحاكاة أن تحرك الرجل لك.	32
521	pt	Movimento	Estude os gráficos de posição, velocidade e aceleração. Mova um pequeno boneco e registre seu movimento. Configure a posiçãp, velocidade e aceleração e deixe a simulação realizar a movimentação automática do boneco.	32
522	zh_CN	运动图像	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
523	hr	Gibanje	Nauči nešto o položaju, putu, brzini i akceleraciji! Pomakni čovječuljka mišem i pogledaj grafički prikaz njegova gibanja! Odaberi položaj, početnu brzinu ili akceleraciju čovječuljka i simulacije će napraviti zadano!	32
524	zh_TW	移動的人形	學習關於位置、速度和加速度圖表。以滑鼠來來回回地移動人形並繪製他運動的圖。設定位置、速度或加速動，並讓模擬程式為你移動圖上的人形。	32
525	en	The Moving Man	Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.	32
526	es	MRI Simplificado	Un modelo simplificado de la proyección de imagen de resonancia magnética	33
527	sk	Magnetická rezonancia	Princíp zobrazovania magnetickou rezonanciou	33
528	el	Μαγνητική Τομογραφία	Απλοποιημένο Μοντέλο Μαγνητικής Τομογραφίας	33
529	pt	MRI Simplificada	Um modelo simplificado para Imagem por Ressonância Magnética	33
530	nl	Vereenvoudigd MRI	Een vereenvoudigd model van beeldvorming door magnetische resonantie 	33
531	hr	Shema MRI	Jednostavan model snimanja magnetskom rezonancijom	33
532	pl	Uproszczony MRI	Uproszczony model magnetycznego rezonansu jądrowego	33
533	zh_TW	簡化的磁共振造影MRI	磁共振造影的簡化模型	33
534	en	Simplified MRI	A simplified model of magnetic resonance imaging	33
535	el	Οπτικός Κβαντικός Έλεγχος	Δημιούργησε έναν  παλμό, μεταβάλλοντας μεμονωμένα τα χρώματα του παλμού εισόδου. Δημιούργησε προσεκτικά το σωστό σχήμα παλμού για να διασπάσεις ένα μόριο. 	34
536	pt	Controle Quântico-Óptico	Construa uma forma de pulso de luz pela manipulação individual das cores de um pulso luminoso. Crie cuidadosamente a forma perfeita de pulso para desintegrar uma molécula.	34
537	ru	Оптический квантовый контроль	Изменяйте цвета светового импульса по отдельности. Точно воссоздайте форму имплульса чтобы разрушить молекулу.	34
538	es	Control Óptico De Quantum	Haga una forma de pulso del diseñador individualmente manipulando los colores de un pulso ligero. Cree cuidadosamente el pulso formado perfecto para romper aparte una molécula.	34
539	nl	Optisch manipuleren van lichtpulsen	Geef een lichtpuls de gewenste vorm door alle kleuren van de lichtpuls apart te manipuleren. Geef zo de lichtpuls de juiste vorm (=energie)  om de bindingen in het molocuul te verbreken.	34
540	hr	Kvantna optika	Ručno uredi puls. Pravim pulsom razbit ćete molekulu.	34
541	zh_TW	光量子控制	藉由逐個地操作光脈衝波的顏色，進行脈衝波波形的設計，謹慎地產生完美的脈衝波波形使得分子被打散。	34
542	en	Optical Quantum Control	Make a designer pulse shape by individually manipulating the colors of a light pulse. Carefully create the perfect shaped pulse to break apart a molecule.	34
543	el	Μοριακοί Κινητήρες	Ανακαλύψτε τι ελέγχει πώς γρήγοροι μικροσκοπικοί κινητήρες στο σώμα μας έλκουν μια μονή αλυσίδα DNA. Πόσο δυνατά μπορεί να "τραβήξει" ο κινητήραςαντιστεκόμενος στο τράβηγμα των οπτικών λαβίδων; Ανακαλύψτε τι τον ενισχύει. Συμπεριφέρονται όλοι οι μοριακοί κινητήρες με τον ίδιο τρόπο; 	35
544	pt	Molecular Motors	Discover what controls how fast tiny molecular motors in our body pull through a single strand of DNA. How hard can the motor pull in a tug of war with the optical tweezers? Discover what helps it pull harder. Do all molecular motors behave the same?	35
545	ru	Молекулярные моторы	Откройте, что управляет тем, как быстро миниатюрные молекулярные моторы в нашем теле тянут вдоль отдельную нить ДНК. Как сильно может тянуть  мотор в перетягивании с оптическим пинцетом? Узнайте, что помогает им тянуть сильнее. Все ли молекулярные моторы ведут себя одинаково?	35
546	es	Motores Moleculares	Descubre qué es lo que controla la rapidez con minúsculos motores moleculares en nuestro cuerpo a través de tirar un solo filamento de la ADN. ¿Qué tan difícil puede tirar el motor en un tira y afloja con el pinzas ópticas? Descubra lo que le ayuda a tirar con más fuerza. ¿Todos los motores moleculares comportarse de la misma manera?	35
547	nl	Moleculaire motors	Ontdek hoe moleculaire motors over een enkele DNA-streng bewegen. Laat de motor en het optisch pincet 'touwtje trekken'.\nOntdek hoe de motor harder kan trekken.\nGedragen alle moleculaire motors zich hetzelfde?	35
548	hr	Djelovanje enzima	Otkrijte kao sićišne molekule, enzimi, upravljaju velikom molekulom DNA.	35
549	zh_TW	分子馬達	由單一DNA鍵來拉伸微小的分子馬達要如何控制？分子馬達在光鑷中受到許多力量彼此拉鋸？分子馬達的動作是否相同？	35
550	pl	Silniki molekularne	Zobacz, jak można sterować maleńkim silnikiem molekularnym w ciele człowieka. Zobacz to na modelu pojedunczej nitki DNA.  Porównaj działanie silnika i "optycznej pensety". Zobacz, co działa silniej. Czy wszystkie silniki molekularne zacchowują się tak samo?	35
551	en	Molecular Motors	Discover what controls how fast tiny molecular motors in our body pull through a single strand of DNA. How hard can the motor pull in a tug of war with the optical tweezers? Discover what helps it pull harder. Do all molecular motors behave the same?	35
552	el	Τέντωμα DNA	Διερευνήστε το τέντωμα μιας μονής αλυσίδας DNA με οπτικές λαβίδες ή ροή ρευστού. Πειραματιστείτε με τις εμπλεκόμενες δυνάμεις και μετρήστε την απαιτούμενη δύναμη για να παραμείνει τεντωμένη. Το DNA συμπεριφέρεται περισσότερο σαν αλυσίδα ή σαν σκοινί;	36
553	pt	Stretching DNA	Explore stretching just a single strand of DNA using optical tweezers or fluid flow. Experiment with the forces involved and measure the relationship between the stretched DNA length and the force required to keep it stretched. Is DNA more like a rope or like a spring?	36
554	ru	Растяжение ДНК	Исследуйте растяжение отдельной нити ДНК под действием оптического пинцета или потока жидкости. Экспериментируйте с задействованными силами и измерьте соотношение между длиной растянутой ДНК и силой, требующейся для этого. Что сильнее напоминает ДНК - верёвку или пружину?	36
555	es	Estirar la DNA	Explora estiramiento de un solo filamento de la ADN utilizando pinzas ópticas o flujo de fluidos. Experimente con las fuerzas que intervienen y medir la relación entre la longitud estirada de ADN y la fuerza necesaria que le mantenga estirado. DNA es más parecido a una cuerda o como una primavera?	36
556	nl	DNA strekken	Onderzoek het strekken van een enkele streng DNA met een optisch pincet of in een vloeistofstroom. Verander de betrokken krachten en meet de relatie tussen de lengte van de streng DNA en de kracht die nodig is om het DNA te strekken. Gedraagt de streng zich als een draad of als een veer?	36
557	hr	Ispravljanje DNA	Otkrijte kao sićišne molekule, enzimi, upravljaju velikom molekulom DNA.	36
558	zh_TW	DNA拉長	探索使用光鑷或流力來來拉伸單鍵DNA。透過實驗來觀測力與DNA拉伸的長度之關係。DNA是否向一個繩子或彈簧？	36
559	pl	Rozciąganie DNA	Zobacz, jak można wydłużyć pojedynczą nitkę DNA uzywająć "pensety optyczne"j i przepływu cieczy. Zbadaj siły, jakie tu występują. Zmierz zależność między rozciągniętym DNA a siłami, które pozwalają na pozostawanie w tym stanie. Czy DNA przypomina bardziej linę czy sprężynę?	36
560	en	Stretching DNA	Explore stretching just a single strand of DNA using optical tweezers or fluid flow. Experiment with the forces involved and measure the relationship between the stretched DNA length and the force required to keep it stretched. Is DNA more like a rope or like a spring?	36
561	el	Οπτικές Λαβίδες και Εφαρμογές	Φανταστήκατε ποτέ ότι μπορείτε να χρησιμοποιήσετε φως για να μετακινήσετε ένα μικροσκοπικό πλασικό σφαιρίδιο; Διερευνήστε τις δυνάμεις στο σφαιρίδιο ή επιβραδύνετε το χρόνο για να παρατηρήσετε την αλληλεπίδραση με το ηλεκτρικό πεδίο του laser. Μπορείτε να κάνετε την αλυσίδα του DNA εντελώς ευθεία ή να σταματήσετε τον μοριακό κινητήρα;	37
562	pt	Pinça Óptica	Já imaginou alguma vez que você poderia utilizar a luz para mover uma bolinha microscópica de plástico e esticar uma única molécula de DNA? Explore as forças que atuam sobre uma amostra, uma pequena bolinha de plástico, utilizando a Pinça Óptica. Utilize a simulação em câmara lenta para visualizar as interações da amostra com o campo elétrico do Laser. Tente esticar uma cadeia de DNA com a sua Pinça Óptica - Você é capaz de esticá-lo completamente? 	37
610	hr	Eksperiment Davissona i Germera	Simulacija originalnog eksperimenta koji pokazuje da se elektroni mogu ponašati kao valovi. Promatramo  difrakciju elektrona na čvornim atomima kristalne rešetke!	41
618	nl	Kwantum golfinterferentie	Kwantum golfinterferentie	42
563	ru	Оптические пинцеты и их применение	Вы когда-нибудь представляли себе, что свет можно использовать для передвижения микроскопического пластикового шарика? Исследуйте силы, действующие на шарик или замедлите время чтобы посмотреть на взаимодействие с электрическим полем лазера. Используйте оптические пинцеты чтобы управлять отдельной нитью ДНК и узнайте физику миниатюрных молекулярных моторов. Можно ли полностью выпрямить ДНК или остановить молекулярный мотор?	37
564	es	Pinzas Ópticas Y Sus Aplicaciones	¿Alguna vez imagino que usted puede utilizar para mover una luz plásticas microscópicas talón? Explora las fuerzas sobre el talón o de la lentitud de tiempo para ver la interacción con el láser del campo eléctrico. Utilice las pinzas ópticas de manipular un solo capítulo de la DNA y explorar la física de diminutos motores moleculares. Se puede obtener el DNA completamente recta o detener los motores moleculares?	37
565	nl	Optisch pincet (tweezers) en toepassingen van het pincet.	Heb je ooit bedacht dat je licht kunt gebruiken om microscopisch kleine plastic deeltjes te bewegen? Onderzoek de krachten die op het deeltje werken of vertraag de tijd om de interactie te zien tussen het deeltje en het elektrisch veld van een laser. \nGebruik het optisch pincet om een streng DNA vast te houden en te strekken. Onderzoek zo de krachten van moleculaire motors. \nKun je de streng helemaal recht krijgen of de motor stoppen?	37
566	hr	Optička pinceta i primjene	Jeste li znali da možete elektromagnetski pomicati sitne mikroskopske plastične probe.	37
567	zh_TW	光學鑷子及應用	你是否曾想像利用光來移動微小的塑膠粒子？利用光鑷來操作單鍵DNA並發現微小的分子馬達之物理現象。	37
568	pl	Optyczna penseta i jej zastosowania	Czy myślałeś kiedyś, że uda ci się przesunąć mikroskopijne podłoże oglądane pod mikroskopem przy pomocy światła? Zbadaj działające siły; możesz spowolnić czas, by dokładniej zapoznać się ze zjawiskiem - dzialanie pola elektrycznego wiążki lasera. Użyj "optycznej pensety" do rozciągania molekuly DNA. Czy możesz ją całkowicie wyprostować?	37
569	en	Optical Tweezers and Applications	Did you ever imagine that you can use light to move a microscopic plastic bead? Explore the forces on the bead or slow time to see the interaction with the laser's electric field. Use the optical tweezers to manipulate a single strand of DNA and explore the physics of tiny molecular motors. Can you get the DNA completely straight or stop the molecular motor?	37
570	es	Escala de pH	Ensaye el pH de cosas tales como café, saliva y jabón, para determinar si son ácidas, básicas o neutras. Visualise las cantidades relativas de iones hidróxido e hidronio en solución. Investigue si el cambiar el volumen o diluir con agua afecta al pH. ¡O puede diseñar su propio líquido!	38
571	fr	Echelle de pH	Testez le pH de liquide comme le café, le crachat ou le savon et déterminer pour chacun s'il est acide, basique, ou neutre. Visualisez le nombre relatif d'ions hydroxyde et d'ions hydronium en solution. Commutez entre les échelles logarithmiques et linéaires. Étudiez l'impact sur le pH d'une variation de volume ou de la dilution. Ou alors particularisez  votre propre liquide !	38
572	fi	pH asteikko	Testaa ph erilaisista liuoksista kuten kahvi, sylki ja saippua, päättele ovatko luokset neutraaleja, happoja vai emäksiä. Tarkastele hydroksini ja hydroniumionien määrää liuoksessa. Vertaa logaritmisiä ja lineeaarisia asteikkoja. Testaa miten liuoksen laimennos vaikuttaa pH:on.	38
573	ru	шкала pH	Протестируйте pH разных веществ, таких как кофе, слюна и мыло, чтобы определить щелочные они, кислые или нейтральные. Визуализируйте относительное число ионов гидроксида и ионов водорода, соединенный с молекулой воды в растворе. Переключайтесь между логарифмическим и линейным масштабами. Исследуйте, как изменение объёма и разбавление водой влияет на pH. Или создайте свою жидкость!	38
574	ar	الأس الهيدروجيني 	اختبر الأس الهيدروجيني لأشياء مثل القهوة، اللعاب، والصابون لتحدد الحمض و القاعدة و المتعادل. تصور العدد النسبي لأيونات الهيدروكسيد وأيونات الهيدرونيوم في المحلول. بدل بين المقياس الخطي والمقياس اللوغاريتمي. حدد ماإذا كان تغيير الحجم أو تخفيف التركيز بالماء يؤثر على الأس الهيدروجيني. أو بإمكانك تصميم واختيار السائل الذي تريده.	38
575	nl	pH-schaal	Onderzoek of de zuurgraad (pH) van stoffen als koffie, speeksel en zeep. Zijn de oplossingen van deze stoffen zuur of basisch?\nOnderzoek of de zuurgraad verandert als je meer of minder vloeistof neemt of als je de oplossing verdunt.\nLaat zien in welke verhouding H<sub>3</sub>O<sup>+</sup> en OH<sup>-</sup> ionen in de oplossingen voorkomen. \nMaak een nieuwe vloeistof!	38
576	pl	skala pH	Sprawdź pH płynów: kawy, śliny i mydła aby określić czy są kwaśne, zasadowe lub obojętne. Zmierz względną liczbę jonów hydroksylowych i wodorowych w roztworze. Przełącz się między skalą logarytniczną i linową. Zbadaj czy zmiana objętości lub rozcieńczanie wodą wpływa na pH. Stwórz swój własny płyn !	38
577	pt_BR	Escala de pH	Teste o pH de materiais como café, cuspe, e sabonete para determinar se são ácidos, alcalinos ou neutros. Visualize o número relativo de íons hidróxido e hidrônio em solução. Alterne em as escalas logarítmica e linear. Investigue como a mudança de volume ou diluição em água afeta o pH. Ou invente um líquido qualquer!	38
578	zh_TW	pH 刻度	檢查咖啡、汽水及肥皂的pH值決定其是否為酸性、鹼性或中性。視覺化的觀查溶液中氫氧根離子及氫離子的比例。並用對數刻度及線性刻度來呈現。改變溶液體積及稀釋溶液是否影響pH值。或者也可選用你指定的液體來做模擬實驗。	38
579	hr	pH	Istraži kiselost (pH) stvari kao što je kava, sapun, pljuvačka i sl. Vizualiziraj relativni broj hidronij i hidroksid iona u otopini.	38
611	pl	Doświadczenie Davisson-Germera	Przeprowadż symulacje doświadczenia, które dowodzi, że elektron może zachowywać się, jak fala. Obserwuj dyfrakcję elektronów na krysztale, wzajemną interferencję, zobacz stosowne prawdopodobieństwa	41
612	en	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
580	el	Κλίμακα pH	Ελέγξτε το pH διαφόρων υλικών, όπως ο καφές, το σάλιο και το σαπούνι για να εξακριβώσετε εάν καθένα από αυτά είναι όξινο, αλκαλικό ή ουδέτερο. Οπτικοποιήστε τους σχετικούς αριθμούς των ιόντων του υδροξειδίου και του υδρονίου. Κάντε εναλλαγές μεταξύ λογαριθμικής και γραμμικής κλίμακας. Διερευνήστε εάν η αλλαγή του όγκου ή η διάλυση με νερό επηρεάζει το pH. Ή μπορείτε να δημιουργήσετε το δικό σας υγρό! 	38
581	et	pH skaala	Katseta, milline on erinevate vedelike pH (kas happeline, aluseline või neutraalne). Vaata kui palju on arvuliselt lahuses hüdroksiid- ja hüdrooniumioone. Vaatle graafikuid logaritmiliselt ja lineaarselt. Leia, kas vee lisamine või vähendamine muudab lahuse pH-d. Sul on võimalus luua ka enda lahus!	38
582	en	pH Scale	Test the pH of things like coffee, spit, and soap to determine whether each is acidic, basic, or neutral. Visualize the relative number of hydroxide ions and hydronium ions in solution. Switch between logarithmic and linear scales. Investigate whether changing the volume or diluting with water affects the pH. Or you can design your own liquid!	38
583	pt	O Efeito Fotoeléctrico	See how light knocks electrons off a metal target, and recreate the experiment that spawned the field of quantum mechanics.	39
584	fa	اثر فوتوالکتریک	نمایش فرود نور بر فلز هدف و خلق الکترونهای فوتوالکتریک با کندن آنها از سطح فلز و نیز نمایش آزمایشی برای بسط گستره مکانیک کوانتم.	39
585	nl	Fotoelektrisch effect	Kijk hoe licht elektronen uit een metalen plaat vrijmaakt en herbeleef het experiment waarmee de quantummechanica een vliegende start maakte.	39
586	es	El Efecto Fotoeléctrico	Observa como la luz hace desprender electrones de una muestra metálica, y recrea el experimento que dió origen al campo de la mecánica cuántica.	39
587	de	Der Photoelektrischer Effekt	Sehen wie licht brechen dem Elektronen ab von eines metallisches Target, und  erfrischen sie das Experiment der hat den Quantenmechanischefeld erzuegt.	39
588	sk	Fotoelektrický jav	Sledujte ako dopadajúce svetlo uvoľňuje elektróny z kovovej platne 	39
589	hr	Fotoelektrični efekt	Mehanizam kako svjetlost izbacije elektrone iz metala ocrtava eksperiment koji je otvorio put razvoju kvantne mehanike.	39
590	hu	Fotoeffektus	Figyeld, ahogy a fény elektronokat üt ki a céltárgyként használt fémből. Reprodukáld a kvantummechanika megszületését kiváltó kísérletet.	39
591	zh_TW	光電效應	觀察電子是如何被撞擊離開金屬標靶，並且重現大量產生量子力場的實驗。	39
592	pl	Efekt fotoelektryczny	Zobacz, jak fotony uderzają fotokatodę i wybijają z niej elektrony. Objaśnij wyniki doświadczenia nagruncie mechaniki kwantowej.	39
593	en	The Photoelectric Effect	See how light knocks electrons off a metal target, and recreate the experiment that spawned the field of quantum mechanics.	39
594	pt	Tunelamento Quântico	<html>Observe "partículas quânticas" tunelando através de barreiras de energia.<br>Explore as propriedades das funções de onda que descrevem estas partículas.<br></html>	40
595	ru	Квантовое туннелирование и волновые пакеты	Пронаблюдайте туннелирование квантовых "частиц" через барьер. Исследуйте свойства волновых функций, описывающих эти частицы.	40
596	es	Penetración Mecánico-Cuántica y Paquete de Ondas	Mire las partículas cuánticas penetrar las barreras. Explore las características de las funciones de onda que describen estas partículas.	40
597	el	Φαινόμενο Σήραγγας και Κυματοδέματα	Παρατηρήστε το φαινόμενο σήραγγας κβαντομηχανικών "σωματιδίων". Διερευνήστε τις ιδιότητες των κυματοσυναρτήσεων που περιγράφουν αυτά τα σωματίδια.	40
598	mn	Квант тунель ба Долгионы багцууд	Watch quantum "particles" tunnel through barriers. Explore the properties of the wave functions that describe these particles.	40
599	nl	Kwantumtunnelling en Golfdeeltjes	Let op hoe 'kwantumdeeltjes' door barrieres gaan.\nOnderzoek de eigenschappen van golffuncties die de eigenschappen van deze deeltjes beschrijven.	40
600	hr	Kvantno tuneliranje i valni paketi	Otkrijte kako kvanti tuneliraju kroz energijsku barijeru. Izučite kako valnom funkcijom možemo objasniti ovaj fenomen.	40
601	hu	Alagúthatás és hullámcsomagok	Figyeld, ahogy a "kvantumrészecskék" áthatolnak az akadályokon. Tanulmányozd a részecskéket leíró hullámfüggvényeket. 	40
602	pl	Tunelowanie kwantowe i paczki falowe	Obserwuj jak cząstka przechodzi przez bariery potencjału (tunelowanie). Zbadaj właściwosci funkcji falowej, która opisuje zachowanie się cząstki.	40
603	en	Quantum Tunneling and Wave Packets	Watch quantum "particles" tunnel through barriers. Explore the properties of the wave functions that describe these particles.	40
604	sk	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
605	el	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
606	pt	Experimento de Davisson-Germer	Simulação da experiência original que provou que eletrons podem comportar uma onda. Assista a difração de elétrons em uma estrutura cristalina, com auto-interferência  para criar picos e vales, definindo franjas de probabilidade de ocorrência.	41
607	ru	Davisson-Germer Experiment	Simulate the original experiment that proved that electrons can behave as waves. Watch electrons diffract off a crystal of atoms, interfering with themselves to create peaks and troughs of probability.	41
608	es	Experimento Davisson-Germer	Simula el experimento original que probó que los electrones pueden comportarse como ondas. Observe a los electrones difractarse en un cristal de átomos, interfiriendose con ellos mismos para crear picos y valles de probabilidad.	41
609	nl	Davisson-Germer Experiment	Simuleer het experiment dat bewees dat elektronen zich bewegen als golven. Zie de diffractie van elektronen van een kristal, hoe ze met elkaar interfereren en zo waarschijnlijkheidspieken en -dalen vormen.	41
613	sk	Quantum Wave Interference	Quantum Wave Interference	42
621	en	Quantum Wave Interference	Quantum Wave Interference	42
622	hu	Rádióhullámok	Annak vizsgálata, hogyan keletkeznek az elektromágneses hullámok, hogyan mozognak a térben és milyen hatásaik vannak.	43
623	sk	Rádiové vlny	Ukážka ako sa tvoria elektromagnetické vlny, ako sa šíria priestorom, a aký majú účinok.	43
624	pt	Ondas de Rádio	Uma exploração de como as ondas eletro-magnéticas são criadas, como se movimentam através do espaço, e seus efeitos.	43
625	tr	Radyo dalgaları	Elektromanyetik dalgaların nasıl üretiler, hava ortamında nasıl ilerler ve diğer etkilerini açıklayıcı simülasyon	43
626	es	Ondas del radio	Una exploración de cómo se crean las ondas electromagnéticas, cómo se mueven a través de espacio, y sus efectos	43
627	nl	Radiogolven	Radiogolven zijn elektromagnetische golven. \nHoe ontstaan deze goven? \nHoe verplaatsen deze golven zich? 	43
628	it	Onde Radio	Come sono generate le onde radio in una antenna? Come si propagano le onde radio? Che effetti hanno le onde sulle antenne riceventi?	43
629	ro	Undele Radio	O explorare a modului de creare a undelor electro-magnetice, cum se misca prin spatiu si efectele lor	43
630	in	Gelombang Radio	Mengeksplorasi bagaimana medan elektromagnetik dihasilkan, bagaimana perambatannya dalam ruang, dan efeknya	43
631	hr	Radio Valovi	Primjer kako nastaju i kako se prostiru elektromagnetski valovi	43
632	zh_TW	無線電波	電磁波如何產生，及其傳輸方式與效應	43
633	pl	Fale radiowe	Badanie pola elektromagnetycznego. Jak jest wytwarzane, jak porusza się w przestrzeni i jakie są jego efelty/	43
634	en	Radio Waves	An exploration of how electro-magnetic waves are created, how they move through space, and their effects	43
635	el	Αντιδράσεις και Ταχύτητα Αντίδρασης	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
636	pt	Reações & taxas	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
637	es	Reacciones y Velocidades	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
638	fr	Réaction et cinétique	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
639	nl	Reacties en reactiesnelheid	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
640	ar	التفاعلات وسرعتها(معدلها)	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
641	zh_TW	化學反應和反應速率	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
642	pl	Reakcje i ich szybkości	Co powoduje rozpoczęcie reakcji? Znajdź co i jak wpływa na szybkośc reakcji? Przeprowadż własne doświadczenia; zbierz dane i oblicz współczynnik szybkości reakcji. Wypróbuj różne reakcje, stężenia i energie.	44
643	en	Reactions & Rates	Explore what makes a reaction happen by colliding atoms and molecules. Design experiments with different reactions, concentrations, and temperatures. When are reactions reversible? What affects the rate of a reaction?	44
644	pt	Torque	Simulação de Torque	45
645	hu	Forgatónyomaték	Vizsgálja meg, hogyan készteti forgásra a nyomaték a testeket. Állapítsa meg a szöggyorsulás, tehetetlenségi nyomaték és a nyomatékok kapcsolatát.	45
646	es	Torsión	Investigue cómo la torsión provoca la rotación de un objeto. Descubra las relaciones entre la aceleración angular, el momento de inercia, el momento angular y la torsión.	45
647	ar	العزم	تحقق كيف يسبب العزم دوران جسم. إكتشف العلاقات بين التسارع الزاوي وعزم القصور، وكمية الدفع الزاوي والعزم 	45
648	nl	Koppel	Onderzoek hoe het moment een voorwerp laat roteren. Ontdek de relatie tussen hoekversnelling, traagheidsmoment, hoekmoment en koppel.	45
649	hr	Moment sile	Zašto tijela rotiraju. Otkrijte odnose brzine i kutne brzine, kutne akceleracije i momenta tromosti...	45
650	zh_TW	力矩	研究力矩如何使一物體轉動，發現角加速度、轉動慣量、角動量和力矩之間的關係。	45
651	en	Torque	Investigate how torque causes an object to rotate.  Discover the relationships between angular acceleration, moment of inertia, angular momentum and torque.	45
652	pt	Revolução da joaninha	Junção da joaninha em uma exploração de movimento rotacional. Rotacione o carrosel para mudar seu ângulo, ou escolha uma velocidade angular constante ou aceleração angular. Explore como movimento circular relaciona o inseto com as posições x,y, velocidade e aceleração usando vetores ou gráficos.	46
653	hu	Katicabogár forgás	Használja a katicabogarat a körmozgás vizsgálatára. Forgassa meg a korongot vagy válasszon állandó sebességet ill. gyorsulást. Vizsgálja meg a bogár x,y elmozdulását, sebességét, gyorsulását és használja a vektorokat ill. grafikonokat.	46
654	es	Revolución Mariquita	Únase a la mariquita en una exploración al movimiento de rotaición. Haga rotar el giradiscos al cambiar su ángulo, o escogiendo una velocidad angular constante o determinada aceleración angular. Explore el movimiento circular viendo su relación con la posición x,y del bicho, la velocidad y la aceleración usando vectores o diagramas.	46
655	ar	دوران الخنفساء	رافق الخنفساء الصغيرة في إستكشاف الحركة الدورانية. أدر لعبة الدوامة لتغيير زاويتها، أو اختر سرعة زاويّة ثابتة أو تسارع زاويّ.  استكشف كيف ترتبط الحركة الدائرية بموقع وسرعة وتسارع  الخنفساء باستخدام المتجهات أو الرسم البياني	46
683	pl	Półprzewodniki	Wprowadź domieszki do półprzewodnika w celu utworzenia diody lub tranzystora. Obserwuj jak elektrony zmieniają swoje położenie oraz energię	49
656	nl	Lieveheersbeestje-draaien	Onderzoek met het lieveheersbeestje de draaibeweging. Laat de draaimolen draaien met verschillende hoeksnelheden en hoekversnellingen.\nOnderzoek hoe de draaibeweging zich verhoudt tot het insects x- en y-positie, snelheid en versnelling. Gebruik daarbij de vectoren en de diagrammen.	46
657	hr	Luna park za bube i žohare	Pridružite se raznim bubama u njihovom svjetovima (koordinatnim sustavima) i otkrijte kako oni vide svoj svjet i koje sile osjećaju.	46
658	zh_TW	旋轉的瓢蟲	參與瓢蟲在旋轉移動的探索。轉動旋轉木馬改變它的角度，或指定一定值的角速度或角加速度。使用向量或圖表，探索圓週運動和甲蟲的x,y位置、速度與加速度的關係。	46
659	en	Ladybug Revolution	Join the ladybug in an exploration of rotational motion.  Rotate the merry-go-round to change its angle, or choose a constant angular velocity or angular acceleration.  Explore how circular motion relates to the bug's x,y position, velocity, and acceleration using vectors or graphs.	46
660	nl	Rutherford verstrooiing	Ook Rutherford kon geen atomen zien. Toch ontdekte hij met zijn beroemd  geworden experiment de atoomkern.  Hoe deed hij dat?\nMet deze simulatie doe jij Rutherford's  beroemde experiment na en ontdek je ook dat bijna alle massa van het atoom in de heel kleine kern moet zitten. Een atoom zou wel een  vreemde krentenbol zijn: al het brood in een klein puntje en de krenten daar ver vandaan!\nHet model van een atoom als een kleine kern en elektronen op grote afstand daaromheen noem je het atoommodel van Rutherford.	47
661	hu	Rutherford-szórás	Hogyan jött rá Rutherford, hogy az atomnak magja van, anélkül, hogy belelátott volna? Szimuláld a híres kísérletet, mely bebizonyította, hogy az atom mazsoláspuding-modellje helytelen. Az alfa-részecskék olykori eltérülése ugyanis azt jelezte, hogy az atomnak apró, de nehéz magja lehet, pozitív töltéssel. (Az "olykori" valójában nagyon ritka eseményt jelent, hiszen a jelentősebb eltérülések "ablaka", melyet a Rutherford-atom animációja kitár előttünk, 1/1000 része sincs az atom átmérőjének.) Az atommag kiindulási összetétele (az egykori mérések emlékére) az Au-197 nuklidét tükrözi, mely az arany egyetlen stabil izotópja.	47
662	sk	Rutherfordov pokus 	Ako Rutherford určil existenciu atómového jadra?  Simuláciou slávneho pokusu, ktorý vyvrátil správnosť pudingového modelu atómu, zistíte že alfa častice sa odrážajú od atómu tak, ako by mal malé jadro.	47
663	vi	Tan xa Rutherford	Rutherford đã khám phá ra hạt nhân nguyên tử như thế nào? Các hạt nhân alpha khi được bắn vào các nguyên tử thì bị chệch hướng, chứng tỏ nguyên tử có một lõi tích điện dương. Điều này bác bỏ giả thuyết cho rằng nguyên tử có dạng như chiếc bánh Pudding nho.	47
664	uk	Резерфордівське розсіяння	How did Rutherford figure out the structure of the atomic nucleus without looking at it?  Simulate the famous experiment in which he disproved the Plum Pudding model of the atom by observing alpha particles bouncing off atoms and determining that they must have a small core.	47
665	el	Σκέδαση Rutherford	Πως ανακάλυψε ο Rutherford τη δομή του πυρήνα του ατόμου χωρίς να τον βλέπει; Προσομοιώστε το περίφημο πείραμα με το οποίο κατέρριψε το ατομικό  μοντέλο του "Σταφιδόψωμου" συμπεραίνοντας ότι τα άτομα πρέπει να έχουν πυρήνα, παρατηρώντας σωμάτια άλφα να εκπέμπονται  από τα άτομα.	47
666	pt	Dispersão de Rutherford	Como Rutherford descobriu a estrutura do núcleo atômico sem vê-lo?  Simule o famoso experimento no qual ele não dispunha do modelo atômico pudim de ameixas para observar as particulas alpha saltitantes dos átomos.	47
667	es	Dispersión de Rutherford	¿Cómo descubrió Rutherford  la estructura del núcleo atómico sin mirarlo? Simule el famoso experimento en el cual refutó el modelo de budín de ciruelas del átomo, observando partículas alfa rebotando de átomos y determinando que estos deben tener un núcleo pequeño.	47
668	hr	Rutherfordovo raspršenje	Kako znamo da atomi izgledaju upravo onako nako nas uče: mala kompaktna pozitivno nabijena jezgra koja nosi gotovo svu masu atoma i veliki elektronski oblak oko nje u kojem se nalaze lagani negativno nabijeni elektroni. Rutherford je eksperimentalno utvrdio ovu sliku! Ideju eksperimenta dočarava i ova simulacija.	47
669	zh_TW	拉塞福散射實驗	拉塞福是如何在看不見原子的情況下找出原子具有原子核的結構？這裡模擬了拉塞福著名的實驗，他觀察alpha 柆子撞擊原子而推翻了李子布丁原子模型確立了原子核模型。	47
670	pl	Doświadczenie Rutherforda 	Jak Rutheerford odkrył budowę atomu bez mozliwości zajrzenia do jego wnętrza? Przeprowadż symulacje słynnego doświadczenia. Pokaż, że model atomu- ciasta ze śliwkami nie odpowiada rzeczywistości. Zobacz, jak wyniki dowodzą, że atom ma bardzo male ( w porównaniu z całościa ) jądro.	47
671	en	Rutherford Scattering	How did Rutherford figure out the structure of the atomic nucleus without looking at it?  Simulate the famous experiment in which he disproved the Plum Pudding model of the atom by observing alpha particles bouncing off atoms and determining that they must have a small core.	47
672	pt	Modelo de auto-impulso da partícula	Tutorial do modelo de auto-impulso da partícula	48
673	es	Modelo de Partículas Auto-impulsadas (DSWeb)	Tutorial de Modelo de Partículas Auto-impuladas (DSWeb)	48
674	nl	Zelf aangedreven deeltjesmodel (DSWeb)	Tutorial voor Zelf aangedreven deeltjesmodel (DSWeb)	48
675	zh_TW	自驅式粒子模型(DSWeb)	教導自驅式粒子模型(DSWeb)	48
676	en	Self-Driven Patricle Model (DSWeb)	Self-Driven Patricle Model Tutorial (DSWeb)	48
677	el	Semiconductors	Προσθέστε προσμίξεις στον ημιαγωγό για να δημιουργήσετε μια δίοδο ή τρανζίστορ. Παρακολουθείστε τα ηλεκτρόνια να μεταβάλλουν τη θέση και την ενέργειά τους.	49
678	pt	Semicondutores	Dose o semicondutor para criar um diodo ou transistor. Veja a posição da carga dos elétrons e a energia.	49
679	es	Semiconductores	Semiconductores	49
680	ro	Semiconductori	Dopeaza semiconductorul pentru a crea o dioda sau un tranzistor. Urmareste cum electronii isi schimba pozitia si energia.	49
681	nl	Halfgeleiders	Doteer het halfgeleidermateriaal (zuiver Si, paars gebied) met dopants (e-doner voor N-type en e-acceptor voor P=type) en maak een diode of transistor. Let op stroom, de spanning en hoe de elektronen van plaats en energie veranderen.	49
682	hr	Poluvodiči	Otkrij koncept poluvodiča.Dopriaj poluvodič i konstruiraj diodu ili tranzistor.	49
757	sk	Interferencia vlnenia	Simulácia interferencie vlnenia	56
684	zh_TW	半導體	添加半導體以製作二極體或電晶體，並觀察電子改變位置與能量。	49
685	en	Semiconductors	Dope the semiconductor to create a diode or transistor. Watch the electrons change position and energy.	49
686	el	Σήμα - Κύκλωμα	Διερεύνηση της μετάδοσης σημάτων σε ηλεκτικά κυκλώματα	50
687	pt	Sinais de circuito	Uma exploração de como os sinais de circuito trabalham	50
688	es	Signal Circuit	An exploration of how signal circuits work	50
689	de	Signal-Stromkreis	Eine Erklärung wie Signal-Stromkreise funktionieren	50
690	nl	Stroomkirng	Onderzoek hoe het komt dat een lamp direct brandt als je een schakelaar aanzet.	50
691	sk	Signál v obvode	An exploration of how signal circuits work	50
692	uk	Signal Circuit	An exploration of how signal circuits work	50
693	ro	Semnal Circuit	Explorarea modului de functionare a semnalului circuitelor	50
694	hr	Struja i signal	Kako se signal propagira kroz električne voiče!	50
695	pl	Obwód elektryczny	Badanie działania prostego obwodu elektrycznego	50
696	zh_TW	信號電路	探索信號電路如何產生作用	50
697	en	Signal Circuit	An exploration of how signal circuits work	50
698	ru	Растворимость солей	Сильно и слаборастворимые соли и как они соотносятся с коэффициентом растворимости Ksp	51
699	es	Sales y Solubilidad	Sales muy y poco solubles, cómo se relacionan con Kps.	51
700	nl	Zouten en Oplosbaarheid	Goed en matig oplosbare zouten: wat is hun relatie met Ks?	51
701	it	Sali & Solubilità	Sali altamente e debolmente solubili. Prodotto di solubilità Kps.\nLocalizzazione italiana a cura della Scuola Secondaria di I grado "A. Manzoni" - Sannicandro di Bari (Bari)\ne-mail bamm195008@istruzione.it	51
702	el	Άλατα & Διαλυτότητα	Ευδιάλυτα και Δυσδιάλυτα άλατα, πως σχετίζονται με το Ksp	51
703	pt	Sais e Solubilidade	Sais muito e ligeiramente solúveis, e como eles se relacionam com Ks	51
704	fr	Solubilité et sel	Sels légèrement et hautement solubles (leur rapport avec Ksp.)	51
705	ar	الأملاح والذوبانية	أملاح عالية وقليلة الذوبان، كيف تتعلق بقيمة منتج الذوبان Ksp	51
706	pl	Sole i rozpuszczalność	Sole dobrze i słabo rozpuszczalne, wspólczynniki rozpuszczlności K(sp)	51
707	pt_BR	Sais e Solubilidade	Sais muito e pouco solúveis, e como eles se relacionam com Kps	51
708	hr	Soli i njihova topljivost	Topljivost soli ...	51
709	zh_TW	鹽類和溶解度	可溶及微溶性盬類和 Ksp 的關係。	51
710	et	Soolade lahustumine	Kuidas eristada hästi- ja vähelahustuvaid soolasid.	51
711	en	Salts & Solubility	Highly- and slightly-soluble salts, a how they relate to Ksp.	51
712	es	Ondas Acusticas	Cómo las ondas acústicas trabajan y se oyen	52
713	ga	Tonnta Fuaime	An bealach an oibríonn tonnta fuaime agus a caoi a cloistear iad	52
714	et	Helilained	Kuidas helilained levivad ja on kuuldavad	52
715	nl	Geluidsgolven	Eigenschappen van geluidsgolven en het horen van geluid	52
716	sk	Zvukové vlny	Ako sa zvukové vlny šíria a ako ich počujeme.	52
717	uk	Sound Waves	How sound waves work and are heard	52
718	pt	Ondas Sonoras	Como as ondas sonoras trabalham e como são ouvidas.	52
719	it	Onde sonore	Come funzionano e come si sentono le onde sonore	52
720	el	Ηχητικά κύματα	Πως συμπεριφέρονται τα ηχητικά κύματα και πως ακούγονται	52
721	fr	Ondes sonores	Comment se déplacent et sont perçues  les ondes sonores	52
722	hr	Zvuk	Kao čujemo zvučne valove	52
723	in	Gelombang Suara	Bagaimana gelombang suara bekerja dan dapat didengar	52
724	zh_TW	聲波	聲音是如何產生作用和被聽見的	52
725	pl	Fale dźwiękowe	Fale dźwiękowe i słyszenie	52
726	en	Sound Waves	How sound waves work and are heard	52
727	ak	Sim2 Name (el)	Sim2 Description (el)	53
728	zh_TW	Sim2 Name (zh_TW)	Sim2 Description (zh_TW)	53
729	el	Sim2 Name (el)	Sim2 Description (el)	53
730	en	Sim2 Name	Sim2 Description	53
731	ak	Sim1 Name (el)	Sim1 Description (el)	54
732	zh_TW	Sim1 Name (zh_TW)	Sim1 Description (zh_TW)	54
733	el	Sim1 Name (el)	Sim1 Description (el)	54
734	en	Sim1 Name	Sim1 Description	54
735	ru	Травольтаж	Изучение статического электричества	55
736	es	Travoltaje	Una exploración de la electricidad estática.	55
737	et	Travoltage	Staatilise elektri uurimine.	55
738	nl	Travolta - statische elektriciteit	Een simulatie van statische elektriciteit	55
739	sk	John Tra-volt	Skúmanie statickej elektriny.	55
740	el	Travoltage	Διερεύνηση του στατικού ηλεκτρισμού	55
741	pt	Travoltagem	Uma explicação da eletriciadade estática	55
742	ro	Travoltaj	O explorare a electricitatii statice.	55
743	fr	Travolt ... aahh	A la découverte de l'électricité statique	55
744	hu	Travoltage	Az elektrosztatika tanulmányozása	55
745	hr	Johntra Volta	Koncept nastanka statičkog elektriciteta!	55
746	pl	Jasio Woltuś	Elektryczność statyczna	55
747	zh_TW	Travoltage	靜電的探索	55
748	en	Travoltage	An exploration of static electricity.	55
749	sr	Interferencija talasa	Симулација интерференције таласа	56
750	tr	Wave Interference	Wave Interference Simulation	56
751	ru	Интерференция волн	Модель интерференции волн	56
752	es	Interferencia De la Onda	Simulación De Interferencia De la Onda	56
753	de	Wave Interference	Wave Interference Simulation	56
754	sv	Våginterferens	Simulering av våginterferens	56
755	nl	Golven: interferentie	Simulatie simuleert in terferentie van golven	56
756	it	Interferenza di onde	Simulazione dell'interferenza di onde sull'acqua, onde sonore e luce.	56
758	iw	Wave Interference	Wave Interference Simulation	56
759	uk	Wave Interference	Wave Interference Simulation	56
760	pt	Interferência de Onda	Simulação de Interferência de Onda	56
761	el	Συμβολή κυμάτων	Προσομοίωση συμβολής κυμάτων	56
762	cs	Vlnová interference	Simulace vlnové interference	56
763	hr	Interferencija valova	Simulacija Interferencije Valova	56
764	zh_TW	水波干涉	水波干涉模擬	56
765	pl	Interferencja fal	Symulacja interferencji fal	56
766	ar	تداخل الموجة	تمثيل تداخل الموجة	56
767	en	Wave Interference	Wave Interference Simulation	56
768	en	Arithmetic	Remember your multiplication tables? ... me neither. Brush up on your multiplication, division, and factoring skills with this exciting game. No calculators allowed!	57
769	es	Aritmética	\N	57
770	nl	Rekenen	\N	57
771	ar	arithmetic	\N	57
772	hr	Aritmetika	\N	57
773	el	Αριθμητικός	\N	57
774	zh_TW	算數	\N	57
775	pl	Arytmetyka	\N	57
776	pt_BR	blackbody-spectrum	\N	58
777	en	Blackbody Spectrum	How does the blackbody spectrum of the sun compare to visible light? Learn about the blackbody spectrum of the sun, a light bulb, an oven, and the earth. Adjust the temperature to see the wavelength and intensity of the spectrum change. View the color of the peak of the spectral curve.	58
778	es	blackbody-spectrum	\N	58
779	it	blackbody-spectrum	\N	58
780	nl	blackbody-spectrum	\N	58
781	vi	blackbody-spectrum	\N	58
782	sk	blackbody-spectrum	\N	58
783	hr	Zračenje crnog tijela	\N	58
784	zh_TW	黑體幅射光譜	\N	58
785	pl	Spektrum ciała doskonale czarnego	\N	58
786	el	charges-and-fields	\N	59
787	en	Charges and Fields	Move point charges around on the playing field and then view the electric field, voltages, equipotential lines, and more. It's colorful, it's dynamic, it's free.	59
788	es	charges-and-fields	\N	59
789	nl	charges-and-fields	\N	59
790	pt_BR	charges-and-fields	\N	59
791	sk	charges-and-fields	\N	59
792	ro	Sarcini si Campuri Electrice	\N	59
793	ar	الشحنات والمجالات	\N	59
794	hr	Naboj i Električno polje	\N	59
795	zh_TW	電荷及電場	\N	59
796	pl	Ładunki i pola elektryczne	\N	59
797	nl	Functie van een grafiek bepalen	\N	60
798	en	Curve Fitting	With your mouse, drag data points and their error bars, and watch the best-fit polynomial curve update instantly. You choose the type of fit: linear, quadratic, cubic, or quartic. The reduced chi-square statistic shows you when the fit is good. Or you can try to find the best fit by manually adjusting fit parameters.	60
799	es	curve-fitting	\N	60
800	pt_BR	curve-fitting	\N	60
801	hr	Prilagodba podataka polinomom	\N	60
802	zh_TW	Curve Fitting 曲線配適	\N	60
803	pl	Krzywa dopasowania	\N	60
804	nl	Grafieken van vergelijkingen tekenen	\N	61
805	en	Equation Grapher	Learn about graphing polynomials. The shape of the curve changes as the constants are adjusted. View the curves for the individual terms (e.g. y=bx ) to see how they add to generate the polynomial curve.	61
806	es	equation-grapher	\N	61
807	si	equation-grapher	\N	61
808	vi	equation-grapher	\N	61
809	pt_BR	equation-grapher	\N	61
810	hr	Kvadratna jednadžba	\N	61
811	sq	grafiku i ekuacionit	\N	61
812	zh_TW	Equation Grapher 方程式繪圖	\N	61
813	pl	Kreator wykresu równania	\N	61
814	en	Estimation	Explore size estimation in one, two and three dimensions! Multiple levels of difficulty allow for progressive skill improvement.	62
815	es	Estimación	\N	62
816	pt_BR	estimation	\N	62
817	nl	Schatting	\N	62
818	hr	Procjena	\N	62
819	pl	Oszacowania	\N	62
820	zh_TW	估計	\N	62
821	da	faradays-law	\N	63
822	en	Faraday's Law	Light a light bulb by waving a magnet. This demonstration of Faraday's Law shows you how to reduce your power bill at the expense of your grocery bill.	63
823	es	faradays-law	\N	63
824	nl	faradays-law	\N	63
825	vi	faradays-law	\N	63
826	ro	Legea lui Faraday	\N	63
827	in	faradays-law	\N	63
828	zh_CN	法拉第电磁感应定律	\N	63
829	hr	Faradayev zakon indukcije	\N	63
830	pl	Prawo Faradaya	\N	63
831	zh_TW	法拉第定律	\N	63
832	en	Friction	Learn how friction causes a material to heat up and melt. Rub two objects together and they heat up. When one reaches the melting temperature, particles break free as the material melts away.	64
833	vi	friction	\N	64
834	nl	Wrijving	\N	64
835	hr	Trenje	\N	64
836	pl	Tarcie	\N	64
837	zh_TW	摩擦	\N	64
838	en	Geometric Optics	How does a lens form an image? See how light rays are refracted by a lens. Watch how the image changes when you adjust the focal length of the lens, move the object, move the lens, or move the screen.	65
839	es	geometric-optics	\N	65
840	it	geometric-optics	\N	65
841	nl	geometric-optics	\N	65
842	sk	geometric-optics	\N	65
843	vi	geometric-optics	\N	65
844	cs	geometric-optics	\N	65
845	el	geometric-optics	\N	65
846	sq	geometric-optics	\N	65
847	sl	geometric-optics	\N	65
848	pl	geometric-optics	\N	65
849	hr	Optička Klupa	\N	65
850	zh_TW	幾何光學	\N	65
851	pt	geometric-optics	\N	65
852	en	Lunar Lander	Can you avoid the boulder field and land safely, just before your fuel runs out, as Neil Armstrong did in 1969? Our version of this classic video game accurately simulates the real motion of the lunar lander with the correct mass, thrust, fuel consumption rate, and lunar gravity. The real lunar lander is very hard to control.	66
853	es	lunar-lander	\N	66
857	pl	Lądownik księżycowy	\N	66
858	et	Kuul maanduja	\N	66
859	zh_TW	月球登陸器	\N	66
860	en	Masses & Springs	A realistic mass and spring laboratory. Hang masses from springs and adjust the spring stiffness and damping. You can even slow time. Transport the lab to different planets. A chart shows the kinetic, potential, and thermal energy for each spring.	67
861	es	mass-spring-lab	\N	67
862	it	mass-spring-lab	\N	67
863	ja	mass-spring-lab	\N	67
864	nl	mass-spring-lab	\N	67
865	sk	mass-spring-lab	\N	67
866	sl	mass-spring-lab	\N	67
867	fr	mass-spring-lab	\N	67
868	el	mass-spring-lab	\N	67
869	vi	mass-spring-lab	\N	67
870	zh_TW	mass-spring-lab	\N	67
871	ar	الكتل والزنبرك	\N	67
872	zh_CN	重物和弹簧	\N	67
873	cs	Závaží a pružiny	\N	67
874	hr	mass-spring-lab	\N	67
875	pl	Ciężarki na sprężynach	\N	67
876	et	Koormised ja vedrud	\N	67
877	en	My Solar System	Build your own system of heavenly bodies and watch the gravitational ballet. With this orbit simulator, you can set initial positions, velocities, and masses of 2, 3, or 4 bodies, and then see them orbit each other.	68
878	es	my-solar-system	\N	68
879	nl	my-solar-system	\N	68
880	it	My Solar System	\N	68
881	hr	Solarni sustavi	\N	68
882	el	Το ηλιακό μου σύστημα	\N	68
883	pl	Mój Układ Słoneczny	\N	68
884	zh_TW	我的太陽系	\N	68
885	en	Ohm's Law	See how the equation form of Ohm's law relates to a simple circuit. Adjust the voltage and resistance, and see the current change according to Ohm's law. The sizes of the symbols in the equation change to match the circuit diagram.	69
886	es	ohms-law	\N	69
887	mn	ohms-law	\N	69
888	nl	ohms-law	\N	69
889	vi	ohms-law	\N	69
890	nb	ohms-law	\N	69
891	ro	Legea lui Ohm	\N	69
892	sk	ohms-law	\N	69
893	fi	ohms-law	\N	69
894	it	ohms-law	\N	69
895	pl	Prawo Ohma	\N	69
896	hr	Ohmov Zakon	\N	69
897	zh_TW	歐姆定律	\N	69
898	de	pendulum-lab	\N	70
899	en	Pendulum Lab	Play with one or two pendulums and discover how the period of a simple pendulum depends on the length of the string, the mass of the pendulum bob, and the amplitude of the swing. It's easy to measure the period using the photogate timer. You can vary friction and the strength of gravity. Use the pendulum to find the value of g on planet X. Notice the anharmonic behavior at large amplitude.	70
900	es	pendulum-lab	\N	70
901	ja	pendulum-lab	\N	70
902	nl	pendulum-lab	\N	70
903	sk	pendulum-lab	\N	70
904	hr	pendulum-lab	\N	70
905	cs	pendulum-lab	\N	70
906	fr	pendulum-lab	\N	70
907	el	pendulum-lab	\N	70
908	sl	pendulum-lab	\N	70
909	zh_TW	pendulum-lab	\N	70
910	en	Plinko Probability	Play Plinko and develop your knowledge of statistics. Drops balls through a triangular grid of pegs and see the balls random walk through the lattice. Watch the histogram of final positions build up and approach the binomial distribution. Inspired by the Virtual Lab in Probability and Statistics at U. Alabama in Huntsville (www.math.uah.edu/stat)	71
911	es	plinko-probability	\N	71
912	nl	Knikkerspel en waarschijnlijkheidsverdeling	\N	71
913	hr	Pascalov trokut	\N	71
914	pl	Deska Galtona -prawdopodobieństwo	\N	71
915	zh_TW	二項分佈彈珠台機率	\N	71
916	en	Projectile Motion	Blast a Buick out of a cannon! Learn about projectile motion by firing various objects. Set the angle, initial speed, and mass. Add air resistance. Make a game out of this simulation by trying to hit a target.	72
917	es	projectile-motion	\N	72
918	sk	projectile-motion	\N	72
919	tr	projectile-motion	\N	72
920	vi	projectile-motion	\N	72
921	hr	Gibanje projektila	\N	72
922	nl	Beweging projectiel	\N	72
923	fr	Mouvement d'un projectile	\N	72
924	pl	Rzuty	\N	72
925	zh_CN	拋物線運動	\N	72
926	zh_TW	拋物線運動	\N	72
927	en	Resistance in a Wire	Learn about the physics of resistance in a wire. Change its resistivity, length, and area to see how they affect the wire's resistance. The sizes of the symbols in the equation change along with the diagram of a wire.	73
928	es	resistance-in-a-wire	\N	73
929	mn	resistance-in-a-wire	\N	73
930	nl	resistance-in-a-wire	\N	73
931	ro	Rezistenta intr-un cablu	\N	73
932	sk	resistance-in-a-wire	\N	73
933	it	resistance-in-a-wire	\N	73
934	hr	Otpor vodiča	\N	73
935	pl	Rezystancja przewodnika	\N	73
936	zh_TW	電線的電阻	\N	73
937	en	Stern-Gerlach Experiment	The classic Stern-Gerlach Experiment shows that atoms have a property called spin. Spin is a kind of intrinsic angular momentum, which has no classical counterpart. When the z-component of the spin is measured, one always gets one of two values: spin up or spin down.	74
938	es	Experimento de Stern-Gerlach	\N	74
939	nl	stern-gerlach	\N	74
940	hr	Stern-Gerlachov eksperiment	\N	74
941	en	Vector Addition	Learn how to add vectors. Drag vectors onto a graph, change their length and angle, and sum them together. The magnitude, angle, and components of each vector can be displayed in several formats.	75
942	es	vector-addition	\N	75
943	sk	vector-addition	\N	75
944	vi	vector-addition	\N	75
945	nl	Optellen van Vectoren	\N	75
946	hr	Zbrajanje vektora	\N	75
947	zh_TW	向量加法	\N	75
948	pl	Dodawanie wektorów	\N	75
949	de	wave-on-a-string	\N	76
950	en	Wave on a String	Watch a string vibrate in slow motion. Wiggle the end of the string and make waves, or adjust the frequency and amplitude of an oscillator. Adjust the damping and tension. The end can be fixed, loose, or open.	76
951	es	wave-on-a-string	\N	76
952	it	wave-on-a-string	\N	76
953	nl	wave-on-a-string	\N	76
954	sk	wave-on-a-string	\N	76
955	el	wave-on-a-string	\N	76
956	vi	wave-on-a-string	\N	76
957	hr	Valovi na žici	\N	76
958	zh_TW	弦波	\N	76
959	pl	Fale na linie	\N	76
\.


--
-- Data for Name: phet_user; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY phet_user (id, email, password, teammember) FROM stdin;
1	olsonsjc@gmail.com	WH39ah79fP15QF79Tv0pOv0b/SY=	t
2	guest@phet.colorado.edu	Wv1h/3k/QAX9JQts/TMbfvz9	f
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
33	reactions-and-rates	1	3	10	33541	1248296588
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

COPY simulation (id, name, type, project) FROM stdin;
1	balloons	0	1
2	battery-resistor-circuit	0	2
3	battery-voltage	0	3
4	band-structure	0	4
5	covalent-bonds	0	4
6	bound-states	0	4
7	circuit-construction-kit-ac	0	5
8	circuit-construction-kit-dc	0	5
9	color-vision	0	6
10	conductivity	0	7
11	discharge-lamps	0	8
12	eating-and-exercise	0	9
13	efield	0	10
14	electric-hockey	0	11
15	energy-skate-park	0	12
16	magnets-and-electromagnets	0	13
17	generator	0	13
18	magnet-and-compass	0	13
19	faraday	0	13
20	forces-1d	0	14
21	fourier	0	15
22	glaciers	0	16
23	greenhouse	0	17
24	hydrogen-atom	0	18
25	gas-properties	0	19
26	balloons-and-buoyancy	0	19
27	reversible-reactions	0	19
28	lasers	0	20
29	maze-game	0	21
30	microwaves	0	22
31	motion-2d	0	23
32	moving-man	0	24
33	mri	0	25
34	optical-quantum-control	0	26
35	molecular-motors	0	27
36	stretching-dna	0	27
37	optical-tweezers	0	27
38	ph-scale	0	28
39	photoelectric	0	29
40	quantum-tunneling	0	30
41	davisson-germer	0	31
42	quantum-wave-interference	0	31
43	radio-waves	0	32
44	reactions-and-rates	0	33
45	torque	0	34
46	rotation	0	34
47	rutherford-scattering	0	35
48	self-driven-particle-model	0	36
49	semiconductor	0	37
50	signal-circuit	0	38
51	soluble-salts	0	39
52	sound	0	40
53	sim2	0	41
54	sim1	0	41
55	travoltage	0	42
56	wave-interference	0	43
57	arithmetic	1	44
58	blackbody-spectrum	1	45
59	charges-and-fields	1	46
60	curve-fitting	1	47
61	equation-grapher	1	48
62	estimation	1	49
63	faradays-law	1	50
64	friction	1	51
65	geometric-optics	1	52
66	lunar-lander	1	53
67	mass-spring-lab	1	54
68	my-solar-system	1	55
69	ohms-law	1	56
70	pendulum-lab	1	57
71	plinko-probability	1	58
72	projectile-motion	1	59
73	resistance-in-a-wire	1	60
74	stern-gerlach	1	61
75	vector-addition	1	62
76	wave-on-a-string	1	63
\.


--
-- Data for Name: translated_string; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY translated_string (id, key, value, translation) FROM stdin;
65	home.header	互动科学模拟	79
106	troubleshooting.main.q4.title	OIHSRLTNSLRKNTSLRKNT	80
107	simulationPage.title	{0} ({1})	80
63	nav.motion	运动	79
64	language.name	中文	79
66	home.subheader	有趣，互动，以研究为基础的模拟物理现象从碧项目在科罗拉多大学	79
67	home.playWithSims	玩模拟... >	79
68	nav.home	家	79
69	nav.simulations	模拟	79
70	nav.featured	精选模拟	79
71	nav.new	新的模拟	79
72	nav.physics	物理	79
73	nav.sound-and-waves	健全和波	79
74	nav.work-energy-and-power	能源和电力工作	79
75	nav.heat-and-thermodynamics	热和热力学	79
76	nav.quantum-phenomena	量子现象	79
77	nav.light-and-radiation	光及辐射	79
78	nav.electricity-magnets-and-circuits	电力和磁铁和电路	79
79	nav.biology	生物学	79
80	nav.chemistry	化学	79
81	nav.earth-science	地球科学	79
82	nav.math	数学	79
83	nav.tools	工具	79
84	nav.applications	应用	79
85	nav.cutting-edge-research	前沿研究	79
86	nav.all	所有模拟	79
87	nav.about	关于PhET	79
88	simulationMainPanel.translatedVersions	翻译文本	79
89	sponsors.principalSponsors	主要提案国	79
90	sponsors.hewlett	使赠款，以解决最严重的社会和环境问题所面临的社会，风险资本，负责任的投资，可能会发挥作用随着时间的推移	79
91	sponsors.nsf	一个独立的联邦机构由美国国会创办于1950年，以促进科学的进步	79
92	sponsors.ksu	沙特国王大学力求成为一个领先的教育和技术创新，科学发现和创造性通过促进的气氛中进行的智力启迪和伙伴关系，社会的繁荣	79
93	home.title	PhET: 免费在线物理，化学，生物，地球科学和数学模拟	79
94	simulationDisplay.title	{0} - PhET 模拟	79
95	simulationPage.title	{0} ({1})	79
97	language.dir	ltr	79
98	nav.troubleshooting.main	疑难解答	79
99	troubleshooting.main.title	疑难解答- PhET模拟	79
100	troubleshooting.main.intro	此页将帮助您解决一些问题，人们普遍有运行我们的程序。如果你根本无法解决您的问题，请通过电子邮件通知我们在以下的电子邮件地址 {0}	79
101	troubleshooting.main.java	Java的安装和故障检修	79
102	troubleshooting.main.flash	闪光安装和故障检修	79
103	troubleshooting.main.javascript	JavaScript的疑难解答（注：这是您的浏览器，而不是模拟）	79
104	troubleshooting.main.q2.title	的系统要求是什么运行碧模拟？	79
105	troubleshooting.main.q2.answer	<p><strong>Windows Systems</strong><br/>Intel Pentium processor<br/>Microsoft Windows 98SE/2000/XP/Vista<br/>256MB RAM minimum<br/>Approximately 97 MB available disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Sun Java 1.5.0_15 or later<br/>Macromedia Flash 8 or later<br/>Microsoft Internet Explorer 6 or later, Firefox 2 or later</p><p><strong>Macintosh Systems</strong><br/>G3, G4, G5 or Intel processor<br/>OS 10.4 or later<br/>256MB RAM minimum<br/>Approximately 86 MB available disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Apple Java 1.5.0_19 or later<br/>Macromedia Flash 8 or later<br/>Safari 2 or later, Firefox 2 or later</p><p><strong>Linux Systems</strong><br/>Intel Pentium processor<br/>256MB RAM minimum<br/>Approximately 81 MB disk space (for full <a {0}>installation</a>)<br/>1024x768 screen resolution or better<br/>Sun Java 1.5.0_15 or later<br/>Macromedia Flash 8 or later<br/>Firefox 2 or later<br/></p><p><strong>Support Software</strong></p><p>Some of our simulations use Java, and some use Flash. Both of these are available as free downloads, and our downloadable <a {0}>PhET Offline Website Installer</a> includes Java for those who need it.</p>	79
\.


--
-- Data for Name: translation; Type: TABLE DATA; Schema: public; Owner: phet
--

COPY translation (id, locale) FROM stdin;
80	nl_NL
79	zh_CN
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
-- Name: fk1854a79ac047ff77; Type: FK CONSTRAINT; Schema: public; Owner: phet
--

ALTER TABLE ONLY translated_string
    ADD CONSTRAINT fk1854a79ac047ff77 FOREIGN KEY (translation) REFERENCES translation(id);


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


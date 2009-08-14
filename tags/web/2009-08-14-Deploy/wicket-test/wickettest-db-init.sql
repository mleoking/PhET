#INSERT INTO simulation (project, simulation, type) VALUES ('arithmetic', 'arithmetic', 1);
#INSERT INTO simulation (project, simulation, type) VALUES ('vector-addition', 'vector-addition', 1);
#INSERT INTO simulation (project, simulation, type) VALUES ('moving-man', 'moving-man', 0);
#INSERT INTO simulation (project, simulation, type) VALUES ('equation-grapher', 'equation-grapher', 1);
#INSERT INTO simulation (project, simulation, type) VALUES ('mass-spring-lab', 'mass-spring-lab', 1);
#INSERT INTO simulation (project, simulation, type) VALUES ('energy-skate-park', 'energy-skate-park', 0);

INSERT INTO sim_type (name) VALUES ('java'), ('flash');

# for testing purposes
INSERT INTO project (name, sim_type) VALUES ('arithmetic', 1);
INSERT INTO simulation (name, project) VALUES ('arithmetic', 1);
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (1, 'en', 'Arithmetic', 'Remember your multiplication tables? ... me neither. Brush up on your multiplication, division and factoring skills with this exciting game. No calculators allowed!');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (1, 'es', 'Aritmética', 'Recuerde sus tablas de multiplicar? ... yo tampoco. Pon tu multiplicación, división y habilidades facturaje con este emocionante juego. Calculadoras no permitido!');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (1, 'nl', 'Rekenen', 'Vergeet niet uw vermenigvuldiging tafels? ... mij ook niet. Penseel op uw vermenigvuldiging, deling en factoring vaardigheden met dit spannende spel. Geen rekenmachines toegestaan!');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (1, 'ar', 'علم الحساب', 'تذكر جداول الضرب الخاص بك؟ ... ليست لي. فرشاة حتى على تكاثر والانقسام والتعميل مهارات هذه اللعبة المثيرة. لا يسمح للحاسبات!');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (1, 'hr', 'Aritmetika', 'Zapamti Vaše umnožavanje tablica? ... ni meni. Očetkati na vašem razmnožavanje, podjela i faktoring vještine s ovim uzbudljiva igra. Ne Kalkulatori dozvoljeno!');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (1, 'el', 'Αριθμητικός', 'Να θυμάστε σας πολλαπλασιασμό πινάκων; ... δεν μου. Brush up on your πολλαπλασιασμού, της διαίρεσης και του factoring δεξιότητες με αυτό το συναρπαστικό παιχνίδι. Αριθμομηχανές Δεν επιτρέπεται!');

INSERT INTO project (name, sim_type) VALUES ('vector-addition', 1);
INSERT INTO simulation (name, project) VALUES ('vector-addition', 2);
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (2, 'en', 'Vector Addition', 'Learn how to add vectors. Drag vectors onto a graph, change their length and angle, and sum them together. The magnitude, angle, and components of each vector can be displayed in several formats.');

INSERT INTO project (name, sim_type) VALUES ('mass-spring-lab', 1);
INSERT INTO simulation (name, project) VALUES ('mass-spring-lab', 3);
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (3, 'en', 'Masses & Springs', 'A realistic mass and spring laboratory. Hang masses from springs and adjust the spring stiffness and damping. You can even slow time. Transport the lab to different planets. A chart shows the kinetic, potential, and thermal energy for each spring.');

INSERT INTO project (name, sim_type) VALUES ('moving-man', 0);
INSERT INTO simulation (name, project) VALUES ('moving-man', 4);
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'en', 'The Moving Man', 'Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'es', 'El Hombre Móvil', 'La aplicacion del Hombre Móvil.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'et', 'The Moving Man', 'Õpi tundma asukoha, kiiruse ja kiirenduse graafikuid. Liiguta meest hiire abil edasi-tagasi ja kuva tema liikumise graafikud. Sisesta positsiooni, kiiruse ja kiirenduse parameetrid ning lase simulatsioonil meest liigutada.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'fa', 'The Moving Man', 'Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'nl', 'Het bewegende mannetje', 'Bewegingen worden weergegeven in plaats-tijd, snelheid-tijd en versnelling-tijd diagrammen. Beweeg dit mannetje met de muis over het scherm en bekijk de diagrammen van de beweging. Stel plaats, tijd en versnelling in en laat het mannetje lopen terwijl de diagrammen verschijnen.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'sk', 'MotionMan', 'Nau&#269;te sa rozumie&#357; grafom polohy, r&#253;chlosti a zr&#253;chlenia. Pomocou my&#353;ky pohybujte &#269;lovie&#269;ikom (MotionManom) sem a tam a sledujte grafy jeho pohybu. Alebo si nastavte poz&#237;ciu, r&#253;chlos&#357; alebo zr&#253;chlenie a dovo&#318;te po&#269;ita&#269;u, aby pohyboval MotionManom za V&#225;s.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'sl', 'Gibanje človeka', 'Z gibanjem človeka odkriješ spreminjanje poti, hitrosti in pospeška.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'uk', 'The Moving Man', 'Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'el', 'Ο Κινούμενος Άνδρας', 'Μάθετε για τα γραφήματα θέσης, ταχύτητας και επιτάχυνσης. Μετακινήστε τον μικρό άνδρα μπροστά και πίσω με το ποντίκι και παραστήστε γραφικά την κινησή του. Καθορίστε τη θέση, την ταχύτητά του ή την επιτάχυνσή του και αφήστε την προσομοίωση να κινήσει τον άνδρα για εσάς. Μετάφραση: Βαγγέλης Κολτσάκης.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'tn', 'Monna yo o tsamayang', 'Tiriso ya monna yo o tsamayang');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'de', 'Der bewegte Mann', 'Lernen Sie etwas über Weg-Zeit; Geschwindigkeits-Zeit und Beschleunigungs-Zeit-Diagramme. Bewegen Sie den kleinen Mann mit der Maus hin und her und verpassen Sie ihm eine Bewegung. Legen Sie Ort, Geschwindigkeit oder Beschleunigung fest und lassen Sie den kleinen Mann durch die Simulation bewegen.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'ar', 'الرجل المتحرك', 'تعلم عن الرسوم البيانية للمكان ،للسرعة، للتسارع. حرك الرجل الصغير ذهابا وإيابا بالفأرة وارسم حركته. حدد المكان، السرعة والتسارع واسمح للمحاكاة أن تحرك الرجل لك.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'pt', 'Movimento', 'Estude os gráficos de posição, velocidade e aceleração. Mova um pequeno boneco e registre seu movimento. Configure a posiçãp, velocidade e aceleração e deixe a simulação realizar a movimentação automática do boneco.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'zh_CN', '运动图像', 'Learn about position, velocity, and acceleration graphs. Move the little man back and forth with the mouse and plot his motion. Set the position, velocity, or acceleration and let the simulation move the man for you.');
INSERT INTO localized_simulation (simulation, locale, title, description) VALUES (4, 'hr', 'Gibanje', 'Nauči nešto o položaju, putu, brzini i akceleraciji! Pomakni čovječuljka mišem i pogledaj grafički prikaz njegova gibanja! Odaberi položaj, početnu brzinu ili akceleraciju čovječuljka i simulacije će napraviti zadano!');
























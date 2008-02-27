/* ------------------------------------------------------------ */
/*
HTTrack Website Copier, Offline Browser for Windows and Unix
Copyright (C) Xavier Roche and other contributors

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


Important notes:

- We hereby ask people using this source NOT to use it in purpose of grabbing
emails addresses, or collecting any other private information on persons.
This would disgrace our work, and spoil the many hours we spent on it.


Please visit our Website: http://www.httrack.com
*/


/* ------------------------------------------------------------ */
/* File: httrack.c subroutines:                                 */
/*       wizard system (accept/refuse links)                    */
/* Author: Xavier Roche                                         */
/* ------------------------------------------------------------ */

/* Internal engine bytecode */
#define HTS_INTERNAL_BYTECODE

#include "htscore.h"
#include "htswizard.h"

/* specific definitions */
#include "htsbase.h"
#include <ctype.h>
/* END specific definitions */

// version 1 pour httpmirror
// flusher si on doit lire peu � peu le fichier
#define test_flush if (opt->flush) { fflush(opt->log); fflush(opt->log); }

// pour all�ger la syntaxe, des raccourcis sont cr��s
#define urladr   (liens[ptr]->adr)
#define urlfil   (liens[ptr]->fil)

// lib�rer filters[0] pour ins�rer un �l�ment dans filters[0]
#define HT_INSERT_FILTERS0 do {\
  int i;\
  if (*opt->filters.filptr > 0) {\
    for(i = (*opt->filters.filptr)-1 ; i>=0 ; i--) {\
      strcpybuff((*opt->filters.filters)[i+1],(*opt->filters.filters)[i]);\
    }\
  }\
  (*opt->filters.filters)[0][0]='\0';\
  (*opt->filters.filptr)++;\
  assertf((*opt->filters.filptr) < opt->maxfilter); \
} while(0)

typedef struct htspair_t {
	char *tag;
	char *attr;
} htspair_t;

/* "embedded" */
htspair_t hts_detect_embed[] = {
	{ "img", "src" },
	{ "link", "href" },

	/* embedded script hack */
	{ "script", ".src" },

	/* style */
	{ "style", "import" },

	{ NULL, NULL }
};

/* Internal */
static int hts_acceptlink_(httrackp* opt,
													int ptr,int lien_tot,lien_url** liens,
													char* adr,char* fil,
													char* tag, char* attribute,
													int* set_prio_to,
													int* just_test_it);

/*
httrackp opt	 bloc d'options
int ptr,int lien_tot,lien_url** liens
							 relatif aux liens
char* adr,char* fil
							 adresse/fichier � tester
char** filters,int filptr,int filter_max
							 relatif aux filtres
robots_wizard* robots
							 relatif aux robots
int* set_prio_to
							 callback obligatoire "capturer ce lien avec prio=N-1"
int* just_test_it
							 callback optionnel "ne faire que tester ce lien �ventuellement"
retour:
0 accept�
1 refus�
-1 pas d'avis
*/

int hts_acceptlink(httrackp* opt,
									 int ptr,int lien_tot,lien_url** liens,
									 char* adr,char* fil,
									 char* tag, char* attribute,
									 int* set_prio_to,
									 int* just_test_it) 
{
	int forbidden_url = hts_acceptlink_(opt, ptr, lien_tot, liens,
		adr, fil, tag, attribute, set_prio_to, just_test_it);
	int prev_prio = set_prio_to ? *set_prio_to : 0;

	// -------------------- PHASE 6 --------------------
	{
		int test_url = RUN_CALLBACK3(opt, check_link, adr, fil, forbidden_url);
		if (test_url != -1) {
			forbidden_url = test_url;
			if (set_prio_to)
				*set_prio_to = prev_prio;
		}
	}

	return forbidden_url;
}

static int cmp_token(const char *tag, const char *cmp) {
	int p;
	return (strncasecmp(tag, cmp, ( p = (int) strlen(cmp) ) ) == 0
		&& !isalnum((unsigned char) tag[p]));
}

static int hts_acceptlink_(httrackp* opt,
													int ptr,int lien_tot,lien_url** liens,
													char* adr,char* fil,
													char* tag, char* attribute,
													int* set_prio_to,
													int* just_test_it) 
{
  int forbidden_url=-1;
  int meme_adresse;
	int embedded_triggered = 0;
#define _FILTERS     (*opt->filters.filters)
#define _FILTERS_PTR (opt->filters.filptr)
#define _ROBOTS      ((robots_wizard*)opt->robotsptr)
  int may_set_prio_to=0;

  // -------------------- PHASE 0 --------------------

  /* Infos */
  if ((opt->debug>1) && (opt->log!=NULL)) {
    HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"wizard test begins: %s%s"LF,adr,fil);
    test_flush;
  }
  
  /* Already exists? Then, we know that we knew that this link had to be known */
  if (adr[0] != '\0'
    && fil[0] != '\0'
    && opt->hash != NULL
    && hash_read(opt->hash, adr, fil, 1, opt->urlhack) >= 0
    ) {
    return 0;  /* Yokai */
  }
  
  // -------------------- PRELUDE OF PHASE 3-BIS --------------------

	/* Built-in known tags (<img src=..>, ..) */
	if (forbidden_url != 0 && opt->nearlink && tag != NULL && attribute != NULL) {
		int i;
		for(i = 0 ; hts_detect_embed[i].tag != NULL ; i++) {
			if (cmp_token(tag, hts_detect_embed[i].tag)
				&& cmp_token(attribute, hts_detect_embed[i].attr)
				) 
			{
				embedded_triggered = 1;
				break;
			}
		}
	}


  // -------------------- PHASE 1 --------------------

  /* Doit-on traiter les non html? */
  if ((opt->getmode & 2)==0) {    // non on ne doit pas
    if (!ishtml(opt,fil)) {  // non il ne faut pas
      //adr[0]='\0';    // ne pas traiter ce lien, pas traiter
      forbidden_url=1;    // interdire r�cup�ration du lien
      if ((opt->debug>1) && (opt->log!=NULL)) {
        HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"non-html file ignored at %s : %s"LF,adr,fil);
        test_flush;
      }
      
    }
  }
  
  /* Niveau 1: ne pas parser suivant! */
  if (ptr>0) {
    if ( ( liens[ptr]->depth <= 0 ) || ( liens[ptr]->depth <= 1 && !embedded_triggered ) ) {
      forbidden_url=1;    // interdire r�cup�ration du lien
      if ((opt->debug>1) && (opt->log!=NULL)) {
        HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"file from too far level ignored at %s : %s"LF,adr,fil);
        test_flush;
      }
    }
  }

  /* en cas d'�chec en phase 1, retour imm�diat! */
  if (forbidden_url == 1) {
    return forbidden_url;
  }
  
  // -------------------- PHASE 2 --------------------

  // ------------------------------------------------------
  // doit-on traiter ce lien?.. v�rifier droits de d�placement
  meme_adresse=strfield2(adr,urladr);
  if ((opt->debug>1) && (opt->log!=NULL)) {
    HTS_LOG(opt,LOG_DEBUG); 
    if (meme_adresse) 
      fprintf(opt->log,"Compare addresses: %s=%s"LF,adr,urladr);
    else
      fprintf(opt->log,"Compare addresses: %s!=%s"LF,adr,urladr);
    test_flush;
  }
  if (meme_adresse) {  // m�me adresse 
    {  // tester interdiction de descendre
      // MODIFIE : en cas de remont�e puis de redescente, il se pouvait qu'on ne puisse pas atteindre certains fichiers
      // probl�me: si un fichier est virtuellement accessible via une page mais dont le lien est sur une autre *uniquement*..
      char BIGSTK tempo[HTS_URLMAXSIZE*2];
      char BIGSTK tempo2[HTS_URLMAXSIZE*2];
      tempo[0] = tempo2[0] = '\0';
      
      // note (up/down): on calcule � partir du lien primaire, ET du lien pr�c�dent.
      // ex: si on descend 2 fois on peut remonter 1 fois
      
      if (lienrelatif(tempo,fil,liens[liens[ptr]->premier]->fil)==0) {
        if (lienrelatif(tempo2,fil,liens[ptr]->fil)==0) {
          if ((opt->debug>1) && (opt->log!=NULL)) {
            HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"build relative links to test: %s %s (with %s and %s)"LF,tempo,tempo2,liens[liens[ptr]->premier]->fil,liens[ptr]->fil);
            test_flush;
          }
          
          // si vient de primary, ne pas tester lienrelatif avec (car host "diff�rent")
          /*if (liens[liens[ptr]->premier] == 0) {   // vient de primary
          }
          */
          
          // NEW: finalement OK, sauf pour les moved rep�r�s par link_import
          // PROBLEME : annul� a cause d'un lien �ventuel isol� accept�..qui entrainerait un miroir
          
          // (test m�me niveau (NOUVEAU � cause de certains probl�mes de filtres non int�gr�s))
          // NEW
          if ( 
            (tempo[0]  != '\0' && tempo[1]  != '\0' && strchr(tempo+1,'/') == 0)
            ||
            (tempo2[0] != '\0' && tempo2[1] != '\0' && strchr(tempo2+1,'/') == 0) 
            ) {
            if (!liens[ptr]->link_import) {   // ne r�sulte pas d'un 'moved'
              forbidden_url=0;
              if ((opt->debug>1) && (opt->log!=NULL)) {
                HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"same level link authorized: %s%s"LF,adr,fil);
                test_flush;
             }
            }
          }
          
          // down
          if ( (strncmp(tempo,"../",3)) || (strncmp(tempo2,"../",3)))  {   // pas mont�e sinon ne nbous concerne pas
            int test1,test2;
            if (!strncmp(tempo,"../",3))
              test1=0;
            else
              test1 = (strchr(tempo +((*tempo =='/')?1:0),'/')!=NULL);
            if (!strncmp(tempo2,"../",3))
              test2=0;
            else
              test2 = (strchr(tempo2+((*tempo2=='/')?1:0),'/')!=NULL);
            if ( (test1) && (test2) ) {   // on ne peut que descendre
              if ((opt->seeker & 1)==0) {  // interdiction de descendre
                forbidden_url=1;
                if ((opt->debug>1) && (opt->log!=NULL)) {
                  HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"lower link canceled: %s%s"LF,adr,fil);
                  test_flush;
                }
              } else {    // autoris� � priori - NEW
                if (!liens[ptr]->link_import) {   // ne r�sulte pas d'un 'moved'
                  forbidden_url=0;
                  if ((opt->debug>1) && (opt->log!=NULL)) {
                    HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"lower link authorized: %s%s"LF,adr,fil);
                    test_flush;
                  }
                }
              }
            } else if ( (test1) || (test2) ) {   // on peut descendre pour acc�der au lien
              if ((opt->seeker & 1)!=0) {  // on peut descendre - NEW
                if (!liens[ptr]->link_import) {   // ne r�sulte pas d'un 'moved'
                  forbidden_url=0;
                  if ((opt->debug>1) && (opt->log!=NULL)) {
                    HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"lower link authorized: %s%s"LF,adr,fil);
                    test_flush;
                  }
                }
              }
            }
          }
          
          
          // up
          if ( (!strncmp(tempo,"../",3)) && (!strncmp(tempo2,"../",3)) ) {    // impossible sans monter
            if ((opt->seeker & 2)==0) {  // interdiction de monter
              forbidden_url=1;
              if ((opt->debug>1) && (opt->log!=NULL)) {
                HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"upper link canceled: %s%s"LF,adr,fil);
                test_flush;
              }
            } else {       // autoris� � monter - NEW
              if (!liens[ptr]->link_import) {   // ne r�sulte pas d'un 'moved'
                forbidden_url=0;
                if ((opt->debug>1) && (opt->log!=NULL)) {
                  HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"upper link authorized: %s%s"LF,adr,fil);
                  test_flush;
                }
              }
            }
          } else if ( (!strncmp(tempo,"../",3)) || (!strncmp(tempo2,"../",3)) ) {    // Possible en montant
            if ((opt->seeker & 2)!=0) {  // autoris� � monter - NEW
              if (!liens[ptr]->link_import) {   // ne r�sulte pas d'un 'moved'
                forbidden_url=0;
                if ((opt->debug>1) && (opt->log!=NULL)) {
                  HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"upper link authorized: %s%s"LF,adr,fil);
                  test_flush;
                }
              }
            }  // sinon autoris� en descente
          }
          
          
        } else {
          if (opt->log) {
            fprintf(opt->log,"Error building relative link %s and %s"LF,fil,liens[ptr]->fil);
            test_flush;
          }
        }
      } else {
        if (opt->log) {
          fprintf(opt->log,"Error building relative link %s and %s"LF,fil,liens[liens[ptr]->premier]->fil);
          test_flush;
        }
      }
      
    }  // tester interdiction de descendre?
    
    {  // tester interdiction de monter
      char BIGSTK tempo[HTS_URLMAXSIZE*2];
      char BIGSTK tempo2[HTS_URLMAXSIZE*2];
      if (lienrelatif(tempo,fil,liens[liens[ptr]->premier]->fil)==0) {
        if (lienrelatif(tempo2,fil,liens[ptr]->fil)==0) {
        } else {
          if (opt->log) { 
            fprintf(opt->log,"Error building relative link %s and %s"LF,fil,liens[ptr]->fil);
            test_flush;
          }
          
        }
      } else {
        if (opt->log) { 
          fprintf(opt->log,"Error building relative link %s and %s"LF,fil,liens[liens[ptr]->premier]->fil);
          test_flush;
        }
        
      }
    }   // fin tester interdiction de monter
    
  } else {    // adresse diff�rente, sortir?
    
    //if (!opt->wizard) {    // mode non wizard
    // doit-on traiter ce lien?.. v�rifier droits de sortie
    switch((opt->travel & 255)) {
    case 0: 
      if (!opt->wizard)    // mode non wizard
        forbidden_url=1; break;    // interdicton de sortir au dela de l'adresse
    case 1: {              // sortie sur le m�me dom.xxx
      size_t i = strlen(adr)-1;
      size_t j = strlen(urladr)-1;
      while( (i>0) && (adr[i]!='.')) i--;
      while( (j>0) && (urladr[j]!='.')) j--;
      i--; j--;
      while( (i>0) && (adr[i]!='.')) i--;
      while( (j>0) && (urladr[j]!='.')) j--;
      if ((i>0) && (j>0)) {
        if (!strfield2(adr+i,urladr+j)) {   // !=
          if (!opt->wizard) {   // mode non wizard
            //printf("refused: %s\n",adr);
            forbidden_url=1;  // pas m�me domaine  
            if ((opt->debug>1) && (opt->log!=NULL)) {
              HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"foreign domain link canceled: %s%s"LF,adr,fil);
              test_flush;
            }
          }
          
        } else {
          if (opt->wizard) {   // mode wizard
            forbidden_url=0;  // m�me domaine  
            if ((opt->debug>1) && (opt->log!=NULL)) {
              HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"same domain link authorized: %s%s"LF,adr,fil);
              test_flush;
            }
          }
        }
        
      } else
        forbidden_url=1;
            } 
      break;  
    case 2: {                      // sortie sur le m�me .xxx
      size_t i = strlen(adr)-1;
      size_t j = strlen(urladr)-1;
      while( (i>0) && (adr[i]!='.')) i--;
      while( (j>0) && (urladr[j]!='.')) j--;
      if ((i>0) && (j>0)) {
        if (!strfield2(adr+i,urladr+j)) {   // !-
          if (!opt->wizard) {   // mode non wizard
            //printf("refused: %s\n",adr);
            forbidden_url=1;  // pas m�me .xx  
            if ((opt->debug>1) && (opt->log!=NULL)) {
              HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"foreign location link canceled: %s%s"LF,adr,fil);
              test_flush;
            }
          }
        } else {
          if (opt->wizard) {   // mode wizard
            forbidden_url=0;  // m�me domaine  
            if ((opt->debug>1) && (opt->log!=NULL)) {
              HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"same location link authorized: %s%s"LF,adr,fil);
              test_flush;
            }
          }
        }
      } else forbidden_url=1;     
            } 
      break;
    case 7:                 // everywhere!!
      if (opt->wizard) {   // mode wizard
        forbidden_url=0;
        break;
      }
    }  // switch
    
    // ANCIENNE POS -- r�cup�rer les liens � c�t�s d'un lien (nearlink)
    
  }  // fin test adresse identique/diff�rente

  // -------------------- PHASE 3 --------------------

  // r�cup�rer les liens � c�t�s d'un lien (nearlink) (nvelle pos)
  if (forbidden_url != 0 && opt->nearlink) {
    if (!ishtml(opt,fil)) {  // non html
      //printf("ok %s%s\n",ad,fil);
      forbidden_url=0;    // autoriser
      may_set_prio_to=1+1; // set prio to 1 (parse but skip urls) if near is the winner
      if ((opt->debug>1) && (opt->log!=NULL)) {
        HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"near link authorized: %s%s"LF,adr,fil);
        test_flush;
      }
    }
  }

  // -------------------- PHASE 3-BIS --------------------

	/* Built-in known tags (<img src=..>, ..) */
	if (forbidden_url != 0 && embedded_triggered) {
		forbidden_url=0;    // autoriser
		may_set_prio_to=1+1; // set prio to 1 (parse but skip urls) if near is the winner
		if ((opt->debug>1) && (opt->log!=NULL)) {
			HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"near link authorized (friendly tag): %s%s"LF,adr,fil);
			test_flush;
		}
	}


  // -------------------- PHASE 4 --------------------
  
  // ------------------------------------------------------
  // Si wizard, il se peut qu'on autorise ou qu'on interdise 
  // un lien sp�cial avant m�me de tester sa position, sa hi�rarchie etc.
  // peut court-circuiter le forbidden_url pr�c�dent
  if (opt->wizard) { // le wizard entre en action..
    //
    int question=1;         // poser une question                            
    int force_mirror=0;     // pour mirror links
    int filters_answer=0;   // d�cision prise par les filtres
    char BIGSTK l[HTS_URLMAXSIZE*2];
    char BIGSTK lfull[HTS_URLMAXSIZE*2];
    
    if (forbidden_url!=-1) question=0;  // pas de question, r�solu
    
    // former URL compl�te du lien actuel
    strcpybuff(l,jump_identification(adr));
    if (*fil!='/') strcatbuff(l,"/");
    strcatbuff(l,fil);
    // full version (http://foo:bar@www.foo.com/bar.html)
    if (!link_has_authority(adr))
      strcpybuff(lfull,"http://");
    else
      lfull[0]='\0';
    strcatbuff(lfull,adr);
    if (*fil!='/') strcatbuff(lfull,"/");
    strcatbuff(lfull,fil);
    
    // tester filters (URLs autoris�es ou interdites explicitement)
    
    // si lien primaire on saute le joker, on est pas l�mur
    if (ptr==0) {  // lien primaire, autoriser
      question=1;    // la question sera r�solue automatiquement
      forbidden_url=0;
      may_set_prio_to=0;    // clear may-set flag
    } else {
      // eternal depth first
      // v�rifier r�cursivit� ext�rieure
      if (opt->extdepth>0) {
        if ( /*question && */ (ptr>0) && (!force_mirror)) {
          // well, this is kinda a hak
          // we don't want to mirror EVERYTHING, and we have to decide where to stop
          // there is no way yet to tag "external" links, and therefore links that are
          // "weak" (authorized depth < external depth) are just not considered for external
          // hack
          if (liens[ptr]->depth > opt->extdepth) {
            // *set_prio_to = opt->extdepth + 1;
            *set_prio_to = 1 + (opt->extdepth);
            may_set_prio_to=0;  // clear may-set flag
            forbidden_url=0;    // autoris�
            question=0;         // r�solution auto
            if ((opt->debug>1) && (opt->log!=NULL)) {
              if (question) {
                HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) ambiguous link accepted (external depth): link %s at %s%s"LF,l,urladr,urlfil);
              } else {
                HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) forced to accept link (external depth): link %s at %s%s"LF,l,urladr,urlfil);
              }
              test_flush;
            }
            
          }
        }
      }  
      
      // filters
      {
        int jok;
        char* mdepth="";
        // filters, 0=sait pas 1=ok -1=interdit
        {
          int jokDepth1=0,jokDepth2=0;
          int jok1=0,jok2=0;
          jok1  = fa_strjoker(/*url*/0, _FILTERS,*_FILTERS_PTR,lfull,NULL,NULL,&jokDepth1);
          jok2 =  fa_strjoker(/*url*/0, _FILTERS,*_FILTERS_PTR,l,    NULL,NULL,&jokDepth2);
          if (jok2 == 0) {      // #2 doesn't know
            jok = jok1;        // then, use #1
            mdepth = _FILTERS[jokDepth1];
          } else if (jok1 == 0) { // #1 doesn't know
            jok = jok2;        // then, use #2
            mdepth = _FILTERS[jokDepth2];
          } else if (jokDepth1 >= jokDepth2) { // #1 matching rule is "after" #2, then it is prioritary
            jok = jok1;
            mdepth = _FILTERS[jokDepth1];
          } else {                             // #2 matching rule is "after" #1, then it is prioritary
            jok = jok2;
            mdepth = _FILTERS[jokDepth2];
          }
        }
        
        if (jok == 1) {   // autoris�
          filters_answer=1;  // d�cision prise par les filtres
          question=0;    // ne pas poser de question, autoris�
          forbidden_url=0;  // URL autoris�e
          may_set_prio_to=0;    // clear may-set flag
          if ((opt->debug>1) && (opt->log!=NULL)) {
            HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) explicit authorized (%s) link: link %s at %s%s"LF,mdepth,l,urladr,urlfil);
            test_flush;
          }
        } else if (jok == -1) {  // forbidden
          filters_answer=1;  // d�cision prise par les filtres
          question=0;    // ne pas poser de question:
          forbidden_url=1;   // URL interdite
          if ((opt->debug>1) && (opt->log!=NULL)) {
            HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) explicit forbidden (%s) link: link %s at %s%s"LF,mdepth,l,urladr,urlfil);
            test_flush;
          }
        }  // sinon on touche � rien
      }
    }
    
    // v�rifier mode mirror links
    if (question) {
      if (opt->mirror_first_page) {    // mode mirror links
        if (liens[ptr]->precedent==0) {  // parent=primary!
          forbidden_url=0;    // autoris�
          may_set_prio_to=0;    // clear may-set flag
          question=1;         // r�solution auto
          force_mirror=5;     // mirror (5)
          if ((opt->debug>1) && (opt->log!=NULL)) {
            HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) explicit mirror link: link %s at %s%s"LF,l,urladr,urlfil);
            test_flush;
          }
        }
      }
    }
    
    // on doit poser la question.. peut on la poser?
    // (oui je sais quel preuve de d�licatesse, merci merci)      
    if ((question) && (ptr>0) && (!force_mirror)) {
      if (opt->wizard==2) {    // �liminer tous les liens non r�pertori�s comme autoris�s (ou inconnus)
        question=0;
        forbidden_url=1;
        if ((opt->debug>1) && (opt->log!=NULL)) {
          HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) ambiguous forbidden link: link %s at %s%s"LF,l,urladr,urlfil);
          test_flush;
        }
      }
    }
    
    // v�rifier robots.txt
    if (opt->robots) {
      int r = checkrobots(_ROBOTS,adr,fil);
      if (r == -1) {    // interdiction
#if DEBUG_ROBOTS
        printf("robots.txt forbidden: %s%s\n",adr,fil);
#endif
        // question r�solue, par les filtres, et mode robot non strict
        if ((!question) && (filters_answer) && (opt->robots == 1) && (forbidden_url!=1)) {
          r=0;    // annuler interdiction des robots
          if (!forbidden_url) {
            if ((opt->debug>1) && (opt->log!=NULL)) {
              HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"Warning link followed against robots.txt: link %s at %s%s"LF,l,adr,fil);
              test_flush;
            }
          }
        }
        if (r == -1) {    // interdire
          forbidden_url=1;
          question=0;
          if ((opt->debug>1) && (opt->log!=NULL)) {
            HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(robots.txt) forbidden link: link %s at %s%s"LF,l,adr,fil);
            test_flush;
          }
        }
      }
    }
    
    if (!question) {
      if ((opt->debug>1) && (opt->log!=NULL)) {
        if (!forbidden_url) {
          HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) shared foreign domain link: link %s at %s%s"LF,l,urladr,urlfil);
        } else {
          HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) cancelled foreign domain link: link %s at %s%s"LF,l,urladr,urlfil);
        }
        test_flush;
      }
#if BDEBUG==3
      printf("at %s in %s, wizard says: url %s ",urladr,urlfil,l);
      if (forbidden_url) printf("cancelled"); else printf(">SHARED<");
      printf("\n");
#endif 
    }

    /* en cas de question, ou lien primaire (enregistrer autorisations) */
    if (question || (ptr==0)) {
      const char* s;
      int n=0;
      
      // si primaire (plus bas) alors ...
      if ((ptr!=0) && (force_mirror==0)) {
        HTS_REQUEST_START;
        HT_PRINT("\n");
        HT_PRINT("At "); HT_PRINT(urladr); HT_PRINT(", there is a link ("); HT_PRINT(adr); HT_PRINT("/"); HT_PRINT(fil); HT_PRINT(") which goes outside the address."LF);
        HT_PRINT("What should I do? (press a key + enter)"LF LF);
        HT_PRINT("* Ignore all further links" LF);
        HT_PRINT("0 Ignore this link (default if empty entry)"LF);
        HT_PRINT("1 Ignore directory and lower structures"LF);
        HT_PRINT("2 Ignore all domain"LF);
        //HT_PRINT("3 (Ignore location, not implemented)\n");
        HT_PRINT(LF);
        HT_PRINT("4 Get only this page/link"LF);
        HT_PRINT("5 Mirror this link (useful)"LF);
        HT_PRINT("6 Mirror links located in the same domain"LF);
        HT_PRINT(LF);
        HTS_REQUEST_END;
          {
            char BIGSTK tempo[HTS_URLMAXSIZE*2];
            tempo[0]='\0';
            strcatbuff(tempo,adr);
            strcatbuff(tempo,"/");
            strcatbuff(tempo,fil);
            s = RUN_CALLBACK1(opt, query3, tempo);
          }
          if (strnotempty(s)==0)  // entr�e
            n=0;
          else if (isdigit((unsigned char)*s))
            sscanf(s,"%d",&n);
          else {
            switch(*s) {
            case '*': n=-1; break;
            case '!': n=-999; {
              /*char *a;
              int i;                                    
              a=copie_de_adr-128;
              if (a<r.adr) a=r.adr;
              for(i=0;i<256;i++) {
                if (a==copie_de_adr) printf("\nHERE:\n");
                printf("%c",*a++);
              }
              printf("\n\n");
              */
                      }
              break;
            default: n=-999; printf("What did you say?\n"); break;
              
            } 
          }
        io_flush;
      } else {   // lien primaire: autoriser r�pertoire entier       
        if (!force_mirror) {
          if ((opt->seeker & 1)==0) {  // interdiction de descendre
            n=7;
          } else {
            n=5;   // autoriser miroir r�pertoires descendants (lien primaire)
          }
        } else   // forcer valeur (sub-wizard)
          n=force_mirror;
      }
      
      /* sanity check - reallocate filters HERE */
      if ((*_FILTERS_PTR) + 1 >= opt->maxfilter) {
        opt->maxfilter += HTS_FILTERSINC;
        if (filters_init(&_FILTERS, opt->maxfilter, HTS_FILTERSINC) == 0) {
          printf("PANIC! : Too many filters : >%d [%d]\n", (*_FILTERS_PTR),__LINE__);
          fflush(stdout);
          if (opt->log) {
            fprintf(opt->log,LF"Too many filters, giving up..(>%d)"LF, (*_FILTERS_PTR) );
            fprintf(opt->log,"To avoid that: use #F option for more filters (example: -#F5000)"LF);
            test_flush;
          }
          assertf("too many filters - giving up" == NULL);    // wild..
        }
      }

      // here we have enough room for a new filter if necessary
      switch(n) {
      case -1: // sauter tout le reste
        forbidden_url=1;
        opt->wizard=2;    // sauter tout le reste
        break;
      case 0:    // interdire les m�mes liens: adr/fil
        forbidden_url=1; 
        HT_INSERT_FILTERS0;    // ins�rer en 0
        strcpybuff(_FILTERS[0],"-");
        strcatbuff(_FILTERS[0],jump_identification(adr));
        if (*fil!='/') strcatbuff(_FILTERS[0],"/");
        strcatbuff(_FILTERS[0],fil);
        break;
        
      case 1: // �liminer r�pertoire entier et sous r�p: adr/path/ *
        forbidden_url=1;
        {
          size_t i = strlen(fil)-1;
          while((fil[i]!='/') && (i>0)) i--;
          if (fil[i]=='/') {
            HT_INSERT_FILTERS0;    // ins�rer en 0
            strcpybuff(_FILTERS[0],"-");
            strcatbuff(_FILTERS[0],jump_identification(adr));
            if (*fil!='/') strcatbuff(_FILTERS[0],"/");
            strncatbuff(_FILTERS[0] ,fil,i);
            if (_FILTERS[0][strlen(_FILTERS[0])-1]!='/') 
              strcatbuff(_FILTERS[0],"/");
            strcatbuff(_FILTERS[0],"*");
          }
        }            
        
        // ** ...
        break;
        
      case 2:    // adresse adr*
        forbidden_url=1;
        HT_INSERT_FILTERS0;    // ins�rer en 0                                
        strcpybuff(_FILTERS[0],"-");
        strcatbuff(_FILTERS[0],jump_identification(adr));
        strcatbuff(_FILTERS[0],"*");
        break;
        
      case 3: // ** A FAIRE
        forbidden_url=1;
        /*
        {
        int i=strlen(adr)-1;
        while((adr[i]!='/') && (i>0)) i--;
        if (i>0) {
        
          }
          
      }*/
        
        break;
        //
      case 4:    // same link
        // PAS BESOIN!!
        /*HT_INSERT_FILTERS0;    // ins�rer en 0                                
        strcpybuff(_FILTERS[0],"+");
        strcatbuff(_FILTERS[0],adr);
        if (*fil!='/') strcatbuff(_FILTERS[0],"/");
        strcatbuff(_FILTERS[0],fil);*/
        
        
        // �tant donn� le renversement wizard/primary filter (les primary autorisent up/down ET interdisent)
        // il faut �viter d'un lien isol� effectue un miroir total..
        
        *set_prio_to = 0+1;    // niveau de r�cursion=0 (pas de miroir)
        
        break;
        
      case 5:    // autoriser r�pertoire entier et fils
        if ((opt->seeker & 2)==0) {  // interdiction de monter
          size_t i = strlen(fil)-1;
          while((fil[i]!='/') && (i>0)) i--;
          if (fil[i]=='/') {
            HT_INSERT_FILTERS0;    // ins�rer en 0                                
            strcpybuff(_FILTERS[0],"+");
            strcatbuff(_FILTERS[0],jump_identification(adr));
            if (*fil!='/') strcatbuff(_FILTERS[0],"/");
            strncatbuff(_FILTERS[0],fil,i+1);
            strcatbuff(_FILTERS[0],"*");
          }
        } else {    // autoriser domaine alors!!
          HT_INSERT_FILTERS0;    // ins�rer en 0                                strcpybuff(filters[filptr],"+");
          strcpybuff(_FILTERS[0],"+");
          strcatbuff(_FILTERS[0],jump_identification(adr));
          strcatbuff(_FILTERS[0],"*");
        }
        break;
        
      case 6:    // same domain
        HT_INSERT_FILTERS0;    // ins�rer en 0                                strcpybuff(filters[filptr],"+");
        strcpybuff(_FILTERS[0],"+");
        strcatbuff(_FILTERS[0],jump_identification(adr));
        strcatbuff(_FILTERS[0],"*");
        break;
        //
      case 7:    // autoriser ce r�pertoire
        {
          size_t i = strlen(fil)-1;
          while((fil[i]!='/') && (i>0)) i--;
          if (fil[i]=='/') {
            HT_INSERT_FILTERS0;    // ins�rer en 0                                
            strcpybuff(_FILTERS[0],"+");
            strcatbuff(_FILTERS[0],jump_identification(adr));
            if (*fil!='/') strcatbuff(_FILTERS[0],"/");
            strncatbuff(_FILTERS[0],fil,i+1);
            strcatbuff(_FILTERS[0],"*[file]");
          }
        }
        
        break;
        
      case 50:    // on fait rien
        break;
      }  // switch 
                              
    }  // test du wizard sur l'url
  }  // fin du test wizard..

  // -------------------- PHASE 5 --------------------

  // lien non autoris�, peut-on juste le tester?
  if (just_test_it) {
    if (forbidden_url==1) {
      if (opt->travel&256) {    // tester tout de m�me
        if (strfield(adr,"ftp://")==0
#if HTS_USEMMS
					&& strfield(adr,"mms://")==0
#endif
					) {    // PAS ftp!
          forbidden_url=1;    // oui oui toujours interdit (note: sert � rien car ==1 mais c pour comprendre)
          *just_test_it=1;     // mais on teste
          if ((opt->debug>1) && (opt->log!=NULL)) {
            HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"Testing link %s%s"LF,adr,fil);
          }
        }
      }
    }
    //adr[0]='\0';  // cancel
  }

  // -------------------- FINAL PHASE --------------------
  // Test if the "Near" test won
  if (may_set_prio_to && forbidden_url == 0) {
    *set_prio_to = may_set_prio_to;
  }

  return forbidden_url;
#undef _FILTERS
#undef _FILTERS_PTR
#undef _ROBOTS
}

int hts_acceptmime(httrackp* opt,
                   int ptr,int lien_tot,lien_url** liens,
                   char* adr,char* fil,
                   char* mime) 
{
#define _FILTERS     (*opt->filters.filters)
#define _FILTERS_PTR (opt->filters.filptr)
#define _ROBOTS      ((robots_wizard*)opt->robotsptr)
  int forbidden_url = -1;
  char* mdepth="";
  int jokDepth = 0;
  int jok = 0;

  /* Authorized ? */
  jok  = fa_strjoker(/*mime*/1, _FILTERS, *_FILTERS_PTR, mime, NULL, NULL, &jokDepth);
  if (jok != 0) {
    mdepth = _FILTERS[jokDepth];
    if (jok == 1) {   // autoris�
      forbidden_url=0;  // URL autoris�e
      if ((opt->debug>1) && (opt->log!=NULL)) {
        HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) explicit authorized (%s) link %s%s: mime '%s'"LF,mdepth,adr,fil,mime);
        test_flush;
      }
    } else if (jok == -1) {  // forbidden
      forbidden_url=1;   // URL interdite
      if ((opt->debug>1) && (opt->log!=NULL)) {
        HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"(wizard) explicit forbidden (%s) link %s%s: mime '%s'"LF,mdepth,adr,fil,mime);
        test_flush;
      }
    }  // sinon on touche � rien
  }
  /* userdef test */
	{
    int test_url = RUN_CALLBACK4(opt, check_mime, adr, fil, mime, forbidden_url);
    if (test_url!=-1) {
      forbidden_url=test_url;
    }
	}
  return forbidden_url;
#undef _FILTERS
#undef _FILTERS_PTR
#undef _ROBOTS
}

// tester taille
int hts_testlinksize(httrackp* opt,
                     char* adr,char* fil,
                     LLint size) {
  int jok=0;
  if (size>=0) {
    char BIGSTK l[HTS_URLMAXSIZE*2];
    char BIGSTK lfull[HTS_URLMAXSIZE*2];
    if (size>=0) {
      LLint sz=size;
      int size_flag=0;
      
      // former URL compl�te du lien actuel
      strcpybuff(l,jump_identification(adr));
      if (*fil!='/') strcatbuff(l,"/");
      strcatbuff(l,fil);
      //
      if (!link_has_authority(adr))
        strcpybuff(lfull,"http://");
      else
        lfull[0]='\0';
      strcatbuff(lfull,adr);
      if (*fil!='/') strcatbuff(l,"/");
      strcatbuff(lfull,fil);
      
      // filters, 0=sait pas 1=ok -1=interdit
      {
        int jokDepth1=0,jokDepth2=0;
        int jok1=0,jok2=0;
        LLint sz1=size,sz2=size;
        int size_flag1=0,size_flag2=0;
        jok1  = fa_strjoker(/*url*/0, *opt->filters.filters,*opt->filters.filptr,lfull,&sz1,&size_flag1,&jokDepth1);
        jok2 =  fa_strjoker(/*url*/0, *opt->filters.filters,*opt->filters.filptr,l,    &sz2,&size_flag2,&jokDepth2);
        if (jok2 == 0) {      // #2 doesn't know
          jok = jok1;        // then, use #1
          sz = sz1;
          size_flag = size_flag1;
        } else if (jok1 == 0) {  // #1 doesn't know
          jok = jok2;        // then, use #2
          sz = sz2;
          size_flag = size_flag2;
        } else if (jokDepth1 >= jokDepth2) { // #1 matching rule is "after" #2, then it is prioritary
          jok = jok1;
          sz = sz1;
          size_flag = size_flag1;
        } else {                              // #2 matching rule is "after" #1, then it is prioritary
          jok = jok2;
          sz = sz2;
          size_flag = size_flag2;
        } 
      }
      

      // log
      if (jok==1) {
        if ((opt->debug>1) && (opt->log!=NULL)) {
          HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"File confirmed (size test): %s%s ("LLintP")"LF,adr,fil,(LLint)(size));
        }
      } else if (jok==-1) {
        if (size_flag) {        /* interdit � cause de la taille */
          if ((opt->debug>1) && (opt->log!=NULL)) {
            HTS_LOG(opt,LOG_DEBUG); fprintf(opt->log,"File cancelled due to its size: %s%s ("LLintP", limit: "LLintP")"LF,adr,fil,(LLint)(size),(LLint)(sz));
          }
        } else {
          jok=1;
        }
      }
    }
  }
  return jok;
}



#undef test_flush
#undef urladr
#undef urlfil

#undef HT_INSERT_FILTERS0


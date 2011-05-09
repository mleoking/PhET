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
/*       various tools (filename analyzing ..)                  */
/* Author: Xavier Roche                                         */
/* ------------------------------------------------------------ */

/* Internal engine bytecode */
#define HTS_INTERNAL_BYTECODE

/* String */
#include <ctype.h>
#include "htscore.h"
#include "htstools.h"
#include "htsstrings.h"
#ifdef _WIN32
#include "windows.h"
#else
#include <dirent.h>
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif
#include <sys/stat.h>
#endif

// Portable directory find functions
#ifndef HTS_DEF_FWSTRUCT_find_handle_struct
#define HTS_DEF_FWSTRUCT_find_handle_struct
typedef struct find_handle_struct find_handle_struct;
#endif
#ifdef _WIN32
struct find_handle_struct {
  WIN32_FIND_DATAA hdata;
  HANDLE handle;
};
#else
struct find_handle_struct {
  DIR * hdir;
  struct dirent* dirp;
  struct stat filestat;
  char path[2048];
};
#endif
#ifndef HTS_DEF_FWSTRUCT_topindex_chain
#define HTS_DEF_FWSTRUCT_topindex_chain
typedef struct topindex_chain topindex_chain;
#endif
struct topindex_chain {
  int level;                          /* sort level */
  char* category;                     /* category */
  char name[2048];                    /* path */
  struct topindex_chain* next;        /* next element */
};


/* Tools */

static int ehexh(char c) {
  if ((c>='0') && (c<='9')) return c-'0';
  if ((c>='a') && (c<='f')) c-=('a'-'A');
  if ((c>='A') && (c<='F')) return (c-'A'+10);
  return 0;
}

static int ehex(char* s) {
  return 16*ehexh(*s)+ehexh(*(s+1));
}

static void unescapehttp(char* s, String* tempo) {
  int i;
  for (i=0;i<(int) strlen(s);i++) {
    if (s[i]=='%' && s[i+1]=='%') {
      i++;
      StringAddchar(*tempo, '%');
    } else if (s[i]=='%') {
      char hc;
      i++;
      hc = (char) ehex(s+i);
      StringAddchar(*tempo, (char) hc);
      i++;    // sauter 2 caract�res finalement
    }
    else if (s[i]=='+') {
      StringAddchar(*tempo, ' ');
    }
    else
      StringAddchar(*tempo, s[i]);
  }
}

// forme � partir d'un lien et du contexte (origin_fil et origin_adr d'o� il est tir�) adr et fil
// [adr et fil sont des buffers de 1ko]
// 0 : ok
// -1 : erreur
// -2 : protocole non support� (ftp)
int ident_url_relatif(const char *lien,const char* origin_adr,const char* origin_fil,char* adr,char* fil) {
  int ok=0;
  int scheme=0;

  adr[0]='\0'; fil[0]='\0';    //effacer buffers

  // lien non vide!
  if (strnotempty(lien)==0) return -1;    // erreur!

  // Scheme?
  {
    const char* a=lien;
    while (isalpha((unsigned char)*a))
      a++;
    if (*a == ':')
      scheme=1;
  }

  // filtrer les parazites (mailto & cie)
  // scheme+authority (//)
  if (
               (strfield(lien,"http://"))        // scheme+//
            || (strfield(lien,"file://"))   // scheme+//
            || (strncmp(lien,"//",2)==0)    // // sans scheme (-> default)
       ) {
    if (ident_url_absolute(lien,adr,fil)==-1) {        
      ok=-1;    // erreur URL
    }
  }
  else if (strfield(lien,"ftp://")) {
    // Note: ftp:foobar.gif is not valid
    if (ftp_available()) {     // ftp support�
      if (ident_url_absolute(lien,adr,fil)==-1) {        
        ok=-1;    // erreur URL
      }
    } else {
      ok=-2;  // non support�
    }
#if HTS_USEMMS
	} else if (strfield(lien,"mms://")) {
		if (ident_url_absolute(lien,adr,fil)==-1) {        
			ok=-1;    // erreur URL
		}
#endif
#if HTS_USEOPENSSL
  } else if (strfield(lien,"https://")) {
    if (SSL_is_available) {
      // Note: ftp:foobar.gif is not valid
      if (ident_url_absolute(lien,adr,fil)==-1) {        
        ok=-1;    // erreur URL
      }
    } else {
      ok=-1;
    }
#endif
  } else if ((scheme) && (
    (!strfield(lien,"http:"))
    && (!strfield(lien,"https:"))
    && (!strfield(lien,"ftp:"))
#if HTS_USEMMS
    && (!strfield(lien,"mms:"))
#endif
    )) {
    ok=-1;      // unknown scheme
  } else {    // c'est un lien relatif
    // On forme l'URL compl�te � partie de l'url actuelle
    // et du chemin actuel si besoin est.
    
    // copier adresse
    if (((int) strlen(origin_adr)<HTS_URLMAXSIZE) && ((int) strlen(origin_fil)<HTS_URLMAXSIZE) && ((int) strlen(lien)<HTS_URLMAXSIZE)) {

      /* patch scheme if necessary */
      if (strfield(lien,"http:")) {
        lien+=5;
        strcpybuff(adr, jump_protocol(origin_adr));    // m�me adresse ; protocole vide (http)
      } else if (strfield(lien,"https:")) {
        lien+=6;
        strcpybuff(adr, "https://");   // m�me adresse forc�e en https
        strcatbuff(adr, jump_protocol(origin_adr));
      } else if (strfield(lien,"ftp:")) {
        lien+=4;
        strcpybuff(adr, "ftp://");   // m�me adresse forc�e en ftp
        strcatbuff(adr, jump_protocol(origin_adr));
#if HTS_USEMMS
      } else if (strfield(lien,"mms:")) {
        lien+=4;
        strcpybuff(adr, "mms://");   // m�me adresse forc�e en ftp
        strcatbuff(adr, jump_protocol(origin_adr));
#endif
      } else {
        strcpybuff(adr,origin_adr);    // m�me adresse ; et m�me �ventuel protocole
      }
      
      if (*lien!='/') {  // sinon c'est un lien absolu
        if (*lien == '\0') {
          strcpybuff(fil,origin_fil);
        } else if (*lien == '?') {     // example: a href="?page=2"
          char* a;
          strcpybuff(fil,origin_fil);
          a=strchr(fil,'?');
          if (a) *a='\0';
          strcatbuff(fil,lien);
        } else {
          const char *a=strchr(origin_fil,'?');
          if (a == NULL) a=origin_fil+strlen(origin_fil);
          while((*a!='/') && ( a > origin_fil) ) a--;
          if (*a=='/') {    // ok on a un '/'
            if ( (((int) (a - origin_fil))+1+strlen(lien)) < HTS_URLMAXSIZE) {
              // copier chemin
              strncpy(fil,origin_fil,((int) (a - origin_fil))+1);
              *(fil + ((int) (a - origin_fil))+1)='\0';
              
              // copier chemin relatif
              if (((int) strlen(fil)+(int) strlen(lien)) < HTS_URLMAXSIZE) {
                strcatbuff(fil,lien + ((*lien=='/')?1:0) );      
                // simplifier url pour les ../
                fil_simplifie(fil);
              } else
                ok=-1;    // erreur
            } else {    // erreur
              ok=-1;    // erreur URL
            }
          } else {    // erreur
            ok=-1;    // erreur URL
          }
        }
      } else { // chemin absolu
        // copier chemin directement
        strcatbuff(fil,lien);      
        fil_simplifie(fil);
      }  // *lien!='/'
    } else
      ok=-1;
    
  }  // test news: etc.

  // case insensitive pour adresse
  {
    char *a=jump_identification(adr);
    while(*a) {
      if ((*a>='A') && (*a<='Z'))
        *a+='a'-'A';       
      a++;
    }
  }
  
  return ok;
}





// cr�er dans s, � partir du chemin courant curr_fil, le lien vers link (absolu)
// un ident_url_relatif a d�ja �t� fait avant, pour que link ne soit pas un chemin relatif
int lienrelatif(char* s,const char* link,const char* curr_fil) {
  char BIGSTK _curr[HTS_URLMAXSIZE*2];
  char BIGSTK newcurr_fil[HTS_URLMAXSIZE*2],newlink[HTS_URLMAXSIZE*2];
  char* curr;
  //int n=0;
  char* a;
  int slash=0;
  //
  newcurr_fil[0]='\0'; newlink[0]='\0';
  //

  // patch: �liminer les ? (param�tres) sinon bug
  {
    const char* a;
    if ( (a=strchr(curr_fil,'?')) ) {
      strncatbuff(newcurr_fil,curr_fil,(int) (a - curr_fil));
      curr_fil = newcurr_fil;
    }
    if ( (a=strchr(link,'?')) ) {
      strncatbuff(newlink,link,(int) (a - link));
      link = newlink;
    }
  }

  // recopier uniquement le chemin courant
  curr=_curr;
  strcpybuff(curr,curr_fil);
  if ((a=strchr(curr,'?'))==NULL)  // couper au ? (params)
    a=curr+strlen(curr)-1;         // pas de params: aller � la fin
  while((*a!='/') && ( a> curr)) a--;       // chercher dernier / du chemin courant
  if (*a=='/') *(a+1)='\0';                           // couper dernier /
  
  // "effacer" s
  s[0]='\0';
  
  // sauter ce qui est commun aux 2 chemins
  {
    const char *l,*c;
    if (*link=='/') link++;  // sauter slash
    if (*curr=='/') curr++;
    l=link;
    c=curr;
    // couper ce qui est commun
    while ((streql(*link,*curr)) && (*link!=0)) {link++; curr++; }
    // mais on veut un r�pertoirer entier!
    // si on a /toto/.. et /toto2/.. on ne veut pas sauter /toto !
    while(((*link!='/') || (*curr!='/')) && ( link > l)) { link--; curr--; }
    //if (*link=='/') link++;
    //if (*curr=='/') curr++;
  }
  
  // calculer la profondeur du r�pertoire courant et remonter
  // LES ../ ONT ETE SIMPLIFIES
  a=curr;
  if (*a=='/') a++;
  while(*a) if (*(a++)=='/') strcatbuff(s,"../");
  //if (strlen(s)==0) strcatbuff(s,"/");

  if (slash) strcatbuff(s,"/");    // garder absolu!!
  
  // on est dans le r�pertoire de d�part, copier
  strcatbuff(s,link + ((*link=='/')?1:0) );

  /* Security check */
  if (strlen(s) >= HTS_URLMAXSIZE)
    return -1;

  // on a maintenant une chaine de la forme ../../test/truc.html  
  return 0;
}

/* Is the link absolute (http://www..) or relative (/bar/foo.html) ? */
int link_has_authority(const char* lien) {
  const char* a=lien;
  if (isalpha((unsigned char)*a)) {
    // Skip scheme?
    while (isalpha((unsigned char)*a))
      a++;
    if (*a == ':')
      a++;
    else
      return 0;
  }
  if (strncmp(a,"//",2) == 0)
    return 1;
  return 0;
}

int link_has_authorization(const char* lien) {
  const char* adr = jump_protocol(lien);
  const char* firstslash = strchr(adr, '/');
  const char* detect = strchr(adr, '@');
  if (firstslash) {
    if (detect) {
      return (detect < firstslash);
    }
  } else {
    return (detect != NULL);
  }
  return 0;
}


// conversion chemin de fichier/dossier vers 8-3 ou ISO9660
void long_to_83(int mode,char* n83,char* save) {
  n83[0]='\0';

  while(*save) {
    char fn83[256],fnl[256];
    int i=0;
    fn83[0]=fnl[0]='\0';
    while((save[i]) && (save[i]!='/')) { fnl[i]=save[i]; i++; }
    fnl[i]='\0';
    // conversion
    longfile_to_83(mode,fn83,fnl);
    strcatbuff(n83,fn83);

    save+=i;
    if (*save=='/') { strcatbuff(n83,"/"); save++; }
  }
}


// conversion nom de fichier/dossier isol� vers 8-3 ou ISO9660
void longfile_to_83(int mode,char* n83,char* save) {
  int j=0,max=0;
  int i = 0;
  char nom[256];
  char ext[256];
  nom[0]=ext[0]='\0';
  
  switch(mode) {
  case 1:
    max=8;
    break;
  case 2:
    max=31;
    break;
  default:
    max=8;
    break;
  }

  /* No starting . */
  if (save[0] == '.') {
    save[0]='_';
  }
  /* No multiple dots */
  {
    char* last_dot=strrchr(save, '.');
    char* dot;
    while((dot=strchr(save, '.'))) {
      *dot = '_';
    }
    if (last_dot) {
      *last_dot='.';
    }
  }
  /* 
  Avoid: (ISO9660, but also suitable for 8-3)
  (Thanks to jonat@cellcast.com for te hint)
  /:;?\#*~
  0x00-0x1f and 0x80-0xff
  */
  for(i = 0 ; save[i] != 0 ; i++) {
    char a = save[i];
    if (a >= 'a' && a <= 'z') {
      a -= 'a' - 'A';
    }
    else if ( ! ( (a >= 'A' && a <= 'Z') || (a >= '0' && a <= '9') || a == '_' || a == '.') ) {
      a = '_';
    }
    save[i] = a;
  }

  i=j=0;
  while((i<max) && (save[j]) && (save[j]!='.')) {
    if (save[j]!=' ') {
      nom[i]=save[j]; 
      i++; 
    } 
    j++; 
  }  // recopier nom
  nom[i]='\0';
  if (save[j]) {  // il reste au moins un point
    i = (int) strlen(save)-1;
    while((i>0) && (save[i]!='.') && (save[i]!='/')) i--;    // rechercher dernier .
    if (save[i]=='.') {  // point!
      int j=0;
      i++;
      while((j<3) && (save[i]) ) { if (save[i]!=' ') { ext[j]=save[i]; j++; } i++; }
      ext[j]='\0';
    }
  }
  // corriger vers 8-3
  n83[0]='\0';
  strncatbuff(n83,nom,max);
  if (strnotempty(ext)) {
    strcatbuff(n83,".");
    strncatbuff(n83,ext,3);    
  }
}

// �crire backblue.gif
int verif_backblue(httrackp* opt, const char* base) {
  int* done = &opt->state.verif_backblue_done;
  int ret=0;
  //
  if (!base) {   // init
    *done=0;
    return 0;
  }
  if ( (!*done)
    || (fsize(fconcat(OPT_GET_BUFF(opt), base,"backblue.gif")) != HTS_DATA_BACK_GIF_LEN)) {
    FILE* fp = filecreate(&opt->state.strc, fconcat(OPT_GET_BUFF(opt), base,"backblue.gif"));
    *done=1;
    if (fp) {
      if (fwrite(HTS_DATA_BACK_GIF,HTS_DATA_BACK_GIF_LEN,1,fp) != HTS_DATA_BACK_GIF_LEN)
        ret=1;
      fclose(fp);
      usercommand(opt,0,NULL,fconcat(OPT_GET_BUFF(opt), base,"backblue.gif"),"","");
    } else
      ret=1;
    //
    fp = filecreate(&opt->state.strc, fconcat(OPT_GET_BUFF(opt), base,"fade.gif"));
    if (fp) {
      if (fwrite(HTS_DATA_FADE_GIF,HTS_DATA_FADE_GIF_LEN,1,fp) != HTS_DATA_FADE_GIF_LEN)
        ret=1;
      fclose(fp);
      usercommand(opt,0,NULL,fconcat(OPT_GET_BUFF(opt), base,"fade.gif"),"","");
    } else
      ret=1;
  } 
  return ret;
}

// flag
int verif_external(httrackp* opt,int nb,int test) {
  int* status = &opt->state.verif_external_status;
  if (!test)
    status[nb]=0;   // reset
  else if (!status[nb]) {
    status[nb]=1;
    return 1;
  }
  return 0;
}


// recherche cha�ne de type truc<espaces>=
// renvoi d�calage � effectuer ou 0 si non trouv�
/* SECTION OPTIMISEE:
#define rech_tageq(adr,s) ( \
  ( (*(adr-1)=='<') || (is_space(*(adr-1))) ) ? \
    ( (streql(*adr,*s)) ? \
      (__rech_tageq(adr,s)) \
      : 0 \
    ) \
    : 0\
  )
*/
/*
HTS_INLINE int rech_tageq(const char* adr,const char* s) { 
  if ( (*(adr-1)=='<') || (is_space(*(adr-1))) ) {   // <tag < tag etc
    if (streql(*adr,*s)) {                           // tester premier octet (optimisation)
      return __rech_tageq(adr,s);
    }
  }
  return 0;
}
*/
// Deuxi�me partie
HTS_INLINE int __rech_tageq(const char* adr,const char* s) { 
  int p;
  p=strfield(adr,s);
  if (p) {
    while(is_space(adr[p])) p++;
    if (adr[p]=='=') {
      return p+1;
    }
  }
  return 0;
}

HTS_INLINE int rech_tageq_all(const char* adr, const char* s) { 
	int p;
	char quot = 0;
	const char *token = NULL;
	int s_len = (int) strlen(s);
	if (adr == NULL) {
		return 0;
	}
	for(p = 0 ; adr[p] != 0 ; p++) {
		if (quot == 0) {
			if (adr[p] == '"' || adr[p] == '\'' ) {
				quot = adr[p];
			} else if (adr[p] == '=' || is_realspace(adr[p]) ) {
				token = NULL;
			} else if (adr[p] == '>') {
				break;
			} else {				/* note: bogus for bogus foo = bar */
				if (token == NULL) {
					if (strncasecmp(&adr[p], s, s_len) == 0 
						&& (is_realspace(adr[p + s_len]) || adr[p + s_len] == '=')
						) {
						for( p += s_len ; is_realspace(adr[p]) || adr[p] == '=' ; p++ );
						return p;
					}
					token = &adr[p];
				}
			}
		} else if (adr[p] == quot) {
			quot = 0;
		}
	}
  return 0;
}

HTS_INLINE int rech_endtoken(const char* adr, const char** start) {
  char quote = '\0';
  int length = 0;
  while(is_space(*adr)) adr++;
  if (*adr == '"' || *adr == '\'') 
    quote = *adr++;
  *start = adr;
  while(*adr != 0 && *adr != quote && (quote != '\0' || !is_space(*adr)) ) {
    length++;
	adr++;
  }
  return length;
}
// same, but check begining of adr wirh s (for <object src="bar.mov" .. hotspot123="foo.html">)
HTS_INLINE int __rech_tageqbegdigits(const char* adr,const char* s) { 
  int p;
  p=strfield(adr,s);
  if (p) {
    while(isdigit((unsigned char)adr[p]))  p++;      // jump digits
    while(is_space(adr[p])) p++;
    if (adr[p]=='=') {
      return p+1;
    }
  }
  return 0;
}

// tag sans =
HTS_INLINE int rech_sampletag(const char* adr,const char* s) { 
  int p;
  if ( (*(adr-1)=='<') || (is_space(*(adr-1))) ) {   // <tag < tag etc
    p=strfield(adr,s);
    if (p) {
      if (!isalnum((unsigned char)adr[p])) {  // <srcbis n'est pas <src
        return 1;
      }
      return 0;
    }
  }
  return 0;
}

// teste si le tag contenu dans from est �gal � "tag"
HTS_INLINE int check_tag(char* from,const char* tag) {
  char* a=from+1;
  int i=0;
  char s[256];
  while(is_space(*a)) a++;
  while((isalnum((unsigned char)*a) || (*a=='/')) && (i<250)) { s[i++]=*a; a++; }
  s[i++]='\0';
  return (strfield2(s,tag));  // comparer
}

// teste si un fichier d�passe le quota
int istoobig(httrackp *opt,LLint size,LLint maxhtml,LLint maxnhtml,char* type) {
  int ok=1;
  if (size>0) {
    if (is_hypertext_mime(opt,type, "")) {
      if (maxhtml>0) {
        if (size>maxhtml)
          ok=0;
      }
    } else {
      if (maxnhtml>0) {
        if (size>maxnhtml)
          ok=0;
      }
    }
  }
  return (!ok);
}


static int sortTopIndexFnc(const void * a_, const void * b_) {
  int cmp;
  topindex_chain** a = (topindex_chain**) a_;
  topindex_chain** b = (topindex_chain**) b_;
  /* Category first, then name */
  if ((cmp = (*a)->level - (*b)->level) == 0) {
    if ((cmp = strcmpnocase((*a)->category, (*b)->category)) == 0) {
      cmp = strcmpnocase((*a)->name, (*b)->name);
    }
  }
  return cmp;
}

HTSEXT_API char* hts_getcategory(const char* filename);

HTSEXT_API int hts_buildtopindex(httrackp* opt,const char* path,const char* binpath) {
  FILE* fpo;
  int retval=0;
  char BIGSTK rpath[1024*2];
  char *toptemplate_header=NULL,*toptemplate_body=NULL,*toptemplate_footer=NULL,*toptemplate_bodycat=NULL;
 	char catbuff[CATBUFF_SIZE];

  // et templates html
  toptemplate_header=readfile_or(fconcat(catbuff, binpath,"templates/topindex-header.html"),HTS_INDEX_HEADER);
  toptemplate_body=readfile_or(fconcat(catbuff, binpath,"templates/topindex-body.html"),HTS_INDEX_BODY);
  toptemplate_bodycat=readfile_or(fconcat(catbuff, binpath,"templates/topindex-bodycat.html"),HTS_INDEX_BODYCAT);
  toptemplate_footer=readfile_or(fconcat(catbuff, binpath,"templates/topindex-footer.html"),HTS_INDEX_FOOTER);
  
  if (toptemplate_header && toptemplate_body && toptemplate_footer && toptemplate_bodycat) {
    
    strcpybuff(rpath,path);
    if (rpath[0]) {
      if (rpath[strlen(rpath)-1]=='/')
        rpath[strlen(rpath)-1]='\0';
    }
    
    fpo=fopen(fconcat(catbuff, rpath,"/index.html"),"wb");
    if (fpo) {
      find_handle h;
      verif_backblue(opt,concat(catbuff, rpath,"/"));    // g�n�rer gif
      // Header
      fprintf(fpo,toptemplate_header,
        "<!-- Mirror and index made by HTTrack Website Copier/"HTTRACK_VERSION" "HTTRACK_AFF_AUTHORS" -->"
        );
      
      /* Find valid project names */
      h = hts_findfirst(rpath);
      if (h) {
        struct topindex_chain * chain=NULL;
        struct topindex_chain * startchain=NULL;
        String iname = STRING_EMPTY;
        int chainSize = 0;
        do {
          if (hts_findisdir(h)) {
            StringCopy(iname,rpath);
            StringCat(iname,"/");
            StringCat(iname,hts_findgetname(h));
            StringCat(iname,"/index.html");
            if (fexist(StringBuff(iname))) {
              int level = 0;
              char* category = NULL;
              struct topindex_chain * oldchain=chain;
              
              /* Check for an existing category */
              StringCopy(iname,rpath);
              StringCat(iname,"/");
              StringCat(iname,hts_findgetname(h));
              StringCat(iname,"/hts-cache/winprofile.ini");
              if (fexist(StringBuff(iname))) {
                category = hts_getcategory(StringBuff(iname));
                if (category != NULL) {
                  if (*category == '\0') {
                    freet(category);
                    category = NULL;
                  }
                }
              }
              if (category == NULL) {
                category = strdupt("No categories");
                level = 1;
              }

              chain=calloc(sizeof(struct topindex_chain), 1);
              chainSize++;
              if (!startchain) {
                startchain=chain;
              }
              if (chain) {
                if (oldchain) {
                  oldchain->next=chain;
                }
                chain->next=NULL;
                strcpybuff(chain->name, hts_findgetname(h));
                chain->category = category;
                chain->level = level;
              }
            }
            
          }
        } while(hts_findnext(h));
        hts_findclose(h);
        StringFree(iname);
        
        /* Sort chain */
        {
          struct topindex_chain** sortedElts = (struct topindex_chain**) calloct(sizeof(topindex_chain*), chainSize);
          assertf(sortedElts != NULL);
          if (sortedElts != NULL) {
            int i;
            char* category = "";
            
            /* Build array */
            struct topindex_chain * chain = startchain;
            for(i = 0 ; i < chainSize ; i++) {
              assertf(chain != NULL);
              sortedElts[i] = chain;
              chain = chain->next;
            }
            qsort(sortedElts, chainSize, sizeof(topindex_chain*), sortTopIndexFnc);
            
            /* Build sorted index */
            for(i = 0 ; i < chainSize ; i++) {
              char BIGSTK hname[HTS_URLMAXSIZE*2];
              strcpybuff(hname,sortedElts[i]->name);
              escape_check_url(hname);

              /* Changed category */
              if (strcmp(category, sortedElts[i]->category) != 0) {
                category = sortedElts[i]->category;
                fprintf(fpo,toptemplate_bodycat, category);
              }
              fprintf(fpo,toptemplate_body,
                hname,
                sortedElts[i]->name
                );
            }
            
            /* Wipe elements */
            for(i = 0 ; i < chainSize ; i++) {
              freet(sortedElts[i]->category);
              freet(sortedElts[i]);
              sortedElts[i] = NULL;
            }
            freet(sortedElts);
            
            /* Return value */
            retval=1;
          }
        }
        
      }
      
      // Footer
      fprintf(fpo,toptemplate_footer,
        "<!-- Mirror and index made by HTTrack Website Copier/"HTTRACK_VERSION" "HTTRACK_AFF_AUTHORS" -->"
        );
      
      fclose(fpo);
      
    }
    
  }

  if (toptemplate_header)
    freet(toptemplate_header);
  if (toptemplate_body)
    freet(toptemplate_body);
  if (toptemplate_footer)
    freet(toptemplate_footer);
  if (toptemplate_body)
    freet(toptemplate_body);
  
  return retval;
}

HTSEXT_API char* hts_getcategory(const char* filename) {
  String categ = STRING_EMPTY;
  if (fexist(filename)) {
    FILE* fp = fopen(filename, "rb");
    if (fp != NULL) {
      int done=0;
      while(!feof(fp) && !done) {
        char BIGSTK line[1024];
        int n = linput(fp, line, sizeof(line) - 2);
        if (n > 0) {
          if (strfield(line, "category=")) {
            unescapehttp(line+9, &categ);
            done=1;
          }
        }
      }
      fclose(fp);
    }
  }
  return StringBuffRW(categ);
}

HTSEXT_API char* hts_getcategories(char* path, int type) {
  String categ = STRING_EMPTY;
  String profiles = STRING_EMPTY;
  char* rpath = path;
  find_handle h;
  inthash hashCateg = NULL;
  if (rpath[0]) {
    if (rpath[strlen(rpath)-1]=='/') {
      rpath[strlen(rpath)-1]='\0';      /* note: patching stored (inhash) value */
    }
  }
  h = hts_findfirst(rpath);
  if (h) {
    String iname = STRING_EMPTY;
    if (type == 1) {
      hashCateg = inthash_new(127);
      StringCat(categ, "Test category 1");
      StringCat(categ, "\r\nTest category 2");
    }
    do {
      if (hts_findisdir(h)) {
        char BIGSTK line2[1024];
        StringCopy(iname,rpath);
        StringCat(iname,"/");
        StringCat(iname,hts_findgetname(h));
        StringCat(iname,"/hts-cache/winprofile.ini");
        if (fexist(StringBuff(iname))) {
          if (type == 1) {
            FILE* fp = fopen(StringBuff(iname), "rb");
            if (fp != NULL) {
              int done=0;
              while(!feof(fp) && !done) {
                int n = linput(fp, line2, sizeof(line2) - 2);
                if (n > 0) {
                  if (strfield(line2, "category=")) {
                    if (*(line2+9)) {
                      if (!inthash_read(hashCateg, line2+9, NULL)) {
                        inthash_write(hashCateg, line2+9, 0);
                        if (StringLength(categ) > 0) {
                          StringCat(categ, "\r\n");
                        }
                        unescapehttp(line2+9, &categ);
                      }
                    }
                    done=1;
                  }
                }
              }
              line2[0] = '\0';
              fclose(fp);
            }
          } else {
            if (StringLength(profiles) > 0) {
              StringCat(profiles, "\r\n");
            }
            StringCat(profiles, hts_findgetname(h));
          }
        }
        
      }
    } while(hts_findnext(h));
    hts_findclose(h);
    StringFree(iname);
  }
  if (hashCateg) {
    inthash_delete(&hashCateg);
    hashCateg = NULL;
  }
  if (type == 1)
    return StringBuffRW(categ);
  else
    return StringBuffRW(profiles);
}




// Portable directory find functions
/*
// Example:
find_handle h = hts_findfirst("/tmp");
if (h) {
  do {
    if (hts_findisfile(h))
      printf("File: %s (%d octets)\n",hts_findgetname(h),hts_findgetsize(h));
    else if (hts_findisdir(h))
      printf("Dir: %s\n",hts_findgetname(h));
  } while(hts_findnext(h));
  hts_findclose(h);
}
*/
HTSEXT_API find_handle hts_findfirst(char* path) {
  if (path) {
    if (strnotempty(path)) {
      find_handle_struct* find = (find_handle_struct*) calloc(1,sizeof(find_handle_struct));
      if (find) {
        memset(find, 0, sizeof(find_handle_struct));
#ifdef _WIN32
        {
          char BIGSTK rpath[1024*2];
          strcpybuff(rpath,path);
          if (rpath[0]) {
            if (rpath[strlen(rpath)-1]!='\\')
              strcatbuff(rpath,"\\");
          }
          strcatbuff(rpath,"*.*");
          find->handle = FindFirstFileA(rpath,&find->hdata);
          if (find->handle != INVALID_HANDLE_VALUE)
            return find;
        }
#else
        strcpybuff(find->path,path);
        {
          if (find->path[0]) {
            if (find->path[strlen(find->path)-1]!='/')
              strcatbuff(find->path,"/");
          }
        }
        find->hdir=opendir(path);
        if (find->hdir != NULL) {
          if (hts_findnext(find) == 1)
            return find;
        }
#endif
        free((void*)find);
      }
    }
  }
  return NULL;   
}

HTSEXT_API int hts_findnext(find_handle find) {
  if (find) {
#ifdef _WIN32
    if ( (FindNextFileA(find->handle,&find->hdata)))
      return 1;
#else
		char catbuff[CATBUFF_SIZE];
    memset(&(find->filestat), 0, sizeof(find->filestat));
    if ((find->dirp=readdir(find->hdir)))
      if (find->dirp->d_name)
        if (!stat(concat(catbuff, find->path,find->dirp->d_name),&find->filestat))
          return 1;
#endif
  }
  return 0;
}

HTSEXT_API int hts_findclose(find_handle find) {
  if (find) {
#ifdef _WIN32
    if (find->handle) {
      FindClose(find->handle);
      find->handle=NULL;
    }
#else
    if (find->hdir) {
      closedir (find->hdir);
      find->hdir=NULL;
    }
#endif
    free((void*)find);
  }
  return 0;
}

HTSEXT_API char* hts_findgetname(find_handle find) {
  if (find) {
#ifdef _WIN32
    return find->hdata.cFileName;
#else
    if (find->dirp)
      return find->dirp->d_name;
#endif
  }
  return NULL;
}

HTSEXT_API int hts_findgetsize(find_handle find) {
  if (find) {
#ifdef _WIN32
    return find->hdata.nFileSizeLow;
#else
    return find->filestat.st_size;
#endif
  }
  return -1;
}

HTSEXT_API int hts_findisdir(find_handle find) {
  if (find) {
    if (!hts_findissystem(find)) {
#ifdef _WIN32
      if (find->hdata.dwFileAttributes  & FILE_ATTRIBUTE_DIRECTORY)
        return 1;
#else
      if (S_ISDIR(find->filestat.st_mode))
        return 1;
#endif
    }
  }
  return 0;
}
HTSEXT_API int hts_findisfile(find_handle find) {
  if (find) {
    if (!hts_findissystem(find)) {
#ifdef _WIN32
      if (!(find->hdata.dwFileAttributes  & FILE_ATTRIBUTE_DIRECTORY))
        return 1;
#else
      if (S_ISREG(find->filestat.st_mode))
        return 1;
#endif
    }
  }
  return 0;
}
HTSEXT_API int hts_findissystem(find_handle find) {
  if (find) {
#ifdef _WIN32
    if (find->hdata.dwFileAttributes  & (FILE_ATTRIBUTE_SYSTEM|FILE_ATTRIBUTE_HIDDEN|FILE_ATTRIBUTE_TEMPORARY))
      return 1;
    else if ( (!strcmp(find->hdata.cFileName,"..")) || (!strcmp(find->hdata.cFileName,".")) )
      return 1;
#else
    if (
      (S_ISCHR(find->filestat.st_mode))
      || 
      (S_ISBLK(find->filestat.st_mode))
      || 
      (S_ISFIFO(find->filestat.st_mode))
      || 
      (S_ISSOCK(find->filestat.st_mode))
      )
      return 1;
    else if ( (!strcmp(find->dirp->d_name,"..")) || (!strcmp(find->dirp->d_name,".")) )
      return 1;
#endif
  }
  return 0;
}

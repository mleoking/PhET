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
/* File: Global engine definition file                          */
/* Author: Xavier Roche                                         */
/* ------------------------------------------------------------ */

// Ensemble des param�tres du robot

#ifndef HTTRACK_GLOBAL_ENGINE_DEFH
#define HTTRACK_GLOBAL_ENGINE_DEFH

// ------------------------------------------------------------
// D�finitions du ROBOT

// acc�s des miroirs pour les autres utilisateurs (0/1)
#define HTS_ACCESS 1

// temps de poll d'une socket: 1/10s
#define HTS_SOCK_SEC 0
#define HTS_SOCK_MS 100000

// nom par d�faut
#define DEFAULT_HTML "index.html"

// nom par d�faut pour / en ftp
#define DEFAULT_FTP "index.txt"

// nom par d�faut pour / en mms
#define DEFAULT_MMS "default.avi"

// extension par d�faut pour fichiers n'en ayant pas
#define DEFAULT_EXT       ".html"
#define DEFAULT_EXT_SHORT ".htm"
//#define DEFAULT_EXT       ".txt"
//#define DEFAULT_EXT_SHORT ".txt"

// �viter les /nul, /con..
#define HTS_OVERRIDE_DOS_FOLDERS 1

// indexing (keyword)
#define HTS_MAKE_KEYWORD_INDEX 1

// poll stdin autoris�? (0/1)
#define HTS_POLL 1

// v�rifier les liens sans extension (0/1) [� �viter, tr�s lent]
#define HTS_CHECK_STRANGEDIR 0

// le slash est un html par d�faut (exemple/ est toujours un html)
#define HTS_SLASH_ISHTML 1

// supprimer index si un r�pertoire identique existe
#define HTS_REMOVE_ANNOYING_INDEX 1

// �criture directe dur disque possible (0/1)
#define HTS_DIRECTDISK 1

// always direct-to-disk (0/1)
#define HTS_DIRECTDISK_ALWAYS 1

// g�rer une table de hachage?
// REMOVED
// #define HTS_HASH 1

// fast cache (build hash table)
#define HTS_FAST_CACHE 1

// le > peut �tre consid�r� comme un tag de fermeture de commentaire (<!-- > est valide)
#define GT_ENDS_COMMENT 1

// always adds a '/' at the end if a '~' is encountered (/~smith -> /~smith/)
#define HTS_TILDE_SLASH 0

// always transform a '//' into a sigle '/'
#define HTS_STRIP_DOUBLE_SLASH 0

// case-sensitive pour les dossiers et fichiers (0/1)
// [normalement 1, mais pose des probl�mes (url malform�e par exemple) et n'est pas tr�s utile..
// ..et pas bcp respect�]
// REMOVED
// #define HTS_CASSE 0

// Un fichier ayant une taille diff�rente du content-length doit il �tre annul�?
// SEE opt.tolerant and opt.http10
// #define HTS_CL_IS_FATAL 0

// une erreur supprime le fichier sur disque
// (non fix� pour cause de retry)
#define HTS_REMOVE_BAD_FILES 0

// en cas de Range: xx- donnant un Content-length: xx
// alors skipper le fichier, consid�r� comme transmis
// #define HTS_SKIP_FULL_RANGE 1

// nombre max de filtres que l'utilisateur peut fixer
// #define HTS_FILTERSMAX 10000
#define HTS_FILTERSINC 1000

// connect non bloquant? (poll sur write)
#define HTS_XCONN 1

// gethostbyname non bloquant? (gestion multithread)
#define HTS_XGETHOST 1

// � partir de combien de secondes doit-on �tudier le taux de transfert?
#define HTS_WATCHRATE 15

// ------------------------------------------------------------
//

#endif

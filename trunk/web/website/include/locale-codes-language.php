<?php

// Map of language codes to English readable language names
// This table should (generally) match ISO 369-1 at:
//    http://www.loc.gov/standards/iso639-2/php/code_list.php
// And more importantly should also match the translation
// utility:
//   trunk/util/translation-utility/data/translation-utility/language-codes.properties
//
// This map is sorted by English language name
//
// Java automaticaly maps modern language codes to older
// codes (presumably for backwards compatibilty).  We convert
// back, so the website will be consistent with Java mappings
// and NOT the most recent ISO codes.  But we still keep the
// modern ones around, just in case.
//
// These changes have taken place:
//   The modern code for 'Yiddish' is 'yi', we use the old code 'ji'
//   The modern code for 'Indonesian' is 'id', we use the old code 'in'
//   The modern code for 'Hebrew' is 'he', we use the old code 'iw'
//   Language 'Brazilian Portuguese' has been added with code 'bp'
//   Language 'Taiwanese Chinese' has been added with code 'tc'
//   Language 'Friulian' has been added with code 'fu'.  See ticket #2075. 
//   Language 'Norwegian' is deprecio
//   Language 'Norwegian' is depreciated, see Ticket #2164.  Norwegian
//       will be kept until the transition to the Wicket based website.

function locale_get_language_map() {
    return array(
        "ab" => "Abkhazian",
        "aa" => "Afar",
        "af" => "Afrikaans",
        "ak" => "Akan",
        "sq" => "Albanian",
        "am" => "Amharic",
        "ar" => "Arabic",
        "an" => "Aragonese",
        "hy" => "Armenian",
        "as" => "Assamese",
        "av" => "Avaric",
        "ae" => "Avestan",
        "ay" => "Aymara",
        "az" => "Azerbaijani",
        "bm" => "Bambara",
        "ba" => "Bashkir",
        "eu" => "Basque",
        "be" => "Belarusian",
        "bn" => "Bengali",
        "bh" => "Bihari",
        "bi" => "Bislama",
        "bs" => "Bosnian",
        "bp" => "Brazilian Portuguese", /* PhET custom code */
        "br" => "Breton",
        "bg" => "Bulgarian",
        "my" => "Burmese",
        "ca" => "Catalan",
        "ch" => "Chamorro",
        "ce" => "Chechen",
        "ny" => "Chichewa",
        "zh" => "Chinese",
        "cu" => "Church Slavic",
        "cv" => "Chuvash",
        "kw" => "Cornish",
        "co" => "Corsican",
        "cr" => "Cree",
        "hr" => "Croatian",
        "cs" => "Czech",
        "da" => "Danish",
        "dv" => "Divehi",
        "nl" => "Dutch",
        "dz" => "Dzongkha",
        "en" => "English",
        "eo" => "Esperanto",
        "et" => "Estonian",
        "ee" => "Ewe",
        "fo" => "Faroese",
        "fj" => "Fijian",
        "fi" => "Finnish",
        "fr" => "French",
        "fu" => "Friulian", /* added code, see Ticket #2075 */
        "ff" => "Fulah",
        "gl" => "Galician",
        "lg" => "Ganda",
        "ka" => "Georgian",
        "de" => "German",
        "el" => "Greek",
        "gn" => "Guarani",
        "gu" => "Gujarati",
        "ht" => "Haitian",
        "ha" => "Hausa",
        "he" => "Hebrew", /* older code, this is used, also see "iw" */
        "iw" => "Hebrew", /* new code, not used, included for safety, also see "he" */
        "hz" => "Herero",
        "hi" => "Hindi",
        "ho" => "Hiri Motu",
        "hu" => "Hungarian",
        "is" => "Icelandic",
        "io" => "Ido",
        "ig" => "Igbo",
        "id" => "Indonesian", /* newer code, not used, included for safety, see also "id" */
        "in" => "Indonesian", /* older code, this is used, see also "id" */
        "ia" => "Interlingua",
        "ie" => "Interlingue",
        "iu" => "Inuktitut",
        "ik" => "Inupiaq",
        "ga" => "Irish",
        "it" => "Italian",
        "ja" => "Japanese",
        "jv" => "Javanese",
        "kl" => "Kalaallisut",
        "kn" => "Kannada",
        "kr" => "Kanuri",
        "ks" => "Kashmiri",
        "kk" => "Kazakh",
        "km" => "Khmer",
        "ki" => "Kikuyu",
        "rw" => "Kinyarwanda",
        "ky" => "Kirghiz",
        "rn" => "Kirundi",
        "kv" => "Komi",
        "kg" => "Kongo",
        "ko" => "Korean",
        "ku" => "Kurdish",
        "kj" => "Kwanyama",
        "lo" => "Lao",
        "la" => "Latin",
        "lv" => "Latvian",
        "li" => "Limburgish",
        "ln" => "Lingala",
        "lt" => "Lithuanian",
        "lu" => "Luba-Katanga",
        "lb" => "Luxembourgish",
        "mk" => "Macedonian",
        "mg" => "Malagasy",
        "ms" => "Malay",
        "ml" => "Malayalam",
        "mt" => "Maltese",
        "gv" => "Manx",
        "mi" => "Maori",
        "mr" => "Marathi",
        "mh" => "Marshallese",
        "mo" => "Moldavian",
        "mn" => "Mongolian",
        "na" => "Nauru",
        "nv" => "Navajo",
        "ng" => "Ndonga",
        "ne" => "Nepali",
        "nd" => "North Ndebele",
        "se" => "Northern Sami",
        "nb" => "Norwegian Bokmal",
        "nn" => "Norwegian Nynorsk",
        "no" => "Norwegian",  // Norwegian is depreciated, see Ticket #2164.  Norwegian will be kept until the transition to the Wicket based website.
        "oc" => "Occitan",
        "oj" => "Ojibwa",
        "or" => "Oriya",
        "om" => "Oromo",
        "os" => "Ossetian",
        "pi" => "Pali",
        "pa" => "Panjabi",
        "ps" => "Pashto",
        "fa" => "Persian",
        "pl" => "Polish",
        "pt" => "Portuguese",
        "qu" => "Quechua",
        "rm" => "Raeto-Romance",
        "ro" => "Romanian",
        "ru" => "Russian",
        "ry" => "Rusyn",
        "sm" => "Samoan",
        "sg" => "Sango",
        "sa" => "Sanskrit",
        "sc" => "Sardinian",
        "gd" => "Scottish Gaelic",
        "sr" => "Serbian",
        "sh" => "Serbo-Croatian",
        "sn" => "Shona",
        "ii" => "Sichuan Yi",
        "sd" => "Sindhi",
        "si" => "Sinhalese",
        "sk" => "Slovak",
        "sl" => "Slovenian",
        "so" => "Somali",
        "st" => "Sotho",
        "nr" => "South Ndebele",
        "es" => "Spanish",
        "su" => "Sundanese",
        "sw" => "Swahili",
        "ss" => "Swati",
        "sv" => "Swedish",
        "tl" => "Tagalog",
        "ty" => "Tahitian",
        "tc" => "Taiwanese Chinese",  /* PhET custom code */
        "tg" => "Tajik",
        "ta" => "Tamil",
        "tt" => "Tatar",
        "te" => "Telugu",
        "th" => "Thai",
        "bo" => "Tibetan",
        "ti" => "Tigrinya",
        "to" => "Tonga",
        "ts" => "Tsonga",
        "tn" => "Tswana",
        "tr" => "Turkish",
        "tk" => "Turkmen",
        "tw" => "Twi",
        "ug" => "Uighur",
        "uk" => "Ukrainian",
        "ur" => "Urdu",
        "uz" => "Uzbek",
        "ve" => "Venda",
        "vi" => "Vietnamese",
        "vo" => "Volapuk",  // removed umlated u in case PHP can't handle it
        "wa" => "Walloon",
        "cy" => "Welsh",
        "fy" => "Western Frisian",
        "wo" => "Wolof",
        "xh" => "Xhosa",
        "ji" => "Yiddish", /* older code, this is used, see also "yi" */
        "yi" => "Yiddish", /* newer code, not used, included for safety, see also "ji" */
        "yo" => "Yoruba",
        "za" => "Zhuang",
        "zu" => "Zulu"
        );

}

?>

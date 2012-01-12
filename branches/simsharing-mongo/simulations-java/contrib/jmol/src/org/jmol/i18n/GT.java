/* $RCSfile$
 * $Author: nicove $
 * $Date: 2011-03-07 15:29:08 -0800 (Mon, 07 Mar 2011) $
 * $Revision: 15244 $
 *
 * Copyright (C) 2005  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.i18n;

import java.text.MessageFormat;
import java.util.*;

import org.jmol.util.Logger;

public class GT {

  private static boolean ignoreApplicationBundle = false;
  private static GT getTextWrapper;
  private ResourceBundle[] translationResources = null;
  private int translationResourcesCount = 0;
  private boolean doTranslate = true;
  private String language;

  public GT(String la) {
    getTranslation(la);
  }
  
  private GT() {
    getTranslation(null);
  }

  // =============
  // Language list
  // =============

  public static class Language {
    public final String code;
    public final String language;
    public final String nativeLanguage;
    private boolean display;

    /**
     * @param code Language code (see ISO 639-1 for the values)
     * @param language Language name in English (see ISO 639-1 for the values)
     * @param nativeLanguage Language name in its own language (see ISO 639-1 for the values)
     * @param display True if this language has a good percentage of translations done
     * 
     * {@link "http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes"}
     */
    public Language(String code, String language, String nativeLanguage, boolean display) {
      this.code = code;
      this.language = language;
      this.nativeLanguage = nativeLanguage;
      this.display = display;
    }

    public boolean shouldDisplay() {
      return display;
    }

    public void forceDisplay() {
      display = true;
    }
  }

  private static Language[] languageList;
  //private static String languagePath;
  
  public static Language[] getLanguageList() {
    return (languageList != null ? languageList : getTextWrapper().createLanguageList());
  }

  /**
   * This is the place to put the list of supported languages. It is accessed
   * by JmolPopup to create the menu list. Note that the names are in GT._
   * even though we set doTranslate false. That ensures that the language name
   * IN THIS LIST is untranslated, but it provides the code xgettext needs in
   * order to provide the list of names that will need translation by translators
   * (the .po files). Later, in JmolPopup.updateLanguageMenu(), GT._() is used
   * again to create the actual, localized menu item name.
   *
   * list order:
   * 
   * The order presented here is the order in which the list will be presented in the 
   * popup menu. In addition, the order of variants is significant. In all cases, place
   * common-language entries in the following order:
   * 
   * la_co_va
   * la_co
   * la
   * 
   * In addition, there really is no need for "la" by itself. Every translator introduces
   * a bias from their originating country. It would be perfectly fine if we had NO "la"
   * items, and just la_co. Thus, we could have just:
   * 
   * pt_BR
   * pt_PT
   * 
   * In this case, the "default" language translation should be entered LAST.
   * 
   * If a user selects pt_ZQ, the code below will find (a) that we don't support pt_ZQ, 
   * (b) that we don't support pt_ZQ_anything, (c) that we don't support pt, and, finally,
   * that we do support pt_PT, and it will select that one, returning to the user the message
   * that language = "pt_PT" instead of pt_ZQ.
   *  
   * For that matter, we don't even need anything more than 
   * 
   * la_co_va
   * 
   * because the algorithm will track that down from anything starting with la, and in all cases
   * find the closest match. 
   * 
   * Introduced in Jmol 11.1.34 
   * Author Bob Hanson May 7, 2007
   * @return  list of codes and untranslated names
   */
  synchronized private Language[] createLanguageList() {
    boolean wasTranslating = doTranslate;
    doTranslate = false;
    languageList = new Language[] {
      new Language("ar",    GT._("Arabic"),               "العربية",              false),
      new Language("ca",    GT._("Catalan"),              "Català",               true),
      new Language("cs",    GT._("Czech"),                "Čeština",              true),
      new Language("da",    GT._("Danish"),               "Dansk",                true),
      new Language("de",    GT._("German"),               "Deutsch",              true),
      new Language("el",    GT._("Greek"),                "Ελληνικά",             false),
      new Language("en_GB", GT._("British English"),      "British English",      true),
      new Language("en_US", GT._("American English"),     "American English",     true), // global default for "en" will be "en_US"
      new Language("es",    GT._("Spanish"),              "Español",              true),
      new Language("et",    GT._("Estonian"),             "Eesti",                false),
      new Language("fi",    GT._("Finnish"),              "Suomi",                true),
      new Language("fo",    GT._("Faroese"),              "Føroyskt",             false),
      new Language("fr",    GT._("French"),               "Français",             true),
      new Language("hr",    GT._("Croatian"),             "Hrvatski",             false),
      new Language("hu",    GT._("Hungarian"),            "Magyar",               true),
      new Language("id",    GT._("Indonesian"),           "Indonesia",            true),
      new Language("it",    GT._("Italian"),              "Italiano",             true),
      new Language("ja",    GT._("Japanese"),             "日本語",               false),
      new Language("jv",    GT._("Javanese"),             "Basa Jawa",            false),
      new Language("ko",    GT._("Korean"),               "한국어",               true),
      new Language("nb",    GT._("Norwegian Bokmal"),     "Norsk Bokmål",         false),
      new Language("nl",    GT._("Dutch"),                "Nederlands",           true),
      new Language("oc",    GT._("Occitan"),              "Occitan",              false),
      new Language("pl",    GT._("Polish"),               "Polski",               false),
      new Language("pt",    GT._("Portuguese"),           "Português",            false),
      new Language("pt_BR", GT._("Brazilian Portuguese"), "Português brasileiro", true),
      new Language("ru",    GT._("Russian"),              "Русский",              false),
      new Language("sl",    GT._("Slovenian"),            "Slovenščina",          false),
      new Language("sv",    GT._("Swedish"),              "Svenska",              false),
      new Language("ta",    GT._("Tamil"),                "தமிழ்",                 false),
      new Language("tr",    GT._("Turkish"),              "Türkçe",               true),
      new Language("uk",    GT._("Ukrainian"),            "Українська",           true),
      new Language("zh_CN", GT._("Simplified Chinese"),   "简体中文",             true),
      new Language("zh_TW", GT._("Traditional Chinese"),  "繁體中文",             true),
    };
    doTranslate = wasTranslating;
    return languageList;
  }

  private String getSupported(String languageCode, boolean isExact) {
    if (languageCode == null)
      return null;
    if (languageList == null)
      createLanguageList();
    for (int i = 0; i < languageList.length; i++) {
      if (languageList[i].code.equalsIgnoreCase(languageCode))
        return languageList[i].code;
    }
    return (isExact ? null : findClosest(languageCode));
  }
 
  /**
   * 
   * @param la
   * @return   a localization of the desired language, but not it exactly 
   */
  private String findClosest(String la) {
    for (int i = languageList.length; --i >= 0; ) {
      if (languageList[i].code.startsWith(la))
        return languageList[i].code;
    }
    return null;    
  }
  
  public static String getLanguage() {
    return getTextWrapper().language;
  }
  
  synchronized private void getTranslation(String langCode) {
    Locale locale;
    translationResources = null;
    translationResourcesCount = 0;
    getTextWrapper = this;
    if (langCode != null && langCode.length() == 0)
      langCode="none";
    if (langCode != null)
      language = langCode;
    if ("none".equals(language))
      language = null;
    if (language == null && (locale = Locale.getDefault()) != null) {
      language = locale.getLanguage();
      if (locale.getCountry() != null) {
        language += "_" + locale.getCountry();
        if (locale.getVariant() != null && locale.getVariant().length() > 0)
          language += "_" + locale.getVariant();
      }
    }
    if (language == null)
      language = "en";

    int i;
    String la = language;
    String la_co = language;
    String la_co_va = language;
    if ((i = language.indexOf("_")) >= 0) {
      la = la.substring(0, i);
      if ((i = language.indexOf("_", ++i)) >= 0) {
        la_co = language.substring(0, i);
      } else {
        la_co_va = null;
      }
    } else {
      la_co = null;
      la_co_va = null;
    }

    /*
     * find the best match. In each case, if the match is not found,
     * but a variation at the next level higher exists, pick that variation.
     * So, for example, if fr_CA does not exist, but fr_FR does, then 
     * we choose fr_FR, because that is taken as the "base" class for French.
     * 
     * Or, if the language requested is "fr", and there is no fr.po, but there
     * is an fr_FR.po, then return that. 
     * 
     * Thus, the user is informed of which country/variant is in effect,
     * if they want to know. 
     * 
     */
    if ((language = getSupported(la_co_va, false)) == null
        && (language = getSupported(la_co, false)) == null
        && (language = getSupported(la, false)) == null) {
      language = "en";
      Logger.debug(language + " not supported -- using en");
      return;
    }
    la_co_va = null;
    la_co = null;
    switch (language.length()) {
    case 2:
      la = language;
      break;
    case 5:
      la_co = language;
      la = language.substring(0, 2);
      break;
    default:
      la_co_va = language;
      la_co = language.substring(0, 5);
      la = language.substring(0, 2);
    }

    /*
     * Time to determine exactly what .po files we actually have.
     * No need to check a file twice.
     * 
     */

    la_co = getSupported(la_co, false);
    la = getSupported(la, false);

    if (la == la_co || "en_US".equals(la))
      la = null;
    if (la_co == la_co_va)
      la_co = null;
    if ("en_US".equals(la_co))
      return;
    if (Logger.debugging)
      Logger.debug("Instantiating gettext wrapper for " + language
          + " using files for language:" + la + " country:" + la_co
          + " variant:" + la_co_va);
    if (!ignoreApplicationBundle)
      addBundles("Jmol", la_co_va, la_co, la);
    addBundles("JmolApplet", la_co_va, la_co, la);
  }
  
  private void addBundles(String type, String la_co_va, String la_co, String la) {
    try {
        String className = "org.jmol.translation." + type + ".";
        if (la_co_va != null)
          addBundle(className, la_co_va);
        if (la_co != null)
          addBundle(className, la_co);
        if (la != null)
          addBundle(className, la);
    } catch (Exception exception) {
      Logger.error("Some exception occurred!", exception);
      translationResources = null;
      translationResourcesCount = 0;
    }
  }

  private void addBundle(String className, String name) {
    Class<?> bundleClass = null;
    className += name + ".Messages_" + name;
    //    if (languagePath != null
    //      && !ZipUtil.isZipFile(languagePath + "_i18n_" + name + ".jar"))
    //  return;
    try {
      bundleClass = Class.forName(className);
    } catch (Throwable e) {
      Logger.error("GT could not find the class " + className);
    }
    if (bundleClass == null
        || !ResourceBundle.class.isAssignableFrom(bundleClass))
      return;
    try {
      ResourceBundle myBundle = (ResourceBundle) bundleClass.newInstance();
      if (myBundle != null) {
        if (translationResources == null) {
          translationResources = new ResourceBundle[8];
          translationResourcesCount = 0;
        }
        translationResources[translationResourcesCount] = myBundle;
        translationResourcesCount++;
        Logger.debug("GT adding " + className);
      }
    } catch (IllegalAccessException e) {
      Logger.warn("Illegal Access Exception: " + e.getMessage());
    } catch (InstantiationException e) {
      Logger.warn("Instantiation Excaption: " + e.getMessage());
    }
  }

  private static GT getTextWrapper() {
    return (getTextWrapper == null ? getTextWrapper = new GT() : getTextWrapper);
  }

  public static void ignoreApplicationBundle() {
    ignoreApplicationBundle = true;
  }

  public static void setDoTranslate(boolean TF) {
    getTextWrapper().doTranslate = TF;
  }

  public static boolean getDoTranslate() {
    return getTextWrapper().doTranslate;
  }

  public static String _(String string) {
    return getTextWrapper().getString(string);
  }

  public static String _(String string, String item) {
    return getTextWrapper().getString(string, new Object[] { item });
  }

  public static String _(String string, int item) {
    return getTextWrapper().getString(string,
        new Object[] { Integer.valueOf(item) });
  }

  public static String _(String string, Object[] objects) {
    return getTextWrapper().getString(string, objects);
  }

  //forced translations
  
  public static String _(String string, boolean t) {
    return _(string, (Object[]) null, t);
  }

  public static String _(String string,
                         String item,
                         @SuppressWarnings("unused") boolean t) {
    return _(string, new Object[] { item });
  }

  public static String _(String string,
                         int item,
                         @SuppressWarnings("unused") boolean t) {
    return _(string, new Object[] { Integer.valueOf(item) });
  }

  public static synchronized String _(String string,
                                      Object[] objects,
                                      @SuppressWarnings("unused") boolean t) {
    boolean wasTranslating;
    if (!(wasTranslating = getTextWrapper().doTranslate))
      setDoTranslate(true);
    String str = (objects == null ? _(string) : _(string, objects));
    if (!wasTranslating)
      setDoTranslate(false);
    return str;
  }

  private String getString(String string) {
    if (!doTranslate || translationResourcesCount == 0)
      return string;
    for (int bundle = 0; bundle < translationResourcesCount; bundle++) {
      try {
        String trans = translationResources[bundle].getString(string);
        return trans;
      } catch (MissingResourceException e) {
        // Normal
      }
    }
    if (Logger.debugging) {
      Logger.info("No trans, using default: " + string);
    }
    return string;
  }

  private String getString(String string, Object[] objects) {
    String trans = null;
    if (!doTranslate)
      return MessageFormat.format(string, objects);
    for (int bundle = 0; bundle < translationResourcesCount; bundle++) {
      try {
        trans = MessageFormat.format(translationResources[bundle]
            .getString(string), objects);
        return trans;
      } catch (MissingResourceException e) {
        // Normal
      }
    }
    trans = MessageFormat.format(string, objects);
    if (translationResourcesCount > 0) {
      if (Logger.debugging) {
        Logger.debug("No trans, using default: " + trans);
      }
    }
    return trans;
  }

  public static String escapeHTML(String msg) {
    char ch;
    for (int i = msg.length(); --i >= 0;)
      if ((ch = msg.charAt(i)) > 0x7F) {
        msg = msg.substring(0, i) 
            + "&#" + ((int)ch) + ";" + msg.substring(i + 1);
      }
    return msg;   
  }

  /**
   * 
   * @param languagePath UNUSED
   */
  public static void setLanguagePath(String languagePath) {
    //GT.languagePath = languagePath;
  }
}

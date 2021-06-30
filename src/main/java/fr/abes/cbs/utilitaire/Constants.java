package fr.abes.cbs.utilitaire;

public class Constants {
    public static final String SUBFIELD = "subfield";
    public static final String DOUBLEDOLLAR = "\\$\\$";
    public static final String DATAFIELD = "<datafield  tag=\"";
    public static final String IND1 = "\" ind1=\"";
    public static final String IND2 = "\" ind2=\"";
    public static final String ONETWODOLLAR = "1dollar2";
    public static final String VERROR = "VERROR";
    public static final String VTAFR = "VTAFR";
    public static final String VCUTF8 = "V!Cutf-8";
    public static final String CUTF8 = "Cutf-8";
    public static final String CUS = "CUS";
    public static final String VT1 = "V!T1";
    public static final String VTI1 = "VTI1";
    public static final String VTXTE = "VTXTE";
    public static final String VTXTBIB = "VTXTBIB";
    public static final String VTX0T = "VTX0T";
    public static final String BBWVDA0 = "BBWVDA0";
    public static final String BBWVTX0T = "BBWVTX0T";
    public static final String VTX0TBIB = "VTX0TBIB";
    public static final String VSE = "VSE";
    public static final String VSE3 = "VSE3";
    public static final String VTX = "VTX";
    public static final String VET = "VET";
    public static final String VBY = "VBY";
    public static final String VMC = "VMC";
    public static final String VSE1 = "VSE1";
    public static final String VPRI = "VPRI";
    public static final String VPRK = "VPRK";
    public static final String VTXT = "VTXT";
    public static final String VPRUNMA = "VPRUNMA";
    public static final String VPRUNM = "VPRUNM";
    public static final String CKPPN = "CPC\\KIL \\PPN ";
    public static final String CMPPN = "CPC\\MUT \\PPN ";
    public static final String CIPPN = "CPC\\INV \\PPN ";
    public static final String CPCWIS = "CPC\\WIS \\USE ";
    public static final String CUSTOO = "CUS\\too S";
    public static final String CUSCREL = "CUScre l";
    public static final String CUSMUT = "CUS\\MUT S";
    public static final String UNMTOO = " UNM;\\TOO S";
    public static final String LV0 = "LV0";
    public static final String LV1 = "LV1";
    public static final String LV2 = "LV2";
    public static final String LV3 = "LV3";
    public static final String LV4 = "LV4";
    public static final String UNM = "UNM";
    public static final String UNMA = "UNMA";
    public static final String DOLLAR = "$";
    public static final String DIEZ = "#";
    public static final String LP = "P";
    public static final String BSA = "\\$a";
    public static final String ERR_NOTICE_NSELECTED = "pas de notice selectionnée ou pas de no d exemplaire";
    public static final String MSG_APPN = "avec PPN ";

    public static final String STR_1D = new String(new char[] { (char) 29 });
    public static final String STR_1E = new String(new char[] { (char) 30 });
    public static final String STR_0D = new String(new char[] { (char) 13 });
    public static final String STR_1B = new String(new char[] { (char) 27 });
    public static final String STR_1F = new String(new char[] { (char) 31 });
    public static final String STR_E9 = new String(new char[] { (char) 101 });
    public static final String STR_769 = new String(new char[] { (char) 769 });
    public static final char SEP_CHAMP = (char) Integer.parseInt("0D", 16);
    public static final String STR_DOLLAR = new String(new char[] { (char) 36 });
    public static final String STR_SPACE = new String(new char[] { (char) 32 });
    public static final String FINLIGNE = new String(
            new char[] { (char) 13, (char) 10, (char) 13, (char) 10, (char) 13, (char) 10, (char) 31, (char) 30 });
    public static final String SEPS = new String(new char[] { (char) 27, 'L', 'P', 'P' });
    public static final String SEPS1 = new String(new char[] { (char) 27, 'L', 'N', 'R' });
    public static final String LMA = new String(new char[] { (char) 27, 'L', 'M', 'A' });
    public static final String CR_152_S = new String(new char[] { (char) 152 });
    public static final String CR_156_S = new String(new char[] { (char) 156 });

    public static final String STR_ZOE = "?\\zoe+";

    public static final String A97 = "A97";
    public static final String A98 = "A98";
    public static final String A99 = "A99";

    /*******************************************************************/
    /* REGEXP pour matcher les zones / sous zones exemplaires **********/
    /*******************************************************************/

    //NOTE En cas d'extension du nombre de sous zones possibles par zone,
    // il suffit d'augmenter le nombre de groupes szvN / szkN et aussi d'augmenter le compteur dans la boucle for

    //Traduction des acronymes de groupe de la zone système
    //zStaName := nom de la ZONE
    //zStaSpace := espaces au nombre indeterminés entre la ZONE et le début de la suite de sous-zones ou dièses
    //szk(n) := clé n d'une sous-zone
    //szv(n) := valeur n d'une sous zone

    //Traduction des acronymes de groupe des zones système
    //zSysName := nom de la zone système
    //zSysSpace := espaces au nombre indeterminé entre le nom de la zone système et les informations
    //zSysv(n) := valeur n d'une zone système, zSysv0:97 = DATE, zSysv1:97 = HOUR, zSysv0:98 = RCR, zSysv1:98 = DATE, zSysv0
    //zSysSpace2 := espacement hypothetique entre la fin de la sous zone (espacements inutiles)

    public static final String zoneSansSousZoneRegex        = "^(\\x1b[PD])?(?<zStaName>[\\d]{3})? *(?<zSysSpace>\\s*)? *(?<zVal>.*)?";
    public static final String standardZoneSequence         = "^(?<zStaName>[^A][^E][\\d]*)(?<zStaSpace>\\s*)(?<zStaHash>[\\d]*[#]*[\\d]*) *(?<szv>.+)?";
    public static final String systemZone97Regex            = "^(?<zSysName>[A][\\d]*)? *(?<zSysSpace>\\s*)? *(?<zSysv0>[\\d][\\d][-][\\d][\\d][-][\\d][\\d])? *(?<zSysv1>\\d\\d:\\d\\d:\\d\\d.?\\d?\\d?\\d?)? *(?<zSysSpace2>\\s*)?";
    public static final String systemZone98Regex            = "^(?<zSysName>[A][\\d]*)? *(?<zSysSpace>\\s*)? *(?<zSysv0>\\d{1,9}:?)? *(?<zSysv1>\\d\\d-\\d\\d-\\d\\d)?";
    public static final String systemZone99Regex            = "^(?<zSysName>[A][\\d]*)? *(?<zSysSpace>\\s*)? *(?<zSysv0>\\d{1,9}X?:?)? *(?<zSysSpace2>\\s*)?";
    public static final String donneesLocalesRegex          = "^(?<zStaName>[L]?[0-9][0-9][0-9])(?<zStaSpace>\\s*)(?<zStaHash>[\\d]*[#]*[\\d]*) *((?<szk0>[$][a-z])(?<szv0>[^$]*))? *((?<szk1>[$][a-z])(?<szv1>[^$]*))? *((?<szk2>[$][a-z])(?<szv2>[^$]*))? *((?<szk3>[$][a-z])(?<szv3>[^$]*))? *((?<szk4>[$][a-z])(?<szv4>[^$]*))? *((?<szk5>[$][a-z])(?<szv5>[^$]*))? *((?<szk6>[$][a-z])(?<szv6>[^$]*))? *((?<szk7>[$][a-z])(?<szv7>[^$]*))? *((?<szk8>[$][a-z])(?<szv8>[^$]*))? *((?<szk9>[$][a-z])(?<szv9>[^$]*))? *((?<szk10>[$][a-z])(?<szv10>[^$]*))? *((?<szk11>[$][a-z])(?<szv11>[^$]*))? *((?<szk12>[$][a-z])(?<szv12>[^$]*))? *((?<szk13>[$][a-z])(?<szv13>[^$]*))? *((?<szk14>[$][a-z])(?<szv14>[^$]*))? *((?<szk15>[$][a-z])(?<szv15>[^$]*))? *((?<szk16>[$][a-z])(?<szv16>[^$]*))? *((?<szk17>[$][a-z])(?<szv17>[^$]*))? *((?<szk18>[$][a-z])(?<szv18>[^$]*))? *((?<szk19>[$][a-z])(?<szv19>[^$]*))? *((?<szk20>[$][a-z])(?<szv20>[^$]*))? *((?<szk21>[$][a-z])(?<szv21>[^$]*))? *((?<szk22>[$][a-z])(?<szv22>[^$]*))? *((?<szk23>[$][a-z])(?<szv23>[^$]*))? *((?<szk24>[$][a-z])(?<szv24>[^$]*))? *((?<szk25>[$][a-z])(?<szv25>[^$]*))? *((?<szk26>[$][a-z])(?<szv26>[^$]*))? *((?<szk27>[$][a-z])(?<szv27>[^$]*))? *((?<szk28>[$][a-z])(?<szv28>[^$]*))? *((?<szk29>[$][a-z])(?<szv29>[^$]*))? *((?<szk30>[$][a-z])(?<szv30>[^$]*))? *((?<szk31>[$][a-z])(?<szv31>[^$]*))? *((?<szk32>[$][a-z])(?<szv32>[^$]*))? *((?<szk33>[$][a-z])(?<szv33>[^$]*))? *((?<szk34>[$][a-z])(?<szv34>[^$]*))? *((?<szk35>[$][a-z])(?<szv35>[^$]*))? *((?<szk36>[$][a-z])(?<szv36>[^$]*))? *((?<szk37>[$][a-z])(?<szv37>[^$]*))? *((?<szk38>[$][a-z])(?<szv38>[^$]*))? *((?<szk39>[$][a-z])(?<szv39>[^$]*))? *((?<szk40>[$][a-z])(?<szv40>[^$]*))? *((?<szk41>[$][a-z])(?<szv41>[^$]*))? *((?<szk42>[$][a-z])(?<szv42>[^$]*))? *((?<szk43>[$][a-z])(?<szv43>[^$]*))? *((?<szk44>[$][a-z])(?<szv44>[^$]*))? *((?<szk45>[$][a-z])(?<szv45>[^$]*))? *((?<szk46>[$][a-z])(?<szv46>[^$]*))? *((?<szk47>[$][a-z])(?<szv47>[^$]*))? *((?<szk48>[$][a-z])(?<szv48>[^$]*))? *((?<szk49>[$][a-z])(?<szv49>[^$]*))? *((?<szk50>[$][a-z])(?<szv50>[^$]*))?";
    public static final String systemZoneL005Regex          = "^(?<zSysName>L005)? *(?<zSysSpace>\\s*)? *(?<zSysv0>[\\d][\\d][-][\\d][\\d][-][\\d][\\d])? *(?<zSysv1>\\d\\d:\\d\\d:\\d\\d.?\\d?\\d?\\d?)? *(?<zSysSpace2>\\s*)?";

    public static final String SOUS_ZONE_REGEXP_SEQUENCE    = "^((?<szk0>[$][a-z\\d])(?<szv0>[^$]*))? *((?<szk1>[$][a-z\\d])(?<szv1>[^$]*))? *((?<szk2>[$][a-z\\d])(?<szv2>[^$]*))? *((?<szk3>[$][a-z\\d])(?<szv3>[^$]*))? *((?<szk4>[$][a-z\\d])(?<szv4>[^$]*))? *((?<szk5>[$][a-z\\d])(?<szv5>[^$]*))? *((?<szk6>[$][a-z\\d])(?<szv6>[^$]*))? *((?<szk7>[$][a-z\\d])(?<szv7>[^$]*))? *((?<szk8>[$][a-z\\d])(?<szv8>[^$]*))? *((?<szk9>[$][a-z\\d])(?<szv9>[^$]*))? *((?<szk10>[$][a-z\\d])(?<szv10>[^$]*))? *((?<szk11>[$][a-z\\d])(?<szv11>[^$]*))? *((?<szk12>[$][a-z\\d])(?<szv12>[^$]*))? *((?<szk13>[$][a-z\\d])(?<szv13>[^$]*))? *((?<szk14>[$][a-z\\d])(?<szv14>[^$]*))?";

    public static final int NB_SOUS_ZONES_REGEXP_STANDARD = 50;
    public static final int NB_SOUS_ZONES_REGEXP_ETAT_COLLECTION = 100;

    public static final String ZONE_BIBLIO_NAME_REGEX = "^[0-8]\\d{2}";
    public static final String ZONE_LOCALE_NAME_REGEX = "^[L]\\d{3}";
    public static final String ZONE_EXEMPLAIRE_NAME_REGEX = "^([A|e]\\d{2})|^([E]\\d{3})|^([9]\\d{2})";
    //public static final String ZONE_DEBUT_EXEMPLAIRE_REGEX = "^[e]\\d{2}";
    public static final String ZONE_DEBUT_EXEMPLAIRE_XML_REGEX = "930";

    /***
     * Constructeur privé permet de rendre la classe non instantiable
     */
    private Constants() {
    }
}

package fr.abes.cbs.zones;

import fr.abes.cbs.notices.TYPE_NOTICE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZonesSpecifications {

    public static ZoneSpecification getZoneSpecification(String label, TYPE_NOTICE type) {
        if (label.matches("[e]\\d\\d")) {
            return zoneSpecification.stream().filter(zone -> zone.name.equals("exx") && zone.typeNoticeZone.equals(TYPE_NOTICE.EXEMPLAIRE)).findFirst().orElse(null);
        } else if (label.matches("[A]\\d\\d")) {
            return zoneSpecification.stream().filter(zone -> zone.name.equals("Axx") && zone.typeNoticeZone.equals(TYPE_NOTICE.EXEMPLAIRE)).findFirst().orElse(null);
        }
        return zoneSpecification.stream().filter(zone -> zone.name.equals(label) && zone.typeNoticeZone.equals(type)).findFirst().orElse(null);
    }

    public static final List<ZoneSpecification> zoneSpecification = new ArrayList<ZoneSpecification>(
            Arrays.asList(
                    //format bibliographique
                    new ZoneSpecification("000", "0", true, TYPE_NOTICE.BIBLIOGRAPHIQUE),
                    new ZoneSpecification("001", ""),
                    new ZoneSpecification("002", "a2"),
                    new ZoneSpecification("003", ""),
                    new ZoneSpecification("004", ""),
                    new ZoneSpecification("005", ""),
                    new ZoneSpecification("006", ""),
                    new ZoneSpecification("007", ""),
                    new ZoneSpecification("008", "a"),
                    new ZoneSpecification("00A", "0", true, TYPE_NOTICE.BIBLIOGRAPHIQUE),
                    new ZoneSpecification("00U", "0", true, TYPE_NOTICE.BIBLIOGRAPHIQUE),
                    new ZoneSpecification("010", "aAbdz9"),
                    new ZoneSpecification("011", "abdfgyz"),
                    new ZoneSpecification("012", "a2"),
                    new ZoneSpecification("013", "abdz"),
                    new ZoneSpecification("014", "az2"),
                    new ZoneSpecification("015", "abdz"),
                    new ZoneSpecification("017", "abdz2"),
                    new ZoneSpecification("019", "aAbdz"),
                    new ZoneSpecification("020", "abz"),
                    new ZoneSpecification("021", "abz"),
                    new ZoneSpecification("022", "abz"),
                    new ZoneSpecification("023", "a"),
                    new ZoneSpecification("024", "abx3"),
                    new ZoneSpecification("029", "abemoz"),
                    new ZoneSpecification("032", "a0"),
                    new ZoneSpecification("033", "az2d"),
                    new ZoneSpecification("034", "a0b"),
                    new ZoneSpecification("035", "az2d"),
                    new ZoneSpecification("040", "az"),
                    new ZoneSpecification("071", "bacdz"),
                    new ZoneSpecification("072", "abcdz"),
                    new ZoneSpecification("073", "abcdz"),
                    new ZoneSpecification("100", "abcdefgh"),
                    new ZoneSpecification("101", "abcdefghij"),
                    new ZoneSpecification("102", "abc2"),
                    new ZoneSpecification("104", "abcdef"),
                    new ZoneSpecification("105", "abcdefg"),
                    new ZoneSpecification("106", "a"),
                    new ZoneSpecification("110", "abcdefghi"),
                    new ZoneSpecification("115", "abcdefghijklmnoABCDEFGHJK"),
                    new ZoneSpecification("116", "abcdefg"),
                    new ZoneSpecification("117", "abc"),
                    new ZoneSpecification("120", "abcdef"),
                    new ZoneSpecification("121", "abcdefgABCDEF"),
                    new ZoneSpecification("122", "a"),
                    new ZoneSpecification("123", "abcdefghijkmnopq"),
                    new ZoneSpecification("124", "abcdefg"),
                    new ZoneSpecification("125", "abAB"),
                    new ZoneSpecification("126", "abcdefghijABC"),
                    new ZoneSpecification("127", "a"),
                    new ZoneSpecification("128", "abcd"),
                    new ZoneSpecification("130", "abcd"),
                    new ZoneSpecification("135", "abcdefghijk"),
                    new ZoneSpecification("140", ""),
                    new ZoneSpecification("181", "Pc"),
                    new ZoneSpecification("182", "Pc"),
                    new ZoneSpecification("183", "Pa"),
                    new ZoneSpecification("200", "67[acdehirfg]z"),
                    new ZoneSpecification("205", "67abdfg9"),
                    new ZoneSpecification("206", "67abcdef"),
                    new ZoneSpecification("207", "67az"),
                    new ZoneSpecification("208", "67ad"),
                    new ZoneSpecification("209", "bcdh"),
                    new ZoneSpecification("210", "67[abcd][efgh]rs"),
                    new ZoneSpecification("211", "a"),
                    new ZoneSpecification("214", "67[abc]drs"),
                    new ZoneSpecification("215", "acde"),
                    new ZoneSpecification("219", "abcdiprsP"),
                    new ZoneSpecification("225", "adefhivxz"),
                    new ZoneSpecification("230", "a"),
                    new ZoneSpecification("300", "au2"),
                    new ZoneSpecification("301", "au2"),
                    new ZoneSpecification("302", "au2"),
                    new ZoneSpecification("303", "au2"),
                    new ZoneSpecification("304", "au2"),
                    new ZoneSpecification("305", "au2"),
                    new ZoneSpecification("306", "au2"),
                    new ZoneSpecification("307", "au2"),
                    new ZoneSpecification("308", "au2"),
                    new ZoneSpecification("309", "abcd"),
                    new ZoneSpecification("310", "au2"),
                    new ZoneSpecification("311", "au2"),
                    new ZoneSpecification("312", "au2"),
                    new ZoneSpecification("313", "au2"),
                    new ZoneSpecification("314", "au2"),
                    new ZoneSpecification("315", "au2"),
                    new ZoneSpecification("316", "au2"),
                    new ZoneSpecification("317", "au2"),
                    new ZoneSpecification("320", "au2"),
                    new ZoneSpecification("321", "abcxu2"),
                    new ZoneSpecification("322", "au2"),
                    new ZoneSpecification("323", "au2"),
                    new ZoneSpecification("324", "au2"),
                    new ZoneSpecification("325", "ab[cde]fghijnuvxyz"),
                    new ZoneSpecification("326", "abu2"),
                    new ZoneSpecification("327", "67au2"),
                    new ZoneSpecification("328", "zbcedtau2"),
                    new ZoneSpecification("330", "au2"),
                    new ZoneSpecification("332", "au2"),
                    new ZoneSpecification("333", "au2"),
                    new ZoneSpecification("334", "abcdu2"),
                    new ZoneSpecification("336", "au2"),
                    new ZoneSpecification("337", "au2"),
                    new ZoneSpecification("338", "[be]cdfg"),
                    new ZoneSpecification("339", "ad"),
                    new ZoneSpecification("345", "abcdu2"),
                    new ZoneSpecification("359", "vabcdefghipa"),
                    new ZoneSpecification("371", "8acd"),
                    new ZoneSpecification("410", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("411", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("412", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("413", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("421", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("422", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("423", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("424", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("425", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("430", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("431", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("432", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("433", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("434", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("435", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("436", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("437", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("440", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("441", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("442", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("443", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("444", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("445", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("446", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("447", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("448", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("451", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("452", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("453", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("454", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("455", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("456", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("461", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("463", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("464", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("470", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("481", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("482", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("488", "a[tohilbfghi]ecndpsuxy0v9"),
                    new ZoneSpecification("500", "673abhiklmnqrsuw"),
                    new ZoneSpecification("501", "673abekmrsuw"),
                    new ZoneSpecification("503", "673abdefhijklmno"),
                    new ZoneSpecification("510", "673aehijnz"),
                    new ZoneSpecification("512", "673aehijnz"),
                    new ZoneSpecification("513", "673aehijnz"),
                    new ZoneSpecification("514", "673aehijnz"),
                    new ZoneSpecification("515", "673aehijnz"),
                    new ZoneSpecification("516", "673aehijnz"),
                    new ZoneSpecification("517", "673aehijnz"),
                    new ZoneSpecification("518", "673aehijnz"),
                    new ZoneSpecification("520", "673aehijnx"),
                    new ZoneSpecification("530", "673abj"),
                    new ZoneSpecification("531", "673ab"),
                    new ZoneSpecification("532", "673az"),
                    new ZoneSpecification("540", "673aehijnz"),
                    new ZoneSpecification("541", "673aehiz"),
                    new ZoneSpecification("545", "673aehijnz"),
                    new ZoneSpecification("579", "1673"),
                    new ZoneSpecification("600", "673abcdDfxyz2"),
                    new ZoneSpecification("601", "673a[bc]dfe[xyz]2"),
                    new ZoneSpecification("602", "673afxyz2"),
                    new ZoneSpecification("604", "673atxyz2"),
                    new ZoneSpecification("605", "673ahiklmnqxyz2"),
                    new ZoneSpecification("606", "673axyz2"),
                    new ZoneSpecification("607", "673axyz2"),
                    new ZoneSpecification("608", "673axyz2"),
                    new ZoneSpecification("610", "a"),
                    new ZoneSpecification("615", "amnx2"),
                    new ZoneSpecification("616", "3acfxyz"),
                    new ZoneSpecification("620", "abcdefghikmno"),
                    new ZoneSpecification("626", "abc"),
                    new ZoneSpecification("660", "a"),
                    new ZoneSpecification("661", "a"),
                    new ZoneSpecification("670", "bcez"),
                    new ZoneSpecification("675", "avz"),
                    new ZoneSpecification("676", "avz"),
                    new ZoneSpecification("680", "ab"),
                    new ZoneSpecification("686", "abcv2"),
                    new ZoneSpecification("700", "673a[bc]dfe4"),
                    new ZoneSpecification("701", "673a[bc]dfe4"),
                    new ZoneSpecification("702", "673a[bc]dfe4"),
                    new ZoneSpecification("710", "673a[bc]dfe4"),
                    new ZoneSpecification("711", "673a[bc]dfe4"),
                    new ZoneSpecification("712", "673a[bc]dfe4"),
                    new ZoneSpecification("716", "673acf4"),
                    new ZoneSpecification("720", "673af4"),
                    new ZoneSpecification("721", "673af4"),
                    new ZoneSpecification("722", "673af4"),
                    new ZoneSpecification("801", "abcgh2"),
                    new ZoneSpecification("802", "a"),
                    new ZoneSpecification("830", "a"),
                    new ZoneSpecification("839", ""),
                    new ZoneSpecification("856", "abcdefhijklmnopqrstuvwxyz2"),
                    new ZoneSpecification("859", "abcdefhijklmnopqrstuvwxyz2"),
                    //format d'exemplaire
                    new ZoneSpecification("exx", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900"),
                    new ZoneSpecification("e01", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900aa"),
                    new ZoneSpecification("e02", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ab"),
                    new ZoneSpecification("e03", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ac"),
                    new ZoneSpecification("e04", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ad"),
                    new ZoneSpecification("e05", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ae"),
                    new ZoneSpecification("e06", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900af"),
                    new ZoneSpecification("e07", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ag"),
                    new ZoneSpecification("e08", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ah"),
                    new ZoneSpecification("e09", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ai"),
                    new ZoneSpecification("e10", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900aj"),
                    new ZoneSpecification("e11", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ba"),
                    new ZoneSpecification("e12", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bb"),
                    new ZoneSpecification("e13", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bc"),
                    new ZoneSpecification("e14", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bd"),
                    new ZoneSpecification("e15", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900be"),
                    new ZoneSpecification("e16", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bf"),
                    new ZoneSpecification("e17", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bg"),
                    new ZoneSpecification("e18", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bh"),
                    new ZoneSpecification("e19", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bi"),
                    new ZoneSpecification("e20", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900bj"),
                    new ZoneSpecification("e21", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ca"),
                    new ZoneSpecification("e22", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900cb"),
                    new ZoneSpecification("e23", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900cc"),
                    new ZoneSpecification("e24", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900cd"),
                    new ZoneSpecification("e25", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ce"),
                    new ZoneSpecification("e26", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900cf"),
                    new ZoneSpecification("e27", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900cg"),
                    new ZoneSpecification("e28", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ch"),
                    new ZoneSpecification("e29", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ci"),
                    new ZoneSpecification("e30", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900cj"),
                    new ZoneSpecification("e31", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900da"),
                    new ZoneSpecification("e32", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900db"),
                    new ZoneSpecification("e33", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900dc"),
                    new ZoneSpecification("e34", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900dd"),
                    new ZoneSpecification("e35", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900de"),
                    new ZoneSpecification("e36", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900df"),
                    new ZoneSpecification("e37", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900dg"),
                    new ZoneSpecification("e38", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900dh"),
                    new ZoneSpecification("e39", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900di"),
                    new ZoneSpecification("e40", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900dj"),
                    new ZoneSpecification("e41", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ea"),
                    new ZoneSpecification("e42", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900eb"),
                    new ZoneSpecification("e43", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ec"),
                    new ZoneSpecification("e44", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ed"),
                    new ZoneSpecification("e45", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ee"),
                    new ZoneSpecification("e46", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ef"),
                    new ZoneSpecification("e47", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900eg"),
                    new ZoneSpecification("e48", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900eh"),
                    new ZoneSpecification("e49", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ei"),
                    new ZoneSpecification("e50", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ej"),
                    new ZoneSpecification("e51", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fa"),
                    new ZoneSpecification("e52", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fb"),
                    new ZoneSpecification("e53", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fc"),
                    new ZoneSpecification("e54", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fd"),
                    new ZoneSpecification("e55", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fe"),
                    new ZoneSpecification("e56", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ff"),
                    new ZoneSpecification("e57", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fg"),
                    new ZoneSpecification("e58", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fh"),
                    new ZoneSpecification("e59", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fi"),
                    new ZoneSpecification("e60", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900fj"),
                    new ZoneSpecification("e61", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ga"),
                    new ZoneSpecification("e62", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gb"),
                    new ZoneSpecification("e63", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gc"),
                    new ZoneSpecification("e64", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gd"),
                    new ZoneSpecification("e65", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ge"),
                    new ZoneSpecification("e66", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gf"),
                    new ZoneSpecification("e67", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gg"),
                    new ZoneSpecification("e68", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gh"),
                    new ZoneSpecification("e69", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gi"),
                    new ZoneSpecification("e70", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900gj"),
                    new ZoneSpecification("e71", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ha"),
                    new ZoneSpecification("e72", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hb"),
                    new ZoneSpecification("e73", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hc"),
                    new ZoneSpecification("e74", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hd"),
                    new ZoneSpecification("e75", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900he"),
                    new ZoneSpecification("e76", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hf"),
                    new ZoneSpecification("e77", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hg"),
                    new ZoneSpecification("e78", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hh"),
                    new ZoneSpecification("e79", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hi"),
                    new ZoneSpecification("e80", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900hj"),
                    new ZoneSpecification("e81", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ia"),
                    new ZoneSpecification("e82", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ib"),
                    new ZoneSpecification("e83", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ic"),
                    new ZoneSpecification("e84", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900id"),
                    new ZoneSpecification("e85", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ie"),
                    new ZoneSpecification("e86", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900if"),
                    new ZoneSpecification("e87", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ig"),
                    new ZoneSpecification("e88", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ih"),
                    new ZoneSpecification("e89", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ii"),
                    new ZoneSpecification("e90", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ij"),
                    new ZoneSpecification("e91", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ja"),
                    new ZoneSpecification("e92", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900jb"),
                    new ZoneSpecification("e93", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900jc"),
                    new ZoneSpecification("e94", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900jd"),
                    new ZoneSpecification("e95", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900je"),
                    new ZoneSpecification("e96", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900jf"),
                    new ZoneSpecification("e97", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900jg"),
                    new ZoneSpecification("e98", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900jh"),
                    new ZoneSpecification("e99", "ab", false, TYPE_NOTICE.EXEMPLAIRE, "900ji"),
                    new ZoneSpecification("915", "abcdef", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("917", "a", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("919", "a", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("920", "abc", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("930", "bcdlezpaijwkfghtv2", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("931", "bcdlezpaijfghtv2", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("932", "bcdlezpaijfghtv2", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("955", "123457", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("956", "12347", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("957", "12347", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("958", "avc", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("959", "4", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("985", "a", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("990", "a", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("991", "abcdlezijfghtv28", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("992", "a2", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("995", "a", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("997", "ba", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("999", "abcdefghijklmnopqrstuvwxyzAB15", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("Axx", "", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E012", "a2", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E316", "a", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E317", "a", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E318", "abcdefhijklnopru28", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E319", "abcdx8", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E325", "ab[cde]fghijnuvxyz", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E702", "abgdDcf34", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E712", "ab[ghdfpec]34", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E722", "acdf34", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("E856", "abcdefhijklmnopqrstuvwxyz29", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("A97", "", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("A98", "", false, TYPE_NOTICE.EXEMPLAIRE),
                    new ZoneSpecification("A99", "", false, TYPE_NOTICE.EXEMPLAIRE),
                    //format données locales
                    new ZoneSpecification("L005", "", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L012", "a2", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L035", "a", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L225", "adfxJ", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L316", "a", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L317", "a", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L318", "abcdefhijklnopru", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L319", "abcdx", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L540", "adfxJ", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L541", "adfxJ", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L542", "adfxJ", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L600", "abcdDfxyz2367", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L601", "abcdefghxyz2367", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L602", "afxyz2367", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L606", "axyz2", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L676", "abefv", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L680", "abef", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L681", "abef", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L686", "abcefv2", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L701", "abcdfgp34", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L702", "abgdDcf34", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L712", "ab[ghdfpec]34", false, TYPE_NOTICE.LOCALE),
                    new ZoneSpecification("L722", "acdf34", false, TYPE_NOTICE.LOCALE),
                    //format autorités
                    new ZoneSpecification("001", "", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("003", "", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("004", "", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("005", "", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("006", "", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("007", "", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("008", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("00A", "0", true, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("00U", "0", true, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("010", "a2Cdz", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("024", "ab3", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("033", "a2Cdz", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("035", "a2Cdz", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("050", "az", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("051", "az", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("052", "az", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("061", "az", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("101", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("102", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("103", "abcd", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("106", "abc", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("120", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("123", "defgqrst", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("126", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("128", "abc", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("150", "ab", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("180", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("200", "9abcdDfxyz5678", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("210", "9abcdefghxyz678", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("215", "axyz6789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("216", "afcxyz6789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("220", "acdfxyz6789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("230", "abhiklmnqrsuwxyz6789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("240", "atxyz6789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("250", "axyz89", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("280", "axyz89", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("300", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("305", "ab", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("310", "ab", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("320", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("330", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("340", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("356", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("400", "9abcDfgxyz05678", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("410", "abcdefghjxyz056789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("415", "axyz056789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("416", "afc056789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("420", "acdfxyz056789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("430", "abhiklmnqrsuwxyz056789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("440", "atxyz056789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("450", "axyz0589", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("480", "axyz0589", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("500", "abcdDfgxyz03567", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("510", "abcdefghxyz03567", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("515", "axyz03567", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("516", "afcxyz03567", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("520", "acdfxyz03567", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("530", "abhiklmnqrsuwxyz03567", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("540", "atxyz03567", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("550", "axyz035", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("580", "axyz035", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("676", "av", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("686", "abc2", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("700", "abcdDfgxyz56789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("710", "abcdefghxyz56789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("715", "axyz589", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("716", "afcxyz56789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("720", "acdfxyz56789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("730", "abhiklmnqrsuwxyz56789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("740", "atxyz56789", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("750", "axyz89", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("780", "axyz89", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("801", "abcgh2", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("810", "ab", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("815", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("820", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("822", "adiluz2", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("825", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("830", "a", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("835", "abd3", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("836", "bcd", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("839", "", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("856", "abcdefghijklmnopqrstuvwxyz", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("898", "ad", false, TYPE_NOTICE.AUTORITE),
                    new ZoneSpecification("899", "a", false, TYPE_NOTICE.AUTORITE)

            ));
}

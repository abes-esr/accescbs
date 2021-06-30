package fr.abes.cbs.notices;

import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import fr.abes.cbs.zones.enumZones.EnumZones;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NoticeTest {
    private static INotice notice;

    @BeforeEach
    void init() throws Exception {
        notice = new Biblio();
        notice.addZone("200", "$a", "test");
        notice.addSousZone("200", "$e", "toto");
    }

    @Test
    void findTagWithValue() throws Exception {
        List<Zone> zoneList = notice.findZoneWithPattern("200", "$e", "toto");

        assertThat(zoneList).isNotNull();
        assertThat(zoneList.size()).isEqualTo(1);
        assertThat(zoneList.get(0).getLabelForOutput()).isEqualTo("200");

        zoneList = notice.findZoneWithPattern("200", "$e", "rrr");

        assertThat(zoneList).isNotNull();
        assertThat(zoneList.size()).isEqualTo(0);

        Zone zone011 = new Zone("011", TYPE_NOTICE.BIBLIOGRAPHIQUE);
        zone011.addSubLabel("b", "jbf");
        zone011.addSubLabel("a", "5824");
        notice.addZone(zone011);

        Zone zone600 = new Zone("600", TYPE_NOTICE.BIBLIOGRAPHIQUE);
        zone600.addSubLabel("b", "jbfgr");
        zone600.addSubLabel("a", "58gdr24");
        zone600.addSubLabel("3", "58gdcsr24");
        zone600.addSubLabel("D", "58gcswdr24");
        zone600.addSubLabel("3", "58gdcbr24");

        notice.addZone(zone600);

        zoneList = notice.findZoneWithPattern("600", "$D", "58gcswdr");
        assertThat(zoneList).isNotNull();
        assertThat(zoneList.size()).isEqualTo(1);
        assertThat(zoneList.get(0).getLabel()).isEqualTo("600");

        zoneList = notice.findZoneWithPattern("600", "$3", "58g");
        assertThat(zoneList).isNotNull();
        assertThat(zoneList.size()).isEqualTo(2);
        assertThat(zoneList.get(0).getLabel()).isEqualTo("600");

        notice.addZone(zone600);
        zoneList = notice.findZoneWithPattern("600", "$3", "58g");
        assertThat(zoneList).isNotNull();
        assertThat(zoneList.size()).isEqualTo(4);
        assertThat(zoneList.get(0).getLabel()).isEqualTo("600");

    }

    @Test
    void replaceSousZoneWithValueTest() throws Exception {
        notice.replaceSousZoneWithValue("200", "$a", "test", "retest2");

        assertThat(notice.findZones("200").get(0).getSubLabelList().get("$a").get(0)).isEqualTo("retest2");
        assertThat(notice.findZones("200").size()).isEqualTo(1);
        assertThat(notice.findZones("200").get(0).getSubLabelList().size()).isEqualTo(2);

        notice.addSousZone("200", "$a", "retest2");
        notice.replaceSousZoneWithValue("200", "$a", "retest2", "yyy");

        assertThat(notice.findZones("200").get(0).getSubLabelList().get("$a").get(0)).isEqualTo("yyy");
        assertThat(notice.findZones("200").get(0).getSubLabelList().get("$a").get(1)).isEqualTo("yyy");
        assertThat(notice.findZones("200").size()).isEqualTo(1);
        assertThat(notice.findZones("200").get(0).getSubLabelList().size()).isEqualTo(3);
    }

    @Test
    void deleteSousZoneTest(){
        notice.deleteSousZone("200", "$a");
        assertThat(notice.findZones("200").size()).isEqualTo(1);

        notice.deleteSousZone("200", "$e");
        assertThat(notice.findZones("200").size()).isEqualTo(0);
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + Constants.STR_1E);
    }

    @Test
    void deleteZoneWithValueTest() throws Exception {
        notice.addZone("200", "$a","Ceci est un test de suppression");
        notice.deleteZoneWithValue("200", "$a", "Ceci");
        assertThat(notice.findZoneWithPattern("200", "$a", "Ceci").size()).isEqualTo(0);
        assertThat(notice.findZones("200").size()).isEqualTo(1);
    }

    @Test
    void deleteZoneTest() throws Exception {
        notice.addZone("200", "$a", "kjfhe");

        notice.deleteZone("200");
        assertThat(notice.findZones("200").size()).isEqualTo(0);
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + Constants.STR_1E);
    }

    @Test
    void deleteZoneWithIndexTest() throws Exception {
        notice.addZone("200", "$a", "test");
        notice.addZone("200", "$a", "test2");
        notice.deleteZone("200", 1);
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F +
                "200 $atest$etoto\r" +
                "200 $atest2\r" +
                Constants.STR_1E);
    }

    @Test
    void addSousZoneTest() throws Exception {
        notice.addSousZone("200", "$d", "oooo");
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + "200 $atest$etoto$doooo\r" + Constants.STR_1E);

        notice.addZone("200", "$a", "oih");
        notice.addSousZone("200", "$d", "ttt", "$a", "oih");
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + "200 $atest$etoto$doooo\r200 $aoih$dttt\r" + Constants.STR_1E);

        notice.addSousZone("200", "$e", "test", 1);
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + "200 $atest$etoto$doooo\r200 $aoih$dttt$etest\r" + Constants.STR_1E);
    }

    @Test
    void addZoneTest() throws Exception {
        notice.addZone("600", "edit");

        assertThat(notice.getListeZones().size()).isEqualTo(2);
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + "200 $atest$etoto\r600 edit\r" + Constants.STR_1E);

        notice.addZone("600", "$d", "uii");
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + "200 $atest$etoto\r600 edit\r600 $duii\r" + Constants.STR_1E);

        notice.addZone("540", "$h", "ty", "##".toCharArray());
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + "200 $atest$etoto\r540 ##$hty\r600 edit\r600 $duii\r" + Constants.STR_1E);

        Zone zone = new Zone("034", TYPE_NOTICE.BIBLIOGRAPHIQUE);
        zone.addSubLabel("$a", "test");
        notice.addZone(zone);
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F + "034 $atest\r200 $atest$etoto\r540 ##$hty\r600 edit\r600 $duii\r" + Constants.STR_1E);
    }

    @DisplayName("Test toString Exemplaire")
    @Test
    void getStringExemplaire() throws Exception {
        notice = new Exemplaire(Constants.STR_1F + "e01 $a31-01-19$bx" + Constants.STR_1E);
        notice.addZone("930", "$j", "x");
        notice.addZone("915", "$a", "test");
        notice.addSousZone("930", "$b", "341720001");
        notice.addZone("E316", "$a", "test");
        assertThat(notice.toString()).isEqualTo(Constants.STR_1F +
                "e01 $a31-01-19$bx\r" +
                "915 $atest\r" +
                "930 $b341720001$jx\r" +
                "E316 $atest\r" +
                Constants.STR_1E);
    }

    @Test
    void getIndicateurTest() throws DocumentException {

        String zone = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<record xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:date=\"http://exslt.org/dates-and-times\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
                "  <datafield tag=\"002\" ind1=\"2\" ind2=\" \">\n" +
                "    <subfield code=\"a\">STAR10007</subfield>\n" +
                "    <subfield code=\"2\">STAR</subfield>\n" +
                "  </datafield>" +
                "</record>";

        Document doc = DocumentHelper.parseText(zone);
        Node node = doc.selectNodes("//record/*").get(0);

        assertThat(Notice.getIndicateurs(node)).isEqualTo(new char[]{'2','#'});
    }
}

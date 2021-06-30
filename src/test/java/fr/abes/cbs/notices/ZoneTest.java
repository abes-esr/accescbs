package fr.abes.cbs.notices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ZoneTest {
    private Zone zone008;
    private Zone zone930;
    private Zone zone411;

    @BeforeEach
    void init() throws Exception {
        zone008 = new Zone("008", TYPE_NOTICE.BIBLIOGRAPHIQUE);
        zone008.addSubLabel("$a", "Aax");
        zone008.addSubLabel("$a", "Test");

        zone930 = new Zone("930", TYPE_NOTICE.EXEMPLAIRE);
        zone930.addSubLabel("$b", "341725201");

        char[] indicateurs = {'#', '#'};
        zone411 = new Zone("411", TYPE_NOTICE.BIBLIOGRAPHIQUE, indicateurs);
    }

    @Test
    void findSousZoneTest() {
        assertThat(zone008.findSubLabel("$a")).isEqualTo("Aax");
    }

    @Test
    void modifierSousZoneTest() {
        zone008.editSubLabel("$a", "Oax");
        assertThat(zone008.findSubLabel("$a")).isEqualTo("Oax");
    }

    @Test
    void getLabelForOutputTest() {
        assertThat(zone008.getLabelForOutput()).isEqualTo("008");
        assertThat(zone930.getLabelForOutput()).isEqualTo("930");
    }

    @Test
    void addSousZoneTest() throws Exception {
        zone930.addSubLabel("$j", "a");
        assertThat(zone930.findSubLabel("$j")).isEqualTo("a");
    }

    @Test
    void deleteSousZoneTest() throws Exception {
        zone930.addSubLabel("$j", "a");
        zone930.addSubLabel("$j", "d");

        zone930.deleteSubLabel("$j");
        assertThat(zone930.findSubLabel("$j")).isNull();
    }

    @Test
    void sublabelSequenceTest() throws Exception {
        //a[tohilbfghi]ecndpsuxy0v9
        zone411.addSubLabel("a", "fleur");
        zone411.addSubLabel("t", "thon");
        zone411.addSubLabel("a", "arbre");
        zone411.addSubLabel("t", "tuna");
        zone411.addSubLabel("o", "olive");
        zone411.addSubLabel("h", "Hector");
        zone411.addSubLabel("i", "ile");
        zone411.addSubLabel("t", "table");
        zone411.addSubLabel("h", "ville");
        zone411.addSubLabel("g", "Montpellier");


        assertThat(zone411.toString()).isEqualTo("411 ##$afleur$aarbre$tthon$ttuna$oolive$hHector$iile$ttable$hville$gMontpellier\r");

    }

}

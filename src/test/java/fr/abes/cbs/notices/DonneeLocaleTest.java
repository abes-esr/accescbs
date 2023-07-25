package fr.abes.cbs.notices;

import fr.abes.cbs.utilitaire.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class DonneeLocaleTest {
    private static DonneeLocale donneeLocale;


    @DisplayName("DonneesLocalesFromString")
    @Test
    void donneesLocalesFromString() throws Exception {
        String donneeStr =
                "L005 09-06-16 14:43:11.000\r" +
                "L035 ##$aBonjour\r";
        donneeLocale = new DonneeLocale(donneeStr);
        assertThat(donneeLocale.getListeZones().size()).isEqualTo(2);
        assertThat(donneeLocale.findZones("L005").get(0).getValeur()).isEqualTo("09-06-16 14:43:11.000");
        assertThat(donneeLocale.findZones("L035").get(0).getSubLabelList().size()).isEqualTo(1);
        assertThat(donneeLocale.toString()).isEqualTo(
                "L005 09-06-16 14:43:11.000\r" +
                "L035 ##$aBonjour\r");

        donneeStr =
                "L005 09-06-16 14:43:11.000\r" +
                "L035 ##$abonjour1\r" +
                "L035 1#$abonjour2\r";
        donneeLocale = new DonneeLocale(donneeStr);
        assertThat(donneeLocale.getListeZones().size()).isEqualTo(3);
        assertThat(donneeLocale.findZones("L035").get(0).getIndicateurs()).isEqualTo(new char[]{'#', '#'});
        assertThat(donneeLocale.findZones("L035").get(1).getIndicateurs()).isEqualTo(new char[]{'1', '#'});
        assertThat(donneeLocale.findZones("L035").get(0).getSubLabelList().size()).isEqualTo(1);
        assertThat(donneeLocale.findZones("L035").get(0).getSubLabelList().get("$a").get(0)).isEqualTo("bonjour1");
    }

}

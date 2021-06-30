package fr.abes.cbs.notices;

import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.utilitaire.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoriteTest {
    @DisplayName("Test création autorité à partir d'une chaine")
    @Test
    void autoriteFromStringBasique() throws ZoneException {
        String autoriteStr = Constants.STR_1F +
                "003 232801398\r" +
                "004 341720001:16-10-20\r" +
                "005 341720001:16-10-20 10:02:01.000\r" +
                "006 341720001:16-10-20\r" +
                "008 $aTp5\r" +
                "00A $00\r" +
                "00U $0utf8\r" +
                "010 ##$a0000000119343714$2ISNI$CVIAF$d20130731\r" +
                "033 ##$ahttp://catalogue.bnf.fr/ark:/12148/cb139479097$2BNF$d20201016\r" +
                "035 ##$aFRBNF139479092\r" +
                "102 ##$aGB\r" +
                "103 ##$a19050515$b19760530\r" +
                "106 ##$a0$b1$c0\r" +
                "120 ##$ab\r" +
                "200 ##$90y$aRignold$bHugo$f1905-1976\r" +
                "300 ##$aChef d'orchestre et violoniste\r" +
                "340 ##$aLieu de naissance : Kingston on Thames. Lieu de décès : Hampstead.\r" +
                "510 0#$a@Royal Liverpool Philharmonic Orchestra\r" +
                "510 0#$a@City of Birmingham symphony orchestra\r" +
                "801 ##$aFR$bFR-751131015$c20110609\r" +
                "810 ##$aL'amour sorcier [Enregistrement sonore]... Hugo Rignold, dir. Mode MD6008\r" +
                "810 ##$aGrove\r" +
                "899 ##$aNotice BnF dérivée via IdRef, le 16/10/2020\r" + Constants.STR_1E;
        Autorite autorite = new Autorite(autoriteStr);
        assertThat(autorite.getListeZones().size()).isEqualTo(23);
        assertThat(autorite.findZones("200").get(0).getSubLabelList().size()).isEqualTo(4);
        autorite.getListeZones().values();
        assertThat(autorite.toString()).isEqualTo(Constants.STR_1F +
                "003 232801398\r" +
                "004 341720001:16-10-20\r" +
                "005 341720001:16-10-20 10:02:01.000\r" +
                "006 341720001:16-10-20\r" +
                "008 $aTp5\r" +
                Constants.STR_1B + "P00A $00\r" +
                Constants.STR_1B + "D" + Constants.STR_1B + "P00U $0utf8\r" +
                Constants.STR_1B + "D010 ##$a0000000119343714$2ISNI$CVIAF$d20130731\r" +
                "033 ##$ahttp://catalogue.bnf.fr/ark:/12148/cb139479097$2BNF$d20201016\r" +
                "035 ##$aFRBNF139479092\r" +
                "102 ##$aGB\r" +
                "103 ##$a19050515$b19760530\r" +
                "106 ##$a0$b1$c0\r" +
                "120 ##$ab\r" +
                "200 ##$90y$aRignold$bHugo$f1905-1976\r" +
                "300 ##$aChef d'orchestre et violoniste\r" +
                "340 ##$aLieu de naissance : Kingston on Thames. Lieu de décès : Hampstead.\r" +
                "510 0#$a@Royal Liverpool Philharmonic Orchestra\r" +
                "510 0#$a@City of Birmingham symphony orchestra\r" +
                "801 ##$aFR$bFR-751131015$c20110609\r" +
                "810 ##$aL'amour sorcier [Enregistrement sonore]... Hugo Rignold, dir. Mode MD6008\r" +
                "810 ##$aGrove\r" +
                "899 ##$aNotice BnF dérivée via IdRef, le 16/10/2020\r" + Constants.STR_1E);
    }

    @Test
    void autoriteFromXmlTest() throws ZoneException {
        Scanner scanner = new Scanner(BiblioTest.class.getResourceAsStream("/noticeAutorite.xml"), StandardCharsets.UTF_8).useDelimiter("\\r");
        StringBuilder notice = new StringBuilder();
        while (scanner.hasNext()) {
            notice.append(scanner.next());
        }
        Autorite autorite = new Autorite(notice.toString(), FORMATS.XML);
        assertThat(autorite.toString()).isEqualTo(Constants.STR_1F +
                "003 079695434\r" +
                "004 674822116:23-07-04\r" +
                "005 674822116:04-04-20 05:23:41.000\r" +
                "006 674822116:23-07-04\r" +
                "008 $aTp5\r" +
                Constants.STR_1B + "P00A $00\r" +
                Constants.STR_1B + "D" + Constants.STR_1B + "P00U $0utf8\r" +
                Constants.STR_1B + "D010 ##$a0000000004872894$2ISNI$CVIAF$d20200401\r" +
                "035 ##$ahttp://viaf.org/viaf/22507970$2VIAF$CVIAF$d20200401\r" +
                "101 ##$ager\r" +
                "102 ##$aDE\r" +
                "106 ##$a0$b1$c0\r" +
                "120 ##$ab\r" +
                "200 #1$aSchröder$bHugo\r" +
                "340 ##$aInspecteur de l'enseignement primaire à Halle (Allemagne) en 1928\r" +
                "400 #1$aSchroeder$bHugo\r" +
                "810 ##$aSoziologie der Volksschulklasse / Hugo Schröder\r" +
                Constants.STR_1E);
    }
}

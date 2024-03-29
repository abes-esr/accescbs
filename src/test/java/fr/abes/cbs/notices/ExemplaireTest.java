package fr.abes.cbs.notices;

import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.utilitaire.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExemplaireTest {
    @DisplayName("test création Exemplaire à partir d'une chaine")
    @Test
    void exemplaireFromString() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "930 ##$b341720001\r" +
                "E702 #1$3082280568$4390 (Ancien possesseur)\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 61882824X\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.getListeZones().size()).isEqualTo(7);
        assertThat(exemp.findZones("915").get(0).getSubLabelList().size()).isEqualTo(1);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "930 ##$b341720001\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 61882824X\r" +
                "E702 #1$3082280568$4390 (Ancien possesseur)\r");
        exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "930 ##$b341720001$2test\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 61882824X\r";
        exemp = new Exemplaire(exempStr);
        assertThat(exemp.getListeZones().size()).isEqualTo(6);
        assertThat(exemp.findZones("930").get(0).getSubLabelList().size()).isEqualTo(2);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "930 ##$b341720001$2test\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 61882824X\r");

        exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "917 $atest2\r" +
                "930 ##$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        exemp = new Exemplaire(exempStr);
        assertThat(exemp.getListeZones().size()).isEqualTo(9);
        assertThat(exemp.findZones("930").get(0).getSubLabelList().size()).isEqualTo(2);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "917 $atest2\r" +
                "930 ##$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");

        exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "917 $atest2\r" +
                "930 ##$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "991 $ctest\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        exemp = new Exemplaire(exempStr);
        assertThat(exemp.getListeZones().size()).isEqualTo(10);
        assertThat(exemp.findZones("930").get(0).getSubLabelList().size()).isEqualTo(2);
        assertThat(exemp.findZones("991").size()).isEqualTo(2);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "917 $atest2\r" +
                "930 ##$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "991 $ctest\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");

        exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "930 #1$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r";
        exemp = new Exemplaire(exempStr);
        assertThat(exemp.getListeZones().size()).isEqualTo(9);
        assertThat(exemp.findZones("930").get(0).getSubLabelList().size()).isEqualTo(2);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "930 #1$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r");

        exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "930 1#$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r";
        exemp = new Exemplaire(exempStr);
        assertThat(exemp.getListeZones().size()).isEqualTo(9);
        assertThat(exemp.findZones("930").get(0).getSubLabelList().size()).isEqualTo(2);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "930 1#$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r");
        exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$d7$e56$a2000$k2001$0 $d5$e56$a2001$0 $d7$e100$a2002$k2004$0 $d10$e26$a2005$k2008$0 $d5$e56$a2009$0 $d7$e100$a2010$k2014\r" +
                "957 41$b2$cjan$a2001$k2002$0 $b6$csep$a2000$k2002$0 $b6$csep$a2001$k2003$0 $b6$csep$a2004$k2006$0 $b6$csep$a2007$k2008$0 $b6$csep$a2009$k2011\r" +
                "958 ##$ammm$v4\r" +
                "959 ##$d7$e4$f4$b5$cjan$a2000\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r";
        exemp = new Exemplaire(exempStr);
        assertThat(exemp.getListeZones().size()).isEqualTo(13);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$d7$e56$a2000$k2001$0 $d5$e56$a2001$0 $d7$e100$a2002$k2004$0 $d10$e26$a2005$k2008$0 $d5$e56$a2009$0 $d7$e100$a2010$k2014\r" +
                "957 41$b2$cjan$a2001$k2002$0 $b6$csep$a2000$k2002$0 $b6$csep$a2001$k2003$0 $b6$csep$a2004$k2006$0 $b6$csep$a2007$k2008$0 $b6$csep$a2009$k2011\r" +
                "958 ##$ammm$v4\r" +
                "959 ##$d7$e4$f4$b5$cjan$a2000\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r");
        exempStr = "e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest$atest2\r" +
                "920 $atest$btest$ctest\r" +
                "930 1#$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r";
        exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "915 $a1\r" +
                "917 $atest$atest2\r" +
                "920 $atest$btest$ctest\r" +
                "930 1#$b341720001$jx\r" +
                "991 $atest$btest2\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r" +
                "E856 $atest$btest\r");
    }

    @Test
    void exemplaireFromStringWithEtatColl() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000-\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000-\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");
    }

    @Test
    void exemplaireFromStringWithEtatColl2() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$k2003\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$k2003\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");
    }
    @Test
    void exemplaireFromStringWithEtatColl3() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$b2$c3$k2003$l3$m8$0 $a2003-\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$b2$c3$k2003$l3$m8$0 $a2003-\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");
    }

    @Test
    void exemplaireFromStringWithEtatColl4() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$b2$c3$5 $a1998$b1$0 $a2003-\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$b2$c3$5 $a1998$b1$0 $a2003-\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");
    }

    @Test
    void exemplaireFromStringWithEtatColl5() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$b2$c3$5 $a1998$b1$0 $a2003-\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        exemp.addSousZone("955", "$7", "Lac.");
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$a2000$b2$c3$5 $a1998$b1$0 $a2003-$7Lac.\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");
    }

    @Test
    void exemplaireFromStringWithEtatColl6() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$d274$a1998$o846$k2010$0 $e891$a2012-$4Casiers Sciences de l'information$7Lac.\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "955 41$d274$a1998$o846$k2010$0 $e891$a2012-$4Casiers Sciences de l'information$7Lac.\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");
    }

    @Test
    void exemplaireFromStringWithEtatColl7() throws Exception {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "959 ##$d4$e6$b05$cnov$a2000$0 $d8$e4-8,5,6$f25$b07$c12$a2003$h8$iFascicule$gCommentaire 1 séquence$0 $d9$e6-14$a2004$h66$iLexique$gCommentaire 2 séquence$4Commentaire sur l'ensemble des lacunes\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 1#$b341720001$jx\r" +
                "959 ##$d4$e6$b05$cnov$a2000$0 $d8$e4-8,5,6$f25$b07$c12$a2003$h8$iFascicule$gCommentaire 1 séquence$0 $d9$e6-14$a2004$h66$iLexique$gCommentaire 2 séquence$4Commentaire sur l'ensemble des lacunes\r" +
                "A97 19-11-18 17:01:25.000\r" +
                "A98 341720001:17-09-18\r" +
                "A99 618828249\r");
    }

    @DisplayName("test zone E712")
    @Test
    void testE712() throws ZoneException {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 ##$b341720001$jx\r" +
                "E712 11$a@test$btest$gtest$htest$dtest$ptest$etest$ctest$gtest$htest$dtest$ptest$etest$ctest$4920\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 ##$b341720001$jx\r" +
                "E712 11$a@test$btest$gtest$htest$dtest$ptest$etest$ctest$gtest$htest$dtest$ptest$etest$ctest$4920\r");
    }

    @DisplayName("test zone E712")
    @Test
    void testE712cas2() throws ZoneException {
        String exempStr = "e01 $a17-09-18$bx\r" +
                "930 ##$b341720001$jx\r" +
                "E712 11$a@test$gtest$htest$dtest$ptest$etest$ctest$btest$gtest$htest$dtest$ptest$etest$ctest$4920\r";
        Exemplaire exemp = new Exemplaire(exempStr);
        assertThat(exemp.toString()).isEqualTo("e01 $a17-09-18$bx\r" +
                "930 ##$b341720001$jx\r" +
                "E712 11$a@test$btest$gtest$htest$dtest$ptest$etest$ctest$gtest$htest$dtest$ptest$etest$ctest$4920\r");
    }

    @DisplayName("test récupération numéro d'exemplaire")
    @Test
    void getNumEx() throws Exception {
        Exemplaire exemp = new Exemplaire("e01 $a26-11-19$bx");
        assertThat(exemp.getNumEx()).isEqualTo("01");
    }

    @DisplayName("test ajout zone E325")
    @Test
    void testE325() throws Exception {
        String exempStr =
                "e01 $a27-11-20$bx\r" +
                "930 ##$b341720001\r" +
                "997 $balma$a2211189990000572\r" +
                "A97 23-11-21 16:35:13.000\r" +
                "A98 341720001:27-11-20\r" +
                "A99 625586905\r" +
                "E325 ##$aFac-similé par la maison d'édition Du pareil au même$uttp://www.dupareilaumeme$v20210916\r" +
                "E325 ##$bFac-similé$cParis$dEdition du pareil au même$e2012$f1 vol. (338 p.)$gCollection L'Identique$h1$iA noter que c'est complet$j3ly04$nRien à dire sur la repro$uhttp://www.dupareilaumeme.fr$v20210404$x2419-6592\r";
        Exemplaire exemp = new Exemplaire(exempStr);

        assertThat(exemp.toString()).isEqualTo(
                "e01 $a27-11-20$bx\r" +
                "930 ##$b341720001\r" +
                "997 $balma$a2211189990000572\r" +
                "A97 23-11-21 16:35:13.000\r" +
                "A98 341720001:27-11-20\r" +
                "A99 625586905\r" +
                "E325 ##$aFac-similé par la maison d'édition Du pareil au même$uttp://www.dupareilaumeme$v20210916\r" +
                "E325 ##$bFac-similé$cParis$dEdition du pareil au même$e2012$f1 vol. (338 p.)$gCollection L'Identique$h1$iA noter que c'est complet$j3ly04$nRien à dire sur la repro$uhttp://www.dupareilaumeme.fr$v20210404$x2419-6592\r");



    }


}
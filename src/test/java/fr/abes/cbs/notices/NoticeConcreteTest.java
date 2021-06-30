package fr.abes.cbs.notices;

import fr.abes.cbs.utilitaire.Constants;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class NoticeConcreteTest {
    @Test
    void NoticeConcreteTest() {
        Scanner scanner = new Scanner(BiblioTest.class.getResourceAsStream("/noticeXML.xml"), "UTF-8").useDelimiter("\\r");
        StringBuilder notice = new StringBuilder();
        while (scanner.hasNext()) {
            notice.append(scanner.next());
        }
        NoticeConcrete noticeConcrete = new NoticeConcrete(notice.toString());
        assertThat(noticeConcrete.getNoticeBiblio().toString()).isEqualTo(Constants.STR_1F +
                "002 ##$aSTAR10007$2STAR\r" +
                "008 ##$aOax3\r" +
                "029 ##$aFR$b2010ECAP0020\r" +
                "033 ##$ahttp://www.theses.fr/2010ECAP0020\r" +
                "035 ##$aSTAR10007\r" +
                "100 0#$a2010\r" +
                "101 0#$afre$dfre$deng\r" +
                "102 ##$aFR\r" +
                "104 ##$ak$by$cy$dba$e0$ffre\r" +
                "105 ##$bm$ba$c0$d0$fy$gy\r" +
                "135 ##$ad$br\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cc\r" +
                "183 ##$P01$aceb\r" +
                "200 1#$a@Contribution à l'élaboration d'un outil de simulation de procédés de transformation physico-chimique de matières premières issues des agro ressources$eapplication aux procédés de transformation de biopolymères par extrusion réactive$fMarie-Amélie De ville d'avray$gsous la direction de Arsène Isambert\r" +
                "219 #1$d2010\r" +
                "230 ##$aDonnées textuelles\r" +
                "304 ##$aTitre provenant de l'écran-titre\r" +
                "310 ##$aThèse confidentielle jusqu'au 05 juillet 2015\r" +
                "314 ##$aEcole(s) Doctorale(s) : Sciences pour l'ingénieur\r" +
                "314 ##$aPartenaire(s) de recherche : Laboratoire Génie des Procédés et Matériaux (Laboratoire), Laboratoire de Genie des Procédés et Matériaux (Laboratoire)\r" +
                "314 ##$aAutre(s) contribution(s) : Gilles Trystram (Président du jury) ; Arsène Isambert, Hélène Ducatel, Stéphane Brochot (Membre(s) du jury) ; Yann Le gorrec, Xuân Meyer (Rapporteur(s))\r" +
                "328 #0$bThèse de doctorat$cGénie des procédés$eEcole centrale Paris$d2010\r" +
                "606 ##$3034228942$3027253139$2rameau\r" +
                "606 ##$3029677440$3027253139$2rameau\r" + Constants.STR_1E);
    }

    @Test
    void NoticeConcreteTest2() {
        Scanner scanner = new Scanner(BiblioTest.class.getResourceAsStream("/noticeConcreteXML.xml"), "UTF-8").useDelimiter("\\r");
        StringBuilder notice = new StringBuilder();
        while (scanner.hasNext()) {
            notice.append(scanner.next());
        }
        NoticeConcrete noticeConcrete = new NoticeConcrete(notice.toString());

        assertThat(noticeConcrete.getNoticeLocale().toString()).isEqualTo(Constants.STR_1F +
                "L035 ##$a123456789\r" + Constants.STR_1E);

        assertThat(noticeConcrete.getExemplaires().get(0).toString()).isEqualTo(Constants.STR_1F +
                //"e01 $bx\r" +
                "930 ##$b341725201$jg\r" +
                Constants.STR_1E
        );

        assertThat(noticeConcrete.getExemplaires().get(1).toString()).isEqualTo(Constants.STR_1F +
                //"e02 $bx\r" +
                "930 ##$b341725202$jg\r" +
                Constants.STR_1E
        );

    }

    @Test
    void getNumExTest() throws Exception {
        Scanner scanner = new Scanner(BiblioTest.class.getResourceAsStream("/noticeConcreteXML.xml"), "UTF-8").useDelimiter("\\r");
        StringBuilder notice = new StringBuilder();
        while (scanner.hasNext()) {
            notice.append(scanner.next());
        }
        NoticeConcrete noticeConcrete = new NoticeConcrete(notice.toString());
        //ajout de 2 zones exx pour vérifier que le numex du dernier exemplaire est 02
        noticeConcrete.getExemplaires().get(0).addZone("e01", "$b", "x");
        noticeConcrete.getExemplaires().get(1).addZone("e02", "$b", "x");
        assertThat(noticeConcrete.getNumEx()).isEqualTo("02");
        //suppression de tous les exemplaires pour vérifier que le numEx est null
        noticeConcrete.getExemplaires().remove(1);
        noticeConcrete.getExemplaires().remove(0);
        assertThat(noticeConcrete.getNumEx()).isNull();
    }
}

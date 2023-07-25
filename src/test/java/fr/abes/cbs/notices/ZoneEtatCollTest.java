package fr.abes.cbs.notices;

import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.sequences.SequenceEtatColl;
import fr.abes.cbs.notices.sequences.SequencePrimaire;
import fr.abes.cbs.utilitaire.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ZoneEtatCollTest {
    private char[] indicateurs = {'#', '#'};
    ZoneEtatColl zone955;

    @BeforeEach
    void init() throws ZoneException {
        zone955 = new ZoneEtatColl("955", indicateurs);
    }

    @Test
    void addSequencePrimaire()  {
        zone955.addSequence(new SequenceEtatColl(new SequencePrimaire("$a2000$k2000", "955")));
        assertThat(zone955.toString()).isEqualTo("955 ##$a2000$k2000\r");
    }

    @Test
    void addMultipleSequencePrimaire() {
        zone955.addSequence(new SequenceEtatColl(new SequencePrimaire("$a2000$k2000", "955")));
        zone955.addSequence(new SequenceEtatColl(new SequencePrimaire("$a2010", "955")));
        assertThat(zone955.toString()).isEqualTo("955 ##$a2000$k2000$0 $a2010\r");
    }

    @Test
    void addSousZone() throws ZoneException {
        zone955.addSequence(new SequenceEtatColl(new SequencePrimaire("$a2000$k2000", "955")));
        zone955.addSequence(new SequenceEtatColl(new SequencePrimaire("$a2020", "955")));
        zone955.addSousZone("$k", "2030", 1);
        assertThat(zone955.toString()).isEqualTo("955 ##$a2000$k2000$0 $a2020$k2030\r");
    }

    @DisplayName("Test ajout zone état de collection")
    @Test
    void addZone() throws ZoneException {
        Exemplaire exemp = new Exemplaire("930 ##$aPAR 3933$jg");
        exemp.addZoneEtatCollection("955", "$a", "2000", new char[]{'4', '1'});
        assertThat(exemp.toString()).isEqualTo("930 ##$aPAR 3933$jg\r" +
                "955 41$a2000\r");
    }
}

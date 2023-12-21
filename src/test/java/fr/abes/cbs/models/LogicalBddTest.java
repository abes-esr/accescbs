package fr.abes.cbs.models;

import fr.abes.cbs.utilitaire.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicalBddTest {
    @Test
    void constructeurWithString() {
        String vkz = Constants.STR_1B +  "LNR1" + Constants.STR_1B + "E" + Constants.STR_1B + "LID1.1" + Constants.STR_1B + "E" + Constants.STR_1B + "LNACatalogue" + Constants.STR_1B + "E" + Constants.STR_1E;
        LogicalBdd bdd = new LogicalBdd(vkz);
        Assertions.assertEquals("1", bdd.getLineNumber());
        Assertions.assertEquals("1.1", bdd.getBddNumber());
        Assertions.assertEquals("Catalogue", bdd.getBddLabel());
    }

}
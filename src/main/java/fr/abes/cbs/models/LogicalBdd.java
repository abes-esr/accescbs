package fr.abes.cbs.models;

import fr.abes.cbs.utilitaire.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LogicalBdd {
    private String lineNumber;
    private String bddNumber;
    private String bddLabel;

    public LogicalBdd(String vkz) {
        String[] params = vkz.split(Constants.STR_1B + "E" + Constants.STR_1B);
        lineNumber = params[0].substring(4);
        bddNumber = params[1].substring(3);
        bddLabel = params[2].substring(3, params[2].length()-3);
    }
}

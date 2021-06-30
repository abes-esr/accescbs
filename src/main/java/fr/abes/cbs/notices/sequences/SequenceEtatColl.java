package fr.abes.cbs.notices.sequences;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SequenceEtatColl {
    public static final String SEPARATEUR = "$0 ";

    private SequencePrimaire sequencePrimaire;
    private SequenceParallele sequenceParallele;

    public SequenceEtatColl(SequencePrimaire sequencePrimaire) {
        this.sequencePrimaire = sequencePrimaire;
        this.sequenceParallele = null;
    }

    public SequenceEtatColl(SequencePrimaire sequencePrimaire, SequenceParallele sequenceParallele) {
        this.sequencePrimaire = sequencePrimaire;
        this.sequenceParallele = sequenceParallele;
    }

    @Override
    public String toString() {
        StringBuilder sequenceStr = new StringBuilder();
        sequenceStr.append(sequencePrimaire.toString());
        if (sequenceParallele != null) {
            sequenceStr.append(SequenceParallele.SEPARATEUR).append(sequenceParallele.toString());
        }
        return sequenceStr.toString();
    }
}

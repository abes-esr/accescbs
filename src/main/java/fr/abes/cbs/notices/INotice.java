package fr.abes.cbs.notices;

import com.google.common.collect.ListMultimap;
import fr.abes.cbs.exception.ZoneException;
import jdk.jshell.spi.ExecutionControl;

import java.util.List;


public interface INotice {
    ListMultimap<String, Zone> getListeZones();

    void addZone(String zone, String sousZone, String valeur) throws ZoneException;

    void addZone(String zone, String sousZone, String valeur, char[] indicateurs) throws ZoneException;

    void addZone(String zone, String valeur) throws ZoneException;

    void addZone(Zone zone);

    void addZoneEtatCollection(String zone, String sousZone, String valeur, char[] indicateurs) throws ExecutionControl.NotImplementedException, ZoneException;

    List<Zone> findZones(String label);

    Zone findZone(String label, Integer index);

    List<Zone> findZoneWithPattern(String zone, String sousZone, String pattern);

    void replaceSousZoneWithValue(String zone, String sousZone, String valeurInit, String newValeur);

    void addSousZone(String zone, String sousZone, String valeur) throws Exception;

    void deleteZone(String zone, Integer index);

    void deleteZoneWithValue(String zone, String sousZone, String valeur);

    void deleteZone(String zone);

    void deleteSousZone(String zone, String sousZone);

    void replaceSousZone(String zone, String sousZone, String valeur);

    String getNumEx() throws ExecutionControl.NotImplementedException;

    void addSousZone(String zone, String sousZone, String valeur, Integer index) throws Exception;

    void addSousZone(String zone, String sousZone, String valeur, String sousZoneBefore, String valeurBefore) throws ZoneException;

    TYPE_NOTICE getType();

}

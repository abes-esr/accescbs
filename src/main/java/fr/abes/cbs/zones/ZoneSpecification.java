package fr.abes.cbs.zones;

import fr.abes.cbs.notices.TYPE_NOTICE;

public class ZoneSpecification {

    public String name;

    public String sublabelSpecification;

    public boolean isProtected;

    public TYPE_NOTICE typeNoticeZone;

    public String order;

    public ZoneSpecification(String name, String sublabelSpecification, boolean isProtected, TYPE_NOTICE type){
        this.name = name;
        this.sublabelSpecification = sublabelSpecification;
        this.isProtected = isProtected;
        this.typeNoticeZone = type;
        this.order = name;
    }

    public ZoneSpecification(String name, String sublabelSpecification, boolean isProtected, TYPE_NOTICE type, String order){
        this.name = name;
        this.sublabelSpecification = sublabelSpecification;
        this.isProtected = isProtected;
        this.typeNoticeZone = type;
        this.order = order;
    }

    public ZoneSpecification(String name, String sublabelSpecification){
        this.name = name;
        this.sublabelSpecification = sublabelSpecification;
        this.isProtected = false;
        this.typeNoticeZone = TYPE_NOTICE.BIBLIOGRAPHIQUE;
        this.order = name;
    }
}

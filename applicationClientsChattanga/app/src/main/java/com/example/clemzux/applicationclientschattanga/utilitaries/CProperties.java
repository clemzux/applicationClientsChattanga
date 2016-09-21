package com.example.clemzux.applicationclientschattanga.utilitaries;


import chattanga.classes.CDate;
import chattanga.classes.CReservation;

/**
 * Created by clemzux on 24/08/16.
 */
public class CProperties {

    public static Boolean hasReserved = false;
    public static String nameCurrentReservation = null;
    public static int idCurrentReservation;

    // plat du jour actuel
    public static CDate CURRENT_DAYDISH = null;
    public static CReservation CURRENT_RESERVATION = new CReservation();

    // ip chattanga
//    public static final String SERVER_URL = "http://176.157.85.69:9999/";
    // ip ?
//    public static final String SERVER_URL = "http://192.168.0.195:9999/";
    // ip maison capi
//    public static final String SERVER_URL = "http://192.168.1.91:9999/";
    // ip maison mémé
    public static final String SERVER_URL = "http://192.168.1.17:9999/";

    // request types
    public final static String GET      = "GET";
    public final static String POST     = "POST";
    public final static String PUT      = "PUT";
    public final static String DELETE   = "DEL";
    public final static String GET_ALL  = "GET_ALL";
    public final static String GET_BY   = "GET_BY";

    // other
    public final static int APP_VERSION = 1;
    public final static int HOUR_RESET_DAYDISH = 15;
    public final static String DAYDISH_NOT_LOADED = "Si le plat du jour n'est pas encore chargé reéssayez plus tard !";
    public final static String RESERVATION_ACCEPTED = "Votre réservation a bien été prise en compte !";
    public final static String DB_NAME = "informations";
    public final static String WEBSITE_URL = "http://www.la-chattanga.fr";

    // path for requests
    public final static String DATES = "dates";
    public final static String RESERVATIONS = "reservations";
    public final static String APP_VERSION_PATH = "appversions";
    public final static String DATE_BY_DATE = "dates/date/";
}

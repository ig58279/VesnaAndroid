package ru.cproject.vesnaandroid;

/**
 * Created by Bitizen on 31.10.16.
 */

public class ServerApi {

    private static final String SERVER_ADDRESS = "http://retail.megapartnership.ru/malls/api/test/";


    public static String getImgUrl(String id, boolean min) {
        return SERVER_ADDRESS + "imgs/?id=" + id + "&min=" + String.valueOf(min);
    }

    public static String getTileUrl(int z, int x, int y, long v) {
        return String.format(SERVER_ADDRESS + "map/%d/%d/%d.png?v=%d",z,x,y,v);
    }

    /**
     * Метод авторизации
     */
    public static final String AUTH = SERVER_ADDRESS + "auth";

    //TODO Убрать тестовую отправку SMS
    /**
     * Метод отправки SMS
     */
    public static final String SMS = SERVER_ADDRESS + "sms";

    /**
     * Метод получения информации о торговом центре
     */
    public static final String MALL = SERVER_ADDRESS + "mall";

    /**
     * Метод получения списка акций
     */
    public static final String GET_STOCKS = SERVER_ADDRESS + "stocks";

    /**
     * Метод получения акции
     */
    public static final String GET_STOCK = SERVER_ADDRESS + "stock";

    /**
     * Метод получения магазинов
     */
    public static final String GET_SHOPS = SERVER_ADDRESS + "list?mod=";

    /**
     * Метод получения магазина
     */
    public static final String GET_SHOP = SERVER_ADDRESS + "shop";

    /**
     * Метод получения фильмов
     */
    public static final String GET_FILMS = SERVER_ADDRESS + "films";

    /**
     * Метод получения фильма
     */
    public static final String GET_FILM = SERVER_ADDRESS + "film";

    /**
     * Метод получния событий
     */
    public static final String GET_EVENTS = SERVER_ADDRESS + "events";

    /**
     * Метод получния событий
     */
    public static final String GET_USER = SERVER_ADDRESS + "user";

    /**
     * Метод получения события
     */
    public static final String GET_EVENT = SERVER_ADDRESS + "event";

    /**
     * Метод для поиска
     */
    public static final String SEARCH = SERVER_ADDRESS + "list";

    /**
     * Метод для работы с категориям
     */
    public static final String CATS = SERVER_ADDRESS + "cats";

    /**
     * Метод получения графа и инфо о карте
     */
    public static final String ROUTES = SERVER_ADDRESS + "routes";

    /**
     * Метод добавления магазина/акции в избранное
     */
    public static final String LIKE = SERVER_ADDRESS + "like";

    /**
     * Метод удаления магазина/акции из избранного
     */
    public static final String DISLIKE = SERVER_ADDRESS + "dislike";
}

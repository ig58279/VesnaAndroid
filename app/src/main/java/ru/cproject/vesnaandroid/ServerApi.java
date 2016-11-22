package ru.cproject.vesnaandroid;

/**
 * Created by Bitizen on 31.10.16.
 */

public class ServerApi {

    private static final String SERVER_ADDRESS = "http://retail.megapartnership.ru/malls/api/test/";


    public static String getImgUrl(String id, boolean min) {
        return SERVER_ADDRESS + "imgs/?id=" + id + "&min=" + String.valueOf(min);
    }

    /**
     * Метод авторизации
     * Параметры(все обязательные)
     * type - тип авторизации (vk,fb,phone)
     * token - ключ от соц. сетей (только для vk и fb)
     * phone - номер телефона (только для phone)
     * pass - номер телефона (только для phone)
     */
    public static final String AUTH = SERVER_ADDRESS + "auth";

    /**
     * Метод получения информации о торговом центре
     * Параметры
     * token - токен
     */
    public static final String MALL = SERVER_ADDRESS + "mall";

    /**
     * Метод получения списка акций
     * Параметры
     * cid - идентификатор категории
     * token - токен
     * sort - сортировка
     *      (special - сначала эксклюзивные
     *       a - по алфавиту (а-я)
     *       z - по алфавиту (я-а))
     */
    public static final String GET_STOCKS = SERVER_ADDRESS + "stocks";

    /**
     * Метод получения акции
     * Параметры
     * id - идентификатор акции - Обязательный
     * token - токен
     */
    public static final String GET_STOCK = SERVER_ADDRESS + "stock";

    /**
     * Метод получения магазинов
     * Параметры
     * cid - идентификатор категории
     * token - токен
     */
    public static final String GET_SHOPS = SERVER_ADDRESS + "shops";

    /**
     * Метод получения магазина
     * Параметр
     * id - идентификатор мазагина
     * token - токен
     */
    public static final String GET_SHOP = SERVER_ADDRESS + "shop";

    /**
     * Метод получения фильмов
     * Параметр
     * sort - фильтрация фильмов по дате - обязательный
     * token - токен
     */
    public static final String GET_FILMS = SERVER_ADDRESS + "films";

    /**
     * Метод получения фильма
     * Параметр
     * id - идентификатор фильма - Обязательный
     * token - токен
     */
    public static final String GET_FILM = SERVER_ADDRESS + "film";

    /**
     * Метод получния событий
     */
    public static final String GET_EVENTS = SERVER_ADDRESS + "events";

    /**
     * Метод получения события
     */
    public static final String GET_EVENT = SERVER_ADDRESS + "event";

    /**
     * Метод для поиска
     */
    public static final String SEARCH = SERVER_ADDRESS + "list";
}

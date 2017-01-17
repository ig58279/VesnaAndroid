package ru.cproject.vesnaandroid;

/**
 * Created by Bitizen on 04.11.16.
 */

public class Settings {
    public static final String COMMON = "common";
    public class Common {
        public static final String stockLikeWithChangedState = "positionOfRecyclerElement";   //костыль, если акцию лайкнули/дизлайкнули, то тут будет позиция элемента
    }

    public static final String MALL_INFO = "mallInfo";
    public class MallInfo {
        public static final String MALL = "mall";
        public static final String FUNCTIONAL = "functional";
        public static final String SHOWS = "shows";
    }
    public static final String REGISTRATION_INFO = "registrationInfo";
    public class RegistrationInfo {
        public static final String PHONE = "phone";
        public static final String NAME = "name";
        public static final String SURNAME = "surname";
        public static final String ID = "id";
        public static final String ROLE = "role";
        public static final String PBS = "pbs";
        public static final String PSS = "pss";
        public static final String PHOTO = "photo";

    }

    public static final String MAP_INFO = "mapInfo";
    public class MapInfo {
        public static final String VERSION = "version";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String STYLE_INFO = "styleInfo";
    }

}

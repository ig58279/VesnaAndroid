package ru.cproject.vesnaandroid.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Event;
import ru.cproject.vesnaandroid.obj.Film;
import ru.cproject.vesnaandroid.obj.Search;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.Show;
import ru.cproject.vesnaandroid.obj.Stock;
import ru.cproject.vesnaandroid.obj.mall.Address;
import ru.cproject.vesnaandroid.obj.mall.Function;
import ru.cproject.vesnaandroid.obj.mall.Link;
import ru.cproject.vesnaandroid.obj.mall.MallInfo;
import ru.cproject.vesnaandroid.obj.mall.ShopMode;
import ru.cproject.vesnaandroid.obj.responses.FilmsResponse;
import ru.cproject.vesnaandroid.obj.responses.MallResponse;

/**
 * Created by Bitizen on 31.10.16.
 */

public class ResponseParser {

    public static List<Stock> parseStocks(String json) {
        List<Stock> stocks = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(json).getAsJsonObject();

        String list = "list";
        if (response.has(list) && !response.get(list).isJsonNull()) {
            JsonArray stocksArray = response.get(list).getAsJsonArray();

            for (JsonElement e : stocksArray)
                stocks.add(parseStock(e.toString()));
        }
        return stocks;
    }

    public static Stock parseStock(String json) {
        Stock stock = new Stock();
        JsonParser parser = new JsonParser();
        JsonObject response = (JsonObject) parser.parse(json);

        String id = "id";
        if (response.has(id) && !response.get(id).isJsonNull())
            stock.setId(response.get(id).getAsInt());

        String img = "img";
        if (response.has(img) && !response.get(img).isJsonNull())
            stock.setImage(response.get(img).getAsString());
        else {
            img = "logo";
            if (response.has(img) && !response.get(img).isJsonNull())
                stock.setImage(response.get(img).getAsString());
        }

        String name = "name";
        if (response.has(name) && !response.get(name).isJsonNull())
            stock.setTitle(response.get(name).getAsString());

        String desc = "desc";
        if (response.has(desc) && !response.get(desc).isJsonNull())
            stock.setContent(response.get(desc).getAsString());

        String photos = "photos";
        if (response.has(photos) && !response.get(photos).isJsonNull()) {
            List<String> photosList = new ArrayList<>();
            JsonArray photosArray = response.get(photos).getAsJsonArray();
            for (JsonElement e : photosArray) photosList.add(e.getAsString());
            stock.setPhotos(photosList);
        }

        String mode = "mode";
        if (response.has(mode) && !response.get(mode).isJsonNull())
            stock.setDate(response.get(mode).getAsString());

        String shopJson = "shop";
        if (response.has(shopJson) && !response.get(shopJson).isJsonNull()) {
            Shop shop = parseShop(response.get(shopJson).toString());
            stock.setShop(shop);
        }

        return stock;
    }

    public static Shop parseShop(String json) {
        Shop shop = new Shop();
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(json).getAsJsonObject();

        String idJson = "id";
        if (response.has(idJson) && !response.get(idJson).isJsonNull())
            shop.setId(response.get(idJson).getAsInt());

        String logoJson = "img";
        if (response.has(logoJson) && !response.get(logoJson).isJsonNull())
            shop.setLogo(response.get(logoJson).getAsString());
        else {
            logoJson = "logo";
            if (response.has(logoJson) && !response.get(logoJson).isJsonNull())
                shop.setLogo(response.get(logoJson).getAsString());
        }

        String titleJson = "name";
        if (response.has(titleJson) && !response.get(titleJson).isJsonNull())
            shop.setName(response.get(titleJson).getAsString());

        String descJson = "desc";
        if (response.has(descJson) && !response.get(descJson).isJsonNull())
            shop.setContent(response.get(descJson).getAsString());

        String photosJson = "photos";
        if (response.has(photosJson) && !response.get(photosJson).isJsonNull()) {
            List<String> photos = new ArrayList<>();
            JsonArray photosArray = response.get(photosJson).getAsJsonArray();
            for (JsonElement e : photosArray) photos.add(e.getAsString());
            shop.setPhotos(photos);
        }

        // TODO: 22.11.16 категории

        List<Shop.Complement> complements = new ArrayList<>();
        String phoneJson = "phone";
        if (response.has(phoneJson) && !response.get(phoneJson).isJsonNull())
            complements.add(new Shop.Complement("phone", response.get(phoneJson).getAsString()));

        String siteJson = "site";
        if (response.has(siteJson) && !response.get(siteJson).isJsonNull())
            complements.add(new Shop.Complement("site", response.get(siteJson).getAsString()));

        String modeJson = "mode";
        if (response.has(modeJson) && !response.get(modeJson).isJsonNull())
            complements.add(new Shop.Complement("mode", response.get(modeJson).getAsString()));

        shop.setComplements(complements);

        String likeJson = "like";
        if (response.has(likeJson) && !response.get(likeJson).isJsonNull())
            shop.setLike(response.get(likeJson).getAsBoolean());


        return shop;
    }

    public static List<Shop> parseShops(String json) {
        List<Shop> response = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray shopList = parser.parse(json).getAsJsonArray();

        for (JsonElement e : shopList)
            response.add(parseShop(e.toString()));

        return response;
    }

    public static FilmsResponse parseFilms(String json) {
        FilmsResponse response = new FilmsResponse();
        JsonParser parser = new JsonParser();
        JsonObject responseJson = parser.parse(json).getAsJsonObject();

        String ITEMS = "items";
        if (responseJson.has(ITEMS) && !responseJson.get(ITEMS).isJsonNull()) {
            List<Film> films = new ArrayList<>();
            JsonArray items = responseJson.get(ITEMS).getAsJsonArray();

            for (int i = 0; i < items.size(); i++)
                films.add(parseFilm(items.get(i).getAsJsonObject().toString()));

            response.setItems(films);
        }

        String CINEMA = "cinema";
        if (responseJson.has(CINEMA) && !responseJson.get(CINEMA).isJsonNull())
            response.setCinema(parseShop(responseJson.get(CINEMA).getAsJsonObject().toString()));

        return response;
    }

    public static Film parseFilm(String json) {
        Film film = new Film();
        JsonParser parser = new JsonParser();
        JsonObject filmJson = parser.parse(json).getAsJsonObject();

        String ID = "id";
        if (filmJson.has(ID) && !filmJson.get(ID).isJsonNull())
            film.setId(filmJson.get(ID).getAsInt());

        String NAME = "name";
        if (filmJson.has(NAME) && !filmJson.get(NAME).isJsonNull())
            film.setName(filmJson.get(NAME).getAsString());

        String CONTENT = "content";
        if (filmJson.has(CONTENT) && !filmJson.get(CONTENT).isJsonNull())
            film.setContent(filmJson.get(CONTENT).getAsString());

        String AGE = "age";
        if (filmJson.has(AGE) && !filmJson.get(AGE).isJsonNull())
            film.setAge(filmJson.get(AGE).getAsInt());

        String CAST = "cast";
        if (filmJson.has(CAST) && !filmJson.get(CAST).isJsonNull()) {
            List<String> cast = new ArrayList<>();
            JsonArray castJson = filmJson.get(CAST).getAsJsonArray();

            for (int i = 0; i < castJson.size(); i++)
                cast.add(castJson.get(i).getAsString());

            film.setCast(cast);
        }

        String DIRECTOR = "director";
        if (filmJson.has(DIRECTOR) && !filmJson.get(DIRECTOR).isJsonNull()) {
            List<String> director = new ArrayList<>();
            JsonArray directorJson = filmJson.get(DIRECTOR).getAsJsonArray();

            for (int i = 0; i < directorJson.size(); i++)
                director.add(directorJson.get(i).getAsString());

            film.setDirector(director);
        }

        String PRODUCER = "producer";
        if (filmJson.has(PRODUCER) && !filmJson.get(PRODUCER).isJsonNull()) {
            List<String> producer = new ArrayList<>();
            JsonArray producerJson = filmJson.get(PRODUCER).getAsJsonArray();

            for (int i = 0; i < producerJson.size(); i++)
                producer.add(producerJson.get(i).getAsString());

            film.setProducer(producer);
        }

        String GENRES = "genres";
        if (filmJson.has(GENRES) && !filmJson.get(GENRES).isJsonNull()) {
            List<String> genres = new ArrayList<>();
            JsonArray genresJson = filmJson.get(GENRES).getAsJsonArray();

            for (int i = 0; i < genresJson.size(); i++)
                genres.add(genresJson.get(i).getAsString());

            film.setGenre(genres);
        }

        String COUNTRY = "country";
        if (filmJson.has(COUNTRY) && !filmJson.get(COUNTRY).isJsonNull()) {
            List<String> country = new ArrayList<>();
            JsonArray countryJson = filmJson.get(COUNTRY).getAsJsonArray();

            for (int i = 0; i < countryJson.size(); i++)
                country.add(countryJson.get(i).getAsString());

            film.setCountry(country);
        }

        String SEANSE = "seanse";
        if (filmJson.has(SEANSE) && !filmJson.get(SEANSE).isJsonNull()) {
            List<Long> seanse = new ArrayList<>();
            JsonArray seanseJson = filmJson.get(SEANSE).getAsJsonArray();

            for (int i = 0; i < seanseJson.size(); i++)
                seanse.add(seanseJson.get(i).getAsLong());

            film.setSeanse(seanse);
        }

        String RATING = "rating";
        if (filmJson.has(RATING) && !filmJson.get(RATING).isJsonNull())
            film.setRating(filmJson.get(RATING).getAsFloat());

        String POSTER = "poster";
        if (filmJson.has(POSTER) && !filmJson.get(POSTER).isJsonNull())
            film.setPoster(filmJson.get(POSTER).getAsString());

        String PHOTOS = "photos";
        if (filmJson.has(PHOTOS) && !filmJson.get(PHOTOS).isJsonNull()) {
            JsonArray photosJson = filmJson.get(PHOTOS).getAsJsonArray();
            List<Film.Photo> photos = new ArrayList<>();
            for (int i = 0; i < photosJson.size(); i++) {
                Film.Photo photo = new Film.Photo();
                JsonObject photoJson = photosJson.get(i).getAsJsonObject();

                String SMALL = "small";
                if (photoJson.has(SMALL) && !photoJson.get(SMALL).isJsonNull())
                    photo.setSmall(photoJson.get(SMALL).getAsString());

                String ORIGINAL = "small";
                if (photoJson.has(ORIGINAL) && !photoJson.get(ORIGINAL).isJsonNull())
                    photo.setOriginal(photoJson.get(ORIGINAL).getAsString());

                photos.add(photo);
            }
            film.setPhotos(photos);
        }


        String TRAILER = "trailer";
        if (filmJson.has(TRAILER) && !filmJson.get(TRAILER).isJsonNull())
            film.setTrailer(filmJson.get(TRAILER).getAsString());

        String DURATION = "duration";
        if (filmJson.has(DURATION) && !filmJson.get(DURATION).isJsonNull())
            film.setDuration(filmJson.get(DURATION).getAsInt());

        return film;
    }

    public static MallResponse parseMall(String json) {
        MallResponse response = new MallResponse();
        JsonParser parser = new JsonParser();
        JsonObject responseJson = parser.parse(json).getAsJsonObject();

        MallInfo mallInfo = new MallInfo();

        String desc = "desc";
        if (responseJson.has(desc) && !responseJson.get(desc).isJsonNull())
            mallInfo.setContent(responseJson.get(desc).getAsString());

        Address address = new Address();

        String addressJson = "address";
        if (responseJson.has(addressJson) && !responseJson.get(addressJson).isJsonNull())
            address.setTitle(responseJson.get(addressJson).getAsString());

        String placeJson = "place";
        if (responseJson.has(placeJson) && !responseJson.get(placeJson).isJsonNull()){
            JsonObject coords = responseJson.get(placeJson).getAsJsonObject();
            address.setLat(coords.get("lat").getAsDouble());
            address.setLng(coords.get("lng").getAsDouble());
        }
        mallInfo.setAddress(address);

        String photosJson = "photos";
        if (responseJson.has(photosJson) && !responseJson.get(photosJson).isJsonNull()) {
            List<String> photos = new ArrayList<>();
            JsonArray photosArray = responseJson.get(photosJson).getAsJsonArray();
            for (JsonElement photo: photosArray)
                photos.add(photo.getAsString());
            mallInfo.setPhotos(photos);
        }

        String timeJson = "time";
        if (responseJson.has(timeJson) && !responseJson.get(timeJson).isJsonNull()) {
            List<ShopMode> shopsMode = new ArrayList<>();
            JsonArray modesJson = responseJson.get(timeJson).getAsJsonArray();
            for (JsonElement mode : modesJson) {
                JsonObject modeObj = mode.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = modeObj.entrySet();
                for (Map.Entry<String, JsonElement> e : entries) {
                    ShopMode shopMode = new ShopMode();
                    shopMode.setName(e.getKey());
                    shopMode.setMode(e.getValue().getAsString());
                    shopsMode.add(shopMode);
                }
            }
            mallInfo.setShopsModes(shopsMode);
        }

        String contactsJson = "contacts";
        if (responseJson.has(contactsJson) && !responseJson.get(contactsJson).isJsonNull()) {
            List<Link> links = new ArrayList<>();
            JsonArray contactsArray = responseJson.get(contactsJson).getAsJsonArray();
            for (JsonElement j :  contactsArray) {
                JsonObject link = j.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = link.entrySet();
                for (Map.Entry<String, JsonElement> e : entries) {
                    switch (e.getKey()) {
                        case "phone":
                            mallInfo.setPhone(e.getValue().getAsString());
                            break;
                        default:
                            Link l = new Link();
                            l.setType(e.getKey());
                            l.setParametr(e.getValue().getAsString());
                            links.add(l);
                    }
                }
            }
            mallInfo.setLinks(links);
        }

        response.setMallInfo(mallInfo);

        String modulesJson = "modules";
        if (responseJson.has(modulesJson) && !responseJson.get(modulesJson).isJsonNull()) {
            List<Function> functional = new ArrayList<>();
            JsonArray modulesArray = responseJson.get(modulesJson).getAsJsonArray();
            for (JsonElement e : modulesArray) {
                JsonObject moduleObj = e.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = moduleObj.entrySet();
                for (Map.Entry<String, JsonElement> entry : entries) {
                    Function function = new Function();
                    function.setType(entry.getKey());
                    function.setHome(entry.getValue().getAsBoolean());
                    functional.add(function);
                }
            }
            response.setFunctional(functional);
        }

        String showsJson = "shows";
        if (responseJson.has(showsJson) && !responseJson.get(showsJson).isJsonNull()) {
            List<Show> shows = new ArrayList<>();
            JsonArray showsArray = responseJson.get(showsJson).getAsJsonArray();
            for (JsonElement element : showsArray) {
                JsonObject showJson = element.getAsJsonObject();
                Show show = new Show();
                show.setId(showJson.get("id").getAsInt());
                show.setType(showJson.get("type").getAsString());
                show.setImage(showJson.get("img").getAsString());
                shows.add(show);
            }
            response.setShows(shows);
        }

        return response;
    }

    public static List<Event> parseEvents(String json){
        List<Event> events = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(json).getAsJsonObject();

        String LIST = "list";
        if (response.has(LIST) && !response.get(LIST).isJsonNull()) {
            JsonArray list = response.get(LIST).getAsJsonArray();

            for (JsonElement element: list) {
                events.add(parseEvent(element.toString()));
            }

        }

        return events;
    }

    public static Event parseEvent(String json) {
        Event event = new Event();
        JsonParser parser = new JsonParser();
        JsonObject eventJson = parser.parse(json).getAsJsonObject();

        String ID = "id";
        if (eventJson.has(ID) && !eventJson.get(ID).isJsonNull())
            event.setId(eventJson.get(ID).getAsInt());

        String IMG = "img";
        if (eventJson.has(IMG) && !eventJson.get(IMG).isJsonNull())
            event.setImage(eventJson.get(IMG).getAsString());

        String NAME = "name";
        if (eventJson.has(NAME) && !eventJson.get(NAME).isJsonNull())
            event.setTitle(eventJson.get(NAME).getAsString());

        String DESC = "desc";
        if (eventJson.has(DESC) && !eventJson.get(DESC).isJsonNull())
            event.setDescription(eventJson.get(DESC).getAsString());

        String photos = "photos";
        if (eventJson.has(photos) && !eventJson.get(photos).isJsonNull()) {
            List<String> photosList = new ArrayList<>();
            JsonArray photosArray = eventJson.get(photos).getAsJsonArray();
            for (JsonElement e : photosArray) photosList.add(e.getAsString());
            event.setPhotos(photosList);
        }

        return event;
    }

    public static List<Search> parseSearch(String json) {
        List<Search> searches = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject searchesJson = parser.parse(json).getAsJsonObject();

        String list = "list";
        if (searchesJson.has(list) && !searchesJson.get(list).isJsonNull()) {
            JsonArray searchResults = searchesJson.get(list).getAsJsonArray();

            for (int i = 0; i < searchResults.size(); i++) {
                Search search = new Search();
                JsonObject searchJson = searchResults.get(i).getAsJsonObject();

                String ID = "id";
                if (searchJson.has(ID) && !searchJson.get(ID).isJsonNull())
                    search.setId(searchJson.get(ID).getAsInt());

                String TYPE = "type";
                if (searchJson.has(TYPE) && !searchJson.get(TYPE).isJsonNull())
                    search.setType(searchJson.get(TYPE).getAsString());

                String NAME = "name";
                if (searchJson.has(NAME) && !searchJson.get(NAME).isJsonNull())
                    search.setName(searchJson.get(NAME).getAsString());

                String IMAGE = "img";
                if (searchJson.has(IMAGE) && !searchJson.get(IMAGE).isJsonNull())
                    search.setImageURL(searchJson.get(IMAGE).getAsString());

                searches.add(search);
            }
        }
        return searches;
    }

}

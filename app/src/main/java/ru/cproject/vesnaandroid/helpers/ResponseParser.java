package ru.cproject.vesnaandroid.helpers;

import android.graphics.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.cproject.vesnaandroid.obj.Category;
import ru.cproject.vesnaandroid.obj.Coupon;
import ru.cproject.vesnaandroid.obj.Event;
import ru.cproject.vesnaandroid.obj.Film;
import ru.cproject.vesnaandroid.obj.Search;
import ru.cproject.vesnaandroid.obj.Shop;
import ru.cproject.vesnaandroid.obj.Show;
import ru.cproject.vesnaandroid.obj.Stock;
import ru.cproject.vesnaandroid.obj.User;
import ru.cproject.vesnaandroid.obj.mall.Address;
import ru.cproject.vesnaandroid.obj.mall.Function;
import ru.cproject.vesnaandroid.obj.mall.Link;
import ru.cproject.vesnaandroid.obj.mall.MallInfo;
import ru.cproject.vesnaandroid.obj.mall.ShopMode;
import ru.cproject.vesnaandroid.obj.map.Edge;
import ru.cproject.vesnaandroid.obj.map.StyleInfo;
import ru.cproject.vesnaandroid.obj.map.Vertex;
import ru.cproject.vesnaandroid.obj.responses.FilmsResponse;
import ru.cproject.vesnaandroid.obj.responses.MallResponse;
import ru.cproject.vesnaandroid.obj.responses.MapResponse;

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

        String like = "like";
        if (response.has(like) && !response.get(like).isJsonNull())
            stock.setLike(response.get(like).getAsBoolean());

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

        String catsJson = "cats";
        if (response.has(catsJson) && !response.get(catsJson).isJsonNull()) {
            List<Category> categories = new ArrayList<>();
            JsonArray categoriesArray = response.get(catsJson).getAsJsonArray();
            for (JsonElement j : categoriesArray) {
                JsonObject categoryObj = j.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = categoryObj.entrySet();
                for (Map.Entry<String, JsonElement> e : entries) {
                    Category category = new Category();
                    category.setType(e.getKey());
                    category.setCategories(e.getValue().getAsString());
                    categories.add(category);
                }
            }
            stock.setCategories(categories);
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

    public static Coupon parseCoupone(String json) {
        Coupon coupon = new Coupon();
        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(json).getAsJsonObject();

        String name = "name";
        if (response.has(name) && !response.get(name).isJsonNull())
            coupon.setName(response.get(name).getAsString());

        String image = "img";
        if (response.has(image) && !response.get(image).isJsonNull())
            coupon.setImage(response.get(image).getAsString());

        return coupon;
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

        String catsJson = "cats";
        if (response.has(catsJson) && !response.get(catsJson).isJsonNull()) {
            List<Category> categories = new ArrayList<>();
            JsonArray categoriesArray = response.get(catsJson).getAsJsonArray();
            for (JsonElement j : categoriesArray) {
                JsonObject categoryObj = j.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = categoryObj.entrySet();
                for (Map.Entry<String, JsonElement> e : entries) {
                    Category category = new Category();
                    category.setType(e.getKey());
                    category.setCategories(e.getValue().getAsString());
                    categories.add(category);
                }
            }
            shop.setCategories(categories);
        }

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

        String itemsJson = "items";
        if (response.has(itemsJson) && !response.get(itemsJson).isJsonNull()) {
            List<Stock> stockList = new ArrayList<>();
            JsonArray stockArray = response.get(itemsJson).getAsJsonArray();
            for (JsonElement e : stockArray)
                stockList.add(parseStock(e.toString()));
            shop.setStocks(stockList);
        }

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

        String films = "films";
        if (responseJson.has(films) && !responseJson.get(films).isJsonNull()) {
            JsonArray filmsArray = responseJson.get(films).getAsJsonArray();
            List<Film> filmList = new ArrayList<>();
            for (JsonElement e : filmsArray)
                filmList.add(parseFilm(e.toString()));
            response.setItems(filmList);
        }

        response.setCinema(parseShop(json));

        String stocksJson = "stocks";
        if (responseJson.has(stocksJson) && !responseJson.get(stocksJson).isJsonNull()) {
            List<Stock> stocksList = new ArrayList<>();
            JsonArray stocksArray = responseJson.get(stocksJson).getAsJsonArray();
            for (JsonElement e : stocksArray)
                stocksList.add(parseStock(e.toString()));
            Shop shop = response.getCinema();
            shop.setStocks(stocksList);
            response.setCinema(shop);
        }

        return response;
    }

    public static Film parseFilm(String json) {
        Film film = new Film();
        JsonParser parser = new JsonParser();
        JsonObject filmJson = parser.parse(json).getAsJsonObject();

        String ID = "id";
        if (filmJson.has(ID) && !filmJson.get(ID).isJsonNull())
            film.setId(filmJson.get(ID).getAsInt());

        String img = "img";
        if (filmJson.has(img) && !filmJson.get(img).isJsonNull())
            film.setPoster(filmJson.get(img).getAsString());
        else {
            img = "logo";
            if (filmJson.has(img) && !filmJson.get(img).isJsonNull())
                film.setPoster(filmJson.get(img).getAsString());
        }

        String content = "desc";
        if (filmJson.has(content) && !filmJson.get(content).isJsonNull()) {
            film.setContent(filmJson.get(content).getAsString());
        }

        String photosJson = "photos";
        if (filmJson.has(photosJson) && !filmJson.get(photosJson).isJsonNull()) {
            List<String> photos = new ArrayList<>();
            JsonArray photosArray = filmJson.get(photosJson).getAsJsonArray();
            for (JsonElement e : photosArray) photos.add(e.getAsString());
            film.setPhotos(photos);
        }

        String NAME = "name";
        if (filmJson.has(NAME) && !filmJson.get(NAME).isJsonNull())
            film.setName(filmJson.get(NAME).getAsString());

        String TRAILER = "trailer";
        if (filmJson.has(TRAILER) && !filmJson.get(TRAILER).isJsonNull())
            film.setTrailer(filmJson.get(TRAILER).getAsString());

        String attrsJson = "attrs";
        if (filmJson.has(attrsJson) && !filmJson.get(attrsJson).isJsonNull()) {
            JsonObject attributes = filmJson.get(attrsJson).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = attributes.entrySet();
            for (Map.Entry<String, JsonElement> e : entries) {
                switch (e.getKey()) {
                    case "age":
                        film.setAge(e.getValue().getAsString());
                        break;
                    case "duration":
                        film.setDuration(e.getValue().getAsString());
                        break;
                    case "genre":
                        List<String> genresList = new ArrayList<>();
                        JsonArray genresArray = attributes.get("genre").getAsJsonArray();
                        for (JsonElement j : genresArray) genresList.add(j.getAsString());
                        film.setGenre(genresList);
                        break;
                    case "country":
                        List<String> countriesList = new ArrayList<>();
                        JsonArray countriesArray = attributes.get("country").getAsJsonArray();
                        for (JsonElement j : countriesArray) countriesList.add(j.getAsString());
                        film.setCountry(countriesList);
                        break;
                    case "seance":
                        List<String> seancesList = new ArrayList<>();
                        JsonArray seancesArray = attributes.get("seance").getAsJsonArray();
                        for (JsonElement j : seancesArray) seancesList.add(j.getAsString());
                        film.setSeanse(seancesList);
                        break;
                    case "rating":
                        film.setRating(e.getValue().getAsString());
                        break;
                    case "actor":
                        List<String> acrtorsList = new ArrayList<>();
                        JsonArray actorsArray = attributes.get("actor").getAsJsonArray();
                        for (JsonElement j : actorsArray) acrtorsList.add(j.getAsString());
                        film.setCast(acrtorsList);
                        break;
                    case "director":
                        List<String> directorsList = new ArrayList<>();
                        JsonArray directorsArray = attributes.get("director").getAsJsonArray();
                        for (JsonElement j : directorsArray) directorsList.add(j.getAsString());
                        film.setDirector(directorsList);
                        break;
                    case "producer":
                        List<String> producersList = new ArrayList<>();
                        JsonArray producersArray = attributes.get("producer").getAsJsonArray();
                        for (JsonElement j : producersArray) producersList.add(j.getAsString());
                        film.setProducer(producersList);
                        break;
                }
            }
        }
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

        String mode = "mode";
        if (responseJson.has(mode) && !responseJson.get(mode).isJsonNull())
            mallInfo.setMode(responseJson.get(mode).getAsString());

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
                if(showJson.has("id") && !showJson.get("id").isJsonNull())
                show.setId(showJson.get("id").getAsInt());
                if(showJson.has("type") && !showJson.get("type").isJsonNull())
                show.setType(showJson.get("type").getAsString());
                if(showJson.has("img") && !showJson.get("img").isJsonNull())
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

    public static MapResponse parseMapResponse(String json) {
        MapResponse response = new MapResponse();
        JsonParser parser = new JsonParser();
        JsonObject routeInfo = parser.parse(json).getAsJsonObject();

        String vertex = "vertex";
        if (routeInfo.has(vertex) && !routeInfo.get(vertex).isJsonNull()) {
            List<Vertex> vertexList = new ArrayList<>();
            JsonArray vertexsArray = routeInfo.get(vertex).getAsJsonArray();
            for (int i = 0; i < vertexsArray.size(); i++) {
                JsonObject vertexJson = vertexsArray.get(i).getAsJsonObject();
                Vertex vertexObj = new Vertex();

                vertexObj.setId(i);

                String place = "place";
                vertexObj.setX(vertexJson.get(place).getAsJsonArray().get(0).getAsFloat());
                vertexObj.setY(vertexJson.get(place).getAsJsonArray().get(1).getAsFloat());

                String type = "type";
                if (vertexJson.has(type) && !vertexJson.get(type).isJsonNull())
                    vertexObj.setType(vertexJson.get(type).getAsString());

                String route = "route";
                if (vertexJson.has(route) && !vertexJson.get(route).isJsonNull())
                    vertexObj.setRouteId(vertexJson.get(route).getAsInt());

                String shop = "shop";
                if (vertexJson.has(shop) && !vertexJson.get(shop).isJsonNull())
                    vertexObj.setShopId(vertexJson.get(shop).getAsInt());

                String park = "park";
                if (vertexJson.has(park) && !vertexJson.get(park).isJsonNull())
                    vertexObj.setPark(vertexJson.get(park).getAsString());

                String name = "name";
                if (vertexJson.has(name) && !vertexJson.get(name).isJsonNull())
                    vertexObj.setShopName(vertexJson.get(name).getAsString());

                String cats = "cats";
                if (vertexJson.has(cats) && !vertexJson.get(cats).isJsonNull()) {
                    JsonArray catsJson = vertexJson.get(cats).getAsJsonArray();
                    String catsBuf = catsJson.get(0).getAsString();
                    for (int j = 1; j < catsJson.size(); j++)
                        catsBuf += "," + catsJson.get(j);
                    vertexObj.setCats(catsBuf);
                }

                vertexList.add(vertexObj);
            }
            response.setVerstexs(vertexList);
        }

        String edge = "edges";
        if (routeInfo.has(edge) && !routeInfo.get(edge).isJsonNull()) {
            List<Edge> edgeList = new ArrayList<>();
            JsonArray edgeArray = routeInfo.get(edge).getAsJsonArray();
            for (JsonElement e :  edgeArray) {
                JsonObject edgeJson = e.getAsJsonObject();
                Edge edgeObj = new Edge();

                String from = "from";
                if (edgeJson.has(from) && !edgeJson.get(from).isJsonNull())
                    edgeObj.setFromId(edgeJson.get(from).getAsInt());

                String to = "to";
                if (edgeJson.has(to) && !edgeJson.get(to).isJsonNull())
                    edgeObj.setToId(edgeJson.get(to).getAsInt());

                String bi = "bi";
                if (edgeJson.has(bi) && !edgeJson.get(bi).isJsonNull())
                    edgeObj.setBi(edgeJson.get(bi).getAsBoolean());
                else
                    edgeObj.setBi(false);

                String cost = "cost";
                if (edgeJson.has(cost) && !edgeJson.get(cost).isJsonNull())
                    edgeObj.setCost(edgeJson.get(cost).getAsInt());

                edgeList.add(edgeObj);
            }

            response.setEdges(edgeList);
        }

        String style = "style";
        if (routeInfo.has(style) && !routeInfo.get(style).isJsonNull()) {
            StyleInfo styleInfo = new StyleInfo();
            JsonObject styleJson = routeInfo.get(style).getAsJsonObject();

            String line = "line";
            if (styleJson.has(line) && !styleJson.get(line).isJsonNull()) {
                JsonArray lineInfo = styleJson.get(line).getAsJsonArray();

                String rgba = lineInfo.get(0).getAsString();
                String color = "#" + rgba.substring(6) + rgba.substring(0,6);
                styleInfo.setLineColor(Color.parseColor(color));

                styleInfo.setLineWidth(lineInfo.get(1).getAsInt());
            }

            String route = "route";
            if (styleJson.has(route) && !styleJson.get(route).isJsonNull()) {
                styleInfo.setStartIcon(styleJson.get(route).getAsJsonArray().get(0).getAsString());
                styleInfo.setEndIcon(styleJson.get(route).getAsJsonArray().get(1).getAsString());
            }

            String lift = "lift";
            if (styleJson.has(lift) && !styleJson.get(lift).isJsonNull())
                styleInfo.setLiftIcon(styleJson.get(lift).getAsString());

            String escalator = "escalator";
            if (styleJson.has(escalator) && !styleJson.get(escalator).isJsonNull()) {
                styleInfo.setEscalatorIcon(styleJson.get(escalator).getAsString());
            }

            String park = "park";
            if (styleJson.has(park) && !styleJson.get(park).isJsonNull()) {
                styleInfo.setParkIcon(styleJson.get(park).getAsString());
            }

            String pipe = "pipe";
            if (styleJson.has(pipe) && !styleJson.get(pipe).isJsonNull()) {
                JsonArray pipeInfo = styleJson.get(pipe).getAsJsonArray();

                String rgba = pipeInfo.get(0).getAsString();
                String color = "#" + rgba.substring(6) + rgba.substring(0,6);
                styleInfo.setPipeColor(Color.parseColor(color));

                styleInfo.setPipeWidth(pipeInfo.get(1).getAsInt());
            }
            response.setStyleInfo(styleInfo);
        }

        String width = "width";
        if (routeInfo.has(width) && !routeInfo.get(width).isJsonNull())
            response.setWidth(routeInfo.get(width).getAsInt());

        String height = "height";
        if (routeInfo.has(height) && !routeInfo.get(height).isJsonNull())
            response.setHeight(routeInfo.get(height).getAsInt());

        String version = "version";
        if (routeInfo.has(version) && !routeInfo.get(version).isJsonNull())
            response.setVersion(routeInfo.get(version).getAsLong());

        return response;
    }

    public static User parseUser(String usr){
        User user = new User();
        JsonObject mainJsonObject = new JsonParser().parse(usr).getAsJsonObject();
        String id = "id";
        if (mainJsonObject.has(id) && !mainJsonObject.get(id).isJsonNull()) {
            user.setId(mainJsonObject.get(id).getAsString());
        }
        String role = "role";
        if (mainJsonObject.has(role) && !mainJsonObject.get(role).isJsonNull()) {
            user.setRole(mainJsonObject.get(role).getAsString());
        }
        String pbs = "pbs";
        if (mainJsonObject.has(pbs) && !mainJsonObject.get(pbs).isJsonNull()) {
            user.setPbs(mainJsonObject.get(pbs).getAsString());
        }
        String pss = "pss";
        if (mainJsonObject.has(pss) && !mainJsonObject.get(pss).isJsonNull()) {
            user.setPss(mainJsonObject.get(pss).getAsString());
        }
        String fname = "fname";
        if (mainJsonObject.has(fname) && !mainJsonObject.get(fname).isJsonNull()) {
            user.setFname(mainJsonObject.get(fname).getAsString());
        }
        String lname = "lname";
        if (mainJsonObject.has(lname) && !mainJsonObject.get(lname).isJsonNull()) {
            user.setLname(mainJsonObject.get(lname).getAsString());
        }
        String photo = "photo";
        if (mainJsonObject.has(photo) && !mainJsonObject.get(photo).isJsonNull()) {
            user.setPhoto(mainJsonObject.get(photo).getAsString());
        }
        String shops = "shops";
        if (mainJsonObject.has(shops) && !mainJsonObject.get(shops).isJsonNull()) {
            ArrayList arrayList = new ArrayList();
            JsonArray shopList = mainJsonObject.get("shops").getAsJsonArray();
            for (JsonElement e : shopList)
                arrayList.add(parseShop(e.toString()));
            user.setShops(arrayList);
        }
        String stocks = "stocks";
        if (mainJsonObject.has(stocks) && !mainJsonObject.get(stocks).isJsonNull()) {
            ArrayList arrayList = new ArrayList();
            JsonArray shopList = mainJsonObject.get("stocks").getAsJsonArray();
            for (JsonElement e : shopList)
                arrayList.add(parseStock(e.toString()));
            user.setStocks(arrayList);
        }
        String coupons = "coupons";
        if (mainJsonObject.has(coupons) && !mainJsonObject.get(coupons).isJsonNull()) {
            ArrayList arrayList = new ArrayList();
          /*  JsonArray shopList = mainJsonObject.get("stocks").getAsJsonArray();
            for (JsonElement e : shopList)
                arrayList.add(parseStocks(e.toString()));*/
            JsonArray couponList = mainJsonObject.get("coupons").getAsJsonArray();
            for (JsonElement e : couponList)
                arrayList.add(parseCoupone(e.toString()));
            user.setCoupons(arrayList);
        }

        return user;
    }

}

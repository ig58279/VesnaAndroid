package ru.cproject.vesnaandroid.obj.mall;

import java.util.List;

/**
 * Created by Bitizen on 04.11.16.
 */

public class MallInfo {

    private String mode;
    private String phone;
    private List<ShopMode> shopsModes;
    private String content;
    private Address address;
    private List<String> photos;
    private List<Link> links;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<ShopMode> getShopsModes() {
        return shopsModes;
    }

    public void setShopsModes(List<ShopMode> shopsModes) {
        this.shopsModes = shopsModes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

}

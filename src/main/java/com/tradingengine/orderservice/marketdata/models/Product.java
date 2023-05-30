package com.tradingengine.orderservice.marketdata.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.UUID;



public abstract class Product {

    private  String product;
    private  int quantity;
    private  double price;
    private  String side;
    private  String orderType;
    private  String exchangeUrl;


    public Product() {

    }
    public Product(String product, int quantity, double price,
                   String side, String orderType, String exchangeUrl) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.exchangeUrl = exchangeUrl;
        this.orderType = orderType;
    }

    public String getProduct() {
        return product;
    }

    public Product setProduct(String product) {
         this.product = product;
         return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getSide() {
        return side;
    }

    public Product setSide(String side) {
        this.side = side;
        return this;
    }

    public String getExchangeUrl() {
        return exchangeUrl;
    }

    public Product setExchangeUrl(String exchangeUrl) {
        this.exchangeUrl = exchangeUrl;
        return this;
    }

    public String getOrderType() {
        return orderType;
    }

    public Product setOrderType(String orderType) {
        this.orderType = orderType;
        return this;
    }
}
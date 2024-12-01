package com.pricehub;

import lombok.Data;

@Data
public class Item {
    private String itemName;    
    private double price;
    private String image;
    private String platform;
}
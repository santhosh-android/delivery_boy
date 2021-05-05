package com.app.dosmiosdelivery.Model;

public class OrderDetailsModelList {

    private String id;
    private String itemName;
    private String itemPrice;
    private String itemQty;
    private String subTotal;
    private String itemImg;
    private VendorDetailsModel vendorDeailsModel;

    public OrderDetailsModelList(String id, String itemName, String itemPrice, String itemQty, String subTotal, String itemImg, VendorDetailsModel vendorDeailsModel) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQty = itemQty;
        this.subTotal = subTotal;
        this.itemImg = itemImg;
        this.vendorDeailsModel = vendorDeailsModel;
    }

    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemQty() {
        return itemQty;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public String getItemImg() {
        return itemImg;
    }

    public VendorDetailsModel getVendorDeailsModel() {
        return vendorDeailsModel;
    }
}

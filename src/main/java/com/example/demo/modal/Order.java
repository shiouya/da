package com.example.demo.modal;

import java.util.Date;

import lombok.Data;

@Data
public class Order {

	private Date orderDate;
	private String company;
	private String productName;
	private Integer price;
	private Integer quantity;
	private Integer totalPrice;

}

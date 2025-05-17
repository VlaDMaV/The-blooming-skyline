package com.example.thebloomingskyline

import Item

data class Order(
    val items: List<Item>,
    val shipping: String,
    val delivery: String,
    val payment: String,
    val promo: String
)

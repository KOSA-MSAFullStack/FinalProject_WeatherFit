package com.fitcaster.weatherfit.order.api.dto.response;

import com.fitcaster.weatherfit.order.domain.entity.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderHistoryItemDto {
    private Long orderItemId;
    private Long itemId;
    private String itemName;
    private int quantity;
    private int itemPrice;
    private String itemImage;

    public static OrderHistoryItemDto from(OrderItem orderItem) {
        OrderHistoryItemDto dto = new OrderHistoryItemDto();
        dto.orderItemId = orderItem.getId();
        dto.itemId = orderItem.getItem().getItemId();
        dto.itemName = orderItem.getItem().getItemName();
        dto.quantity = orderItem.getQuantity();
        dto.itemPrice = orderItem.getItem().getPrice();
        dto.itemImage = orderItem.getItem().getImageURL();
        return dto;
    }
}
package com.fitcaster.weatherfit.common.exception;

/**
 * 장바구니에 이미 상품이 존재할 때 발생하는 예외
 */
public class CartItemExistsException extends RuntimeException {

    // 장바구니에 담으려는 상품의 이름을 저장하여 컨트롤러에서 메시지를 구성할 수 있게 합니다.
    private final String itemName;

    public CartItemExistsException(String message, String itemName) {
        super(message);
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }
}
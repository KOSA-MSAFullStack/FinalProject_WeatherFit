// ItemRepository.java
// Item 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// * author: 김기성
public interface ItemRepository extends JpaRepository<Item, Long> {
    // 상품명 검색
    List<Item> findByItemNameContainingIgnoreCase(String itemName);

    // 상품 코드로 상품 존재 여부 확인
    boolean existsByItemCode(String itemCode);

    // 상품명 또는 상품 코드로 검색 (페이지네이션)
    Page<Item> findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCase(String itemName, String itemCode, Pageable pageable);

    // 판매중인 상품 개수 조회
    long countByQuantityGreaterThan(int quantity);

    // 품절된 상품 개수 조회
    long countByQuantity(int quantity);

    // 검색 결과 중 판매중인 상품 개수 조회
    @Query("SELECT COUNT(i) FROM Item i WHERE (LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND i.quantity > 0")
    long countSearchResultsWithStock(@Param("keyword") String keyword);

    // 검색 결과 중 품절된 상품 개수 조회
    @Query("SELECT COUNT(i) FROM Item i WHERE (LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND i.quantity = 0")
    long countSearchResultsWithoutStock(@Param("keyword") String keyword);

    // 스토어 페이지에 넘겨줄 전체 상품 최신순 조회
    List<Item> findAllByOrderByCreatedAtDesc();
}

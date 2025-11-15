// ItemService.java
// 상품 서비스
package com.fitcaster.weatherfit.catalog.application;

import com.fitcaster.weatherfit.catalog.api.dto.ItemRequestDTO;
import com.fitcaster.weatherfit.catalog.api.dto.ItemResponseDTO;
import com.fitcaster.weatherfit.catalog.domain.entity.Category;
import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.entity.ItemSeason;
import com.fitcaster.weatherfit.catalog.domain.repository.CategoryRepository;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemSeasonRepository;
import com.fitcaster.weatherfit.catalog.domain.entity.Season;
import com.fitcaster.weatherfit.catalog.domain.repository.SeasonRepository;
import com.fitcaster.weatherfit.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// * author: 김기성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ItemSeasonRepository itemSeasonRepository; // 상품 계절 중개 테이블 레포지토리
    private final ImageUploadService imageUploadService; // 이미지 업로드 서비스
    private final SeasonRepository seasonRepository; // 계절 레포지토리

    // [모든 상품 목록 조회]
    public List<ItemResponseDTO> getAllItems() {
        return itemRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ItemResponseDTO::from)
                .collect(Collectors.toList());
    }

    // [상품 단건 조회]
    @Transactional(readOnly = true)
    public ItemResponseDTO getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + itemId));
        return ItemResponseDTO.from(item);
    }

   // [상품명 검색]
   public List<ItemResponseDTO> searchItemsByName(String itemName) {
       return itemRepository.findByItemNameContainingIgnoreCase(itemName)
               .stream()
               .map(ItemResponseDTO::from)
               .collect(Collectors.toList());
   }

    
    // [상품 등록]
    @Transactional
    public ItemResponseDTO createItem(ItemRequestDTO.Create request) {
        // 카테고리 조회
        Category category = categoryRepository.findByCategory(request.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("⚠️ 카테고리를 찾을 수 없습니다: " + request.getCategory()));

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                // itemCode를 ImageUploadService로 전달
                imageUrl = imageUploadService.uploadImage(request.getImage(), request.getItemCode());
            } catch (IOException e) {
                throw new IllegalArgumentException("⚠️ 이미지 업로드에 실패했습니다.", e);
            }
        }

        // Item 엔티티 생성
        Item item = Item.builder()
                .category(category)
                .itemName(request.getItemName())
                .itemCode(request.getItemCode()) // 요청에서 받은 itemCode 사용
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .gender(request.getGender())
                .imageURL(imageUrl)
                .aiDescription(request.getAiDescription())
                .maxTemperature(request.getMaxTemperature() != null ? request.getMaxTemperature() : 0) // 추가
                .minTemperature(request.getMinTemperature() != null ? request.getMinTemperature() : 0) // 추가
                .createdAt(LocalDate.now()) // 현재 날짜로 등록일 설정
                .build();

        // Item 저장
        try {
            Item savedItem = itemRepository.save(item);

            // 계절 정보 저장
            if (request.getSeasonName() != null && !request.getSeasonName().isEmpty()) {
                for (String seasonStr : request.getSeasonName()) {
                    Season season = seasonRepository.findBySeasonName(seasonStr)
                            .orElseThrow(() -> new IllegalArgumentException("⚠️ 계절 정보를 찾을 수 없습니다: " + seasonStr));
                    ItemSeason itemSeason = ItemSeason.builder()
                            .item(savedItem)
                            .season(season)
                            .build();
                    itemSeasonRepository.save(itemSeason);
                }
            }
            
            // ItemResponse로 변환하여 반환
            return ItemResponseDTO.from(savedItem);
        } catch (DataIntegrityViolationException e) {
            // DB 제약 조건 위반 시 (예: 유니크 키 중복) - 409 Conflict
            throw new IllegalArgumentException("⚠️ 상품 등록 중 데이터 무결성 오류가 발생했습니다.", e);
        }
    }

    // [상품 수정]
    @Transactional
    public ItemResponseDTO updateItem(Long itemId, ItemRequestDTO.Update request) {
        // 1) 상품 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("⚠️ 요청하신 ID에 해당하는 상품 찾을 수 없음"));

        // 2) 카테고리 업데이트
        if (request.getCategoryId() != null) { // TODO: 카테고리 이름으로 업데이트하도록 변경 필요
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("⚠️ 요청하신 ID에 해당하는 카테고리 찾을 수 없음"));
            item.updateCategory(category);
        }

        // 3) 나머지 필드 업데이트
        item.updateDetails(
                request.getItemName(),
                request.getPrice(),
                request.getQuantity(),
                request.getGender(),
                request.getImageURL(), // TODO: 이미지 파일 업데이트 로직 추가 필요
                request.getAiDescription(),
                request.getMaxTemperature(), // TODO: 온도 정보는 계절 기반으로 변경 필요
                request.getMinTemperature()  // TODO: 온도 정보는 계절 기반으로 변경 필요
        );

        // 4) 상품 저장 및 반환
        try {
            Item updatedItem = itemRepository.save(item);
            return ItemResponseDTO.from(updatedItem);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("⚠️ 이미 사용 중인 상품 코드입니다.");
        }
    }

    
    // [상품 삭제]
    @Transactional
    public void deleteItem(Long itemId) {
        // 상품 존재 여부 확인
        if (!itemRepository.existsById(itemId)) {
            throw new NoSuchElementException("⚠️ 요청하신 ID에 해당하는 상품 찾을 수 없음");
        }

        try {
            itemRepository.deleteById(itemId);
        } catch (DataIntegrityViolationException e) {
            // DB 제약 조건 위반 시 (예: 외래 키 참조)
            throw new IllegalArgumentException("⚠️ 해당 상품을 참조하는 데이터(예: 주문 내역)가 존재하여 삭제할 수 없음");
        }
    }

    public List<Item> findByClassificationAndSeason(String classification, String season) {
        List<Item> items = itemSeasonRepository.findByClassificationAndSeason(classification, season);
        return items;
    }
}

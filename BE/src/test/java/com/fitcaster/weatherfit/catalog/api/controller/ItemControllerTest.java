// ItemControllerTest.java
// ItemController 통합 테스트

package com.fitcaster.weatherfit.catalog.api.controller;

import com.fitcaster.weatherfit.catalog.domain.entity.Category;
import com.fitcaster.weatherfit.catalog.domain.entity.Classification;
import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.repository.CategoryRepository;
import com.fitcaster.weatherfit.catalog.domain.repository.ClassificationRepository;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        Classification classification = Classification.builder()
                .classification("아우터")
                .build();
        classificationRepository.save(classification);

        testCategory = Category.builder()
                .classification(classification)
                .category("자켓")
                .build();
        categoryRepository.save(testCategory);
    }

    @Test
    void getAllItems_shouldReturnItemList() throws Exception {
        // given
        Item item1 = Item.builder()
                .category(testCategory)
                .itemName("Test Jacket 1")
                .itemCode("T001")
                .price(10000)
                .quantity(10)
                .minTemperature(10)
                .maxTemperature(20)
                .gender("C")
                .imageURL("http://example.com/image1.jpg")
                .createdAt(LocalDate.now())
                .aiDescription("AI description for jacket 1")
                .build();

        Item item2 = Item.builder()
                .category(testCategory)
                .itemName("Test Jacket 2")
                .itemCode("T002")
                .price(20000)
                .quantity(5)
                .minTemperature(5)
                .maxTemperature(15)
                .gender("M")
                .imageURL("http://example.com/image2.jpg")
                .createdAt(LocalDate.now())
                .aiDescription("AI description for jacket 2")
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        // when & then
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].itemName", is("Test Jacket 1")))
                .andExpect(jsonPath("$[1].itemName", is("Test Jacket 2")));
    }
}
package com.riddle.airiddlegame.controller;

import com.riddle.airiddlegame.dto.request.RiddleRequest;
import com.riddle.airiddlegame.dto.response.ApiResponse;
import com.riddle.airiddlegame.dto.response.CategoryDto;
import com.riddle.airiddlegame.dto.response.RiddleDto;
import com.riddle.airiddlegame.service.CategoryService;
import com.riddle.airiddlegame.service.RiddleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final RiddleService riddleService;
    private final CategoryService categoryService;

    public AdminController(RiddleService riddleService, CategoryService categoryService) {
        this.riddleService = riddleService;
        this.categoryService = categoryService;
    }

    // --- Riddle CRUD ---
    @PostMapping("/riddles")
    public ResponseEntity<ApiResponse<RiddleDto>> createRiddle(@Valid @RequestBody RiddleRequest request) {
        RiddleDto created = riddleService.createRiddle(request);
        return ResponseEntity.ok(ApiResponse.success("Riddle created successfully", created));
    }

    @PutMapping("/riddles/{id}")
    public ResponseEntity<ApiResponse<RiddleDto>> updateRiddle(@PathVariable("id") Long id, @Valid @RequestBody RiddleRequest request) {
        RiddleDto updated = riddleService.updateRiddle(id, request);
        return ResponseEntity.ok(ApiResponse.success("Riddle updated successfully", updated));
    }

    @DeleteMapping("/riddles/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRiddle(@PathVariable("id") Long id) {
        riddleService.deleteRiddle(id);
        return ResponseEntity.ok(ApiResponse.success("Riddle deleted successfully", null));
    }

    // --- Category CRUD ---
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestBody CategoryDto dto) {
        CategoryDto created = categoryService.createCategory(dto);
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", created));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable("id") Long id, @RequestBody CategoryDto dto) {
        CategoryDto updated = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}

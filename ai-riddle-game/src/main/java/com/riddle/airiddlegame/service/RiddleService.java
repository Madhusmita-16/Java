package com.riddle.airiddlegame.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riddle.airiddlegame.dto.request.RiddleRequest;
import com.riddle.airiddlegame.dto.response.RiddleDto;
import com.riddle.airiddlegame.entity.Category;
import com.riddle.airiddlegame.entity.Riddle;
import com.riddle.airiddlegame.exception.ResourceNotFoundException;
import com.riddle.airiddlegame.repository.CategoryRepository;
import com.riddle.airiddlegame.repository.RiddleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RiddleService {

    private final RiddleRepository riddleRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RiddleService(RiddleRepository riddleRepository, CategoryRepository categoryRepository) {
        this.riddleRepository = riddleRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<RiddleDto> getAllRiddles(String difficulty, Long categoryId) {
        List<Riddle> riddles = riddleRepository.findByFilter(difficulty, categoryId);
        return riddles.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public RiddleDto getRiddleById(Long id) {
        Riddle riddle = riddleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Riddle not found with id: " + id));
        return mapToDto(riddle);
    }

    @Transactional
    public RiddleDto createRiddle(RiddleRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        String optionsJson = convertListToJson(request.getOptions());
        String altAnswersJson = convertListToJson(request.getAltAnswers());

        Riddle riddle = new Riddle(
                request.getQuestion(),
                optionsJson,
                request.getCorrectAnswer(),
                altAnswersJson,
                request.getHint(),
                request.getDifficulty(),
                category,
                request.getBasePoints()
        );

        Riddle saved = riddleRepository.save(riddle);
        return mapToDto(saved);
    }

    @Transactional
    public RiddleDto updateRiddle(Long id, RiddleRequest request) {
        Riddle riddle = riddleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Riddle not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        riddle.setQuestion(request.getQuestion());
        riddle.setOptionsJson(convertListToJson(request.getOptions()));
        riddle.setCorrectAnswer(request.getCorrectAnswer());
        riddle.setAltAnswersJson(convertListToJson(request.getAltAnswers()));
        riddle.setHint(request.getHint());
        riddle.setDifficulty(request.getDifficulty());
        riddle.setCategory(category);
        riddle.setBasePoints(request.getBasePoints());

        Riddle updated = riddleRepository.save(riddle);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteRiddle(Long id) {
        if (!riddleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Riddle not found with id: " + id);
        }
        riddleRepository.deleteById(id);
    }

    public RiddleDto mapToDto(Riddle riddle) {
        RiddleDto dto = new RiddleDto();
        dto.setId(riddle.getId());
        dto.setQuestion(riddle.getQuestion());
        dto.setOptions(convertJsonToList(riddle.getOptionsJson()));
        dto.setCorrectAnswer(riddle.getCorrectAnswer());
        dto.setAltAnswers(convertJsonToList(riddle.getAltAnswersJson()));
        dto.setHint(riddle.getHint());
        dto.setDifficulty(riddle.getDifficulty());
        dto.setCategoryId(riddle.getCategory().getId());
        dto.setCategoryName(riddle.getCategory().getName());
        dto.setBasePoints(riddle.getBasePoints());
        return dto;
    }

    private String convertListToJson(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception ex) {
            return null;
        }
    }

    private List<String> convertJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}

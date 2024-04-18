package com.example.qafordevs.rest;

import com.example.qafordevs.dto.DeveloperDto;
import com.example.qafordevs.dto.ErrorDto;
import com.example.qafordevs.entity.DeveloperEntity;
import com.example.qafordevs.exception.DeveloperNotFoundException;
import com.example.qafordevs.exception.DeveloperWithDuplicateException;
import com.example.qafordevs.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/developers")
@RequiredArgsConstructor
public class DeveloperRestControllerV1 {

    private final DeveloperService developerService;

    @PostMapping
    public ResponseEntity<?> createDeveloper(@RequestBody DeveloperDto dto) {
        try {
            DeveloperEntity entity = dto.toEntity();
            DeveloperEntity createdDeveloper = developerService.saveDeveloper(entity);
            DeveloperDto result = DeveloperDto.fromEntity(createdDeveloper);
            return ResponseEntity.ok(result);
        } catch (DeveloperWithDuplicateException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorDto.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateDeveloper(@RequestBody DeveloperDto dto) {
        try {
            DeveloperEntity entity = dto.toEntity();
            DeveloperEntity updatedEntity = developerService.updateDeveloper(entity);
            DeveloperDto result = DeveloperDto.fromEntity(updatedEntity);
            return ResponseEntity.ok(result);
        } catch (DeveloperNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorDto.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeveloperById(@PathVariable("id") Integer id) {
        try {
            DeveloperEntity entity = developerService.getDeveloperById(id);
            DeveloperDto result = DeveloperDto.fromEntity(entity);
            return ResponseEntity.ok(result);
        } catch (DeveloperNotFoundException e) {
            return ResponseEntity
                    .status(404)
                    .body(ErrorDto.builder()
                            .status(404)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllDevelopers() {
        List<DeveloperEntity> entities = developerService.getAllDevelopers();
        List<DeveloperDto> dtos = entities.stream()
                .map(DeveloperDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<?> getAllDevelopersBySpecialty(@PathVariable("specialty") String specialty) {
        List<DeveloperEntity> entities = developerService.getAllActiveBySpecialty(specialty);
        List<DeveloperDto> dtos = entities.stream()
                .map(DeveloperDto::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeveloperById(@PathVariable("id") Integer id, @RequestParam(value = "isHard", defaultValue = "false") boolean isHard) {
        try {
            if (isHard) {
                developerService.hardDeleteById(id);
            } else {
                developerService.softDeleteById(id);
            }
            return ResponseEntity.ok().build();
        } catch (DeveloperNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorDto.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build());
        }
    }
}

package com.cdac.dreamblog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dreamblog.dto.DreamWithCommentsDto;
import com.cdac.dreamblog.dto.request.DreamRequestDto;
import com.cdac.dreamblog.dto.request.ReactionRequestDto;
import com.cdac.dreamblog.dto.response.DreamResponseDto;
import com.cdac.dreamblog.service.implementation.DreamServiceImplementation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dreams")
public class DreamController {
    @Autowired
    private DreamServiceImplementation dreamService;

    @PostMapping
    public ResponseEntity<?> createDream(@Valid @RequestBody DreamRequestDto dreamDto) {
        try {
            DreamResponseDto dreamResponseDto = dreamService.createDream(dreamDto);
            return ResponseEntity.ok(dreamResponseDto);
        } catch (Exception e) {

            System.out.println("Error creating dream: " + e.getMessage());
            System.out.println("Stack trace: " + e.getCause());
            return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllDreams(  
      ) {
        try {
            return ResponseEntity.ok(dreamService.getAllDreams());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }

    }

    @GetMapping("/user/{userId}") // A more RESTful endpoint for dreams by user
    public ResponseEntity<?> getDreamsByUserId(@PathVariable Long userId) {
        try {
            List<DreamWithCommentsDto> dreamDto = dreamService.getDreamsByUserId(userId);
            return ResponseEntity.ok(dreamDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDreamById(@PathVariable Long id) {
        try {
            DreamResponseDto dreamResponseDto = dreamService.getDreamById(id);
            return ResponseEntity.ok(dreamResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDream(@PathVariable Long id, @Valid @RequestBody DreamRequestDto dreamDto) {
        try {
            DreamResponseDto dreamResponseDto = dreamService.updateDream(id,dreamDto);
            return ResponseEntity.ok(dreamResponseDto);
        } catch (Exception e) {

            System.out.println("Error creating dream: " + e.getMessage());
            System.out.println("Stack trace: " + e.getCause());
            return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reaction")
    public ResponseEntity<?> likeDream(@PathVariable Long id, @RequestBody ReactionRequestDto reactionDto) {
        try {
            DreamResponseDto dreamResponseDto = dreamService.reactToDream(id, reactionDto.getUserId(), reactionDto.getReactionType());
            return ResponseEntity.ok(dreamResponseDto);
        } catch (Exception e) {

            System.out.println("Error creating dream: " + e.getMessage());
            System.out.println("Stack trace: " + e.getStackTrace());
            return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDream(@PathVariable Long id) {
        try {
            dreamService.deleteDream(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleted Successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }

    }
}

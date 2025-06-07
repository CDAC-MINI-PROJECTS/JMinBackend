package com.cdac.dreamblog.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dreamblog.model.Comment;
import com.cdac.dreamblog.model.Dream;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.dto.DreamDto;
import com.cdac.dreamblog.dto.comment.CommentResponseDto;
import com.cdac.dreamblog.dto.dream.DreamWithCommentsDto;
import com.cdac.dreamblog.dto.follow.UserMinimalDto;
import com.cdac.dreamblog.repository.CommentRepository;
import com.cdac.dreamblog.repository.DreamRepository;
import com.cdac.dreamblog.repository.UserRepository;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/dreams")
public class DreamController {
    @Autowired
    private DreamRepository dreamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

     private UserMinimalDto toUserMinimalDto(User user) {
        if (user == null) return null;
        UserMinimalDto dto = new UserMinimalDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        return dto;
    }
    private CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment == null) return null;
        CommentResponseDto dto = new CommentResponseDto();
        dto.setCommentId(comment.getCommentId());
        dto.setCommentText(comment.getCommentText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setVisibility(comment.getVisibility());
        // For nested objects, ensure no circular references.
        // If CommentResponseDto has a Dream, set it to null here to break cycle.
        dto.setDream(null); // Explicitly setting to null to avoid circular references
        dto.setUser(toUserMinimalDto(comment.getUser()));
        return dto;
    }

     private DreamWithCommentsDto toDreamWithCommentsDto(Dream dream) {
        if (dream == null) return null;
        DreamWithCommentsDto dto = new DreamWithCommentsDto();
        dto.setDreamId(dream.getDreamId());
        dto.setContent(dream.getContent());
        dto.setCreatedAt(dream.getCreatedAt());
        dto.setVisibility(dream.getVisibility());
        dto.setUser(toUserMinimalDto(dream.getUser()));

        // --- NEW: Fetch comments directly using commentRepository ---
        List<Comment> comments = commentRepository.findByDreamOrderByCreatedAtAsc(dream);
        dto.setComments(comments.stream()
                                .map(this::toCommentResponseDto)
                                .collect(Collectors.toList()));
        return dto;
    }

    @PostMapping
    public ResponseEntity<?> createDream(@Valid @RequestBody DreamDto dreamDto) {
        System.out.println(dreamDto);
        try {
            User user = userRepository.findById(dreamDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            Dream dream = new Dream();
            dream.setTitle(dreamDto.getTitle());
            dream.setContent(dreamDto.getContent());
            dream.setTags(dreamDto.getTags());
            dream.setVisibility(dreamDto.getVisibility());
            dream.setCreatedAt(LocalDateTime.now());
            dream.setUser(user);
            dreamRepository.save(dream);
            return ResponseEntity.ok(dream);
            
        } catch (Exception e) {

              System.out.println("Error creating dream: " + e.getMessage());
              System.out.println("Stack trace: "+ e.getCause());
              return ResponseEntity.status(500).body("Error creating dream: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Dream> getAllDreams() {
        return dreamRepository.findAll();
    }

    @GetMapping("/user/{userId}") // A more RESTful endpoint for dreams by user
    public ResponseEntity<List<DreamWithCommentsDto>> getDreamsByUserId(@PathVariable Long userId) {

        // 1. Find the UserEntity first
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            // Return 404 Not Found if the user doesn't exist
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        // 2. Use a custom method in DreamRepository to find dreams by UserEntity
        List<Dream> dreams = dreamRepository.findByUser(user);
        

        List<DreamWithCommentsDto> dreamDtos = dreams.stream()
                                                    .map(this::toDreamWithCommentsDto)
                                                    .collect(Collectors.toList());
        // Or, if you prefer to find directly by user's ID (assuming DreamEntity has a direct userId column or a custom method):
        // List<DreamEntity> dreams = dreamRepository.findByUserId(userId); // Requires this method in DreamRepository

        return ResponseEntity.ok(dreamDtos);
    }

    // If you literally meant "get dreams by a single dream ID" (not user ID),
    // and your @PathVariable `id` is a dream's ID:
    @GetMapping("/{id}") // More precise endpoint for a single dream by its ID
    public ResponseEntity<Dream> getDreamById(@PathVariable Long id) {
        Optional<Dream> dreamOptional = dreamRepository.findById(id);

        return dreamOptional.map(ResponseEntity::ok) // If found, return 200 OK
                            .orElseGet(() -> ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeletePost(@PathVariable Long id) { 
        Optional<Dream> dreamOptional = dreamRepository.findById(id);
        if (dreamOptional.isPresent()) {
            dreamRepository.deleteById(id);
            return ResponseEntity.ok().body("Dream deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dream not found");
        }
    }
}

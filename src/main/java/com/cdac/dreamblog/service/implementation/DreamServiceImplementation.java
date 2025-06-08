package com.cdac.dreamblog.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cdac.dreamblog.dto.DreamWithCommentsDto;
import com.cdac.dreamblog.dto.UserMinimalDto;
import com.cdac.dreamblog.dto.request.DreamRequestDto;
import com.cdac.dreamblog.dto.response.CommentResponseDto;
import com.cdac.dreamblog.dto.response.DreamResponseDto;
import com.cdac.dreamblog.dto.response.UserResponseDto;
import com.cdac.dreamblog.exception.ResourceNotFoundException;
import com.cdac.dreamblog.model.Comment;
import com.cdac.dreamblog.model.Dream;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.CommentRepository;
import com.cdac.dreamblog.repository.DreamRepository;
import com.cdac.dreamblog.repository.UserRepository;

@Service
public class DreamServiceImplementation {

    @Autowired
    private DreamRepository dreamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private UserMinimalDto toUserMinimalDto(User user) {
        if (user == null)
            return null;
        UserMinimalDto dto = new UserMinimalDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        return dto;
    }

    // Add this method to convert User to UserResponseDto
    private UserResponseDto toUserResponseDto(User user) {
        if (user == null)
            return null;
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        // Set other fields as needed
        return dto;
    }

    private CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment == null)
            return null;
        CommentResponseDto dto = new CommentResponseDto();
        dto.setCommentId(comment.getCommentId());
        dto.setCommentText(comment.getCommentText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setVisibility(comment.getVisibility());
        // For nested objects, ensure no circular references.
        // If CommentResponseDto has a Dream, set it to null here to break cycle.
        dto.setDream(null); // Explicitly setting to null to avoid circular
        dto.setUser(toUserMinimalDto(comment.getUser()));
        return dto;
    }

    private DreamWithCommentsDto toDreamWithCommentsDto(Dream dream) {
        if (dream == null)
            return null;
        DreamWithCommentsDto dto = new DreamWithCommentsDto();
        dto.setDreamId(dream.getDreamId());
        dto.setContent(dream.getContent());
        dto.setUser(toUserMinimalDto(dream.getUser()));
        List<Comment> comments = commentRepository.findByDreamOrderByCreatedAtAsc(dream);
        dto.setComments(comments.stream()
                .map(this::toCommentResponseDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private DreamResponseDto toDreamResponseDto(Dream dream) {
        DreamResponseDto dreamResponseDto = new DreamResponseDto();
        dreamResponseDto.setDreamId(dream.getDreamId());
        dreamResponseDto.setContent(dream.getContent());
        dreamResponseDto.setCreatedAt(dream.getCreatedAt());
        dreamResponseDto.setVisibility(dream.getVisibility());
        dreamResponseDto.setUser(toUserResponseDto(dream.getUser()));
        return dreamResponseDto;
    }

    public DreamResponseDto createDream(DreamRequestDto dreamRequestDto) {
        User user = userRepository.findById(dreamRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Dream dream = new Dream();
        dream.setTitle(dreamRequestDto.getTitle());
        dream.setContent(dreamRequestDto.getContent());
        dream.setTags(dreamRequestDto.getTags());
        dream.setVisibility(dreamRequestDto.getVisibility());
        dream.setCreatedAt(LocalDateTime.now());
        dream.setUser(user);
        dreamRepository.save(dream);
        return toDreamResponseDto(dream);
    }

    public List<DreamResponseDto> getAllDreams() {
        List<Dream> dream = dreamRepository.findAll();
        List<DreamResponseDto> dreamResponseDtos = dream.stream().map(this::toDreamResponseDto)
                .collect(Collectors.toList());
        return dreamResponseDtos;
    }

    public DreamResponseDto getDreamById(Long id) {
        Optional<Dream> dreamOptional = dreamRepository.findById(id);
        return dreamOptional.map(this::toDreamResponseDto).orElse(null);
    }

    public List<DreamWithCommentsDto> getDreamsByUserId(Long userId) {
        // 1. Find the UserEntity first
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            // Return 404 Not Found if the user doesn't exist
            // throw ResourceNotFoundException("user doesnt exist");
        }

        User user = userOptional.get();

        // 2. Use a custom method in DreamRepository to find dreams by UserEntity
        List<Dream> dreams = dreamRepository.findByUser(user);

        List<DreamWithCommentsDto> dreamDtos = dreams.stream()
                .map(this::toDreamWithCommentsDto)
                .collect(Collectors.toList());
        // Or, if you prefer to find directly by user's ID (assuming DreamEntity has a
        // direct userId column or a custom method):
        // List<DreamEntity> dreams = dreamRepository.findByUserId(userId); // Requires
        // this method in DreamRepositor
        return dreamDtos;
    }

    public DreamResponseDto updateDream(Long id, DreamRequestDto dreamRequestDto) {
        Dream dream = dreamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        dream.setContent(dreamRequestDto.getContent());
        dream.setTitle(dreamRequestDto.getTitle());
        // dream.getTags(dreamRequestDto.getTags());
        dream.setLastUpdated(LocalDateTime.now());

        return toDreamResponseDto(dream);

    }

    public boolean deleteDream(Long id) {
        Optional<Dream> dreamOptional = dreamRepository.findById(id);
        if (dreamOptional.isPresent()) {
            dreamRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}

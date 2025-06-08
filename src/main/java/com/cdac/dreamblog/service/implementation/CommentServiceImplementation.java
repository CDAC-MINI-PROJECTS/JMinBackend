package com.cdac.dreamblog.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import com.cdac.dreamblog.dto.DreamMinimalDto;
import com.cdac.dreamblog.dto.UserMinimalDto;
import com.cdac.dreamblog.dto.request.CommentRequestDto;
import com.cdac.dreamblog.dto.response.CommentResponseDto;
import com.cdac.dreamblog.exception.BadRequestException;
import com.cdac.dreamblog.model.Comment;
import com.cdac.dreamblog.model.Dream;
import com.cdac.dreamblog.model.User;
import com.cdac.dreamblog.repository.CommentRepository;
import com.cdac.dreamblog.repository.DreamRepository;
import com.cdac.dreamblog.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CommentServiceImplementation {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DreamRepository dreamRepository;

    private UserMinimalDto toUserMinimalDto(User user) {
        if (user == null)
            return null;
        UserMinimalDto dto = new UserMinimalDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        return dto;
    }

    private DreamMinimalDto toDreamMinimalDto(Dream dream) {
        if (dream == null)
            return null;
        DreamMinimalDto dto = new DreamMinimalDto();
        dto.setDreamId(dream.getDreamId());
        // Truncate content for minimal DTO
        dto.setContent(dream.getContent() != null && dream.getContent().length() > 50
                ? dream.getContent().substring(0, 50) + "..."
                : dream.getContent());
        dto.setVisibility(dream.getVisibility());
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
        dto.setDream(toDreamMinimalDto(comment.getDream()));
        dto.setUser(toUserMinimalDto(comment.getUser()));
        return dto;
    }

    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + id));

        User requestingUser = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Requesting user not found with ID: " + requestDto.getUserId()));

        // Basic authorization check: Only the comment author can update
        if (!existingComment.getUser().getUserId().equals(requestingUser.getUserId())) {
            throw new BadRequestException("You are not authorized to update this comment.");
        }

        existingComment.setCommentText(requestDto.getCommentText());
        // Update visibility if provided and valid
        Optional.ofNullable(requestDto.getVisibility())
                .filter(v -> v.equals("public") || v.equals("private"))
                .ifPresent(existingComment::setVisibility);

        Comment updatedComment = commentRepository.save(existingComment);

        CommentResponseDto commentResponseDto = toCommentResponseDto(updatedComment);

        return commentResponseDto;

    }

    public CommentResponseDto createComment(CommentRequestDto requestDto) {
        // 1. Fetch associated Dream and User entities from repositories
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + requestDto.getUserId()));

        Dream dream = dreamRepository.findById(requestDto.getDreamId())
                .orElseThrow(() -> new EntityNotFoundException("Dream not found with ID: " + requestDto.getDreamId()));

        // 2. Create Comment entity
        Comment comment = new Comment();
        comment.setCommentText(requestDto.getCommentText());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setDream(dream);
        comment.setUser(user);

        // Set visibility, defaulting to "public"
        comment.setVisibility(Optional.ofNullable(requestDto.getVisibility())
                .filter(v -> v.equals("public") || v.equals("private"))
                .orElse("public"));

        // 3. Save to database directly via repository
        Comment savedComment = commentRepository.save(comment);

        // 4. Convert and return DTO
        return toCommentResponseDto(savedComment);

    }

    public CommentResponseDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        return toCommentResponseDto(comment);
    }

    public List<CommentResponseDto> getCommentByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        List<Comment> comments = commentRepository.findByUserOrderByCreatedAtDesc(user);
        return comments.stream().map(this::toCommentResponseDto).collect(Collectors.toList());

    }

    public List<CommentResponseDto> getCommentByDream(Long dreamId) {
        Dream dream = dreamRepository.findById(dreamId)
                .orElseThrow(() -> new EntityNotFoundException("Dream not found with ID: " + dreamId));

        List<Comment> comments;
        comments = commentRepository.findByDreamOrderByCreatedAtAsc(dream);
        // Basic visibility logic here (e.g., if user is admin, show all; otherwise,
        // only public)
        // if ("ADMIN".equalsIgnoreCase(userRole)) { // Example admin role check
        // } else {
            // comments = commentRepository.findByDreamAndVisibilityOrderByCreatedAtAsc(dream, "public");
        // }
        return comments.stream()
                .map(this::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId, Long requestingUserId) {
        Comment commentToDelete = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Requesting user not found with ID: " + requestingUserId));

        // Basic authorization check: Only the comment author can delete (or an admin,
        // if implemented)
        if (!commentToDelete.getUser().getUserId().equals(requestingUser.getUserId())) {
            // In a real app, you'd also check if requestingUser has ADMIN role here
            throw new BadCredentialsException("You are not authorized to delete this comment.");
        }

        commentRepository.delete(commentToDelete);
    }

}

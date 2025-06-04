package com.cdac.dreamblog.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
@IdClass(Follow.FollowId.class)
public class Follow {

    @Id
    @ManyToOne
    @JoinColumn(name = "followerId")
    private User follower;

    @Id
    @ManyToOne
    @JoinColumn(name = "followedId")
    private User followed;

    private LocalDateTime followedAt;

    // Composite ID class
     public static class FollowId implements Serializable {
        private Long follower;
        private Long followed;

        public FollowId() {}

        public FollowId(Long follower, Long followed) {
            this.follower = follower;
            this.followed = followed;
        }

        // Getters, Setters, equals, and hashCode
        public Long getFollower() {
            return follower;
        }

        public void setFollower(Long follower) {
            this.follower = follower;
        }

        public Long getFollowed() {
            return followed;
        }

        public void setFollowed(Long followed) {
            this.followed = followed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FollowId)) return false;
            FollowId that = (FollowId) o;
            return Objects.equals(follower, that.follower) &&
                   Objects.equals(followed, that.followed);
        }

        @Override
        public int hashCode() {
            return Objects.hash(follower, followed);
        }
    }

}
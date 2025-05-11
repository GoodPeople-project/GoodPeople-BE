package team.goodpeople.community.post.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.goodpeople.community.post.entity.Post

interface PostRepository : JpaRepository<Post, Long> {
}
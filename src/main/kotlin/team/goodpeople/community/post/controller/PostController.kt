package team.goodpeople.community.post.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.goodpeople.community.post.dto.CreatePostDto
import team.goodpeople.community.post.dto.PostDto
import team.goodpeople.community.post.service.PostService
import team.goodpeople.global.response.ApiResponse
import team.goodpeople.security.jwt.annotation.GetUserId

@RequestMapping("/api/community/post")
@RestController
class PostController(
    private val postService: PostService
) {

    @PostMapping
    fun createNewPost(
        @GetUserId userId: Long,
        @RequestBody createPostDto: CreatePostDto
    ): ApiResponse<Boolean> {

        val result = postService.createNewPost(userId, createPostDto)

        return ApiResponse.success(
            result = result,
            status = HttpStatus.CREATED.value(),
            message = "게시글이 작성되었습니다."
        )
    }
}
package team.goodpeople.community.post.service

import org.springframework.stereotype.Service
import team.goodpeople.community.board.repository.BoardRepository
import team.goodpeople.community.post.dto.CreatePostDto
import team.goodpeople.community.post.dto.PostDto
import team.goodpeople.community.post.dto.UpdatePostDto
import team.goodpeople.community.post.entity.Post.Companion.createPost
import team.goodpeople.community.post.repository.PostRepository
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException
import team.goodpeople.user.repository.UserRepository

@Service
class PostService(
    private val postRepository: PostRepository,
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {

    fun createNewPost(
        userId: Long,
        createPostDto: CreatePostDto
    ): Boolean {
        /** 요청자 ID 유효성 검사 */
        val user = userRepository.getUserById(userId)
            ?: throw GlobalException(CustomErrorCode.USER_NOT_EXISTS)
        
        // TODO; 예외처리
        val board = boardRepository.getBoardById(createPostDto.boardId)
            ?: throw GlobalException(CustomErrorCode.EXAMPLE_ERROR)
        
        val newPost = createPost(
            title = createPostDto.title,
            body = createPostDto.body,
            board = board,
            author = user
        )
        
        postRepository.save(newPost)

        return true
    }
    
    fun updatePostBody(
        userId: Long,
        updatePostDto: UpdatePostDto
    ) {
        val user = userRepository.findById(userId)
            .orElseThrow { throw GlobalException(CustomErrorCode.USER_NOT_EXISTS) }
        
        
    }
}
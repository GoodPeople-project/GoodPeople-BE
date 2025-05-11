package team.goodpeople.community.board.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import team.goodpeople.community.board.dto.CreateBoardDto
import team.goodpeople.community.board.dto.BoardResponseDto
import team.goodpeople.community.board.service.BoardService
import team.goodpeople.global.response.ApiResponse

@RequestMapping("/api/community/board")
@RestController
class BoardController(
    private val boardService: BoardService
) {

    @PostMapping
    fun createNewBoard(
        @RequestBody createBoardDto: CreateBoardDto
    ): ApiResponse<BoardResponseDto> {

        val result = boardService.createNewBoard(createBoardDto)

        return ApiResponse.success(
            result = result,
            status = HttpStatus.CREATED.value(),
            message = "게시판이 생성되었습니다.")
    }

    @GetMapping("/{boardId}")
    fun goToBoard(
        @PathVariable boardId: Long
    ): ApiResponse<BoardResponseDto> {

        val result = boardService.getBoard(boardId)

        return ApiResponse.success(
            result = result,
            status = HttpStatus.OK.value(),
            message = "게시판을 불러왔습니다.")
    }

    @GetMapping("/all")
    fun getAllBoards(
    ): ApiResponse<List<BoardResponseDto>> {

        val result = boardService.getAllBoards()

        return ApiResponse.success(
            result = result,
            status = HttpStatus.OK.value(),
            message = "모든 게시판을 불러왔습니다."
        )
    }
}
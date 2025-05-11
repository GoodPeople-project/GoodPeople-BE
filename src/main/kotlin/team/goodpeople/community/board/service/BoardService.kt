package team.goodpeople.community.board.service

import org.springframework.stereotype.Service
import team.goodpeople.community.board.dto.CreateBoardDto
import team.goodpeople.community.board.dto.BoardResponseDto
import team.goodpeople.community.board.entity.Board.Companion.createBoard
import team.goodpeople.community.board.repository.BoardRepository
import team.goodpeople.global.exception.CustomErrorCode
import team.goodpeople.global.exception.GlobalException

@Service
class BoardService(
    private val boardRepository: BoardRepository
) {

    fun createNewBoard(
        createBoardDto: CreateBoardDto
    ): BoardResponseDto {
        val topic = createBoardDto.topic
        val description = createBoardDto.description

        val newBoard = createBoard(
            topic = topic,
            description = description,)

        boardRepository.save(newBoard)

        val createdBoard = boardRepository.getBoardByTopic(topic)
            // TODO: 예외처리
            ?: throw GlobalException(CustomErrorCode.EXAMPLE_ERROR)

        return BoardResponseDto(
                boardId = createdBoard.id!!,
                topic = createdBoard.topic,
                description = createdBoard.description,
        )
    }

    fun getBoard(
        boardId: Long
    ): BoardResponseDto {
        val board = boardRepository.getBoardById(boardId)
            // TODO; 예외처리
            ?: throw GlobalException(CustomErrorCode.EXAMPLE_ERROR)

        return BoardResponseDto(
            boardId = board.id!!,
            topic = board.topic,
            description = board.description,
        )
    }
    
    fun getAllBoards(
    ): List<BoardResponseDto> {

        val boards = boardRepository.findAll()
            // TODO; 예외처리    
            ?: throw GlobalException(CustomErrorCode.EXAMPLE_ERROR)
        
        return boards.map { board ->
            BoardResponseDto(
                boardId = board.id!!,
                topic = board.topic,
                description = board.description,
            )
        }
    }
}
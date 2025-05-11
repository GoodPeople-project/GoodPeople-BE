package team.goodpeople.community.board.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.goodpeople.community.board.entity.Board

interface BoardRepository : JpaRepository<Board, Long> {
    fun getBoardByTopic(topic: String): Board?

    fun getBoardById(id: Long): Board?
}
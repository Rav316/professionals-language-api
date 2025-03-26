package ru.alex.professionalslanguageapi.database.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alex.professionalslanguageapi.database.entity.LeaderboardItem;

import java.util.List;

@Repository
public interface LeaderboardItemRepository extends JpaRepository<LeaderboardItem, Integer> {
    @Query("SELECT li FROM LeaderboardItem li LEFT JOIN FETCH li.user")
    List<LeaderboardItem> findAllWithUsers(Sort sort);
}

package ru.alex.professionalslanguageapi.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alex.professionalslanguageapi.database.entity.Word;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Integer> {

    @Query(value = """
        WITH word_selection AS (
            SELECT * FROM word
            ORDER BY RANDOM()
            LIMIT 5
        ),
        wrong_answers AS (
            SELECT w1.id, w2.translation
            FROM word_selection w1
            JOIN word w2 ON w1.translation <> w2.translation
            ORDER BY RANDOM()
        ),
        final_answers AS (
            SELECT id, translation, TRUE AS is_correct FROM word_selection
            UNION ALL
            SELECT id, translation, FALSE AS is_correct
            FROM (
                SELECT id, translation,
                ROW_NUMBER() OVER (PARTITION BY id ORDER BY RANDOM()) AS rn
                FROM wrong_answers
            ) sub
            WHERE rn <= 3
        )
        SELECT w.id AS word_id, w.word, w.transcription,
               fa.translation, fa.is_correct
        FROM final_answers fa
        JOIN word w ON w.id = fa.id
        ORDER BY w.id, RANDOM();
        """, nativeQuery = true)
    List<Object[]> fetchGameDataRaw();

    @Query(value = "SELECT * FROM word ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Word getRandomWord();
}

package com.QA.quiz_app.repository;

import com.QA.quiz_app.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Integer> {
    List<Question> findAll();

    @Query(value="SELECT * FROM question ORDER BY RANDOM() LIMIT:number", nativeQuery = true)
    List<Question> findRandomQuestions(int number);
}

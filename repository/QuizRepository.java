package com.QA.quiz_app.repository;

import com.QA.quiz_app.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz,Integer> {
}

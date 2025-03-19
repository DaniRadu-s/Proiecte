package com.QA.quiz_app.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String question_text;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String correct_answer;
}
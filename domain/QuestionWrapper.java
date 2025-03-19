package com.QA.quiz_app.domain;

import lombok.Data;

@Data
public class QuestionWrapper {
    private Integer id;
    private String question_text;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;

    public QuestionWrapper(Integer id, String question_text, String answer1, String answer2, String answer3, String answer4) {
        this.id = id;
        this.question_text = question_text;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }
}

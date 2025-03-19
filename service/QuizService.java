package com.QA.quiz_app.service;

import com.QA.quiz_app.domain.Question;
import com.QA.quiz_app.domain.QuestionWrapper;
import com.QA.quiz_app.domain.Quiz;
import com.QA.quiz_app.domain.Response;
import com.QA.quiz_app.repository.QuestionRepository;
import com.QA.quiz_app.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

    public ResponseEntity<String> createQuiz(int number, String title) {
        Quiz quiz = new Quiz();
        List<Question> questions = questionRepository.findRandomQuestions(number);
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizRepository.save(quiz);
        return new ResponseEntity<>("Created",HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(int id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        List<Question> list = quiz.get().getQuestions();
        List<QuestionWrapper> listforusers = new ArrayList<>();
        for (Question question : list) {
            QuestionWrapper qw = new QuestionWrapper(id, question.getQuestion_text(), question.getAnswer1(), question.getAnswer2(), question.getAnswer3(), question.getAnswer4());
            listforusers.add(qw);
        }
        return new ResponseEntity<>(listforusers,HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Quiz quiz = quizRepository.findById(id).get();
        List<Question> questions = quiz.getQuestions();
        int correct = 0;
        int i = 0;
        for(Response response : responses) {
            if(response.getResponse().equals(questions.get(i).getCorrect_answer()))
                correct++;
            i++;
        }
        return new ResponseEntity<>(correct,HttpStatus.OK);
    }
}

package project.domain.mbti.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import project.domain.mbti.MbtiQuestion;
import project.domain.mbti.Step;
import project.domain.mbti.dto.MbtiQuestionResponse.AnswerInfo;
import project.domain.mbti.dto.MbtiQuestionResponse.QuestionInfo;
import project.domain.mbti.dto.MbtiQuestionResponse.QuestionInfoList;

public abstract class MbtiQuestionConverter {

    public static QuestionInfoList toQuestionInfoList(List<MbtiQuestion> questionList) {

        Map<Integer, List<MbtiQuestion>> groupedByQuestion = questionList.stream()
            .collect(Collectors.groupingBy(MbtiQuestion::getQuestionId));

        List<QuestionInfo> questionInfos = groupedByQuestion.values().stream()
            .map(MbtiQuestionConverter::toQuestionInfo)
            .collect(Collectors.toList());

        return QuestionInfoList.builder()
            .questions(questionInfos)
            .build();

    }

    public static QuestionInfo toQuestionInfo(List<MbtiQuestion> questions) {
        MbtiQuestion question = questions.get(0);

        List<AnswerInfo> answers = questions.stream()
            .map(MbtiQuestionConverter::toAnswerInfo)
            .collect(Collectors.toList());

        return QuestionInfo.builder()
            .id(question.getQuestionId())
            .content(question.getQuestion())
            .answers(answers)
            .build();
    }

    public static AnswerInfo toAnswerInfo(MbtiQuestion question) {
        return AnswerInfo.builder()
            .answer(question.getAnswer())
            .isResult(Step.checkEnd(question.getStep()))
            .nextQuestionId(question.getNextQuestionId())
            .build();
    }
}

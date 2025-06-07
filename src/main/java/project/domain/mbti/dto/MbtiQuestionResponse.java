package project.domain.mbti.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class MbtiQuestionResponse {

    /*
        피부 질문정보들을 담은 리스트
     */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionInfoList {
        List<QuestionInfo> questions;
    }

    /*
        피부 질문 상세질문
     */
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class QuestionInfo {

        int id;
        String content;
        List<AnswerInfo> answers;
    }

    /*
        해당 질문에 대한 답변
     */
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class AnswerInfo {

        int nextQuestionId;
        String answer;
        private Boolean isResult;
    }
}

package project.domain.mbti.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class MbtiQuestionResponse {


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionListDTO {
        private String type;
        List<QuestionInfoDTO> questions;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionInfoDTO {

        private Long id;
        private String question;
        private OptionDTO optionO;
        private OptionDTO optionX;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDTO {

        private Long nextQuestionId;
        private String text;
        private String value;
        private boolean isResult;
    }
}

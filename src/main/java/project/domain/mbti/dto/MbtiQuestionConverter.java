package project.domain.mbti.dto;

import static project.global.util.QuestionUtil.findEnumByCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import project.domain.mbti.MbtiQuestion;
import project.domain.mbti.Step;
import project.domain.mbti.dto.MbtiQuestionResponse.OptionDTO;
import project.domain.mbti.dto.MbtiQuestionResponse.QuestionInfoDTO;
import project.domain.mbti.dto.MbtiQuestionResponse.QuestionListDTO;

public abstract class MbtiQuestionConverter {

    public static List<QuestionListDTO> toQuestionListDTO(List<MbtiQuestion> questions) {
        return questions.stream()
            .collect(Collectors.groupingBy(q -> q.getAxis().name()))
            .entrySet().stream()
            .map(entry -> {
                String axisType = entry.getKey();
                List<MbtiQuestion> groupedQuestions = entry.getValue();

                return QuestionListDTO.builder().questions(toQuestionInfoDTO(groupedQuestions))
                    .type(axisType).build();
            })
            .toList();
    }

    public static List<QuestionInfoDTO> toQuestionInfoDTO(List<MbtiQuestion> questions) {
        return questions.stream()
            .map(question -> QuestionInfoDTO.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .optionO(toOptionODTO(question))
                .optionX(toOptionXDTO(question))
                .build()).toList();

    }

    public static OptionDTO toOptionODTO(MbtiQuestion question) {
        return OptionDTO.builder()
            .value(question.getOptionONextId() < 0 ? findEnumByCode(question.getOptionONextId())
                : question.getOptionOText())
            .nextQuestionId(question.getOptionONextId())
            .isResult(question.getOptionONextId() < 0)
            .text(question.getOptionOText())
            .build();
    }

    public static OptionDTO toOptionXDTO(MbtiQuestion question) {
        return OptionDTO.builder()
            .value(question.getOptionXNextId() < 0 ? findEnumByCode(question.getOptionXNextId())
                : question.getOptionXText())
            .nextQuestionId(question.getOptionXNextId())
            .isResult(question.getOptionXNextId() < 0)
            .text(question.getOptionXText())
            .build();
    }

}

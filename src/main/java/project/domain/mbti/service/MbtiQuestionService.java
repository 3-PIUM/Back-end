package project.domain.mbti.service;

import java.util.FormatterClosedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.mbti.MbtiQuestion;
import project.domain.mbti.SkinAxis;
import project.domain.mbti.dto.MbtiQuestionConverter;
import project.domain.mbti.dto.MbtiQuestionResponse;
import project.domain.mbti.dto.MbtiQuestionResponse.QuestionInfoList;
import project.domain.mbti.repository.MbtiQuestionRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbtiQuestionService {

    private final MbtiQuestionRepository mbtiQuestionRepository;

    public ApiResponse<QuestionInfoList> getQuestionInfoList(String axis) {

        List<MbtiQuestion> byAxis = mbtiQuestionRepository.findByAxisOrderByStartFirst(SkinAxis.valueOf(axis));
        QuestionInfoList questionInfoList = MbtiQuestionConverter.toQuestionInfoList(byAxis);

        return ApiResponse.onSuccess(questionInfoList);
    }
}

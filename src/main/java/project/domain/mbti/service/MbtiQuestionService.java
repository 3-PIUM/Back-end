package project.domain.mbti.service;

import java.util.Arrays;
import java.util.FormatterClosedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.mbti.MbtiQuestion;
import project.domain.mbti.SkinAxis;
import project.domain.mbti.dto.MbtiQuestionConverter;
import project.domain.mbti.dto.MbtiQuestionResponse;
import project.domain.mbti.dto.MbtiQuestionResponse.QuestionListDTO;
import project.domain.mbti.repository.MbtiQuestionRepository;
import project.domain.member.Member;
import project.domain.member.enums.Language;
import project.domain.member.repository.MemberRepository;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbtiQuestionService {

    private final MbtiQuestionRepository mbtiQuestionRepository;
    private final MemberRepository memberRepository;

    public ApiResponse<List<QuestionListDTO>> getMbtiQuestionInfoList(String lang, Member member) {

        memberRepository.findById(member.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.MAIL_NOT_FOUND));
        // 스킨타입이 있으면 스킨타입 질문을 제외하고 보내준다.
        // 스키타입이 없으면 스키타입 질문을 포함해서 보내준다.
        if (member.getSkinType() == null) {
            List<MbtiQuestion> questionByALL = getQuestionByALL(member.getLang());
            List<QuestionListDTO> questionListDTO = MbtiQuestionConverter.toQuestionListDTO(
                questionByALL);
            return ApiResponse.onSuccess(questionListDTO);
        } else {
            List<MbtiQuestion> questionByAxis = getQuestionByAxis(member.getLang());
            List<QuestionListDTO> questionListDTO = MbtiQuestionConverter.toQuestionListDTO(
                questionByAxis);
            return ApiResponse.onSuccess(questionListDTO);
        }
    }

    public ApiResponse<List<QuestionListDTO>> getSkinTypeQuestion(String lang) {
        Language language = Language.getLanguage(lang);
        List<MbtiQuestion> questionBySkinType = getQuestionBySkinType(language);
        return ApiResponse.onSuccess(MbtiQuestionConverter.toQuestionListDTO(questionBySkinType));
    }

    public List<MbtiQuestion> getQuestionByALL(Language lang) {
        return mbtiQuestionRepository.findByAllQuestions(lang);
    }

    public List<MbtiQuestion> getQuestionBySkinType(Language lang) {
        return mbtiQuestionRepository.findBySkinQuestion(lang);
    }

    public List<MbtiQuestion> getQuestionByAxis(Language lang) {
        return mbtiQuestionRepository.findByAxisQuestions(lang);
    }

}

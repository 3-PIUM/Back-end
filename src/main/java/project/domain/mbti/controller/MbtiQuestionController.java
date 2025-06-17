package project.domain.mbti.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.domain.mbti.dto.MbtiQuestionResponse.QuestionListDTO;
import project.domain.mbti.service.MbtiQuestionService;
import project.domain.member.Member;
import project.global.response.ApiResponse;
import project.global.security.annotation.LoginMember;

@Tag(name = "피부 MBTI API", description = "피부 MBTI 관련 기능입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mbti")
public class MbtiQuestionController {

    private final MbtiQuestionService mbtiQuestionService;

    @Operation(
        summary = "피부 Mbti 테스트 질문 조회",
        description = "mbit 테스트 질문을 축별로 조회를 합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/questions")
    public ApiResponse<List<QuestionListDTO>> getMbtiQuestion(
        @LoginMember Member member,
        @RequestParam String lang
    ) {
        return mbtiQuestionService.getMbtiQuestionInfoList(lang,member);
    }

    @Operation(
        summary = "피부타입 Mbti 질문 조회",
        description = "피부타입 설문을 조회를 합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/skin-questions")
    public ApiResponse<List<QuestionListDTO>> getSkinTypeQuestion(
        @RequestParam String lang
    ) {
        return mbtiQuestionService.getSkinTypeQuestion(lang);
    }

}

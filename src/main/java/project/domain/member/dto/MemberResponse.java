package project.domain.member.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public abstract class MemberResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailInfoDTO {

        public String nickname;

        public String profileImg;

        public String email;

        public LocalDate birth;

        public String gender;

        public String skinType;

        public String personalType;

        // 사용자의 mbti에 관한 코드
        public String mbtiCode;

        public String area;

        public String language;

        public List<Integer> skinIssue;
    }

}

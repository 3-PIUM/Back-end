# Back-end

## 작업 FLOW

### 마일스톤 생성

- 주 마다 마일스톤 생성
- 이번주에 해야 할일 정하고 나누고, 다같이 todo 만들기

### issue 생성

- 각자 만든 todo에 따라 issue 생성
    - 제목 : `[{태그}]:{간단한 제목}`
- project -> todo로 issue 생성
    - assignment, label, 해당 주차 milstone 설정하기

#### 작업에 해당하는 브랜치 만들기

#### Todo 에서 In Progress 로 옮기고 작업시작

## 브랜치 관리

### 브랜치 naming rule

```
feature/{issue 번호}  -> 각자 기능 개발할 때 사용, 로컬에서 각자 테스트

	(Squash and Merge)

hotfix/{issue 번호} -> merge후 오류 수정, 혹은 api 연동시 빠르게 수정하는 경우

develop  -> 개발한 기능을 병합, 로컬에서 테스트 진행

	(Release 해당 Develop 브랜치에서 생성)

release  -> develop 브랜치를 로컬에서 실행 시 문제가 없으면 해당 커밋에 release 브랜치 생성
-> 개발시 테스트용 서버로 배포

	(Commit and Merge)

main  -> 실제 운영할 서버로 배포, release에서 QA 후 main으로 merge
```

### pull request

- pr 이름은 issue랑 똑같이
- 대신 []안에 티켓번호(이슈 번호도 붙이기)
    - ex. [Docs-10] : github pr, commit 컨벤션 관련 README 작성

#### review

- line by line 으로 읽으면서 comment
- 초반에 코드 와르르 올라올때는 진짜 제대로 review 할것
- approve 4개 이상 못받으면 merge 불가

#### merge

- merge는 develop 브랜치를 타켓으로 `squash and merge`
- 만든 브랜치는 삭제

## code convention

### 주석 규칙 - 권장

[좋은 주석과 나쁜 주석](https://velog.io/@hangem422/clean-code-comment)

### 구글의 Java 스타일 기반으로 Code Style 구성

1. **main/resource/style 디렉토리 아래 .xml file 적용**
- xml 파일은 google java code convention
- Setting -> Code Style의 scheme를 GoogleStyle로 적용
2. **Setting -> Code style -> Java 수정**
- Tab Size -> 4
- Indent -> 4
- Continuation Indent -> 8
3. **Setting -> Tools -> Action on Save**
- reformat code 체크
- optimazation imports 체크

### 디렉토리 구조

```bash
(예시)

├── 🗂com.payroad
   ├── domain
   |   ├── member
   |   |   ├── controller
   |   |   ├── dto
   |   |   ├── entity
   |   |   └── service
   |   |
   |   └── budget
   |       ├── controller
   |       ├── dto
   |       ├── entity
   |       └── service
   |   
   └── global
       ├── config(설정)
       ├── response(응답관련)
       ├── common(도메인 공통 사용)
			 ├── S3
       └── util(유틸, 날짜, 스트링변환등)
```

### Java 코딩 스타일 가이드

[](http://developer.gaeasoft.co.kr/development-guide/java-guide/java-coding-style-guide/)

### 중괄호 스타일 (K&R) - Java 표준

```bash
if (condition) 
{
  // 이거 말고 X
} 
if (condition) {
  // 이거로 O
} 
```

### 클래스, 인터페이스 이름

- 클래스 이름
    - Customer
    - SalesOrder
- 패키지 이름
    - 소문자만 사용
- 변수 이름
    - camelCase

### DTO 이름

- 뒤에 붙이는 규칙

```markdown
단일+요약 조회 : SimpleInfoDTO - 단일 게시글의 일부만 반환할 때
단일+상세 조회 : DetailInfoDTO - 단일 게시글의 모든 데이터를 반환할 때
리스트+요약 조회 : SimpleListDTO - 다중 게시글 각각의 일부만 반환할 때
리스트+상세 조회 : DetailListDTO - 다중 게시글 각각의 모든 데이터를 반환할 때
```

- 도메인 별로 Response, Request DTO 클래스 파일 만들고 안에 nested static class 로 DTO 생성
- 인스턴스 생성 방지를 위해 `abstract` 추상클래스로 선언

```java
public abstract class MemberResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleInfoDTO {

        private String name;
        private String nickname;
        private String gender;
        private String birthday;
    }
}
```

- Converter 활용
- 인스턴스 생성 방지를 위해 `abstract` 추상클래스로 선언

```java
public abstract MemberConverter {
    public static Member toMember (
            String clientId,
            MemberRequestDTO.SignUpRequestDTO signUpRequestDTO){

        return Member.builder()
                .clientId(clientId)
                .socialType(ClientIdMaker.getSocialTypeInClientId(clientId))
                .role(Role.USER)
                .name(signUpRequestDTO.getName())
                .nickname(signUpRequestDTO.getNickname())
                .gender(Gender.MALE)
                .birthDay(signUpRequestDTO.getBirthday())
                .persona(signUpRequestDTO.getPersona())
                .build();
    }

    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO (Member member){

        return MemberResponseDTO.MemberInfoDTO.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .gender(member.getGender().toString())
                .birthday(member.getBirthDay().toString())
                .persona(member.getPersona())
                .build();
    }

    // ...
}
```

### 컨트롤러, 서비스 메소드명

- 메소드에 사용되는 단어, CRUD를 뜻하는 단어는 동사를 사용!!!
- CRUD단어는 접두사로!
- (crud명)- (도메인) - (suffix)
    - CRUD명 컨벤션
        - **등록 및 작성**
            - create
        - **수정**
            - 완전 수정 → update
            - 일부 수정 → change
        - **삭제**
            - 논리 삭제 → remove
            - 물리 삭제 → delete
        - **조회**
            - get사용
            - 조회는 너무 다양하고 복잡(목록 조회, 상세 조회,간단 조회 등등) → 접미사로 구분
            - 목록조회는 LIST를 접미사로!
                - getUserList
                - getRoomList
            - 단일항목은 항목 이름을 접미사로!
                - getUser
                - getRoom
            - 최소한의 데이터 호출시 SIMPLE을 접미사로!
                - getUserSimple
                - getRoomSimple

### 컨트롤러 규칙

- Controller에서 http메소드와 api명 명시
- service에서는 복잡한 로직, 혹은 다른 함수에서 사용되는 함수일 경우에 //를 통한 기입
- 파라미터는 두번째 줄에 작성
- 파라미터는 하나당 한 라인 사용

```java

@PostMapping("/sign-up")
public ApiResponse<TokenInfo> register(
        @Valid @RequestBody RegisterMember registerMember) {
    return ApiResponse <>(memberService.registerMember)
```

### Restful Api 설계 규칙

https://dev-cool.tistory.com/32

- 엔티티만 해당 폴더에 두고, 나머지는 각 기능별로 패키지 감싸기
- 로그인 → 리프레시 토큰 저장
- ApiResponse로 감싸서 반환
- 400 BAD REQUEST로 통일
- ENUM의 사용 주의
- string 요구사항 추가되는 경우에 db에서도 추가해줘야 함
- Static 상수는 각 상수 맨 위에 Upper Case


## Commit Convention

### header

#### 태그

태그 : 제목의 형태이며, : 뒤에만 space 사용

```
feat : 새로운 기능 추가
fix : 버그 수정
docs : 문서 수정
style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
refactor : 코드 리펙토링
test : 테스트 코드, 리펙토링 테스트 코드 추가
chore : 빌드 업무 수정, 패키지 매니저 수정
```

#### subject

최대 50글자가 넘지 않도록
간략 한 설명을 적을것

### body

본문은 다음의 규칙을 지킨다.

본문은 한 줄 당 72자 내로 작성한다.
본문 내용은 양에 구애받지 않고 최대한 상세히 작성한다.
본문 내용은 어떻게 변경했는지 보다 무엇을 변경했는지 또는 왜 변경했는지를 설명

### footer

선택사항
꼬리말은 "유형: #이슈 번호" 형식으로 사용
여러 개의 이슈 번호를 적을 때는 쉼표(,)로 구분
이슈 트래커 유형은 다음 중 하나를 사용한다.

- Fixes: 이슈 수정중 (아직 해결되지 않은 경우)
- Resolves: 이슈를 해결했을 때 사용
- Ref: 참고할 이슈가 있을 때 사용
- Related to: 해당 커밋에 관련된 이슈번호 (아직 해결되지 않은 경우)

#### Example

```
docs: commit convention 문서화

commit convention을 문서화 하였습니다.

Resolves: #10
```

---
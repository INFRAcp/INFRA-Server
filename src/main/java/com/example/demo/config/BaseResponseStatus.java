package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    // x300 : 프로젝트 관련
    SEARCH_LENGTH_ERROR(false, 1300, "검색어 2글자 이상 입력해주세요."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false, 2003, "권한이 없는 유저의 접근입니다."),
    EXPIRATION_REFRESH_JWT(false, 2004, "리프레시 토큰이 만료되었습니다."),
    EXPIRATION_ACCESS_JWT(false, 2005, "엑세스 토큰이 만료되었습니다."),
    REQUEST_BODY_FORMAT_ERROR(false, 2006, "JSON format으로 BODY를 전송하세요."),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED(false, 2007, "지원하지 않는 Media Type입니다."),


    // users (2100~)
    USERS_EMPTY_USER_ID(false, 2100, "유저 아이디 값을 확인해주세요."),
    REQUEST_EMPTY(false, 2004, "필수 정보가 비었습니다."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2101, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2102, "이메일 형식을 확인해주세요."),
    POST_USERS_INVALID_ID(false, 2103, "아이디 형식을 확인해주세요"),
    POST_USERS_INVALID_PW(false, 2104, "비밀번호 형식을 확인해주세요"),
    POST_USERS_INVALID_PHONE(false, 2106, "핸드폰 번호 형식을 확인해주세요."),

    POST_USERS_EMPTY_INFO(false, 2107, "필수 정보가 비어있습니다."),
    POST_USERS_ALREADY_IN(false, 2108, "이미 회원가입이 완료된 사용자입니다."),

    // 소개페이지 (2200~)
    POST_USERS_PROFILE_EMPTY_INFO(false, 2201, "소개 페이지의 필수 정보가 비어있습니다."),
    POST_USER_PROFILE_MIN_PROFILE(false, 2202, "소개글은 최소 10자 이상 입력해주세요."),
    POST_USER_PROFILE_MIN_ABILITY(false, 2203, "능력은 최소 1글자 이상 입력해주세요."),
    POST_USERS_PROFILE_KEYWORD_COUNT(false, 2204, "키워드는 최대 6개까지 입력할 수 있습니다."),
    POST_USERS_PROFILE_KEYWORD_WORD_COUNT(false, 2205, "키워드의 글자수는 최소 1글자, 최대 5글자로 작성해주세요."),

    INVALID_ODER_PRPHOTO(false, 2211, "프로필 사진은 등록 및 삭제만 가능합니다 (user_prPhoto key 값을 확인해주세요)"),

    // 프로젝트 (2300~)
    // Project 프로젝트 등록시 빈값 에러
    POST_PROJECT_EMPTY_HEADER(false, 2310, "프로젝트 제목을 입력해주세요."),
    POST_PROJECT_EMPTY_FIELD(false, 2311, "프로젝트 분야를 입력해주세요."),
    POST_PROJECT_EMPTY_CONTENT(false, 2312, "프로젝트 내용을 입력해주세요."),
    POST_PROJECT_EMPTY_SUBFIELD(false, 2314, "프로젝트 세부 분야를 입력해주세요."),
    POST_PROJECT_EMPTY_PROGRESS(false, 2315, "프로젝트 진도를 입력해주세요."),
    POST_PROJECT_EMPTY_END_TERM(false, 2316, "프로젝트 종료 시점을 입력해주세요."),
    POST_PROJECT_EMPTY_START_TERM(false, 2317, "프로젝트 시작 시점을 입력해주세요."),
    POST_PROJECT_EMPTY_DEADLINE(false, 2318, "프로젝트 모집 마감일을 입력해주세요."),
    POST_PROJECT_EMPTY_TOTAL_PERSON(false, 2319, "프로젝트 모집 인원을 입력해주세요."),


    POST_PROJECT_COINCIDE_CHECK(false, 2320, "이미 지원한 프로젝트입니다."),

    //프로젝트 등록시 키워드 글자수 초과
    POST_PROJECT_KEYWORD_EXCEED(false, 2321, "키워드 5글자를 초과하였습니다."),
    POST_PROJECT_KEYWORD_CNT_EXCEED(false, 2322, "키워드 6개를 초과하였습니다."),
    POST_PROJECT_HASHTAG_DUPLICATION(false, 2323, "중복 키워드가 존재합니다."),

    //프로젝트 날짜 오류
    POST_PROJECT_DEADLINE_BEFORE_START(false, 2323, "예상 시작일이 마감일 이전에 있습니다."),
    POST_PROJECT_END_BEFORE_START(false, 2324, "예상 종료일이 예상 시작일보다 이전에 있습니다."),

    //거절된 프로젝트 재지원
    POST_PROJECT_REJECT_RESTART(false, 2325, "거절된 프로젝트입니다."),

    //프로젝트에 지원한 사람이 아무도 없을 때
    GET_PROJECT_APPLY_LIST_NULL(false, 2340, "지원한 사람이 없습니다."),

    //프로젝트에 승인된 유저가 아무도 없을 때
    POST_PROJECT_GETTEAM_NULL(false, 2341, "승인된 팀원이 없습니다."),
    // 팀원 평가
    // [Post] /evaluate
    POST_PROJECT_EVALUATE_SCORE(false, 2350, "평가 점수 범위는 0 ~ 5 이어야 합니다."),
    POST_PROJECT_EVALUATE_EMPTY(false, 2351, "필수 요소가 비었습니다."),

    // 고객센터 (2500~)
    // 신고(report)
    POST_REPORTS_EMPTY_INFO(false, 2501, "신고 접수에 필요한 모든 항목을 작성해주세요."),
    POST_REPORTS_DELETE_ERROR(false, 2502, "신고 철회 요청에 실패하였습니다."),

    // 질의응답(QA)
    DELETE_FAIL_QA(false, 2511, "질문 삭제 실패"),
    MODIFY_FAIL_QA(false, 2512, "질문 수정 실패"),
    MODIFY_FAIL_ANSWER(false, 2513, "질문 답변 실패"),
    INVALID_AUTHORITY_ANSWER(false, 2514, "질문 답변 권한이 없습니다. (관리자만 답변이 가능합니다.)"),
    QA_REQUEST_BODY_EMPTY(false, 2515, "필수 정보가 비어있습니다."),

    //SMS 관련 (2600~)
    POST_SMS_PHONEFORM_ERROE(false, 2601, "전화번호 형식이 알맞지 않습니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // User 관련 (3100~)
    NOT_EXISTS_EMAIL(false, 3100, "해당하는 이메일 정보가 없습니다."),
    NOT_EXISTS_USER_ID(false, 3101, "해당하는 아이디가 없습니다."),
    POST_USERS_EXISTS_ID(false, 3102, "중복된 아이디입니다."),
    POST_USERS_EXISTS_PHONE(false, 3103, "중복된 핸드폰 번호입니다."),
    POST_USERS_EXISTS_NICKNAME(false, 3104, "중복된 닉네임 입니다."),
    POST_USERS_EXISTS_EMAIL(false, 3105, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false, 3106, "없는 아이디거나 비밀번호가 틀렸습니다."),

    // 회원가입 관련
    FAILED_TO_SIGNUP_DEL_USER(false, 3116, "탈퇴한 후 일주일 후에 가입 가능합니다."),
    FAILED_TO_SIGNUP_OUT_USER(false, 3117, "강제 탈퇴된 사용자로 3개월 후에 가입 가능합니다."),
    FAILED_TO_SIGNUP_ALREADY_USER(false, 3118, "이미 인프라의 회원입니다."),

    // 소개페이지 관련
    POST_USER_PROFILE_SAME_ABILITY(false, 3121, "중복된 능력(ability)은 입력할 수 없습니다."),
    POST_USER_PROFILE_SAME_LINK(false, 3122, "중복된 링크(link)는 입력할 수 없습니다."),
    POST_USER_PROFILE_SAME_KEYWORD(false, 3123, "중복된 키워드(keyword)는 입력할 수 없습니다."),

    // 관심분야
    EMPTY_INTEREST_CATEGORY(false, 3150, "설정한 관심분야가 없습니다."),

    // 프로젝트 (3300~)
    // 팀원 평가
    PROJECT_EVALUATE_AUTHORITY(false, 3351, "평가 권한이 없습니다. (프로젝트에 참여해야 평가 권한이 주어집니다.)"),
    PROJECT_MEMBER(false, 3352, "해당 프로젝트의 참여 인원이 아닙니다. 프로젝트 번호와 인원의 이름을 확인해주세요."),
    PROJECT_EVALUATE(false, 3353, "존재하지 않는 평가입니다."),

    // QA
    NOT_EXIST_QA(false, 3501, "존재하지 않는 질문입니다."),
    FAIL_TO_CREATE_QA(false, 3502, "질문 등록에 실패하셨습니다."),

    //SMS 관련 (3600~)
    DUPLICATED_PHONE(false, 3601, "해당번호로 이미 가입하였습니다."),


    // 프로젝트 신청 유저 승인 관련
    PROJECT_APPROVE_AUTHORITY(false, 3360, "승인 권한이 없습니다. (프로젝트 팀장 권한)"),
    PROJECT_INVITESTATUS_ALREADY(false, 3361, "이미 승인한 유저입니다."),
    PROJECT_INVITESTATUS_REJECT(false, 3362, "이미 거절한 유저입니다."),
    PROJECT_KICK_OUT(false, 3363, "팀원이 아닙니다. (팀원만 강퇴가 가능합니다.)"),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    EMAIL_ERROR(false, 4002, "서버 전용 이메일에 문제가 발생했습니다."),
    EMAIL_AUTH_ERROR(false, 4003, "서버 전용 이메일 계정 인증에 실패했습니다."),
    URI_NOT_EXIST(false, 4004, "존재하지 않는 URI입니다."),
    METHOD_NOT_EXIST(false, 4005, "사용할 수 없는 Method입니다."),

    //[PATCH] /users/{userIdx} (4010~)
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),
    MODIFY_FAIL_USERNAME(false, 4014, "유저네임 수정 실패"),
    MODIFY_FAIL_USERPW(false, 4015, "비밀번호 수정 실패");


    // 5000 : 필요시 만들어서 쓰세요


    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

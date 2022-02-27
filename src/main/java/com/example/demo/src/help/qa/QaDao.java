package com.example.demo.src.help.qa;

import com.example.demo.src.help.qa.model.GetQaRes;
import com.example.demo.src.help.qa.model.PatchAnswerReq;
import com.example.demo.src.help.qa.model.PatchQaReq;
import com.example.demo.src.help.qa.model.PostQaReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class QaDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 질문 리스트 가져오기
     *
     * @param
     * @return List<질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status></질문>
     * @author shinhyeon
     */
    public List<GetQaRes> getQaAll() {
        String getQaQuery = "select * from QA where qa_status!='삭제'";

        return this.jdbcTemplate.query(getQaQuery,
                (rs, rowNum) -> new GetQaRes(
                        rs.getInt("qa_num"),
                        rs.getString("user_id"),
                        rs.getString("qa_q"),
                        rs.getString("qa_a"),
                        rs.getString("qa_aTime"),
                        rs.getString("qa_qTime"),
                        rs.getString("qa_status")
                )
        );
    }

    /**
     * 특정 사용자의 질문 목록 조회
     *
     * @param user_id
     * @return List<질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status></질문>
     * @author shinhyeon
     */
    public List<GetQaRes> getQaByUserId(String user_id) {
        String getQaByUserIdQuery = "select * from QA Where user_id = ? and qa_status!='삭제'";
        String getQaByUserIdParam = user_id;

        return this.jdbcTemplate.query(getQaByUserIdQuery,
                (rs, rowNum) -> new GetQaRes(
                        rs.getInt("qa_num"),
                        rs.getString("user_id"),
                        rs.getString("qa_q"),
                        rs.getString("qa_a"),
                        rs.getString("qa_aTime"),
                        rs.getString("qa_qTime"),
                        rs.getString("qa_status")
                ),
                getQaByUserIdParam);
    }

    /**
     * 특정 QA의 user_id 가져오기
     *
     * @param qa_num
     * @return String
     * @author yunhee
     */
    public String getUserIdByQaNum(int qa_num) {
        String query = "select user_id from QA where qa_num=? and qa_status!='삭제'";
        return this.jdbcTemplate.queryForObject(query, String.class, qa_num);
    }

    /**
     * 해당 qa_num을 갖는 질문 수정
     *
     * @param qa_num
     * @param patchQaReq
     * @return int
     * @author shinhyeon
     */
    public int modifyQa(int qa_num, PatchQaReq patchQaReq) {
        String modifyQaQuery = "update QA set qa_q = ?, qa_qTime = now() where qa_num = ?";
        Object[] modifyQaParam = new Object[]{patchQaReq.getQa_q(), qa_num};

        return this.jdbcTemplate.update(modifyQaQuery, modifyQaParam);
    }

    /**
     * 특정 질문 조회
     *
     * @param qa_num
     * @return 질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status
     * @author shinhyeon
     */
    public GetQaRes getQaByQaNum(int qa_num) {
        String getQaByQaNumQuery = "select * from QA Where qa_num = ? and qa_status!='삭제'";
        int getQaByQaNumParam = qa_num;

        return this.jdbcTemplate.queryForObject(getQaByQaNumQuery,
                (rs, rowNum) -> new GetQaRes(
                        rs.getInt("qa_num"),
                        rs.getString("user_id"),
                        rs.getString("qa_q"),
                        rs.getString("qa_a"),
                        rs.getString("qa_aTime"),
                        rs.getString("qa_qTime"),
                        rs.getString("qa_status")
                ),
                getQaByQaNumParam);
    }

    /**
     * 질문 등록
     *
     * @param postQaReq (user_id, qa_q)
     * @return int
     * @author shinhyeon
     */
    public int createQa(PostQaReq postQaReq) {
        String uploadQaQuery = "insert into QA (user_id, qa_q, qa_status) VALUES (?,?,?)";
        Object[] uploadQaParam = new Object[]{postQaReq.getUser_id(), postQaReq.getQa_q(), "등록"};

        return this.jdbcTemplate.update(uploadQaQuery, uploadQaParam);
    }

    /**
     * 질문 삭제 : qa_status를 '삭제'로 변경
     *
     * @param qa_num
     * @return int
     * @author shinhyeon
     */
    public int deleteQa(int qa_num) {
        String modifyQaQuery = "update QA set qa_status = ? where qa_num = ?";
        Object[] modifyQaParam = new Object[]{"삭제", qa_num};

        return this.jdbcTemplate.update(modifyQaQuery, modifyQaParam);
    }

    /**
     * 해당 qa_num에 해당하는 답변
     *
     * @param qa_num, patchAnswerReq
     * @return int
     * @author shinhyeon
     */
    public int answerQa(int qa_num, PatchAnswerReq patchAnswerReq) {
        String answerQaQuery = "update QA set qa_a = ?, qa_aTime = now() where qa_num = ?";
        Object[] answerQaParam = new Object[]{patchAnswerReq.getQa_a(), qa_num};
        return this.jdbcTemplate.update(answerQaQuery, answerQaParam);
    }
}
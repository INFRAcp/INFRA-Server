package com.example.demo.src.help.qa;

import com.example.demo.src.help.qa.model.GetQaRes;
import com.example.demo.src.help.qa.model.PatchAnswerReq;
import com.example.demo.src.help.qa.model.PatchQaReq;
import com.example.demo.src.help.qa.model.PostQaReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Repository

public class QaDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 모든 질문 조회x
     *
     * @param
     * @return List<질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status></질문>
     * @author shinhyeon
     */
    public List<GetQaRes> getQaRes(){
        String getQaQuery = "select * from QA";

        return this.jdbcTemplate.query(getQaQuery,
                (rs, rowNum)-> new GetQaRes(
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
     * 특정 질문 조회
     *
     * @param user_id
     * @return List<질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status></질문>
     * @author shinhyeon
     */
    public List<GetQaRes> getQaByUserId(String user_id) {
        String getQaByUserIdQuery = "select * from QA Where user_id = ?";
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
     * 해당 qa_num을 갖는 질문 수정
     *
     * @param PatchQaReq (qa_num, qa_q)
     * @return int
     * @author shinhyeon
     */
    public int modifyQa(int qa_num, PatchQaReq patchQaReq){
        String qa_qtimeQuery = "SELECT now()";
        patchQaReq.setQa_qTime(this.jdbcTemplate.queryForObject(qa_qtimeQuery, Timestamp.class));

        String modifyQaQuary = "update QA set qa_q = ?, qa_qTime = ? where qa_num = ?";
        Object[] modifyQaParam = new Object[]{patchQaReq.getQa_q(), patchQaReq.getQa_qTime() , qa_num};

        return this.jdbcTemplate.update(modifyQaQuary, modifyQaParam);
    }

    /**
     * 특정 질문 조회
     *
     * @param qa_num
     * @return 질문 번호, 아이디, 질문, 답변, 질문한 시간, 답변한 시간, status
     * @author shinhyeon
     */
    public GetQaRes getQaByQaNum(int qa_num) {
        String getQaByQaNumQuery = "select * from QA Where qa_num = ?";
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
     * @param PostQaReq (user_id, qa_q)
     * @return int
     * @author shinhyeon
     */
    public int uploadQa(PostQaReq postQaReq) {
        String uploadQaQuery = "insert into QA (user_id, qa_q) VALUES (?,?)";
        Object[] uploadQaParam = new Object[]{postQaReq.getUser_id(), postQaReq.getQa_q()};

        return this.jdbcTemplate.update(uploadQaQuery, uploadQaParam);
    }

    /**
     * 질문 삭제
     *
     * @param PostQaReq (user_id, qa_q)
     * @return int
     * @author shinhyeon
     */
    public int modifyQa2(int qa_num){
        String modifyQaQuary = "update QA set qa_status = ? where qa_num = ?";
        Object[] modifyQaParam = new Object[]{"삭제", qa_num};

        return this.jdbcTemplate.update(modifyQaQuary, modifyQaParam);
    }

    /**
     * 해당 qa_num을 갖는 질문 수정
     *
     * @param qa_num, patchAnswerReq
     * @return int
     * @author shinhyeon
     */

    public int answerQa(int qa_num, PatchAnswerReq patchAnswerReq) {
        String qa_qtimeQuery = "SELECT now()";
        patchAnswerReq.setQa_aTime(this.jdbcTemplate.queryForObject(qa_qtimeQuery, Timestamp.class));

        String answerQaQuary = "update QA set qa_a = ?, qa_aTime = ? where qa_num = ?";
        Object[] answerQaParam = new Object[]{patchAnswerReq.getQa_a(), patchAnswerReq.getQa_aTime(), qa_num};

        return this.jdbcTemplate.update(answerQaQuary, answerQaParam);
    }
}
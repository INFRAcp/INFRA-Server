package com.example.demo.src.help.qa;

import com.example.demo.src.help.qa.model.GetQaRes;
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
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 모든 질문 조회
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

    // 특정 질문 조회 (해당 user_id 를 갖는)
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

    // 해당 qa_num을 갖는 질문 삭제
    public int deleteQa(int qa_num){
        String deleteQuary = "delete from QA where qa_num = ?";

        return this.jdbcTemplate.update(deleteQuary, qa_num);
    }

    // 해당 qa_num을 갖는 질문 수정
    public int modifyQa(PatchQaReq patchQaReq){
        String modifyQaQuary = "update QA set qa_q = ? where qa_num = ?";
        Object[] modifyQaParam = new Object[]{patchQaReq.getQa_q(), patchQaReq.getQa_num()};

        return this.jdbcTemplate.update(modifyQaQuary, modifyQaParam);
    }

    // 특정 질문 조회 (해당 qa_num 를 갖는)
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

    // 질문 등록
    public int uploadQa(PostQaReq postQaReq) {
        String uploadQaQuery = "insert into QA (user_id, qa_q) VALUES (?,?)";
        Object[] uploadQaParam = new Object[]{postQaReq.getUser_id(), postQaReq.getQa_q()};

        return this.jdbcTemplate.update(uploadQaQuery, uploadQaParam);
    }

    // 해당 qa_num을 갖는 질문 삭제
    public int modifyQa2(int num){
        String modifyQaQuary = "update QA set qa_status = ? where qa_num = ?";
        Object[] modifyQaParam = new Object[]{"삭제", num};

        return this.jdbcTemplate.update(modifyQaQuary, modifyQaParam);
    }
}

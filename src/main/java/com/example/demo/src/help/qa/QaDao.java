package com.example.demo.src.help.qa;

import com.example.demo.src.help.qa.model.GetQaRes;
import com.example.demo.src.help.qa.model.PatchQaReq;
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
                        rs.getInt("QA_num"),
                        rs.getString("User_id"),
                        rs.getString("QA_q"),
                        rs.getString("QA_a")
                )
        );
    }

    // 특정 질문 조회 (해당 User_id 를 갖는)
    public List<GetQaRes> getQaByUserId(String User_id) {
        String getQaByUserIdQuery = "select * from QA Where User_id = ?";
        String getQaByUserIdParam = User_id;

        return this.jdbcTemplate.query(getQaByUserIdQuery,
                (rs, rowNum) -> new GetQaRes(
                        rs.getInt("QA_num"),
                        rs.getString("User_id"),
                        rs.getString("QA_q"),
                        rs.getString("QA_a")
                ),
                getQaByUserIdParam);
    }

    // 해당 QA_num을 갖는 질문 삭제
    public int deleteQa(int QA_num){
        String deleteQuary = "delete from QA where QA_num = ?";

        return this.jdbcTemplate.update(deleteQuary, QA_num);
    }

    // 해당 QA_num을 갖는 질문 수정
    public int modifyQa(PatchQaReq patchQaReq){
        String modifyQaQuary = "update QA set Qa_q = ? where QA_num = ?";
        Object[] modifyQaParam = new Object[]{patchQaReq.getQA_q(), patchQaReq.getQA_num()};

        return this.jdbcTemplate.update(modifyQaQuary, modifyQaParam);
    }

    // 특정 질문 조회 (해당 qa_num 를 갖는)
    public List<GetQaRes> getQaByQaNum(int qa_num) {
        String getQaByQaNumQuery = "select * from QA Where QA_num = ?";
        int getQaByQaNumParam = qa_num;

        return this.jdbcTemplate.query(getQaByQaNumQuery,
                (rs, rowNum) -> new GetQaRes(
                        rs.getInt("QA_num"),
                        rs.getString("User_id"),
                        rs.getString("QA_q"),
                        rs.getString("QA_a")
                ),
                getQaByQaNumParam);
    }
}

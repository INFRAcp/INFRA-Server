package com.example.demo.src.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.config.BaseException;
import com.example.demo.src.project.ProjectDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.MODIFY_FAIL_QA;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Service {

    public final AmazonS3Client amazonS3Client;
    public final S3Dao s3Dao;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String uploadPrphoto(MultipartFile multipartFile, String dirName) throws IOException{
        File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("파일 전환 실패"));

        return uploadPrphoto(uploadFile, dirName);
    }
    // S3로 파일 업로드하기
    public String uploadPrphoto(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }
    // S3로 업로드
    public String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
    // 로컬에 저장된 이미지 지우기
    public void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }
    public Optional<File> convert(MultipartFile multipartFile) throws IOException{
        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
        // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    /**
     * 프로필 사진 업로드
     * @param imgPath
     * @param user_id
     * @author shinhyeon
     */
    public void uploadPrphoto(String imgPath, String user_id) throws BaseException {
        try {
            s3Dao.uploadPrPhoto(imgPath, user_id);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 프로젝트 사진 업로드
     * @param imgPath
     * @param pj_num
     * @throws BaseException
     * @author shinhyeon
     */
    public void uploadPjPhoto(String imgPath, int pj_num) throws BaseException {
        try {
            s3Dao.uploadPjPhoto(imgPath, pj_num);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

package project.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    private final String bucket = "pium-image-bucket";

    public String uploadFile(MultipartFile file, String dirName) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uploadFileName = dirName + "/" + UUID.randomUUID() + "_" + originalFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(
                new PutObjectRequest(bucket, uploadFileName, file.getInputStream(), metadata)
                        /*
                         .withCannedAcl(CannedAccessControlList.PublicRead) // public 읽기 권한 부여 */
        );

        return amazonS3.getUrl(bucket, uploadFileName).toString(); // 이미지 URL 반환
    }

    public List<String> uploadFiles(List<MultipartFile> files, String dirName) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadFile(file, dirName);
                uploadedUrls.add(url);
            }
        }
        return uploadedUrls;
    }

    public void deleteFile(String fileName, String dirName) {
        String key = dirName + "/" + fileName;
        amazonS3.deleteObject(bucket, key);
    }

    public String extractFileNameFromUrl(String url) {
        if (url == null || !url.contains("/")) {
            throw new IllegalArgumentException("유효하지 않은 S3 URL입니다: " + url);
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }
}

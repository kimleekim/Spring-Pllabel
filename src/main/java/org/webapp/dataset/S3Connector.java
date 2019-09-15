package org.webapp.dataset;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.awt.image.BufferedImage;
import java.io.File;

import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Component
public class S3Connector {
    @Autowired
    private AmazonS3Client amazonS3Client;
    int fileIndex = (int) Math.random();

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException, InterruptedException {
        File uploadFile = new File( multipartFile.getOriginalFilename()); multipartFile.transferTo(uploadFile);
        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        System.out.println(uploadImageUrl);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (AmazonClientException error) {
            error.printStackTrace();
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public MultipartFile convertFileDatatype(int isInsta, String photoURL, String url) throws IOException {
        MultipartFile multipartFile = null;

        if (isInsta == 1) {
            int targetNum = photoURL.indexOf("640w");
            photoURL = photoURL.substring(0, targetNum);
        }
        URL convertURL = new URL(photoURL);
        BufferedImage img = ImageIO.read(convertURL.openStream());

        String fileName = url.concat(Integer.toString(fileIndex).concat(".jpg"));
        File file = new File(fileName);
        ImageIO.write(img, "jpg", file);
        FileInputStream input = new FileInputStream(file);
        multipartFile = new MockMultipartFile(fileName, file.getName(), "image/jpg", IOUtils.toByteArray(input));

        return multipartFile;
    }
}
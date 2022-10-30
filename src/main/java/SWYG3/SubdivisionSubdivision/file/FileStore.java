package SWYG3.SubdivisionSubdivision.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    // 이미지 파일 하나 저장
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        // image.png면 확장자 png 추출
        int pos = originalFilename.lastIndexOf(".");
        String substring = originalFilename.substring(pos + 1);

        String uuid = UUID.randomUUID().toString(); // 서버에 저장하는 파일명

        String storeFileName = uuid + '.' + substring;
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);
    }

    // 이미지 파일 여러개 저장
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multiPartFile : multipartFiles) {
            if(!multiPartFile.isEmpty()) {
                UploadFile uploadFile = storeFile(multiPartFile);
                storeFileResult.add(uploadFile);
            }
        }

        return storeFileResult;
    }

}

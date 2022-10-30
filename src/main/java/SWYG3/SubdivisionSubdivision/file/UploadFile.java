package SWYG3.SubdivisionSubdivision.file;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class UploadFile {

    // 다룬 두 유저가 같은 파일명의 파일을 업로드 했을시에 시스템에 저장되는 파일이 덮어쓰기 될수 있다 그래서 파일명을 분리한다.(고객이 업로드한 파일명으로 서버 내부에 파일을 저장하면 안된다.)
    private String uploadFileName; // 고객이 업로드한 파일명
    private String storeFileName; // 시스템에 저장한 파일명

    public UploadFile() {
    }

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

}

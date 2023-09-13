import com.nevvv.green.scan.GreenImageScan;
import com.nevvv.green.scan.GreenTextScan;
import com.pojo.ScanResult;
import com.test.Appliction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;

@SpringBootTest(classes = Appliction.class)
public class ContentSafe {
    @Autowired
    private GreenTextScan textScan;
    @Autowired
    private GreenImageScan imageScan;
//    @Autowired
//    private
    @Test
    public void textTest() throws Exception {
        ScanResult result= textScan.greeTextScan("百度司马了");
        System.out.println(result);
    }
    @Test
    public void imageTest() throws Exception {
        ScanResult result= imageScan.imageScan(new FileInputStream("D:\\夜小雨\\桌面\\微信图片_20230605152431.png"));
        System.out.println(result);
    }
}

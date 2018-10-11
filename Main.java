import com.soar.analysis.FileAnalysisUtil;
import com.soar.analysis.Result;
import com.soar.analysis.SplitList;

import java.io.*;

public class Main {
    public static void main(String[] args) {

        //测试文件分割合并
        File dir = new File(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "test" + File.separator);
        File sourceFile = new File(dir, "test.zip");
        int limitSize = 4 * 1024 * 1024;//4M
        String password = "123";
        Result result = FileAnalysisUtil.split(sourceFile, dir, limitSize, "png", password);
        if (result.code == 0) {
            File targetFile = new File(dir, "out_" + sourceFile.getName());
            result = FileAnalysisUtil.merge(result.splitList, dir, targetFile);
        }
    }

}

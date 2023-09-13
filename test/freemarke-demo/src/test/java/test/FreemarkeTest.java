package test;

import com.FreemarkerApplication;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.UUID;

@SpringBootTest(classes = FreemarkerApplication.class)
public class FreemarkeTest {
    @Autowired
    private Configuration configuration;
    @Test
    public void test01() throws Exception {
        //java objects数据类型，可以是实体类型，也可以是Map
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","马蓉");
        //模板+数据生成静态页面
        //1.加载模板到内存
        Template template = configuration.getTemplate("news.ftl");
        //2.利用模板+数据生成静态页面
        String name = UUID.randomUUID().toString();
        Writer writer = new FileWriter("d:\\" + name + ".html");
        template.process(map,writer);
        writer.close();
    }
}

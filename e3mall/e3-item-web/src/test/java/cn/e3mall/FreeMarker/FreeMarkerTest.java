package cn.e3mall.FreeMarker;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerTest {
	@Test
	public void testFreeMarker() throws Exception{
		//1、创建一个模板文件
		//2、创建一个Configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		//3、设置文件模板所在的目录
		configuration.setDirectoryForTemplateLoading(new File("F:/workspaces-dingnan/JavaWeb/e3-item-web/src/main/webapp/WEB-INF/ftl"));
		//4、设置文件编码格式
		configuration.setDefaultEncoding("utf-8");
		//5、加载一个模板文件，创建一个模板对象
		Template template = configuration.getTemplate("student.ftl");
		//6、创建一个数据集，可以使Map也可以是pojo，建议Map
		Map data = new HashMap<>();
		data.put("hello", "This is my second freemarker");
		Student student = new Student("张三", 25, "北京");
		List<Student> studentList = new ArrayList<Student>();
		studentList.add(new Student("张三", 25, "北京"));
		studentList.add(new Student("李四", 26, "北京"));
		studentList.add(new Student("王五", 27, "北京"));
		studentList.add(new Student("赵柳", 28, "北京"));
		studentList.add(new Student("风气", 29, "北京"));
		studentList.add(new Student("篱笆", 23, "北京"));
		data.put("student", student);
		data.put("studentList", studentList);
		data.put("date", new Date());
		data.put("val", "666");
		//7、创建一个Writer对象，指出输出文件的位置和后缀名
		Writer out = new FileWriter("F:/GitHub/student.html");
		//8、生成静态页面
		template.process(data, out);
		//9、关闭流
		out.close();
	}
}

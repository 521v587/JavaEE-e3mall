package cn.e3mall.item.controller;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 生成静态页面Controller测试
 * @author fudin
 *
 */
@Controller
public class HtmlGenController {

	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@RequestMapping("/genHtml")
	@ResponseBody
	public String genHtml() throws Exception {
		//初始化Configuration对象
		Configuration configuration = freeMarkerConfigurer.getConfiguration();
		//初始化一个模板，创建一个模板对象
		Template template = configuration.getTemplate("hello.ftl");
		//创建一个数据对象
		Map dataModel = new HashMap<>();
		dataModel.put("hello", "hhhh");
		//创建一个Writer写入模板数据
		Writer out = new FileWriter("E:/hello2.html");
		//将数据写入模板中
		template.process(dataModel, out);
		return "OK666";
		
	}
}

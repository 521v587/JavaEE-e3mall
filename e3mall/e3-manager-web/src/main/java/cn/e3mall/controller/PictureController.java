package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;

@Controller
public class PictureController {
	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String uploadFile(MultipartFile uploadFile){
		Map map = new HashMap();
		try{
			//图片上传服务器
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
			//取文件扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			//取文件的后缀名
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
			//得到完整的图片地址和文件名
			String url = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			//补充为完整的url地址名
			//url = "http://192.168.25.133/" + url;
			url = IMAGE_SERVER_URL + url;
			map.put("error", 0);
			map.put("url", url);
			//直接返回Map，Content-Type为application/json，兼容性出现问题
			//需要将对象返回text/plain，纯文本兼容性好
			return JsonUtils.objectToJson(map);
		}catch(Exception ex){
			ex.printStackTrace();
			map.put("error", 1);
			map.put("message", "文件上传失败");
			return JsonUtils.objectToJson(map);
		}
	}
}

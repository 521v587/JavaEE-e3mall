package cn.e3mall.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

/**
 * 首页展示
 * @author fudin
 *
 */
@Controller
public class IndexController {
	@Autowired
	private ContentService contentService;
	//首页轮播图广告cid
	@Value("${CONTENT_ADVERTISEMENT_ID}")
	private Long CONTENT_ADVERTISEMENT_ID;
	
	@RequestMapping("/index")
	public String showIndex(Model model){
		List<TbContent> ad1List = contentService.getContentListByCid(CONTENT_ADVERTISEMENT_ID);
		model.addAttribute("ad1List", ad1List);
		return "index";
	}

}

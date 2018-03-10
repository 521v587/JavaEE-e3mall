package cn.e3mall.search.message;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;

/**
 * 监听添加商品的信息，接收到消息后，将商品信息同步到索引库
 * 
 * @author fudin
 *
 */
public class ItemAddMessageListener implements MessageListener {

	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private SolrServer solrServer;

	@Override
	public void onMessage(Message message) {
		try {
			//接收到添加商品的id
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			Long itemId = new Long(text);
			
			//等待商品添加提交后再进行查询
			Thread.sleep(1000);
			
			//根据id查询商品
			SearchItem searchItem = itemMapper.getItemById(itemId);
			//创建一个SolrInputDocument对象
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			// 将集合写入到Document对象
			solrServer.add(document);
			// 提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

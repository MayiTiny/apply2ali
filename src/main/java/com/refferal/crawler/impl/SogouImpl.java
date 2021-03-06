package com.refferal.crawler.impl;

import com.refferal.common.AppContext;
import com.refferal.dao.JobDescriptionDao;
import com.refferal.entity.JobDescription;
import com.refferal.enums.CompanyEnum;
import com.refferal.enums.SogouCategoryEnum;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SogouImpl extends WebCrawler {

	
	private JobDescriptionDao jobDescriptionDao;

	public boolean shouldVisit(WebURL url) {
		if (url.getURL().contains(
				"http://jobs.sogou.ourats.com/req/social/?page=")
				|| url.getURL().contains(
						"http://jobs.sogou.ourats.com/req/detail/")) {
			return true;
		}
		return false;
	}

	public void visit(Page page) {
		jobDescriptionDao = AppContext.getInstance().getBean(
				JobDescriptionDao.class);
		JobDescription jobDescroption = new JobDescription();
		String url = page.getWebURL().getURL();
		if (url.contains("http://jobs.sogou.ourats.com/req/detail/")
				&& page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			Document doc = Jsoup.parse(htmlParseData.getHtml());
			String title =  doc.getElementsByClass("position-ifo-name").get(0).text();
			String name = title.split("-")[1];
			String dept = title.split("-")[0];
			jobDescroption.setName(name);
			jobDescroption.setDepartment(dept);
			jobDescroption.setCompany(CompanyEnum.SOGOU.getCompanyId());
			Elements fonts = doc.getElementsByClass("position-ifo-xg").get(0).getElementsByTag("span");
			String location = fonts.get(0).text();
			jobDescroption.setCityId(location);
			String headCount = fonts.get(1).text().split("人")[0];
			jobDescroption.setHeadCount(Integer.valueOf(headCount));
			Elements jobDesc = doc.getElementsByClass("pre-style");
			if(jobDesc.size() == 3){
				String desc = jobDesc.get(0).html();
				desc += "</br>" + jobDesc.get(1).html();
				jobDescroption.setPostDescription(desc.replace("\n", "</br>"));
				String require = jobDesc.get(2).text();
				jobDescroption.setPostRequire(require.replace("\n", "</br>"));
			}else{
				String desc = jobDesc.get(0).html();
				jobDescroption.setPostDescription(desc.replace("\n", "</br>"));
				String require = jobDesc.get(1).text();
				jobDescroption.setPostRequire(require.replace("\n", "</br>"));
			}
			jobDescroption.setDegree("本科");
			jobDescroption.setFunctionType(SogouCategoryEnum.getCodeByName(title));
			jobDescroption.setYearsLimit("3年");
			int isExsit = jobDescriptionDao.selectExsit(jobDescroption);
			if(isExsit == 0){
				System.out.println(jobDescroption.getName() + "职位不存在,插入！");
				jobDescriptionDao.insert(jobDescroption);
			}else{
				System.out.println(jobDescroption.getName() + "职位已存在,更新！");
				jobDescriptionDao.updateById(isExsit);
			}
			
		}
	}

}

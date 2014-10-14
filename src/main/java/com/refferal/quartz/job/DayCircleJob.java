package com.refferal.quartz.job;

import org.springframework.beans.factory.annotation.Autowired;

import com.refferal.crawler.JDCrawler;

public class DayCircleJob {

	@Autowired
	private JDCrawler ali;
	
	@Autowired
	private JDCrawler baidu;
	
	public void deploy() throws Exception{
		ali.startCrawl();
		baidu.startCrawl();
	}
}
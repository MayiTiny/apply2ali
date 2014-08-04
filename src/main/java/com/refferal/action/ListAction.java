package com.refferal.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.refferal.common.PageList;
import com.refferal.entity.JobDescription;
import com.refferal.service.JobDescriptionService;

@Controller
public class ListAction {

	private static final int PAGE_SIZE = 10;
	
	@Autowired
	private JobDescriptionService listService;
	
	@RequestMapping("/list")
    public ModelAndView list(HttpServletRequest request) {
		
		ModelAndView mv = new ModelAndView("jobs");
		
		String keyword = request.getParameter("keyword");

		int offset = ServletRequestUtils.getIntParameter(request, "pager.offset", 0);
		PageList<JobDescription> jds = listService.search(keyword, offset ,PAGE_SIZE);
		mv.addObject("jds", jds);
		mv.addObject("keyword", keyword);
		
        return mv;
    }
	
}

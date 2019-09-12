package org.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.webapp.service.MainPageImpl;
import org.webapp.service.SecondPageImpl;
import org.webapp.service.ThirdPageImpl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class PageController {
    @Resource(name = "mainservice")
    private MainPageImpl mainPage;
    @Resource(name = "secondpageservice")
    private SecondPageImpl secondPage;
    @Resource(name = "thirdpageservice")
    private ThirdPageImpl thirdPage;

    @GetMapping("/")
    public String setMain(Model model) {
        String[] top3_restaurant = mainPage.getTOP3Restaurant();
        String[] top3_place = mainPage.getTOP3Place();
        String[] top3_station = mainPage.getTOP3Station();

        model.addAttribute("top3_restaurant", top3_restaurant);
        model.addAttribute("top3_place", top3_place);
        model.addAttribute("top3_station", top3_station);
        return "main";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces="text/plain;charset=UTF-8")
    public String setNextPage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model)
            throws IOException {
        String station = httpServletRequest.getParameter("station");

        if (!mainPage.isExistStation(station)) {
            httpServletResponse.setContentType("text/html; charset=UTF-8");
            PrintWriter out = httpServletResponse.getWriter();
            out.println("<script>alert('지원하지 않는 지하철역 입니다!');history.go(-1);</script>");
            out.flush();
            return "main";
        }
        else {
            model.addAttribute("station", station);
            model.addAttribute("withwho", secondPage.withWho(station));
            model.addAttribute("top5_hashtag", thirdPage.getRelatedHashtags(station));
            return "second";
        }
    }
}

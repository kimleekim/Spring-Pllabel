package org.webapp.controller;

import com.google.api.client.util.Value;
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
import java.util.List;

@Controller
public class PageController {
    @Resource(name = "mainservice")
    private MainPageImpl mainPage;
    @Resource(name = "secondpageservice")
    private SecondPageImpl secondPage;
    @Resource(name = "thirdpageservice")
    private ThirdPageImpl thirdPage;
    private String station = "";
    private String[] top3_place = new String[3];

    @GetMapping("/intro")
    public String setIntro() {
        return "loading.html";
    }

    @GetMapping("/")
    public String setMain(Model model) {
        List<String> top3_station = mainPage.getTOP3Station();
        top3_place = mainPage.getTOP3Place();
        String[] top3_restaurant = mainPage.getTOP3Restaurant();

        model.addAttribute("top3_restaurant", top3_restaurant);
        model.addAttribute("top3_place", top3_place);
        model.addAttribute("top3_station", top3_station);

        return "main.html";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces="text/plain;charset=UTF-8")
    public String setResultPage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Model model) throws IOException {
        station = httpServletRequest.getParameter("station");

        station = mainPage.setStationframe(station);
        if (!mainPage.isExistStation(station)) {
            httpServletResponse.setContentType("text/html; charset=UTF-8");
            PrintWriter out = httpServletResponse.getWriter();
            out.println("<script>alert('아직 지원하지 않는 지역 입니다! 서울시 내 범위로 재검색해주세요.');" +
                    "history.go(-1);</script>");
            out.flush();

            return "main.html";
        }
        else {
            model.addAttribute("station", station);

            model.addAttribute("likecnt", secondPage.getLikeCNT(station));
            model.addAttribute("withwho", secondPage.withWho(station));
            model.addAttribute("hotPost", secondPage.showHotPost(station, false));
            model.addAttribute("topPlace", top3_place[0]);
            model.addAttribute("foodPost", secondPage.showFoodPost(station, false));

            model.addAttribute("top10_hashtag", thirdPage.getRelatedHashtags(station));
            model.addAttribute("top10_hashtagCount", thirdPage.getRelatedHashtagCounts(station));
            model.addAttribute("byDayPostCount", thirdPage.getByDayPostGraph(station));
            model.addAttribute("thisWeekPostCount", thirdPage.getThisWeekPostGraph(station));

            return "result.html";
        }
    }

    @RequestMapping(value = "/moreplace", method = RequestMethod.POST, produces="text/plain;charset=UTF-8")
    public String setMorePlace(Model model) {
        model.addAttribute("morePost", secondPage.showHotPost(station, true));

        return "more.html";
    }

    @RequestMapping(value = "/morefood", method = RequestMethod.POST, produces="text/plain;charset=UTF-8")
    public String setMoreFood(Model model) {
        model.addAttribute("morePost", secondPage.showFoodPost(station, true));

        return "more.html";
    }

    @RequestMapping(value = "/like", method = RequestMethod.GET, produces="text/plain;charset=UTF-8")
    public void updateLike(Model model) {
        secondPage.updateLikeCNT(station);
        model.addAttribute("likecnt", secondPage.getLikeCNT(station));
    }
}
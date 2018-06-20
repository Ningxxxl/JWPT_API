package cn.ningxy.service;

import cn.ningxy.bean.Questionnaire;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description: 评教
 * @Date: 2018-06-18 00:32
 **/
public class Evaluate {
    private static final String URL_COURSE_LIST = "http://jwpt.tjpu.edu.cn/jxpgXsAction.do?oper=listWj";

    /**
     * @Author: ningxy
     * @Description: 评教
     * @params: [cookies]
     * @return: void
     * @Date: 2018/6/18 上午12:45
     */
    public void evaluate(Set<Cookie> cookie) {
        final String[] alertInfo = new String[1];
        WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setTimeout(5000);
        webClient.setAlertHandler(new AlertHandler() {
            @Override
            public void handleAlert(Page page, String s) {
//                System.out.println(s);
                alertInfo[0] = s;
            }
        });

        Iterator<Cookie> i = cookie.iterator();
        while (i.hasNext()) {
            System.out.println(i);
            webClient.getCookieManager().addCookie(i.next());
        }

        try {
            WebRequest request = new WebRequest(new URL(URL_COURSE_LIST));
            request.setHttpMethod(HttpMethod.GET);
            HtmlPage evaluateListPage = webClient.getPage(request);
            List<Questionnaire> list = parseHtml(evaluateListPage);
            System.out.println("检测到共有["+list.size()+"]门课程");

            for(Questionnaire questionnaire : list) {
                if(questionnaire.isEvaluated()) continue;   // 如果已经被评教则跳过
                DomElement domElement = evaluateListPage.getElementByName(questionnaire.getString());
                HtmlPage evaluatePage = domElement.click();

                System.out.println("课程[" + questionnaire.getContent() + "]  " + "开始处理");
//                System.out.println(evaluatePage.asXml());

                // 填写表单
                fillForm(evaluatePage);
                System.out.println("课程[" + questionnaire.getContent() + "]  " + "评价填写完成");

                // 提交表单
                evaluateListPage = submitForm(evaluatePage);
                System.out.println("课程[" + questionnaire.getContent() + "]  " + "评价提交完成");

                // 获取评教结果
                boolean evaluateResult = isEvaluateSuccess(alertInfo[0]);
                System.out.println("课程[" + questionnaire.getContent() + "]  评教结果：" + evaluateResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webClient.close();
            System.out.println("评教结束");
        }

    }

    /**
    * @Author: ningxy
    * @Description: 解析HTML获取待评价课程的信息
    * @params: [htmlPage]
    * @return: java.util.List<cn.ningxy.Questionnaire>
    * @Date: 2018/6/19 下午2:23
    */
    private List<Questionnaire> parseHtml(HtmlPage htmlPage) {
        List<Questionnaire> list = new ArrayList<>();
        Document document = Jsoup.parse(htmlPage.asXml());
        Elements courseElements = document.select("tr.odd");
        if (courseElements != null) {
            for (Element courseElement : courseElements) {
                Elements detailsElements = courseElement.select("td>img");
                String[] infos = detailsElements.get(0).attr("name").split("#@");
                Questionnaire questionnaire = new Questionnaire();
                questionnaire.setWjbm(infos[0]);
                questionnaire.setBpr(infos[1]);
                questionnaire.setCharacter(infos[2]);
                questionnaire.setName(infos[3]);
                questionnaire.setContent(infos[4]);
                questionnaire.setPgnr(infos[5]);
                questionnaire.setEvaluated(courseElement.select("td").get(3).text());
                System.out.println(questionnaire.toString());
                list.add(questionnaire);
            }
        }
        return list;
    }

    /**
    * @Author: ningxy
    * @Description: 填充表单
    * @params: [htmlPage]
    * @return: void
    * @Date: 2018/6/19 下午2:23
    */
    private void fillForm(HtmlPage htmlPage) {
        HtmlForm form = htmlPage.getFormByName("StDaForm");

        // 循环找radio的name
        for(int i = 100; i < 150; i++){
            List<HtmlRadioButtonInput> problemList = form.getRadioButtonsByName("0000000" + i);
            if (problemList.size() > 0) {
                HtmlRadioButtonInput problem = problemList.get(0);
                problem.setChecked(true);
            }
        }
        HtmlTextArea textArea = form.getTextAreaByName("zgpj");
        textArea.setText("讲课生动，表达优秀，很喜欢这个老师，好评!!!");
    }

    /**
    * @Author: ningxy
    * @Description: 提交评教内容的表单
    * @params: [htmlPage]
    * @return: com.gargoylesoftware.htmlunit.html.HtmlPage
    * @Date: 2018/6/19 下午2:26
    */
    private HtmlPage submitForm(HtmlPage htmlPage) throws Exception {
        DomElement submitButton = (DomElement) htmlPage.getByXPath("//img[@onclick='check()']").get(0);
        return submitButton.click();
    }

    /**
    * @Author: ningxy
    * @Description: 查看是否评教成功
    * @params: [alertText]
    * @return: boolean
    * @Date: 2018/6/19 下午2:27
    */
    private boolean isEvaluateSuccess(String alertText) {
        return "评估成功！".equals(alertText);
    }
}

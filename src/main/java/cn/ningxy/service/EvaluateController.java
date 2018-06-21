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
import java.util.List;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-21 08:59
 **/
public class EvaluateController {

    private Set<Cookie> cookies;
    private static final String URL_COURSE_LIST = "http://jwpt.tjpu.edu.cn/jxpgXsAction.do?oper=listWj";

    public EvaluateController(Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    public void evaluate() {
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

        for (Cookie cooky : cookies) {
            webClient.getCookieManager().addCookie(cooky);
        }

        int numTot = 0;
        int numOK = 0;

        try {
            WebRequest request = new WebRequest(new URL(URL_COURSE_LIST));
            request.setHttpMethod(HttpMethod.GET);
            HtmlPage evaluateListPage = webClient.getPage(request);
            List<Questionnaire> list = parseHtml(evaluateListPage);
            System.out.println("检测到共有[" + list.size() + "]门课程");

            for (Questionnaire questionnaire : list) {
                if (questionnaire.isEvaluated()) continue;   // 如果已经被评教则跳过
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

                numTot += 1;
                numOK += (evaluateResult ? 1 : 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webClient.close();
            System.out.println("评教结束");
            System.out.printf("总共评教[%d]门课程\n", numTot);
            System.out.printf("评教成功[%d]门课程\n", numOK);
        }

    }

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

    private void fillForm(HtmlPage htmlPage) {
        HtmlForm form = htmlPage.getFormByName("StDaForm");

        // 循环找radio的name
        for (int i = 100; i < 150; i++) {
            List<HtmlRadioButtonInput> problemList = form.getRadioButtonsByName("0000000" + i);
            if (problemList.size() > 0) {
                HtmlRadioButtonInput problem = problemList.get(0);
                problem.setChecked(true);
            }
        }
        HtmlTextArea textArea = form.getTextAreaByName("zgpj");
        textArea.setText("讲课生动，表达优秀，很喜欢这个老师，好评!!!");
    }

    private HtmlPage submitForm(HtmlPage htmlPage) throws Exception {
        DomElement submitButton = (DomElement) htmlPage.getByXPath("//img[@onclick='check()']").get(0);
        return submitButton.click();
    }

    private boolean isEvaluateSuccess(String alertText) {
        return "评估成功！".equals(alertText);
    }

}
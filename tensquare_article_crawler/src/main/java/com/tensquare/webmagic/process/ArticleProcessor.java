package com.tensquare.webmagic.process;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class ArticleProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        String title = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div/div/div[1]/h1/text()").toString();
        String content = page.getHtml().xpath("//*[@id=\"content_views\"]").toString();

        if(title!=null&&content!=null){
            page.putField("title",title);
            page.putField("content",content);
        }else{
            page.setSkip(true); //跳过
        }


    }

    @Override
    public Site getSite() {
        return Site.me().setTimeOut(1000).setRetryTimes(3);
    }


}

package com.tensquare.user.process;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class UserProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        String nikename = page.getHtml().xpath("//*[@id=\"uid\"]/text()").toString();
        String imgUrl = page.getHtml().xpath("//*[@id=\"asideProfile\"]/div[1]/div[1]/a/img/@src").toString();

        if(nikename!=null&&imgUrl!=null){
            page.putField("nikename",nikename);
            page.putField("imgUrl",imgUrl);
        }else{
            page.setSkip(true); //跳过
        }


    }

    @Override
    public Site getSite() {
        return Site.me().setTimeOut(1000).setRetryTimes(3);
    }


}

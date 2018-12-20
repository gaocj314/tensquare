package com.tensquare.user.pipeline;

import com.tensquare.user.dao.UserDao;
import com.tensquare.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import util.DownloadUtil;
import util.IdWorker;

import java.io.IOException;

@Component
public class UserDbPipeline implements Pipeline {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserDao userDao;

    @Value("${savePath}")
    private String savePath;

    @Override
    public void process(ResultItems resultItems, Task task) {
        String nikename = resultItems.get("nikename");
        String imgUrl = resultItems.get("imgUrl");

        String avatar = imgUrl.substring(imgUrl.lastIndexOf("/")+1);

        //保存图片
        try {
            DownloadUtil.download(imgUrl,avatar,"d:/userImg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = new User();
        user.setId(idWorker.nextId()+"");
        user.setNickname(nikename);
        user.setAvatar(avatar);

        userDao.save(user);
    }
}

package com.p2p.controller;

import com.p2p.bean.User;
import com.p2p.common.ServerResponse;
import com.p2p.service.UserService;
import com.p2p.utils.EncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by 娃娃鱼 on 2017/12/22.
 */
@RestController
@RequestMapping("/data/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("regPhone")
    @ResponseBody
    public ServerResponse regPhone(String phone) {
        System.out.println(phone);
        return userService.getByPhone(phone);
    }

    @RequestMapping("reg")
    @ResponseBody
    public ServerResponse reg(User user) {
        user.setUpwd(EncryptUtils.md5(user.getUpwd()));
        return userService.save(user);
    }

    @RequestMapping("login")
    @ResponseBody
    public ServerResponse login(User user, HttpSession session) {
        ServerResponse serverResponse = null;
        User user1 = userService.getByPhonePwd(user.getPhone(), EncryptUtils.md5(user.getUpwd()));
        if(user1 != null) {
            session.setAttribute("user", user1);
            serverResponse = ServerResponse.createBySuccess();
        } else {
            serverResponse = ServerResponse.createByError();
        }
        return serverResponse;
    }

}
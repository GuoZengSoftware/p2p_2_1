package com.p2p.controller.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by qingfeng on 2018/1/5.
 */

@Controller
@RequestMapping("/page/Role")
public class RoleView {

    @RequestMapping("List")
    public String RoleList() {
        return "Admin/roleList";
    }

}